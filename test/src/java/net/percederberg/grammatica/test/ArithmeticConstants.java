/*
 * ArithmeticConstants.java
 *
 * THIS FILE HAS BEEN GENERATED AUTOMATICALLY. DO NOT EDIT!
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
