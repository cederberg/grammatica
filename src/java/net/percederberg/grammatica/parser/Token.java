/*
 * Token.java
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

package net.percederberg.grammatica.parser;

/**
 * A token node. This class represents a token (i.e. a set of adjacent
 * characters) in a parse tree. The tokens are created by a tokenizer,
 * that groups characters together into tokens according to a set of
 * token patterns.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.4
 */
public class Token extends Node {

    /**
     * The token pattern used for this token.
     */
    private TokenPattern pattern;

    /**
     * The characters that constitute this token. This is normally
     * referred to as the token image.
     */
    private String image;

    /**
     * The line number of the first character in the token image.
     */
    private int startLine;

    /**
     * The column number of the first character in the token image.
     */
    private int startColumn;

    /**
     * The line number of the last character in the token image.
     */
    private int endLine;

    /**
     * The column number of the last character in the token image.
     */
    private int endColumn;

    /**
     * The previous token in the list of tokens.
     */
    private Token previous = null;

    /**
     * The next token in the list of tokens.
     */
    private Token next = null;

    /**
     * Creates a new token.
     *
     * @param pattern        the token pattern
     * @param image          the token image (i.e. characters)
     * @param line           the line number of the first character
     * @param col            the column number of the first character
     */
    public Token(TokenPattern pattern, String image, int line, int col) {
        this.pattern = pattern;
        this.image = image;
        this.startLine = line;
        this.startColumn = col;
        this.endLine = line;
        this.endColumn = col + image.length() - 1;
        for (int pos = 0; image.indexOf('\n', pos) >= 0;) {
            pos = image.indexOf('\n', pos) + 1;
            this.endLine++;
            this.endColumn = image.length() - pos;
        }
    }

    /**
     * Returns the token (pattern) id. This value is set as a unique
     * identifier when creating the token pattern to simplify later
     * identification.
     *
     * @return the token id
     */
    public int getId() {
        return pattern.getId();
    }

    /**
     * Returns the token node name.
     *
     * @return the token node name
     */
    public String getName() {
        return pattern.getName();
    }

    /**
     * Returns the token image. The token image consists of the
     * input characters matched to form this token.
     *
     * @return the token image
     */
    public String getImage() {
        return image;
    }

    /**
     * The line number of the first character in the token image.
     *
     * @return the line number of the first token character
     */
    public int getStartLine() {
        return startLine;
    }

    /**
     * The column number of the first character in the token image.
     *
     * @return the column number of the first token character
     */
    public int getStartColumn() {
        return startColumn;
    }

    /**
     * The line number of the last character in the token image.
     *
     * @return the line number of the last token character
     */
    public int getEndLine() {
        return endLine;
    }

    /**
     * The column number of the last character in the token image.
     *
     * @return the column number of the last token character
     */
    public int getEndColumn() {
        return endColumn;
    }

    /**
     * Returns the token pattern.
     *
     * @return the token pattern
     */
    TokenPattern getPattern() {
        return pattern;
    }

    /**
     * Returns the previuos token. The previous token may be a token
     * that has been ignored in the parsing. Note that if the token
     * list feature hasn't been used in the tokenizer, this method
     * will always return null. By default the token list feature is
     * not used.
     *
     * @return the previous token, or
     *         null if no such token is available
     *
     * @see #getNextToken
     * @see Tokenizer#getUseTokenList
     * @see Tokenizer#setUseTokenList
     *
     * @since 1.4
     */
    public Token getPreviousToken() {
        return previous;
    }

    /**
     * Sets the previous token in the token list. This method will
     * also modify the token specified to have this token as it's
     * next token.
     *
     * @param previous       the previous token, or null for none
     *
     * @since 1.4
     */
    void setPreviousToken(Token previous) {
        if (this.previous != null) {
            this.previous.next = null;
        }
        this.previous = previous;
        if (previous != null) {
            previous.next = this;
        }
    }

    /**
     * Returns the next token. The next token may be a token that has
     * been ignored in the parsing. Note that if the token list
     * feature hasn't been used in the tokenizer, this method will
     * always return null. By default the token list feature is not
     * used.
     *
     * @return the next token, or
     *         null if no such token is available
     *
     * @see #getPreviousToken
     * @see Tokenizer#getUseTokenList
     * @see Tokenizer#setUseTokenList
     *
     * @since 1.4
     */
    public Token getNextToken() {
        return next;
    }

    /**
     * Sets the next token in the token list. This method will also
     * modify the token specified to have this token as it's
     * previous token.
     *
     * @param next           the next token, or null for none
     *
     * @since 1.4
     */
    void setNextToken(Token next) {
        if (this.next != null) {
            this.next.previous = null;
        }
        this.next = next;
        if (next != null) {
            next.previous = this;
        }
    }

    /**
     * Returns a detailed string representation of this token.
     *
     * @return a detailed string representation of this token
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer  buffer = new StringBuffer();
        char          chr;

        buffer.append(pattern.getName());
        buffer.append("(");
        buffer.append(pattern.getId());
        buffer.append("): \"");
        for (int i = 0; i < image.length(); i++) {
            chr = image.charAt(i);
            if (Character.isISOControl(chr) || (i > 25 && image.length() > 30)) {
                buffer.append("(...)");
                break;
            } else {
                buffer.append(chr);
            }
        }
        buffer.append("\", line: ");
        buffer.append(startLine);
        buffer.append(", col: ");
        buffer.append(startColumn);

        return buffer.toString();
    }

    /**
     * Returns a short string representation of this token. The string
     * will only contain the token image and possibly the token
     * pattern name.
     *
     * @return a short string representation of this token
     */
    public String toShortString() {
        StringBuffer  buffer = new StringBuffer();
        char          chr;

        buffer.append('"');
        for (int i = 0; i < image.length(); i++) {
            chr = image.charAt(i);
            if (Character.isISOControl(chr) || (i > 25 && image.length() > 30)) {
                buffer.append("(...)");
                break;
            } else {
                buffer.append(chr);
            }
        }
        buffer.append('"');
        if (pattern.getType() == TokenPattern.REGEXP_TYPE) {
            buffer.append(" <");
            buffer.append(pattern.getName());
            buffer.append(">");
        }

        return buffer.toString();
    }
}
