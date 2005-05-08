/*
 * ArithmeticParser.java
 *
 * THIS FILE HAS BEEN GENERATED AUTOMATICALLY. DO NOT EDIT!
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
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
 * Copyright (c) 2003 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.test;

import java.io.Reader;

import net.percederberg.grammatica.parser.Analyzer;
import net.percederberg.grammatica.parser.ParserCreationException;
import net.percederberg.grammatica.parser.ProductionPattern;
import net.percederberg.grammatica.parser.ProductionPatternAlternative;
import net.percederberg.grammatica.parser.RecursiveDescentParser;

/**
 * A token stream parser.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
class ArithmeticParser extends RecursiveDescentParser {

    /**
     * Creates a new parser.
     *
     * @param in             the input stream to read from
     *
     * @throws ParserCreationException if the parser couldn't be
     *             initialized correctly
     */
    public ArithmeticParser(Reader in) throws ParserCreationException {
        super(new ArithmeticTokenizer(in));
        createPatterns();
    }

    /**
     * Creates a new parser.
     *
     * @param in             the input stream to read from
     * @param analyzer       the analyzer to use while parsing
     *
     * @throws ParserCreationException if the parser couldn't be
     *             initialized correctly
     */
    public ArithmeticParser(Reader in, Analyzer analyzer)
        throws ParserCreationException {

        super(new ArithmeticTokenizer(in), analyzer);
        createPatterns();
    }

    /**
     * Initializes the parser by creating all the production patterns.
     *
     * @throws ParserCreationException if the parser couldn't be
     *             initialized correctly
     */
    private void createPatterns() throws ParserCreationException {
        ProductionPattern             pattern;
        ProductionPatternAlternative  alt;

        pattern = new ProductionPattern(ArithmeticConstants.EXPRESSION,
                                        "Expression");
        alt = new ProductionPatternAlternative();
        alt.addProduction(ArithmeticConstants.TERM, 1, 1);
        alt.addProduction(ArithmeticConstants.EXPRESSION_REST, 0, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(ArithmeticConstants.EXPRESSION_REST,
                                        "ExpressionRest");
        alt = new ProductionPatternAlternative();
        alt.addToken(ArithmeticConstants.ADD, 1, 1);
        alt.addProduction(ArithmeticConstants.EXPRESSION, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(ArithmeticConstants.SUB, 1, 1);
        alt.addProduction(ArithmeticConstants.EXPRESSION, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(ArithmeticConstants.TERM,
                                        "Term");
        alt = new ProductionPatternAlternative();
        alt.addProduction(ArithmeticConstants.FACTOR, 1, 1);
        alt.addProduction(ArithmeticConstants.TERM_REST, 0, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(ArithmeticConstants.TERM_REST,
                                        "TermRest");
        alt = new ProductionPatternAlternative();
        alt.addToken(ArithmeticConstants.MUL, 1, 1);
        alt.addProduction(ArithmeticConstants.TERM, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(ArithmeticConstants.DIV, 1, 1);
        alt.addProduction(ArithmeticConstants.TERM, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(ArithmeticConstants.FACTOR,
                                        "Factor");
        alt = new ProductionPatternAlternative();
        alt.addProduction(ArithmeticConstants.ATOM, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(ArithmeticConstants.LEFT_PAREN, 1, 1);
        alt.addProduction(ArithmeticConstants.EXPRESSION, 1, 1);
        alt.addToken(ArithmeticConstants.RIGHT_PAREN, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(ArithmeticConstants.ATOM,
                                        "Atom");
        alt = new ProductionPatternAlternative();
        alt.addToken(ArithmeticConstants.NUMBER, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(ArithmeticConstants.IDENTIFIER, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);
    }
}
