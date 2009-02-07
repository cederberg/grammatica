/*
 * ParserTestCase.cs
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
using System.IO;

using PerCederberg.Grammatica.Runtime;

namespace PerCederberg.Grammatica.Test {

    /**
     * Base class for all the parser test cases.
     *
     * @author   Per Cederberg, <per at percederberg dot net>
     * @version  1.0
     */
    public abstract class ParserTestCase {

        /**
         * Parses with the parser and checks the output. If the parsing
         * failed or if the tree didn't match the specified output, a test
         * failure will be reported.
         *
         * @param parser         the parser to use
         * @param output         the expected parse tree
         */
        protected void Parse(Parser parser, string output) {
            try {
                ValidateTree(parser.Parse(), output);
            } catch (ParserCreationException e) {
                Fail(e.Message);
            } catch (ParserLogException e) {
                Fail(e[0].Message);
            }
        }

        /**
         * Parses with the parser and checks the parse error. If the
         * parsing succeeded or if the parse exception didn't match the
         * specified values, a test failure will be reported.
         *
         * @param parser         the parser to use
         * @param type           the parse error type
         * @param line           the line number
         * @param column         the column number
         */
        protected void FailParse(Parser parser,
                                 ParseException.ErrorType type,
                                 int line,
                                 int column) {

            try {
                parser.Parse();
                Fail("parsing succeeded");
            } catch (ParserCreationException e) {
                Fail(e.Message);
            } catch (ParserLogException e) {
                ParseException  p = e[0];

                AssertEquals("error count", 1, e.Count);
                AssertEquals("error type", type, p.Type);
                AssertEquals("line number", line, p.Line);
                AssertEquals("column number", column, p.Column);
            }
        }

        /**
         * Validates that a parse tree is identical to a string
         * representation. If the two representations mismatch, a test
         * failure will be reported.
         *
         * @param root           the parse tree root node
         * @param str            the string representation
         */
        private void ValidateTree(Node root, string str) {
            StringWriter output = new StringWriter();

            root.PrintTo(output);
            ValidateLines(str, output.ToString());
        }

        /**
         * Validates that two strings are identical. If the two strings
         * mismatch, a test failure will be reported.
         *
         * @param expected       the expected result
         * @param result         the result obtained
         */
        private void ValidateLines(string expected, string result) {
            int     line = 1;
            string  expectLine;
            string  resultLine;
            int     pos;

            while (expected.Length > 0 || result.Length > 0) {
                pos = expected.IndexOf('\n');
                if (pos >= 0) {
                    expectLine = expected.Substring(0, pos);
                    expected = expected.Substring(pos + 1);
                } else {
                    expectLine = expected;
                    expected = "";
                }
                pos = result.IndexOf('\n');
                if (pos >= 0) {
                    resultLine = result.Substring(0, pos);
                    result = result.Substring(pos + 1);
                } else {
                    resultLine = result;
                    result = "";
                }
                ValidateLine(line, expectLine, resultLine);
                line++;
            }
        }

        /**
         * Validates that two strings are identical. If the two strings
         * mismatch, a test failure will be reported.
         *
         * @param line           the line number to report
         * @param expected       the expected result
         * @param result         the result obtained
         */
        private void ValidateLine(int line, string expected, string result) {
            AssertEquals("on line: " + line,
                         expected.Trim(),
                         result.Trim());
        }

        /**
         * Throws a test fail exception.
         *
         * @param message         the test error message
         */
        protected void Fail(string message) {
            throw new Exception(message);
        }

        /**
         * Checks that two values are identical. If the values are not
         * identical, a test failure will be reported.
         *
         * @param label          the error label
         * @param expected       the expected value
         * @param result         the obtained value
         */
        protected void AssertEquals(string label,
                                    object expected,
                                    object result) {

            if (!expected.Equals(result)) {
                Fail(label + ", expected: " + expected +
                     ", found: " + result);
            }
        }
    }
}
