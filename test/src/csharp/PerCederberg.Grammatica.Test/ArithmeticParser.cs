/*
 * ArithmeticParser.cs
 * 
 * THIS FILE HAS BEEN GENERATED AUTOMATICALLY. DO NOT EDIT!
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

using System.IO;

using PerCederberg.Grammatica.Parser;

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
         * <summary>Creates a new parser.</summary>
         * 
         * <param name='input'>the input stream to read from</param>
         * 
         * <exception cref='ParserCreationException'>if the parser
         * couldn't be initialized correctly</exception>
         */
        public ArithmeticParser(TextReader input)
            : base(new ArithmeticTokenizer(input)) {

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
        public ArithmeticParser(TextReader input, Analyzer analyzer)
            : base(new ArithmeticTokenizer(input), analyzer) {

            CreatePatterns();
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
