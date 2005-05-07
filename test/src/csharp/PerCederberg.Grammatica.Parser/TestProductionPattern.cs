/*
 * TestProductionPattern.cs
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
 * Copyright (c) 2003-2005 Per Cederberg. All rights reserved.
 */

using System;
using PerCederberg.Grammatica.Parser;

/**
 * A test case for the ProductionPattern class.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
public class TestProductionPattern {

    /**
     * A token constant.
     */
    private const int T1 = 1001;

    /**
     * A token constant.
     */
    private const int T2 = 1002;

    /**
     * A production constant.
     */
    private const int P1 = 2001;

    /**
     * A production constant.
     */
    private const int P2 = 2002;

    /**
     * The production pattern variable used in tests.
     */
    private ProductionPattern pattern;

    /**
     * The production pattern alternative variable used in tests.
     */
    private ProductionPatternAlternative alt;

    /**
     * Tests the pattern left-recursive detection.
     */
    public void TestLeftRecursive() {
        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.AddProduction(P2, 0, -1);
        alt.AddToken(T1, 0, 1);
        alt.AddProduction(P1, 0, 1);
        alt.AddToken(T2, 1, 1);
        AddAlternative(pattern, alt);
        AssertTrue(pattern.IsLeftRecursive());
    }

    /**
     * Tests the pattern right-recursive detection.
     */
    public void TestRightRecursive() {
        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.AddToken(T2, 1, 1);
        alt.AddProduction(P1, 0, 1);
        alt.AddProduction(P2, 0, -1);
        alt.AddToken(T1, 0, 1);
        AddAlternative(pattern, alt);
        AssertTrue(pattern.IsRightRecursive());
    }

    /**
     * Tests the pattern empty matching detection.
     */
    public void TestMatchingEmpty() {
        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.AddProduction(P2, 0, -1);
        alt.AddToken(T1, 0, 1);
        alt.AddProduction(P1, 0, 1);
        AddAlternative(pattern, alt);
        AssertTrue(pattern.IsMatchingEmpty());
    }

    /**
     * Tests adding a single pattern alternative twice.
     */
    public void TestDuplicateAlternative() {
        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.AddToken(T1, 1, 1);
        AddAlternative(pattern, alt);
        FailAddAlternative(pattern, alt);
    }

    /**
     * Tests adding a duplicate pattern alternative.
     */
    public void TestIdenticalAlternative() {
        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.AddToken(T1, 1, 1);
        AddAlternative(pattern, alt);
        alt = new ProductionPatternAlternative();
        alt.AddToken(T1, 1, 1);
        FailAddAlternative(pattern, alt);
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
     * Adds a pattern alternative. This method reports a test failure
     * if no exception was thrown.
     *
     * @param pattern        the production pattern
     * @param alt            the pattern alternative to add
     */
    private void FailAddAlternative(ProductionPattern pattern,
                                    ProductionPatternAlternative alt) {

        try {
            pattern.AddAlternative(alt);
            Fail("could add alternative to " + pattern.Name);
        } catch (ParserCreationException e) {
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
     * Throws a test fail exception if the specified value isn't true.
     *
     * @param value          the value to test
     */
    private void AssertTrue(bool value) {
        if (!value) {
            Fail("assertion failure");
        }
    }
}
