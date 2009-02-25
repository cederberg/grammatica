/*
 * TestRegExp.cs
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
using System.Text;

using PerCederberg.Grammatica.Runtime.RE;

/**
 * A test case for the RegExp class.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
public class TestRegExp {

    /**
     * The ASCII alphabet characters.
     */
    private const string ASCII_ALPHABET =
        "ABCDEFGHIJKLMNOPQRSTUNWXYZabcdefghijklmnopqrstuvwxyz";

    /**
     * A set of normal characters from ISO-8859-1 .
     */
    private const string LATIN_1_CHARACTERS =
        "\u00c1\u00c0\u00c4\u00c2\u00c5\u00c9\u00c8\u00cb\u00ca\u00cd" +
        "\u00cc\u00cf\u00ce\u00d3\u00d2\u00d6\u00d4\u00d5\u00da\u00d9" +
        "\u00dc\u00db\u00dd\u00e1\u00e0\u00e4\u00e2\u00e3\u00e9\u00e8" +
        "\u00eb\u00ea\u00ed\u00ec\u00ef\u00ee\u00f3\u00f2\u00f6\u00f4" +
        "\u00f5\u00fa\u00f9\u00fc\u00fb\u00fd\u00ff";

    /**
     * A set of symbol characters from ISO-8859-1 .
     */
    private const string LATIN_1_SYMBOLS =
        "\u00a7\u0021\u0023\u00a4\u0025\u0026\u002f\u003d\u0060\u0027" +
        "\u00bd\u0040\u00a3\u007e\u002d\u005f\u002c\u003a\u003b\u00a9" +
        "\u00de\u00ae\u00aa\u00df\u00ab\u00bb\u00b5\u00a1\u00bf\u00b2" +
        "\u00b3\u00bc\u00a2";

    /**
     * A set of digit characters.
     */
    private const string DIGITS =
        "0123456789";

    /**
     * A set of whitespace characters.
     */
    private const string WHITESPACE =
        " \t\n\r\f\r\n\u000B";

    /**
     * Creates a new test case.
     */
    public TestRegExp() {
    }

    /**
     * Tests various regular expression syntax errors.
     */
    public void TestSyntaxErrors() {
        FailCreateRegExp("");
        FailCreateRegExp("?");
        FailCreateRegExp("*");
        FailCreateRegExp("+");
        FailCreateRegExp("{0}");
        FailCreateRegExp("(");
        FailCreateRegExp(")");
        FailCreateRegExp("[ (])");
    }

    /**
     * Tests matching of plain characters.
     */
    public void TestCharacters() {
        MatchRegExp(ASCII_ALPHABET, ASCII_ALPHABET);
        //MatchRegExp(LATIN_1_CHARACTERS, LATIN_1_CHARACTERS);
        //MatchRegExp(LATIN_1_SYMBOLS, LATIN_1_SYMBOLS);
        MatchRegExp(DIGITS, DIGITS);
        MatchRegExp(WHITESPACE, WHITESPACE);
    }

    /**
     * Tests matching of special characters.
     */
    public void TestSpecialCharacters() {
        MatchRegExp(".*", ASCII_ALPHABET);
        MatchRegExp(".*", LATIN_1_CHARACTERS);
        MatchRegExp(".*", LATIN_1_SYMBOLS);
        MatchRegExp(".*", DIGITS);
        MatchRegExp(".*", " \t");
        FailMatchRegExp(".+", "\n");
        FailMatchRegExp(".+", "\r");
        FailMatchRegExp(".+", "\r\n");
        FailMatchRegExp(".+", "\u0085");
        FailMatchRegExp(".+", "\u2028");
        FailMatchRegExp(".+", "\u2029");
        FailCreateRegExp("^");
        FailCreateRegExp("$");
    }

    /**
     * Tests matching of character set escape sequences.
     */
    public void TestCharacterEscapes() {
        MatchRegExp("\\d+", DIGITS);
        FailMatchRegExp("\\d+", ASCII_ALPHABET);
        FailMatchRegExp("\\d+", WHITESPACE);
        MatchRegExp("\\D+", ASCII_ALPHABET);
        MatchRegExp("\\D+", WHITESPACE);
        FailMatchRegExp("\\D+", DIGITS);
        MatchRegExp("\\s+", WHITESPACE);
        FailMatchRegExp("\\s+", ASCII_ALPHABET);
        MatchRegExp("\\S+", ASCII_ALPHABET);
        FailMatchRegExp("\\S+", WHITESPACE);
        MatchRegExp("\\w+", ASCII_ALPHABET);
        MatchRegExp("\\w+", DIGITS);
        MatchRegExp("\\w+", "_");
        FailMatchRegExp("\\w+", WHITESPACE);
        FailMatchRegExp("\\w+", LATIN_1_CHARACTERS);
        FailMatchRegExp("\\W+", ASCII_ALPHABET);
        FailMatchRegExp("\\W+", DIGITS);
        FailMatchRegExp("\\W+", "_");
        MatchRegExp("\\W+", WHITESPACE);
        MatchRegExp("\\W+", LATIN_1_CHARACTERS);
    }

    /**
     * Tests matching of symbol escape sequences.
     */
    public void TestSymbolEscapes() {
        MatchRegExp("\\\\", "\\");
        MatchRegExp("\\\"", "\"");
        MatchRegExp("\\'", "'");
        MatchRegExp("\\.", ".");
        MatchRegExp("\\*", "*");
        MatchRegExp("\\+", "+");
        MatchRegExp("\\?", "?");
        MatchRegExp("\\(", "(");
        MatchRegExp("\\)", ")");
        MatchRegExp("\\{", "{");
        MatchRegExp("\\}", "}");
        MatchRegExp("\\[", "[");
        MatchRegExp("\\]", "]");
        MatchRegExp("\\@", "@");
        MatchRegExp("\\<", "<");
        MatchRegExp("\\>", ">");
        MatchRegExp("\\$", "$");
        MatchRegExp("\\%", "%");
        MatchRegExp("\\&", "&");
    }

    /**
     * Tests matching of control escape sequences.
     */
    public void TestControlEscapes() {
        MatchRegExp("\\t", "\t");
        MatchRegExp("\\n", "\n");
        MatchRegExp("\\r", "\r");
        MatchRegExp("\\f", "\f");
        MatchRegExp("\\a", "\u0007");
        MatchRegExp("\\e", "\u001B");
    }

    /**
     * Tests matching of octal escape sequences.
     */
    public void TestOctalEscapes() {
        FailCreateRegExp("\\0");
        MatchRegExp("\\01", "\x0001");
        MatchRegExp("\\012", "\x000A");
        MatchRegExp("\\0101", "A");
        MatchRegExp("\\01174", "O4");
        MatchRegExp("\\0117a", "Oa");
        MatchRegExp("\\018", "\x00018");
        MatchRegExp("\\0118", "\x00098");
        FailCreateRegExp("\\08");
        FailCreateRegExp("\\043");
        FailCreateRegExp("\\0432");
    }

    /**
     * Tests matching of hexadecimal escape sequences.
     */
    public void TestHexEscapes() {
        FailCreateRegExp("\\x");
        FailCreateRegExp("\\x1");
        FailCreateRegExp("\\x1g");
        MatchRegExp("\\x41", "A");
        MatchRegExp("\\x4f", "O");
        MatchRegExp("\\xABC", "\x00ABC");
    }

    /**
     * Tests matching of unicode escape sequences.
     */
    public void TestUnicodeEscapes() {
        FailCreateRegExp("\\u");
        FailCreateRegExp("\\u1");
        FailCreateRegExp("\\u11");
        FailCreateRegExp("\\u111");
        FailCreateRegExp("\\u111g");
        MatchRegExp("\\u0041", "A");
        MatchRegExp("\\u004f", "O");
        MatchRegExp("\\u00ABC", "\x00ABC");
    }

    /**
     * Tests matching of invalid escape characters.
     */
    public void TestInvalidEscapes() {
        FailCreateRegExp("\\A");
        FailCreateRegExp("\\B");
        FailCreateRegExp("\\C");
        FailCreateRegExp("\\E");
        FailCreateRegExp("\\F");
        FailCreateRegExp("\\G");
        FailCreateRegExp("\\H");
        FailCreateRegExp("\\I");
        FailCreateRegExp("\\J");
        FailCreateRegExp("\\K");
        FailCreateRegExp("\\L");
        FailCreateRegExp("\\M");
        FailCreateRegExp("\\N");
        FailCreateRegExp("\\O");
        FailCreateRegExp("\\P");
        FailCreateRegExp("\\Q");
        FailCreateRegExp("\\R");
        FailCreateRegExp("\\T");
        FailCreateRegExp("\\U");
        FailCreateRegExp("\\V");
        FailCreateRegExp("\\X");
        FailCreateRegExp("\\Y");
        FailCreateRegExp("\\Z");
        FailCreateRegExp("\\b");
        FailCreateRegExp("\\c");
        FailCreateRegExp("\\g");
        FailCreateRegExp("\\h");
        FailCreateRegExp("\\i");
        FailCreateRegExp("\\j");
        FailCreateRegExp("\\k");
        FailCreateRegExp("\\l");
        FailCreateRegExp("\\m");
        FailCreateRegExp("\\o");
        FailCreateRegExp("\\p");
        FailCreateRegExp("\\q");
        FailCreateRegExp("\\u");
        FailCreateRegExp("\\v");
        FailCreateRegExp("\\y");
        FailCreateRegExp("\\z");
    }

    /**
     * Tests matching of character sets.
     */
    public void TestCharacterSet() {
        MatchRegExp("[ab]", "a");
        MatchRegExp("[ab]", "b");
        FailMatchRegExp("[ab]", "c");
        FailMatchRegExp("[^ab]", "a");
        FailMatchRegExp("[^ab]", "b");
        MatchRegExp("[^ab]", "c");
        MatchRegExp("[A-Za-z]+", ASCII_ALPHABET);
        FailMatchRegExp("[A-Za-z]+", DIGITS);
        FailMatchRegExp("[A-Za-z]+", WHITESPACE);
        FailMatchRegExp("[^A-Za-z]+", ASCII_ALPHABET);
        MatchRegExp("[^A-Za-z]+", DIGITS);
        MatchRegExp("[^A-Za-z]+", WHITESPACE);
        MatchRegExp("[.]", ".");
        FailMatchRegExp("[.]", "a");
        MatchRegExp("[a-]+", "a-");
        MatchRegExp("[-a]+", "a-");
        MatchRegExp("[a-]+", "ab", "a");
        MatchRegExp("[ \\t\\n\\r\\f\\x0B]*", WHITESPACE);
    }

    /**
     * Tests matching of various greedy quantifiers.
     */
    public void TestGreedyQuantifiers() {
        MatchRegExp("a?", "");
        MatchRegExp("a?", "a");
        MatchRegExp("a?", "aaaa", "a");
        MatchRegExp("a*", "");
        MatchRegExp("a*", "aaaa");
        FailMatchRegExp("a+", "");
        MatchRegExp("a+", "a");
        MatchRegExp("a+", "aaaa");
        FailCreateRegExp("a{0}");
        FailMatchRegExp("a{3}", "aa");
        MatchRegExp("a{3}", "aaa");
        MatchRegExp("a{3}", "aaaa", "aaa");
        FailMatchRegExp("a{3,}", "aa");
        MatchRegExp("a{3,}", "aaa");
        MatchRegExp("a{3,}", "aaaaa");
        FailMatchRegExp("a{2,3}", "a");
        MatchRegExp("a{2,3}", "aa");
        MatchRegExp("a{2,3}", "aaa");
        MatchRegExp("a{2,3}", "aaaa", "aaa");
    }

    /**
     * Tests matching of various reluctant quantifiers.
     */
    public void TestReluctantQuantifiers() {
        MatchRegExp("a??", "");
        MatchRegExp("a??", "a", "");
        MatchRegExp("a*?", "");
        MatchRegExp("a*?", "aaaa", "");
        FailMatchRegExp("a+?", "");
        MatchRegExp("a+?", "a");
        MatchRegExp("a+?", "aaaa", "a");
        FailMatchRegExp("a{3}?", "aa");
        FailCreateRegExp("a{0}?");
        MatchRegExp("a{3}?", "aaa");
        MatchRegExp("a{3}?", "aaaa", "aaa");
        FailMatchRegExp("a{3,}?", "aa");
        MatchRegExp("a{3,}?", "aaa");
        MatchRegExp("a{3,}?", "aaaaa", "aaa");
        FailMatchRegExp("a{2,3}?", "a");
        MatchRegExp("a{2,3}?", "aa");
        MatchRegExp("a{2,3}?", "aaa", "aa");
        MatchRegExp("a{2,3}?", "aaaa", "aa");
    }

    /**
     * Tests matching of various possessive quantifiers.
     */
    public void TestPossessiveQuantifiers() {
        MatchRegExp("a?+", "");
        MatchRegExp("a?+", "a");
        MatchRegExp("a*+", "");
        MatchRegExp("a*+", "aaaa");
        FailMatchRegExp("a++", "");
        MatchRegExp("a++", "a");
        MatchRegExp("a++", "aaaa");
        FailMatchRegExp("a{3}+", "aa");
        FailCreateRegExp("a{0}+");
        MatchRegExp("a{3}+", "aaa");
        MatchRegExp("a{3}+", "aaaa", "aaa");
        FailMatchRegExp("a{3,}+", "aa");
        MatchRegExp("a{3,}+", "aaa");
        MatchRegExp("a{3,}+", "aaaaa", "aaaaa");
        FailMatchRegExp("a{2,3}+", "a");
        MatchRegExp("a{2,3}+", "aa");
        MatchRegExp("a{2,3}+", "aaa");
        MatchRegExp("a{2,3}+", "aaaa", "aaa");
    }

    /**
     * Tests the backtracking over the quantifier matches.
     */
    public void TestQuantifierBacktracking() {
        MatchRegExp("a?a", "a");
        MatchRegExp("a*a", "aaaa");
        MatchRegExp("a*aaaa", "aaaa");
        FailMatchRegExp("a*aaaa", "aaa");
        MatchRegExp("a+a", "aaaa");
        MatchRegExp("a+aaa", "aaaa");
        FailMatchRegExp("a+aaaa", "aaaa");
        FailMatchRegExp("a{3,}a", "aaa");
        MatchRegExp("a{3,}a", "aaaaa");
        MatchRegExp("a{2,3}a", "aaa");
        FailMatchRegExp("a{2,3}a", "aa");
        MatchRegExp("a??b", "ab");
        MatchRegExp("a*?b", "aaab");
        MatchRegExp("a+?b", "aaab");
        MatchRegExp("a{3,}?b", "aaaaab");
        MatchRegExp("a{2,3}?b", "aaab");
        FailMatchRegExp("a?+a", "a");
        FailMatchRegExp("a*+a", "aaaa");
        FailMatchRegExp("a++a", "aaaa");
        FailMatchRegExp("a{3,}+a", "aaaaa");
        FailMatchRegExp("a{2,3}+a", "aaa");
    }

    /**
     * Tests the quantifier backtracking for stack overflows.
     * (Bug #3632)
     */
    public void TestQuantifierStackOverflow() {
        StringBuilder  buffer = new StringBuilder();
        String         str;

        for (int i = 0; i < 4096; i++) {
            buffer.Append("a");
        }
        str = buffer.ToString();
        MatchRegExp("a*" + str, str);
        FailMatchRegExp("a*a" + str, str);
        MatchRegExp("a*?b", str + "b");
        FailMatchRegExp("a*?b", str);
        MatchRegExp("a*+", str);
        FailMatchRegExp("a*+a", str);
    }

    /**
     * Tests matching of various logical operators.
     */
    public void TestLogicalOperators() {
        MatchRegExp("a|ab|b", "a");
        MatchRegExp("a|ab|b", "b");
        MatchRegExp("a|ab|b", "ab");
        MatchRegExp("(ab)", "ab");
        MatchRegExp("(a)(b)", "ab");
    }

    /**
     * Tests the regular expression operator associativity.
     */
    public void TestAssociativity() {
        MatchRegExp("ab?c", "ac");
        FailMatchRegExp("ab?c", "c");
        MatchRegExp("aa|b", "aa");
        FailMatchRegExp("aa|b", "ab");
        MatchRegExp("ab|bc", "ab");
        MatchRegExp("ab|bc", "bc");
        MatchRegExp("(a|b)c", "ac");
        MatchRegExp("(a|b)c", "bc");
        FailMatchRegExp("(a|b)c", "abc");
    }

    /**
     * Tests matching of various complex expressions.
     */
    public void TestComplex() {
        MatchRegExp("a*-", "aa-");
        MatchRegExp("([) ])+", ") ))");
        MatchRegExp("a*a*aa", "aa");
        MatchRegExp("(a*)*aa", "aaaa");
        MatchRegExp("a+a+aa", "aaaa");
        MatchRegExp("(a+)+aa", "aaaa");
    }

    /**
     * Tests resetting the matcher with another input string.
     */
    public void TestReset() {
        Matcher  m;

        try {
            m = CreateRegExp("a*aa").Matcher("a");
            if (m.MatchFromBeginning()) {
                Fail("found invalid match '" + m.ToString() +
                     "' to regexp 'a*aa' in input 'a'");
            }
            m.Reset("aaaa");
            if (!m.MatchFromBeginning()) {
                Fail("couldn't match 'aaaa' to regexp 'a*aa'");
            } else if (!m.ToString().Equals("aaaa")) {
                Fail("incorrect match for 'a*aa', found: '" +
                     m.ToString() + "', expected: 'aaaa'");
            }
            m = CreateRegExp("a*?b").Matcher("aaa");
            if (m.MatchFromBeginning()) {
                Fail("found invalid match '" + m.ToString() +
                     "' to regexp 'a*?b' in input 'aaa'");
            }
            m.Reset("aaaaab");
            if (!m.MatchFromBeginning()) {
                Fail("couldn't match 'aaaaab' to regexp 'a*?b'");
            } else if (!m.ToString().Equals("aaaaab")) {
                Fail("incorrect match for 'a*?b', found: '" +
                     m.ToString() + "', expected: 'aaaaab'");
            }
        } catch (IOException e) {
            Fail("io error: " + e.Message);
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
    private RegExp CreateRegExp(string pattern) {
        try {
            return new RegExp(pattern, false);
        } catch (RegExpException e) {
            Fail("couldn't create regular expression '" + pattern +
                 "': " + e.Message);
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
    private void FailCreateRegExp(string pattern) {
        try {
            new RegExp(pattern, false);
            Fail("regular expression '" + pattern + "' could be " +
                 "created although it isn't valid");
        } catch (RegExpException) {
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
    private void MatchRegExp(string pattern, string input) {
        MatchRegExp(pattern, input, input);
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
    private void MatchRegExp(string pattern, string input, string match) {
        RegExp   r = CreateRegExp(pattern);
        Matcher  m = r.Matcher(input);

        try {
            if (!m.MatchFromBeginning()) {
                Fail("couldn't match '" + input + "' to regexp '" +
                     pattern + "'");
            } else if (!match.Equals(m.ToString())) {
                Fail("incorrect match for '" + pattern + "', found: '" +
                     m.ToString() + "', expected: '" + match + "'");
            }
        } catch (IOException e) {
            Fail("io error: " + e.Message);
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
    private void FailMatchRegExp(string pattern, string input) {
        RegExp   r = CreateRegExp(pattern);
        Matcher  m = r.Matcher(input);

        try {
            if (m.MatchFromBeginning()) {
                Fail("found invalid match '" + m.ToString() +
                     "' to regexp '" + pattern + "' in input '" +
                     input + "'");
            }
        } catch (IOException e) {
            Fail("io error: " + e.Message);
        }
    }

    /**
     * Throws a test fail exception.
     *
     * @param message         the test error message
     */
    private void Fail(string message) {
        throw new Exception(message);
    }
}
