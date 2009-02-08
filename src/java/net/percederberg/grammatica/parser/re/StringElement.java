/*
 * StringElement.java
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

package net.percederberg.grammatica.parser.re;

import java.io.IOException;
import java.io.PrintWriter;

import net.percederberg.grammatica.parser.ReaderBuffer;

/**
 * A regular expression string element. This element only matches an
 * exact string. Once created, the string element is immutable.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
class StringElement extends Element {

    /**
     * The string to match with.
     */
    private String value = null;

    /**
     * Creates a new string element.
     *
     * @param c              the character to match with
     */
    public StringElement(char c) {
        this(String.valueOf(c));
    }

    /**
     * Creates a new string element.
     *
     * @param str            the string to match with
     */
    public StringElement(String str) {
        value = str;
    }

    /**
     * Returns the string to be matched.
     *
     * @return the string to be matched
     */
    public String getString() {
        return value;
    }

    /**
     * Returns this element as it is immutable.
     *
     * @return this string element
     */
    public Object clone() {
        return this;
    }

    /**
     * Returns the length of a matching string starting at the
     * specified position. The number of matches to skip can also be
     * specified, but numbers higher than zero (0) cause a failed
     * match for any element that doesn't attempt to combine other
     * elements.
     *
     * @param m              the matcher being used
     * @param buffer         the input character buffer to match
     * @param start          the starting position
     * @param skip           the number of matches to skip
     *
     * @return the length of the longest matching string, or
     *         -1 if no match was found
     *
     * @throws IOException if a I/O error occurred
     */
    public int match(Matcher m, ReaderBuffer buffer, int start, int skip)
        throws IOException {

        int  c;

        if (skip != 0) {
            return -1;
        }
        for (int i = 0; i < value.length(); i++) {
            c = buffer.peek(start + i);
            if (c < 0) {
                m.setReadEndOfString();
                return -1;
            }
            if (m.isCaseInsensitive()) {
                c = Character.toLowerCase((char) c);
            }
            if (c != value.charAt(i)) {
                return -1;
            }
        }
        return value.length();
    }

    /**
     * Prints this element to the specified output stream.
     *
     * @param output         the output stream to use
     * @param indent         the current indentation
     */
    public void printTo(PrintWriter output, String indent) {
        output.println(indent + "'" + value + "'");
    }

}
