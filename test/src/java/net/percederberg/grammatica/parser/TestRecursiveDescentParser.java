/*
 * TestRecursiveDescentParser.java
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

package net.percederberg.grammatica.parser;

import junit.framework.TestCase;

/**
 * A test case for the RecursiveDescentParser class.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
public class TestRecursiveDescentParser extends TestCase {

    /**
     * A token constant.
     */
    private static final int T1 = 1001;

    /**
     * A token constant.
     */
    private static final int T2 = 1002;

    /**
     * A token constant.
     */
    private static final int T3 = 1003;

    /**
     * A production constant.
     */
    private static final int P1 = 2001;

    /**
     * A production constant.
     */
    private static final int P2 = 2002;

    /**
     * A production constant.
     */
    private static final int P3 = 2003;

    /**
     * The production pattern variable used in all tests.
     */
    private ProductionPattern pattern;

    /**
     * The production pattern alternative variable used in all tests.
     */
    private ProductionPatternAlternative alt;

    /**
     * Tests adding an empty pattern to the parser.
     */
    public void testEmptyPattern() {
        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        addAlternative(pattern, alt);
        failAddPattern(createParser(), pattern);
    }

    /**
     * Tests adding a possible empty pattern to the parser.
     */
    public void testPossiblyEmptyPattern() {
        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.addToken(T1, 1, 1);
        addAlternative(pattern, alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(T2, 0, 1);
        addAlternative(pattern, alt);
        failAddPattern(createParser(), pattern);
    }

    /**
     * Tests adding a left-recursive pattern to the parser.
     */
    public void testLeftRecursivePattern() {
        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.addProduction(P1, 0, 1);
        alt.addToken(T1, 1, 1);
        addAlternative(pattern, alt);
        failAddPattern(createParser(), pattern);
    }

    /**
     * Tests adding a right-recursive pattern to the parser.
     */
    public void testRightRecursivePattern() {
        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.addToken(T1, 1, 1);
        alt.addProduction(P1, 0, 1);
        addAlternative(pattern, alt);
        addPattern(createParser(), pattern);
    }

    /**
     * Tests adding the same pattern twice.
     */
    public void testDuplicatePattern() {
        Parser  parser = createParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.addToken(T1, 1, 1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);
        failAddPattern(parser, pattern);
    }

    /**
     * Tests adding two patterns with the same id.
     */
    public void testPatternCollision() {
        Parser  parser = createParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.addToken(T1, 1, 1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);

        pattern = new ProductionPattern(P1, "P2");
        alt = new ProductionPatternAlternative();
        alt.addToken(T2, 1, 1);
        addAlternative(pattern, alt);
        failAddPattern(parser, pattern);
    }

    /**
     * Tests adding two patterns with the same alternatives.
     */
    public void testIdenticalPatterns() {
        Parser  parser = createParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.addToken(T1, 1, 1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);

        pattern = new ProductionPattern(P2, "P2");
        alt = new ProductionPatternAlternative();
        alt.addToken(T1, 1, 1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);
    }

    /**
     * Tests a simple grammar loop.
     */
    public void testSimpleGrammarLoop() {
        Parser parser = createParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.addProduction(P2, 1, 1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);

        pattern = new ProductionPattern(P2, "P2");
        alt = new ProductionPatternAlternative();
        alt.addProduction(P1, 1, 1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);

        failPrepareParser(parser);
    }

    /**
     * Tests a complex grammar loop with optional parts and
     * alternatives.
     */
    public void testComplexGrammarLoop() {
        Parser parser = createParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.addProduction(P2, 1, 1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);

        pattern = new ProductionPattern(P2, "P2");
        alt = new ProductionPatternAlternative();
        alt.addProduction(P3, 0, 1);
        alt.addToken(T1, 1, 1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);

        pattern = new ProductionPattern(P3, "P3");
        alt = new ProductionPatternAlternative();
        alt.addToken(T3, 1, 1);
        addAlternative(pattern, alt);
        alt = new ProductionPatternAlternative();
        alt.addProduction(P1, 0, 1);
        alt.addToken(T2, 1, 1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);

        failPrepareParser(parser);
    }

    /**
     * Tests an unresolvable conflict between two productions.
     */
    public void testProductionConflict() {
        Parser parser = createParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.addProduction(P2, 1, 1);
        addAlternative(pattern, alt);
        alt = new ProductionPatternAlternative();
        alt.addProduction(P3, 1, 1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);

        pattern = new ProductionPattern(P2, "P2");
        alt = new ProductionPatternAlternative();
        alt.addToken(T1, 0, -1);
        alt.addToken(T2, 1, 1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);

        pattern = new ProductionPattern(P3, "P3");
        alt = new ProductionPatternAlternative();
        alt.addToken(T1, 1, 1);
        alt.addProduction(P3, 0, 1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);

        failPrepareParser(parser);
    }

    /**
     * Tests an unresolvable conflict between two production
     * alternatives.
     */
    public void testAlternativeConflict() {
        Parser parser = createParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.addToken(T1, 0, -1);
        alt.addToken(T2, 1, 1);
        addAlternative(pattern, alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(T1, 1, -1);
        alt.addToken(T3, 1, 1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);

        failPrepareParser(parser);
    }

    /**
     * Tests an unresolvable token conflict inside a production
     * alternative.
     */
    public void testElementTokenConflict() {
        Parser parser = createParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.addToken(T1, 0, 1);
        alt.addToken(T1, 1, -1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);

        failPrepareParser(parser);
    }

    /**
     * Tests an unresolvable production conflict inside a production
     * alternative.
     */
    public void testElementProductionConflict() {
        Parser parser = createParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.addProduction(P2, 0, 1);
        alt.addProduction(P3, 1, 1);
        alt.addToken(T2, 1, 1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);

        pattern = new ProductionPattern(P2, "P2");
        alt = new ProductionPatternAlternative();
        alt.addToken(T1, 1, 1);
        alt.addProduction(P2, 0, 1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);

        pattern = new ProductionPattern(P3, "P3");
        alt = new ProductionPatternAlternative();
        addAlternative(pattern, alt);
        alt.addToken(T1, 1, -1);
        addPattern(parser, pattern);

        failPrepareParser(parser);
    }

    /**
     * Tests an unresolvable production conflict in the tail of a
     * production alternative.
     */
    public void testElementTailConflict() {
        Parser parser = createParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.addProduction(P2, 1, 1);
        alt.addToken(T2, 0, 1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);

        pattern = new ProductionPattern(P2, "P2");
        alt = new ProductionPatternAlternative();
        alt.addToken(T1, 1, 1);
        alt.addToken(T2, 0, 1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);

        // TODO: enable this test
        // failPrepareParser(parser);
    }

    /**
     * Tests a resolvable conflict between two production patterns.
     */
    public void testResolvableProductionConflict() {
        Parser parser = createParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.addProduction(P2, 1, 1);
        addAlternative(pattern, alt);
        alt = new ProductionPatternAlternative();
        alt.addProduction(P3, 1, 1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);

        pattern = new ProductionPattern(P2, "P2");
        alt = new ProductionPatternAlternative();
        alt.addToken(T1, 1, 1);
        alt.addToken(T1, 0, 1);
        alt.addToken(T2, 1, 1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);

        pattern = new ProductionPattern(P3, "P3");
        alt = new ProductionPatternAlternative();
        alt.addToken(T1, 1, -1);
        alt.addToken(T3, 1, 1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);

        prepareParser(parser);
    }

    /**
     * Tests a resolvable conflict between two production
     * alternatives.
     */
    public void testResolvableAlternativeConflict() {
        Parser parser = createParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.addToken(T1, 1, 1);
        alt.addToken(T1, 0, 1);
        alt.addToken(T2, 1, 1);
        addAlternative(pattern, alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(T1, 1, -1);
        alt.addToken(T3, 1, 1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);

        prepareParser(parser);
    }

    /**
     * Tests a resolvable token conflict inside a production
     * alternative.
     */
    public void testResolvableElementTokenConflict() {
        Parser parser = createParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.addToken(T1, 0, 1);
        alt.addToken(T1, 1, 1);
        alt.addToken(T2, 1, 1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);

        prepareParser(parser);
    }

    /**
     * Tests a resolvable production conflict inside a production
     * alternative.
     */
    public void testResolvableElementProductionConflict() {
        Parser parser = createParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.addProduction(P2, 0, 1);
        alt.addToken(T1, 1, -1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);

        pattern = new ProductionPattern(P2, "P2");
        alt = new ProductionPatternAlternative();
        alt.addToken(T1, 1, 1);
        alt.addToken(T2, 1, 1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);

        prepareParser(parser);
    }

    /**
     * Tests a resolvable production conflict in the tail of a
     * production alternative.
     */
    public void testResolvableElementTailConflict() {
        Parser parser = createParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.addProduction(P2, 1, 1);
        alt.addToken(T2, 1, 1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);

        pattern = new ProductionPattern(P2, "P2");
        alt = new ProductionPatternAlternative();
        alt.addToken(T1, 1, 1);
        alt.addToken(T2, 0, 1);
        addAlternative(pattern, alt);
        addPattern(parser, pattern);

        prepareParser(parser);
    }

    /**
     * Creates a new parser.
     *
     * @return a new parser
     */
    private Parser createParser() {
        return new RecursiveDescentParser((Tokenizer) null);
    }

    /**
     * Prepares the parser and reports a test failure if it failed.
     *
     * @param parser         the parser to prepare
     */
    private void prepareParser(Parser parser) {
        try {
            parser.prepare();
        } catch (ParserCreationException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Prepares the parser and reports a test failure if it succeeded.
     *
     * @param parser         the parser to prepare
     */
    private void failPrepareParser(Parser parser) {
        try {
            parser.prepare();
            fail();
        } catch (ParserCreationException e) {
            // Failure was expected
        }
    }

    /**
     * Adds a production pattern to a parser and reports a test
     * failure if it failed.
     *
     * @param parser         the parser to add a pattern to
     * @param pattern        the production pattern to add
     */
    private void addPattern(Parser parser, ProductionPattern pattern) {
        try {
            parser.addPattern(pattern);
        } catch (ParserCreationException e) {
            fail("couldn't add pattern " + pattern.getName() + ": " +
                 e.getMessage());
        }
    }

    /**
     * Adds a production pattern to a parser and reports a test
     * failure if it succeeded.
     *
     * @param parser         the parser to add a pattern to
     * @param pattern        the production pattern to add
     */
    private void failAddPattern(Parser parser, ProductionPattern pattern) {
        try {
            parser.addPattern(pattern);
            fail("could add pattern " + pattern.getName());
        } catch (ParserCreationException e) {
            // Failure was expected
        }
    }

    /**
     * Adds a pattern alternative. This method reports a test failure
     * if an exception was thrown.
     *
     * @param pattern        the production pattern
     * @param alt            the pattern alternative to add
     */
    private void addAlternative(ProductionPattern pattern,
                                ProductionPatternAlternative alt) {

        try {
            pattern.addAlternative(alt);
        } catch (ParserCreationException e) {
            fail("couldn't add alternative to " + pattern.getName() +
                 ": " + e.getMessage());
        }
    }
}
