/*
 * ArithmeticAnalyzer.java
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
 * Copyright (c) 2003 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.test;

import net.percederberg.grammatica.parser.Analyzer;
import net.percederberg.grammatica.parser.Node;
import net.percederberg.grammatica.parser.ParseException;
import net.percederberg.grammatica.parser.Production;
import net.percederberg.grammatica.parser.Token;

/**
 * A class providing callback methods for the parser.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
abstract class ArithmeticAnalyzer extends Analyzer {

    /**
     * Called when entering a parse tree node.
     *
     * @param node           the node being entered
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enter(Node node) throws ParseException {
        switch (node.getId()) {
        case ArithmeticConstants.ADD:
            enterAdd((Token) node);
            break;
        case ArithmeticConstants.SUB:
            enterSub((Token) node);
            break;
        case ArithmeticConstants.MUL:
            enterMul((Token) node);
            break;
        case ArithmeticConstants.DIV:
            enterDiv((Token) node);
            break;
        case ArithmeticConstants.LEFT_PAREN:
            enterLeftParen((Token) node);
            break;
        case ArithmeticConstants.RIGHT_PAREN:
            enterRightParen((Token) node);
            break;
        case ArithmeticConstants.NUMBER:
            enterNumber((Token) node);
            break;
        case ArithmeticConstants.IDENTIFIER:
            enterIdentifier((Token) node);
            break;
        case ArithmeticConstants.EXPRESSION:
            enterExpression((Production) node);
            break;
        case ArithmeticConstants.EXPRESSION_REST:
            enterExpressionRest((Production) node);
            break;
        case ArithmeticConstants.TERM:
            enterTerm((Production) node);
            break;
        case ArithmeticConstants.TERM_REST:
            enterTermRest((Production) node);
            break;
        case ArithmeticConstants.FACTOR:
            enterFactor((Production) node);
            break;
        case ArithmeticConstants.ATOM:
            enterAtom((Production) node);
            break;
        }
    }

    /**
     * Called when exiting a parse tree node.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exit(Node node) throws ParseException {
        switch (node.getId()) {
        case ArithmeticConstants.ADD:
            return exitAdd((Token) node);
        case ArithmeticConstants.SUB:
            return exitSub((Token) node);
        case ArithmeticConstants.MUL:
            return exitMul((Token) node);
        case ArithmeticConstants.DIV:
            return exitDiv((Token) node);
        case ArithmeticConstants.LEFT_PAREN:
            return exitLeftParen((Token) node);
        case ArithmeticConstants.RIGHT_PAREN:
            return exitRightParen((Token) node);
        case ArithmeticConstants.NUMBER:
            return exitNumber((Token) node);
        case ArithmeticConstants.IDENTIFIER:
            return exitIdentifier((Token) node);
        case ArithmeticConstants.EXPRESSION:
            return exitExpression((Production) node);
        case ArithmeticConstants.EXPRESSION_REST:
            return exitExpressionRest((Production) node);
        case ArithmeticConstants.TERM:
            return exitTerm((Production) node);
        case ArithmeticConstants.TERM_REST:
            return exitTermRest((Production) node);
        case ArithmeticConstants.FACTOR:
            return exitFactor((Production) node);
        case ArithmeticConstants.ATOM:
            return exitAtom((Production) node);
        }
        return node;
    }

    /**
     * Called when adding a child to a parse tree node.
     *
     * @param node           the parent node
     * @param child          the child node, or null
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected void child(Production node, Node child)
        throws ParseException {

        switch (node.getId()) {
        case ArithmeticConstants.EXPRESSION:
            childExpression(node, child);
            break;
        case ArithmeticConstants.EXPRESSION_REST:
            childExpressionRest(node, child);
            break;
        case ArithmeticConstants.TERM:
            childTerm(node, child);
            break;
        case ArithmeticConstants.TERM_REST:
            childTermRest(node, child);
            break;
        case ArithmeticConstants.FACTOR:
            childFactor(node, child);
            break;
        case ArithmeticConstants.ATOM:
            childAtom(node, child);
            break;
        }
    }

    /**
     * Called when entering a parse tree node.
     *
     * @param node           the node being entered
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterAdd(Token node) throws ParseException {
    }

    /**
     * Called when exiting a parse tree node.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitAdd(Token node) throws ParseException {
        return node;
    }

    /**
     * Called when entering a parse tree node.
     *
     * @param node           the node being entered
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterSub(Token node) throws ParseException {
    }

    /**
     * Called when exiting a parse tree node.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitSub(Token node) throws ParseException {
        return node;
    }

    /**
     * Called when entering a parse tree node.
     *
     * @param node           the node being entered
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterMul(Token node) throws ParseException {
    }

    /**
     * Called when exiting a parse tree node.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitMul(Token node) throws ParseException {
        return node;
    }

    /**
     * Called when entering a parse tree node.
     *
     * @param node           the node being entered
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterDiv(Token node) throws ParseException {
    }

    /**
     * Called when exiting a parse tree node.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitDiv(Token node) throws ParseException {
        return node;
    }

    /**
     * Called when entering a parse tree node.
     *
     * @param node           the node being entered
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterLeftParen(Token node) throws ParseException {
    }

    /**
     * Called when exiting a parse tree node.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitLeftParen(Token node) throws ParseException {
        return node;
    }

    /**
     * Called when entering a parse tree node.
     *
     * @param node           the node being entered
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterRightParen(Token node) throws ParseException {
    }

    /**
     * Called when exiting a parse tree node.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitRightParen(Token node) throws ParseException {
        return node;
    }

    /**
     * Called when entering a parse tree node.
     *
     * @param node           the node being entered
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterNumber(Token node) throws ParseException {
    }

    /**
     * Called when exiting a parse tree node.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitNumber(Token node) throws ParseException {
        return node;
    }

    /**
     * Called when entering a parse tree node.
     *
     * @param node           the node being entered
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterIdentifier(Token node) throws ParseException {
    }

    /**
     * Called when exiting a parse tree node.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitIdentifier(Token node) throws ParseException {
        return node;
    }

    /**
     * Called when entering a parse tree node.
     *
     * @param node           the node being entered
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterExpression(Production node)
        throws ParseException {
    }

    /**
     * Called when exiting a parse tree node.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitExpression(Production node)
        throws ParseException {

        return node;
    }

    /**
     * Called when adding a child to a parse tree node.
     *
     * @param node           the parent node
     * @param child          the child node, or null
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected void childExpression(Production node, Node child)
        throws ParseException {

        node.addChild(child);
    }

    /**
     * Called when entering a parse tree node.
     *
     * @param node           the node being entered
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterExpressionRest(Production node)
        throws ParseException {
    }

    /**
     * Called when exiting a parse tree node.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitExpressionRest(Production node)
        throws ParseException {

        return node;
    }

    /**
     * Called when adding a child to a parse tree node.
     *
     * @param node           the parent node
     * @param child          the child node, or null
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected void childExpressionRest(Production node, Node child)
        throws ParseException {

        node.addChild(child);
    }

    /**
     * Called when entering a parse tree node.
     *
     * @param node           the node being entered
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterTerm(Production node) throws ParseException {
    }

    /**
     * Called when exiting a parse tree node.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitTerm(Production node) throws ParseException {
        return node;
    }

    /**
     * Called when adding a child to a parse tree node.
     *
     * @param node           the parent node
     * @param child          the child node, or null
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected void childTerm(Production node, Node child)
        throws ParseException {

        node.addChild(child);
    }

    /**
     * Called when entering a parse tree node.
     *
     * @param node           the node being entered
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterTermRest(Production node)
        throws ParseException {
    }

    /**
     * Called when exiting a parse tree node.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitTermRest(Production node) throws ParseException {
        return node;
    }

    /**
     * Called when adding a child to a parse tree node.
     *
     * @param node           the parent node
     * @param child          the child node, or null
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected void childTermRest(Production node, Node child)
        throws ParseException {

        node.addChild(child);
    }

    /**
     * Called when entering a parse tree node.
     *
     * @param node           the node being entered
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterFactor(Production node) throws ParseException {
    }

    /**
     * Called when exiting a parse tree node.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitFactor(Production node) throws ParseException {
        return node;
    }

    /**
     * Called when adding a child to a parse tree node.
     *
     * @param node           the parent node
     * @param child          the child node, or null
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected void childFactor(Production node, Node child)
        throws ParseException {

        node.addChild(child);
    }

    /**
     * Called when entering a parse tree node.
     *
     * @param node           the node being entered
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enterAtom(Production node) throws ParseException {
    }

    /**
     * Called when exiting a parse tree node.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitAtom(Production node) throws ParseException {
        return node;
    }

    /**
     * Called when adding a child to a parse tree node.
     *
     * @param node           the parent node
     * @param child          the child node, or null
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected void childAtom(Production node, Node child)
        throws ParseException {

        node.addChild(child);
    }
}
