/*
 * StringElement.cs
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

using System;
using System.IO;

using PerCederberg.Grammatica.Runtime;

namespace PerCederberg.Grammatica.Runtime.RE {

    /**
     * A regular expression string element. This element only matches
     * an exact string. Once created, the string element is immutable.
     *
     * @author   Per Cederberg, <per at percederberg dot net>
     * @version  1.5
     */
    internal class StringElement : Element {

        /**
         * The string to match with.
         */
        private string value;

        /**
         * Creates a new string element.
         *
         * @param c              the character to match with
         */
        public StringElement(char c)
            : this(c.ToString()) {
        }

        /**
         * Creates a new string element.
         *
         * @param str            the string to match with
         */
        public StringElement(string str) {
            value = str;
        }

        /**
         * Returns the string to be matched.
         *
         * @return the string to be matched
         */
        public string GetString() {
            return value;
        }

        /**
         * Returns this element as it is immutable.
         *
         * @return this string element
         */
        public override object Clone() {
            return this;
        }

        /**
         * Returns the length of a matching string starting at the
         * specified position. The number of matches to skip can also
         * be specified, but numbers higher than zero (0) cause a
         * failed match for any element that doesn't attempt to
         * combine other elements.
         *
         * @param m              the matcher being used
         * @param buffer         the input character buffer to match
         * @param start          the starting position
         * @param skip           the number of matches to skip
         *
         * @return the length of the longest matching string, or
         *         -1 if no match was found
         *
         * @throws IOException if an I/O error occurred
         */
        public override int Match(Matcher m,
                                  ReaderBuffer buffer,
                                  int start,
                                  int skip) {

            int  c;

            if (skip != 0) {
                return -1;
            }
            for (int i = 0; i < value.Length; i++) {
                c = buffer.Peek(start + i);
                if (c < 0) {
                    m.SetReadEndOfString();
                    return -1;
                }
                if (m.IsCaseInsensitive()) {
                    c = (int) Char.ToLower((char) c);
                }
                if (c != (int) value[i]) {
                    return -1;
                }
            }
            return value.Length;
        }

        /**
         * Prints this element to the specified output stream.
         *
         * @param output         the output stream to use
         * @param indent         the current indentation
         */
        public override void PrintTo(TextWriter output, string indent) {
            output.WriteLine(indent + "'" + value + "'");
        }
    }
}
