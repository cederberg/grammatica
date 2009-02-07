/*
 * ArithmeticConstants.java
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
 * Copyright (c) 2003 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.test;

/**
 * An interface with constants for the parser and tokenizer.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
interface ArithmeticConstants {

    /**
     * A token identity constant.
     */
    public static final int ADD = 1001;

    /**
     * A token identity constant.
     */
    public static final int SUB = 1002;

    /**
     * A token identity constant.
     */
    public static final int MUL = 1003;

    /**
     * A token identity constant.
     */
    public static final int DIV = 1004;

    /**
     * A token identity constant.
     */
    public static final int LEFT_PAREN = 1005;

    /**
     * A token identity constant.
     */
    public static final int RIGHT_PAREN = 1006;

    /**
     * A token identity constant.
     */
    public static final int NUMBER = 1007;

    /**
     * A token identity constant.
     */
    public static final int IDENTIFIER = 1008;

    /**
     * A token identity constant.
     */
    public static final int WHITESPACE = 1009;

    /**
     * A production node identity constant.
     */
    public static final int EXPRESSION = 2001;

    /**
     * A production node identity constant.
     */
    public static final int EXPRESSION_REST = 2002;

    /**
     * A production node identity constant.
     */
    public static final int TERM = 2003;

    /**
     * A production node identity constant.
     */
    public static final int TERM_REST = 2004;

    /**
     * A production node identity constant.
     */
    public static final int FACTOR = 2005;

    /**
     * A production node identity constant.
     */
    public static final int ATOM = 2006;
}
