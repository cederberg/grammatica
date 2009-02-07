/*
 * Node.java
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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Vector;

/**
 * An abstract parse tree node. This class is inherited by all nodes
 * in the parse tree, i.e. by the token and production classes.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.2
 */
public abstract class Node {

    /**
     * The parent node.
     */
    private Node parent = null;

    /**
     * The computed node values.
     */
    private ArrayList values = null;

    /**
     * Checks if this node is hidden, i.e. if it should not be visible
     * outside the parser.
     *
     * @return true if the node should be hidden, or
     *         false otherwise
     */
    boolean isHidden() {
        return false;
    }

    /**
     * Returns the node type id. This value is set as a unique
     * identifier for each type of node, in order to simplify later
     * identification.
     *
     * @return the node type id
     */
    public abstract int getId();

    /**
     * Returns the node name.
     *
     * @return the node name
     */
    public abstract String getName();

    /**
     * The line number of the first character in this node. If the
     * node has child elements, this value will be fetched from the
     * first child.
     *
     * @return the line number of the first character, or
     *         -1 if not applicable
     */
    public int getStartLine() {
        int  line;

        for (int i = 0; i < getChildCount(); i++) {
            line = getChildAt(i).getStartLine();
            if (line >= 0) {
                return line;
            }
        }
        return -1;
    }

    /**
     * The column number of the first character in this node. If the
     * node has child elements, this value will be fetched from the
     * first child.
     *
     * @return the column number of the first token character, or
     *         -1 if not applicable
     */
    public int getStartColumn() {
        int  col;

        for (int i = 0; i < getChildCount(); i++) {
            col = getChildAt(i).getStartColumn();
            if (col >= 0) {
                return col;
            }
        }
        return -1;
    }

    /**
     * The line number of the last character in this node. If the node
     * has child elements, this value will be fetched from the last
     * child.
     *
     * @return the line number of the last token character, or
     *         -1 if not applicable
     */
    public int getEndLine() {
        int  line;

        for (int i = getChildCount() - 1; i >= 0; i--) {
            line = getChildAt(i).getEndLine();
            if (line >= 0) {
                return line;
            }
        }
        return -1;
    }

    /**
     * The column number of the last character in this node. If the
     * node has child elements, this value will be fetched from the
     * last child.
     *
     * @return the column number of the last token character, or
     *         -1 if not applicable
     */
    public int getEndColumn() {
        int  col;

        for (int i = getChildCount() - 1; i >= 0; i--) {
            col = getChildAt(i).getEndColumn();
            if (col >= 0) {
                return col;
            }
        }
        return -1;
    }

    /**
     * Returns the parent node.
     *
     * @return the parent parse tree node
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Sets the parent node.
     *
     * @param parent         the new parent node
     */
    void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * Returns the number of child nodes.
     *
     * @return the number of child nodes
     */
    public int getChildCount() {
        return 0;
    }

    /**
     * Returns the child node with the specified index.
     *
     * @param index          the child index, 0 <= index < count
     *
     * @return the child node found, or
     *         null if index out of bounds
     */
    public Node getChildAt(int index) {
        return null;
    }

    /**
     * Returns the number of descendant nodes.
     *
     * @return the number of descendant nodes
     *
     * @since 1.2
     */
    public int getDescendantCount() {
        int  count = 0;

        for (int i = 0; i < getChildCount(); i++) {
            count += 1 + getChildAt(i).getDescendantCount();
        }
        return count;
    }

    /**
     * Returns the number of computed values associated with this
     * node. Any number of values can be associated with a node
     * through calls to addValue().
     *
     * @return the number of values associated with this node
     */
    public int getValueCount() {
        if (values == null) {
            return 0;
        } else {
            return values.size();
        }
    }

    /**
     * Returns a computed value of this node, if previously set. A
     * value may be used for storing intermediate results in the parse
     * tree during analysis.
     *
     * @param pos             the value position, 0 <= pos < count
     *
     * @return the computed node value, or
     *         null if not set
     */
    public Object getValue(int pos) {
        if (values == null || pos < 0 || pos >= values.size()) {
            return null;
        } else {
            return values.get(pos);
        }
    }

    /**
     * Returns the vector with all the computed values for this node.
     * Note that the vector is not a copy, so changes will affect the
     * values in this node (as it is the same object).
     *
     * @return a vector with all values, or
     *         null if no values have been set
     *
     * @since 1.2
     */
    public ArrayList getAllValues() {
        return values;
    }

    /**
     * Adds a computed value to this node. The computed value may be
     * used for storing intermediate results in the parse tree during
     * analysis.
     *
     * @param value          the node value
     */
    public void addValue(Object value) {
        if (value != null) {
            if (this.values == null) {
                this.values = new ArrayList();
            }
            values.add(value);
        }
    }

    /**
     * Adds a set of computed values to this node.
     *
     * @param values         the list with node values
     */
    public void addValues(Vector values) {
        if (values != null) {
            for (int i = 0; i < values.size(); i++) {
                addValue(values.get(i));
            }
        }
    }

    /**
     * Adds a set of computed values to this node.
     *
     * @param values         the list with node values
     *
     * @since 1.2
     */
    public void addValues(ArrayList values) {
        if (values != null) {
            for (int i = 0; i < values.size(); i++) {
                addValue(values.get(i));
            }
        }
    }

    /**
     * Removes all computed values stored in this node.
     */
    public void removeAllValues() {
        values = null;
    }

    /**
     * Prints this node and all subnodes to the specified output
     * stream.
     *
     * @param output         the output stream to use
     */
    public void printTo(PrintStream output) {
        printTo(new PrintWriter(output));
    }

    /**
     * Prints this node and all subnodes to the specified output
     * stream.
     *
     * @param output         the output stream to use
     */
    public void printTo(PrintWriter output) {
        printTo(output, "");
        output.flush();
    }

    /**
     * Prints this node and all subnodes to the specified output
     * stream.
     *
     * @param output         the output stream to use
     * @param indent         the indentation string
     */
    private void printTo(PrintWriter output, String indent) {
        output.println(indent + toString());
        indent = indent + "  ";
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).printTo(output, indent);
        }
    }
}
