/*
 * RepeatElement.java
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

package net.percederberg.grammatica.parser.re;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.BitSet;

import net.percederberg.grammatica.parser.ReaderBuffer;

/**
 * A regular expression element repeater. The element repeats the
 * matches from a specified element, attempting to reach the maximum
 * repetition count.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
class RepeatElement extends Element {

    /**
     * The greedy repeat type constant.
     */
    public static final int GREEDY = 1;

    /**
     * The reluctant repeat type constant.
     */
    public static final int RELUCTANT = 2;

    /**
     * The possessive repeat type constant.
     */
    public static final int POSSESSIVE = 3;

    /**
     * The element to repeat.
     */
    private Element elem;

    /**
     * The minimum number of repetitions.
     */
    private int min;

    /**
     * The maximum number of repetitions.
     */
    private int max;

    /**
     * The repeat type.
     */
    private int type;

    /**
     * The start position of the last set of matches.
     */
    private int matchStart;

    /**
     * A set with all matches starting as matchStart. A match with a
     * specific length is reported by a non-zero bit in the bit set.
     */
    private BitSet matches;

    /**
     * Creats a new element repeater.
     *
     * @param elem           the element to repeat
     * @param min            the minimum count
     * @param max            the maximum count
     * @param type           the repeat type constant
     *
     * @see #GREEDY
     * @see #RELUCTANT
     * @see #POSSESSIVE
     */
    public RepeatElement(Element elem, int min, int max, int type) {
        this.elem = elem;
        this.min = min;
        if (max <= 0) {
            this.max = Integer.MAX_VALUE;
        } else {
            this.max = max;
        }
        this.type = type;
        this.matchStart = -1;
        this.matches = null;
    }

    /**
     * Creates a copy of this element. The copy will be an instance
     * of the same class matching the same strings. Copies of elements
     * are necessary to allow elements to cache intermediate results
     * while matching strings without interfering with other threads.
     *
     * @return a copy of this element
     */
    public Object clone() {
        return new RepeatElement((Element) elem.clone(), min, max, type);
    }

    /**
     * Returns the length of a matching string starting at the
     * specified position. The number of matches to skip can also be
     * specified.
     *
     * @param m              the matcher being used
     * @param buffer         the input character buffer to match
     * @param start          the starting position
     * @param skip           the number of matches to skip
     *
     * @return the length of the matching string, or
     *         -1 if no match was found
     *
     * @throws IOException if an I/O error occurred
     */
    public int match(Matcher m, ReaderBuffer buffer, int start, int skip)
        throws IOException {

        if (skip == 0) {
            matchStart = -1;
            matches = null;
        }
        switch (type) {
        case GREEDY:
            return matchGreedy(m, buffer, start, skip);
        case RELUCTANT:
            return matchReluctant(m, buffer, start, skip);
        case POSSESSIVE:
            if (skip == 0) {
                return matchPossessive(m, buffer, start, 0);
            }
            break;
        }
        return -1;
    }

    /**
     * Returns the length of the longest possible matching string
     * starting at the specified position. The number of matches to
     * skip can also be specified.
     *
     * @param m              the matcher being used
     * @param buffer         the input character buffer to match
     * @param start          the starting position
     * @param skip           the number of matches to skip
     *
     * @return the length of the longest matching string, or
     *         -1 if no match was found
     *
     * @throws IOException if an I/O error occurred
     */
    private int matchGreedy(Matcher m,
                            ReaderBuffer buffer,
                            int start,
                            int skip)
        throws IOException {

        // Check for simple case
        if (skip == 0) {
            return matchPossessive(m, buffer, start, 0);
        }

        // Find all matches
        if (matchStart != start) {
            matchStart = start;
            matches = new BitSet();
            findMatches(m, buffer, start, 0, 0, 0);
        }

        // Find first non-skipped match
        for (int i = matches.size(); i >= 0; i--) {
            if (matches.get(i)) {
                if (skip == 0) {
                    return i;
                }
                skip--;
            }
        }
        return -1;
    }

    /**
     * Returns the length of the shortest possible matching string
     * starting at the specified position. The number of matches to
     * skip can also be specified.
     *
     * @param m              the matcher being used
     * @param buffer         the input character buffer to match
     * @param start          the starting position
     * @param skip           the number of matches to skip
     *
     * @return the length of the shortest matching string, or
     *         -1 if no match was found
     *
     * @throws IOException if an I/O error occurred
     */
    private int matchReluctant(Matcher m,
                               ReaderBuffer buffer,
                               int start,
                               int skip)
        throws IOException {

        // Find all matches
        if (matchStart != start) {
            matchStart = start;
            matches = new BitSet();
            findMatches(m, buffer, start, 0, 0, 0);
        }

        // Find first non-skipped match
        for (int i = 0; i <= matches.size(); i++) {
            if (matches.get(i)) {
                if (skip == 0) {
                    return i;
                }
                skip--;
            }
        }
        return -1;
    }

    /**
     * Returns the length of the maximum number of elements matching
     * the string starting at the specified position. This method
     * allows no backtracking, i.e. no skips..
     *
     * @param m              the matcher being used
     * @param buffer         the input character buffer to match
     * @param start          the starting position
     * @param count          the start count, normally zero (0)
     *
     * @return the length of the longest matching string, or
     *         -1 if no match was found
     *
     * @throws IOException if an I/O error occurred
     */
    private int matchPossessive(Matcher m,
                                ReaderBuffer buffer,
                                int start,
                                int count)
        throws IOException {

        int  length = 0;
        int  subLength = 1;

        // Match as many elements as possible
        while (subLength > 0 && count < max) {
            subLength = elem.match(m, buffer, start + length, 0);
            if (subLength >= 0) {
                count++;
                length += subLength;
            }
        }

        // Return result
        if (min <= count && count <= max) {
            return length;
        } else {
            return -1;
        }
    }

    /**
     * Finds all matches and adds the lengths to the matches set.
     *
     * @param m              the matcher being used
     * @param buffer         the input character buffer to match
     * @param start          the starting position
     * @param length         the match length at the start position
     * @param count          the number of sub-elements matched
     * @param attempt        the number of match attempts here
     *
     * @throws IOException if an I/O error occurred
     */
    private void findMatches(Matcher m,
                             ReaderBuffer buffer,
                             int start,
                             int length,
                             int count,
                             int attempt)
        throws IOException {

        int  subLength;

        // Check match ending here
        if (count > max) {
            return;
        }
        if (min <= count && attempt == 0) {
            matches.set(length);
        }

        // Check element match
        subLength = elem.match(m, buffer, start, attempt);
        if (subLength < 0) {
            return;
        } else if (subLength == 0) {
            if (min == count + 1) {
                matches.set(length);
            }
            return;
        }

        // Find alternative and subsequent matches
        findMatches(m, buffer, start, length, count, attempt + 1);
        findMatches(m,
                    buffer,
                    start + subLength,
                    length + subLength,
                    count + 1,
                    0);
    }

    /**
     * Prints this element to the specified output stream.
     *
     * @param output         the output stream to use
     * @param indent         the current indentation
     */
    public void printTo(PrintWriter output, String indent) {
        output.print(indent + "Repeat (" + min + "," + max + ")");
        if (type == RELUCTANT) {
            output.print("?");
        } else if (type == POSSESSIVE) {
            output.print("+");
        }
        output.println();
        elem.printTo(output, indent + "  ");
    }
}
