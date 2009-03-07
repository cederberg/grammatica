/*
 * TestRecursiveDescentParser.cs
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
using PerCederberg.Grammatica.Runtime;

/**
 * A test case for the RecursiveDescentParser class.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
public class TestRecursiveDescentParser {

    /**
     * A token constant.
     */
    private const int T1 = 1001;

    /**
     * A token constant.
     */
    private const int T2 = 1002;

    /**
     * A token constant.
     */
    private const int T3 = 1003;

    /**
     * A production constant.
     */
    private const int P1 = 2001;

    /**
     * A production constant.
     */
    private const int P2 = 2002;

    /**
     * A production constant.
     */
    private const int P3 = 2003;

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
    public void TestEmptyPattern() {
        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        AddAlternative(pattern, alt);
        FailAddPattern(CreateParser(), pattern);
    }

    /**
     * Tests adding a possible empty pattern to the parser.
     */
    public void TestPossiblyEmptyPattern() {
        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.AddToken(T1, 1, 1);
        AddAlternative(pattern, alt);
        alt = new ProductionPatternAlternative();
        alt.AddToken(T2, 0, 1);
        AddAlternative(pattern, alt);
        FailAddPattern(CreateParser(), pattern);
    }

    /**
     * Tests adding a left-recursive pattern to the parser.
     */
    public void TestLeftRecursivePattern() {
        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.AddProduction(P1, 0, 1);
        alt.AddToken(T1, 1, 1);
        AddAlternative(pattern, alt);
        FailAddPattern(CreateParser(), pattern);
    }

    /**
     * Tests adding a right-recursive pattern to the parser.
     */
    public void TestRightRecursivePattern() {
        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.AddToken(T1, 1, 1);
        alt.AddProduction(P1, 0, 1);
        AddAlternative(pattern, alt);
        AddPattern(CreateParser(), pattern);
    }

    /**
     * Tests adding the same pattern twice.
     */
    public void TestDuplicatePattern() {
        Parser  parser = CreateParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.AddToken(T1, 1, 1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);
        FailAddPattern(parser, pattern);
    }

    /**
     * Tests adding two patterns with the same id.
     */
    public void TestPatternCollision() {
        Parser  parser = CreateParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.AddToken(T1, 1, 1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);

        pattern = new ProductionPattern(P1, "P2");
        alt = new ProductionPatternAlternative();
        alt.AddToken(T2, 1, 1);
        AddAlternative(pattern, alt);
        FailAddPattern(parser, pattern);
    }

    /**
     * Tests adding two patterns with the same alternatives.
     */
    public void TestIdenticalPatterns() {
        Parser  parser = CreateParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.AddToken(T1, 1, 1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);

        pattern = new ProductionPattern(P2, "P2");
        alt = new ProductionPatternAlternative();
        alt.AddToken(T1, 1, 1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);
    }

    /**
     * Tests a simple grammar loop.
     */
    public void TestSimpleGrammarLoop() {
        Parser parser = CreateParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.AddProduction(P2, 1, 1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);

        pattern = new ProductionPattern(P2, "P2");
        alt = new ProductionPatternAlternative();
        alt.AddProduction(P1, 1, 1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);

        FailPrepareParser(parser);
    }

    /**
     * Tests a complex grammar loop with optional parts and
     * alternatives.
     */
    public void TestComplexGrammarLoop() {
        Parser parser = CreateParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.AddProduction(P2, 1, 1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);

        pattern = new ProductionPattern(P2, "P2");
        alt = new ProductionPatternAlternative();
        alt.AddProduction(P3, 0, 1);
        alt.AddToken(T1, 1, 1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);

        pattern = new ProductionPattern(P3, "P3");
        alt = new ProductionPatternAlternative();
        alt.AddToken(T3, 1, 1);
        AddAlternative(pattern, alt);
        alt = new ProductionPatternAlternative();
        alt.AddProduction(P1, 0, 1);
        alt.AddToken(T2, 1, 1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);

        FailPrepareParser(parser);
    }

    /**
     * Tests an unresolvable conflict between two productions.
     */
    public void TestProductionConflict() {
        Parser parser = CreateParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.AddProduction(P2, 1, 1);
        AddAlternative(pattern, alt);
        alt = new ProductionPatternAlternative();
        alt.AddProduction(P3, 1, 1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);

        pattern = new ProductionPattern(P2, "P2");
        alt = new ProductionPatternAlternative();
        alt.AddToken(T1, 0, -1);
        alt.AddToken(T2, 1, 1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);

        pattern = new ProductionPattern(P3, "P3");
        alt = new ProductionPatternAlternative();
        alt.AddToken(T1, 1, 1);
        alt.AddProduction(P3, 0, 1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);

        FailPrepareParser(parser);
    }

    /**
     * Tests an unresolvable conflict between two production
     * alternatives.
     */
    public void TestAlternativeConflict() {
        Parser parser = CreateParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.AddToken(T1, 0, -1);
        alt.AddToken(T2, 1, 1);
        AddAlternative(pattern, alt);
        alt = new ProductionPatternAlternative();
        alt.AddToken(T1, 1, -1);
        alt.AddToken(T3, 1, 1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);

        FailPrepareParser(parser);
    }

    /**
     * Tests an unresolvable token conflict inside a production
     * alternative.
     */
    public void TestElementTokenConflict() {
        Parser parser = CreateParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.AddToken(T1, 0, 1);
        alt.AddToken(T1, 1, -1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);

        FailPrepareParser(parser);
    }

    /**
     * Tests an unresolvable production conflict inside a production
     * alternative.
     */
    public void TestElementProductionConflict() {
        Parser parser = CreateParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.AddProduction(P2, 0, 1);
        alt.AddProduction(P3, 1, 1);
        alt.AddToken(T2, 1, 1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);

        pattern = new ProductionPattern(P2, "P2");
        alt = new ProductionPatternAlternative();
        alt.AddToken(T1, 1, 1);
        alt.AddProduction(P2, 0, 1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);

        pattern = new ProductionPattern(P3, "P3");
        alt = new ProductionPatternAlternative();
        AddAlternative(pattern, alt);
        alt.AddToken(T1, 1, -1);
        AddPattern(parser, pattern);

        FailPrepareParser(parser);
    }

    /**
     * Tests an unresolvable production conflict in the tail of a
     * production alternative.
     */
    public void TestElementTailConflict() {
        Parser parser = CreateParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.AddProduction(P2, 1, 1);
        alt.AddToken(T2, 0, 1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);

        pattern = new ProductionPattern(P2, "P2");
        alt = new ProductionPatternAlternative();
        alt.AddToken(T1, 1, 1);
        alt.AddToken(T2, 0, 1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);

        // TODO: enable this test
        // FailPrepareParser(parser);
    }

    /**
     * Tests a resolvable conflict between two production patterns.
     */
    public void TestResolvableProductionConflict() {
        Parser parser = CreateParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.AddProduction(P2, 1, 1);
        AddAlternative(pattern, alt);
        alt = new ProductionPatternAlternative();
        alt.AddProduction(P3, 1, 1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);

        pattern = new ProductionPattern(P2, "P2");
        alt = new ProductionPatternAlternative();
        alt.AddToken(T1, 1, 1);
        alt.AddToken(T1, 0, 1);
        alt.AddToken(T2, 1, 1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);

        pattern = new ProductionPattern(P3, "P3");
        alt = new ProductionPatternAlternative();
        alt.AddToken(T1, 1, -1);
        alt.AddToken(T3, 1, 1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);

        PrepareParser(parser);
    }

    /**
     * Tests a resolvable conflict between two production
     * alternatives.
     */
    public void TestResolvableAlternativeConflict() {
        Parser parser = CreateParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.AddToken(T1, 1, 1);
        alt.AddToken(T1, 0, 1);
        alt.AddToken(T2, 1, 1);
        AddAlternative(pattern, alt);
        alt = new ProductionPatternAlternative();
        alt.AddToken(T1, 1, -1);
        alt.AddToken(T3, 1, 1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);

        PrepareParser(parser);
    }

    /**
     * Tests a resolvable token conflict inside a production
     * alternative.
     */
    public void TestResolvableElementTokenConflict() {
        Parser parser = CreateParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.AddToken(T1, 0, 1);
        alt.AddToken(T1, 1, 1);
        alt.AddToken(T2, 1, 1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);

        PrepareParser(parser);
    }

    /**
     * Tests a resolvable production conflict inside a production
     * alternative.
     */
    public void TestResolvableElementProductionConflict() {
        Parser parser = CreateParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.AddProduction(P2, 0, 1);
        alt.AddToken(T1, 1, -1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);

        pattern = new ProductionPattern(P2, "P2");
        alt = new ProductionPatternAlternative();
        alt.AddToken(T1, 1, 1);
        alt.AddToken(T2, 1, 1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);

        PrepareParser(parser);
    }

    /**
     * Tests a resolvable production conflict in the tail of a
     * production alternative.
     */
    public void TestResolvableElementTailConflict() {
        Parser parser = CreateParser();

        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.AddProduction(P2, 1, 1);
        alt.AddToken(T2, 1, 1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);

        pattern = new ProductionPattern(P2, "P2");
        alt = new ProductionPatternAlternative();
        alt.AddToken(T1, 1, 1);
        alt.AddToken(T2, 0, 1);
        AddAlternative(pattern, alt);
        AddPattern(parser, pattern);

        PrepareParser(parser);
    }

    /**
     * Creates a new parser.
     *
     * @return a new parser
     */
    private Parser CreateParser() {
        return new RecursiveDescentParser((Tokenizer) null);
    }

    /**
     * Prepares the parser and reports a test failure if it failed.
     *
     * @param parser         the parser to prepare
     */
    private void PrepareParser(Parser parser) {
        try {
            parser.Prepare();
        } catch (ParserCreationException e) {
            Fail(e.Message);
        }
    }

    /**
     * Prepares the parser and reports a test failure if it succeeded.
     *
     * @param parser         the parser to prepare
     */
    private void FailPrepareParser(Parser parser) {
        try {
            parser.Prepare();
            Fail("succeeded in preparing parser");
        } catch (ParserCreationException) {
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
    private void AddPattern(Parser parser, ProductionPattern pattern) {
        try {
            parser.AddPattern(pattern);
        } catch (ParserCreationException e) {
            Fail("couldn't add pattern " + pattern.Name + ": " +
                 e.Message);
        }
    }

    /**
     * Adds a production pattern to a parser and reports a test
     * failure if it succeeded.
     *
     * @param parser         the parser to add a pattern to
     * @param pattern        the production pattern to add
     */
    private void FailAddPattern(Parser parser, ProductionPattern pattern) {
        try {
            parser.AddPattern(pattern);
            Fail("could add pattern " + pattern.Name);
        } catch (ParserCreationException) {
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
    private void AddAlternative(ProductionPattern pattern,
                                ProductionPatternAlternative alt) {

        try {
            pattern.AddAlternative(alt);
        } catch (ParserCreationException e) {
            Fail("couldn't add alternative to " + pattern.Name +
                 ": " + e.Message);
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
