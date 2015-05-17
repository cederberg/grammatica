/*
 * Production.java
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the BSD license.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * LICENSE.txt file for more details.
 *
 * Copyright (c) 2003-2015 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.parser;

import java.util.ArrayList;

/**
 * A production node. This class represents a grammar production (i.e.
 * a list of child nodes) in a parse tree. The productions are created
 * by a parser, that adds children a according to a set of production
 * patterns (i.e. grammar rules).
 *
 * @author   Per Cederberg
 * @version  1.1
 */
public class Production extends Node {

    /**
     * The production pattern used for this production.
     */
    private ProductionPattern pattern;

    /**
     * The child nodes.
     */
    private ArrayList children;

    /**
     * Creates a new production node.
     *
     * @param pattern        the production pattern
     */
    public Production(ProductionPattern pattern) {
        this.pattern = pattern;
        this.children = new ArrayList();
    }

    /**
     * Checks if this node is hidden, i.e. if it should not be visible
     * outside the parser.
     *
     * @return true if the node should be hidden, or
     *         false otherwise
     */
    boolean isHidden() {
        return pattern.isSynthetic();
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
    public int getChildCount() {
        return children.size();
    }

    /**
     * Returns the child node with the specified index.
     *
     * @param index          the child index, starting at 0
     *
     * @return the child node found, or
     *         null if index out of bounds
     */
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
        if (child != null) {
            child.setParent(this);
            children.add(child);
        }
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
