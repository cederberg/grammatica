/*
 * TokenMatch.java
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

/**
 * The token match status. This class contains logic to ensure that
 * only the longest match is considered. It also prefers lower token
 * pattern identifiers if two matches have the same length.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 * @since    1.5
 */
class TokenMatch {

    /**
     * The length of the longest match.
     */
    private int length = 0;

    /**
     * The pattern in the longest match.
     */
    private TokenPattern pattern = null;

    /**
     * Clears the current match information.
     */
    public void clear() {
        length = 0;
        pattern = null;
    }

    /**
     * Returns the length of the longest match found.
     *
     * @return the length of the longest match, or
     *         zero (0) if no match has been found
     */
    public int length() {
        return length;
    }

    /**
     * Returns the token pattern for the longest match found.
     *
     * @return the token pattern for the longest match, or
     *         null if no match has been found
     */
    public TokenPattern pattern() {
        return pattern;
    }

    /**
     * Updates this match with new values. The new values will only
     * be considered if the length is longer than any previous match
     * found.
     *
     * @param length         the matched length
     * @param pattern        the matched pattern
     */
    public void update(int length, TokenPattern pattern) {
        if (this.length < length) {
            this.length = length;
            this.pattern = pattern;
        } else if (this.length == length &&
                   this.pattern.getId() > pattern.getId()) {
            this.length = length;
            this.pattern = pattern;
        }
    }
}
