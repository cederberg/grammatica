/*
 * SecondPassAnalyzer.java
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

package net.percederberg.grammatica;

import net.percederberg.grammatica.parser.Node;
import net.percederberg.grammatica.parser.ParseException;
import net.percederberg.grammatica.parser.ParserCreationException;
import net.percederberg.grammatica.parser.Production;
import net.percederberg.grammatica.parser.ProductionPattern;
import net.percederberg.grammatica.parser.ProductionPatternAlternative;
import net.percederberg.grammatica.parser.ProductionPatternElement;
import net.percederberg.grammatica.parser.Token;
import net.percederberg.grammatica.parser.TokenPattern;

/**
 * A second pass grammar analyzer. This class processes the grammar
 * parse tree and adds all production pattern rules to the patterns.
 * All required syntetic production patterns will also be added to
 * the grammar.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
class SecondPassAnalyzer extends GrammarAnalyzer {

    /**
     * The grammar where tokens and patterns are stored.
     */
    private Grammar grammar;

    /**
     * The current production pattern. This is set when processing the
     * production declaration, and is used when creating syntetic
     * productions.
     */
    private ProductionPattern currentProduction = null;

    /**
     * The next free syntetic production id.
     */
    private int nextSynteticId = 3001;

    /**
     * Creates a new grammar analyser.
     *
     * @param grammar        the grammar where objects are added
     */
    public SecondPassAnalyzer(Grammar grammar) {
        this.grammar = grammar;
    }

    /**
     * Sets the node value to the token or production pattern. If no
     * matching pattern was found, an exception is thrown.
     *
     * @param node           the token node
     *
     * @return the token node
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitIdentifier(Token node) throws ParseException {
        String             name = node.getImage();
        TokenPattern       token = grammar.getTokenPatternByName(name);
        ProductionPattern  prod = grammar.getProductionPatternByName(name);

        if (token != null) {
            node.addValue(token);
        } else if (prod != null) {
            node.addValue(prod);
        } else {
            throw new ParseException(
                ParseException.ANALYSIS_ERROR,
                "unrecognized identifier '" + name + "'",
                node.getStartLine(),
                node.getStartColumn());
        }
        return node;
    }

    /**
     * Sets the node value to the token pattern. If no matching
     * pattern was found, an exception is thrown.
     *
     * @param node           the token node
     *
     * @return the token node
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitQuotedString(Token node) throws ParseException {
        String        str;
        TokenPattern  token;

        str = node.getImage();
        str = str.substring(1, str.length() - 1);
        token = grammar.getTokenPatternByImage(str);
        if (token != null) {
            node.addValue(token);
        } else {
            throw new ParseException(
                ParseException.ANALYSIS_ERROR,
                "unrecognized token \"" + str + "\"",
                node.getStartLine(),
                node.getStartColumn());
        }
        return node;
    }

    /**
     * Removes the parse tree by returning null.
     *
     * @param node           the production node
     *
     * @return the new production node
     */
    protected Node exitGrammar(Production node) {
        return null;
    }

    /**
     * Removes the production part from the parse tree by returning
     * null.
     *
     * @param node           the production node
     *
     * @return the new production node
     */
    protected Node exitProductionPart(Production node) {
        return null;
    }

    /**
     * Sets the production name variable when encountering the
     * identifier child. This variable is used when creating new
     * subproductions.
     *
     * @param node           the production node
     * @param child          the child to add
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected void childProductionDeclaration(Production node, Node child)
        throws ParseException {

        super.childProductionDeclaration(node, child);
        if (child.getId() == GrammarConstants.IDENTIFIER) {
            currentProduction = (ProductionPattern) child.getValue(0);
        }
    }

    /**
     * Adds all the pattern rules to the production pattern. This
     * method also removes the production declaration from the parse
     * tree by returning null.
     *
     * @param node           the production node
     *
     * @return the new production node
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitProductionDeclaration(Production node)
        throws ParseException {

        ProductionPattern             pattern;
        ProductionPatternAlternative  alt;
        Node                          child;

        pattern = (ProductionPattern) getValue(getChildAt(node, 0), 0);
        child = getChildAt(node, 2);
        for (int i = 0; i < child.getValueCount(); i++) {
            alt = (ProductionPatternAlternative) getValue(child, i);
            try {
                pattern.addAlternative(alt);
            } catch (ParserCreationException e) {
                throw new ParseException(
                    ParseException.ANALYSIS_ERROR,
                    e.getMessage(),
                    node.getStartLine(),
                    node.getStartColumn());
            }
        }
        return null;
    }

    /**
     * Sets the node values to the pattern rules.
     *
     * @param node           the production node
     *
     * @return the new production node
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitProduction(Production node) throws ParseException {
        ProductionPatternAlternative  alt;
        ProductionPatternElement      elem;
        Node                          child;

        alt = new ProductionPatternAlternative();
        node.addValue(alt);
        for (int i = 0; i < node.getChildCount(); i++) {
            child = getChildAt(node, i);
            if (child.getId() == GrammarConstants.PRODUCTION_ATOM) {
                for (int j = 0; j < child.getValueCount(); j++) {
                    elem = (ProductionPatternElement) getValue(child, j);
                    alt.addElement(elem);
                }
            } else if (child.getId() == GrammarConstants.PRODUCTION) {
                node.addValues(child.getAllValues());
            }
        }

        return node;
    }

    /**
     * Sets the node value to the production pattern element.
     *
     * @param node           the production node
     *
     * @return the new production node
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitProductionAtom(Production node)
        throws ParseException {

        Node     child;
        boolean  token = false;
        int      id = 0;
        int      min = 1;
        int      max = 1;
        Object   obj;

        // Handle the alternatives
        child = getChildAt(node, 0);
        switch (child.getId()) {
        case GrammarConstants.IDENTIFIER:
            obj = getValue(child, 0);
            if (obj instanceof TokenPattern) {
                token = true;
                id = ((TokenPattern) obj).getId();
            } else {
                token = false;
                id = ((ProductionPattern) obj).getId();
            }
            break;
        case GrammarConstants.QUOTED_STRING:
            token = true;
            id = ((TokenPattern) getValue(child, 0)).getId();
            break;
        case GrammarConstants.LEFT_PAREN:
        case GrammarConstants.LEFT_BRACE:
        case GrammarConstants.LEFT_BRACKET:
            ProductionPatternElement  elem;

            if (child.getId() == GrammarConstants.LEFT_BRACE) {
                min = 0;
                max = -1;
            } else if (child.getId() == GrammarConstants.LEFT_BRACKET) {
                min = 0;
                max = 1;
            }
            elem = getProductionElement(getChildAt(node, 1));
            token = elem.isToken();
            id = elem.getId();
            break;
        }

        // Handle optional '?', '*' or '+'
        child = getChildAt(node, node.getChildCount() - 1);
        if (child.getId() == GrammarConstants.QUESTION_MARK) {
            min = 0;
            max = 1;
        } else if (child.getId() == GrammarConstants.ASTERISK) {
            min = 0;
            max = -1;
        } else if (child.getId() == GrammarConstants.PLUS_SIGN) {
            min = 1;
            max = -1;
        }

        // Create production pattern element
        node.addValue(new ProductionPatternElement(token, id, min, max));
        return node;
    }

    /**
     * Returns the production pattern element for a production node.
     * The production node only contains a set of production rules, so
     * this method normally creates a syntetic production and adds all
     * the rules to it. If only a single rule was present in the rule
     * set, and if it contained only an single mandatory element, that
     * element will be returned instead.
     *
     * @param node           the production parse tree node
     *
     * @return the production pattern element
     *
     * @throws ParseException if the node analysis discovered errors
     */
    private ProductionPatternElement getProductionElement(Node node)
        throws ParseException {

        ProductionPattern             prod;
        ProductionPatternAlternative  alt;
        String                        str;

        alt = (ProductionPatternAlternative) getValue(node, 0);
        if (node.getValueCount() == 1 && isSimple(alt)) {
            return alt.getElement(0);
        } else {
            str = currentProduction.getName() + "(" +
                  (nextSynteticId - 3000) + ")";
            prod = new ProductionPattern(nextSynteticId, str);
            prod.setSynthetic(true);
            for (int i = 0; i < node.getValueCount(); i++) {
                alt = (ProductionPatternAlternative) getValue(node, i);
                try {
                    prod.addAlternative(alt);
                } catch (ParserCreationException e) {
                    throw new ParseException(
                        ParseException.ANALYSIS_ERROR,
                        e.getMessage(),
                        node.getStartLine(),
                        node.getStartColumn());
                }
            }
            grammar.addProduction(prod,
                                  node.getStartLine(),
                                  node.getEndLine());
            return new ProductionPatternElement(false,
                                                nextSynteticId++,
                                                1,
                                                1);
        }
    }

    /**
     * Checks if a production pattern alternative contains a single
     * mandatory element.
     *
     * @param alt            the production pattern alternative
     *
     * @return true if the alternative is simple, or
     *         false otherwise
     */
    private boolean isSimple(ProductionPatternAlternative alt) {
        return alt.getElementCount() == 1
            && alt.getMinElementCount() == 1
            && alt.getMaxElementCount() == 1;
    }
}
