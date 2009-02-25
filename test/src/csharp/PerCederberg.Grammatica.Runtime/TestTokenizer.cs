/*
 * TestTokenizer.cs
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

/**
 * A test case for the Tokenizer class.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
public class TestTokenizer {

    /**
     * The end of file token identifier.
     */
    private const int EOF = 0;

    /**
     * The keyword token identifier.
     */
    private const int KEYWORD = 1;

    /**
     * The identifier token identifier.
     */
    private const int IDENTIFIER = 2;

    /**
     * The number token identifier.
     */
    private const int NUMBER = 3;

    /**
     * The whitespace token identifier.
     */
    private const int WHITESPACE = 4;

    /**
     * The error token identifier.
     */
    private const int ERROR = 5;

    /**
     * Test various invalid patterns.
     */
    public void TestInvalidPattern() {
        Tokenizer     tokenizer = CreateTokenizer("", false);
        TokenPattern  pattern;

        pattern = new TokenPattern(NUMBER,
                                   "NUMBER",
                                   TokenPattern.PatternType.REGEXP + 13,
                                   "13");
        FailAddPattern(tokenizer, pattern);
        pattern = new TokenPattern(NUMBER,
                                   "NUMBER",
                                   TokenPattern.PatternType.REGEXP,
                                   "1(3");
        FailAddPattern(tokenizer, pattern);
    }

    /**
     * Tests the tokenizer with empty input.
     */
    public void TestEmptyInput() {
        Tokenizer  tokenizer = CreateDefaultTokenizer("", false);

        ReadToken(tokenizer, EOF);
    }

    /**
     * Tests the ignored tokens.
     */
    public void TestIgnoreTokens() {
        Tokenizer  tokenizer = CreateDefaultTokenizer(" 12 keyword 0 ", false);

        ReadToken(tokenizer, NUMBER);
        ReadToken(tokenizer, KEYWORD);
        ReadToken(tokenizer, NUMBER);
        ReadToken(tokenizer, EOF);
    }

    /**
     * Tests the ignored tokens.
     */
    public void TestErrorTokens() {
        Tokenizer  tokenizer = CreateDefaultTokenizer("12 error1  ", false);

        ReadToken(tokenizer, NUMBER);
        FailReadToken(tokenizer);
        ReadToken(tokenizer, NUMBER);
        ReadToken(tokenizer, EOF);
    }

    /**
     * Test the parse error recovery.
     */
    public void TestParseError() {
        Tokenizer  tokenizer = CreateDefaultTokenizer("12 (keyword)", false);

        ReadToken(tokenizer, NUMBER);
        FailReadToken(tokenizer);
        ReadToken(tokenizer, KEYWORD);
        FailReadToken(tokenizer);
        ReadToken(tokenizer, EOF);
    }

    /**
     * Tests the token list functions.
     */
    public void TestTokenList() {
        Tokenizer  tokenizer = CreateDefaultTokenizer("12 keyword 0", false);
        Token      token;

        AssertEquals("default token list setting",
                     false,
                     tokenizer.UseTokenList);
        tokenizer.UseTokenList = true;
        token = ReadToken(tokenizer, NUMBER);
        ReadToken(tokenizer, KEYWORD);
        ReadToken(tokenizer, NUMBER);
        ReadToken(tokenizer, EOF);
        AssertEquals("previous token", null, token.Previous);
        token = token.Next;
        AssertEquals("token id", WHITESPACE, token.Id);
        token = token.Next;
        AssertEquals("token id", KEYWORD, token.Id);
        token = token.Next;
        AssertEquals("token id", WHITESPACE, token.Id);
        token = token.Next;
        AssertEquals("token id", NUMBER, token.Id);
        AssertEquals("next token", null, token.Next);
        token = token.Previous;
        AssertEquals("token id", WHITESPACE, token.Id);
        token = token.Previous;
        AssertEquals("token id", KEYWORD, token.Id);
        token = token.Previous;
        AssertEquals("token id", WHITESPACE, token.Id);
        token = token.Previous;
        AssertEquals("token id", NUMBER, token.Id);
    }

    /**
     * Tests the case-insensitive mode.
     */
    public void TestCaseInsensitive() {
        Tokenizer  tokenizer = CreateDefaultTokenizer("kEyWOrd aBc ", true);

        ReadToken(tokenizer, KEYWORD);
        ReadToken(tokenizer, IDENTIFIER);
        ReadToken(tokenizer, EOF);
    }

    /**
     * Tests resetting the tokenizer with different input streams.
     */
    public void TestReset() {
        Tokenizer  tokenizer = CreateDefaultTokenizer(" 12 keyword 0 ", false);

        ReadToken(tokenizer, NUMBER);
        ReadToken(tokenizer, KEYWORD);
        ReadToken(tokenizer, NUMBER);
        ReadToken(tokenizer, EOF);

        tokenizer.Reset(new StringReader("12 (keyword)"));
        ReadToken(tokenizer, NUMBER);
        FailReadToken(tokenizer);
        ReadToken(tokenizer, KEYWORD);

        tokenizer.Reset(new StringReader(""));
        ReadToken(tokenizer, EOF);

        tokenizer.Reset(new StringReader(" 12 keyword 0 "));
        ReadToken(tokenizer, NUMBER);
        ReadToken(tokenizer, KEYWORD);
        ReadToken(tokenizer, NUMBER);
        ReadToken(tokenizer, EOF);
    }

    /**
     * Creates a new tokenizer.
     *
     * @param input          the input string
     * @param ignoreCase     the character case ignore flag
     *
     * @return a new tokenizer
     */
    private Tokenizer CreateTokenizer(string input, bool ignoreCase) {
        return new Tokenizer(new StringReader(input), ignoreCase);
    }

    /**
     * Creates a new default tokenizer that recognizes a trivial
     * language.
     *
     * @param input          the input string
     * @param ignoreCase     the character case ignore flag
     *
     * @return a new tokenizer
     */
    private Tokenizer CreateDefaultTokenizer(string input, bool ignoreCase) {
        Tokenizer     tokenizer = CreateTokenizer(input, ignoreCase);
        TokenPattern  pattern;

        pattern = new TokenPattern(KEYWORD,
                                   "KEYWORD",
                                   TokenPattern.PatternType.STRING,
                                   "keyword");
        AddPattern(tokenizer, pattern);
        pattern = new TokenPattern(IDENTIFIER,
                                   "IDENTIFIER",
                                   TokenPattern.PatternType.REGEXP,
                                   "[A-Z]+");
        AddPattern(tokenizer, pattern);
        pattern = new TokenPattern(NUMBER,
                                   "NUMBER",
                                   TokenPattern.PatternType.REGEXP,
                                   "[0-9]+");
        AddPattern(tokenizer, pattern);
        pattern = new TokenPattern(WHITESPACE,
                                   "WHITESPACE",
                                   TokenPattern.PatternType.REGEXP,
                                   "[ \t\n]+");
        pattern.Ignore = true;
        AddPattern(tokenizer, pattern);
        pattern = new TokenPattern(ERROR,
                                   "ERROR",
                                   TokenPattern.PatternType.STRING,
                                   "error");
        pattern.Error = true;
        AddPattern(tokenizer, pattern);

        return tokenizer;
    }

    /**
     * Adds a pattern to the tokenizer and reports a test failure if
     * it failed.
     *
     * @param tokenizer      the tokenizer
     * @param pattern        the pattern to add
     */
    private void AddPattern(Tokenizer tokenizer, TokenPattern pattern) {
        try {
            tokenizer.AddPattern(pattern);
        } catch (ParserCreationException e) {
            Fail("couldn't add pattern " + pattern.Name + ": " +
                 e.Message);
        }
    }

    /**
     * Adds a pattern to the tokenizer and reports a test failure if
     * it failed.
     *
     * @param tokenizer      the tokenizer
     * @param pattern        the pattern to add
     */
    private void FailAddPattern(Tokenizer tokenizer, TokenPattern pattern) {
        try {
            tokenizer.AddPattern(pattern);
            Fail("could add pattern " + pattern.Name);
        } catch (ParserCreationException) {
            // Failure was expected
        }
    }

    /**
     * Reads the next token. This method reports a test failure if a
     * token couldn't be read.
     *
     * @param tokenizer      the tokenizer to use
     *
     * @return the token read
     */
    private Token ReadToken(Tokenizer tokenizer) {
        try {
            return tokenizer.Next();
        } catch (ParseException e) {
            Fail("couldn't read next token: " + e.Message);
            return null; // Unreachable
        }
    }

    /**
     * Reads the next token and checks it's id. This method reports a
     * test failure if the right token couldn't be read.
     *
     * @param tokenizer      the tokenizer to use
     * @param id             the expected token id
     *
     * @return the token read
     */
    private Token ReadToken(Tokenizer tokenizer, int id) {
        Token  token = ReadToken(tokenizer);

        if (id == EOF) {
            if (token != null) {
                Fail("expected end of file, found " + token);
            }
        } else {
            if (token != null) {
                AssertEquals("token id", id, token.Id);
            } else {
                Fail("expected " + id + ", found EOF");
            }
        }
        return token;
    }

    /**
     * Fails to read the next token. This method reports a test
     * failure if a token could be read.
     *
     * @param tokenizer      the tokenizer to use
     */
    private void FailReadToken(Tokenizer tokenizer) {
        Token  token;

        try {
            token = tokenizer.Next();
            Fail("could read token " + token.ToString());
        } catch (ParseException) {
            // Failure was expected
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

    /**
     * Throws a test fail exception if the values aren't equal.
     *
     * @param label          the assert label
     * @param expected       the expected value
     * @param value          the value found
     */
    private void AssertEquals(string label, int expected, int value) {
        if (expected != value) {
            Fail(label + ", expected: " + expected + ", found: " + value);
        }
    }

    /**
     * Throws a test fail exception if the values aren't equal.
     *
     * @param label          the assert label
     * @param expected       the expected value
     * @param value          the value found
     */
    private void AssertEquals(string label, bool expected, bool value) {
        if (expected != value) {
            Fail(label + ", expected: " + expected + ", found: " + value);
        }
    }

    /**
     * Throws a test fail exception if the values aren't equal.
     *
     * @param label          the assert label
     * @param expected       the expected value
     * @param value          the value found
     */
    private void AssertEquals(string label, object expected, object value) {
        if (expected != value) {
            Fail(label + ", expected: " + expected + ", found: " + value);
        }
    }
}
