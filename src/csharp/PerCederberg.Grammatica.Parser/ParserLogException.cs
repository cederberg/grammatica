/*
 * ParserLogException.cs
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
 * Copyright (c) 2003-2005 Per Cederberg. All rights reserved.
 */

using System;
using System.Collections;
using System.Text;

namespace PerCederberg.Grammatica.Parser {

    /**
     * A parser log exception. This class contains a list of all the
     * parse errors encountered while parsing.
     *
     * @author   Per Cederberg, <per at percederberg dot net>
     * @version  1.5
     * @since    1.1
     */
    public class ParserLogException : Exception {

        /**
         * The list of errors found.
         */
        private ArrayList errors = new ArrayList();

        /**
         * Creates a new empty parser log exception.
         */
        public ParserLogException() {
        }

        /**
         * The message property (read-only). This property contains
         * the detailed exception error message.
         */
        public override string Message {
            get{
                StringBuilder  buffer = new StringBuilder();

                for (int i = 0; i < Count; i++) {
                    if (i > 0) {
                        buffer.Append("\n");
                    }
                    buffer.Append(this[i].Message);
                }
                return buffer.ToString();
            }
        }

        /**
         * The error count property (read-only).
         *
         * @since 1.5
         */
        public int Count {
            get {
                return errors.Count;
            }
        }

        /**
         * Returns the number of errors in this log.
         *
         * @return the number of errors in this log
         *
         * @see #Count
         *
         * @deprecated Use the Count property instead.
         */
        public int GetErrorCount() {
            return Count;
        }

        /**
         * The error index (read-only). This index contains all the
         * errors in this error log.
         *
         * @param index          the error index, 0 <= index < Count
         *
         * @return the parse error requested
         *
         * @since 1.5
         */
        public ParseException this[int index] {
            get {
                return (ParseException) errors[index];
            }
        }

        /**
         * Returns a specific error from the log.
         *
         * @param index          the error index, 0 <= index < count
         *
         * @return the parse error requested
         *
         * @deprecated Use the class indexer instead.
         */
        public ParseException GetError(int index) {
            return this[index];
        }

        /**
         * Adds a parse error to the log.
         *
         * @param e              the parse error to add
         */
        public void AddError(ParseException e) {
            errors.Add(e);
        }

        /**
         * Returns the detailed error message. This message will contain
         * the error messages from all errors in this log, separated by
         * a newline.
         *
         * @return the detailed error message
         *
         * @see #Message
         *
         * @deprecated Use the Message property instead.
         */
        public string GetMessage() {
            return Message;
        }
    }
}
