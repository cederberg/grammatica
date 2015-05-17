/*
 * ArithmeticParser.cs
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
     * <remarks>A token stream parser.</remarks>
     */
    internal class ArithmeticParser : RecursiveDescentParser {

        /**
         * <summary>An enumeration with the generated production node
         * identity constants.</summary>
         */
        private enum SynteticPatterns {
        }

        /**
         * <summary>Creates a new parser with a default analyzer.</summary>
         *
         * <param name='input'>the input stream to read from</param>
         *
         * <exception cref='ParserCreationException'>if the parser
         * couldn't be initialized correctly</exception>
         */
        public ArithmeticParser(TextReader input)
            : base(input) {

            CreatePatterns();
        }

        /**
         * <summary>Creates a new parser.</summary>
         *
         * <param name='input'>the input stream to read from</param>
         *
         * <param name='analyzer'>the analyzer to parse with</param>
         *
         * <exception cref='ParserCreationException'>if the parser
         * couldn't be initialized correctly</exception>
         */
        public ArithmeticParser(TextReader input, ArithmeticAnalyzer analyzer)
            : base(input, analyzer) {

            CreatePatterns();
        }

        /**
         * <summary>Creates a new tokenizer for this parser. Can be overridden
         * by a subclass to provide a custom implementation.</summary>
         *
         * <param name='input'>the input stream to read from</param>
         *
         * <returns>the tokenizer created</returns>
         *
         * <exception cref='ParserCreationException'>if the tokenizer
         * couldn't be initialized correctly</exception>
         */
        protected override Tokenizer NewTokenizer(TextReader input) {
            return new ArithmeticTokenizer(input);
        }

        /**
         * <summary>Initializes the parser by creating all the production
         * patterns.</summary>
         *
         * <exception cref='ParserCreationException'>if the parser
         * couldn't be initialized correctly</exception>
         */
        private void CreatePatterns() {
            ProductionPattern             pattern;
            ProductionPatternAlternative  alt;

            pattern = new ProductionPattern((int) ArithmeticConstants.EXPRESSION,
                                            "Expression");
            alt = new ProductionPatternAlternative();
            alt.AddProduction((int) ArithmeticConstants.TERM, 1, 1);
            alt.AddProduction((int) ArithmeticConstants.EXPRESSION_REST, 0, 1);
            pattern.AddAlternative(alt);
            AddPattern(pattern);

            pattern = new ProductionPattern((int) ArithmeticConstants.EXPRESSION_REST,
                                            "ExpressionRest");
            alt = new ProductionPatternAlternative();
            alt.AddToken((int) ArithmeticConstants.ADD, 1, 1);
            alt.AddProduction((int) ArithmeticConstants.EXPRESSION, 1, 1);
            pattern.AddAlternative(alt);
            alt = new ProductionPatternAlternative();
            alt.AddToken((int) ArithmeticConstants.SUB, 1, 1);
            alt.AddProduction((int) ArithmeticConstants.EXPRESSION, 1, 1);
            pattern.AddAlternative(alt);
            AddPattern(pattern);

            pattern = new ProductionPattern((int) ArithmeticConstants.TERM,
                                            "Term");
            alt = new ProductionPatternAlternative();
            alt.AddProduction((int) ArithmeticConstants.FACTOR, 1, 1);
            alt.AddProduction((int) ArithmeticConstants.TERM_REST, 0, 1);
            pattern.AddAlternative(alt);
            AddPattern(pattern);

            pattern = new ProductionPattern((int) ArithmeticConstants.TERM_REST,
                                            "TermRest");
            alt = new ProductionPatternAlternative();
            alt.AddToken((int) ArithmeticConstants.MUL, 1, 1);
            alt.AddProduction((int) ArithmeticConstants.TERM, 1, 1);
            pattern.AddAlternative(alt);
            alt = new ProductionPatternAlternative();
            alt.AddToken((int) ArithmeticConstants.DIV, 1, 1);
            alt.AddProduction((int) ArithmeticConstants.TERM, 1, 1);
            pattern.AddAlternative(alt);
            AddPattern(pattern);

            pattern = new ProductionPattern((int) ArithmeticConstants.FACTOR,
                                            "Factor");
            alt = new ProductionPatternAlternative();
            alt.AddProduction((int) ArithmeticConstants.ATOM, 1, 1);
            pattern.AddAlternative(alt);
            alt = new ProductionPatternAlternative();
            alt.AddToken((int) ArithmeticConstants.LEFT_PAREN, 1, 1);
            alt.AddProduction((int) ArithmeticConstants.EXPRESSION, 1, 1);
            alt.AddToken((int) ArithmeticConstants.RIGHT_PAREN, 1, 1);
            pattern.AddAlternative(alt);
            AddPattern(pattern);

            pattern = new ProductionPattern((int) ArithmeticConstants.ATOM,
                                            "Atom");
            alt = new ProductionPatternAlternative();
            alt.AddToken((int) ArithmeticConstants.NUMBER, 1, 1);
            pattern.AddAlternative(alt);
            alt = new ProductionPatternAlternative();
            alt.AddToken((int) ArithmeticConstants.IDENTIFIER, 1, 1);
            pattern.AddAlternative(alt);
            AddPattern(pattern);
        }
    }
}
