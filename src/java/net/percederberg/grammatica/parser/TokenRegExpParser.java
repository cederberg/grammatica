/*
 * TokenRegExpParser.java
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

package net.percederberg.grammatica.parser;

import java.util.HashMap;

import net.percederberg.grammatica.parser.re.RegExpException;

/**
 * A regular expression parser. The parser creates an NFA for the
 * regular expression having a single start and acceptance states.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 * @since    1.5
 */
class TokenRegExpParser {

    /**
     * The regular expression pattern.
     */
    private String pattern;

    /**
     * The character case ignore flag.
     */
    private boolean ignoreCase;

    /**
     * The current position in the pattern. This variable is used by
     * the parsing methods.
     */
    private int pos;

    /**
     * The start NFA state for this regular expression.
     */
    protected TokenNFA.State start = new TokenNFA.State();

    /**
     * The end NFA state for this regular expression.
     */
    protected TokenNFA.State end = null;

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
    public TokenRegExpParser(String pattern) throws RegExpException {
        this(pattern, false);
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
    public TokenRegExpParser(String pattern, boolean ignoreCase)
        throws RegExpException {

        this.pattern = pattern;
        this.ignoreCase = ignoreCase;
        this.pos = 0;
        this.end = parseExpr(start);
        if (pos < pattern.length()) {
            throw new RegExpException(
                RegExpException.UNEXPECTED_CHARACTER,
                pos,
                pattern);
        }
    }

    /**
     * Returns the debug information for the generated NFA.
     *
     * @return the debug information for the generated NFA
     */
    public String getDebugInfo() {
        if (stateCount == 0) {
            updateStats(start, new HashMap());
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
    private void updateStats(TokenNFA.State state, HashMap visited) {
        if (!visited.containsKey(state)) {
            visited.put(state, null);
            stateCount++;
            for (int i = 0; i < state.outgoing.length; i++) {
                transitionCount++;
                if (state.outgoing[i] instanceof TokenNFA.EpsilonTransition) {
                    epsilonCount++;
                }
                updateStats(state.outgoing[i].state, visited);
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
    private TokenNFA.State parseExpr(TokenNFA.State start) throws RegExpException {
        TokenNFA.State  end = new TokenNFA.State();
        TokenNFA.State  subStart;
        TokenNFA.State  subEnd;

        do {
            if (peekChar(0) == '|') {
                readChar('|');
            }
            subStart = new TokenNFA.State();
            subEnd = parseTerm(subStart);
            if (subStart.incoming.length == 0) {
                subStart.mergeInto(start);
            } else {
                start.addOut(new TokenNFA.EpsilonTransition(subStart));
            }
            if (subEnd.outgoing.length == 0 ||
                (!end.hasTransitions() && peekChar(0) != '|')) {
                subEnd.mergeInto(end);
            } else {
                subEnd.addOut(new TokenNFA.EpsilonTransition(end));
            }
        } while (peekChar(0) == '|');
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
    private TokenNFA.State parseTerm(TokenNFA.State start) throws RegExpException {
        TokenNFA.State  end;

        end = parseFact(start);
        while (true) {
            switch (peekChar(0)) {
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
                end = parseFact(end);
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
    private TokenNFA.State parseFact(TokenNFA.State start) throws RegExpException {
        TokenNFA.State  placeholder = new TokenNFA.State();
        TokenNFA.State  end;

        end = parseAtom(placeholder);
        switch (peekChar(0)) {
        case '?':
        case '*':
        case '+':
        case '{':
            end = parseAtomModifier(placeholder, end);
            break;
        }
        if (placeholder.incoming.length > 0 && start.outgoing.length > 0) {
            start.addOut(new TokenNFA.EpsilonTransition(placeholder));
            return end;
        } else {
            placeholder.mergeInto(start);
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
    private TokenNFA.State parseAtom(TokenNFA.State start)
        throws RegExpException {

        TokenNFA.State  end;

        switch (peekChar(0)) {
        case '.':
            readChar('.');
            return start.addOut(new TokenNFA.DotTransition(new TokenNFA.State()));
        case '(':
            readChar('(');
            end = parseExpr(start);
            readChar(')');
            return end;
        case '[':
            readChar('[');
            end = parseCharSet(start);
            readChar(']');
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
                RegExpException.UNEXPECTED_CHARACTER,
                pos,
                pattern);
        default:
            return parseChar(start);
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
    private TokenNFA.State parseAtomModifier(TokenNFA.State start,
                                             TokenNFA.State end)
        throws RegExpException {

        int  min = 0;
        int  max = -1;
        int  firstPos = pos;

        // Read min and max
        switch (readChar()) {
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
            min = readNumber();
            max = min;
            if (peekChar(0) == ',') {
                readChar(',');
                max = -1;
                if (peekChar(0) != '}') {
                    max = readNumber();
                }
            }
            readChar('}');
            if (max == 0 || (max > 0 && min > max)) {
                throw new RegExpException(
                    RegExpException.INVALID_REPEAT_COUNT,
                    firstPos,
                    pattern);
            }
            break;
        default:
            throw new RegExpException(
                RegExpException.UNEXPECTED_CHARACTER,
                pos - 1,
                pattern);
        }

        // Read possessive or reluctant modifiers
        if (peekChar(0) == '?') {
            throw new RegExpException(
                RegExpException.UNSUPPORTED_SPECIAL_CHARACTER,
                pos,
                pattern);
        } else if (peekChar(0) == '+') {
            throw new RegExpException(
                RegExpException.UNSUPPORTED_SPECIAL_CHARACTER,
                pos,
                pattern);
        }

        // Handle supported repeaters
        if (min == 0 && max == 1) {
            return start.addOut(new TokenNFA.EpsilonTransition(end));
        } else if (min == 0 && max == -1) {
            if (end.outgoing.length == 0) {
                end.mergeInto(start);
            } else {
                end.addOut(new TokenNFA.EpsilonTransition(start));
            }
            return start;
        } else if (min == 1 && max == -1) {
            if (start.outgoing.length == 1 &&
                end.outgoing.length == 0 &&
                end.incoming.length == 1 &&
                start.outgoing[0] == end.incoming[0]) {

                end.addOut(start.outgoing[0].copy(end));
            } else {
                end.addOut(new TokenNFA.EpsilonTransition(start));
            }
            return end;
        } else {
            throw new RegExpException(
                RegExpException.INVALID_REPEAT_COUNT,
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
    private TokenNFA.State parseCharSet(TokenNFA.State start)
        throws RegExpException {

        TokenNFA.State                end = new TokenNFA.State();
        TokenNFA.CharRangeTransition  range;
        char                          min;
        char                          max;

        if (peekChar(0) == '^') {
            readChar('^');
            range = new TokenNFA.CharRangeTransition(true, ignoreCase, end);
        } else {
            range = new TokenNFA.CharRangeTransition(false, ignoreCase, end);
        }
        start.addOut(range);
        while (peekChar(0) > 0) {
            min = (char) peekChar(0);
            switch (min) {
            case ']':
                return end;
            case '\\':
                range.addCharacter(readEscapeChar());
                break;
            default:
                readChar(min);
                if (peekChar(0) == '-'
                 && peekChar(1) > 0
                 && peekChar(1) != ']') {

                    readChar('-');
                    max = readChar();
                    range.addRange(min, max);
                } else {
                    range.addCharacter(min);
                }
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
    private TokenNFA.State parseChar(TokenNFA.State start)
        throws RegExpException {

        switch (peekChar(0)) {
        case '\\':
            return parseEscapeChar(start);
        case '^':
        case '$':
            throw new RegExpException(
                RegExpException.UNSUPPORTED_SPECIAL_CHARACTER,
                pos,
                pattern);
        default:
            return start.addOut(readChar(), ignoreCase, new TokenNFA.State());
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
    private TokenNFA.State parseEscapeChar(TokenNFA.State start)
        throws RegExpException {

        TokenNFA.State  end = new TokenNFA.State();

        if (peekChar(0) == '\\' && peekChar(1) > 0) {
            switch ((char) peekChar(1)) {
            case 'd':
                readChar();
                readChar();
                return start.addOut(new TokenNFA.DigitTransition(end));
            case 'D':
                readChar();
                readChar();
                return start.addOut(new TokenNFA.NonDigitTransition(end));
            case 's':
                readChar();
                readChar();
                return start.addOut(new TokenNFA.WhitespaceTransition(end));
            case 'S':
                readChar();
                readChar();
                return start.addOut(new TokenNFA.NonWhitespaceTransition(end));
            case 'w':
                readChar();
                readChar();
                return start.addOut(new TokenNFA.WordTransition(end));
            case 'W':
                readChar();
                readChar();
                return start.addOut(new TokenNFA.NonWordTransition(end));
            }
        }
        return start.addOut(readEscapeChar(), ignoreCase, end);
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
    private char readEscapeChar() throws RegExpException {
        char    c;
        String  str;

        readChar('\\');
        c = readChar();
        switch (c) {
        case '0':
            c = readChar();
            if (c < '0' || c > '3') {
                throw new RegExpException(
                    RegExpException.UNSUPPORTED_ESCAPE_CHARACTER,
                    pos - 3,
                    pattern);
            }
            str = String.valueOf(c);
            c = (char) peekChar(0);
            if ('0' <= c && c <= '7') {
                str += String.valueOf(readChar());
                c = (char) peekChar(0);
                if ('0' <= c && c <= '7') {
                    str += String.valueOf(readChar());
                }
            }
            try {
                return (char) Integer.parseInt(str, 8);
            } catch (NumberFormatException e) {
                throw new RegExpException(
                    RegExpException.UNSUPPORTED_ESCAPE_CHARACTER,
                    pos - str.length() - 2,
                    pattern);
            }
        case 'x':
            str = String.valueOf(readChar()) +
                  String.valueOf(readChar());
            try {
                return (char) Integer.parseInt(str, 16);
            } catch (NumberFormatException e) {
                throw new RegExpException(
                    RegExpException.UNSUPPORTED_ESCAPE_CHARACTER,
                    pos - str.length() - 2,
                    pattern);
            }
        case 'u':
            str = String.valueOf(readChar()) +
                  String.valueOf(readChar()) +
                  String.valueOf(readChar()) +
                  String.valueOf(readChar());
            try {
                return (char) Integer.parseInt(str, 16);
            } catch (NumberFormatException e) {
                throw new RegExpException(
                    RegExpException.UNSUPPORTED_ESCAPE_CHARACTER,
                    pos - str.length() - 2,
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
                    RegExpException.UNSUPPORTED_ESCAPE_CHARACTER,
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
    private int readNumber() throws RegExpException {
        StringBuffer  buf = new StringBuffer();
        int           c;

        c = peekChar(0);
        while ('0' <= c && c <= '9') {
            buf.append(readChar());
            c = peekChar(0);
        }
        if (buf.length() <= 0) {
            throw new RegExpException(
                RegExpException.UNEXPECTED_CHARACTER,
                pos,
                pattern);
        }
        return Integer.parseInt(buf.toString());
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
    private char readChar() throws RegExpException {
        int  c = peekChar(0);

        if (c < 0) {
            throw new RegExpException(
                RegExpException.UNTERMINATED_PATTERN,
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
    private char readChar(char c) throws RegExpException {
        if (c != readChar()) {
            throw new RegExpException(
                RegExpException.UNEXPECTED_CHARACTER,
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
    private int peekChar(int count) {
        if (pos + count < pattern.length()) {
            return pattern.charAt(pos + count);
        } else {
            return -1;
        }
    }
}
