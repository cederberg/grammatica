/*
 * TestProductionPattern.java
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
 * A test case for the ProductionPattern class.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
public class TestProductionPattern extends TestCase {

    /**
     * A token constant.
     */
    private static final int T1 = 1001;

    /**
     * A token constant.
     */
    private static final int T2 = 1002;

    /**
     * A production constant.
     */
    private static final int P1 = 2001;

    /**
     * A production constant.
     */
    private static final int P2 = 2002;

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
    public void testLeftRecursive() {
        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.addProduction(P2, 0, -1);
        alt.addToken(T1, 0, 1);
        alt.addProduction(P1, 0, 1);
        alt.addToken(T2, 1, 1);
        addAlternative(pattern, alt);
        assertTrue(pattern.isLeftRecursive());
    }

    /**
     * Tests the pattern right-recursive detection.
     */
    public void testRightRecursive() {
        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.addToken(T2, 1, 1);
        alt.addProduction(P1, 0, 1);
        alt.addProduction(P2, 0, -1);
        alt.addToken(T1, 0, 1);
        addAlternative(pattern, alt);
        assertTrue(pattern.isRightRecursive());
    }

    /**
     * Tests the pattern empty matching detection.
     */
    public void testMatchingEmpty() {
        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.addProduction(P2, 0, -1);
        alt.addToken(T1, 0, 1);
        alt.addProduction(P1, 0, 1);
        addAlternative(pattern, alt);
        assertTrue(pattern.isMatchingEmpty());
    }

    /**
     * Tests adding a single pattern alternative twice.
     */
    public void testDuplicateAlternative() {
        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.addToken(T1, 1, 1);
        addAlternative(pattern, alt);
        failAddAlternative(pattern, alt);
    }

    /**
     * Tests adding a duplicate pattern alternative.
     */
    public void testIdenticalAlternative() {
        pattern = new ProductionPattern(P1, "P1");
        alt = new ProductionPatternAlternative();
        alt.addToken(T1, 1, 1);
        addAlternative(pattern, alt);
        alt = new ProductionPatternAlternative();
        alt.addToken(T1, 1, 1);
        failAddAlternative(pattern, alt);
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

    /**
     * Adds a pattern alternative. This method reports a test failure
     * if no exception was thrown.
     *
     * @param pattern        the production pattern
     * @param alt            the pattern alternative to add
     */
    private void failAddAlternative(ProductionPattern pattern,
                                    ProductionPatternAlternative alt) {

        try {
            pattern.addAlternative(alt);
            fail("could add alternative to " + pattern.getName());
        } catch (ParserCreationException e) {
            // Failure was expected
        }
    }
}
