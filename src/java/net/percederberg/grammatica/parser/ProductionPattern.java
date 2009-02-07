/*
 * ProductionPattern.java
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

package net.percederberg.grammatica.parser;

import java.util.ArrayList;

/**
 * A production pattern. This class represents a set of production
 * alternatives that together forms a single production. A production
 * pattern is identified by an integer id and a name, both provided
 * upon creation. The pattern id is used for referencing the
 * production pattern from production pattern elements.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
public class ProductionPattern {

    /**
     * The production pattern identity.
     */
    private int id;

    /**
     * The production pattern name.
     */
    private String name;

    /**
     * The synthectic production flag. If this flag is set, the
     * production identified by this pattern has been artificially
     * inserted into the grammar.
     */
    private boolean synthetic;

    /**
     * The list of production pattern alternatives.
     */
    private ArrayList alternatives;

    /**
     * The default production pattern alternative. This alternative
     * is used when no other alternatives match. It may be set to
     * -1, meaning that there is no default (or fallback) alternative.
     */
    private int defaultAlt;

    /**
     * The look-ahead set associated with this pattern.
     */
    private LookAheadSet lookAhead;

    /**
     * Creates a new production pattern.
     *
     * @param id             the production pattern id
     * @param name           the production pattern name
     */
    public ProductionPattern(int id, String name) {
        this.id = id;
        this.name = name;
        this.synthetic = false;
        this.alternatives = new ArrayList();
        this.defaultAlt = -1;
        this.lookAhead = null;
    }

    /**
     * Checks if the synthetic production flag is set. If this flag is
     * set, the production identified by this pattern has been
     * artificially inserted into the grammar. No parse tree nodes
     * will be created for such nodes, instead the child nodes will
     * be added directly to the parent node.
     *
     * @return true if this production pattern is synthetic, or
     *         false otherwise
     *
     * @since 1.5
     */
    public boolean isSynthetic() {
        return synthetic;
    }

    /**
     * Checks if the synthetic production flag is set. If this flag is
     * set, the production identified by this pattern has been
     * artificially inserted into the grammar. No parse tree nodes
     * will be created for such nodes, instead the child nodes will
     * be added directly to the parent node.
     *
     * @return true if this production pattern is synthetic, or
     *         false otherwise
     *
     * @deprecated Use the isSynthetic() method instead. This method
     *     name contained a spelling error.
     */
    public boolean isSyntetic() {
        return isSynthetic();
    }

    /**
     * Checks if this pattern is recursive on the left-hand side. This
     * method checks if any of the production pattern alternatives is
     * left-recursive.
     *
     * @return true if at least one alternative is left recursive, or
     *         false otherwise
     */
    public boolean isLeftRecursive() {
        ProductionPatternAlternative  alt;

        for (int i = 0; i < alternatives.size(); i++) {
            alt = (ProductionPatternAlternative) alternatives.get(i);
            if (alt.isLeftRecursive()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this pattern is recursive on the right-hand side.
     * This method checks if any of the production pattern
     * alternatives is right-recursive.
     *
     * @return true if at least one alternative is right recursive, or
     *         false otherwise
     */
    public boolean isRightRecursive() {
        ProductionPatternAlternative  alt;

        for (int i = 0; i < alternatives.size(); i++) {
            alt = (ProductionPatternAlternative) alternatives.get(i);
            if (alt.isRightRecursive()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this pattern would match an empty stream of tokens.
     * This method checks if any one of the production pattern
     * alternatives would match the empty token stream.
     *
     * @return true if at least one alternative match no tokens, or
     *         false otherwise
     */
    public boolean isMatchingEmpty() {
        ProductionPatternAlternative  alt;

        for (int i = 0; i < alternatives.size(); i++) {
            alt = (ProductionPatternAlternative) alternatives.get(i);
            if (alt.isMatchingEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the unique production pattern identity value.
     *
     * @return the production pattern id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the production pattern name.
     *
     * @return the production pattern name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the synthetic production pattern flag. If this flag is set,
     * the production identified by this pattern has been artificially
     * inserted into the grammar. By default this flag is set to
     * false.
     *
     * @param synthetic       the new value of the synthetic flag
     *
     * @since 1.5
     */
    public void setSynthetic(boolean synthetic) {
        this.synthetic = synthetic;
    }

    /**
     * Sets the synthetic production pattern flag. If this flag is set,
     * the production identified by this pattern has been artificially
     * inserted into the grammar. By default this flag is set to
     * false.
     *
     * @param synthetic       the new value of the synthetic flag
     *
     * @deprecated Use the setSynthetic() method instead. This method
     *     name contained a spelling error.
     */
    public void setSyntetic(boolean synthetic) {
        setSynthetic(synthetic);
    }

    /**
     * Returns the number of alternatives in this pattern.
     *
     * @return the number of alternatives in this pattern
     */
    public int getAlternativeCount() {
        return alternatives.size();
    }

    /**
     * Returns an alternative in this pattern.
     *
     * @param pos            the alternative position, 0 <= pos < count
     *
     * @return the alternative found
     */
    public ProductionPatternAlternative getAlternative(int pos) {
        return (ProductionPatternAlternative) alternatives.get(pos);
    }

    /**
     * Adds a production pattern alternative.
     *
     * @param alt            the production pattern alternative to add
     *
     * @throws ParserCreationException if an identical alternative has
     *             already been added
     */
    public void addAlternative(ProductionPatternAlternative alt)
        throws ParserCreationException {

        if (alternatives.contains(alt)) {
            throw new ParserCreationException(
                ParserCreationException.INVALID_PRODUCTION_ERROR,
                name,
                "two identical alternatives exist");
        }
        alt.setPattern(this);
        alternatives.add(alt);
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a token string representation
     */
    public String toString() {
        StringBuffer  buffer = new StringBuffer();
        StringBuffer  indent = new StringBuffer();
        int           i;

        buffer.append(name);
        buffer.append("(");
        buffer.append(id);
        buffer.append(") ");
        for (i = 0; i < buffer.length(); i++) {
            indent.append(" ");
        }
        for (i = 0; i < alternatives.size(); i++) {
            if (i == 0) {
                buffer.append("= ");
            } else {
                buffer.append("\n");
                buffer.append(indent);
                buffer.append("| ");
            }
            buffer.append(alternatives.get(i));
        }
        return buffer.toString();
    }

    /**
     * Returns the look-ahead set associated with this alternative.
     *
     * @return the look-ahead set associated with this alternative
     */
    LookAheadSet getLookAhead() {
        return lookAhead;
    }

    /**
     * Sets the look-ahead set for this alternative.
     *
     * @param lookAhead      the new look-ahead set
     */
    void setLookAhead(LookAheadSet lookAhead) {
        this.lookAhead = lookAhead;
    }

    /**
     * Returns the default pattern alternative. The default
     * alternative is used when no other alternative matches.
     *
     * @return the default pattern alternative, or
     *         null if none has been set
     */
    ProductionPatternAlternative getDefaultAlternative() {
        if (defaultAlt >= 0) {
            Object obj = alternatives.get(defaultAlt);
            return (ProductionPatternAlternative) obj;
        } else {
            return null;
        }
    }

    /**
     * Sets the default pattern alternative. The default alternative
     * is used when no other alternative matches.
     *
     * @param pos            the position of the default alternative
     */
    void setDefaultAlternative(int pos) {
        if (pos >= 0 && pos < alternatives.size()) {
            this.defaultAlt = pos;
        }
    }
}
