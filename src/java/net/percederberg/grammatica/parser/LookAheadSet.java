/*
 * LookAheadSet.java
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

import java.util.ArrayList;

/**
 * A token look-ahead set. This class contains a set of token id
 * sequences. All sequences in the set are limited in length, so that
 * no single sequence is longer than a maximum value. This class also
 * filters out duplicates. Each token sequence also contains a repeat
 * flag, allowing the look-ahead set to contain information about
 * possible infinite repetitions of certain sequences. That
 * information is important when conflicts arise between two
 * look-ahead sets, as such a conflict cannot be resolved if the
 * conflicting sequences can be repeated (would cause infinite loop).
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
class LookAheadSet {

    /**
     * The set of token look-ahead sequences. Each sequence in
     * turn is represented by an ArrayList with Integers for the
     * token id:s.
     */
    private ArrayList elements = new ArrayList();

    /**
     * The maximum length of any look-ahead sequence.
     */
    private int maxLength;

    /**
     * Creates a new look-ahead set with the specified maximum
     * length.
     *
     * @param maxLength      the maximum token sequence length
     */
    public LookAheadSet(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * Creates a duplicate look-ahead set, possibly with a different
     * maximum length.
     *
     * @param maxLength      the maximum token sequence length
     * @param set            the look-ahead set to copy
     */
    public LookAheadSet(int maxLength, LookAheadSet set) {
        this(maxLength);
        addAll(set);
    }

    /**
     * Checks if this look-ahead set is empty.
     *
     * @return true if this look-ahead set is empty, or
     *         false otherwise
     *
     * @since 1.5
     */
    public boolean isEmpty() {
        return elements.size() == 0;
    }

    /**
     * Returns the length of the shortest token sequence in this
     * set. This method will return zero (0) if the set is empty.
     *
     * @return the length of the shortest token sequence
     */
    public int getMinLength() {
        Sequence  seq;
        int       min = -1;

        for (int i = 0; i < elements.size(); i++) {
            seq = (Sequence) elements.get(i);
            if (min < 0 || seq.length() < min) {
                min = seq.length();
            }
        }
        return (min < 0) ? 0 : min;
    }

    /**
     * Returns the length of the longest token sequence in this
     * set. This method will return zero (0) if the set is empty.
     *
     * @return the length of the longest token sequence
     */
    public int getMaxLength() {
        Sequence  seq;
        int       max = 0;

        for (int i = 0; i < elements.size(); i++) {
            seq = (Sequence) elements.get(i);
            if (seq.length() > max) {
                max = seq.length();
            }
        }
        return max;
    }

    /**
     * Returns a list of the initial token id:s in this look-ahead
     * set. The list returned will not contain any duplicates.
     *
     * @return a list of the inital token id:s in this look-ahead set
     */
    public int[] getInitialTokens() {
        ArrayList  list = new ArrayList();
        int[]      result;
        Integer    token;
        int        i;

        for (i = 0; i < elements.size(); i++) {
            token = ((Sequence) elements.get(i)).getToken(0);
            if (token != null && !list.contains(token)) {
                list.add(token);
            }
        }
        result = new int[list.size()];
        for (i = 0; i < list.size(); i++) {
            result[i] = ((Integer) list.get(i)).intValue();
        }
        return result;
    }

    /**
     * Checks if this look-ahead set contains a repetitive token
     * sequence.
     *
     * @return true if at least one token sequence is repetitive, or
     *         false otherwise
     */
    public boolean isRepetitive() {
        Sequence  seq;

        for (int i = 0; i < elements.size(); i++) {
            seq = (Sequence) elements.get(i);
            if (seq.isRepetitive()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the next token(s) in the parser match any token
     * sequence in this set.
     *
     * @param parser         the parser to check
     *
     * @return true if the next tokens are in the set, or
     *         false otherwise
     */
    public boolean isNext(Parser parser) {
        Sequence  seq;

        for (int i = 0; i < elements.size(); i++) {
            seq = (Sequence) elements.get(i);
            if (seq.isNext(parser)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the next token(s) in the parser match any token
     * sequence in this set.
     *
     * @param parser         the parser to check
     * @param length         the maximum number of tokens to check
     *
     * @return true if the next tokens are in the set, or
     *         false otherwise
     */
    public boolean isNext(Parser parser, int length) {
        Sequence  seq;

        for (int i = 0; i < elements.size(); i++) {
            seq = (Sequence) elements.get(i);
            if (seq.isNext(parser, length)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if another look-ahead set has an overlapping token
     * sequence. An overlapping token sequence is a token sequence
     * that is identical to another sequence, but for the length. I.e.
     * one of the two sequences may be longer than the other.
     *
     * @param set            the look-ahead set to check
     *
     * @return true if there is some token sequence that overlaps, or
     *         false otherwise
     *
     * @since 1.5
     */
    public boolean hasOverlap(LookAheadSet set) {
        for (int i = 0; i < elements.size(); i++) {
            if (set.hasOverlap((Sequence) elements.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a token sequence is overlapping. An overlapping token
     * sequence is a token sequence that is identical to another
     * sequence, but for the length. I.e. one of the two sequences may
     * be longer than the other.
     *
     * @param seq            the token sequence to check
     *
     * @return true if there is some token sequence that overlaps, or
     *         false otherwise
     *
     * @since 1.5
     */
    private boolean hasOverlap(Sequence seq) {
        Sequence  elem;

        for (int i = 0; i < elements.size(); i++) {
            elem = (Sequence) elements.get(i);
            if (seq.startsWith(elem) || elem.startsWith(seq)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if some token sequence is present in both this set
     * and a specified one.
     *
     * @param set            the look-ahead set to compare with
     *
     * @return true if the look-ahead sets intersect, or
     *         false otherwise
     *
     * @since 1.5
     */
    public boolean hasIntersection(LookAheadSet set) {
        for (int i = 0; i < elements.size(); i++) {
            if (set.contains((Sequence) elements.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the specified token sequence is present in the
     * set.
     *
     * @param elem           the token sequence to check
     *
     * @return true if the sequence is present in this set, or
     *         false otherwise
     */
    private boolean contains(Sequence elem) {
        return findSequence(elem) != null;
    }

    /**
     * Finds an identical token sequence if present in the set.
     *
     * @param elem           the token sequence to search for
     *
     * @return an identical the token sequence if found, or
     *         null if not found
     */
    private Sequence findSequence(Sequence elem) {
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).equals(elem)) {
                return (Sequence) elements.get(i);
            }
        }
        return null;
    }

    /**
     * Adds a token sequence to this set. The sequence will only be
     * added if it is not already in the set. Also, if the sequence is
     * longer than the allowed maximum, a truncated sequence will be
     * added instead.
     *
     * @param seq            the token sequence to add
     */
    private void add(Sequence seq) {
        if (seq.length() > maxLength) {
            seq = new Sequence(maxLength, seq);
        }
        if (!contains(seq)) {
            elements.add(seq);
        }
    }

    /**
     * Adds a new token sequence with a single token to this set. The
     * sequence will only be added if it is not already in the set.
     *
     * @param token          the token to add
     */
    public void add(int token) {
        add(new Sequence(false, token));
    }

    /**
     * Adds all the token sequences from a specified set. Only
     * sequences not already in this set will be added.
     *
     * @param set            the set to add from
     */
    public void addAll(LookAheadSet set) {
        for (int i = 0; i < set.elements.size(); i++) {
            add((Sequence) set.elements.get(i));
        }
    }

    /**
     * Adds an empty token sequence to this set. The sequence will
     * only be added if it is not already in the set.
     */
    public void addEmpty() {
        add(new Sequence());
    }

    /**
     * Removes a token sequence from this set.
     *
     * @param seq            the token sequence to remove
     */
    private void remove(Sequence seq) {
        elements.remove(seq);
    }

    /**
     * Removes all the token sequences from a specified set. Only
     * sequences already in this set will be removed.
     *
     * @param set            the set to remove from
     */
    public void removeAll(LookAheadSet set) {
        for (int i = 0; i < set.elements.size(); i++) {
            remove((Sequence) set.elements.get(i));
        }
    }

    /**
     * Creates a new look-ahead set that is the result of reading the
     * specified token. The new look-ahead set will contain the
     * rest of all the token sequences that started with the specified
     * token.
     *
     * @param token          the token to read
     *
     * @return a new look-ahead set containing the remaining tokens
     */
    public LookAheadSet createNextSet(int token) {
        LookAheadSet  result = new LookAheadSet(maxLength - 1);
        Sequence      seq;
        Integer       value;

        for (int i = 0; i < elements.size(); i++) {
            seq = (Sequence) elements.get(i);
            value = seq.getToken(0);
            if (value != null && value.intValue() == token) {
                result.add(seq.subsequence(1));
            }
        }
        return result;
    }

    /**
     * Creates a new look-ahead set that is the intersection of
     * this set with another set. The token sequences in the net set
     * will only have the repeat flag set if it was set in both the
     * identical token sequences.
     *
     * @param set            the set to intersect with
     *
     * @return a new look-ahead set containing the intersection
     */
    public LookAheadSet createIntersection(LookAheadSet set) {
        LookAheadSet  result = new LookAheadSet(maxLength);
        Sequence      seq1;
        Sequence      seq2;

        for (int i = 0; i < elements.size(); i++) {
            seq1 = (Sequence) elements.get(i);
            seq2 = set.findSequence(seq1);
            if (seq2 != null && seq1.isRepetitive()) {
                result.add(seq2);
            } else if (seq2 != null) {
                result.add(seq1);
            }
        }
        return result;
    }

    /**
     * Creates a new look-ahead set that is the combination of
     * this set with another set. The combination is created by
     * creating new token sequences that consist of appending all
     * elements from the specified set onto all elements in this set.
     * This is sometimes referred to as the cartesian product.
     *
     * @param set            the set to combine with
     *
     * @return a new look-ahead set containing the combination
     */
    public LookAheadSet createCombination(LookAheadSet set) {
        LookAheadSet  result = new LookAheadSet(maxLength);
        Sequence      first;
        Sequence      second;

        // Handle special cases
        if (this.isEmpty()) {
            return set;
        } else if (set.isEmpty()) {
            return this;
        }

        // Create combinations
        for (int i = 0; i < elements.size(); i++) {
            first = (Sequence) elements.get(i);
            if (first.length() >= maxLength) {
                result.add(first);
            } else if (first.length() <= 0) {
                result.addAll(set);
            } else {
                for (int j = 0; j < set.elements.size(); j++) {
                    second = (Sequence) set.elements.get(j);
                    result.add(first.concat(maxLength, second));
                }
            }
        }
        return result;
    }

    /**
     * Creates a new look-ahead set with overlaps from another. All
     * token sequences in this set that overlaps with the other set
     * will be added to the new look-ahead set.
     *
     * @param set            the look-ahead set to check with
     *
     * @return a new look-ahead set containing the overlaps
     */
    public LookAheadSet createOverlaps(LookAheadSet set) {
        LookAheadSet  result = new LookAheadSet(maxLength);
        Sequence      seq;

        for (int i = 0; i < elements.size(); i++) {
            seq = (Sequence) elements.get(i);
            if (set.hasOverlap(seq)) {
                result.add(seq);
            }
        }
        return result;
    }

    /**
     * Creates a new look-ahead set filter. The filter will contain
     * all sequences from this set, possibly left trimmed by each one
     * of the sequences in the specified set.
     *
     * @param set            the look-ahead set to trim with
     *
     * @return a new look-ahead set filter
     */
    public LookAheadSet createFilter(LookAheadSet set) {
        LookAheadSet  result = new LookAheadSet(maxLength);
        Sequence      first;
        Sequence      second;

        // Handle special cases
        if (this.isEmpty() || set.isEmpty()) {
            return this;
        }

        // Create combinations
        for (int i = 0; i < elements.size(); i++) {
            first = (Sequence) elements.get(i);
            for (int j = 0; j < set.elements.size(); j++) {
                second = (Sequence) set.elements.get(j);
                if (first.startsWith(second)) {
                    result.add(first.subsequence(second.length()));
                }
            }
        }
        return result;
    }

    /**
     * Creates a new identical look-ahead set, except for the repeat
     * flag being set in each token sequence.
     *
     * @return a new repetitive look-ahead set
     */
    public LookAheadSet createRepetitive() {
        LookAheadSet  result = new LookAheadSet(maxLength);
        Sequence      seq;

        for (int i = 0; i < elements.size(); i++) {
            seq = (Sequence) elements.get(i);
            if (seq.isRepetitive()) {
                result.add(seq);
            } else {
                result.add(new Sequence(true, seq));
            }
        }
        return result;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        return toString(null);
    }

    /**
     * Returns a string representation of this object.
     *
     * @param tokenizer      the tokenizer containing the tokens
     *
     * @return a string representation of this object
     */
    public String toString(Tokenizer tokenizer) {
        StringBuffer  buffer = new StringBuffer();
        Sequence      seq;

        buffer.append("{");
        for (int i = 0; i < elements.size(); i++) {
            seq = (Sequence) elements.get(i);
            buffer.append("\n  ");
            buffer.append(seq.toString(tokenizer));
        }
        buffer.append("\n}");
        return buffer.toString();
    }


    /**
     * A token sequence. This class contains a list of token ids. It
     * is immutable after creation, meaning that no changes will be
     * made to an instance after creation.
     *
     * @author   Per Cederberg, <per at percederberg dot net>
     * @version  1.0
     */
    private class Sequence {

        /**
         * The repeat flag. If this flag is set, the token sequence
         * or some part of it may be repeated infinitely.
         */
        private boolean repeat = false;

        /**
         * The list of token ids in this sequence.
         */
        private ArrayList tokens = null;

        /**
         * Creates a new empty token sequence. The repeat flag will be
         * set to false.
         */
        public Sequence() {
            this.repeat = false;
            this.tokens = new ArrayList(0);
        }

        /**
         * Creates a new token sequence with a single token.
         *
         * @param repeat         the repeat flag value
         * @param token          the token to add
         */
        public Sequence(boolean repeat, int token) {
            this.repeat = false;
            this.tokens = new ArrayList(1);
            this.tokens.add(new Integer(token));
        }

        /**
         * Creates a new token sequence that is a duplicate of another
         * sequence. Only a limited number of tokens will be copied
         * however. The repeat flag from the original will be kept
         * intact.
         *
         * @param length         the maximum number of tokens to copy
         * @param seq            the sequence to copy
         */
        public Sequence(int length, Sequence seq) {
            this.repeat = seq.repeat;
            this.tokens = new ArrayList(length);
            if (seq.length() < length) {
                length = seq.length();
            }
            for (int i = 0; i < length; i++) {
                tokens.add(seq.tokens.get(i));
            }
        }

        /**
         * Creates a new token sequence that is a duplicate of another
         * sequence. The new value of the repeat flag will be used
         * however.
         *
         * @param repeat         the new repeat flag value
         * @param seq            the sequence to copy
         */
        public Sequence(boolean repeat, Sequence seq) {
            this.repeat = repeat;
            this.tokens = seq.tokens;
        }

        /**
         * Returns the length of the token sequence.
         *
         * @return the number of tokens in the sequence
         */
        public int length() {
            return tokens.size();
        }

        /**
         * Returns a token at a specified position in the sequence.
         *
         * @param pos            the sequence position
         *
         * @return the token id found, or null
         */
        public Integer getToken(int pos) {
            if (pos >= 0 && pos < tokens.size()) {
                return (Integer) tokens.get(pos);
            } else {
                return null;
            }
        }

        /**
         * Checks if this sequence is equal to another object. Only
         * token sequences with the same tokens in the same order will
         * be considered equal. The repeat flag will be disregarded.
         *
         * @param obj            the object to compare with
         *
         * @return true if the objects are equal, or
         *         false otherwise
         */
        public boolean equals(Object obj) {
            if (obj instanceof Sequence) {
                return tokens.equals(((Sequence) obj).tokens);
            } else {
                return false;
            }
        }

        /**
         * Returns a hash code for this object.
         *
         * @return a hash code for this object
         */
        public int hashCode() {
            return this.tokens.hashCode();
        }

        /**
         * Checks if this token sequence starts with the tokens from
         * another sequence. If the other sequence is longer than this
         * sequence, this method will always return false.
         *
         * @param seq            the token sequence to check
         *
         * @return true if this sequence starts with the other, or
         *         false otherwise
         */
        public boolean startsWith(Sequence seq) {
            if (length() < seq.length()) {
                return false;
            }
            for (int i = 0; i < seq.tokens.size(); i++) {
                if (!tokens.get(i).equals(seq.tokens.get(i))) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Checks if this token sequence is repetitive. A repetitive
         * token sequence is one with the repeat flag set.
         *
         * @return true if this token sequence is repetitive, or
         *         false otherwise
         */
        public boolean isRepetitive() {
            return repeat;
        }

        /**
         * Checks if the next token(s) in the parser matches this
         * token sequence.
         *
         * @param parser         the parser to check
         *
         * @return true if the next tokens are in the sequence, or
         *         false otherwise
         */
        public boolean isNext(Parser parser) {
            Token    token;
            Integer  id;

            for (int i = 0; i < tokens.size(); i++) {
                id = (Integer) tokens.get(i);
                token = parser.peekToken(i);
                if (token == null || token.getId() != id.intValue()) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Checks if the next token(s) in the parser matches this
         * token sequence.
         *
         * @param parser         the parser to check
         * @param length         the maximum number of tokens to check
         *
         * @return true if the next tokens are in the sequence, or
         *         false otherwise
         */
        public boolean isNext(Parser parser, int length) {
            Token    token;
            Integer  id;

            if (length > tokens.size()) {
                length = tokens.size();
            }
            for (int i = 0; i < length; i++) {
                id = (Integer) tokens.get(i);
                token = parser.peekToken(i);
                if (token == null || token.getId() != id.intValue()) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Returns a string representation of this object.
         *
         * @return a string representation of this object
         */
        public String toString() {
            return toString(null);
        }

        /**
         * Returns a string representation of this object.
         *
         * @param tokenizer      the tokenizer containing the tokens
         *
         * @return a string representation of this object
         */
        public String toString(Tokenizer tokenizer) {
            StringBuffer  buffer = new StringBuffer();
            String        str;
            Integer       id;

            if (tokenizer == null) {
                buffer.append(tokens.toString());
            } else {
                buffer.append("[");
                for (int i = 0; i < tokens.size(); i++) {
                    id = (Integer) tokens.get(i);
                    str = tokenizer.getPatternDescription(id.intValue());
                    if (i > 0) {
                        buffer.append(" ");
                    }
                    buffer.append(str);
                }
                buffer.append("]");
            }
            if (repeat) {
                buffer.append(" *");
            }
            return buffer.toString();
        }

        /**
         * Creates a new token sequence that is the concatenation of
         * this sequence and another. A maximum length for the new
         * sequence is also specified.
         *
         * @param length         the maximum length of the result
         * @param seq            the other sequence
         *
         * @return the concatenated token sequence
         */
        public Sequence concat(int length, Sequence seq) {
            Sequence  res = new Sequence(length, this);

            if (seq.repeat) {
                res.repeat = true;
            }
            length -= this.length();
            if (length > seq.length()) {
                res.tokens.addAll(seq.tokens);
            } else {
                for (int i = 0; i < length; i++) {
                    res.tokens.add(seq.tokens.get(i));
                }
            }
            return res;
        }

        /**
         * Creates a new token sequence that is a subsequence of this
         * one.
         *
         * @param start          the subsequence start position
         *
         * @return the new token subsequence
         */
        public Sequence subsequence(int start) {
            Sequence  res = new Sequence(length(), this);

            while (start > 0 && res.tokens.size() > 0) {
                res.tokens.remove(0);
                start--;
            }
            return res;
        }
    }
}
