/*
 * Matcher.java
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
 * Copyright (c) 2003 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.parser.re;

/**
 * A regular expression string matcher. This class handles the 
 * matching of a specific string with a specific regular expression.
 * It contains state information about the matching process, as for
 * example the position of the latest match, and a number of flags
 * that were set. This class is not thread-safe.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
public class Matcher {

    /**
     * The base regular expression element.
     */
    private Element element;
    
    /**
     * The string to work with
     */
    private CharBuffer str;

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
     * @param str            the string to work with
     */
    protected Matcher(Element e, CharBuffer str) {
        this.element = e;
        this.str = str;
        this.start = 0;
        reset();
    }

    /**
     * Resets the information about the last match. This will clear 
     * all flags and set the match length to a negative value. This 
     * method is automatically called by all matching methods.
     */
    public void reset() {
        length = -1;
        endOfString = false;
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
     */
    public boolean matchFromBeginning() {
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
     */
    public boolean matchFrom(int pos) {
        reset();
        start = pos;
        length = element.match(this, str, start, 0);
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
            return str.substring(start, start + length);
        }
    }
}
