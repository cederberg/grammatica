/*
 * GrammarParser.java
 *
 * THIS FILE HAS BEEN GENERATED AUTOMATICALLY. DO NOT EDIT!
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
 * Copyright (c) 2003-2009 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica;

import java.io.Reader;

import net.percederberg.grammatica.parser.ParserCreationException;
import net.percederberg.grammatica.parser.ProductionPattern;
import net.percederberg.grammatica.parser.ProductionPatternAlternative;
import net.percederberg.grammatica.parser.RecursiveDescentParser;
import net.percederberg.grammatica.parser.Tokenizer;

/**
 * A token stream parser.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
class GrammarParser extends RecursiveDescentParser {

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_1 = 3001;

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_2 = 3002;

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_3 = 3003;

    /**
     * A generated production node identity constant.
     */
    private static final int SUBPRODUCTION_4 = 3004;

    /**
     * Creates a new parser with a default analyzer.
     *
     * @param in             the input stream to read from
     *
     * @throws ParserCreationException if the parser couldn't be
     *             initialized correctly
     */
    public GrammarParser(Reader in) throws ParserCreationException {
        super(in);
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
    public GrammarParser(Reader in, GrammarAnalyzer analyzer)
        throws ParserCreationException {

        super(in, analyzer);
        createPatterns();
    }

    /**
     * Creates a new tokenizer for this parser. Can be overridden by a
     * subclass to provide a custom implementation.
     *
     * @param in             the input stream to read from
     *
     * @return the tokenizer created
     *
     * @throws ParserCreationException if the tokenizer couldn't be
     *             initialized correctly
     */
    protected Tokenizer newTokenizer(Reader in)
        throws ParserCreationException {

        return new GrammarTokenizer(in);
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

        pattern = new ProductionPattern(GrammarConstants.GRAMMAR,
                                        "Grammar");
        alt = new ProductionPatternAlternative();
        alt.addProduction(GrammarConstants.HEADER_PART, 0, 1);
        alt.addProduction(GrammarConstants.TOKEN_PART, 1, 1);
        alt.addProduction(GrammarConstants.PRODUCTION_PART, 0, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(GrammarConstants.HEADER_PART,
                                        "HeaderPart");
        alt = new ProductionPatternAlternative();
        alt.addToken(GrammarConstants.HEADER, 1, 1);
        alt.addProduction(GrammarConstants.HEADER_DECLARATION, 0, -1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(GrammarConstants.HEADER_DECLARATION,
                                        "HeaderDeclaration");
        alt = new ProductionPatternAlternative();
        alt.addToken(GrammarConstants.IDENTIFIER, 1, 1);
        alt.addToken(GrammarConstants.EQUALS, 1, 1);
        alt.addToken(GrammarConstants.QUOTED_STRING, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(GrammarConstants.TOKEN_PART,
                                        "TokenPart");
        alt = new ProductionPatternAlternative();
        alt.addToken(GrammarConstants.TOKENS, 1, 1);
        alt.addProduction(GrammarConstants.TOKEN_DECLARATION, 0, -1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(GrammarConstants.TOKEN_DECLARATION,
                                        "TokenDeclaration");
        alt = new ProductionPatternAlternative();
        alt.addToken(GrammarConstants.IDENTIFIER, 1, 1);
        alt.addToken(GrammarConstants.EQUALS, 1, 1);
        alt.addProduction(GrammarConstants.TOKEN_VALUE, 1, 1);
        alt.addProduction(GrammarConstants.TOKEN_HANDLING, 0, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(GrammarConstants.TOKEN_VALUE,
                                        "TokenValue");
        alt = new ProductionPatternAlternative();
        alt.addToken(GrammarConstants.QUOTED_STRING, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GrammarConstants.REGEXP, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(GrammarConstants.TOKEN_HANDLING,
                                        "TokenHandling");
        alt = new ProductionPatternAlternative();
        alt.addToken(GrammarConstants.IGNORE, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GrammarConstants.ERROR, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(GrammarConstants.PRODUCTION_PART,
                                        "ProductionPart");
        alt = new ProductionPatternAlternative();
        alt.addToken(GrammarConstants.PRODUCTIONS, 1, 1);
        alt.addProduction(GrammarConstants.PRODUCTION_DECLARATION, 0, -1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(GrammarConstants.PRODUCTION_DECLARATION,
                                        "ProductionDeclaration");
        alt = new ProductionPatternAlternative();
        alt.addToken(GrammarConstants.IDENTIFIER, 1, 1);
        alt.addToken(GrammarConstants.EQUALS, 1, 1);
        alt.addProduction(GrammarConstants.PRODUCTION, 1, 1);
        alt.addToken(GrammarConstants.SEMICOLON, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(GrammarConstants.PRODUCTION,
                                        "Production");
        alt = new ProductionPatternAlternative();
        alt.addProduction(GrammarConstants.PRODUCTION_ATOM, 1, -1);
        alt.addProduction(SUBPRODUCTION_1, 0, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(GrammarConstants.PRODUCTION_ATOM,
                                        "ProductionAtom");
        alt = new ProductionPatternAlternative();
        alt.addToken(GrammarConstants.IDENTIFIER, 1, 1);
        alt.addProduction(SUBPRODUCTION_2, 0, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GrammarConstants.QUOTED_STRING, 1, 1);
        alt.addProduction(SUBPRODUCTION_3, 0, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GrammarConstants.LEFT_PAREN, 1, 1);
        alt.addProduction(GrammarConstants.PRODUCTION, 1, 1);
        alt.addToken(GrammarConstants.RIGHT_PAREN, 1, 1);
        alt.addProduction(SUBPRODUCTION_4, 0, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GrammarConstants.LEFT_BRACE, 1, 1);
        alt.addProduction(GrammarConstants.PRODUCTION, 1, 1);
        alt.addToken(GrammarConstants.RIGHT_BRACE, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GrammarConstants.LEFT_BRACKET, 1, 1);
        alt.addProduction(GrammarConstants.PRODUCTION, 1, 1);
        alt.addToken(GrammarConstants.RIGHT_BRACKET, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_1,
                                        "Subproduction1");
        pattern.setSynthetic(true);
        alt = new ProductionPatternAlternative();
        alt.addToken(GrammarConstants.VERTICAL_BAR, 1, 1);
        alt.addProduction(GrammarConstants.PRODUCTION, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_2,
                                        "Subproduction2");
        pattern.setSynthetic(true);
        alt = new ProductionPatternAlternative();
        alt.addToken(GrammarConstants.QUESTION_MARK, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GrammarConstants.ASTERISK, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GrammarConstants.PLUS_SIGN, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_3,
                                        "Subproduction3");
        pattern.setSynthetic(true);
        alt = new ProductionPatternAlternative();
        alt.addToken(GrammarConstants.QUESTION_MARK, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GrammarConstants.ASTERISK, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GrammarConstants.PLUS_SIGN, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);

        pattern = new ProductionPattern(SUBPRODUCTION_4,
                                        "Subproduction4");
        pattern.setSynthetic(true);
        alt = new ProductionPatternAlternative();
        alt.addToken(GrammarConstants.QUESTION_MARK, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GrammarConstants.ASTERISK, 1, 1);
        pattern.addAlternative(alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(GrammarConstants.PLUS_SIGN, 1, 1);
        pattern.addAlternative(alt);
        addPattern(pattern);
    }
}
