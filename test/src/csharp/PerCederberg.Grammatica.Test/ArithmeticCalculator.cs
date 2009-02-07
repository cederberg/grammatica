/*
 * ArithmeticCalculator.java
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
using System.Collections;
using System.IO;
using PerCederberg.Grammatica.Runtime;

namespace PerCederberg.Grammatica.Test {

    /**
     * A simple arithmetic calculator.
     *
     * @author   Per Cederberg, <per at percederberg dot net>
     * @version  1.0
     */
    internal class ArithmeticCalculator : ArithmeticAnalyzer {

        /**
         * The map with all variable names and values.
         */
        private Hashtable variables;

        /**
         * Creates a new arithmetic calculator.
         */
        public ArithmeticCalculator()
        	: this(new Hashtable()) {
        }

        /**
         * Creates a new arithmetic calculator.
         *
         * @param variables      the variable bindings to use
         */
        public ArithmeticCalculator(Hashtable variables) {
            this.variables = variables;
        }

        /**
         * Calculates the numeric value of an expression.
         *
         * @param expression     the expression to calculate
         *
         * @return the numeric value of the expression
         *
         * @throws Exception if the expression contained an error
         */
        public int Calculate(string expression) {
            ArithmeticParser  parser;
            Node              node;

            parser = new ArithmeticParser(new StringReader(expression), this);
            parser.Prepare();
            node = parser.Parse();
            return (int) node.Values[0];
        }

        /**
         * Adds the addition operator as a node value.
         *
         * @param node           the node being exited
         *
         * @return the node to add to the parse tree
         */
        public override Node ExitAdd(Token node) {
            node.Values.Add("+");
            return node;
        }

        /**
         * Adds the subtraction operator as a node value.
         *
         * @param node           the node being exited
         *
         * @return the node to add to the parse tree
         */
        public override Node ExitSub(Token node) {
            node.Values.Add("-");
            return node;
        }

        /**
         * Adds the multiplication operator as a node value.
         *
         * @param node           the node being exited
         *
         * @return the node to add to the parse tree
         */
        public override Node ExitMul(Token node) {
            node.Values.Add("*");
            return node;
        }

        /**
         * Adds the division operator as a node value.
         *
         * @param node           the node being exited
         *
         * @return the node to add to the parse tree
         */
        public override Node ExitDiv(Token node) {
            node.Values.Add("/");
            return node;
        }

        /**
         * Adds the number as a node value.
         *
         * @param node           the node being exited
         *
         * @return the node to add to the parse tree
         */
        public override Node ExitNumber(Token node) {
            node.Values.Add(Int32.Parse(node.Image));
            return node;
        }

        /**
         * Adds the identifier value as a node value.
         *
         * @param node           the node being exited
         *
         * @return the node to add to the parse tree
         */
        public override Node ExitIdentifier(Token node) {
            node.Values.Add(variables[node.Image]);
            return node;
        }

        /**
         * Adds the expression result as a node value.
         *
         * @param node           the node being exited
         *
         * @return the node to add to the parse tree
         */
        public override Node ExitExpression(Production node) {
            ArrayList  values = GetChildValues(node);
            int        value1;
            int        value2;
            string     op;
            int        result;

            if (values.Count == 1) {
                result = (int) values[0];
            } else {
                value1 = (int) values[0];
                value2 = (int) values[2];
                op = (string) values[1];
                result = Operate(op, value1, value2);
            }
            node.Values.Add(result);
            return node;
        }

        /**
         * Adds the child values as node values.
         *
         * @param node           the node being exited
         *
         * @return the node to add to the parse tree
         */
        public override Node ExitExpressionRest(Production node) {
            node.Values.AddRange(GetChildValues(node));
            return node;
        }

        /**
         * Adds the term result as a node value.
         *
         * @param node           the node being exited
         *
         * @return the node to add to the parse tree
         */
        public override Node ExitTerm(Production node) {
            ArrayList  values = GetChildValues(node);
            int        value1;
            int        value2;
            string     op;
            int        result;

            if (values.Count == 1) {
                result = (int) values[0];
            } else {
                value1 = (int) values[0];
                value2 = (int) values[2];
                op = (string) values[1];
                result = Operate(op, value1, value2);
            }
            node.Values.Add(result);
            return node;
        }

        /**
         * Adds the child values as node values.
         *
         * @param node           the node being exited
         *
         * @return the node to add to the parse tree
         */
        public override Node ExitTermRest(Production node) {
            node.Values.AddRange(GetChildValues(node));
            return node;
        }

        /**
         * Adds the factor value as a node value.
         *
         * @param node           the node being exited
         *
         * @return the node to add to the parse tree
         *
         * @throws ParseException if the node analysis discovered errors
         */
        public override Node ExitFactor(Production node) {
            int  result;

            if (node.Count == 1) {
                result = GetIntValue(GetChildAt(node, 0), 0);
            } else {
                result = GetIntValue(GetChildAt(node, 1), 0);
            }
            node.Values.Add(result);
            return node;
        }

        /**
         * Adds the child values as node values.
         *
         * @param node           the node being exited
         *
         * @return the node to add to the parse tree
         */
        public override Node ExitAtom(Production node) {
            node.Values.AddRange(GetChildValues(node));
            return node;
        }

        /**
         * Performs a numerical operation.
         *
         * @param op             the operator to use
         * @param value1         the first value
         * @param value2         the second value
         *
         * @return the result of performing the operation
         *
         * @throws Exception if the operator was unknown
         */
        private int Operate(string op, int value1, int value2) {
            switch (op[0]) {
            case '+':
                return value1 + value2;
            case '-':
                return value1 - value2;
            case '*':
                return value1 * value2;
            case '/':
                return value1 / value2;
            default:
                throw new Exception("unknown operator: " + op);
            }
        }
    }
}
