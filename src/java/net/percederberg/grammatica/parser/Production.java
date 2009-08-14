/*
 * Production.java
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
 * A production node. This class represents a grammar production (i.e.
 * a list of child nodes) in a parse tree. The productions are created
 * by a parser, that adds children a according to a set of production
 * patterns (i.e. grammar rules).
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.2
 */
public class Production extends Node {

    /**
     * The production pattern used for this production.
     */
    private ProductionPattern pattern;

    /**
     * The alternative used for this node.
     */
    private ProductionPatternAlternative alt;

    /**
     * The child nodes.
     */
    protected ArrayList<Node> children;

    /**
     * Creates a new production node.
     *
     * @param pattern        the production pattern
     */
    public Production(ProductionPatternAlternative alt) {
        this.alt = alt;
        this.pattern = alt.getPattern();
        this.children = new ArrayList();
    }

    /**
     * Checks if this node is synthetic.
     *
     * @return true if the node is synthetic, or false otherwise
     */
    @Override
    public boolean isSynthetic() {
        return pattern.isSynthetic();
    }

    /**
     * Returns the production pattern alternative for this node.
     *
     * @return the production pattern alternative
     */
    public ProductionPatternAlternative getAlternative() {
        return alt;
    }

    /**
     * Returns the production pattern for this production.
     *
     * @return the production pattern
     */
    public ProductionPattern getPattern() {
        return pattern;
    }

    /**
     * Returns the production (pattern) id. This value is set as a
     * unique identifier when creating the production pattern to
     * simplify later identification.
     *
     * @return the production id
     */
    public int getId() {
        return pattern.getId();
    }

    /**
     * Returns the production node name.
     *
     * @return the production node name
     */
    public String getName() {
        return pattern.getName();
    }

    /**
     * Returns the number of child nodes.
     *
     * @return the number of child nodes
     */
    @Override
    public int getChildCount() {
        return children.size();
    }

    /**
     * Returns the child node with the specified index.
     *
     * @param index          the child index, 0 <= index < count
     *
     * @return the child node found, or
     *         null if index out of bounds
     */
    @Override
    public Node getChildAt(int index) {
        if (index < 0 || index >= children.size()) {
            return null;
        } else {
            return (Node) children.get(index);
        }
    }

    /**
     * Adds a child node. The node will be added last in the list of
     * children.
     *
     * @param child          the child node to add
     */
    public void addChild(Node child) {
        // Set the parent if the child is not null.
        if (child != null) {
            child.setParent(this);
        }

        // Add the child.
        children.add(child);
    }

    /**
     * Returns a string representation of this production.
     *
     * @return a string representation of this production
     */
    public String toString() {
        StringBuffer  buffer = new StringBuffer();

        buffer.append(pattern.getName());
        buffer.append('(');
        buffer.append(pattern.getId());
        buffer.append(')');

        return buffer.toString();
    }
}