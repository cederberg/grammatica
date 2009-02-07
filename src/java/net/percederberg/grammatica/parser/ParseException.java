/*
 * ParseException.java
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

import java.util.ArrayList;

/**
 * A parse exception.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.1
 */
public class ParseException extends Exception {

    /**
     * The internal error type constant. This type is only used to
     * signal an error that is a result of a bug in the parser or
     * tokenizer code.
     */
    public static final int INTERNAL_ERROR = 0;

    /**
     * The I/O error type constant. This type is used for stream I/O
     * errors.
     */
    public static final int IO_ERROR = 1;

    /**
     * The unexpected end of file error type constant. This type is
     * used when end of file is encountered instead of a valid token.
     */
    public static final int UNEXPECTED_EOF_ERROR = 2;

    /**
     * The unexpected character error type constant. This type is used
     * when a character is read that isn't handled by one of the token
     * patterns.
     */
    public static final int UNEXPECTED_CHAR_ERROR = 3;

    /**
     * The unexpected token error type constant. This type is used
     * when another token than the expected one is encountered.
     */
    public static final int UNEXPECTED_TOKEN_ERROR = 4;

    /**
     * The invalid token error type constant. This type is used when
     * a token pattern with an error message is matched. The
     * additional information provided should contain the error
     * message.
     */
    public static final int INVALID_TOKEN_ERROR = 5;

    /**
     * The analysis error type constant. This type is used when an
     * error is encountered in the analysis. The additional
     * information provided should contain the error message.
     */
    public static final int ANALYSIS_ERROR = 6;

    /**
     * The error type.
     */
    private int type;

    /**
     * The additional information string.
     */
    private String info;

    /**
     * The additional details information. This variable is only used
     * for unexpected token errors.
     */
    private ArrayList details;

    /**
     * The line number.
     */
    private int line;

    /**
     * The column number.
     */
    private int column;

    /**
     * Creates a new parse exception.
     *
     * @param type           the parse error type
     * @param info           the additional information
     * @param line           the line number, or -1 for unknown
     * @param column         the column number, or -1 for unknown
     */
    public ParseException(int type,
                          String info,
                          int line,
                          int column) {

        this(type, info, null, line, column);
    }

    /**
     * Creates a new parse exception. This constructor is only used
     * to supply the detailed information array, which is only used
     * for expected token errors. The list then contains descriptions
     * of the expected tokens.
     *
     * @param type           the parse error type
     * @param info           the additional information
     * @param details        the additional detailed information
     * @param line           the line number, or -1 for unknown
     * @param column         the column number, or -1 for unknown
     */
    public ParseException(int type,
                          String info,
                          ArrayList details,
                          int line,
                          int column) {

        super();
        this.type = type;
        this.info = info;
        this.details = details;
        this.line = line;
        this.column = column;
    }

    /**
     * Returns the error type.
     *
     * @return the error type
     */
    public int getErrorType() {
        return type;
    }

    /**
     * Returns the additional error information.
     *
     * @return the additional error information
     */
    public String getInfo() {
        return info;
    }

    /**
     * Returns the additional detailed error information.
     *
     * @return the additional detailed error information
     */
    public ArrayList getDetails() {
        return new ArrayList(details);
    }

    /**
     * Returns the line number where the error occured.
     *
     * @return the line number of the error, or
     *         -1 if unknown
     */
    public int getLine() {
        return line;
    }

    /**
     * Returns the column number where the error occured.
     *
     * @return the column number of the error, or
     *         -1 if unknown
     */
    public int getColumn() {
        return column;
    }

    /**
     * Returns the detailed error message. This message will contain
     * the same string as getErrorMessage(), but with line number and
     * column number information appended.
     *
     * @return the detailed error message
     */
    public String getMessage() {
        StringBuffer  buffer = new StringBuffer();

        // Add error description
        buffer.append(getErrorMessage());

        // Add line and column
        if (line > 0 && column > 0) {
            buffer.append(", on line ");
            buffer.append(line);
            buffer.append(" column: ");
            buffer.append(column);
        }

        return buffer.toString();
    }

    /**
     * Returns the error message. This message will contain all the
     * information available, except for the line and column number
     * information.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        StringBuffer  buffer = new StringBuffer();

        switch (type) {
        case IO_ERROR:
            buffer.append("I/O error: ");
            buffer.append(info);
            break;
        case UNEXPECTED_EOF_ERROR:
            buffer.append("unexpected end of file");
            break;
        case UNEXPECTED_CHAR_ERROR:
            buffer.append("unexpected character '");
            buffer.append(info);
            buffer.append("'");
            break;
        case UNEXPECTED_TOKEN_ERROR:
            buffer.append("unexpected token ");
            buffer.append(info);
            if (details != null) {
                buffer.append(", expected ");
                if (details.size() > 1) {
                    buffer.append("one of ");
                }
                buffer.append(getMessageDetails());
            }
            break;
        case INVALID_TOKEN_ERROR:
            buffer.append(info);
            break;
        case ANALYSIS_ERROR:
            buffer.append(info);
            break;
        default:
            buffer.append("internal error");
            if (info != null) {
                buffer.append(": ");
                buffer.append(info);
            }
        }

        return buffer.toString();
    }

    /**
     * Returns a string containing all the detailed information in
     * a list. The elements are separated with a comma.
     *
     * @return the detailed information string
     */
    private String getMessageDetails() {
        StringBuffer  buffer = new StringBuffer();

        for (int i = 0; i < details.size(); i++) {
            if (i > 0) {
                buffer.append(", ");
                if (i + 1 == details.size()) {
                    buffer.append("or ");
                }
            }
            buffer.append(details.get(i));
        }

        return buffer.toString();
    }
}
