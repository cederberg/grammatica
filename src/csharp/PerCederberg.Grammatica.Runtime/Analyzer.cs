/*
 * Analyzer.cs
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

using System.Collections;

namespace PerCederberg.Grammatica.Runtime {

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
         * Creates a new parse tree analyzer.
         */
        public Analyzer() {
        }

        /**
         * Resets this analyzer when the parser is reset for another
         * input stream. The default implementation of this method does
         * nothing.
         *
         * @since 1.5
         */
        public virtual void Reset() {
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
        public Node Analyze(Node node) {
            ParserLogException  log = new ParserLogException();

            node = Analyze(node, log);
            if (log.Count > 0) {
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
        private Node Analyze(Node node, ParserLogException log) {
            Production  prod;
            int         errorCount;

            errorCount = log.Count;
            if (node is Production) {
                prod = (Production) node;
                prod = NewProduction(prod.Pattern);
                try {
                    Enter(prod);
                } catch (ParseException e) {
                    log.AddError(e);
                }
                for (int i = 0; i < node.Count; i++) {
                    try {
                        Child(prod, Analyze(node[i], log));
                    } catch (ParseException e) {
                        log.AddError(e);
                    }
                }
                try {
                    return Exit(prod);
                } catch (ParseException e) {
                    if (errorCount == log.Count) {
                        log.AddError(e);
                    }
                }
            } else {
                node.Values.Clear();
                try {
                    Enter(node);
                } catch (ParseException e) {
                    log.AddError(e);
                }
                try {
                    return Exit(node);
                } catch (ParseException e) {
                    if (errorCount == log.Count) {
                        log.AddError(e);
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
        public virtual Production NewProduction(ProductionPattern pattern) {
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
        public virtual void Enter(Node node) {
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
        public virtual Node Exit(Node node) {
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
        public virtual void Child(Production node, Node child) {
            node.AddChild(child);
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
        protected Node GetChildAt(Node node, int pos) {
            Node  child;

            if (node == null) {
                throw new ParseException(
                    ParseException.ErrorType.INTERNAL,
                    "attempt to read 'null' parse tree node",
                    -1,
                    -1);
            }
            child = node[pos];
            if (child == null) {
                throw new ParseException(
                    ParseException.ErrorType.INTERNAL,
                    "node '" + node.Name + "' has no child at " +
                    "position " + pos,
                    node.StartLine,
                    node.StartColumn);
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
        protected Node GetChildWithId(Node node, int id) {
            Node  child;

            if (node == null) {
                throw new ParseException(
                    ParseException.ErrorType.INTERNAL,
                    "attempt to read 'null' parse tree node",
                    -1,
                    -1);
            }
            for (int i = 0; i < node.Count; i++) {
                child = node[i];
                if (child != null && child.Id == id) {
                    return child;
                }
            }
            throw new ParseException(
                ParseException.ErrorType.INTERNAL,
                "node '" + node.Name + "' has no child with id " + id,
                node.StartLine,
                node.StartColumn);
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
        protected object GetValue(Node node, int pos) {
            object  value;

            if (node == null) {
                throw new ParseException(
                    ParseException.ErrorType.INTERNAL,
                    "attempt to read 'null' parse tree node",
                    -1,
                    -1);
            }
            value = node.Values[pos];
            if (value == null) {
                throw new ParseException(
                    ParseException.ErrorType.INTERNAL,
                    "node '" + node.Name + "' has no value at " +
                    "position " + pos,
                    node.StartLine,
                    node.StartColumn);
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
        protected int GetIntValue(Node node, int pos) {
            object  value;

            value = GetValue(node, pos);
            if (value is int) {
                return (int) value;
            } else {
                throw new ParseException(
                    ParseException.ErrorType.INTERNAL,
                    "node '" + node.Name + "' has no integer value " +
                    "at position " + pos,
                    node.StartLine,
                    node.StartColumn);
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
        protected string GetStringValue(Node node, int pos) {
            object  value;

            value = GetValue(node, pos);
            if (value is string) {
                return (string) value;
            } else {
                throw new ParseException(
                    ParseException.ErrorType.INTERNAL,
                    "node '" + node.Name + "' has no string value " +
                    "at position " + pos,
                    node.StartLine,
                    node.StartColumn);
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
        protected ArrayList GetChildValues(Node node) {
            ArrayList  result = new ArrayList();
            Node       child;
            ArrayList  values;

            for (int i = 0; i < node.Count; i++) {
                child = node[i];
                values = child.Values;
                if (values != null) {
                    result.AddRange(values);
                }
            }
            return result;
        }
    }
}
