/*
 * ArithmeticTokenizer.java
 *
 * THIS FILE HAS BEEN GENERATED AUTOMATICALLY. DO NOT EDIT!
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the BSD license.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * LICENSE.txt file for more details.
 *
 * Copyright (c) 2003-2015 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.test;

import java.io.Reader;

import net.percederberg.grammatica.parser.ParserCreationException;
import net.percederberg.grammatica.parser.TokenPattern;
import net.percederberg.grammatica.parser.Tokenizer;

/**
 * A character stream tokenizer.
 *
 * @author   Per Cederberg
 * @version  1.0
 */
class ArithmeticTokenizer extends Tokenizer {

    /**
     * Creates a new tokenizer for the specified input stream.
     *
     * @param input          the input stream to read
     *
     * @throws ParserCreationException if the tokenizer couldn't be
     *             initialized correctly
     */
    public ArithmeticTokenizer(Reader input)
        throws ParserCreationException {

        super(input, false);
        createPatterns();
    }

    /**
     * Initializes the tokenizer by creating all the token patterns.
     *
     * @throws ParserCreationException if the tokenizer couldn't be
     *             initialized correctly
     */
    private void createPatterns() throws ParserCreationException {
        TokenPattern  pattern;

        pattern = new TokenPattern(ArithmeticConstants.ADD,
                                   "ADD",
                                   TokenPattern.STRING_TYPE,
                                   "+");
        addPattern(pattern);

        pattern = new TokenPattern(ArithmeticConstants.SUB,
                                   "SUB",
                                   TokenPattern.STRING_TYPE,
                                   "-");
        addPattern(pattern);

        pattern = new TokenPattern(ArithmeticConstants.MUL,
                                   "MUL",
                                   TokenPattern.STRING_TYPE,
                                   "*");
        addPattern(pattern);

        pattern = new TokenPattern(ArithmeticConstants.DIV,
                                   "DIV",
                                   TokenPattern.STRING_TYPE,
                                   "/");
        addPattern(pattern);

        pattern = new TokenPattern(ArithmeticConstants.LEFT_PAREN,
                                   "LEFT_PAREN",
                                   TokenPattern.STRING_TYPE,
                                   "(");
        addPattern(pattern);

        pattern = new TokenPattern(ArithmeticConstants.RIGHT_PAREN,
                                   "RIGHT_PAREN",
                                   TokenPattern.STRING_TYPE,
                                   ")");
        addPattern(pattern);

        pattern = new TokenPattern(ArithmeticConstants.NUMBER,
                                   "NUMBER",
                                   TokenPattern.REGEXP_TYPE,
                                   "[0-9]+");
        addPattern(pattern);

        pattern = new TokenPattern(ArithmeticConstants.IDENTIFIER,
                                   "IDENTIFIER",
                                   TokenPattern.REGEXP_TYPE,
                                   "[a-z]");
        addPattern(pattern);

        pattern = new TokenPattern(ArithmeticConstants.WHITESPACE,
                                   "WHITESPACE",
                                   TokenPattern.REGEXP_TYPE,
                                   "[ \\t\\n\\r]+");
        pattern.setIgnore();
        addPattern(pattern);
    }
}
