/*
 * Tokenizer.cs
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
 * Copyright (c) 2003-2005 Per Cederberg. All rights reserved.
 */

using System.Collections;
using System.IO;
using System.Text;
using PerCederberg.Grammatica.Runtime.RE;

namespace PerCederberg.Grammatica.Runtime {

    /**
     * A character stream tokenizer. This class groups the characters read
     * from the stream together into tokens ("words"). The grouping is
     * controlled by token patterns that contain either a fixed string to
     * search for, or a regular expression. If the stream of characters
     * don't match any of the token patterns, a parse exception is thrown.
     *
     * @author   Per Cederberg, <per at percederberg dot net>
     * @version  1.5
     */
    public class Tokenizer {

        /**
         * The ignore character case flag.
         */
        private bool ignoreCase = false;

        /**
         * The token list feature flag.
         */
        private bool useTokenList = false;

        /**
         * The string token matcher. This token matcher is used for all
         * string token patterns. This matcher implements a DFA to
         * provide maximum performance.
         */
        private StringTokenMatcher stringMatcher;

        /**
         * The list of all regular expression token matchers. These
         * matchers each test matches for a single regular expression.
         */
        private ArrayList regexpMatchers = new ArrayList();

        /**
         * The look-ahead character stream reader.
         */
        private LookAheadReader input = null;

        /**
         * The previous token in the token list.
         */
        private Token previousToken = null;

        /**
         * Creates a new case-sensitive tokenizer for the specified
         * input stream.
         *
         * @param input          the input stream to read
         */
        public Tokenizer(TextReader input)
            : this(input, false) {
        }

        /**
         * Creates a new tokenizer for the specified input stream. The
         * tokenizer can be set to process tokens either in
         * case-sensitive or case-insensitive mode.
         *
         * @param input          the input stream to read
         * @param ignoreCase     the character case ignore flag
         *
         * @since 1.5
         */
        public Tokenizer(TextReader input, bool ignoreCase) {
            this.stringMatcher = new StringTokenMatcher(ignoreCase);
            this.input = new LookAheadReader(input);
            this.ignoreCase = ignoreCase;
        }

        /**
         * The token list flag property. If the token list flag is
         * set, all tokens (including ignored tokens) link to each
         * other in a double-linked list. By default the token list
         * flag is set to false.
         *
         * @see Token#Previous
         * @see Token#Next
         *
         * @since 1.5
         */
        public bool UseTokenList {
            get {
                return useTokenList;
            }
            set {
                useTokenList = value;
            }
        }

        /**
         * Checks if the token list feature is used. The token list
         * feature makes all tokens (including ignored tokens) link to
         * each other in a linked list. By default the token list feature
         * is not used.
         *
         * @return true if the token list feature is used, or
         *         false otherwise
         *
         * @see #UseTokenList
         * @see #SetUseTokenList
         * @see Token#GetPreviousToken
         * @see Token#GetNextToken
         *
         * @since 1.4
         *
         * @deprecated Use the UseTokenList property instead.
         */
        public bool GetUseTokenList() {
            return useTokenList;
        }

        /**
         * Sets the token list feature flag. The token list feature makes
         * all tokens (including ignored tokens) link to each other in a
         * linked list when active. By default the token list feature is
         * not used.
         *
         * @param useTokenList   the token list feature flag
         *
         * @see #UseTokenList
         * @see #GetUseTokenList
         * @see Token#GetPreviousToken
         * @see Token#GetNextToken
         *
         * @since 1.4
         *
         * @deprecated Use the UseTokenList property instead.
         */
        public void SetUseTokenList(bool useTokenList) {
            this.useTokenList = useTokenList;
        }

        /**
         * Returns a description of the token pattern with the
         * specified id.
         *
         * @param id             the token pattern id
         *
         * @return the token pattern description, or
         *         null if not present
         */
        public string GetPatternDescription(int id) {
            TokenPattern        pattern;
            RegExpTokenMatcher  re;

            pattern = stringMatcher.GetPattern(id);
            if (pattern != null) {
                return pattern.ToShortString();
            }
            for (int i = 0; i < regexpMatchers.Count; i++) {
                re = (RegExpTokenMatcher) regexpMatchers[i];
                if (re.GetPattern().Id == id) {
                    return re.GetPattern().ToShortString();
                }
            }
            return null;
        }

        /**
         * Returns the current line number. This number will be the line
         * number of the next token returned.
         *
         * @return the current line number
         */
        public int GetCurrentLine() {
            return input.LineNumber;
        }

        /**
         * Returns the current column number. This number will be the
         * column number of the next token returned.
         *
         * @return the current column number
         */
        public int GetCurrentColumn() {
            return input.ColumnNumber;
        }

        /**
         * Adds a new token pattern to the tokenizer. The pattern will be
         * added last in the list, choosing a previous token pattern in
         * case two matches the same string.
         *
         * @param pattern        the pattern to add
         *
         * @throws ParserCreationException if the pattern couldn't be
         *             added to the tokenizer
         */
        public void AddPattern(TokenPattern pattern) {
            switch (pattern.Type) {
            case TokenPattern.PatternType.STRING:
                stringMatcher.AddPattern(pattern);
                break;
            case TokenPattern.PatternType.REGEXP:
                try {
                    regexpMatchers.Add(new RegExpTokenMatcher(pattern,
                                                              ignoreCase,
                                                              input));
                } catch (RegExpException e) {
                    throw new ParserCreationException(
                        ParserCreationException.ErrorType.INVALID_TOKEN,
                        pattern.Name,
                        "regular expression contains error(s): " +
                        e.Message);
                }
                break;
            default:
                throw new ParserCreationException(
                    ParserCreationException.ErrorType.INVALID_TOKEN,
                    pattern.Name,
                    "pattern type " + pattern.Type +
                    " is undefined");
            }
        }

        /**
         * Resets this tokenizer for usage with another input stream.
         * This method will clear all the internal state in the
         * tokenizer as well as close the previous input stream. It is
         * normally called in order to reuse a parser and tokenizer
         * pair for parsing another input stream.
         *
         * @param input          the new input stream to read
         *
         * @since 1.5
         */
        public void Reset(TextReader input) {
            this.input.Close();
            this.input = new LookAheadReader(input);
            this.previousToken = null;
            stringMatcher.Reset();
            for (int i = 0; i < regexpMatchers.Count; i++) {
                ((RegExpTokenMatcher) regexpMatchers[i]).Reset(this.input);
            }
        }

        /**
         * Finds the next token on the stream. This method will return
         * null when end of file has been reached. It will return a
         * parse exception if no token matched the input stream, or if
         * a token pattern with the error flag set matched. Any tokens
         * matching a token pattern with the ignore flag set will be
         * silently ignored and the next token will be returned.
         *
         * @return the next token found, or
         *         null if end of file was encountered
         *
         * @throws ParseException if the input stream couldn't be read or
         *             parsed correctly
         */
        public Token Next() {
            Token  token = null;

            do {
                token = NextToken();
                if (useTokenList && token != null) {
                    token.Previous = previousToken;
                    previousToken = token;
                }
                if (token == null) {
                    return null;
                } else if (token.Pattern.Error) {
                    throw new ParseException(
                        ParseException.ErrorType.INVALID_TOKEN,
                        token.Pattern.ErrorMessage,
                        token.StartLine,
                        token.StartColumn);
                } else if (token.Pattern.Ignore) {
                    token = null;
                }
            } while (token == null);

            return token;
        }

        /**
         * Finds the next token on the stream. This method will return
         * null when end of file has been reached. It will return a
         * parse exception if no token matched the input stream.
         *
         * @return the next token found, or
         *         null if end of file was encountered
         *
         * @throws ParseException if the input stream couldn't be read or
         *             parsed correctly
         */
        private Token NextToken() {
            TokenMatcher  m;
            string        str;
            int           line;
            int           column;

            try {
                m = FindMatch();
                if (m != null) {
                    line = input.LineNumber;
                    column = input.ColumnNumber;
                    str = input.ReadString(m.GetMatchedLength());
                    return new Token(m.GetMatchedPattern(), str, line, column);
                } else if (input.Peek() < 0) {
                    return null;
                } else {
                    line = input.LineNumber;
                    column = input.ColumnNumber;
                    throw new ParseException(
                        ParseException.ErrorType.UNEXPECTED_CHAR,
                        input.ReadString(1),
                        line,
                        column);
                }
            } catch (IOException e) {
                throw new ParseException(ParseException.ErrorType.IO,
                                         e.Message,
                                         -1,
                                         -1);
            }
        }

        /**
         * Finds the longest token match from the current buffer
         * position. This method will return the token matcher for the
         * best match, or null if no match was found. As a side
         * effect, this method will also set the end of buffer flag.
         *
         * @return the token mathcher with the longest match, or
         *         null if no match was found
         *
         * @throws IOException if an I/O error occurred
         */
        private TokenMatcher FindMatch() {
            TokenMatcher        bestMatch = null;
            int                 bestLength = 0;
            RegExpTokenMatcher  re;

            // Check string matches
            if (stringMatcher.Match(input)) {
                bestMatch = stringMatcher;
                bestLength = bestMatch.GetMatchedLength();
            }

            // Check regular expression matches
            for (int i = 0; i < regexpMatchers.Count; i++) {
                re = (RegExpTokenMatcher) regexpMatchers[i];
                if (re.Match() && re.GetMatchedLength() > bestLength) {
                    bestMatch = re;
                    bestLength = re.GetMatchedLength();
                }
            }
            return bestMatch;
        }

        /**
         * Returns a string representation of this object. The returned
         * string will contain the details of all the token patterns
         * contained in this tokenizer.
         *
         * @return a detailed string representation
         */
        public override string ToString() {
            StringBuilder  buffer = new StringBuilder();

            buffer.Append(stringMatcher);
            for (int i = 0; i < regexpMatchers.Count; i++) {
                buffer.Append(regexpMatchers[i]);
            }
            return buffer.ToString();
        }
    }


    /**
     * A token pattern matcher. This class is the base class for the
     * two types of token matchers that exist. The token matcher
     * checks for matches with the tokenizer buffer, and maintains the
     * state of the last match.
     */
    internal abstract class TokenMatcher {

        /**
         * Returns the latest matched token pattern.
         *
         * @return the latest matched token pattern, or
         *         null if no match found
         */
        public abstract TokenPattern GetMatchedPattern();

        /**
         * Returns the length of the latest match.
         *
         * @return the length of the latest match, or
         *         zero (0) if no match found
         */
        public abstract int GetMatchedLength();
    }


    /**
     * A regular expression token pattern matcher. This class is used
     * to match a single regular expression with an input stream. This
     * class also maintains the state of the last match.
     */
    internal class RegExpTokenMatcher : TokenMatcher {

        /**
         * The token pattern to match with.
         */
        private TokenPattern pattern;

        /**
         * The regular expression to use.
         */
        private RegExp regExp;

        /**
         * The regular expression matcher to use.
         */
        private Matcher matcher = null;

        /**
         * Creates a new regular expression token matcher.
         *
         * @param pattern        the pattern to match
         * @param ignoreCase     the character case ignore flag
         * @param input          the input stream to check
         *
         * @throws RegExpException if the regular expression couldn't
         *             be created properly
         */
        public RegExpTokenMatcher(TokenPattern pattern,
                                  bool ignoreCase,
                                  LookAheadReader input) {

            this.pattern = pattern;
            this.regExp = new RegExp(pattern.Pattern, ignoreCase);
            this.matcher = regExp.Matcher(input);
        }

        /**
         * Resets the matcher for another character input stream. This
         * will clear the results of the last match.
         *
         * @param input           the new input stream to check
         */
        public void Reset(LookAheadReader input) {
            matcher.Reset(input);
        }

        /**
         * Returns the token pattern.
         *
         * @return the token pattern
         */
        public TokenPattern GetPattern() {
            return pattern;
        }

        /**
         * Returns the latest matched token pattern.
         *
         * @return the latest matched token pattern, or
         *         null if no match found
         */
        public override TokenPattern GetMatchedPattern() {
            if (matcher == null || matcher.Length() <= 0) {
                return null;
            } else {
                return pattern;
            }
        }

        /**
         * Returns the length of the latest match.
         *
         * @return the length of the latest match, or
         *         zero (0) if no match found
         */
        public override int GetMatchedLength() {
            return (matcher == null) ? 0 : matcher.Length();
        }

        /**
         * Checks if the token pattern matches the input stream. This
         * method will also reset all flags in this matcher.
         *
         * @param str            the string to match
         * @param pos            the starting position
         *
         * @return true if a match was found, or
         *         false otherwise
         *
         * @throws IOException if an I/O error occurred
         */
        public bool Match() {
            return matcher.MatchFromBeginning();
        }

        /**
         * Returns a string representation of this token matcher.
         *
         * @return a detailed string representation of this matcher
         */
        public override string ToString() {
            return pattern.ToString() + "\n" +
                   regExp.ToString() + "\n";
        }
    }


    /**
     * A string token pattern matcher. This class is used to match a
     * set of strings with an input stream. This class internally uses
     * a DFA for maximum performance. It also maintains the state of
     * the last match.
     */
    internal class StringTokenMatcher : TokenMatcher {

        /**
         * The list of string token patterns.
         */
        private ArrayList patterns = new ArrayList();

        /**
         * The finite automaton to use for matching.
         */
        private Automaton start = new Automaton();

        /**
         * The last token pattern match found.
         */
        private TokenPattern match = null;

        /**
         * The ignore character case flag.
         */
        private bool ignoreCase = false;

        /**
         * Creates a new string token matcher.
         *
         * @param ignoreCase      the character case ignore flag
         */
        public StringTokenMatcher(bool ignoreCase) {
            this.ignoreCase = ignoreCase;
        }

        /**
         * Resets the matcher state. This will clear the results of
         * the last match.
         */
        public void Reset() {
            match = null;
        }

        /**
         * Returns the latest matched token pattern.
         *
         * @return the latest matched token pattern, or
         *         null if no match found
         */
        public override TokenPattern GetMatchedPattern() {
            return match;
        }

        /**
         * Returns the length of the latest match.
         *
         * @return the length of the latest match, or
         *         zero (0) if no match found
         */
        public override int GetMatchedLength() {
            if (match == null) {
                return 0;
            } else {
                return match.Pattern.Length;
            }
        }

        /**
         * Returns the token pattern with the specified id. Only
         * token patterns handled by this matcher can be returned.
         *
         * @param id         the token pattern id
         *
         * @return the token pattern found, or
         *         null if not found
         */
        public TokenPattern GetPattern(int id) {
            TokenPattern  pattern;

            for (int i = 0; i < patterns.Count; i++) {
                pattern = (TokenPattern) patterns[i];
                if (pattern.Id == id) {
                    return pattern;
                }
            }
            return null;
        }

        /**
         * Adds a string token pattern to this matcher.
         *
         * @param pattern        the pattern to add
         */
        public void AddPattern(TokenPattern pattern) {
            patterns.Add(pattern);
            start.AddMatch(pattern.Pattern, ignoreCase, pattern);
        }

        /**
         * Checks if the token pattern matches the input stream. This
         * method will also reset all flags in this matcher.
         *
         * @param input          the input stream to match
         *
         * @return true if a match was found, or
         *         false otherwise
         *
         * @throws IOException if an I/O error occurred
         */
        public bool Match(LookAheadReader input) {
            Reset();
            match = (TokenPattern) start.MatchFrom(input, 0, ignoreCase);
            return match != null;
        }

        /**
         * Returns a string representation of this matcher. This will
         * contain all the token patterns.
         *
         * @return a detailed string representation of this matcher
         */
        public override string ToString() {
            StringBuilder  buffer = new StringBuilder();

            for (int i = 0; i < patterns.Count; i++) {
                buffer.Append(patterns[i]);
                buffer.Append("\n\n");
            }
            return buffer.ToString();
        }
    }
}
