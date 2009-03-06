/*
 * Tokenizer.java
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

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

import net.percederberg.grammatica.parser.re.RegExp;
import net.percederberg.grammatica.parser.re.Matcher;

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
    protected boolean ignoreCase = false;

    /**
     * The token list feature flag.
     */
    private boolean useTokenList = false;

    /**
     * The string DFA token matcher. This token matcher uses a
     * deterministic finite automaton (DFA) implementation and is
     * used for all string token patterns. It has a slight speed
     * advantage to the NFA implementation, but should be equivalent
     * on memory usage.
     */
    private StringDFAMatcher stringDfaMatcher = new StringDFAMatcher();

    /**
     * The regular expression NFA token matcher. This token matcher
     * uses a non-deterministic finite automaton (DFA) implementation
     * and is used for most regular expression token patterns. It is
     * somewhat faster than the other recursive regular expression
     * implementations available, but doesn't support the full
     * syntax. It conserves memory by using a fast queue instead of
     * the stack during processing (no stack overflow).
     */
    private NFAMatcher nfaMatcher = new NFAMatcher();

    /**
     * The regular expression token matcher. This token matcher is
     * used for complex regular expressions, but should be avoided
     * due to possibly degraded speed and memory usage compared to
     * the automaton implementations.
     */
    private RegExpMatcher regExpMatcher = new RegExpMatcher();

    /**
     * The character stream reader buffer.
     */
    private ReaderBuffer buffer = null;

    /**
     * The last token match found.
     */
    private TokenMatch lastMatch = new TokenMatch();

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
        this.buffer = new ReaderBuffer(input);
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
        TokenPattern  pattern;

        pattern = stringDfaMatcher.getPattern(id);
        if (pattern == null) {
            pattern = nfaMatcher.getPattern(id);
        }
        if (pattern == null) {
            pattern = regExpMatcher.getPattern(id);
        }
        return (pattern == null) ? null : pattern.toShortString();
    }

    /**
     * Returns the current line number. This number will be the line
     * number of the next token returned.
     *
     * @return the current line number
     */
    public int getCurrentLine() {
        return buffer.lineNumber();
    }

    /**
     * Returns the current column number. This number will be the
     * column number of the next token returned.
     *
     * @return the current column number
     */
    public int getCurrentColumn() {
        return buffer.columnNumber();
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
            try {
                stringDfaMatcher.addPattern(pattern);
            } catch (Exception e) {
                throw new ParserCreationException(
                    ParserCreationException.INVALID_TOKEN_ERROR,
                    pattern.getName(),
                    "error adding string token: " +
                    e.getMessage());
            }
            break;
        case TokenPattern.REGEXP_TYPE:
            try {
                nfaMatcher.addPattern(pattern);
            } catch (Exception ignore) {
                try {
                    regExpMatcher.addPattern(pattern);
                } catch (Exception e) {
                    throw new ParserCreationException(
                        ParserCreationException.INVALID_TOKEN_ERROR,
                        pattern.getName(),
                        "regular expression contains error(s): " +
                        e.getMessage());
                }
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
     * in order to reuse a parser and tokenizer pair with multiple
     * input streams, thereby avoiding the cost of re-analyzing the
     * grammar structures.
     *
     * @param input          the new input stream to read
     *
     * @see Parser#reset(Reader)
     *
     * @since 1.5
     */
    public void reset(Reader input) {
        this.buffer.dispose();
        this.buffer = new ReaderBuffer(input);
        this.previousToken = null;
        this.lastMatch.clear();
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
            if (token == null) {
                return null;
            }
            if (useTokenList) {
                token.setPreviousToken(previousToken);
                previousToken = token;
            }
            if (token.getPattern().isIgnore()) {
                token = null;
            } else if (token.getPattern().isError()) {
                throw new ParseException(
                    ParseException.INVALID_TOKEN_ERROR,
                    token.getPattern().getErrorMessage(),
                    token.getStartLine(),
                    token.getStartColumn());
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
        String  str;
        int     line;
        int     column;

        try {
            lastMatch.clear();
            stringDfaMatcher.match(buffer, lastMatch);
            nfaMatcher.match(buffer, lastMatch);
            regExpMatcher.match(buffer, lastMatch);
            if (lastMatch.length() > 0) {
                line = buffer.lineNumber();
                column = buffer.columnNumber();
                str = buffer.read(lastMatch.length());
                return newToken(lastMatch.pattern(), str, line, column);
            } else if (buffer.peek(0) < 0) {
                return null;
            } else {
                line = buffer.lineNumber();
                column = buffer.columnNumber();
                throw new ParseException(ParseException.UNEXPECTED_CHAR_ERROR,
                                         buffer.read(1),
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
     * Factory method for creating a new token. This method can be
     * overridden to provide other token implementations than the
     * default one.
     *
     * @param pattern        the token pattern
     * @param image          the token image (i.e. characters)
     * @param line           the line number of the first character
     * @param column         the column number of the first character
     *
     * @return the token created
     *
     * @since 1.5
     */
    protected Token newToken(TokenPattern pattern,
                             String image,
                             int line,
                             int column) {

        return new Token(pattern, image, line, column);
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

        buffer.append(stringDfaMatcher);
        buffer.append(nfaMatcher);
        buffer.append(regExpMatcher);
        return buffer.toString();
    }


    /**
     * A token pattern matcher. This class is the base class for the
     * various types of token matchers that exist. The token matcher
     * checks for matches with the tokenizer buffer, and maintains the
     * state of the last match.
     */
    abstract class TokenMatcher {

        /**
         * The array of token patterns.
         */
        protected TokenPattern[] patterns = new TokenPattern[0];

        /**
         * Searches for matching token patterns at the start of the
         * input stream. If a match is found, the token match object
         * is updated.
         *
         * @param buffer         the input buffer to check
         * @param match          the token match to update
         *
         * @throws IOException if an I/O error occurred
         */
        public abstract void match(ReaderBuffer buffer, TokenMatch match)
        throws IOException;

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
            for (int i = 0; i < patterns.length; i++) {
                if (patterns[i].getId() == id) {
                    return patterns[i];
                }
            }
            return null;
        }

        /**
         * Adds a token pattern to this matcher.
         *
         * @param pattern        the pattern to add
         *
         * @throws Exception if the pattern couldn't be added to the matcher
         */
        public void addPattern(TokenPattern pattern) throws Exception {
            TokenPattern[]  temp = patterns;

            patterns = new TokenPattern[temp.length + 1];
            System.arraycopy(temp, 0, patterns, 0, temp.length);
            patterns[temp.length] = pattern;
        }

        /**
         * Returns a string representation of this matcher. This will
         * contain all the token patterns.
         *
         * @return a detailed string representation of this matcher
         */
        public String toString() {
            StringBuffer  buffer = new StringBuffer();

            for (int i = 0; i < patterns.length; i++) {
                buffer.append(patterns[i]);
                buffer.append("\n\n");
            }
            return buffer.toString();
        }
    }


    /**
     * A token pattern matcher using a DFA for string tokens. This
     * class only supports string tokens and must be complemented
     * with another matcher for regular expressions. Internally it
     * uses a DFA to provide high performance.
     */
    class StringDFAMatcher extends TokenMatcher {

        /**
         * The deterministic finite state automaton used for
         * matching.
         */
        private TokenStringDFA automaton = new TokenStringDFA();

        /**
         * Adds a string token pattern to this matcher.
         *
         * @param pattern        the pattern to add
         *
         * @throws Exception if the pattern couldn't be added to the matcher
         */
        public void addPattern(TokenPattern pattern) throws Exception {
            automaton.addMatch(pattern.getPattern(), ignoreCase, pattern);
            super.addPattern(pattern);
        }

        /**
         * Searches for matching token patterns at the start of the
         * input stream. If a match is found, the token match object
         * is updated.
         *
         * @param buffer         the input buffer to check
         * @param match          the token match to update
         *
         * @throws IOException if an I/O error occurred
         */
        public void match(ReaderBuffer buffer, TokenMatch match)
        throws IOException {
            TokenPattern  res = automaton.match(buffer, ignoreCase);

            if (res != null) {
                match.update(res.getPattern().length(), res);
            }
        }
    }


    /**
     * A token pattern matcher using a NFA for both string and
     * regular expression tokens. This class has limited support for
     * regular expressions and must be complemented with another
     * matcher providing full regular expression support. Internally
     * it uses a NFA to provide high performance and low memory
     * usage.
     */
    class NFAMatcher extends TokenMatcher {

        /**
         * The non-deterministic finite state automaton used for
         * matching.
         */
        private TokenNFA automaton = new TokenNFA();

        /**
         * Adds a token pattern to this matcher.
         *
         * @param pattern        the pattern to add
         *
         * @throws Exception if the pattern couldn't be added to the matcher
         */
        public void addPattern(TokenPattern pattern) throws Exception {
            if (pattern.getType() == TokenPattern.STRING_TYPE) {
                automaton.addTextMatch(pattern.getPattern(), ignoreCase, pattern);
            } else {
                automaton.addRegExpMatch(pattern.getPattern(), ignoreCase, pattern);
            }
            super.addPattern(pattern);
        }

        /**
         * Searches for matching token patterns at the start of the
         * input stream. If a match is found, the token match object
         * is updated.
         *
         * @param buffer         the input buffer to check
         * @param match          the token match to update
         *
         * @throws IOException if an I/O error occurred
         */
        public void match(ReaderBuffer buffer, TokenMatch match)
        throws IOException {
            automaton.match(buffer, match);
        }
    }


    /**
     * A token pattern matcher for complex regular expressions. This
     * class only supports regular expression tokens and must be
     * complemented with another matcher for string tokens.
     * Internally it uses the Grammatica RE package for high
     * performance or the native java.util.regex package for maximum
     * compatibility.
     */
    class RegExpMatcher extends TokenMatcher {

        /**
         * The regular expression handlers.
         */
        private RE[] regExps = new RE[0];

        /**
         * Adds a regular expression token pattern to this matcher.
         *
         * @param pattern        the pattern to add
         *
         * @throws Exception if the pattern couldn't be added to the matcher
         */
        public void addPattern(TokenPattern pattern) throws Exception {
            RE[]  temp = regExps;
            RE    re;

            re = new JavaRE(pattern.getPattern());
            regExps = new RE[temp.length + 1];
            System.arraycopy(temp, 0, regExps, 0, temp.length);
            regExps[temp.length] = re;
            pattern.setDebugInfo("native Java regexp");
            super.addPattern(pattern);
        }

        /**
         * Searches for matching token patterns at the start of the
         * input stream. If a match is found, the token match object
         * is updated.
         *
         * @param buffer         the input buffer to check
         * @param match          the token match to update
         *
         * @throws IOException if an I/O error occurred
         */
        public void match(ReaderBuffer buffer, TokenMatch match)
        throws IOException {

            for (int i = 0; i < regExps.length; i++) {
                int length = regExps[i].match(buffer);
                if (length > 0) {
                    match.update(length, patterns[i]);
                }
            }
        }
    }


    /**
     * The regular expression handler base class.
     */
    abstract class RE {

        /**
         * Checks if the start of the input stream matches this
         * regular expression.
         *
         * @param buffer         the input buffer to check
         *
         * @return the longest match found, or
         *         zero (0) if no match was found
         *
         * @throws IOException if an I/O error occurred
         */
        public abstract int match(ReaderBuffer buffer) throws IOException;
    }


    /**
     * The Grammatica built-in regular expression handler.
     */
    class GrammaticaRE extends RE {

        /**
         * The compiled regular expression.
         */
        private RegExp regExp;

        /**
         * The regular expression matcher to use.
         */
        private Matcher matcher = null;

        /**
         * Creates a new Grammatica regular expression handler.
         *
         * @param regex          the regular expression text
         *
         * @throws Exception if the regular expression contained
         *             invalid syntax
         */
        public GrammaticaRE(String regex) throws Exception {
            regExp = new RegExp(regex, ignoreCase);
        }

        /**
         * Checks if the start of the input stream matches this
         * regular expression.
         *
         * @param buffer         the input buffer to check
         *
         * @return the longest match found, or
         *         zero (0) if no match was found
         *
         * @throws IOException if an I/O error occurred
         */
        public int match(ReaderBuffer buffer) throws IOException {
            if (matcher == null) {
                matcher = regExp.matcher(buffer);
            } else {
                matcher.reset(buffer);
            }
            return matcher.matchFromBeginning() ? matcher.length() : 0;
        }
    }


    /**
     * A native Java regular expression handler.
     */
    class JavaRE extends RE {

        /**
         * The compiled regular expression pattern.
         */
        Pattern  pattern;

        /**
         * The regular expression matcher used.
         */
        java.util.regex.Matcher  matcher = null;

        /**
         * Creates a new native regular expression handler.
         *
         * @param regex          the regular expression text
         *
         * @throws Exception if the regular expression contained
         *             invalid syntax
         */
        public JavaRE(String regex) throws Exception {
            if (ignoreCase) {
                pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            } else {
                pattern = Pattern.compile(regex);
            }
        }

        /**
         * Checks if the start of the input stream matches this
         * regular expression.
         *
         * @param buffer         the input buffer to check
         *
         * @return the longest match found, or
         *         zero (0) if no match was found
         *
         * @throws IOException if an I/O error occurred
         */
        public int match(ReaderBuffer buffer) throws IOException {
            int      minSize = ReaderBuffer.BLOCK_SIZE;
            boolean  match;
            int      c;

            if (matcher == null) {
                matcher = pattern.matcher(buffer);
            } else {
                matcher.reset(buffer);
            }
            matcher.useTransparentBounds(true);
            do {
                c = buffer.peek(minSize);
                matcher.region(buffer.position(), buffer.length());
                match = matcher.lookingAt();
                if (matcher.hitEnd()) {
                    minSize *= 2;
                }
            } while (c >= 0 && matcher.hitEnd());
            return match ? matcher.end() - matcher.start() : 0;
        }
    }
}
