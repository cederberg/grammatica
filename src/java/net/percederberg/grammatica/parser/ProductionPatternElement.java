/*
 * ProductionPatternElement.java
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

/**
 * A production pattern element. This class represents a reference to
 * either a token or a production. Each element also contains minimum
 * and maximum occurence counters, controlling the number of
 * repetitions allowed. A production pattern element is always
 * contained within a production pattern rule.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
public class ProductionPatternElement {

    /**
     * The token flag. This flag is true for token elements, and
     * false for production elements.
     */
    private boolean token;

    /**
     * The node identity.
     */
    private int id;

    /**
     * The minimum occurance count.
     */
    private int min;

    /**
     * The maximum occurance count.
     */
    private int max;

    /**
     * The look-ahead set associated with this element.
     */
    private LookAheadSet lookAhead;

    /**
     * Creates a new element. If the maximum value if zero (0) or
     * negative, it will be set to Integer.MAX_VALUE.
     *
     * @param isToken        the token flag
     * @param id             the node identity
     * @param min            the minimum number of occurancies
     * @param max            the maximum number of occurancies, or
     *                       negative for infinite
     */
    public ProductionPatternElement(boolean isToken,
                                    int id,
                                    int min,
                                    int max) {

        this.token = isToken;
        this.id = id;
        if (min < 0) {
            min = 0;
        }
        this.min = min;
        if (max <= 0) {
            max = Integer.MAX_VALUE;
        } else if (max < min) {
            max = min;
        }
        this.max = max;
        this.lookAhead = null;
    }

    /**
     * Returns true if this element represents a token.
     *
     * @return true if the element is a token, or
     *         false otherwise
     */
    public boolean isToken() {
        return token;
    }

    /**
     * Returns true if this element represents a production.
     *
     * @return true if the element is a production, or
     *         false otherwise
     */
    public boolean isProduction() {
        return !token;
    }

    /**
     * Checks if a specific token matches this element. This method
     * will only return true if this element is a token element, and
     * the token has the same id and this element.
     *
     * @param token          the token to check
     *
     * @return true if the token matches this element, or
     *         false otherwise
     */
    public boolean isMatch(Token token) {
        return isToken() && token != null && token.getId() == id;
    }

    /**
     * Returns the node identity.
     *
     * @return the node identity
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the minimum occurence count.
     *
     * @return the minimum occurence count
     */
    public int getMinCount() {
        return min;
    }

    /**
     * Returns the maximum occurence count.
     *
     * @return the maximum occurence count
     */
    public int getMaxCount() {
        return max;
    }

    /**
     * Checks if this object is equal to another. This method only
     * returns true for another identical production pattern element.
     *
     * @param obj            the object to compare with
     *
     * @return true if the object is identical to this one, or
     *         false otherwise
     */
    public boolean equals(Object obj) {
        ProductionPatternElement  elem;

        if (obj instanceof ProductionPatternElement) {
            elem = (ProductionPatternElement) obj;
            return this.token == elem.token
                && this.id == elem.id
                && this.min == elem.min
                && this.max == elem.max;
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
        return this.id * 37;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        StringBuffer  buffer = new StringBuffer();

        buffer.append(id);
        if (token) {
            buffer.append("(Token)");
        } else {
            buffer.append("(Production)");
        }
        if (min != 1 || max != 1) {
            buffer.append("{");
            buffer.append(min);
            buffer.append(",");
            buffer.append(max);
            buffer.append("}");
        }
        return buffer.toString();
    }

    /**
     * Returns the look-ahead set associated with this alternative.
     *
     * @return the look-ahead set associated with this alternative
     */
    LookAheadSet getLookAhead() {
        return lookAhead;
    }

    /**
     * Sets the look-ahead set for this alternative.
     *
     * @param lookAhead      the new look-ahead set
     */
    void setLookAhead(LookAheadSet lookAhead) {
        this.lookAhead = lookAhead;
    }
}
