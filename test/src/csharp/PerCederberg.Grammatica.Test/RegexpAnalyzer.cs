/*
 * RegexpAnalyzer.cs
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
    internal abstract class RegexpAnalyzer : Analyzer {

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
            case (int) RegexpConstants.LEFT_PAREN:
                enterLeftParen((Token) node);
                break;
            case (int) RegexpConstants.RIGHT_PAREN:
                enterRightParen((Token) node);
                break;
            case (int) RegexpConstants.LEFT_BRACKET:
                enterLeftBracket((Token) node);
                break;
            case (int) RegexpConstants.RIGHT_BRACKET:
                enterRightBracket((Token) node);
                break;
            case (int) RegexpConstants.LEFT_BRACE:
                enterLeftBrace((Token) node);
                break;
            case (int) RegexpConstants.RIGHT_BRACE:
                enterRightBrace((Token) node);
                break;
            case (int) RegexpConstants.QUESTION:
                enterQuestion((Token) node);
                break;
            case (int) RegexpConstants.ASTERISK:
                enterAsterisk((Token) node);
                break;
            case (int) RegexpConstants.PLUS:
                enterPlus((Token) node);
                break;
            case (int) RegexpConstants.VERTICAL_BAR:
                enterVerticalBar((Token) node);
                break;
            case (int) RegexpConstants.DOT:
                enterDot((Token) node);
                break;
            case (int) RegexpConstants.COMMA:
                enterComma((Token) node);
                break;
            case (int) RegexpConstants.NUMBER:
                enterNumber((Token) node);
                break;
            case (int) RegexpConstants.CHAR:
                enterChar((Token) node);
                break;
            case (int) RegexpConstants.EXPR:
                enterExpr((Production) node);
                break;
            case (int) RegexpConstants.TERM:
                enterTerm((Production) node);
                break;
            case (int) RegexpConstants.FACT:
                enterFact((Production) node);
                break;
            case (int) RegexpConstants.ATOM:
                enterAtom((Production) node);
                break;
            case (int) RegexpConstants.ATOM_MODIFIER:
                enterAtomModifier((Production) node);
                break;
            case (int) RegexpConstants.CHARACTER_SET:
                enterCharacterSet((Production) node);
                break;
            case (int) RegexpConstants.CHARACTER:
                enterCharacter((Production) node);
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
            case (int) RegexpConstants.LEFT_PAREN:
                return exitLeftParen((Token) node);
            case (int) RegexpConstants.RIGHT_PAREN:
                return exitRightParen((Token) node);
            case (int) RegexpConstants.LEFT_BRACKET:
                return exitLeftBracket((Token) node);
            case (int) RegexpConstants.RIGHT_BRACKET:
                return exitRightBracket((Token) node);
            case (int) RegexpConstants.LEFT_BRACE:
                return exitLeftBrace((Token) node);
            case (int) RegexpConstants.RIGHT_BRACE:
                return exitRightBrace((Token) node);
            case (int) RegexpConstants.QUESTION:
                return exitQuestion((Token) node);
            case (int) RegexpConstants.ASTERISK:
                return exitAsterisk((Token) node);
            case (int) RegexpConstants.PLUS:
                return exitPlus((Token) node);
            case (int) RegexpConstants.VERTICAL_BAR:
                return exitVerticalBar((Token) node);
            case (int) RegexpConstants.DOT:
                return exitDot((Token) node);
            case (int) RegexpConstants.COMMA:
                return exitComma((Token) node);
            case (int) RegexpConstants.NUMBER:
                return exitNumber((Token) node);
            case (int) RegexpConstants.CHAR:
                return exitChar((Token) node);
            case (int) RegexpConstants.EXPR:
                return exitExpr((Production) node);
            case (int) RegexpConstants.TERM:
                return exitTerm((Production) node);
            case (int) RegexpConstants.FACT:
                return exitFact((Production) node);
            case (int) RegexpConstants.ATOM:
                return exitAtom((Production) node);
            case (int) RegexpConstants.ATOM_MODIFIER:
                return exitAtomModifier((Production) node);
            case (int) RegexpConstants.CHARACTER_SET:
                return exitCharacterSet((Production) node);
            case (int) RegexpConstants.CHARACTER:
                return exitCharacter((Production) node);
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
            case (int) RegexpConstants.EXPR:
                childExpr(node, child);
                break;
            case (int) RegexpConstants.TERM:
                childTerm(node, child);
                break;
            case (int) RegexpConstants.FACT:
                childFact(node, child);
                break;
            case (int) RegexpConstants.ATOM:
                childAtom(node, child);
                break;
            case (int) RegexpConstants.ATOM_MODIFIER:
                childAtomModifier(node, child);
                break;
            case (int) RegexpConstants.CHARACTER_SET:
                childCharacterSet(node, child);
                break;
            case (int) RegexpConstants.CHARACTER:
                childCharacter(node, child);
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
        public void enterLeftBracket(Token node) {
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
        public Node exitLeftBracket(Token node) {
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
        public void enterRightBracket(Token node) {
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
        public Node exitRightBracket(Token node) {
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
        public void enterLeftBrace(Token node) {
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
        public Node exitLeftBrace(Token node) {
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
        public void enterRightBrace(Token node) {
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
        public Node exitRightBrace(Token node) {
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
        public void enterQuestion(Token node) {
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
        public Node exitQuestion(Token node) {
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
        public void enterAsterisk(Token node) {
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
        public Node exitAsterisk(Token node) {
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
        public void enterPlus(Token node) {
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
        public Node exitPlus(Token node) {
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
        public void enterVerticalBar(Token node) {
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
        public Node exitVerticalBar(Token node) {
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
        public void enterDot(Token node) {
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
        public Node exitDot(Token node) {
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
        public void enterComma(Token node) {
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
        public Node exitComma(Token node) {
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
        public void enterChar(Token node) {
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
        public Node exitChar(Token node) {
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
        public void enterExpr(Production node) {
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
        public Node exitExpr(Production node) {
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
        public void childExpr(Production node, Node child) {
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
        public void enterFact(Production node) {
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
        public Node exitFact(Production node) {
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
        public void childFact(Production node, Node child) {
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

        /**
         * <summary>Called when entering a parse tree node.</summary>
         * 
         * <param name='node'>the node being entered</param>
         * 
         * <exception cref='ParseException'>if the node analysis
         * discovered errors</exception>
         */
        public void enterAtomModifier(Production node) {
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
        public Node exitAtomModifier(Production node) {
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
        public void childAtomModifier(Production node, Node child) {
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
        public void enterCharacterSet(Production node) {
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
        public Node exitCharacterSet(Production node) {
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
        public void childCharacterSet(Production node, Node child) {
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
        public void enterCharacter(Production node) {
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
        public Node exitCharacter(Production node) {
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
        public void childCharacter(Production node, Node child) {
            node.AddChild(child);
        }
    }
}
