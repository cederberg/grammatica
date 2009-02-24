/*
 * TokenPattern.java
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

/**
 * A token pattern. This class contains the definition of a token
 * (i.e. it's pattern), and allows testing a string against this
 * pattern. A token pattern is uniquely identified by an integer id,
 * that must be provided upon creation.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
public class TokenPattern {

    /**
     * The string pattern type. This pattern type is used for tokens
     * that only match an exact string.
     */
    public static final int STRING_TYPE = 1;

    /**
     * The regular expression pattern type. This pattern type is used
     * for tokens that match a regular expression.
     */
    public static final int REGEXP_TYPE = 2;

    /**
     * The token pattern identity.
     */
    private int id;

    /**
     * The token pattern name.
     */
    private String name;

    /**
     * The token pattern type.
     */
    private int type;

    /**
     * The token pattern.
     */
    private String pattern;

    /**
     * The token error flag. If this flag is set, it means that an
     * error should be reported if the token is found. The error
     * message is present in the errorMessage variable.
     *
     * @see #errorMessage
     */
    private boolean error = false;

    /**
     * The token error message. This message will only be set if the
     * token error flag is set.
     *
     * @see #error
     */
    private String errorMessage = null;

    /**
     * The token ignore flag. If this flag is set, it means that the
     * token should be ignored if found. If an ignore message is
     * present in the ignoreMessage variable, it will also be reported
     * as a warning.
     *
     * @see #ignoreMessage
     */
    private boolean ignore = false;

    /**
     * The token ignore message. If this message is set when the token
     * ignore flag is also set, a warning message will be printed if
     * the token is found.
     *
     * @see #ignore
     */
    private String ignoreMessage = null;

    /**
     * The optional debug information message. This is normally set
     * when the token pattern is analyzed by the tokenizer.
     */
    private String debugInfo = null;

    /**
     * Creates a new token pattern.
     *
     * @param id             the token pattern id
     * @param name           the token pattern name
     * @param type           the token pattern type
     * @param pattern        the token pattern
     */
    public TokenPattern(int id, String name, int type, String pattern) {

        this.id = id;
        this.name = name;
        this.type = type;
        this.pattern = pattern;
    }

    /**
     * Checks if the pattern corresponds to an error token. If this
     * is true, it means that an error should be reported if a
     * matching token is found.
     *
     * @return true if the pattern maps to an error token, or
     *         false otherwise
     */
    public boolean isError() {
        return error;
    }

    /**
     * Checks if the pattern corresponds to an ignored token. If this
     * is true, it means that the token should be ignored if found.
     *
     * @return true if the pattern maps to an ignored token, or
     *         false otherwise
     */
    public boolean isIgnore() {
        return ignore;
    }

    /**
     * Returns the unique token pattern identity value.
     *
     * @return the token pattern id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the token pattern name.
     *
     * @return the token pattern name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the token pattern type.
     *
     * @return the token pattern type
     *
     * @see #STRING_TYPE
     * @see #REGEXP_TYPE
     */
    public int getType() {
        return type;
    }

    /**
     * Returns te token pattern.
     *
     * @return the token pattern
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Returns the token error message if the pattern corresponds to
     * an error token.
     *
     * @return the token error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Returns the token ignore message if the pattern corresponds to
     * an ignored token.
     *
     * @return the token ignore message
     */
    public String getIgnoreMessage() {
        return ignoreMessage;
    }

    /**
     * Returns the token debug info message. This is normally set
     * when the token pattern is analyzed by the tokenizer.
     *
     * @return the token debug info message
     *
     * @since 1.5
     */
    public String getDebugInfo() {
        return debugInfo;
    }

    /**
     * Sets the token error flag and assigns a default error message.
     */
    public void setError() {
        setError("unrecognized token found");
    }

    /**
     * Sets the token error flag and assigns the specified error
     * message.
     *
     * @param message        the error message to display
     */
    public void setError(String message) {
        error = true;
        errorMessage = message;
    }

    /**
     * Sets the token ignore flag and clears the ignore message.
     */
    public void setIgnore() {
        setIgnore(null);
    }

    /**
     * Sets the token ignore flag and assigns the specified ignore
     * message.
     *
     * @param message        the ignore message to display
     */
    public void setIgnore(String message) {
        ignore = true;
        ignoreMessage = message;
    }

    /**
     * Sets the token debug info message. This is normally set when
     * the token pattern is analyzed by the tokenizer.
     *
     * @param info           the token debug info message
     *
     * @since 1.5
     */
    public void setDebugInfo(String info) {
        debugInfo = info;
    }

    /**
     * Returns a detailed string representation of this object.
     *
     * @return a detailed string representation of this object
     */
    public String toString() {
        StringBuffer  buffer = new StringBuffer();

        buffer.append(name);
        buffer.append(" (");
        buffer.append(id);
        buffer.append(") = ");
        if (type == STRING_TYPE) {
            buffer.append("\"");
            buffer.append(pattern);
            buffer.append("\"");
        } else if (type == REGEXP_TYPE) {
            buffer.append("<<");
            buffer.append(pattern);
            buffer.append(">>");
        }
        if (error) {
            buffer.append(" ERROR: \"");
            buffer.append(errorMessage);
            buffer.append("\"");
        }
        if (ignore) {
            buffer.append(" IGNORE");
            if (ignoreMessage != null) {
                buffer.append(": \"");
                buffer.append(ignoreMessage);
                buffer.append("\"");
            }
        }
        if (debugInfo != null) {
            buffer.append("\n  ");
            buffer.append(debugInfo);
        }
        return buffer.toString();
    }

    /**
     * Returns a short string representation of this object.
     *
     * @return a short string representation of this object
     */
    public String toShortString() {
        StringBuffer  buffer = new StringBuffer();
        int           newline = pattern.indexOf('\n');

        if (type == STRING_TYPE) {
            buffer.append("\"");
            if (newline >= 0) {
                if (newline > 0 && pattern.charAt(newline - 1) == '\r') {
                    newline--;
                }
                buffer.append(pattern.substring(0, newline));
                buffer.append("(...)");
            } else {
                buffer.append(pattern);
            }
            buffer.append("\"");
        } else {
            buffer.append("<");
            buffer.append(name);
            buffer.append(">");
        }

        return buffer.toString();
    }
}
