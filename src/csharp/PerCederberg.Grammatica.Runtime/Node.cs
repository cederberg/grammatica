/*
 * Node.cs
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

using System.Collections;
using System.IO;

namespace PerCederberg.Grammatica.Runtime {

    /**
     * An abstract parse tree node. This class is inherited by all
     * nodes in the parse tree, i.e. by the token and production
     * classes.
     *
     * @author   Per Cederberg, <per at percederberg dot net>
     * @version  1.5
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
         * Checks if this node is hidden, i.e. if it should not be
         * visible outside the parser.
         *
         * @return true if the node should be hidden, or
         *         false otherwise
         */
        internal virtual bool IsHidden() {
            return false;
        }

        /**
         * The node type id property (read-only). This value is set as
         * a unique identifier for each type of node, in order to
         * simplify later identification.
         *
         * @since 1.5
         */
        public abstract int Id {
            get;
        }

        /**
         * Returns the node type id. This value is set as a unique
         * identifier for each type of node, in order to simplify
         * later identification.
         *
         * @return the node type id
         *
         * @see #Id
         *
         * @deprecated Use the Id property instead.
         */
        public virtual int GetId() {
            return Id;
        }

        /**
         * The node name property (read-only).
         *
         * @since 1.5
         */
        public abstract string Name {
            get;
        }

        /**
         * Returns the node name.
         *
         * @return the node name
         *
         * @see #Name
         *
         * @deprecated Use the Name property instead.
         */
        public virtual string GetName() {
            return Name;
        }

        /**
         * The line number property of the first character in this
         * node (read-only). If the node has child elements, this
         * value will be fetched from the first child.
         *
         * @since 1.5
         */
        public virtual int StartLine {
            get {
                int  line;

                for (int i = 0; i < Count; i++) {
                    line = this[i].StartLine;
                    if (line >= 0) {
                        return line;
                    }
                }
                return -1;
            }
        }

        /**
         * The line number of the first character in this node. If the
         * node has child elements, this value will be fetched from
         * the first child.
         *
         * @return the line number of the first character, or
         *         -1 if not applicable
         *
         * @see #StartLine
         *
         * @deprecated Use the StartLine property instead.
         */
        public virtual int GetStartLine() {
            return StartLine;
        }

        /**
         * The column number property of the first character in this
         * node (read-only). If the node has child elements, this
         * value will be fetched from the first child.
         *
         * @since 1.5
         */
        public virtual int StartColumn {
            get {
                int  col;

                for (int i = 0; i < Count; i++) {
                    col = this[i].StartColumn;
                    if (col >= 0) {
                        return col;
                    }
                }
                return -1;
            }
        }

        /**
         * The column number of the first character in this node. If
         * the node has child elements, this value will be fetched
         * from the first child.
         *
         * @return the column number of the first token character, or
         *         -1 if not applicable
         *
         * @see #StartColumn
         *
         * @deprecated Use the StartColumn property instead.
         */
        public virtual int GetStartColumn() {
            return StartColumn;
        }

        /**
         * The line number property of the last character in this node
         * (read-only). If the node has child elements, this value
         * will be fetched from the last child.
         *
         * @since 1.5
         */
        public virtual int EndLine {
            get {
                int  line;

                for (int i = Count - 1; i >= 0; i--) {
                    line = this[i].EndLine;
                    if (line >= 0) {
                        return line;
                    }
                }
                return -1;
            }
        }

        /**
         * The line number of the last character in this node. If the
         * node has child elements, this value will be fetched from
         * the last child.
         *
         * @return the line number of the last token character, or
         *         -1 if not applicable
         *
         * @see #EndLine
         *
         * @deprecated Use the EndLine property instead.
         */
        public virtual int GetEndLine() {
            return EndLine;
        }

        /**
         * The column number property of the last character in this
         * node (read-only). If the node has child elements, this
         * value will be fetched from the last child.
         *
         * @since 1.5
         */
        public virtual int EndColumn {
            get {
                int  col;

                for (int i = Count - 1; i >= 0; i--) {
                    col = this[i].EndColumn;
                    if (col >= 0) {
                        return col;
                    }
                }
                return -1;
            }
        }

        /**
         * The column number of the last character in this node. If
         * the node has child elements, this value will be fetched
         * from the last child.
         *
         * @return the column number of the last token character, or
         *         -1 if not applicable
         *
         * @see #EndColumn
         *
         * @deprecated Use the EndColumn property instead.
         */
        public virtual int GetEndColumn() {
            return EndColumn;
        }

        /**
         * The parent node property (read-only).
         *
         * @since 1.5
         */
        public Node Parent {
            get {
                return parent;
            }
        }

        /**
         * Returns the parent node.
         *
         * @return the parent parse tree node
         *
         * @see #Parent
         *
         * @deprecated Use the Parent property instead.
         */
        public Node GetParent() {
            return Parent;
        }

        /**
         * Sets the parent node.
         *
         * @param parent         the new parent node
         */
        internal void SetParent(Node parent) {
            this.parent = parent;
        }

        /**
         * The child node count property (read-only).
         *
         * @since 1.5
         */
        public virtual int Count {
            get {
                return 0;
            }
        }

        /**
         * Returns the number of child nodes.
         *
         * @return the number of child nodes
         *
         * @deprecated Use the Count property instead.
         */
        public virtual int GetChildCount() {
            return Count;
        }

        /**
         * Returns the number of descendant nodes.
         *
         * @return the number of descendant nodes
         *
         * @since 1.2
         */
        public int GetDescendantCount() {
            int  count = 0;

            for (int i = 0; i < Count; i++) {
                count += 1 + this[i].GetDescendantCount();
            }
            return count;
        }

        /**
         * The child node index (read-only).
         *
         * @param index          the child index, 0 <= index < Count
         *
         * @return the child node found, or
         *         null if index out of bounds
         *
         * @since 1.5
         */
        public virtual Node this[int index] {
            get {
                return null;
            }
        }

        /**
         * Returns the child node with the specified index.
         *
         * @param index          the child index, 0 <= index < count
         *
         * @return the child node found, or
         *         null if index out of bounds
         *
         * @deprecated Use the class indexer instead.
         */
        public virtual Node GetChildAt(int index) {
            return this[index];
        }

        /**
         * The node values property. This property provides direct
         * access to the list of computed values associated with this
         * node during analysis. Note that setting this property to
         * null will remove all node values. Any operation on the
         * value array list is allowed and is immediately reflected
         * through the various value reading and manipulation methods.
         *
         * @since 1.5
         */
        public ArrayList Values {
            get {
                if (values == null) {
                    values = new ArrayList();
                }
                return values;
            }
            set {
                this.values = value;
            }
        }

        /**
         * Returns the number of computed values associated with this
         * node. Any number of values can be associated with a node
         * through calls to AddValue().
         *
         * @return the number of values associated with this node
         *
         * @see #Values
         *
         * @deprecated Use the Values and Values.Count properties
         *     instead.
         */
        public int GetValueCount() {
            if (values == null) {
                return 0;
            } else {
                return values.Count;
            }
        }

        /**
         * Returns a computed value of this node, if previously set. A
         * value may be used for storing intermediate results in the
         * parse tree during analysis.
         *
         * @param pos             the value position, 0 <= pos < count
         *
         * @return the computed node value, or
         *         null if not set
         *
         * @see #Values
         *
         * @deprecated Use the Values property and it's array indexer
         *     instead.
         */
        public object GetValue(int pos) {
            return Values[pos];
        }

        /**
         * Returns the list with all the computed values for this
         * node. Note that the list is not a copy, so changes will
         * affect the values in this node (as it is the same object).
         *
         * @return a list with all values, or
         *         null if no values have been set
         *
         * @see #Values
         *
         * @deprecated Use the Values property instead. Note that the
         *     Values property will never be null, but possibly empty.
         */
        public ArrayList GetAllValues() {
            return values;
        }

        /**
         * Adds a computed value to this node. The computed value may
         * be used for storing intermediate results in the parse tree
         * during analysis.
         *
         * @param value          the node value
         *
         * @see #Values
         *
         * @deprecated Use the Values property and the Values.Add
         *     method instead.
         */
        public void AddValue(object value) {
            if (value != null) {
                Values.Add(value);
            }
        }

        /**
         * Adds a set of computed values to this node.
         *
         * @param values         the vector with node values
         *
         * @see #Values
         *
         * @deprecated Use the Values property and the Values.AddRange
         *     method instead.
         */
        public void AddValues(ArrayList values) {
            if (values != null) {
                Values.AddRange(values);
            }
        }

        /**
         * Removes all computed values stored in this node.
         *
         * @see #Values
         *
         * @deprecated Use the Values property and the Values.Clear
         *     method instead. Alternatively the Values property can
         *     be set to null.
         */
        public void RemoveAllValues() {
            values = null;
        }

        /**
         * Prints this node and all subnodes to the specified output
         * stream.
         *
         * @param output         the output stream to use
         */
        public void PrintTo(TextWriter output) {
            PrintTo(output, "");
            output.Flush();
        }

        /**
         * Prints this node and all subnodes to the specified output
         * stream.
         *
         * @param output         the output stream to use
         * @param indent         the indentation string
         */
        private void PrintTo(TextWriter output, string indent) {
            output.WriteLine(indent + ToString());
            indent = indent + "  ";
            for (int i = 0; i < Count; i++) {
                this[i].PrintTo(output, indent);
            }
        }
    }
}
