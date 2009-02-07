/*
 * FirstPassAnalyzer.java
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
 * Copyright (c) 2003-2009 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica;

import java.util.HashMap;

import net.percederberg.grammatica.parser.Node;
import net.percederberg.grammatica.parser.ParseException;
import net.percederberg.grammatica.parser.Production;
import net.percederberg.grammatica.parser.ProductionPattern;
import net.percederberg.grammatica.parser.Token;
import net.percederberg.grammatica.parser.TokenPattern;

/**
 * A first pass grammar analyzer. This class processes the grammar
 * parse tree and creates the token and production patterns. Both
 * token and production patterns are added to the grammar, but the
 * production patterns will all be empty. In order to analyze the
 * production pattern rules, all the production pattern names and
 * identifiers must be present in the grammar, so the pattern rules
 * must be analyzed in a second pass. This analyzer also adds all
 * header declarations to the grammar.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
class FirstPassAnalyzer extends GrammarAnalyzer {

    /**
     * The grammar where objects are added.
     */
    private Grammar grammar;

    /**
     * The token id to use.
     */
    private int nextTokenId = 1001;

    /**
     * The production id to use.
     */
    private int nextProductionId = 2001;

    /**
     * A map with all token and production names. This map is indexed
     * by the upper-case names (without '_' characters), and maps
     * these to the declared case-sensitive name.
     */
    private HashMap names = new HashMap();

    /**
     * Creates a new grammar analyser.
     *
     * @param grammar        the grammar where objects are added
     */
    public FirstPassAnalyzer(Grammar grammar) {
        this.grammar = grammar;
    }

    /**
     * Sets the node value to the ignore message. If no message is
     * set, no node value will be added.
     *
     * @param node           the token node
     *
     * @return the token node
     */
    protected Node exitIgnore(Token node) {
        String  str = node.getImage();

        str = str.substring(7, str.length() - 1).trim();
        if (!str.equals("")) {
            node.addValue(str);
        }
        return node;
    }

    /**
     * Sets the node value to the error message. If no message is set,
     * no node value will be added.
     *
     * @param node           the token node
     *
     * @return the token node
     */
    protected Node exitError(Token node) {
        String  str = node.getImage();

        str = str.substring(6, str.length() - 1).trim();
        if (!str.equals("")) {
            node.addValue(str);
        }
        return node;
    }

    /**
     * Sets the node value to the identifier string.
     *
     * @param node           the token node
     *
     * @return the token node
     */
    protected Node exitIdentifier(Token node) {
        node.addValue(node.getImage());
        return node;
    }

    /**
     * Sets the node value to the contents of the quoted string. The
     * quotation marks will be removed, but any escaped character
     * will be left intact.
     *
     * @param node           the token node
     *
     * @return the token node
     */
    protected Node exitQuotedString(Token node) {
        String  str = node.getImage();

        node.addValue(str.substring(1, str.length() - 1));
        return node;
    }

    /**
     * Sets the node value to the regular expression string. The
     * quotation marks will be removed, and the "\<" and "\>" will be
     * unescaped (replaced by the '<' and '>' characters). The rest of
     * the expression is left intact.
     *
     * @param node           the token node
     *
     * @return the token node
     */
    protected Node exitRegexp(Token node) {
        String        str = node.getImage();
        StringBuffer  buf = new StringBuffer();

        str = str.substring(2, str.length() - 2);
        for (int i = 0; i < str.length(); i++) {
            if (str.startsWith("\\<", i)) {
                buf.append('<');
                i++;
            } else if (str.startsWith("\\>", i)) {
                buf.append('>');
                i++;
            } else {
                buf.append(str.charAt(i));
            }
        }
        node.addValue(buf.toString());
        return node;
    }

    /**
     * Removes the header part from the parse tree by returning null.
     *
     * @param node           the production node
     *
     * @return the new production node
     */
    protected Node exitHeaderPart(Production node) {
        return null;
    }

    /**
     * Adds the header declaration to the grammar. This method will
     * also remove the header declaration from the parse tree by
     * returning null.
     *
     * @param node           the production node
     *
     * @return the new production node
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitHeaderDeclaration(Production node)
        throws ParseException {

        String  name;
        String  value;

        name = getStringValue(getChildAt(node, 0), 0);
        value = getStringValue(getChildAt(node, 2), 0);
        grammar.addDeclaration(name, value);
        return null;
    }

    /**
     * Removes the token part from the parse tree by returning null.
     *
     * @param node           the production node
     *
     * @return the new production node
     */
    protected Node exitTokenPart(Production node) {
        return null;
    }

    /**
     * Adds a token pattern to the grammar. This method will also
     * remove the token declaration from the parse tree by reutrning
     * null.
     *
     * @param node           the production node
     *
     * @return the new production node
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitTokenDeclaration(Production node)
        throws ParseException {

        TokenPattern  pattern;
        String        name;
        int           type;
        String        str;
        Token         token;
        Node          child;

        // Create token pattern
        name = getIdentifier((Token) getChildAt(node, 0));
        child = getChildAt(node, 2);
        type = getIntValue(child, 0);
        str = getStringValue(child, 1);
        pattern = new TokenPattern(nextTokenId++, name, type, str);

        // Process optional ignore or error
        if (node.getChildCount() == 4) {
            child = getChildAt(node, 3);
            token = (Token) getValue(child, 0);
            str = null;
            if (child.getValueCount() == 2) {
                str = getStringValue(child, 1);
            }
            switch (token.getId()) {
            case GrammarConstants.IGNORE:
                if (str == null) {
                    pattern.setIgnore();
                } else {
                    pattern.setIgnore(str);
                }
                break;
            case GrammarConstants.ERROR:
                if (str == null) {
                    pattern.setError();
                } else {
                    pattern.setError(str);
                }
                break;
            }
        }

        // Add token to grammar
        grammar.addToken(pattern,
                         node.getStartLine(),
                         node.getEndLine());
        return null;
    }

    /**
     * Sets the node values to the token pattern type and the token
     * pattern string.
     *
     * @param node           the production node
     *
     * @return the new production node
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitTokenValue(Production node) throws ParseException {
        switch (getChildAt(node, 0).getId()) {
        case GrammarConstants.QUOTED_STRING:
            node.addValue(new Integer(TokenPattern.STRING_TYPE));
            break;
        case GrammarConstants.REGEXP:
            node.addValue(new Integer(TokenPattern.REGEXP_TYPE));
            break;
        }
        node.addValue(getStringValue(getChildAt(node, 0), 0));
        return node;
    }

    /**
     * Sets the node values to the error or ignore token. If present,
     * the message string will also be added as a node value.
     *
     * @param node           the production node
     *
     * @return the new production node
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitTokenHandling(Production node)
        throws ParseException {

        Node  child = getChildAt(node, 0);

        node.addValue(child);
        if (child.getValueCount() > 0) {
            node.addValue(getValue(child, 0));
        }
        return node;
    }

    /**
     * Adds an empty production pattern to the grammar. This metod
     * will return the production node to make it available for the
     * second pass analyzer.
     *
     * @param node           the production node
     *
     * @return the new production node
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exitProductionDeclaration(Production node)
        throws ParseException {

        ProductionPattern  production;
        String             name;

        name = getIdentifier((Token) getChildAt(node, 0));
        production = new ProductionPattern(nextProductionId++, name);
        grammar.addProduction(production,
                              node.getStartLine(),
                              node.getEndLine());
        return node;
    }

    /**
     * Returns a token identifier. This method should only be called
     * with identifier tokens, otherwise an exception will be thrown.
     * This method also checks that the identifier name found is
     * globally unique in it's upper-case form, and throws an
     * exception if it is not.
     *
     * @param token          the identifier token
     *
     * @return the identifier name
     *
     * @throws ParseException if the identifier wasn't unique
     */
    private String getIdentifier(Token token) throws ParseException {
        String        name = token.getImage();
        StringBuffer  buf = new StringBuffer(name.toUpperCase());
        char          c;

        // Check for identifier token
        if (token.getId() != GrammarConstants.IDENTIFIER) {
            throw new ParseException(ParseException.INTERNAL_ERROR,
                                     null,
                                     token.getStartLine(),
                                     token.getStartColumn());
        }

        // Remove all non-identifier characters
        for (int i = 0; i < buf.length(); i++) {
            c = buf.charAt(i);
            if (('A' <= c && c <= 'Z') || ('0' <= c && c <= '9')) {
                // Do nothing
            } else {
                buf.deleteCharAt(i--);
            }
        }

        // Check for name collitions
        if (names.containsKey(buf.toString())) {
            throw new ParseException(
                ParseException.ANALYSIS_ERROR,
                "duplicate identifier '" + name + "' is similar or " +
                "equal to previously defined identifier '" +
                names.get(buf.toString()) + "'",
                token.getStartLine(),
                token.getStartColumn());
        } else {
            names.put(buf.toString(), name);
        }

        // Return the identifier
        return name;
    }
}
