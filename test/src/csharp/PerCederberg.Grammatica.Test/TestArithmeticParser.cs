/*
 * TestArithmeticParser.cs
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

using System.IO;
using PerCederberg.Grammatica.Runtime;

namespace PerCederberg.Grammatica.Test {

    /**
     * A test case for the ArithmeticParser class.
     *
     * @author   Per Cederberg, <per at percederberg dot net>
     * @version  1.5
     */
    public class TestArithmeticParser : ParserTestCase {

        /**
         * The valid input string.
         */
        private const string VALID_INPUT =
            "1 + 2*a\n" +
            " + 345";

        /**
         * The parse tree for the valid input string.
         */
        private const string VALID_OUTPUT =
            "Expression(2001)\n" +
            "  Term(2003)\n" +
            "    Factor(2005)\n" +
            "      Atom(2006)\n" +
            "        NUMBER(1007): \"1\", line: 1, col: 1\n" +
            "  ExpressionRest(2002)\n" +
            "    ADD(1001): \"+\", line: 1, col: 3\n" +
            "    Expression(2001)\n" +
            "      Term(2003)\n" +
            "        Factor(2005)\n" +
            "          Atom(2006)\n" +
            "            NUMBER(1007): \"2\", line: 1, col: 5\n" +
            "        TermRest(2004)\n" +
            "          MUL(1003): \"*\", line: 1, col: 6\n" +
            "          Term(2003)\n" +
            "            Factor(2005)\n" +
            "              Atom(2006)\n" +
            "                IDENTIFIER(1008): \"a\", line: 1, col: 7\n" +
            "      ExpressionRest(2002)\n" +
            "        ADD(1001): \"+\", line: 2, col: 2\n" +
            "        Expression(2001)\n" +
            "          Term(2003)\n" +
            "            Factor(2005)\n" +
            "              Atom(2006)\n" +
            "                NUMBER(1007): \"345\", line: 2, col: 4\n";

        /**
         * The unexpected EOF input string.
         */
        private const string UNEXPECTED_EOF_INPUT = "1 *\t \n";

        /**
         * The unexpected character input string.
         */
        private const string UNEXPECTED_CHAR_INPUT = "1\n # 4";

        /**
         * The unexpected token input string.
         */
        private const string UNEXPECTED_TOKEN_INPUT = "1 + 2 3";

        /**
         * Tests parsing a valid input string.
         */
        public void TestValidInput() {
            Parse(CreateParser(VALID_INPUT), VALID_OUTPUT);
        }

        /**
         * Tests parsing with an unexpected EOF error.
         */
        public void TestUnexpectedEOF() {
            FailParse(CreateParser(UNEXPECTED_EOF_INPUT),
                      ParseException.ErrorType.UNEXPECTED_EOF,
                      2,
                      1);
        }

        /**
         * Tests parsing with an unexpected character error.
         */
        public void TestUnexpectedChar() {
            FailParse(CreateParser(UNEXPECTED_CHAR_INPUT),
                      ParseException.ErrorType.UNEXPECTED_CHAR,
                      2,
                      2);
        }

        /**
         * Tests parsing with an unexpected token error.
         */
        public void TestUnexpectedToken() {
            FailParse(CreateParser(UNEXPECTED_TOKEN_INPUT),
                      ParseException.ErrorType.UNEXPECTED_TOKEN,
                      1,
                      7);
        }

        /**
         * Tests reusing the same parser for various different inputs.
         */
        public void TestParserReusage() {
            Parser  p;

            p = CreateParser(VALID_INPUT);
            Parse(p, VALID_OUTPUT);
            p.Tokenizer.Reset(new StringReader(UNEXPECTED_CHAR_INPUT));
            FailParse(p, ParseException.ErrorType.UNEXPECTED_CHAR, 2, 2);
            p.Tokenizer.Reset(new StringReader(VALID_INPUT));
            Parse(p, VALID_OUTPUT);
        }

        /**
         * Creates a new parser.
         *
         * @param input          the input to parse
         *
         * @return the parser created
         */
        private Parser CreateParser(string input) {
            Parser  parser = null;

            try {
                parser = new ArithmeticParser(new StringReader(input));
                parser.Prepare();
            } catch (ParserCreationException e) {
                Fail(e.Message);
            }
            return parser;
        }
    }
}
