/*
 * Automaton.cs
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
 * Copyright (c) 2004 Per Cederberg. All rights reserved.
 */

using System;

namespace PerCederberg.Grammatica.Parser {

    /**
     * A deterministic finite state automaton. This is a simple
     * automaton for character sequences, currently used for string
     * token patterns. It only handles single character transitions
     * between states, but supports running in an all case-insensitive
     * mode.
     *
     * @author   Per Cederberg, <per at percederberg dot net>
     * @version  1.5
     * @since    1.5
     */
    internal class Automaton {

        /**
         * The state value.
         */
        private object value = null;

        /**
         * The automaton state transition tree. Each transition from
         * this state to another state is added to this tree with the
         * corresponding character.
         */
        private AutomatonTree tree = new AutomatonTree();

        /**
         * Creates a new empty automaton.
         */
        public Automaton() {
        }

        /**
         * Adds a string match to this automaton. New states and
         * transitions will be added to extend this automaton to
         * support the specified string.
         *
         * @param m              the string matcher to use
         * @param str            the string to match
         * @param value          the match value
         */
        public void AddMatch(StringMatcher m, string str, object value) {
            Automaton  state;

            if (str.Equals("")) {
                this.value = value;
            } else {
                state = tree.Find(str[0], m.IsCaseInsensitive());
                if (state == null) {
                    state = new Automaton();
                    state.AddMatch(m, str.Substring(1), value);
                    tree.Add(str[0], m.IsCaseInsensitive(), state);
                } else {
                    state.AddMatch(m, str.Substring(1), value);
                }
            }
        }

        /**
         * Checks if the automaton matches a string. The matching will
         * be performed from a specified position. This method will
         * set the end of string flag in the specified matcher if the
         * end of the string is reached. The comparison can be done
         * either in case-sensitive or case-insensitive mode.
         *
         * @param m              the string matcher to use
         * @param buffer         the string buffer to check
         * @param pos            the starting position
         *
         * @return the match value, or
         *         null if no match was found
         */
        public object MatchFrom(StringMatcher m, string str, int pos) {
            object     result = null;
            Automaton  state;

            if (pos >= str.Length) {
                m.SetReadEndOfString();
            } else if (tree != null) {
                state = tree.Find(str[pos], m.IsCaseInsensitive());
                if (state != null) {
                    result = state.MatchFrom(m, str, pos + 1);
                }
            }
            return (result == null) ? value : result;
        }
    }

    /**
     * An automaton state transition tree. This class contains a
     * binary search tree for the automaton transitions from one state
     * to another. All transitions are linked to a single character.
     *
     * @author   Per Cederberg, <per at percederberg dot net>
     * @version  1.5
     * @since    1.5
     */
    internal class AutomatonTree {

        /**
         * The transition character. If this value is set to the zero
         * character ('\0'), this tree is empty.
         */
        private char value = '\0';

        /**
         * The transition state.
         */
        private Automaton state = null;

        /**
         * The left subtree.
         */
        private AutomatonTree left = null;

        /**
         * The right subtree.
         */
        private AutomatonTree right = null;

        /**
         * Creates a new empty automaton transition tree.
         */
        public AutomatonTree() {
        }

        /**
         * Finds an automaton state from the specified transition
         * character. This method searches this transition tree for a
         * matching transition. The comparison can optionally be done
         * with a lower-case conversion of the character.
         *
         * @param c              the character to search for
         * @param lowerCase      the lower-case conversion flag
         *
         * @return the automaton state found, or
         *         null if no transition exists
         */
        public Automaton Find(char c, bool lowerCase) {
            if (lowerCase) {
                c = Char.ToLower(c);
            }
            if (value == '\0' || value == c) {
                return state;
            } else if (value > c) {
                return left.Find(c, false);
            } else {
                return right.Find(c, false);
            }
        }

        /**
         * Adds a transition to this tree. If the lower-case flag is
         * set, the character will be converted to lower-case before
         * being added.
         *
         * @param c              the character to transition for
         * @param lowerCase      the lower-case conversion flag
         * @param state          the state to transition to
         */
        public void Add(char c, bool lowerCase, Automaton state) {
            if (lowerCase) {
                c = Char.ToLower(c);
            }
            if (value == '\0') {
                this.value = c;
                this.state = state;
                this.left = new AutomatonTree();
                this.right = new AutomatonTree();
            } else if (value > c) {
                left.Add(c, false, state);
            } else {
                right.Add(c, false, state);
            }
        }
    }
}
