/*
 * ArithmeticTokenizer.cs
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

using System.IO;

using PerCederberg.Grammatica.Runtime;

namespace PerCederberg.Grammatica.Test {

    /**
     * <remarks>A character stream tokenizer.</remarks>
     */
    internal class ArithmeticTokenizer : Tokenizer {

        /**
         * <summary>Creates a new tokenizer for the specified input
         * stream.</summary>
         *
         * <param name='input'>the input stream to read</param>
         *
         * <exception cref='ParserCreationException'>if the tokenizer
         * couldn't be initialized correctly</exception>
         */
        public ArithmeticTokenizer(TextReader input)
            : base(input, false) {

            CreatePatterns();
        }

        /**
         * <summary>Initializes the tokenizer by creating all the token
         * patterns.</summary>
         *
         * <exception cref='ParserCreationException'>if the tokenizer
         * couldn't be initialized correctly</exception>
         */
        private void CreatePatterns() {
            TokenPattern  pattern;

            pattern = new TokenPattern((int) ArithmeticConstants.ADD,
                                       "ADD",
                                       TokenPattern.PatternType.STRING,
                                       "+");
            AddPattern(pattern);

            pattern = new TokenPattern((int) ArithmeticConstants.SUB,
                                       "SUB",
                                       TokenPattern.PatternType.STRING,
                                       "-");
            AddPattern(pattern);

            pattern = new TokenPattern((int) ArithmeticConstants.MUL,
                                       "MUL",
                                       TokenPattern.PatternType.STRING,
                                       "*");
            AddPattern(pattern);

            pattern = new TokenPattern((int) ArithmeticConstants.DIV,
                                       "DIV",
                                       TokenPattern.PatternType.STRING,
                                       "/");
            AddPattern(pattern);

            pattern = new TokenPattern((int) ArithmeticConstants.LEFT_PAREN,
                                       "LEFT_PAREN",
                                       TokenPattern.PatternType.STRING,
                                       "(");
            AddPattern(pattern);

            pattern = new TokenPattern((int) ArithmeticConstants.RIGHT_PAREN,
                                       "RIGHT_PAREN",
                                       TokenPattern.PatternType.STRING,
                                       ")");
            AddPattern(pattern);

            pattern = new TokenPattern((int) ArithmeticConstants.NUMBER,
                                       "NUMBER",
                                       TokenPattern.PatternType.REGEXP,
                                       "[0-9]+");
            AddPattern(pattern);

            pattern = new TokenPattern((int) ArithmeticConstants.IDENTIFIER,
                                       "IDENTIFIER",
                                       TokenPattern.PatternType.REGEXP,
                                       "[a-z]");
            AddPattern(pattern);

            pattern = new TokenPattern((int) ArithmeticConstants.WHITESPACE,
                                       "WHITESPACE",
                                       TokenPattern.PatternType.REGEXP,
                                       "[ \\t\\n\\r]+");
            pattern.Ignore = true;
            AddPattern(pattern);
        }
    }
}
