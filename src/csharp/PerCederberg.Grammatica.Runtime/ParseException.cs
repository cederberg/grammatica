/*
 * ParseException.cs
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

using System;
using System.Collections;
using System.Text;

namespace PerCederberg.Grammatica.Runtime {

    /**
     * A parse exception.
     *
     * @author   Per Cederberg, <per at percederberg dot net>
     * @version  1.5
     */
    public class ParseException : Exception {

        /**
         * The error type enumeration.
         */
        public enum ErrorType {

            /**
             * The internal error type is only used to signal an error
             * that is a result of a bug in the parser or tokenizer
             * code.
             */
            INTERNAL,

            /**
             * The I/O error type is used for stream I/O errors.
             */
            IO,

            /**
             * The unexpected end of file error type is used when end
             * of file is encountered instead of a valid token.
             */
            UNEXPECTED_EOF,

            /**
             * The unexpected character error type is used when a
             * character is read that isn't handled by one of the
             * token patterns.
             */
            UNEXPECTED_CHAR,

            /**
             * The unexpected token error type is used when another
             * token than the expected one is encountered.
             */
            UNEXPECTED_TOKEN,

            /**
             * The invalid token error type is used when a token
             * pattern with an error message is matched. The
             * additional information provided should contain the
             * error message.
             */
            INVALID_TOKEN,

            /**
             * The analysis error type is used when an error is
             * encountered in the analysis. The additional information
             * provided should contain the error message.
             */
            ANALYSIS
        }

        /**
         * The error type.
         */
        private ErrorType type;

        /**
         * The additional information string.
         */
        private string info;

        /**
         * The additional details information. This variable is only
         * used for unexpected token errors.
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
        public ParseException(ErrorType type,
                              string info,
                              int line,
                              int column)
            : this(type, info, null, line, column) {
        }

        /**
         * Creates a new parse exception. This constructor is only
         * used to supply the detailed information array, which is
         * only used for expected token errors. The list then contains
         * descriptions of the expected tokens.
         *
         * @param type           the parse error type
         * @param info           the additional information
         * @param details        the additional detailed information
         * @param line           the line number, or -1 for unknown
         * @param column         the column number, or -1 for unknown
         */
        public ParseException(ErrorType type,
                              string info,
                              ArrayList details,
                              int line,
                              int column) {

            this.type = type;
            this.info = info;
            this.details = details;
            this.line = line;
            this.column = column;
        }

        /**
         * The error type property (read-only).
         *
         * @since 1.5
         */
        public ErrorType Type {
            get {
                return type;
            }
        }

        /**
         * Returns the error type.
         *
         * @return the error type
         *
         * @see #Type
         *
         * @deprecated Use the Type property instead.
         */
        public ErrorType GetErrorType() {
            return Type;
        }

        /**
         * The additional error information property (read-only).
         *
         * @since 1.5
         */
        public string Info {
            get {
                return info;
            }
        }

        /**
         * Returns the additional error information.
         *
         * @return the additional error information
         *
         * @see #Info
         *
         * @deprecated Use the Info property instead.
         */
        public string GetInfo() {
            return Info;
        }

        /**
         * The additional detailed error information property
         * (read-only).
         *
         * @since 1.5
         */
        public ArrayList Details {
            get {
                return new ArrayList(details);
            }
        }

        /**
         * Returns the additional detailed error information.
         *
         * @return the additional detailed error information
         *
         * @see #Details
         *
         * @deprecated Use the Details property instead.
         */
        public ArrayList GetDetails() {
            return Details;
        }

        /**
         * The line number property (read-only). This is the line
         * number where the error occured, or -1 if unknown.
         *
         * @since 1.5
         */
        public int Line {
            get {
                return line;
            }
        }

        /**
         * Returns the line number where the error occured.
         *
         * @return the line number of the error, or
         *         -1 if unknown
         *
         * @see #Line
         *
         * @deprecated Use the Line property instead.
         */
        public int GetLine() {
            return Line;
        }

        /**
         * The column number property (read-only). This is the column
         * number where the error occured, or -1 if unknown.
         *
         * @since 1.5
         */
        public int Column {
            get {
                return column;
            }
        }

        /**
         * Returns the column number where the error occured.
         *
         * @return the column number of the error, or
         *         -1 if unknown
         *
         * @see #Column
         *
         * @deprecated Use the Column property instead.
         */
        public int GetColumn() {
            return column;
        }

        /**
         * The message property (read-only). This property contains
         * the detailed exception error message, including line and
         * column numbers when available.
         *
         * @see #ErrorMessage
         */
        public override string Message {
            get{
                StringBuilder  buffer = new StringBuilder();

                // Add error description
                buffer.Append(ErrorMessage);

                // Add line and column
                if (line > 0 && column > 0) {
                    buffer.Append(", on line: ");
                    buffer.Append(line);
                    buffer.Append(" column: ");
                    buffer.Append(column);
                }

                return buffer.ToString();
            }
        }

        /**
         * Returns a default error message.
         *
         * @return a default error message
         *
         * @see #Message
         *
         * @deprecated Use the Message property instead.
         */
        public string GetMessage() {
            return Message;
        }

        /**
         * The error message property (read-only). This property
         * contains all the information available, except for the line
         * and column number information.
         *
         * @see #Message
         *
         * @since 1.5
         */
        public string ErrorMessage {
            get {
                StringBuilder  buffer = new StringBuilder();

                // Add type and info
                switch (type) {
                case ErrorType.IO:
                    buffer.Append("I/O error: ");
                    buffer.Append(info);
                    break;
                case ErrorType.UNEXPECTED_EOF:
                    buffer.Append("unexpected end of file");
                    break;
                case ErrorType.UNEXPECTED_CHAR:
                    buffer.Append("unexpected character '");
                    buffer.Append(info);
                    buffer.Append("'");
                    break;
                case ErrorType.UNEXPECTED_TOKEN:
                    buffer.Append("unexpected token ");
                    buffer.Append(info);
                    if (details != null) {
                        buffer.Append(", expected ");
                        if (details.Count > 1) {
                            buffer.Append("one of ");
                        }
                        buffer.Append(GetMessageDetails());
                    }
                    break;
                case ErrorType.INVALID_TOKEN:
                    buffer.Append(info);
                    break;
                case ErrorType.ANALYSIS:
                    buffer.Append(info);
                    break;
                default:
                    buffer.Append("internal error");
                    if (info != null) {
                	    buffer.Append(": ");
                        buffer.Append(info);
                    }
                    break;
                }

                return buffer.ToString();
            }
        }

        /**
         * Returns the error message. This message will contain all the
         * information available, except for the line and column number
         * information.
         *
         * @return the error message
         *
         * @see #ErrorMessage
         *
         * @deprecated Use the ErrorMessage property instead.
         */
        public string GetErrorMessage() {
            return ErrorMessage;
        }

        /**
         * Returns a string containing all the detailed information in
         * a list. The elements are separated with a comma.
         *
         * @return the detailed information string
         */
        private string GetMessageDetails() {
            StringBuilder  buffer = new StringBuilder();

            for (int i = 0; i < details.Count; i++) {
                if (i > 0) {
                    buffer.Append(", ");
                    if (i + 1 == details.Count) {
                        buffer.Append("or ");
                    }
                }
                buffer.Append(details[i]);
            }

            return buffer.ToString();
        }
    }
}
