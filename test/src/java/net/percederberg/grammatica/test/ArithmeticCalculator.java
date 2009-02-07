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

package net.percederberg.grammatica.test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import net.percederberg.grammatica.parser.Node;
import net.percederberg.grammatica.parser.ParseException;
import net.percederberg.grammatica.parser.Production;
import net.percederberg.grammatica.parser.Token;

/**
 * A simple arithmetic calculator.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
class ArithmeticCalculator extends ArithmeticAnalyzer {

    /**
     * The map with all variable names and values.
     */
    private HashMap variables;

    /**
     * Creates a new arithmetic calculator.
     */
    public ArithmeticCalculator() {
        this(new HashMap());
    }

    /**
     * Creates a new arithmetic calculator.
     *
     * @param variables      the variable bindings to use
     */
    public ArithmeticCalculator(HashMap variables) {
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
    public int calculate(String expression) throws Exception {
        ArithmeticParser  parser;
        Node              node;

        parser = new ArithmeticParser(new StringReader(expression), this);
        parser.prepare();
        node = parser.parse();
        return ((Integer) node.getValue(0)).intValue();
    }

    /**
     * Adds the addition operator as a node value.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree
     */
    protected Node exitAdd(Token node) {
        node.addValue("+");
        return node;
    }

    /**
     * Adds the subtraction operator as a node value.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree
     */
    protected Node exitSub(Token node) {
        node.addValue("-");
        return node;
    }

    /**
     * Adds the multiplication operator as a node value.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree
     */
    protected Node exitMul(Token node) {
        node.addValue("*");
        return node;
    }

    /**
     * Adds the division operator as a node value.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree
     */
    protected Node exitDiv(Token node) {
        node.addValue("/");
        return node;
    }

    /**
     * Adds the number as a node value.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree
     */
    protected Node exitNumber(Token node) {
        node.addValue(new Integer(node.getImage()));
        return node;
    }

    /**
     * Adds the identifier value as a node value.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree
     */
    protected Node exitIdentifier(Token node) {
        node.addValue(variables.get(node.getImage()));
        return node;
    }

    /**
     * Adds the expression result as a node value.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree
     */
    protected Node exitExpression(Production node) {
        ArrayList  values = getChildValues(node);
        Integer    value1;
        Integer    value2;
        String     op;
        int        result;

        if (values.size() == 1) {
            result = ((Integer) values.get(0)).intValue();
        } else {
            value1 = (Integer) values.get(0);
            value2 = (Integer) values.get(2);
            op = (String) values.get(1);
            result = operate(op, value1, value2);
        }
        node.addValue(new Integer(result));
        return node;
    }

    /**
     * Adds the child values as node values.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree
     */
    protected Node exitExpressionRest(Production node) {
        node.addValues(getChildValues(node));
        return node;
    }

    /**
     * Adds the term result as a node value.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree
     */
    protected Node exitTerm(Production node) {
        ArrayList  values = getChildValues(node);
        Integer    value1;
        Integer    value2;
        String     op;
        int        result;

        if (values.size() == 1) {
            result = ((Integer) values.get(0)).intValue();
        } else {
            value1 = (Integer) values.get(0);
            value2 = (Integer) values.get(2);
            op = (String) values.get(1);
            result = operate(op, value1, value2);
        }
        node.addValue(new Integer(result));
        return node;
    }

    /**
     * Adds the child values as node values.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree
     */
    protected Node exitTermRest(Production node) {
        node.addValues(getChildValues(node));
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
    protected Node exitFactor(Production node) throws ParseException {
        int  result;

        if (node.getChildCount() == 1) {
            result = getIntValue(getChildAt(node, 0), 0);
        } else {
            result = getIntValue(getChildAt(node, 1), 0);
        }
        node.addValue(new Integer(result));
        return node;
    }

    /**
     * Adds the child values as node values.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree
     */
    protected Node exitAtom(Production node) {
        node.addValues(getChildValues(node));
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
     */
    private int operate(String op, Integer value1, Integer value2) {
        int  i = value1.intValue();
        int  j = value2.intValue();

        switch (op.charAt(0)) {
        case '+':
            return i + j;
        case '-':
            return i - j;
        case '*':
            return i * j;
        case '/':
            return i / j;
        default:
            throw new RuntimeException("unknown operator: " + op);
        }
    }
}
