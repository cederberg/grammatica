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

import net.percederberg.grammatica.parser.re.RegExpException;

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
     * The initial state lookup table, indexed by the first ASCII
     * character. This array is used to for speed optimizing the
     * first step in the match, since the initial state would
     * otherwise have a long list of transitions to consider.
     */
    private State[] initialChar = new State[128];

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
     * Adds a string match to this automaton. New states and
     * transitions will be added to extend this automaton to support
     * the specified string.
     *
     * @param str            the string to match
     * @param ignoreCase     the case-insensitive match flag
     * @param value          the match value
     */
    public void addTextMatch(String str, boolean ignoreCase, TokenPattern value) {
        State  state;
        char   ch = str.charAt(0);

        if (ch < 128 && !ignoreCase) {
            state = initialChar[ch];
            if (state == null) {
                state = initialChar[ch] = new State();
            }
        } else {
            state = initial.addOut(ch, ignoreCase, null);
        }
        for (int i = 1; i < str.length(); i++) {
            state = state.addOut(str.charAt(i), ignoreCase, null);
        }
        state.value = value;
    }

    /**
     * Adds a regular expression match to this automaton. New states
     * and transitions will be added to extend this automaton to
     * support the specified string. Note that this method only
     * supports a subset of the full regular expression syntax, so
     * a more complete regular expression library must also be
     * provided.
     *
     * @param pattern        the regular expression string
     * @param ignoreCase     the case-insensitive match flag
     * @param value          the match value
     *
     * @throws RegExpException if the regular expression parsing
     *             failed
     */
    public void addRegExpMatch(String pattern,
                               boolean ignoreCase,
                               TokenPattern value)
    throws RegExpException {

        TokenRegExpParser  parser = new TokenRegExpParser(pattern, ignoreCase);
        String             debug = "DFA regexp; " + parser.getDebugInfo();
        boolean            isAscii;

        isAscii = parser.start.isAsciiOutgoing();
        for (int i = 0; isAscii && i < 128; i++) {
            boolean  match = false;
            for (int j = 0; j < parser.start.outgoing.length; j++) {
                if (parser.start.outgoing[j].match((char) i)) {
                    if (match) {
                        isAscii = false;
                        break;
                    }
                    match = true;
                }
            }
            if (match && initialChar[i] != null) {
                isAscii = false;
            }
        }
        if (parser.start.incoming.length > 0) {
            initial.addOut(new EpsilonTransition(parser.start));
            debug += ", uses initial epsilon";
        } else if (isAscii && !ignoreCase) {
            for (int i = 0; isAscii && i < 128; i++) {
                for (int j = 0; j < parser.start.outgoing.length; j++) {
                    if (parser.start.outgoing[j].match((char) i)) {
                        initialChar[i] = parser.start.outgoing[j].state;
                    }
                }
            }
            debug += ", uses ASCII lookup";
        } else {
            parser.start.mergeInto(initial);
            debug += ", uses initial state";
        }
        parser.end.value = value;
        value.setDebugInfo(debug);
    }

    /**
     * Checks if this NFA matches the specified input text. The
     * matching will be performed from position zero (0) in the
     * buffer. This method will not read any characters from the
     * stream, just peek ahead.
     *
     * @param buffer         the input buffer to check
     * @param match          the token match to update
     *
     * @return the number of characters matched, or
     *         zero (0) if no match was found
     *
     * @throws IOException if an I/O error occurred
     */
    public int match(ReaderBuffer buffer, TokenMatch match) throws IOException {
        int           length = 0;
        int           pos = 1;
        int           peekChar;
        State         state;

        // The first step of the match loop has been unrolled and
        // optimized for performance below.
        this.queue.clear();
        peekChar = buffer.peek(0);
        if (0 <= peekChar && peekChar < 128) {
            state = this.initialChar[peekChar];
            if (state != null) {
                this.queue.addLast(state);
            }
        }
        if (peekChar >= 0) {
            this.initial.matchTransitions((char) peekChar, this.queue, true);
        }
        this.queue.markEnd();
        peekChar = buffer.peek(1);

        // The remaining match loop processes all subsequent states
        while (!this.queue.isEmpty()) {
            if (this.queue.isMarked()) {
                pos++;
                peekChar = buffer.peek(pos);
                this.queue.markEnd();
            }
            state = this.queue.removeFirst();
            if (state.value != null) {
                match.update(pos, state.value);
            }
            if (peekChar >= 0) {
                state.matchTransitions((char) peekChar, this.queue, false);
            }
        }
        return length;
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
         * The incoming transitions to this state.
         */
        protected Transition[] incoming = new Transition[0];

        /**
         * The outgoing transitions from this state.
         */
        protected Transition[] outgoing = new Transition[0];

        /**
         * The outgoing epsilon transitions flag.
         */
        protected boolean epsilonOut = false;

        /**
         * Checks if this state has any incoming or outgoing
         * transitions.
         *
         * @return true if this state has transitions, or
         *         false otherwise
         */
        public boolean hasTransitions() {
            return incoming.length > 0 || outgoing.length > 0;
        }

        /**
         * Checks if all outgoing transitions only match ASCII
         * characters.
         *
         * @return true if all transitions are ASCII-only, or
         *         false otherwise
         */
        public boolean isAsciiOutgoing() {
            for (int i = 0; i < outgoing.length; i++) {
                if (!outgoing[i].isAscii()) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Adds a new incoming transition.
         *
         * @param trans          the transition to add
         */
        public void addIn(Transition trans) {
            Transition[]  temp = incoming;

            incoming = new Transition[temp.length + 1];
            System.arraycopy(temp, 0, incoming, 0, temp.length);
            incoming[temp.length] = trans;
        }

        /**
         * Adds a new outgoing character transition. If the target
         * state specified was null and an identical transition
         * already exists, it will be reused and its target returned.
         *
         * @param ch             he character to match
         * @param ignoreCase     the case-insensitive flag
         * @param state          the target state, or null
         *
         * @return the transition target state
         */
        public State addOut(char ch, boolean ignoreCase, State state) {
            if (ignoreCase) {
                if (state == null) {
                    state = new State();
                }
                addOut(new CharTransition(Character.toLowerCase(ch), state));
                addOut(new CharTransition(Character.toUpperCase(ch), state));
                return state;
            } else { 
                if (state == null) {
                    state = findUniqueCharTransition(ch);
                    if (state != null) {
                        return state;
                    }
                    state = new State();
                }
                return addOut(new CharTransition(ch, state));
            }
        }

        /**
         * Adds a new outgoing transition.
         *
         * @param trans          the transition to add
         *
         * @return the transition target state
         */
        public State addOut(Transition trans) {
            Transition[]  temp = outgoing;

            outgoing = new Transition[temp.length + 1];
            System.arraycopy(temp, 0, outgoing, 0, temp.length);
            outgoing[temp.length] = trans;
            if (trans instanceof EpsilonTransition) {
                epsilonOut = true;
            }
            return trans.state;
        }

        /**
         * Merges all the transitions in this state into another
         * state.
         *
         * @param state      the state to merge into
         */
        public void mergeInto(State state) {
            for (int i = 0; i < incoming.length; i++) {
                state.addIn(incoming[i]);
                incoming[i].state = state;
            }
            incoming = null;
            for (int i = 0; i < outgoing.length; i++) {
                state.addOut(outgoing[i]);
            }
            outgoing = null;
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

            for (int i = 0; i < outgoing.length; i++) {
                trans = outgoing[i];
                if (trans.match(ch) && trans instanceof CharTransition) {
                    if (res != null) {
                        return null;
                    }
                    res = trans;
                }
            }
            for (int i = 0; res != null && i < outgoing.length; i++) {
                trans = outgoing[i];
                if (trans != res && trans.state == res.state) {
                    return null;
                }
            }
            return (res == null) ? null : res.state;
        }

        /**
         * Attempts a match on each of the transitions leading from
         * this state. If a match is found, its state will be added
         * to the queue. If the initial match flag is set, epsilon
         * transitions will also be matched (and their targets called
         * recursively).
         *
         * @param ch         the character to match
         * @param queue      the state queue
         * @param initial    the initial match flag
         */
        public void matchTransitions(char ch, StateQueue queue, boolean initial) {
            Transition  trans;
            State       target;

            for (int i = 0; i < outgoing.length; i++) {
                trans = outgoing[i];
                target = trans.state;
                if (initial && trans instanceof EpsilonTransition) {
                    target.matchTransitions(ch, queue, true);
                } else if (trans.match(ch)) {
                    queue.addLast(target);
                    if (target.epsilonOut) {
                        target.matchEmpty(queue);
                    }
                }
            }
        }

        /**
         * Adds all the epsilon transition targets to the specified
         * queue.
         *
         * @param queue      the state queue
         */
        public void matchEmpty(StateQueue queue) {
            Transition  trans;
            State       target;

            for (int i = 0; i < outgoing.length; i++) {
                trans = outgoing[i];
                if (trans instanceof EpsilonTransition) {
                    target = trans.state;
                    queue.addLast(target);
                    if (target.epsilonOut) {
                        target.matchEmpty(queue);
                    }
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
         * Creates a new state transition.
         *
         * @param state          the target state
         */
        public Transition(State state) {
            this.state = state;
            this.state.addIn(this);
        }

        /**
         * Checks if this transition only matches ASCII characters.
         * I.e. characters with numeric values between 0 and 127.
         *
         * @return true if this transition only matches ASCII, or
         *         false otherwise
         */
        public abstract boolean isAscii();

        /**
         * Checks if the specified character matches the transition.
         *
         * @param ch             the character to check
         *
         * @return true if the character matches, or
         *         false otherwise
         */
        public abstract boolean match(char ch);

        /**
         * Creates a copy of this transition but with another target
         * state.
         *
         * @param state          the new target state
         *
         * @return an identical copy of this transition
         */
        public abstract Transition copy(State state);
    }


    /**
     * The special epsilon transition. This transition matches the
     * empty input, i.e. it is an automatic transition that doesn't
     * read any input. As such, it returns false in the match method
     * and is handled specially everywhere.
     */
    protected static class EpsilonTransition extends Transition {

        /**
         * Creates a new epsilon transition.
         *
         * @param state          the target state
         */
        public EpsilonTransition(State state) {
            super(state);
        }

        /**
         * Checks if this transition only matches ASCII characters.
         * I.e. characters with numeric values between 0 and 127.
         *
         * @return true if this transition only matches ASCII, or
         *         false otherwise
         */
        public boolean isAscii() {
            return false;
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
            return false;
        }

        /**
         * Creates a copy of this transition but with another target
         * state.
         *
         * @param state          the new target state
         *
         * @return an identical copy of this transition
         */
        public Transition copy(State state) {
            return new EpsilonTransition(state);
        }
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
            super(state);
            this.match = match;
        }

        /**
         * Checks if this transition only matches ASCII characters.
         * I.e. characters with numeric values between 0 and 127.
         *
         * @return true if this transition only matches ASCII, or
         *         false otherwise
         */
        public boolean isAscii() {
            return 0 <= match && match < 128;
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

        /**
         * Creates a copy of this transition but with another target
         * state.
         *
         * @param state          the new target state
         *
         * @return an identical copy of this transition
         */
        public Transition copy(State state) {
            return new CharTransition(match, state);
        }
    }


    /**
     * A character range match transition. Used for user-defined
     * character sets in regular expressions.
     */
    protected static class CharRangeTransition extends Transition {

        /**
         * The inverse match flag.
         */
        protected boolean inverse;

        /**
         * The case-insensitive match flag.
         */
        protected boolean ignoreCase;

        /**
         * The character set content. This array may contain either
         * range objects or Character objects.
         */
        private Object[] contents = new Object[0];

        /**
         * Creates a new character range transition.
         *
         * @param inverse        the inverse match flag
         * @param ignoreCase     the case-insensitive match flag
         * @param state          the target state
         */
        public CharRangeTransition(boolean inverse, boolean ignoreCase, State state) {
            super(state);
            this.inverse = inverse;
            this.ignoreCase = ignoreCase;
        }

        /**
         * Checks if this transition only matches ASCII characters.
         * I.e. characters with numeric values between 0 and 127.
         *
         * @return true if this transition only matches ASCII, or
         *         false otherwise
         */
        public boolean isAscii() {
            Object     obj;
            Character  c;

            if (inverse) {
                return false;
            }
            for (int i = 0; i < contents.length; i++) {
                obj = contents[i];
                if (obj instanceof Character) {
                    c = (Character) obj;
                    if (c.charValue() < 0 || 128 <= c.charValue()) {
                        return false;
                    }
                } else if (obj instanceof Range) {
                    if (!((Range) obj).isAscii()) {
                        return false;
                    }
                }
            }
            return true;
        }

        /**
         * Adds a single character to this character set.
         *
         * @param c              the character to add
         */
        public void addCharacter(char c) {
            if (ignoreCase) {
                c = Character.toLowerCase(c);
            }
            addContent(new Character(c));
        }

        /**
         * Adds a character range to this character set.
         *
         * @param min            the minimum character value
         * @param max            the maximum character value
         */
        public void addRange(char min, char max) {
            if (ignoreCase) {
                min = Character.toLowerCase(min);
                max = Character.toLowerCase(max);
            }
            addContent(new Range(min, max));
        }

        /**
         * Adds an object to the character set content array.
         *
         * @param obj            the object to add
         */
        private void addContent(Object obj) {
            Object[]  temp = contents;

            contents = new Object[temp.length + 1];
            System.arraycopy(temp, 0, contents, 0, temp.length);
            contents[temp.length] = obj;
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
            Object     obj;
            Character  c;
            Range      r;

            if (ignoreCase) {
                ch = Character.toLowerCase(ch);
            }
            for (int i = 0; i < contents.length; i++) {
                obj = contents[i];
                if (obj instanceof Character) {
                    c = (Character) obj;
                    if (c.charValue() == ch) {
                        return !inverse;
                    }
                } else if (obj instanceof Range) {
                    r = (Range) obj;
                    if (r.inside(ch)) {
                        return !inverse;
                    }
                }
            }
            return inverse;
        }

        /**
         * Creates a copy of this transition but with another target
         * state.
         *
         * @param state          the new target state
         *
         * @return an identical copy of this transition
         */
        public Transition copy(State state) {
            CharRangeTransition  copy;

            copy = new CharRangeTransition(inverse, ignoreCase, state);
            copy.contents = contents;
            return copy;
        }

        /**
         * A character range class.
         */
        private class Range {

            /**
             * The minimum character value.
             */
            private char min;

            /**
             * The maximum character value.
             */
            private char max;

            /**
             * Creates a new character range.
             *
             * @param min        the minimum character value
             * @param max        the maximum character value
             */
            public Range(char min, char max) {
                this.min = min;
                this.max = max;
            }

            /**
             * Checks if this range only matches ASCII characters
             *
             * @return true if this range only matches ASCII, or
             *         false otherwise
             */
            public boolean isAscii() {
                return 0 <= min && min < 128 &&
                       0 <= max && max < 128;
            }

            /**
             * Checks if the specified character is inside the range.
             *
             * @param c          the character to check
             *
             * @return true if the character is in the range, or
             *         false otherwise
             */
            public boolean inside(char c) {
                return min <= c && c <= max;
            }
        }
    }


    /**
     * The dot ('.') character set transition. This transition
     * matches a single character that is not equal to a newline
     * character.
     */
    protected static class DotTransition extends Transition {

        /**
         * Creates a new dot character set transition.
         *
         * @param state          the target state
         */
        public DotTransition(State state) {
            super(state);
        }

        /**
         * Checks if this transition only matches ASCII characters.
         * I.e. characters with numeric values between 0 and 127.
         *
         * @return true if this transition only matches ASCII, or
         *         false otherwise
         */
        public boolean isAscii() {
            return false;
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
            switch (ch) {
            case '\n':
            case '\r':
            case '\u0085':
            case '\u2028':
            case '\u2029':
                return false;
            default:
                return true;
            }
        }

        /**
         * Creates a copy of this transition but with another target
         * state.
         *
         * @param state          the new target state
         *
         * @return an identical copy of this transition
         */
        public Transition copy(State state) {
            return new DotTransition(state);
        }
    }


    /**
     * The digit character set transition. This transition matches a
     * single numeric character.
     */
    protected static class DigitTransition extends Transition {

        /**
         * Creates a new digit character set transition.
         *
         * @param state          the target state
         */
        public DigitTransition(State state) {
            super(state);
        }

        /**
         * Checks if this transition only matches ASCII characters.
         * I.e. characters with numeric values between 0 and 127.
         *
         * @return true if this transition only matches ASCII, or
         *         false otherwise
         */
        public boolean isAscii() {
            return true;
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
            return '0' <= ch && ch <= '9';
        }

        /**
         * Creates a copy of this transition but with another target
         * state.
         *
         * @param state          the new target state
         *
         * @return an identical copy of this transition
         */
        public Transition copy(State state) {
            return new DigitTransition(state);
        }
    }


    /**
     * The non-digit character set transition. This transition
     * matches a single non-numeric character.
     */
    protected static class NonDigitTransition extends Transition {

        /**
         * Creates a new non-digit character set transition.
         *
         * @param state          the target state
         */
        public NonDigitTransition(State state) {
            super(state);
        }

        /**
         * Checks if this transition only matches ASCII characters.
         * I.e. characters with numeric values between 0 and 127.
         *
         * @return true if this transition only matches ASCII, or
         *         false otherwise
         */
        public boolean isAscii() {
            return false;
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
            return ch < '0' || '9' < ch;
        }

        /**
         * Creates a copy of this transition but with another target
         * state.
         *
         * @param state          the new target state
         *
         * @return an identical copy of this transition
         */
        public Transition copy(State state) {
            return new NonDigitTransition(state);
        }
    }


    /**
     * The whitespace character set transition. This transition
     * matches a single whitespace character.
     */
    protected static class WhitespaceTransition extends Transition {

        /**
         * Creates a new whitespace character set transition.
         *
         * @param state          the target state
         */
        public WhitespaceTransition(State state) {
            super(state);
        }

        /**
         * Checks if this transition only matches ASCII characters.
         * I.e. characters with numeric values between 0 and 127.
         *
         * @return true if this transition only matches ASCII, or
         *         false otherwise
         */
        public boolean isAscii() {
            return true;
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
            switch (ch) {
            case ' ':
            case '\t':
            case '\n':
            case '\f':
            case '\r':
            case 11:
                return true;
            default:
                return false;
            }
        }

        /**
         * Creates a copy of this transition but with another target
         * state.
         *
         * @param state          the new target state
         *
         * @return an identical copy of this transition
         */
        public Transition copy(State state) {
            return new WhitespaceTransition(state);
        }
    }


    /**
     * The non-whitespace character set transition. This transition
     * matches a single non-whitespace character.
     */
    protected static class NonWhitespaceTransition extends Transition {

        /**
         * Creates a new non-whitespace character set transition.
         *
         * @param state          the target state
         */
        public NonWhitespaceTransition(State state) {
            super(state);
        }

        /**
         * Checks if this transition only matches ASCII characters.
         * I.e. characters with numeric values between 0 and 127.
         *
         * @return true if this transition only matches ASCII, or
         *         false otherwise
         */
        public boolean isAscii() {
            return false;
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
            switch (ch) {
            case ' ':
            case '\t':
            case '\n':
            case '\f':
            case '\r':
            case 11:
                return false;
            default:
                return true;
            }
        }

        /**
         * Creates a copy of this transition but with another target
         * state.
         *
         * @param state          the new target state
         *
         * @return an identical copy of this transition
         */
        public Transition copy(State state) {
            return new NonWhitespaceTransition(state);
        }
    }


    /**
     * The word character set transition. This transition matches a
     * single word character.
     */
    protected static class WordTransition extends Transition {

        /**
         * Creates a new word character set transition.
         *
         * @param state          the target state
         */
        public WordTransition(State state) {
            super(state);
        }

        /**
         * Checks if this transition only matches ASCII characters.
         * I.e. characters with numeric values between 0 and 127.
         *
         * @return true if this transition only matches ASCII, or
         *         false otherwise
         */
        public boolean isAscii() {
            return true;
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
            return ('a' <= ch && ch <= 'z')
                || ('A' <= ch && ch <= 'Z')
                || ('0' <= ch && ch <= '9')
                || ch == '_';
        }

        /**
         * Creates a copy of this transition but with another target
         * state.
         *
         * @param state          the new target state
         *
         * @return an identical copy of this transition
         */
        public Transition copy(State state) {
            return new WordTransition(state);
        }
    }


    /**
     * The non-word character set transition. This transition matches
     * a single non-word character.
     */
    protected static class NonWordTransition extends Transition {

        /**
         * Creates a new non-word character set transition.
         *
         * @param state          the target state
         */
        public NonWordTransition(State state) {
            super(state);
        }

        /**
         * Checks if this transition only matches ASCII characters.
         * I.e. characters with numeric values between 0 and 127.
         *
         * @return true if this transition only matches ASCII, or
         *         false otherwise
         */
        public boolean isAscii() {
            return false;
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
            boolean word = ('a' <= ch && ch <= 'z')
                        || ('A' <= ch && ch <= 'Z')
                        || ('0' <= ch && ch <= '9')
                        || ch == '_';
            return !word;
        }

        /**
         * Creates a copy of this transition but with another target
         * state.
         *
         * @param state          the new target state
         *
         * @return an identical copy of this transition
         */
        public Transition copy(State state) {
            return new NonWordTransition(state);
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
