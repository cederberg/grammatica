/*
 * ArithmeticAnalyzer.cs
 * 
 * THIS FILE HAS BEEN GENERATED AUTOMATICALLY. DO NOT EDIT!
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

using PerCederberg.Grammatica.Parser;

namespace PerCederberg.Grammatica.Test {

    /**
     * <remarks>A class providing callback methods for the
     * parser.</remarks>
     */
    internal abstract class ArithmeticAnalyzer : Analyzer {

        /**
         * <summary>Called when entering a parse tree node.</summary>
         * 
         * <param name='node'>the node being entered</param>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public void enter(Node node) {
            switch (node.GetId()) {
            case (int) ArithmeticConstants.ADD:
                enterAdd((Token) node);
                break;
            case (int) ArithmeticConstants.SUB:
                enterSub((Token) node);
                break;
            case (int) ArithmeticConstants.MUL:
                enterMul((Token) node);
                break;
            case (int) ArithmeticConstants.DIV:
                enterDiv((Token) node);
                break;
            case (int) ArithmeticConstants.LEFT_PAREN:
                enterLeftParen((Token) node);
                break;
            case (int) ArithmeticConstants.RIGHT_PAREN:
                enterRightParen((Token) node);
                break;
            case (int) ArithmeticConstants.NUMBER:
                enterNumber((Token) node);
                break;
            case (int) ArithmeticConstants.IDENTIFIER:
                enterIdentifier((Token) node);
                break;
            case (int) ArithmeticConstants.EXPRESSION:
                enterExpression((Production) node);
                break;
            case (int) ArithmeticConstants.EXPRESSION_REST:
                enterExpressionRest((Production) node);
                break;
            case (int) ArithmeticConstants.TERM:
                enterTerm((Production) node);
                break;
            case (int) ArithmeticConstants.TERM_REST:
                enterTermRest((Production) node);
                break;
            case (int) ArithmeticConstants.FACTOR:
                enterFactor((Production) node);
                break;
            case (int) ArithmeticConstants.ATOM:
                enterAtom((Production) node);
                break;
            }
        }

        /**
         * <summary>Called when exiting a parse tree node.</summary>
         * 
         * <param name='node'>the node being exited</param>
         * 
         * <returns>the node to add to the parse tree</returns>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public Node exit(Node node) {
            switch (node.GetId()) {
            case (int) ArithmeticConstants.ADD:
                return exitAdd((Token) node);
            case (int) ArithmeticConstants.SUB:
                return exitSub((Token) node);
            case (int) ArithmeticConstants.MUL:
                return exitMul((Token) node);
            case (int) ArithmeticConstants.DIV:
                return exitDiv((Token) node);
            case (int) ArithmeticConstants.LEFT_PAREN:
                return exitLeftParen((Token) node);
            case (int) ArithmeticConstants.RIGHT_PAREN:
                return exitRightParen((Token) node);
            case (int) ArithmeticConstants.NUMBER:
                return exitNumber((Token) node);
            case (int) ArithmeticConstants.IDENTIFIER:
                return exitIdentifier((Token) node);
            case (int) ArithmeticConstants.EXPRESSION:
                return exitExpression((Production) node);
            case (int) ArithmeticConstants.EXPRESSION_REST:
                return exitExpressionRest((Production) node);
            case (int) ArithmeticConstants.TERM:
                return exitTerm((Production) node);
            case (int) ArithmeticConstants.TERM_REST:
                return exitTermRest((Production) node);
            case (int) ArithmeticConstants.FACTOR:
                return exitFactor((Production) node);
            case (int) ArithmeticConstants.ATOM:
                return exitAtom((Production) node);
            }
            return node;
        }

        /**
         * <summary>Called when adding a child to a parse tree
         * node.</summary>
         * 
         * <param name='node'>the parent node</param>
         * <param name='child'>the child node, or null</param>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public void child(Production node, Node child) {
            switch (node.GetId()) {
            case (int) ArithmeticConstants.EXPRESSION:
                childExpression(node, child);
                break;
            case (int) ArithmeticConstants.EXPRESSION_REST:
                childExpressionRest(node, child);
                break;
            case (int) ArithmeticConstants.TERM:
                childTerm(node, child);
                break;
            case (int) ArithmeticConstants.TERM_REST:
                childTermRest(node, child);
                break;
            case (int) ArithmeticConstants.FACTOR:
                childFactor(node, child);
                break;
            case (int) ArithmeticConstants.ATOM:
                childAtom(node, child);
                break;
            }
        }

        /**
         * <summary>Called when entering a parse tree node.</summary>
         * 
         * <param name='node'>the node being entered</param>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public void enterAdd(Token node) {
        }

        /**
         * <summary>Called when exiting a parse tree node.</summary>
         * 
         * <param name='node'>the node being exited</param>
         * 
         * <returns>the node to add to the parse tree</returns>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public Node exitAdd(Token node) {
            return node;
        }

        /**
         * <summary>Called when entering a parse tree node.</summary>
         * 
         * <param name='node'>the node being entered</param>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public void enterSub(Token node) {
        }

        /**
         * <summary>Called when exiting a parse tree node.</summary>
         * 
         * <param name='node'>the node being exited</param>
         * 
         * <returns>the node to add to the parse tree</returns>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public Node exitSub(Token node) {
            return node;
        }

        /**
         * <summary>Called when entering a parse tree node.</summary>
         * 
         * <param name='node'>the node being entered</param>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public void enterMul(Token node) {
        }

        /**
         * <summary>Called when exiting a parse tree node.</summary>
         * 
         * <param name='node'>the node being exited</param>
         * 
         * <returns>the node to add to the parse tree</returns>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public Node exitMul(Token node) {
            return node;
        }

        /**
         * <summary>Called when entering a parse tree node.</summary>
         * 
         * <param name='node'>the node being entered</param>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public void enterDiv(Token node) {
        }

        /**
         * <summary>Called when exiting a parse tree node.</summary>
         * 
         * <param name='node'>the node being exited</param>
         * 
         * <returns>the node to add to the parse tree</returns>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public Node exitDiv(Token node) {
            return node;
        }

        /**
         * <summary>Called when entering a parse tree node.</summary>
         * 
         * <param name='node'>the node being entered</param>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public void enterLeftParen(Token node) {
        }

        /**
         * <summary>Called when exiting a parse tree node.</summary>
         * 
         * <param name='node'>the node being exited</param>
         * 
         * <returns>the node to add to the parse tree</returns>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public Node exitLeftParen(Token node) {
            return node;
        }

        /**
         * <summary>Called when entering a parse tree node.</summary>
         * 
         * <param name='node'>the node being entered</param>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public void enterRightParen(Token node) {
        }

        /**
         * <summary>Called when exiting a parse tree node.</summary>
         * 
         * <param name='node'>the node being exited</param>
         * 
         * <returns>the node to add to the parse tree</returns>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public Node exitRightParen(Token node) {
            return node;
        }

        /**
         * <summary>Called when entering a parse tree node.</summary>
         * 
         * <param name='node'>the node being entered</param>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public void enterNumber(Token node) {
        }

        /**
         * <summary>Called when exiting a parse tree node.</summary>
         * 
         * <param name='node'>the node being exited</param>
         * 
         * <returns>the node to add to the parse tree</returns>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public Node exitNumber(Token node) {
            return node;
        }

        /**
         * <summary>Called when entering a parse tree node.</summary>
         * 
         * <param name='node'>the node being entered</param>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public void enterIdentifier(Token node) {
        }

        /**
         * <summary>Called when exiting a parse tree node.</summary>
         * 
         * <param name='node'>the node being exited</param>
         * 
         * <returns>the node to add to the parse tree</returns>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public Node exitIdentifier(Token node) {
            return node;
        }

        /**
         * <summary>Called when entering a parse tree node.</summary>
         * 
         * <param name='node'>the node being entered</param>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public void enterExpression(Production node) {
        }

        /**
         * <summary>Called when exiting a parse tree node.</summary>
         * 
         * <param name='node'>the node being exited</param>
         * 
         * <returns>the node to add to the parse tree</returns>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public Node exitExpression(Production node) {
            return node;
        }

        /**
         * <summary>Called when adding a child to a parse tree
         * node.</summary>
         * 
         * <param name='node'>the parent node</param>
         * <param name='child'>the child node, or null</param>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public void childExpression(Production node, Node child) {
            node.AddChild(child);
        }

        /**
         * <summary>Called when entering a parse tree node.</summary>
         * 
         * <param name='node'>the node being entered</param>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public void enterExpressionRest(Production node) {
        }

        /**
         * <summary>Called when exiting a parse tree node.</summary>
         * 
         * <param name='node'>the node being exited</param>
         * 
         * <returns>the node to add to the parse tree</returns>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public Node exitExpressionRest(Production node) {
            return node;
        }

        /**
         * <summary>Called when adding a child to a parse tree
         * node.</summary>
         * 
         * <param name='node'>the parent node</param>
         * <param name='child'>the child node, or null</param>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public void childExpressionRest(Production node, Node child) {
            node.AddChild(child);
        }

        /**
         * <summary>Called when entering a parse tree node.</summary>
         * 
         * <param name='node'>the node being entered</param>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public void enterTerm(Production node) {
        }

        /**
         * <summary>Called when exiting a parse tree node.</summary>
         * 
         * <param name='node'>the node being exited</param>
         * 
         * <returns>the node to add to the parse tree</returns>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public Node exitTerm(Production node) {
            return node;
        }

        /**
         * <summary>Called when adding a child to a parse tree
         * node.</summary>
         * 
         * <param name='node'>the parent node</param>
         * <param name='child'>the child node, or null</param>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public void childTerm(Production node, Node child) {
            node.AddChild(child);
        }

        /**
         * <summary>Called when entering a parse tree node.</summary>
         * 
         * <param name='node'>the node being entered</param>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public void enterTermRest(Production node) {
        }

        /**
         * <summary>Called when exiting a parse tree node.</summary>
         * 
         * <param name='node'>the node being exited</param>
         * 
         * <returns>the node to add to the parse tree</returns>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public Node exitTermRest(Production node) {
            return node;
        }

        /**
         * <summary>Called when adding a child to a parse tree
         * node.</summary>
         * 
         * <param name='node'>the parent node</param>
         * <param name='child'>the child node, or null</param>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public void childTermRest(Production node, Node child) {
            node.AddChild(child);
        }

        /**
         * <summary>Called when entering a parse tree node.</summary>
         * 
         * <param name='node'>the node being entered</param>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public void enterFactor(Production node) {
        }

        /**
         * <summary>Called when exiting a parse tree node.</summary>
         * 
         * <param name='node'>the node being exited</param>
         * 
         * <returns>the node to add to the parse tree</returns>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public Node exitFactor(Production node) {
            return node;
        }

        /**
         * <summary>Called when adding a child to a parse tree
         * node.</summary>
         * 
         * <param name='node'>the parent node</param>
         * <param name='child'>the child node, or null</param>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public void childFactor(Production node, Node child) {
            node.AddChild(child);
        }

        /**
         * <summary>Called when entering a parse tree node.</summary>
         * 
         * <param name='node'>the node being entered</param>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public void enterAtom(Production node) {
        }

        /**
         * <summary>Called when exiting a parse tree node.</summary>
         * 
         * <param name='node'>the node being exited</param>
         * 
         * <returns>the node to add to the parse tree</returns>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public Node exitAtom(Production node) {
            return node;
        }

        /**
         * <summary>Called when adding a child to a parse tree
         * node.</summary>
         * 
         * <param name='node'>the parent node</param>
         * <param name='child'>the child node, or null</param>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public void childAtom(Production node, Node child) {
            node.AddChild(child);
        }
    }
}
