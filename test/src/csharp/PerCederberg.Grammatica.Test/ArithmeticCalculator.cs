/*
 * ArithmeticCalculator.java
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
using System.IO;
using PerCederberg.Grammatica.Parser;

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
            return (int) node.GetValue(0);
        }

        /**
         * Adds the addition operator as a node value.
         * 
         * @param node           the node being exited
         * 
         * @return the node to add to the parse tree
         */
        public override Node ExitAdd(Token node) {
            node.AddValue("+");
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
            node.AddValue("-");
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
            node.AddValue("*");
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
            node.AddValue("/");
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
            node.AddValue(Int32.Parse(node.GetImage()));
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
            node.AddValue(variables[node.GetImage()]);
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
            node.AddValue(result);
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
            node.AddValues(GetChildValues(node));
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
            node.AddValue(result);
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
            node.AddValues(GetChildValues(node));
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
        
            if (node.GetChildCount() == 1) {
                result = GetIntValue(GetChildAt(node, 0), 0);
            } else {
                result = GetIntValue(GetChildAt(node, 1), 0);
            }
            node.AddValue(result);
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
            node.AddValues(GetChildValues(node));
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
