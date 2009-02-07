/*
 * ParserCreationException.java
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
 * A parser creation exception. This exception is used for signalling
 * an error in the token or production patterns, making it impossible
 * to create a working parser or tokenizer.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
public class ParserCreationException extends Exception {

    /**
     * The internal error type constant. This type is only used to
     * signal an error that is a result of a bug in the parser or
     * tokenizer code.
     */
    public static final int INTERNAL_ERROR = 0;

    /**
     * The invalid parser error type constant. This type is used when
     * the parser as such is invalid. This error is typically caused
     * by using a parser without any patterns.
     */
    public static final int INVALID_PARSER_ERROR = 1;

    /**
     * The invalid token error type constant. This type is used when a
     * token pattern is erroneous. This error is typically caused by
     * an invalid pattern type or an erroneous regular expression.
     */
    public static final int INVALID_TOKEN_ERROR = 2;

    /**
     * The invalid production error type constant. This type is used
     * when a production pattern is erroneous. This error is typically
     * caused by referencing undeclared productions, or violating some
     * other production pattern constraint.
     */
    public static final int INVALID_PRODUCTION_ERROR = 3;

    /**
     * The infinite loop error type constant. This type is used when
     * an infinite loop has been detected in the grammar. One of the
     * productions in the loop will be reported.
     */
    public static final int INFINITE_LOOP_ERROR = 4;

    /**
     * The inherent ambiguity error type constant. This type is used
     * when the set of production patterns (i.e. the grammar) contains
     * ambiguities that cannot be resolved.
     */
    public static final int INHERENT_AMBIGUITY_ERROR = 5;

    /**
     * The error type.
     */
    private int type;

    /**
     * The token or production pattern name. This variable is only
     * set for some error types.
     */
    private String name;

    /**
     * The additional error information string. This variable is only
     * set for some error types.
     */
    private String info;

    /**
     * The error details list. This variable is only set for some
     * error types.
     */
    private ArrayList details;

    /**
     * Creates a new parser creation exception.
     *
     * @param type           the parse error type
     * @param info           the additional error information
     */
    public ParserCreationException(int type,
                                   String info) {

        this(type, null, info);
    }

    /**
     * Creates a new parser creation exception.
     *
     * @param type           the parse error type
     * @param name           the token or production pattern name
     * @param info           the additional error information
     */
    public ParserCreationException(int type,
                                   String name,
                                   String info) {

        this(type, name, info, null);
    }

    /**
     * Creates a new parser creation exception.
     *
     * @param type           the parse error type
     * @param name           the token or production pattern name
     * @param info           the additional error information
     * @param details        the error details list
     */
    public ParserCreationException(int type,
                                   String name,
                                   String info,
                                   ArrayList details) {

        this.type = type;
        this.name = name;
        this.info = info;
        this.details = details;
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
     * Returns the token or production name.
     *
     * @return the token or production name
     */
    public String getName() {
        return name;
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
     * Returns the detailed error information as a string
     *
     * @return the detailed error information
     */
    public String getDetails() {
        StringBuffer  buffer = new StringBuffer();

        if (details == null) {
            return null;
        }
        for (int i = 0; i < details.size(); i++) {
            if (i > 0) {
                buffer.append(", ");
                if (i + 1 == details.size()) {
                    buffer.append("and ");
                }
            }
            buffer.append(details.get(i));
        }

        return buffer.toString();
    }

    /**
     * Returns the error message. This message will contain all the
     * information available.
     *
     * @return the error message
     */
    public String getMessage() {
        StringBuffer  buffer = new StringBuffer();

        switch (type) {
        case INVALID_PARSER_ERROR:
            buffer.append("parser is invalid, as ");
            buffer.append(info);
            break;
        case INVALID_TOKEN_ERROR:
            buffer.append("token '");
            buffer.append(name);
            buffer.append("' is invalid, as ");
            buffer.append(info);
            break;
        case INVALID_PRODUCTION_ERROR:
            buffer.append("production '");
            buffer.append(name);
            buffer.append("' is invalid, as ");
            buffer.append(info);
            break;
        case INFINITE_LOOP_ERROR:
            buffer.append("infinite loop found in production pattern '");
            buffer.append(name);
            buffer.append("'");
            break;
        case INHERENT_AMBIGUITY_ERROR:
            buffer.append("inherent ambiguity in production '");
            buffer.append(name);
            buffer.append("'");
            if (info != null) {
                buffer.append(" ");
                buffer.append(info);
            }
            if (details != null) {
                buffer.append(" starting with ");
                if (details.size() > 1) {
                    buffer.append("tokens ");
                } else {
                    buffer.append("token ");
                }
                buffer.append(getDetails());
            }
            break;
        default:
            buffer.append("internal error");
        }

        return buffer.toString();
    }
}
