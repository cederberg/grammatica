/*
 * Analyzer.java
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

package net.percederberg.grammatica.parser;

import java.util.ArrayList;

/**
 * A parse tree analyzer. This class provides callback methods that
 * may be used either during parsing, or for a parse tree traversal.
 * This class should be subclassed to provide adequate handling of the
 * parse tree nodes.
 *
 * The general contract for the analyzer class does not guarantee a
 * strict call order for the callback methods. Depending on the type
 * of parser, the enter() and exit() methods for production nodes can
 * be called either in a top-down or a bottom-up fashion. The only
 * guarantee provided by this API, is that the calls for any given
 * node will always be in the order enter(), child(), and exit(). If
 * various child() calls are made, they will be made from left to
 * right as child nodes are added (to the right).
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
public class Analyzer {

    /**
     * Resets this analyzer when the parser is reset for another
     * input stream. The default implementation of this method does
     * nothing.
     *
     * @since 1.5
     */
    public void reset() {
        // Default implementation does nothing
    }

    /**
     * Analyzes a parse tree node by traversing all it's child nodes.
     * The tree traversal is depth-first, and the appropriate
     * callback methods will be called. If the node is a production
     * node, a new production node will be created and children will
     * be added by recursively processing the children of the
     * specified production node. This method is used to process a
     * parse tree after creation.
     *
     * @param node           the parse tree node to process
     *
     * @return the resulting parse tree node
     *
     * @throws ParserLogException if the node analysis discovered
     *             errors
     */
    public Node analyze(Node node) throws ParserLogException {
        ParserLogException  log = new ParserLogException();

        node = analyze(node, log);
        if (log.getErrorCount() > 0) {
            throw log;
        }
        return node;
    }

    /**
     * Analyzes a parse tree node by traversing all it's child nodes.
     * The tree traversal is depth-first, and the appropriate
     * callback methods will be called. If the node is a production
     * node, a new production node will be created and children will
     * be added by recursively processing the children of the
     * specified production node. This method is used to process a
     * parse tree after creation.
     *
     * @param node           the parse tree node to process
     * @param log            the parser error log
     *
     * @return the resulting parse tree node
     */
    private Node analyze(Node node, ParserLogException log) {
        Production  prod;
        int         errorCount;

        errorCount = log.getErrorCount();
        if (node instanceof Production) {
            prod = (Production) node;
            prod = newProduction(prod.getPattern());
            try {
                enter(prod);
            } catch (ParseException e) {
                log.addError(e);
            }
            for (int i = 0; i < node.getChildCount(); i++) {
                try {
                    child(prod, analyze(node.getChildAt(i), log));
                } catch (ParseException e) {
                    log.addError(e);
                }
            }
            try {
                return exit(prod);
            } catch (ParseException e) {
                if (errorCount == log.getErrorCount()) {
                    log.addError(e);
                }
            }
        } else {
            node.removeAllValues();
            try {
                enter(node);
            } catch (ParseException e) {
                log.addError(e);
            }
            try {
                return exit(node);
            } catch (ParseException e) {
                if (errorCount == log.getErrorCount()) {
                    log.addError(e);
                }
            }
        }
        return null;
    }

    /**
     * Factory method to create a new production node. This method
     * can be overridden to provide other production implementations
     * than the default one.
     *
     * @param pattern        the production pattern
     *
     * @return the new production node
     *
     * @since 1.5
     */
    protected Production newProduction(ProductionPattern pattern) {
        return new Production(pattern);
    }

    /**
     * Called when entering a parse tree node. By default this method
     * does nothing. A subclass can override this method to handle
     * each node separately.
     *
     * @param node           the node being entered
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected void enter(Node node) throws ParseException {
        // Nothing is done here by default
    }

    /**
     * Called when exiting a parse tree node. By default this method
     * returns the node. A subclass can override this method to handle
     * each node separately. If no parse tree should be created, this
     * method should return null.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree, or
     *         null if no parse tree should be created
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected Node exit(Node node) throws ParseException {
        return node;
    }

    /**
     * Called when adding a child to a parse tree node. By default
     * this method adds the child to the production node. A subclass
     * can override this method to handle each node separately. Note
     * that the child node may be null if the corresponding exit()
     * method returned null.
     *
     * @param node           the parent node
     * @param child          the child node, or null
     *
     * @throws ParseException if the node analysis discovered errors
     */
    protected void child(Production node, Node child)
        throws ParseException {

        node.addChild(child);
    }

    /**
     * Returns a child at the specified position. If either the node
     * or the child node is null, this method will throw a parse
     * exception with the internal error type.
     *
     * @param node           the parent node
     * @param pos            the child position
     *
     * @return the child node
     *
     * @throws ParseException if either the node or the child node
     *             was null
     */
    protected Node getChildAt(Node node, int pos) throws ParseException {
        Node  child;

        if (node == null) {
            throw new ParseException(
                ParseException.INTERNAL_ERROR,
                "attempt to read 'null' parse tree node",
                -1,
                -1);
        }
        child = node.getChildAt(pos);
        if (child == null) {
            throw new ParseException(
                ParseException.INTERNAL_ERROR,
                "node '" + node.getName() + "' has no child at " +
                "position " + pos,
                node.getStartLine(),
                node.getStartColumn());
        }
        return child;
    }

    /**
     * Returns the first child with the specified id. If the node is
     * null, or no child with the specified id could be found, this
     * method will throw a parse exception with the internal error
     * type.
     *
     * @param node           the parent node
     * @param id             the child node id
     *
     * @return the child node
     *
     * @throws ParseException if the node was null, or a child node
     *             couldn't be found
     */
    protected Node getChildWithId(Node node, int id)
        throws ParseException {

        Node  child;

        if (node == null) {
            throw new ParseException(
                ParseException.INTERNAL_ERROR,
                "attempt to read 'null' parse tree node",
                -1,
                -1);
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            child = node.getChildAt(i);
            if (child != null && child.getId() == id) {
                return child;
            }
        }
        throw new ParseException(
            ParseException.INTERNAL_ERROR,
            "node '" + node.getName() + "' has no child with id " + id,
            node.getStartLine(),
            node.getStartColumn());
    }

    /**
     * Returns the node value at the specified position. If either
     * the node or the value is null, this method will throw a parse
     * exception with the internal error type.
     *
     * @param node           the parse tree node
     * @param pos            the child position
     *
     * @return the value object
     *
     * @throws ParseException if either the node or the value was null
     */
    protected Object getValue(Node node, int pos) throws ParseException {
        Object  value;

        if (node == null) {
            throw new ParseException(
                ParseException.INTERNAL_ERROR,
                "attempt to read 'null' parse tree node",
                -1,
                -1);
        }
        value = node.getValue(pos);
        if (value == null) {
            throw new ParseException(
                ParseException.INTERNAL_ERROR,
                "node '" + node.getName() + "' has no value at " +
                "position " + pos,
                node.getStartLine(),
                node.getStartColumn());
        }
        return value;
    }

    /**
     * Returns the node integer value at the specified position. If
     * either the node is null, or the value is not an instance of
     * the Integer class, this method will throw a parse exception
     * with the internal error type.
     *
     * @param node           the parse tree node
     * @param pos            the child position
     *
     * @return the value object
     *
     * @throws ParseException if either the node was null, or the
     *             value wasn't an integer
     */
    protected int getIntValue(Node node, int pos) throws ParseException {
        Object  value;

        value = getValue(node, pos);
        if (value instanceof Integer) {
            return ((Integer) value).intValue();
        } else {
            throw new ParseException(
                ParseException.INTERNAL_ERROR,
                "node '" + node.getName() + "' has no integer value " +
                "at position " + pos,
                node.getStartLine(),
                node.getStartColumn());
        }
    }

    /**
     * Returns the node string value at the specified position. If
     * either the node is null, or the value is not an instance of
     * the String class, this method will throw a parse exception
     * with the internal error type.
     *
     * @param node           the parse tree node
     * @param pos            the child position
     *
     * @return the value object
     *
     * @throws ParseException if either the node was null, or the
     *             value wasn't a string
     */
    protected String getStringValue(Node node, int pos)
        throws ParseException {

        Object  value;

        value = getValue(node, pos);
        if (value instanceof String) {
            return (String) value;
        } else {
            throw new ParseException(
                ParseException.INTERNAL_ERROR,
                "node '" + node.getName() + "' has no string value " +
                "at position " + pos,
                node.getStartLine(),
                node.getStartColumn());
        }
    }

    /**
     * Returns all the node values for all child nodes.
     *
     * @param node           the parse tree node
     *
     * @return a list with all the child node values
     *
     * @since 1.3
     */
    protected ArrayList getChildValues(Node node) {
        ArrayList  result = new ArrayList();
        Node       child;
        ArrayList  values;

        for (int i = 0; i < node.getChildCount(); i++) {
            child = node.getChildAt(i);
            values = child.getAllValues();
            if (values != null) {
                result.addAll(values);
            }
        }
        return result;
    }
}
