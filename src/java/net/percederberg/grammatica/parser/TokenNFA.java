/*
 * TokenNFA.java
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
 * Copyright (c) 2009 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.parser;

import java.io.IOException;

/**
 * A non-deterministic finite state automaton (NFA) for matching
 * tokens. It supports both fixed strings and simple regular
 * expressions, but should perform similar to a DFA due to highly
 * optimized data structures and tuning. The memory footprint during
 * matching should be near zero, since no heap memory is allocated
 * unless the pre-allocated queues need to be enlarged. The NFA also
 * does not use recursion, but iterates in a loop instead.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 * @since    1.5
 */
class TokenNFA {

    /**
     * The initial text states, indexed by the first ASCII character.
     * This array is used to for speed optimizing the first step in
     * the match, since the initial state would otherwise have a long
     * list of transitions without any searchable index.
     */
    private State[] textStates = new State[128];

    /**
     * The initial state. This state contains any transitions not
     * already stored in the initial text state array, i.e. non-ASCII
     * or complex transitions (such as regular expressions).
     */
    private State initial = new State();

    /**
     * The NFA state queue to use.
     */
    private StateQueue queue = new StateQueue();

    /**
     * The last match found, or null for none.
     */
    private TokenPattern lastMatch = null;

    /**
     * Adds a string match to this automaton. New states and
     * transitions will be added to extend this automaton to support
     * the specified string.
     *
     * @param str              the string to match
     * @param caseInsensitive  the case-insensitive match flag
     * @param value            the match value
     */
    public void addTextMatch(String str, boolean caseInsensitive, TokenPattern value) {
        State  state;
        State  next;
        char   ch = str.charAt(0);

        if (ch < 128 && !caseInsensitive && initial.transitions.length == 0) {
            state = textStates[ch];
            if (state == null) {
                state = textStates[ch] = new State();
            }
        } else {
            if (caseInsensitive) {
                state = new State();
                initial.add(Character.toLowerCase(ch), state);
                initial.add(Character.toUpperCase(ch), state);
            } else {
                state = initial.add(ch, null);
            }
        }
        for (int i = 1; i < str.length(); i++) {
            ch = str.charAt(i);
            if (caseInsensitive) {
                next = new State();
                state.add(Character.toLowerCase(ch), next);
                state.add(Character.toUpperCase(ch), next);
                state = next;
            } else {
                state = state.add(ch, null);
            }
        }
        state.value = value;
    }

    /**
     * Checks if this NFA matches the specified input text. The
     * matching will be performed from position zero (0) in the
     * buffer. This method will not read any characters from the
     * stream, just peek ahead.
     *
     * @param buffer         the input buffer to check
     *
     * @return the number of characters matched, or
     *         zero (0) if no match was found
     *
     * @throws IOException if an I/O error occurred
     */
    public int match(ReaderBuffer buffer) throws IOException {
        State  state;
        int    offset = 1;
        int    peekChar;
        int    matchLength = 0;

        // The first step of the match loop has been unrolled and
        // optimized for performance below.
        this.lastMatch = null;
        this.queue.clear();
        peekChar = buffer.peek(0);
        if (0 <= peekChar && peekChar < 128) {
            state = this.textStates[peekChar];
            if (state != null) {
                this.queue.addLast(state);
            }
        }
        if (peekChar >= 0) {
            this.initial.matchTransitions((char) peekChar, this.queue);
        }
        this.queue.markEnd();
        peekChar = buffer.peek(1);

        // The remaining match loop processes all subsequent states
        while (!this.queue.isEmpty()) {
            if (this.queue.isMarked()) {
                offset++;
                peekChar = buffer.peek(offset);
                this.queue.markEnd();
            }
            state = this.queue.removeFirst();
            if (state.value != null && offset > matchLength) {
                matchLength = offset;
                this.lastMatch = state.value;
            }
            if (peekChar >= 0) {
                state.matchTransitions((char) peekChar, this.queue);
            }
        }
        return matchLength;
    }

    /**
     * Returns the last matched token pattern.
     *
     * @return the last matched token pattern, or
     *         null if no match has been found
     */
    public TokenPattern matchedValue() {
        return this.lastMatch;
    }


    /**
     * An NFA state. The NFA consists of a series of states, each
     * having zero or more transitions to other states.
     */
    protected static class State {

        /**
         * The optional state value (if it is a final state).
         */
        protected TokenPattern value = null;

        /**
         * The transitions from this state.
         */
        protected Transition[] transitions = new Transition[0];

        /**
         * Adds a new character transition from this state. If the
         * target state specified was null and an identical
         * transition already existed, it will be returned instead.
         *
         * @param ch         the character to match
         * @param state      the target state, or null
         *
         * @return the transition target state
         */
        public State add(char ch, State state) {
            if (state == null) {
                state = findUniqueCharTransition(ch);
                if (state != null) {
                    return state;
                }
                state = new State();
            }
            return add(new CharTransition(ch, state));
        }

        /**
         * Adds a new transition from this state.
         *
         * @param trans          the transition to add
         *
         * @return the transition target state
         */
        public State add(Transition trans) {
            Transition[]  temp = transitions;

            transitions = new Transition[temp.length + 1];
            System.arraycopy(temp, 0, transitions, 0, temp.length);
            transitions[temp.length] = trans;
            return trans.state;
        }

        /**
         * Finds a unique character transition if one exists. The
         * transition must be the only matching single character
         * transition and no other transitions may reach the same
         * state.
         *
         * @param ch             the character to search for
         *
         * @return the unique transition state found, or
         *         null if not found
         */
        private State findUniqueCharTransition(char ch) {
            Transition  res = null;
            Transition  trans;

            for (int i = 0; i < transitions.length; i++) {
                trans = transitions[i];
                if (trans.match(ch) && trans instanceof CharTransition) {
                    if (res != null) {
                        return null;
                    }
                    res = trans;
                }
            }
            for (int i = 0; res != null && i < transitions.length; i++) {
                trans = transitions[i];
                if (trans != res && trans.state == res.state) {
                    return null;
                }
            }
            return (res == null) ? null : res.state;
        }

        /**
         * Attempts a match on each of the transitions leading from
         * this state. If a match is found, its state will be added
         * to the queue.
         *
         * @param ch         the character to match
         * @param queue      the state queue
         */
        public void matchTransitions(char ch, StateQueue queue) {
            for (int i = 0; i < transitions.length; i++) {
                if (transitions[i].match(ch)) {
                    queue.addLast(transitions[i].state);
                }
            }
        }
    }


    /**
     * An NFA state transition. A transition checks a single
     * character of input an determines if it is a match. If a match
     * is encountered, the NFA should move forward to the transition
     * state.
     */
    protected static abstract class Transition {

        /**
         * The target state of the transition.
         */
        protected State state;

        /**
         * Checks if the specified character matches the transition.
         *
         * @param ch             the character to check
         *
         * @return true if the character matches, or
         *         false otherwise
         */
        public abstract boolean match(char ch);
    }


    /**
     * A single character match transition.
     */
    protected static class CharTransition extends Transition {

        /**
         * The character to match.
         */
        protected char match;

        /**
         * Creates a new character transition.
         *
         * @param match          the character to match
         * @param state          the target state
         */
        public CharTransition(char match, State state) {
            this.match = match;
            this.state = state;
        }

        /**
         * Checks if the specified character matches the transition.
         *
         * @param ch             the character to check
         *
         * @return true if the character matches, or
         *         false otherwise
         */
        public boolean match(char ch) {
            return this.match == ch;
        }
    }


    /**
     * An NFA state queue. This queue is used during processing to
     * keep track of the current and subsequent NFA states. The
     * current state is read from the beginning of the queue, and new
     * states are added at the end. A marker index is used to
     * separate the current from the subsequent states.<p>
     *
     * The queue implementation is optimized for quick removal at the
     * beginning and addition at the end. It will attempt to use a
     * fixed-size array to store the whole queue, and moves the data
     * in this array only when absolutely needed. The array is also
     * enlarged automatically if too many states are being processed
     * at a single time.
     */
    protected static class StateQueue {

        /**
         * The state queue array. Will be enlarged as needed.
         */
        private State[] queue = new State[2048];

        /**
         * The position of the first entry in the queue (inclusive).
         */
        private int first = 0;

        /**
         * The position just after the last entry in the queue
         * (exclusive).
         */
        private int last = 0;

        /**
         * The current queue mark position.
         */
        private int mark = 0;

        /**
         * Checks if the queue is empty.
         *
         * @return true if the queue is empty, or
         *         false otherwise
         */
        public boolean isEmpty() {
            return (last <= first);
        }

        /**
         * Checks if the first entry in the queue has been marked.
         *
         * @return true if the first entry is marked, or
         *         false otherwise
         */
        public boolean isMarked() {
            return first == mark;
        }

        /**
         * Clears this queue. This operation is fast, as it just
         * resets the queue position indices.
         */
        public void clear() {
            first = 0;
            last = 0;
            mark = 0;
        }

        /**
         * Marks the end of the queue. This means that the next entry
         * added to the queue will be marked (when it becomes the
         * first in the queue). This operation is fast.
         */
        public void markEnd() {
            mark = last;
        }

        /**
         * Removes and returns the first entry in the queue. This
         * operation is fast, since it will only update the index of
         * the first entry in the queue.
         *
         * @return the previous first entry in the queue
         */
        public State removeFirst() {
            if (first < last) {
                first++;
                return queue[first - 1];
            } else {
                return null;
            }
        }

        /**
         * Adds a new entry at the end of the queue. This operation
         * is mostly fast, unless all the allocated queue space has
         * already been used. 
         *
         * @param state          the state to add
         */
        public void addLast(State state) {
            if (last >= queue.length) {
                if (first <= 0) {
                    State[] temp = queue;
                    queue = new State[temp.length * 2];
                    System.arraycopy(temp, 0, queue, 0, temp.length);
                } else {
                    System.arraycopy(queue, first, queue, 0, last - first);
                    last -= first;
                    mark -= first;
                    first = 0;
                }
            }
            queue[last++] = state;
        }
    }
}
