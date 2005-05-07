/*
 * ProductionPattern.cs
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
 * Copyright (c) 2003-2005 Per Cederberg. All rights reserved.
 */

using System.Collections;
using System.Text;

namespace PerCederberg.Grammatica.Parser {

    /**
     * A production pattern. This class represents a set of production
     * alternatives that together forms a single production. A
     * production pattern is identified by an integer id and a name,
     * both provided upon creation. The pattern id is used for
     * referencing the production pattern from production pattern
     * elements.
     *
     * @author   Per Cederberg, <per at percederberg dot net>
     * @version  1.5
     */
    public class ProductionPattern {

        /**
         * The production pattern identity.
         */
        private int id;

        /**
         * The production pattern name.
         */
        private string name;

        /**
         * The syntectic production flag. If this flag is set, the
         * production identified by this pattern has been artificially
         * inserted into the grammar.
         */
        private bool syntetic;

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
        public ProductionPattern(int id, string name) {
            this.id = id;
            this.name = name;
            this.syntetic = false;
            this.alternatives = new ArrayList();
            this.defaultAlt = -1;
            this.lookAhead = null;
        }

        /**
         * The production pattern identity property (read-only). This
         * property contains the unique identity value.
         *
         * @see #GetId
         *
         * @since 1.5
         */
        public int Id {
            get {
                return GetId();
            }
        }

        /**
         * The production pattern name property (read-only).
         *
         * @see #GetName
         *
         * @since 1.5
         */
        public string Name {
            get {
                return GetName();
            }
        }

        /**
         * The syntetic production pattern property. If this property
         * is set, the production identified by this pattern has been
         * artificially inserted into the grammar. No parse tree nodes
         * will be created for such nodes, instead the child nodes
         * will be added directly to the parent node. By default this
         * property is set to false.
         *
         * @see #IsSyntetic
         * @see #SetSyntetic
         *
         * @since 1.5
         */
        public bool Syntetic {
            get {
                return IsSyntetic();
            }
            set {
                SetSyntetic(value);
            }
        }

        /**
         * The look-ahead set property. This property contains the
         * look-ahead set associated with this alternative.
         */
        internal LookAheadSet LookAhead {
            get {
                return lookAhead;
            }
            set {
                lookAhead = value;
            }
        }

        /**
         * The default pattern alternative property. The default
         * alternative is used when no other alternative matches. The
         * default alternative must previously have been added to the
         * list of alternatives. This property is set to null if no
         * default pattern alternative has been set.
         */
        internal ProductionPatternAlternative DefaultAlternative {
            get {
                if (defaultAlt >= 0) {
                    object obj = alternatives[defaultAlt];
                    return (ProductionPatternAlternative) obj;
                } else {
                    return null;
                }
            }
            set {
                defaultAlt = 0;
                for (int i = 0; i < alternatives.Count; i++) {
                    if (alternatives[i] == value) {
                        defaultAlt = i;
                    }
                }
            }
        }

        /**
         * The production pattern alternative count property
         * (read-only).
         *
         * @see #GetAlternativeCount
         *
         * @since 1.5
         */
        public int Count {
            get {
                return GetAlternativeCount();
            }
        }

        /**
         * The production pattern alternative index (read-only).
         *
         * @param index          the alternative index, 0 <= pos < Count
         *
         * @return the alternative found
         *
         * @see #GetAlternative
         *
         * @since 1.5
         */
        public ProductionPatternAlternative this[int index] {
            get {
                return GetAlternative(index);
            }
        }

        /**
         * Checks if the syntetic production flag is set. If this flag
         * is set, the production identified by this pattern has been
         * artificially inserted into the grammar. No parse tree nodes
         * will be created for such nodes, instead the child nodes
         * will be added directly to the parent node.
         *
         * @return true if this production pattern is syntetic, or
         *         false otherwise
         *
         * @see #Syntetic
         *
         * @deprecated Use the Syntetic property instead.
         */
        public bool IsSyntetic() {
            return syntetic;
        }

        /**
         * Checks if this pattern is recursive on the left-hand side.
         * This method checks if any of the production pattern
         * alternatives is left-recursive.
         *
         * @return true if at least one alternative is left recursive, or
         *         false otherwise
         */
        public bool IsLeftRecursive() {
            ProductionPatternAlternative  alt;

            for (int i = 0; i < alternatives.Count; i++) {
                alt = (ProductionPatternAlternative) alternatives[i];
                if (alt.IsLeftRecursive()) {
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
        public bool IsRightRecursive() {
            ProductionPatternAlternative  alt;

            for (int i = 0; i < alternatives.Count; i++) {
                alt = (ProductionPatternAlternative) alternatives[i];
                if (alt.IsRightRecursive()) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Checks if this pattern would match an empty stream of
         * tokens. This method checks if any one of the production
         * pattern alternatives would match the empty token stream.
         *
         * @return true if at least one alternative match no tokens, or
         *         false otherwise
         */
        public bool IsMatchingEmpty() {
            ProductionPatternAlternative  alt;

            for (int i = 0; i < alternatives.Count; i++) {
                alt = (ProductionPatternAlternative) alternatives[i];
                if (alt.IsMatchingEmpty()) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Returns the unique production pattern identity value.
         *
         * @return the production pattern id
         *
         * @see #Id
         *
         * @deprecated Use the Id property instead.
         */
        public int GetId() {
            return id;
        }

        /**
         * Returns the production pattern name.
         *
         * @return the production pattern name
         *
         * @see #Name
         *
         * @deprecated Use the Name property instead.
         */
        public string GetName() {
            return name;
        }

        /**
         * Sets the syntetic production pattern flag. If this flag is set,
         * the production identified by this pattern has been artificially
         * inserted into the grammar. By default this flag is set to
         * false.
         *
         * @param syntetic       the new value of the syntetic flag
         *
         * @see #Syntetic
         *
         * @deprecated Use the Syntetic property instead.
         */
        public void SetSyntetic(bool syntetic) {
            this.syntetic = syntetic;
        }

        /**
         * Returns the number of alternatives in this pattern.
         *
         * @return the number of alternatives in this pattern
         *
         * @see #Count
         *
         * @deprecated Use the Count property instead.
         */
        public int GetAlternativeCount() {
            return alternatives.Count;
        }

        /**
         * Returns an alternative in this pattern.
         *
         * @param pos            the alternative position, 0 <= pos < count
         *
         * @return the alternative found
         *
         * @deprecated Use the class indexer instead.
         */
        public ProductionPatternAlternative GetAlternative(int pos) {
            return (ProductionPatternAlternative) alternatives[pos];
        }

        /**
         * Adds a production pattern alternative.
         *
         * @param alt            the production pattern alternative to add
         *
         * @throws ParserCreationException if an identical alternative has
         *             already been added
         */
        public void AddAlternative(ProductionPatternAlternative alt) {
            if (alternatives.Contains(alt)) {
                throw new ParserCreationException(
                    ParserCreationException.ErrorType.INVALID_PRODUCTION,
                    name,
                    "two identical alternatives exist");
            }
            alt.SetPattern(this);
            alternatives.Add(alt);
        }

        /**
         * Returns a string representation of this object.
         *
         * @return a token string representation
         */
        public override string ToString() {
            StringBuilder  buffer = new StringBuilder();
            StringBuilder  indent = new StringBuilder();
            int            i;

            buffer.Append(name);
            buffer.Append("(");
            buffer.Append(id);
            buffer.Append(") ");
            for (i = 0; i < buffer.Length; i++) {
                indent.Append(" ");
            }
            for (i = 0; i < alternatives.Count; i++) {
                if (i == 0) {
                    buffer.Append("= ");
                } else {
                    buffer.Append("\n");
                    buffer.Append(indent);
                    buffer.Append("| ");
                }
                buffer.Append(alternatives[i]);
            }
            return buffer.ToString();
        }
    }
}
