/*
 * TestRegExp.cs
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

using System;
using System.Text;

using PerCederberg.Grammatica.Parser.RE;

/**
 * A test case for the RegExp class.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
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
        "¡¿ƒ¬≈…»À ÕÃœŒ”“÷‘’⁄Ÿ‹€›·‡‰‚„ÈËÎÍÌÏÔÓÛÚˆÙı˙˘¸˚˝ˇ"; 
    
    /**
     * A set of symbol characters from ISO-8859-1 .
     */
    private const string LATIN_1_SYMBOLS = 
        "ß!#§%&/=`'Ω@£~-_,:;©ﬁÆ™ﬂ´ªµ°ø≤≥º¢";

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
        MatchRegExp(LATIN_1_CHARACTERS, LATIN_1_CHARACTERS);
        MatchRegExp(LATIN_1_SYMBOLS, LATIN_1_SYMBOLS);
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
     * Creates a new regular expression. If the expression couldn't be
     * parsed correctly, a test failure will be reported.
     * 
     * @param pattern        the pattern to use
     * 
     * @return the newly created regular expression
     */
    private RegExp CreateRegExp(string pattern) {
        try {
            return new RegExp(pattern);
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
            new RegExp(pattern);
            Fail("regular expression '" + pattern + "' could be " +
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

        if (!m.MatchFromBeginning()) {
            Fail("couldn't match '" + input + "' to regexp '" + 
                 pattern + "'");
        } else if (!match.Equals(m.ToString())) {
            Fail("incorrect match for '" + pattern + "', found: '" + 
                 m.ToString() + "', expected: '" + match + "'");
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
        
        if (m.MatchFromBeginning()) {
            Fail("found invalid match '" + m.ToString() + 
                 "' to regexp '" + pattern + "' in input '" + 
                 input + "'");
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
