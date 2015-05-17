/*
 * TestArithmeticCalculator.java
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

import java.util.HashMap;

import junit.framework.TestCase;

/**
 * A test case for the ArithmeticCalculator class.
 *
 * @author   Per Cederberg
 * @version  1.0
 */
public class TestArithmeticCalculator extends TestCase {

    /**
     * The valid input string.
     */
    private static final String VALID_INPUT =
        "1 + 2*a\n" +
        " + 345";

    /**
     * The variable bindings.
     */
    private HashMap variables = new HashMap();

    /**
     * Creates a new test case.
     *
     * @param name           the test case name
     */
    public TestArithmeticCalculator(String name) {
        super(name);
        variables.put("a", new Integer(2));
    }

    /**
     * Tests the calculator with a valid expression.
     */
    public void testValidExpression() {
        calculate(VALID_INPUT, 350);
    }

    /**
     * Calculates an expression and checks the result. If the
     * calculation failed or if the result didn't match the specified
     * one, a test failure will be reported.
     *
     * @param expr           the expression to use
     * @param result         the result to expect
     */
    private void calculate(String expr, int result) {
        ArithmeticCalculator  calc;

        try {
            calc = new ArithmeticCalculator(variables);
            assertEquals("expression result", result, calc.calculate(expr));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
