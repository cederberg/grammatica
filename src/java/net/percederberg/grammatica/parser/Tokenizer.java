/*
 * Tokenizer.java
 *
 * This work is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * As a special exception, the copyright holders of this library give
 * you permission to link this library with independent modules to
 * produce an executable, regardless of the license terms of these
 * independent modules, and to copy and distribute the resulting
 * executable under terms of your choice, provided that you also meet,
 * for each linked independent module, the terms and conditions of the
 * license of that module. An independent module is a module which is
 * not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the
 * library, but you are not obligated to do so. If you do not wish to
 * do so, delete this exception statement from your version.
 *
 * Copyright (c) 2003-2004 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import net.percederberg.grammatica.parser.re.RegExp;
import net.percederberg.grammatica.parser.re.Matcher;
import net.percederberg.grammatica.parser.re.RegExpException;

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
    private boolean ignoreCase = false;

    /**
     * The token list feature flag.
     */
    private boolean useTokenList = false;

    /**
     * The string token matcher. This token matcher is used for all
     * string token patterns. This matcher implements a DFA to
     * provide maximum performance.
     */
    private StringTokenMatcher stringMatcher = new StringTokenMatcher();

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
     * Creates a new case-sensitive tokenizer for the specified input
     * stream.
     *
     * @param input          the input stream to read
     */
    public Tokenizer(Reader input) {
        this(input, false);
    }

    /**
     * Creates a new tokenizer for the specified input stream. The
     * tokenizer can be set to process tokens either in case-sensitive
     * or case-insensitive mode.
     *
     * @param input          the input stream to read
     * @param ignoreCase     the character case ignore flag
     *
     * @since 1.5
     */
    public Tokenizer(Reader input, boolean ignoreCase) {
        this.input = new LookAheadReader(input);
        this.ignoreCase = ignoreCase;
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
     * @see #setUseTokenList
     * @see Token#getPreviousToken
     * @see Token#getNextToken
     *
     * @since 1.4
     */
    public boolean getUseTokenList() {
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
     * @see #getUseTokenList
     * @see Token#getPreviousToken
     * @see Token#getNextToken
     *
     * @since 1.4
     */
    public void setUseTokenList(boolean useTokenList) {
        this.useTokenList = useTokenList;
    }

    /**
     * Returns a description of the token pattern with the specified
     * id.
     *
     * @param id             the token pattern id
     *
     * @return the token pattern description, or
     *         null if not present
     */
    public String getPatternDescription(int id) {
        TokenPattern        pattern;
        RegExpTokenMatcher  re;

        pattern = stringMatcher.getPattern(id);
        if (pattern != null) {
            return pattern.toShortString();
        }
        for (int i = 0; i < regexpMatchers.size(); i++) {
            re = (RegExpTokenMatcher) regexpMatchers.get(i);
            if (re.getPattern().getId() == id) {
                return re.getPattern().toShortString();
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
    public int getCurrentLine() {
        return input.getLineNumber();
    }

    /**
     * Returns the current column number. This number will be the
     * column number of the next token returned.
     *
     * @return the current column number
     */
    public int getCurrentColumn() {
        return input.getColumnNumber();
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
    public void addPattern(TokenPattern pattern)
        throws ParserCreationException {

        switch (pattern.getType()) {
        case TokenPattern.STRING_TYPE:
            stringMatcher.addPattern(pattern);
            break;
        case TokenPattern.REGEXP_TYPE:
            try {
                regexpMatchers.add(new RegExpTokenMatcher(pattern, input));
            } catch (RegExpException e) {
                throw new ParserCreationException(
                    ParserCreationException.INVALID_TOKEN_ERROR,
                    pattern.getName(),
                    "regular expression contains error(s): " +
                    e.getMessage());
            }
            break;
        default:
            throw new ParserCreationException(
                ParserCreationException.INVALID_TOKEN_ERROR,
                pattern.getName(),
                "pattern type " + pattern.getType() + " is undefined");
        }
    }

    /**
     * Resets this tokenizer for usage with another input stream. This
     * method will clear all the internal state in the tokenizer as
     * well as close the previous input stream. It is normally called
     * in order to reuse a parser and tokenizer pair for parsing
     * another input stream.
     *
     * @param input          the new input stream to read
     *
     * @since 1.5
     */
    public void reset(Reader input) {
        try {
            this.input.close();
        } catch (IOException ignore) {
            // Do nothing
        }
        this.input = new LookAheadReader(input);
        this.previousToken = null;
        stringMatcher.reset();
        for (int i = 0; i < regexpMatchers.size(); i++) {
            ((RegExpTokenMatcher) regexpMatchers.get(i)).reset(this.input);
        }
    }

    /**
     * Finds the next token on the stream. This method will return
     * null when end of file has been reached. It will return a parse
     * exception if no token matched the input stream, or if a token
     * pattern with the error flag set matched. Any tokens matching a
     * token pattern with the ignore flag set will be silently ignored
     * and the next token will be returned.
     *
     * @return the next token found, or
     *         null if end of file was encountered
     *
     * @throws ParseException if the input stream couldn't be read or
     *             parsed correctly
     */
    public Token next() throws ParseException {
        Token  token = null;

        do {
            token = nextToken();
            if (useTokenList && token != null) {
                token.setPreviousToken(previousToken);
                previousToken = token;
            }
            if (token == null) {
                return null;
            } else if (token.getPattern().isError()) {
                throw new ParseException(
                    ParseException.INVALID_TOKEN_ERROR,
                    token.getPattern().getErrorMessage(),
                    token.getStartLine(),
                    token.getStartColumn());
            } else if (token.getPattern().isIgnore()) {
                token = null;
            }
        } while (token == null);

        return token;
    }

    /**
     * Finds the next token on the stream. This method will return
     * null when end of file has been reached. It will return a parse
     * exception if no token matched the input stream.
     *
     * @return the next token found, or
     *         null if end of file was encountered
     *
     * @throws ParseException if the input stream couldn't be read or
     *             parsed correctly
     */
    private Token nextToken() throws ParseException {
        TokenMatcher  m;
        String        str;
        int           line;
        int           column;

        try {
            m = findMatch();
            if (m != null) {
                line = input.getLineNumber();
                column = input.getColumnNumber();
                str = input.readString(m.getMatchedLength());
                return new Token(m.getMatchedPattern(), str, line, column);
            } else if (input.peek(0) < 0) {
                return null;
            } else {
                line = input.getLineNumber();
                column = input.getColumnNumber();
                throw new ParseException(ParseException.UNEXPECTED_CHAR_ERROR,
                                         input.readString(1),
                                         line,
                                         column);
            }
        } catch (IOException e) {
            throw new ParseException(ParseException.IO_ERROR,
                                     e.getMessage(),
                                     -1,
                                     -1);
        }

    }

    /**
     * Finds the longest token match from the current buffer position.
     * This method will return the token matcher for the best match,
     * or null if no match was found. As a side effect, this method
     * will also set the end of buffer flag.
     *
     * @return the token mathcher with the longest match, or
     *         null if no match was found
     *
     * @throws IOException if an I/O error occurred
     */
    private TokenMatcher findMatch() throws IOException {
        TokenMatcher        bestMatch = null;
        int                 bestLength = 0;
        RegExpTokenMatcher  re;

        // TODO: The regexp token patterns might not always be defined
        //       last. We should check the ordering of the patterns in
        //       case a string match had the same length as a regexp
        //       match.

        // Check string matches
        if (stringMatcher.match(input)) {
            bestMatch = stringMatcher;
            bestLength = bestMatch.getMatchedLength();
        }

        // Check regular expression matches
        for (int i = 0; i < regexpMatchers.size(); i++) {
            re = (RegExpTokenMatcher) regexpMatchers.get(i);
            if (re.match() && re.getMatchedLength() > bestLength) {
                bestMatch = re;
                bestLength = re.getMatchedLength();
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
    public String toString() {
        StringBuffer  buffer = new StringBuffer();

        buffer.append(stringMatcher);
        for (int i = 0; i < regexpMatchers.size(); i++) {
            buffer.append(regexpMatchers.get(i));
        }
        return buffer.toString();
    }


    /**
     * A token pattern matcher. This class is the base class for the
     * two types of token matchers that exist. The token matcher
     * checks for matches with the tokenizer buffer, and maintains the
     * state of the last match.
     */
    private abstract class TokenMatcher {

        /**
         * Returns the latest matched token pattern.
         *
         * @return the latest matched token pattern, or
         *         null if no match found
         */
        public abstract TokenPattern getMatchedPattern();

        /**
         * Returns the length of the latest match.
         *
         * @return the length of the latest match, or
         *         zero (0) if no match found
         */
        public abstract int getMatchedLength();
    }


    /**
     * A regular expression token pattern matcher. This class is used
     * to match a single regular expression with an input stream. This
     * class also maintains the state of the last match.
     */
    private class RegExpTokenMatcher extends TokenMatcher {

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
        private Matcher matcher;

        /**
         * Creates a new regular expression token matcher.
         *
         * @param pattern        the pattern to match
         * @param input          the input stream to check
         *
         * @throws RegExpException if the regular expression couldn't
         *             be created properly
         */
        public RegExpTokenMatcher(TokenPattern pattern, LookAheadReader input)
            throws RegExpException {

            this.pattern = pattern;
            this.regExp = new RegExp(pattern.getPattern(), ignoreCase);
            this.matcher = regExp.matcher(input);
        }

        /**
         * Resets the matcher for another character input stream. This
         * will clear the results of the last match.
         *
         * @param input          the new input stream to check
         */
        public void reset(LookAheadReader input) {
            matcher.reset(input);
        }

        /**
         * Returns the token pattern.
         *
         * @return the token pattern
         */
        public TokenPattern getPattern() {
            return pattern;
        }

        /**
         * Returns the latest matched token pattern.
         *
         * @return the latest matched token pattern, or
         *         null if no match found
         */
        public TokenPattern getMatchedPattern() {
            if (matcher.length() <= 0) {
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
        public int getMatchedLength() {
            return matcher.length();
        }

        /**
         * Checks if the token pattern matches the input stream. This
         * method will also reset all flags in this matcher.
         *
         * @return true if a match was found, or
         *         false otherwise
         *
         * @throws IOException if an I/O error occurred
         */
        public boolean match() throws IOException {
            return matcher.matchFromBeginning();
        }

        /**
         * Returns a string representation of this token matcher.
         *
         * @return a detailed string representation of this matcher
         */
        public String toString() {
            return pattern.toString() + "\n" +
                   regExp.toString() + "\n";
        }
    }


    /**
     * A string token pattern matcher. This class is used to match a
     * set of strings with an input stream. This class internally uses
     * a DFA for maximum performance. It also maintains the state of
     * the last match.
     */
    private class StringTokenMatcher extends TokenMatcher {

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
         * Creates a new string token matcher.
         */
        public StringTokenMatcher() {
        }

        /**
         * Resets the matcher state. This will clear the results of
         * the last match.
         */
        public void reset() {
            match = null;
        }

        /**
         * Returns the latest matched token pattern.
         *
         * @return the latest matched token pattern, or
         *         null if no match found
         */
        public TokenPattern getMatchedPattern() {
            return match;
        }

        /**
         * Returns the length of the latest match.
         *
         * @return the length of the latest match, or
         *         zero (0) if no match found
         */
        public int getMatchedLength() {
            if (match == null) {
                return 0;
            } else {
                return match.getPattern().length();
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
        public TokenPattern getPattern(int id) {
            TokenPattern  pattern;

            for (int i = 0; i < patterns.size(); i++) {
                pattern = (TokenPattern) patterns.get(i);
                if (pattern.getId() == id) {
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
        public void addPattern(TokenPattern pattern) {
            patterns.add(pattern);
            start.addMatch(pattern.getPattern(), ignoreCase, pattern);
        }

        /**
         * Checks if the token pattern matches the input stream. This
         * method will also reset all flags in this matcher.
         *
         * @param input           the input stream to check
         *
         * @return true if a match was found, or
         *         false otherwise
         *
         * @throws IOException if an I/O error occurred
         */
        public boolean match(LookAheadReader input) throws IOException {
            reset();
            match = (TokenPattern) start.matchFrom(input, 0, ignoreCase);
            return match != null;
        }

        /**
         * Returns a string representation of this matcher. This will
         * contain all the token patterns.
         *
         * @return a detailed string representation of this matcher
         */
        public String toString() {
            StringBuffer  buffer = new StringBuffer();

            for (int i = 0; i < patterns.size(); i++) {
                buffer.append(patterns.get(i));
                buffer.append("\n\n");
            }
            return buffer.toString();
        }
    }
}
