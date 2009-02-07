/*
 * TestArithmeticParser.java
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

package net.percederberg.grammatica.test;

import java.io.StringReader;

import net.percederberg.grammatica.parser.ParseException;
import net.percederberg.grammatica.parser.Parser;
import net.percederberg.grammatica.parser.ParserCreationException;

/**
 * A test case for the generated ArithmeticParser class.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
public class TestArithmeticParser extends ParserTestCase {

    /**
     * The valid input string.
     */
    private static final String VALID_INPUT =
        "1 + 2*a\n" +
        " + 345";

    /**
     * The parse tree for the valid input string.
     */
    private static final String VALID_OUTPUT =
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
    private static final String UNEXPECTED_EOF_INPUT = "1 *\t \n";

    /**
     * The unexpected character input string.
     */
    private static final String UNEXPECTED_CHAR_INPUT = "1\n # 4";

    /**
     * The unexpected token input string.
     */
    private static final String UNEXPECTED_TOKEN_INPUT = "1 + 2 3";

    /**
     * Creates a new test case.
     *
     * @param name           the test case name
     */
    public TestArithmeticParser(String name) {
        super(name);
    }

    /**
     * Tests parsing a valid input string.
     */
    public void testValidInput() {
        parse(createParser(VALID_INPUT), VALID_OUTPUT);
    }

    /**
     * Tests parsing with an unexpected EOF error.
     */
    public void testUnexpectedEOF() {
        failParse(createParser(UNEXPECTED_EOF_INPUT),
                  ParseException.UNEXPECTED_EOF_ERROR,
                  2,
                  1);
    }

    /**
     * Tests parsing with an unexpected character error.
     */
    public void testUnexpectedChar() {
        failParse(createParser(UNEXPECTED_CHAR_INPUT),
                  ParseException.UNEXPECTED_CHAR_ERROR,
                  2,
                  2);
    }

    /**
     * Tests parsing with an unexpected token error.
     */
    public void testUnexpectedToken() {
        failParse(createParser(UNEXPECTED_TOKEN_INPUT),
                  ParseException.UNEXPECTED_TOKEN_ERROR,
                  1,
                  7);
    }

    /**
     * Tests reusing the same parser for various different inputs.
     */
    public void testParserReusage() {
        Parser  p;

        p = createParser(VALID_INPUT);
        parse(p, VALID_OUTPUT);
        p.getTokenizer().reset(new StringReader(UNEXPECTED_CHAR_INPUT));
        failParse(p, ParseException.UNEXPECTED_CHAR_ERROR, 2, 2);
        p.getTokenizer().reset(new StringReader(VALID_INPUT));
        parse(p, VALID_OUTPUT);
    }

    /**
     * Creates a new parser.
     *
     * @param input          the input to parse
     *
     * @return the parser created
     */
    private Parser createParser(String input) {
        Parser  parser = null;

        try {
            parser = new ArithmeticParser(new StringReader(input));
            parser.prepare();
        } catch (ParserCreationException e) {
            fail(e.getMessage());
        }
        return parser;
    }
}
