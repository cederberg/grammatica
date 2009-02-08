/*
 * GrammarConstants.java
 *
 * THIS FILE HAS BEEN GENERATED AUTOMATICALLY. DO NOT EDIT!
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

package net.percederberg.grammatica;

/**
 * An interface with constants for the parser and tokenizer.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
interface GrammarConstants {

    /**
     * A token identity constant.
     */
    public static final int HEADER = 1001;

    /**
     * A token identity constant.
     */
    public static final int TOKENS = 1002;

    /**
     * A token identity constant.
     */
    public static final int PRODUCTIONS = 1003;

    /**
     * A token identity constant.
     */
    public static final int IGNORE = 1004;

    /**
     * A token identity constant.
     */
    public static final int ERROR = 1005;

    /**
     * A token identity constant.
     */
    public static final int UNTERMINATED_DIRECTIVE = 1006;

    /**
     * A token identity constant.
     */
    public static final int EQUALS = 1007;

    /**
     * A token identity constant.
     */
    public static final int LEFT_PAREN = 1008;

    /**
     * A token identity constant.
     */
    public static final int RIGHT_PAREN = 1009;

    /**
     * A token identity constant.
     */
    public static final int LEFT_BRACE = 1010;

    /**
     * A token identity constant.
     */
    public static final int RIGHT_BRACE = 1011;

    /**
     * A token identity constant.
     */
    public static final int LEFT_BRACKET = 1012;

    /**
     * A token identity constant.
     */
    public static final int RIGHT_BRACKET = 1013;

    /**
     * A token identity constant.
     */
    public static final int QUESTION_MARK = 1014;

    /**
     * A token identity constant.
     */
    public static final int PLUS_SIGN = 1015;

    /**
     * A token identity constant.
     */
    public static final int ASTERISK = 1016;

    /**
     * A token identity constant.
     */
    public static final int VERTICAL_BAR = 1017;

    /**
     * A token identity constant.
     */
    public static final int SEMICOLON = 1018;

    /**
     * A token identity constant.
     */
    public static final int IDENTIFIER = 1019;

    /**
     * A token identity constant.
     */
    public static final int QUOTED_STRING = 1020;

    /**
     * A token identity constant.
     */
    public static final int REGEXP = 1021;

    /**
     * A token identity constant.
     */
    public static final int SINGLE_LINE_COMMENT = 1022;

    /**
     * A token identity constant.
     */
    public static final int MULTI_LINE_COMMENT = 1023;

    /**
     * A token identity constant.
     */
    public static final int WHITESPACE = 1024;

    /**
     * A production node identity constant.
     */
    public static final int GRAMMAR = 2001;

    /**
     * A production node identity constant.
     */
    public static final int HEADER_PART = 2002;

    /**
     * A production node identity constant.
     */
    public static final int HEADER_DECLARATION = 2003;

    /**
     * A production node identity constant.
     */
    public static final int TOKEN_PART = 2004;

    /**
     * A production node identity constant.
     */
    public static final int TOKEN_DECLARATION = 2005;

    /**
     * A production node identity constant.
     */
    public static final int TOKEN_VALUE = 2006;

    /**
     * A production node identity constant.
     */
    public static final int TOKEN_HANDLING = 2007;

    /**
     * A production node identity constant.
     */
    public static final int PRODUCTION_PART = 2008;

    /**
     * A production node identity constant.
     */
    public static final int PRODUCTION_DECLARATION = 2009;

    /**
     * A production node identity constant.
     */
    public static final int PRODUCTION = 2010;

    /**
     * A production node identity constant.
     */
    public static final int PRODUCTION_ATOM = 2011;
}
