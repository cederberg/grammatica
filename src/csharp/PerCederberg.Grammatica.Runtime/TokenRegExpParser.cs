/*
 * TokenRegExpParser.cs
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 * Copyright (c) 2003-2009 Per Cederberg. All rights reserved.
 */

using System;
using System.Collections;
using System.Globalization;
using System.Text;
using PerCederberg.Grammatica.Runtime.RE;

namespace PerCederberg.Grammatica.Runtime {

    /**
     * A regular expression parser. The parser creates an NFA for the
     * regular expression having a single start and acceptance states.
     *
     * @author   Per Cederberg, <per at percederberg dot net>
     * @version  1.5
     * @since    1.5
     */
    internal class TokenRegExpParser {

        /**
        * The regular expression pattern.
        */
        private string pattern;

        /**
        * The character case ignore flag.
        */
        private bool ignoreCase;

        /**
        * The current position in the pattern. This variable is used by
        * the parsing methods.
        */
        private int pos;

        /**
        * The start NFA state for this regular expression.
        */
        internal NFAState start = new NFAState();

        /**
        * The end NFA state for this regular expression.
        */
        internal NFAState end = null;

        /**
        * The number of states found.
        */
        private int stateCount = 0;

        /**
        * The number of transitions found.
        */
        private int transitionCount = 0;

        /**
        * The number of epsilon transitions found.
        */
        private int epsilonCount = 0;

        /**
         * Creates a new case-sensitive regular expression parser. Note
         * that this will trigger the parsing of the regular expression.
         *
         * @param pattern        the regular expression pattern
         *
         * @throws RegExpException if the regular expression couldn't be
         *             parsed correctly
         */
        public TokenRegExpParser(string pattern) : this(pattern, false) {
        }

        /**
         * Creates a new regular expression parser. The regular
         * expression can be either case-sensitive or case-insensitive.
         * Note that this will trigger the parsing of the regular
         * expression.
         *
         * @param pattern        the regular expression pattern
         * @param ignoreCase     the character case ignore flag
         *
         * @throws RegExpException if the regular expression couldn't be
         *             parsed correctly
         */
        public TokenRegExpParser(string pattern, bool ignoreCase) {
            this.pattern = pattern;
            this.ignoreCase = ignoreCase;
            this.pos = 0;
            this.end = ParseExpr(start);
            if (pos < pattern.Length) {
                throw new RegExpException(
                    RegExpException.ErrorType.UNEXPECTED_CHARACTER,
                    pos,
                    pattern);
            }
        }

        /**
         * Returns the debug information for the generated NFA.
         *
         * @return the debug information for the generated NFA
         */
        public string GetDebugInfo() {
            if (stateCount == 0) {
                UpdateStats(start, new Hashtable());
            }
            return stateCount + " states, " +
                   transitionCount + " transitions, " +
                   epsilonCount + " epsilons";
        }

        /**
         * Updates the statistical counters for the NFA generated.
         *
         * @param state          the current state to visit
         * @param visited        the lookup map of visited states
         */
        private void UpdateStats(NFAState state, Hashtable visited) {
            if (!visited.ContainsKey(state)) {
                visited.Add(state, state);
                stateCount++;
                for (int i = 0; i < state.outgoing.Length; i++) {
                    transitionCount++;
                    if (state.outgoing[i] is NFAEpsilonTransition) {
                        epsilonCount++;
                    }
                    UpdateStats(state.outgoing[i].state, visited);
                }
            }
        }

        /**
         * Parses a regular expression. This method handles the Expr
         * production in the grammar (see regexp.grammar).
         *
         * @param start          the initial NFA state
         *
         * @return the terminating NFA state
         *
         * @throws RegExpException if an error was encountered in the
         *             pattern string
         */
        private NFAState ParseExpr(NFAState start) {
            NFAState  end = new NFAState();
            NFAState  subStart;
            NFAState  subEnd;

            do {
                if (PeekChar(0) == '|') {
                    ReadChar('|');
                }
                subStart = new NFAState();
                subEnd = ParseTerm(subStart);
                if (subStart.incoming.Length == 0) {
                    subStart.MergeInto(start);
                } else {
                    start.AddOut(new NFAEpsilonTransition(subStart));
                }
                if (subEnd.outgoing.Length == 0 ||
                    (!end.HasTransitions() && PeekChar(0) != '|')) {
                    subEnd.MergeInto(end);
                } else {
                    subEnd.AddOut(new NFAEpsilonTransition(end));
                }
            } while (PeekChar(0) == '|');
            return end;
        }

        /**
         * Parses a regular expression term. This method handles the
         * Term production in the grammar (see regexp.grammar).
         *
         * @param start          the initial NFA state
         *
         * @return the terminating NFA state
         *
         * @throws RegExpException if an error was encountered in the
         *             pattern string
         */
        private NFAState ParseTerm(NFAState start) {
            NFAState  end;

            end = ParseFact(start);
            while (true) {
                switch (PeekChar(0)) {
                case -1:
                case ')':
                case ']':
                case '{':
                case '}':
                case '?':
                case '+':
                case '|':
                    return end;
                default:
                    end = ParseFact(end);
                    break;
                }
            }
        }

        /**
         * Parses a regular expression factor. This method handles the
         * Fact production in the grammar (see regexp.grammar).
         *
         * @param start          the initial NFA state
         *
         * @return the terminating NFA state
         *
         * @throws RegExpException if an error was encountered in the
         *             pattern string
         */
        private NFAState ParseFact(NFAState start) {
            NFAState  placeholder = new NFAState();
            NFAState  end;

            end = ParseAtom(placeholder);
            switch (PeekChar(0)) {
            case '?':
            case '*':
            case '+':
            case '{':
                end = ParseAtomModifier(placeholder, end);
                break;
            }
            if (placeholder.incoming.Length > 0 && start.outgoing.Length > 0) {
                start.AddOut(new NFAEpsilonTransition(placeholder));
                return end;
            } else {
                placeholder.MergeInto(start);
                return (end == placeholder) ? start : end;
            }
        }

        /**
         * Parses a regular expression atom. This method handles the
         * Atom production in the grammar (see regexp.grammar).
         *
         * @param start          the initial NFA state
         *
         * @return the terminating NFA state
         *
         * @throws RegExpException if an error was encountered in the
         *             pattern string
         */
        private NFAState ParseAtom(NFAState start) {
            NFAState  end;

            switch (PeekChar(0)) {
            case '.':
                ReadChar('.');
                return start.AddOut(new NFADotTransition(new NFAState()));
            case '(':
                ReadChar('(');
                end = ParseExpr(start);
                ReadChar(')');
                return end;
            case '[':
                ReadChar('[');
                end = ParseCharSet(start);
                ReadChar(']');
                return end;
            case -1:
            case ')':
            case ']':
            case '{':
            case '}':
            case '?':
            case '*':
            case '+':
            case '|':
                throw new RegExpException(
                    RegExpException.ErrorType.UNEXPECTED_CHARACTER,
                    pos,
                    pattern);
            default:
                return ParseChar(start);
            }
        }

        /**
         * Parses a regular expression atom modifier. This method handles
         * the AtomModifier production in the grammar (see regexp.grammar).
         *
         * @param start          the initial NFA state
         * @param end            the terminal NFA state
         *
         * @return the terminating NFA state
         *
         * @throws RegExpException if an error was encountered in the
         *             pattern string
         */
        private NFAState ParseAtomModifier(NFAState start, NFAState end) {
            int  min = 0;
            int  max = -1;
            int  firstPos = pos;

            // Read min and max
            switch (ReadChar()) {
            case '?':
                min = 0;
                max = 1;
                break;
            case '*':
                min = 0;
                max = -1;
                break;
            case '+':
                min = 1;
                max = -1;
                break;
            case '{':
                min = ReadNumber();
                max = min;
                if (PeekChar(0) == ',') {
                    ReadChar(',');
                    max = -1;
                    if (PeekChar(0) != '}') {
                        max = ReadNumber();
                    }
                }
                ReadChar('}');
                if (max == 0 || (max > 0 && min > max)) {
                    throw new RegExpException(
                        RegExpException.ErrorType.INVALID_REPEAT_COUNT,
                        firstPos,
                        pattern);
                }
                break;
            default:
                throw new RegExpException(
                    RegExpException.ErrorType.UNEXPECTED_CHARACTER,
                    pos - 1,
                    pattern);
            }

            // Read possessive or reluctant modifiers
            if (PeekChar(0) == '?') {
                throw new RegExpException(
                    RegExpException.ErrorType.UNSUPPORTED_SPECIAL_CHARACTER,
                    pos,
                    pattern);
            } else if (PeekChar(0) == '+') {
                throw new RegExpException(
                    RegExpException.ErrorType.UNSUPPORTED_SPECIAL_CHARACTER,
                    pos,
                    pattern);
            }

            // Handle supported repeaters
            if (min == 0 && max == 1) {
                return start.AddOut(new NFAEpsilonTransition(end));
            } else if (min == 0 && max == -1) {
                if (end.outgoing.Length == 0) {
                    end.MergeInto(start);
                } else {
                    end.AddOut(new NFAEpsilonTransition(start));
                }
                return start;
            } else if (min == 1 && max == -1) {
                if (start.outgoing.Length == 1 &&
                    end.outgoing.Length == 0 &&
                    end.incoming.Length == 1 &&
                    start.outgoing[0] == end.incoming[0]) {

                    end.AddOut(start.outgoing[0].Copy(end));
                } else {
                    end.AddOut(new NFAEpsilonTransition(start));
                }
                return end;
            } else {
                throw new RegExpException(
                    RegExpException.ErrorType.INVALID_REPEAT_COUNT,
                    firstPos,
                    pattern);
            }
        }

        /**
         * Parses a regular expression character set. This method handles
         * the contents of the '[...]' construct in a regular expression.
         *
         * @param start          the initial NFA state
         *
         * @return the terminating NFA state
         *
         * @throws RegExpException if an error was encountered in the
         *             pattern string
         */
        private NFAState ParseCharSet(NFAState start) {
            NFAState                end = new NFAState();
            NFACharRangeTransition  range;
            char                    min;
            char                    max;

            if (PeekChar(0) == '^') {
                ReadChar('^');
                range = new NFACharRangeTransition(true, ignoreCase, end);
            } else {
                range = new NFACharRangeTransition(false, ignoreCase, end);
            }
            start.AddOut(range);
            while (PeekChar(0) > 0) {
                min = (char) PeekChar(0);
                switch (min) {
                case ']':
                    return end;
                case '\\':
                    range.AddCharacter(ReadEscapeChar());
                    break;
                default:
                    ReadChar(min);
                    if (PeekChar(0) == '-' &&
                        PeekChar(1) > 0 &&
                        PeekChar(1) != ']') {

                        ReadChar('-');
                        max = ReadChar();
                        range.AddRange(min, max);
                    } else {
                        range.AddCharacter(min);
                    }
                    break;
                }
            }
            return end;
        }

        /**
         * Parses a regular expression character. This method handles
         * a single normal character in a regular expression.
         *
         * @param start          the initial NFA state
         *
         * @return the terminating NFA state
         *
         * @throws RegExpException if an error was encountered in the
         *             pattern string
         */
        private NFAState ParseChar(NFAState start) {
            switch (PeekChar(0)) {
            case '\\':
                return ParseEscapeChar(start);
            case '^':
            case '$':
                throw new RegExpException(
                    RegExpException.ErrorType.UNSUPPORTED_SPECIAL_CHARACTER,
                    pos,
                    pattern);
            default:
                return start.AddOut(ReadChar(), ignoreCase, new NFAState());
            }
        }

        /**
         * Parses a regular expression character escape. This method
         * handles a single character escape in a regular expression.
         *
         * @param start          the initial NFA state
         *
         * @return the terminating NFA state
         *
         * @throws RegExpException if an error was encountered in the
         *             pattern string
         */
        private NFAState ParseEscapeChar(NFAState start) {
            NFAState  end = new NFAState();

            if (PeekChar(0) == '\\' && PeekChar(1) > 0) {
                switch ((char) PeekChar(1)) {
                case 'd':
                    ReadChar();
                    ReadChar();
                    return start.AddOut(new NFADigitTransition(end));
                case 'D':
                    ReadChar();
                    ReadChar();
                    return start.AddOut(new NFANonDigitTransition(end));
                case 's':
                    ReadChar();
                    ReadChar();
                    return start.AddOut(new NFAWhitespaceTransition(end));
                case 'S':
                    ReadChar();
                    ReadChar();
                    return start.AddOut(new NFANonWhitespaceTransition(end));
                case 'w':
                    ReadChar();
                    ReadChar();
                    return start.AddOut(new NFAWordTransition(end));
                case 'W':
                    ReadChar();
                    ReadChar();
                    return start.AddOut(new NFANonWordTransition(end));
                }
            }
            return start.AddOut(ReadEscapeChar(), ignoreCase, end);
        }

        /**
         * Reads a regular expression character escape. This method
         * handles a single character escape in a regular expression.
         *
         * @return the character read
         *
         * @throws RegExpException if an error was encountered in the
         *             pattern string
         */
        private char ReadEscapeChar() {
            char    c;
            string  str;
            int     value;

            ReadChar('\\');
            c = ReadChar();
            switch (c) {
            case '0':
                c = ReadChar();
                if (c < '0' || c > '3') {
                    throw new RegExpException(
                        RegExpException.ErrorType.UNSUPPORTED_ESCAPE_CHARACTER,
                        pos - 3,
                        pattern);
                }
                value = c - '0';
                c = (char) PeekChar(0);
                if ('0' <= c && c <= '7') {
                    value *= 8;
                    value += ReadChar() - '0';
                    c = (char) PeekChar(0);
                    if ('0' <= c && c <= '7') {
                        value *= 8;
                        value += ReadChar() - '0';
                    }
                }
                return (char) value;
            case 'x':
                str = ReadChar().ToString() + ReadChar().ToString();
                try {
                    value = Int32.Parse(str, NumberStyles.AllowHexSpecifier);
                    return (char) value;
                } catch (FormatException) {
                    throw new RegExpException(
                        RegExpException.ErrorType.UNSUPPORTED_ESCAPE_CHARACTER,
                        pos - str.Length - 2,
                        pattern);
                }
            case 'u':
                str = ReadChar().ToString() +
                      ReadChar().ToString() +
                      ReadChar().ToString() +
                      ReadChar().ToString();
                try {
                    value = Int32.Parse(str, NumberStyles.AllowHexSpecifier);
                    return (char) value;
                } catch (FormatException) {
                    throw new RegExpException(
                        RegExpException.ErrorType.UNSUPPORTED_ESCAPE_CHARACTER,
                        pos - str.Length - 2,
                        pattern);
                }
            case 't':
                return '\t';
            case 'n':
                return '\n';
            case 'r':
                return '\r';
            case 'f':
                return '\f';
            case 'a':
                return '\u0007';
            case 'e':
                return '\u001B';
            default:
                if (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z')) {
                    throw new RegExpException(
                        RegExpException.ErrorType.UNSUPPORTED_ESCAPE_CHARACTER,
                        pos - 2,
                        pattern);
                }
                return c;
            }
        }

        /**
         * Reads a number from the pattern. If the next character isn't a
         * numeric character, an exception is thrown. This method reads
         * several consecutive numeric characters.
         *
         * @return the numeric value read
         *
         * @throws RegExpException if an error was encountered in the
         *             pattern string
         */
        private int ReadNumber() {
            StringBuilder  buf = new StringBuilder();
            int            c;

            c = PeekChar(0);
            while ('0' <= c && c <= '9') {
                buf.Append(ReadChar());
                c = PeekChar(0);
            }
            if (buf.Length <= 0) {
                throw new RegExpException(
                    RegExpException.ErrorType.UNEXPECTED_CHARACTER,
                    pos,
                    pattern);
            }
            return Int32.Parse(buf.ToString());
        }

        /**
         * Reads the next character in the pattern. If no next character
         * exists, an exception is thrown.
         *
         * @return the character read
         *
         * @throws RegExpException if no next character was available in
         *             the pattern string
         */
        private char ReadChar() {
            int  c = PeekChar(0);

            if (c < 0) {
                throw new RegExpException(
                    RegExpException.ErrorType.UNTERMINATED_PATTERN,
                    pos,
                    pattern);
            } else {
                pos++;
                return (char) c;
            }
        }

        /**
         * Reads the next character in the pattern. If the character
         * wasn't the specified one, an exception is thrown.
         *
         * @param c              the character to read
         *
         * @return the character read
         *
         * @throws RegExpException if the character read didn't match the
         *             specified one, or if no next character was
         *             available in the pattern string
         */
        private char ReadChar(char c) {
            if (c != ReadChar()) {
                throw new RegExpException(
                    RegExpException.ErrorType.UNEXPECTED_CHARACTER,
                    pos - 1,
                    pattern);
            }
            return c;
        }

        /**
         * Returns a character that has not yet been read from the
         * pattern. If the requested position is beyond the end of the
         * pattern string, -1 is returned.
         *
         * @param count          the preview position, from zero (0)
         *
         * @return the character found, or
         *         -1 if beyond the end of the pattern string
         */
        private int PeekChar(int count) {
            if (pos + count < pattern.Length) {
                return pattern[pos + count];
            } else {
                return -1;
            }
        }
    }
}
