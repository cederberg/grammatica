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
 * Copyright (c) 2003 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import net.percederberg.grammatica.parser.re.CharBuffer;
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
 * @version  1.4
 */
public class Tokenizer {

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
     * The input stream to read from. When this is set to null, no
     * further input is available.
     */
    private Reader input = null;

    /**
     * The buffer with previously read characters. Normally characters
     * are appended in blocks to this buffer, and for every token that
     * is found, its characters are removed from the buffer.
     */
    private CharBuffer buffer = new CharBuffer();

    /**
     * The current position in the string buffer.
     */
    private int position = 0;

    /**
     * The line number of the first character in the buffer. This
     * value will be incremented when reading past line breaks.
     */
    private int line = 1;

    /**
     * The column number of the first character in the buffer. This
     * value will be updated for every character read.
     */
    private int column = 1;

    /**
     * The end of buffer read flag. This flag is set if the end of
     * the buffer was encountered while matching token patterns.
     */
    private boolean endOfBuffer = false;

    /**
     * The previous token in the token list.
     */
    private Token previousToken = null;

    /**
     * Creates a new tokenizer for the specified input stream.
     *
     * @param input          the input stream to read
     */
    public Tokenizer(Reader input) {
        this.input = input;
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
        return line;
    }

    /**
     * Returns the current column number. This number will be the
     * column number of the next token returned.
     *
     * @return the current column number
     */
    public int getCurrentColumn() {
        return column;
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
                regexpMatchers.add(new RegExpTokenMatcher(pattern));
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
        TokenMatcher    m;
        Token           token;
        String          str;
        ParseException  e;

        // Find longest matching string
        do {
            if (endOfBuffer) {
                readInput();
                endOfBuffer = false;
            }
            m = findMatch();
        } while (endOfBuffer && input != null);

        // Return token results
        if (m != null) {
            str = buffer.substring(position, position + m.getMatchedLength());
            token = new Token(m.getMatchedPattern(), str, line, column);
            position += m.getMatchedLength();
            line = token.getEndLine();
            column = token.getEndColumn() + 1;
            return token;
        } else if (position >= buffer.length()) {
            return null;
        } else {
            e = new ParseException(
                ParseException.UNEXPECTED_CHAR_ERROR,
                String.valueOf(buffer.charAt(position)),
                line,
                column);
            if (buffer.charAt(position) == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
            position++;
            throw e;
        }
    }

    /**
     * Reads characters from the input stream and appends them to the
     * input buffer. This method is safe to call even though the end
     * of file has been reached. As a side effect, this method may
     * also remove
     *
     * @throws ParseException if an error was encountered while
     *             reading the input stream
     */
    private void readInput() throws ParseException {
        char  chars[] = new char[4096];
        int   length;

        // Check for end of file
        if (input == null) {
            return;
        }

        // Remove old characters from buffer
        if (position > 1024) {
            buffer.delete(0, position);
            position = 0;
        }

        // Read characters
        try {
            length = input.read(chars);
        } catch (IOException e) {
            input = null;
            throw new ParseException(ParseException.IO_ERROR,
                                     e.getMessage(),
                                     -1,
                                     -1);
        }

        // Append characters to buffer
        if (length > 0) {
            buffer.append(chars, 0, length);
        }
        if (length < chars.length) {
            try {
                input.close();
            } catch (IOException e) {
                // Do nothing
            }
            input = null;
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
     */
    private TokenMatcher findMatch() {
        TokenMatcher        bestMatch = null;
        int                 bestLength = 0;
        RegExpTokenMatcher  re;

        // Check string matches
        if (stringMatcher.matchFrom(position)) {
            bestMatch = stringMatcher;
            bestLength = bestMatch.getMatchedLength();
        }
        if (stringMatcher.hasReadEndOfString()) {
            endOfBuffer = true;
        }

        // Check regular expression matches
        for (int i = 0; i < regexpMatchers.size(); i++) {
            re = (RegExpTokenMatcher) regexpMatchers.get(i);
            if (re.matchFrom(position)
             && re.getMatchedLength() > bestLength) {

                bestMatch = re;
                bestLength = bestMatch.getMatchedLength();
            }
            if (re.hasReadEndOfString()) {
                endOfBuffer = true;
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

        /**
         * Checks if the end of string was encountered during the last
         * match.
         *
         * @return true if the end of string was reached, or
         *         false otherwise
         */
        public abstract boolean hasReadEndOfString();
    }


    /**
     * A regular expression token pattern matcher. This class is used
     * to match a single regular expression with the tokenizer
     * buffer. This class also maintains the state of the last match.
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
         *
         * @throws RegExpException if the regular expression couldn't
         *             be created properly
         */
        public RegExpTokenMatcher(TokenPattern pattern)
            throws RegExpException {

            this.pattern = pattern;
            this.regExp = new RegExp(pattern.getPattern());
            this.matcher = regExp.matcher(buffer);
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
         * Returns the start position of the latest match.
         *
         * @return the start position of the last match, or
         *         zero (0) if none found
         */
        public int start() {
            if (matcher.length() <= 0) {
                return 0;
            } else {
                return matcher.start();
            }
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
         * Checks if the end of string was encountered during the last
         * match.
         *
         * @return true if the end of string was reached, or
         *         false otherwise
         */
        public boolean hasReadEndOfString() {
            return matcher.hasReadEndOfString();
        }

        /**
         * Checks if the token pattern matches the tokenizer buffer
         * from the specified position. This method will also reset
         * all flags in this matcher.
         *
         * @param pos            the starting position
         *
         * @return true if a match was found, or
         *         false otherwise
         */
        public boolean matchFrom(int pos) {
            return matcher.matchFrom(pos);
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
     * set of strings with the tokenizer buffer. This class
     * internally uses a DFA for maximum performance. It also
     * maintains the state of the last match.
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
         * The end of string read flag.
         */
        private boolean endOfString = false;

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
            endOfString = false;
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
         * Checks if the end of string was encountered during the last
         * match.
         *
         * @return true if the end of string was reached, or
         *         false otherwise
         */
        public boolean hasReadEndOfString() {
            return endOfString;
        }

        /**
         * Sets the end of string encountered flag.
         */
        public void setReadEndOfString() {
            endOfString = true;
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
            start.addMatch(pattern.getPattern(), pattern);
        }

        /**
         * Checks if the token pattern matches the tokenizer buffer
         * from the specified position. This method will also reset
         * all flags in this matcher.
         *
         * @param pos            the starting position
         *
         * @return true if a match was found, or
         *         false otherwise
         */
        public boolean matchFrom(int pos) {
            reset();
            match = (TokenPattern) start.matchFrom(this, pos);
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


    /**
     * A deterministic finite automaton. This is a simple automaton
     * for character sequences. It cannot handle character set state
     * transitions, but only supports single character transitions.
     */
    private class Automaton {

        /**
         * The state value.
         */
        private Object value = null;

        /**
         * The automaton state transition tree. Each transition from
         * this state to another state is added to this tree with the
         * corresponding character.
         */
        private AutomatonTree tree = new AutomatonTree();

        /**
         * Creates a new empty automaton.
         */
        public Automaton() {
        }

        /**
         * Adds a string match to this automaton. New states and
         * transitions will be added to extend this automaton to
         * support the specified string.
         *
         * @param str            the string to match
         * @param value          the match value
         */
        public void addMatch(String str, Object value) {
            Automaton  state;

            if (str.equals("")) {
                this.value = value;
            } else {
                state = tree.find(str.charAt(0));
                if (state == null) {
                    state = new Automaton();
                    state.addMatch(str.substring(1), value);
                    tree.add(str.charAt(0), state);
                } else {
                    state.addMatch(str.substring(1), value);
                }
            }
        }

        /**
         * Checks if the automaton matches the tokenizer buffer from
         * the specified position. This method will set the end of
         * buffer flag in the specified token matcher if the end of
         * buffer is reached.
         *
         * @param m              the string token matcher
         * @param pos            the starting position
         *
         * @return the match value, or
         *         null if no match is found
         */
        public Object matchFrom(StringTokenMatcher m, int pos) {
            Object     result = null;
            Automaton  state;

            if (pos >= buffer.length()) {
                m.setReadEndOfString();
            } else if (tree != null) {
                state = tree.find(buffer.charAt(pos));
                if (state != null) {
                    result = state.matchFrom(m, pos + 1);
                }
            }
            return (result == null) ? value : result;
        }
    }


    /**
     * An automaton state transition tree. This class contains a
     * binary search tree for the automaton transitions from one
     * state to another. All transitions are linked to a single
     * character.
     */
    private class AutomatonTree {

        /**
         * The transition character. If this value is set to the zero
         * ('\0') character, this tree is empty.
         */
        private char value = '\0';

        /**
         * The transition state.
         */
        private Automaton state = null;

        /**
         * The left subtree.
         */
        private AutomatonTree left = null;

        /**
         * The right subtree.
         */
        private AutomatonTree right = null;

        /**
         * Creates a new empty automaton transition tree.
         */
        public AutomatonTree() {
        }

        /**
         * Finds an automaton state from the specified transition
         * character. This method searches this transition tree for
         * a matching transition.
         *
         * @param c              the character to search for
         *
         * @return the automaton state found, or
         *         null if no transition exists
         */
        public Automaton find(char c) {
            if (value == '\0' || value == c) {
                return state;
            } else if (value > c) {
                return left.find(c);
            } else {
                return right.find(c);
            }
        }

        /**
         * Adds a transition to this tree.
         *
         * @param c              the character to transition for
         * @param state          the state to transition to
         */
        public void add(char c, Automaton state) {
            if (value == '\0') {
                this.value = c;
                this.state = state;
                this.left = new AutomatonTree();
                this.right = new AutomatonTree();
            } else if (value > c) {
                left.add(c, state);
            } else {
                right.add(c, state);
            }
        }
    }
}
