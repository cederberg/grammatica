/*
 * Matcher.java
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
import java.io.StringReader;

import net.percederberg.grammatica.parser.ReaderBuffer;

/**
 * A regular expression string matcher. This class handles the
 * matching of a specific string with a specific regular expression.
 * It contains state information about the matching process, as for
 * example the position of the latest match, and a number of flags
 * that were set. This class is not thread-safe.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
public class Matcher {

    /**
     * The base regular expression element.
     */
    private Element element;

    /**
     * The input character buffer to work with.
     */
    private ReaderBuffer buffer;

    /**
     * The character case ignore flag.
     */
    private boolean ignoreCase;

    /**
     * The start of the latest match found.
     */
    private int start;

    /**
     * The length of the latest match found.
     */
    private int length;

    /**
     * The end of string reached flag. This flag is set if the end
     * of the string was encountered during the latest match.
     */
    private boolean endOfString;

    /**
     * Creates a new matcher with the specified element.
     *
     * @param e              the base regular expression element
     * @param buffer         the input character buffer to work with
     * @param ignoreCase     the character case ignore flag
     */
    Matcher(Element e, ReaderBuffer buffer, boolean ignoreCase) {
        this.element = e;
        this.buffer = buffer;
        this.ignoreCase = ignoreCase;
        this.start = 0;
        reset();
    }

    /**
     * Checks if this matcher compares in case-insensitive mode.
     *
     * @return true if the matching is case-insensitive, or
     *         false otherwise
     *
     * @since 1.5
     */
    public boolean isCaseInsensitive() {
        return ignoreCase;
    }

    /**
     * Resets the information about the last match. This will clear
     * all flags and set the match length to a negative value. This
     * method is automatically called before starting a new match.
     */
    public void reset() {
        length = -1;
        endOfString = false;
    }

    /**
     * Resets the matcher for use with a new input string. This will
     * clear all flags and set the match length to a negative value.
     *
     * @param str            the new string to work with
     *
     * @since 1.5
     */
    public void reset(String str) {
        reset(new ReaderBuffer(new StringReader(str)));
    }

    /**
     * Resets the matcher for use with a new look-ahead character
     * input buffer. This will clear all flags and set the match
     * length to a negative value.
     *
     * @param buffer          the character input stream
     *
     * @since 1.5
     */
    public void reset(ReaderBuffer buffer) {
        this.buffer = buffer;
        reset();
    }

    /**
     * Returns the start position of the latest match. If no match has
     * been encountered, this method returns zero (0).
     *
     * @return the start position of the latest match
     */
    public int start() {
        return start;
    }

    /**
     * Returns the end position of the latest match. This is one
     * character after the match end, i.e. the first character after
     * the match. If no match has been encountered, this method
     * returns the same value as start().
     *
     * @return the end position of the latest match
     */
    public int end() {
        if (length > 0) {
            return start + length;
        } else {
            return start;
        }
    }

    /**
     * Returns the length of the latest match.
     *
     * @return the length of the latest match, or
     *         -1 if no match was found
     */
    public int length() {
        return length;
    }

    /**
     * Checks if the end of the string was encountered during the last
     * match attempt. This flag signals that more input may be needed
     * in order to get a match (or a longer match).
     *
     * @return true if the end of string was encountered, or
     *         false otherwise
     */
    public boolean hasReadEndOfString() {
        return endOfString;
    }

    /**
     * Sets the end of string encountered flag. This method is called
     * by the various elements analyzing the string.
     */
    void setReadEndOfString() {
        endOfString = true;
    }

    /**
     * Attempts to find a match starting at the beginning of the
     * string.
     *
     * @return true if a match was found, or
     *         false otherwise
     *
     * @throws IOException if an I/O error occurred while reading an
     *             input stream
     */
    public boolean matchFromBeginning() throws IOException {
        return matchFrom(0);
    }

    /**
     * Attempts to find a match starting at the specified position in
     * the string.
     *
     * @param pos            the starting position of the match
     *
     * @return true if a match was found, or
     *         false otherwise
     *
     * @throws IOException if an I/O error occurred while reading an
     *             input stream
     */
    public boolean matchFrom(int pos) throws IOException {
        reset();
        start = pos;
        length = element.match(this, buffer, start, 0);
        return length >= 0;
    }

    /**
     * Returns the latest matched string. If no string has been
     * matched, an empty string will be returned.
     *
     * @return the latest matched string
     */
    public String toString() {
        if (length <= 0) {
            return "";
        } else {
            int pos = buffer.position();
            return buffer.subSequence(pos, pos + length).toString();
        }
    }
}
