/*
 * TestArithmeticCalculator.cs
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

using System;
using System.Collections;

namespace PerCederberg.Grammatica.Test {

    /**
     * A test case for the ArithmeticCalculator class.
     *
     * @author   Per Cederberg, <per at percederberg dot net>
     * @version  1.0
     */
    public class TestArithmeticCalculator {

        /**
         * The valid input string.
         */
        private const string VALID_INPUT =
            "1 + 2*a\n" +
            " + 345";

        /**
         * The variable bindings.
         */
        private Hashtable variables = new Hashtable();

        /**
         * Creates a new test case.
         */
        public TestArithmeticCalculator() {
            variables.Add("a", 2);
        }

        /**
         * Tests the calculator with a valid expression.
         */
        public void TestValidExpression() {
            Calculate(VALID_INPUT, 350);
        }

        /**
         * Calculates an expression and checks the result. If the
         * calculation failed or if the result didn't match the specified
         * one, a test failure will be reported.
         *
         * @param expr           the expression to use
         * @param result         the result to expect
         */
        private void Calculate(string expr, int result) {
            ArithmeticCalculator  calc;

            try {
                calc = new ArithmeticCalculator(variables);
                AssertEquals("expression result", result, calc.Calculate(expr));
            } catch (Exception e) {
                Fail(e.Message);
            }
        }

        /**
         * Throws a test fail exception.
         *
         * @param message         the test error message
         */
        protected void Fail(string message) {
            throw new Exception(message);
        }

        /**
         * Checks that two values are identical. If the values are not
         * identical, a test failure will be reported.
         *
         * @param label          the error label
         * @param expected       the expected value
         * @param result         the obtained value
         */
        protected void AssertEquals(string label,
                                    int expected,
                                    int result) {

            if (expected != result) {
                Fail(label + ", expected: " + expected +
                     ", found: " + result);
            }
        }
    }
}
