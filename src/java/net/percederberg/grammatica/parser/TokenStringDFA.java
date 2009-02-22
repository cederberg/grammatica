/*
 * TokenStringDFA.java
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
 * Copyright (c) 2004-2009 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.parser;

import java.io.IOException;

/**
 * A deterministic finite state automaton for matching exact strings.
 * It uses a sorted binary tree representation of the state
 * transitions in order to enable quick matches with a minimal memory
 * footprint. It only supports a single character transition between
 * states, but may be run in an all case-insensitive mode.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 * @since    1.5
 */
class TokenStringDFA {

    /**
     * The lookup table for root states, indexed by the first ASCII
     * character. This array is used to for speed optimizing the
     * first step in the match.
     */
    private State[] ascii = new State[128];

    /**
     * The automaton state transition tree for non-ASCII characters.
     * Each transition from one state to another is added to the tree
     * with the corresponding character.
     */
    private State nonAscii = new State();

    /**
     * Adds a string match to this automaton. New states and
     * transitions will be added to extend this automaton to support
     * the specified string.
     *
     * @param str              the string to match
     * @param caseInsensitive  the case-insensitive match flag
     * @param value            the match value
     */
    public void addMatch(String str, boolean caseInsensitive, TokenPattern value) {
        State  state;
        State  next;
        char   c = str.charAt(0);
        int    start = 0;

        if (caseInsensitive) {
            c = Character.toLowerCase(c);
        }
        if (c < 128) {
            state = ascii[c];
            if (state == null) {
                state = ascii[c] = new State();
            }
            start++;
        } else {
            state = nonAscii;
        }
        for (int i = start; i < str.length(); i++) {
            next = state.tree.find(str.charAt(i), caseInsensitive);
            if (next == null) {
                next = new State();
                state.tree.add(str.charAt(i), caseInsensitive, next);
            }
            state = next;
        }
        state.value = value;
    }

    /**
     * Checks if the automaton matches an input stream. The matching
     * will be performed from a specified position. This method will
     * not read any characters from the stream, just peek ahead. The
     * comparison can be done either in case-sensitive or
     * case-insensitive mode.
     *
     * @param buffer           the input buffer to check
     * @param caseInsensitive  the case-insensitive match flag
     *
     * @return the match value, or
     *         null if no match was found
     *
     * @throws IOException if an I/O error occurred
     */
    public TokenPattern match(ReaderBuffer buffer, boolean caseInsensitive)
        throws IOException {

        TokenPattern  result = null;
        State         state;
        int           pos = 0;
        int           c;

        c = buffer.peek(0);
        if (c < 0) {
            return null;
        }
        if (caseInsensitive) {
            c = Character.toLowerCase(c);
        }
        if (c < 128) {
            state = ascii[c];
            if (state == null) {
                return null;
            } else if (state.value != null) {
                result = state.value;
            }
            pos++;
        } else {
            state = nonAscii;
        }
        while ((c = buffer.peek(pos)) >= 0) {
            state = state.tree.find((char) c, caseInsensitive);
            if (state == null) {
                break;
            } else if (state.value != null) {
                result = state.value;
            }
            pos++;
        }
        return result;
    }

    /**
     * Returns a detailed string representation of this automaton.
     *
     * @return a detailed string representation of this automaton
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer  buffer = new StringBuffer();

        for (int i = 0; i < ascii.length; i++) {
            if (ascii[i] != null) {
                buffer.append((char) i);
                if (ascii[i].value != null) {
                    buffer.append(": ");
                    buffer.append(ascii[i].value);
                    buffer.append("\n");
                }
                ascii[i].tree.printTo(buffer, " ");
            }
        }
        nonAscii.tree.printTo(buffer, "");
        return buffer.toString();
    }


    /**
     * An automaton state. This class represents a state in the DFA
     * graph.
     *
     * @author   Per Cederberg, <per at percederberg dot net>
     * @version  1.5
     * @since    1.5
     */
    class State {

        /**
         * The token pattern matched at this state.
         */
        protected TokenPattern value = null;

        /**
         * The automaton state transition tree. Each transition from one
         * state to another is added to the tree with the corresponding
         * character.
         */
        protected TransitionTree tree = new TransitionTree();
    }


    /**
     * An automaton state transition tree. This class contains a
     * binary search tree for the automaton transitions from one
     * state to another. All transitions are linked to a single
     * character.
     *
     * @author   Per Cederberg, <per at percederberg dot net>
     * @version  1.5
     * @since    1.5
     */
    class TransitionTree {

        /**
         * The transition character. If this value is set to the zero
         * character ('\0'), this tree is empty.
         */
        private char value = '\0';

        /**
         * The transition target state.
         */
        private State state = null;

        /**
         * The left subtree.
         */
        private TransitionTree left = null;

        /**
         * The right subtree.
         */
        private TransitionTree right = null;

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
        public State find(char c, boolean lowerCase) {
            if (lowerCase) {
                c = Character.toLowerCase(c);
            }
            if (value == '\0' || value == c) {
                return state;
            } else if (value > c) {
                return left.find(c, false);
            } else {
                return right.find(c, false);
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
        public void add(char c, boolean lowerCase, State state) {
            if (lowerCase) {
                c = Character.toLowerCase(c);
            }
            if (value == '\0') {
                this.value = c;
                this.state = state;
                this.left = new TransitionTree();
                this.right = new TransitionTree();
            } else if (value > c) {
                left.add(c, false, state);
            } else {
                right.add(c, false, state);
            }
        }

        /**
         * Prints the automaton tree to the specified string buffer.
         *
         * @param buffer         the string buffer
         * @param indent         the current indentation
         */
        public void printTo(StringBuffer buffer, String indent) {
            if (this.left != null) {
                this.left.printTo(buffer, indent);
            }
            if (this.value != '\0') {
                if (buffer.length() > 0 &&
                    buffer.charAt(buffer.length() - 1) == '\n') {
                    buffer.append(indent);
                }
                buffer.append(this.value);
                if (this.state.value != null) {
                    buffer.append(": ");
                    buffer.append(this.state.value);
                    buffer.append("\n");
                }
                this.state.tree.printTo(buffer, indent + " ");
            }
            if (this.right != null) {
                this.right.printTo(buffer, indent);
            }
        }
    }
}
