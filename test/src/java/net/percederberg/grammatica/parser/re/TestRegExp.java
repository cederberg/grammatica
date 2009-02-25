/*
 * TestRegExp.java
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

package net.percederberg.grammatica.parser.re;

import java.io.IOException;

import junit.framework.TestCase;

/**
 * A test case for the RegExp class.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
public class TestRegExp extends TestCase {

    /**
     * The ASCII alphabet characters.
     */
    private static final String ASCII_ALPHABET =
        "ABCDEFGHIJKLMNOPQRSTUNWXYZabcdefghijklmnopqrstuvwxyz";

    /**
     * A set of normal characters from ISO-8859-1 .
     */
    private static final String LATIN_1_CHARACTERS =
        "\u00c1\u00c0\u00c4\u00c2\u00c5\u00c9\u00c8\u00cb\u00ca\u00cd" +
        "\u00cc\u00cf\u00ce\u00d3\u00d2\u00d6\u00d4\u00d5\u00da\u00d9" +
        "\u00dc\u00db\u00dd\u00e1\u00e0\u00e4\u00e2\u00e3\u00e9\u00e8" +
        "\u00eb\u00ea\u00ed\u00ec\u00ef\u00ee\u00f3\u00f2\u00f6\u00f4" +
        "\u00f5\u00fa\u00f9\u00fc\u00fb\u00fd\u00ff";

    /**
     * A set of symbol characters from ISO-8859-1 .
     */
    private static final String LATIN_1_SYMBOLS =
        "\u00a7\u0021\u0023\u00a4\u0025\u0026\u002f\u003d\u0060\u0027" +
        "\u00bd\u0040\u00a3\u007e\u002d\u005f\u002c\u003a\u003b\u00a9" +
        "\u00de\u00ae\u00aa\u00df\u00ab\u00bb\u00b5\u00a1\u00bf\u00b2" +
        "\u00b3\u00bc\u00a2";

    /**
     * A set of digit characters.
     */
    private static final String DIGITS =
        "0123456789";

    /**
     * A set of whitespace characters.
     */
    private static final String WHITESPACE =
        " \t\n\r\f\r\n\u000B";

    /**
     * Creates a new test case.
     *
     * @param name           the test case name
     */
    public TestRegExp(String name) {
        super(name);
    }

    /**
     * Tests various regular expression syntax errors.
     */
    public void testSyntaxErrors() {
        failCreateRegExp("");
        failCreateRegExp("?");
        failCreateRegExp("*");
        failCreateRegExp("+");
        failCreateRegExp("{0}");
        failCreateRegExp("(");
        failCreateRegExp(")");
        failCreateRegExp("[ (])");
        failCreateRegExp("+|*");
    }

    /**
     * Tests matching of plain characters.
     */
    public void testCharacters() {
        matchRegExp(ASCII_ALPHABET, ASCII_ALPHABET);
        matchRegExp(LATIN_1_CHARACTERS, LATIN_1_CHARACTERS);
        matchRegExp(LATIN_1_SYMBOLS, LATIN_1_SYMBOLS);
        matchRegExp(DIGITS, DIGITS);
        matchRegExp(WHITESPACE, WHITESPACE);
    }

    /**
     * Tests matching of special characters.
     */
    public void testSpecialCharacters() {
        matchRegExp(".*", ASCII_ALPHABET);
        matchRegExp(".*", LATIN_1_CHARACTERS);
        matchRegExp(".*", LATIN_1_SYMBOLS);
        matchRegExp(".*", DIGITS);
        matchRegExp(".*", " \t");
        failMatchRegExp(".+", "\n");
        failMatchRegExp(".+", "\r");
        failMatchRegExp(".+", "\r\n");
        failMatchRegExp(".+", "\u0085");
        failMatchRegExp(".+", "\u2028");
        failMatchRegExp(".+", "\u2029");
        failCreateRegExp("^");
        failCreateRegExp("$");
    }

    /**
     * Tests matching of character set escape sequences.
     */
    public void testCharacterEscapes() {
        matchRegExp("\\d+", DIGITS);
        failMatchRegExp("\\d+", ASCII_ALPHABET);
        failMatchRegExp("\\d+", WHITESPACE);
        matchRegExp("\\D+", ASCII_ALPHABET);
        matchRegExp("\\D+", WHITESPACE);
        failMatchRegExp("\\D+", DIGITS);
        matchRegExp("\\s+", WHITESPACE);
        failMatchRegExp("\\s+", ASCII_ALPHABET);
        matchRegExp("\\S+", ASCII_ALPHABET);
        failMatchRegExp("\\S+", WHITESPACE);
        matchRegExp("\\w+", ASCII_ALPHABET);
        matchRegExp("\\w+", DIGITS);
        matchRegExp("\\w+", "_");
        failMatchRegExp("\\w+", WHITESPACE);
        failMatchRegExp("\\w+", LATIN_1_CHARACTERS);
        failMatchRegExp("\\W+", ASCII_ALPHABET);
        failMatchRegExp("\\W+", DIGITS);
        failMatchRegExp("\\W+", "_");
        matchRegExp("\\W+", WHITESPACE);
        matchRegExp("\\W+", LATIN_1_CHARACTERS);
    }

    /**
     * Tests matching of symbol escape sequences.
     */
    public void testSymbolEscapes() {
        matchRegExp("\\\\", "\\");
        matchRegExp("\\\"", "\"");
        matchRegExp("\\'", "'");
        matchRegExp("\\.", ".");
        matchRegExp("\\*", "*");
        matchRegExp("\\+", "+");
        matchRegExp("\\?", "?");
        matchRegExp("\\(", "(");
        matchRegExp("\\)", ")");
        matchRegExp("\\{", "{");
        matchRegExp("\\}", "}");
        matchRegExp("\\[", "[");
        matchRegExp("\\]", "]");
        matchRegExp("\\@", "@");
        matchRegExp("\\<", "<");
        matchRegExp("\\>", ">");
        matchRegExp("\\$", "$");
        matchRegExp("\\%", "%");
        matchRegExp("\\&", "&");
    }

    /**
     * Tests matching of control escape sequences.
     */
    public void testControlEscapes() {
        matchRegExp("\\t", "\t");
        matchRegExp("\\n", "\n");
        matchRegExp("\\r", "\r");
        matchRegExp("\\f", "\f");
        matchRegExp("\\a", "\u0007");
        matchRegExp("\\e", "\u001B");
    }

    /**
     * Tests matching of octal escape sequences.
     */
    public void testOctalEscapes() {
        failCreateRegExp("\\0");
        matchRegExp("\\01", "\01");
        matchRegExp("\\012", "\012");
        matchRegExp("\\0101", "A");
        matchRegExp("\\01174", "O4");
        matchRegExp("\\0117a", "Oa");
        matchRegExp("\\018", "\018");
        matchRegExp("\\0118", "\0118");
        failCreateRegExp("\\08");
        failCreateRegExp("\\043");
        failCreateRegExp("\\0432");
    }

    /**
     * Tests matching of hexadecimal escape sequences.
     */
    public void testHexEscapes() {
        failCreateRegExp("\\x");
        failCreateRegExp("\\x1");
        failCreateRegExp("\\x1g");
        matchRegExp("\\x41", "A");
        matchRegExp("\\x4f", "O");
        matchRegExp("\\xABC", "\u00ABC");
    }

    /**
     * Tests matching of unicode escape sequences.
     */
    public void testUnicodeEscapes() {
        failCreateRegExp("\\u");
        failCreateRegExp("\\u1");
        failCreateRegExp("\\u11");
        failCreateRegExp("\\u111");
        failCreateRegExp("\\u111g");
        matchRegExp("\\u0041", "A");
        matchRegExp("\\u004f", "O");
        matchRegExp("\\u00ABC", "\u00ABC");
    }

    /**
     * Tests matching of invalid escape characters.
     */
    public void testInvalidEscapes() {
        failCreateRegExp("\\A");
        failCreateRegExp("\\B");
        failCreateRegExp("\\C");
        failCreateRegExp("\\E");
        failCreateRegExp("\\F");
        failCreateRegExp("\\G");
        failCreateRegExp("\\H");
        failCreateRegExp("\\I");
        failCreateRegExp("\\J");
        failCreateRegExp("\\K");
        failCreateRegExp("\\L");
        failCreateRegExp("\\M");
        failCreateRegExp("\\N");
        failCreateRegExp("\\O");
        failCreateRegExp("\\P");
        failCreateRegExp("\\Q");
        failCreateRegExp("\\R");
        failCreateRegExp("\\T");
        failCreateRegExp("\\U");
        failCreateRegExp("\\V");
        failCreateRegExp("\\X");
        failCreateRegExp("\\Y");
        failCreateRegExp("\\Z");
        failCreateRegExp("\\b");
        failCreateRegExp("\\c");
        failCreateRegExp("\\g");
        failCreateRegExp("\\h");
        failCreateRegExp("\\i");
        failCreateRegExp("\\j");
        failCreateRegExp("\\k");
        failCreateRegExp("\\l");
        failCreateRegExp("\\m");
        failCreateRegExp("\\o");
        failCreateRegExp("\\p");
        failCreateRegExp("\\q");
        failCreateRegExp("\\u");
        failCreateRegExp("\\v");
        failCreateRegExp("\\y");
        failCreateRegExp("\\z");
    }

    /**
     * Tests matching of character sets.
     */
    public void testCharacterSet() {
        matchRegExp("[ab]", "a");
        matchRegExp("[ab]", "b");
        failMatchRegExp("[ab]", "c");
        failMatchRegExp("[^ab]", "a");
        failMatchRegExp("[^ab]", "b");
        matchRegExp("[^ab]", "c");
        matchRegExp("[A-Za-z]+", ASCII_ALPHABET);
        failMatchRegExp("[A-Za-z]+", DIGITS);
        failMatchRegExp("[A-Za-z]+", WHITESPACE);
        failMatchRegExp("[^A-Za-z]+", ASCII_ALPHABET);
        matchRegExp("[^A-Za-z]+", DIGITS);
        matchRegExp("[^A-Za-z]+", WHITESPACE);
        matchRegExp("[.]", ".");
        failMatchRegExp("[.]", "a");
        matchRegExp("[a-]+", "a-");
        matchRegExp("[-a]+", "a-");
        matchRegExp("[a-]+", "ab", "a");
        matchRegExp("[ \\t\\n\\r\\f\\x0B]*", WHITESPACE);
    }

    /**
     * Tests matching of various greedy quantifiers.
     */
    public void testGreedyQuantifiers() {
        matchRegExp("a?", "");
        matchRegExp("a?", "a");
        matchRegExp("a?", "aaaa", "a");
        matchRegExp("a*", "");
        matchRegExp("a*", "aaaa");
        failMatchRegExp("a+", "");
        matchRegExp("a+", "a");
        matchRegExp("a+", "aaaa");
        failCreateRegExp("a{0}");
        failMatchRegExp("a{3}", "aa");
        matchRegExp("a{3}", "aaa");
        matchRegExp("a{3}", "aaaa", "aaa");
        failMatchRegExp("a{3,}", "aa");
        matchRegExp("a{3,}", "aaa");
        matchRegExp("a{3,}", "aaaaa");
        failMatchRegExp("a{2,3}", "a");
        matchRegExp("a{2,3}", "aa");
        matchRegExp("a{2,3}", "aaa");
        matchRegExp("a{2,3}", "aaaa", "aaa");
    }

    /**
     * Tests matching of various reluctant quantifiers.
     */
    public void testReluctantQuantifiers() {
        matchRegExp("a??", "");
        matchRegExp("a??", "a", "");
        matchRegExp("a*?", "");
        matchRegExp("a*?", "aaaa", "");
        failMatchRegExp("a+?", "");
        matchRegExp("a+?", "a");
        matchRegExp("a+?", "aaaa", "a");
        failMatchRegExp("a{3}?", "aa");
        failCreateRegExp("a{0}?");
        matchRegExp("a{3}?", "aaa");
        matchRegExp("a{3}?", "aaaa", "aaa");
        failMatchRegExp("a{3,}?", "aa");
        matchRegExp("a{3,}?", "aaa");
        matchRegExp("a{3,}?", "aaaaa", "aaa");
        failMatchRegExp("a{2,3}?", "a");
        matchRegExp("a{2,3}?", "aa");
        matchRegExp("a{2,3}?", "aaa", "aa");
        matchRegExp("a{2,3}?", "aaaa", "aa");
    }

    /**
     * Tests matching of various possessive quantifiers.
     */
    public void testPossessiveQuantifiers() {
        matchRegExp("a?+", "");
        matchRegExp("a?+", "a");
        matchRegExp("a*+", "");
        matchRegExp("a*+", "aaaa");
        failMatchRegExp("a++", "");
        matchRegExp("a++", "a");
        matchRegExp("a++", "aaaa");
        failMatchRegExp("a{3}+", "aa");
        failCreateRegExp("a{0}+");
        matchRegExp("a{3}+", "aaa");
        matchRegExp("a{3}+", "aaaa", "aaa");
        failMatchRegExp("a{3,}+", "aa");
        matchRegExp("a{3,}+", "aaa");
        matchRegExp("a{3,}+", "aaaaa", "aaaaa");
        failMatchRegExp("a{2,3}+", "a");
        matchRegExp("a{2,3}+", "aa");
        matchRegExp("a{2,3}+", "aaa");
        matchRegExp("a{2,3}+", "aaaa", "aaa");
    }

    /**
     * Tests the backtracking over the quantifier matches.
     */
    public void testQuantifierBacktracking() {
        matchRegExp("a?a", "a");
        matchRegExp("a*a", "aaaa");
        matchRegExp("a*aaaa", "aaaa");
        failMatchRegExp("a*aaaa", "aaa");
        matchRegExp("a+a", "aaaa");
        matchRegExp("a+aaa", "aaaa");
        failMatchRegExp("a+aaaa", "aaaa");
        failMatchRegExp("a{3,}a", "aaa");
        matchRegExp("a{3,}a", "aaaaa");
        matchRegExp("a{2,3}a", "aaa");
        failMatchRegExp("a{2,3}a", "aa");
        matchRegExp("a??b", "ab");
        matchRegExp("a*?b", "aaab");
        matchRegExp("a+?b", "aaab");
        matchRegExp("a{3,}?b", "aaaaab");
        matchRegExp("a{2,3}?b", "aaab");
        failMatchRegExp("a?+a", "a");
        failMatchRegExp("a*+a", "aaaa");
        failMatchRegExp("a++a", "aaaa");
        failMatchRegExp("a{3,}+a", "aaaaa");
        failMatchRegExp("a{2,3}+a", "aaa");
    }

    /**
     * Tests the quantifier backtracking for stack overflows.
     * (Bug #3632)
     */
    public void testQuantifierStackOverflow() {
        StringBuffer  buffer = new StringBuffer();
        String        str;

        for (int i = 0; i < 4096; i++) {
            buffer.append("a");
        }
        str = buffer.toString();
        matchRegExp("a*" + str, str);
        failMatchRegExp("a*a" + str, str);
        matchRegExp("a*?b", str + "b");
        failMatchRegExp("a*?b", str);
        matchRegExp("a*+", str);
        failMatchRegExp("a*+a", str);
    }

    /**
     * Tests matching of various logical operators.
     */
    public void testLogicalOperators() {
        matchRegExp("a|ab|b", "a");
        matchRegExp("a|ab|b", "b");
        matchRegExp("a|ab|b", "ab");
        matchRegExp("(ab)", "ab");
        matchRegExp("(a)(b)", "ab");
    }

    /**
     * Tests the regular expression operator associativity.
     */
    public void testAssociativity() {
        matchRegExp("ab?c", "ac");
        failMatchRegExp("ab?c", "c");
        matchRegExp("aa|b", "aa");
        failMatchRegExp("aa|b", "ab");
        matchRegExp("ab|bc", "ab");
        matchRegExp("ab|bc", "bc");
        matchRegExp("(a|b)c", "ac");
        matchRegExp("(a|b)c", "bc");
        failMatchRegExp("(a|b)c", "abc");
    }

    /**
     * Tests matching of various complex expressions.
     */
    public void testComplex() {
        matchRegExp("a*-", "aa-");
        matchRegExp("([) ])+", ") ))");
        matchRegExp("a*a*aa", "aa");
        matchRegExp("(a*)*aa", "aaaa");
        matchRegExp("a+a+aa", "aaaa");
        matchRegExp("(a+)+aa", "aaaa");
    }

    /**
     * Tests resetting the matcher with another input string.
     */
    public void testReset() {
        Matcher  m;

        try {
            m = createRegExp("a*aa").matcher("a");
            if (m.matchFromBeginning()) {
                fail("found invalid match '" + m.toString() +
                     "' to regexp 'a*aa' in input 'a'");
            }
            m.reset("aaaa");
            if (!m.matchFromBeginning()) {
                fail("couldn't match 'aaaa' to regexp 'a*aa'");
            } else if (!m.toString().equals("aaaa")) {
                fail("incorrect match for 'a*aa', found: '" +
                     m.toString() + "', expected: 'aaaa'");
            }
            m = createRegExp("a*?b").matcher("aaa");
            if (m.matchFromBeginning()) {
                fail("found invalid match '" + m.toString() +
                     "' to regexp 'a*?b' in input 'aaa'");
            }
            m.reset("aaaaab");
            if (!m.matchFromBeginning()) {
                fail("couldn't match 'aaaaab' to regexp 'a*?b'");
            } else if (!m.toString().equals("aaaaab")) {
                fail("incorrect match for 'a*?b', found: '" +
                     m.toString() + "', expected: 'aaaaab'");
            }
        } catch (IOException e) {
            fail("io error: " + e.getMessage());
        }
    }

    /**
     * Creates a new regular expression. If the expression couldn't be
     * parsed correctly, a test failure will be reported.
     *
     * @param pattern        the pattern to use
     *
     * @return the newly created regular expression
     */
    private RegExp createRegExp(String pattern) {
        try {
            return new RegExp(pattern, false);
        } catch (RegExpException e) {
            fail("couldn't create regular expression '" + pattern +
                 "': " + e.getMessage());
            return null;
        }
    }

    /**
     * Checks that a specified regular expression pattern is
     * erroneous. If the regular expression class doesn't detect the
     * error, a test failure will be reported.
     *
     * @param pattern        the pattern to check
     */
    private void failCreateRegExp(String pattern) {
        try {
            new RegExp(pattern, false);
            fail("regular expression '" + pattern + "' could be " +
                 "created although it isn't valid");
        } catch (RegExpException e) {
            // Failure was expected
        }
    }

    /**
     * Checks that a specified regular expression matches an input
     * string. The whole input string must be matched by the regular
     * expression. This method will report a failure if the regular
     * expression couldn't be created or if the match wasn't exact.
     *
     * @param pattern        the regular expression to check
     * @param input          the input and match string
     */
    private void matchRegExp(String pattern, String input) {
        matchRegExp(pattern, input, input);
    }

    /**
     * Checks that a specified regular expression matches an input
     * string. The exact match is compared to a specified match. This
     * method will report a failure if the regular expression couldn't
     * be created or if the match wasn't exact.
     *
     * @param pattern        the regular expression to check
     * @param input          the input string
     * @param match          the match string
     */
    private void matchRegExp(String pattern, String input, String match) {
        RegExp   r = createRegExp(pattern);
        Matcher  m = r.matcher(input);

        try {
            if (!m.matchFromBeginning()) {
                fail("couldn't match '" + input + "' to regexp '" +
                     pattern + "'");
            } else if (!match.equals(m.toString())) {
                fail("incorrect match for '" + pattern + "', found: '" +
                     m.toString() + "', expected: '" + match + "'");
            }
        } catch (IOException e) {
            fail("io error: " + e.getMessage());
        }
    }

    /**
     * Checks that a specified regular expression does not match the
     * input string. This method will report a failure if the regular
     * expression couldn't be created or if a match was found.
     *
     * @param pattern        the regular expression to check
     * @param input          the input and match string
     */
    private void failMatchRegExp(String pattern, String input) {
        RegExp   r = createRegExp(pattern);
        Matcher  m = r.matcher(input);

        try {
            if (m.matchFromBeginning()) {
                fail("found invalid match '" + m.toString() +
                     "' to regexp '" + pattern + "' in input '" +
                     input + "'");
            }
        } catch (IOException e) {
            fail("io error: " + e.getMessage());
        }
    }
}
