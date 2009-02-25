/*
 * ProductionPatternAlternative.java
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
 * A production pattern alternative. This class represents a list of
 * production pattern elements. In order to provide productions that
 * cannot be represented with the element occurance counters, multiple
 * alternatives must be created and added to the same production
 * pattern. A production pattern alternative is always contained
 * within a production pattern.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
public class ProductionPatternAlternative {

    /**
     * The production pattern.
     */
    private ProductionPattern pattern;

    /**
     * The element list.
     */
    private ArrayList elements = new ArrayList();

    /**
     * The look-ahead set associated with this alternative.
     */
    private LookAheadSet lookAhead = null;

    /**
     * Checks if this alternative is recursive on the left-hand side.
     * This method checks all the possible left side elements and
     * returns true if the pattern itself is among them.
     *
     * @return true if the alternative is left side recursive, or
     *         false otherwise
     */
    public boolean isLeftRecursive() {
        ProductionPatternElement  elem;

        for (int i = 0; i < elements.size(); i++) {
            elem = (ProductionPatternElement) elements.get(i);
            if (elem.getId() == pattern.getId()) {
                return true;
            } else if (elem.getMinCount() > 0) {
                break;
            }
        }
        return false;
    }

    /**
     * Checks if this alternative is recursive on the right-hand side.
     * This method checks all the possible right side elements and
     * returns true if the pattern itself is among them.
     *
     * @return true if the alternative is right side recursive, or
     *         false otherwise
     */
    public boolean isRightRecursive() {
        ProductionPatternElement  elem;

        for (int i = elements.size() - 1; i >= 0; i--) {
            elem = (ProductionPatternElement) elements.get(i);
            if (elem.getId() == pattern.getId()) {
                return true;
            } else if (elem.getMinCount() > 0) {
                break;
            }
        }
        return false;
    }

    /**
     * Checks if this alternative would match an empty stream of
     * tokens. This check is equivalent of getMinElementCount()
     * returning zero (0).
     *
     * @return true if the rule can match an empty token stream, or
     *         false otherwise
     */
    public boolean isMatchingEmpty() {
        return getMinElementCount() == 0;
    }

    /**
     * Returns the production pattern containing this alternative.
     *
     * @return the production pattern for this alternative
     */
    public ProductionPattern getPattern() {
        return pattern;
    }

    /**
     * Changes the production pattern containing this alternative.
     * This method should only be called by the production pattern
     * class.
     *
     * @param pattern        the new production pattern
     */
    void setPattern(ProductionPattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Returns the number of elements in this alternative.
     *
     * @return the number of elements in this alternative
     */
    public int getElementCount() {
        return elements.size();
    }

    /**
     * Returns the minimum number of elements needed to satisfy this
     * alternative. The value returned is the sum of all the elements
     * minimum count.
     *
     * @return the minimum number of elements
     */
    public int getMinElementCount() {
        ProductionPatternElement  elem;
        int                       min = 0;

        for (int i = 0; i < elements.size(); i++) {
            elem = (ProductionPatternElement) elements.get(i);
            min += elem.getMinCount();
        }
        return min;
    }

    /**
     * Returns the maximum number of elements needed to satisfy this
     * alternative. The value returned is the sum of all the elements
     * maximum count.
     *
     * @return the maximum number of elements
     */
    public int getMaxElementCount() {
        ProductionPatternElement  elem;
        int                       max = 0;

        for (int i = 0; i < elements.size(); i++) {
            elem = (ProductionPatternElement) elements.get(i);
            if (elem.getMaxCount() >= Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            } else {
                max += elem.getMaxCount();
            }
        }
        return max;
    }

    /**
     * Returns an element in this alternative.
     *
     * @param pos            the element position, 0 <= pos < count
     *
     * @return the element found
     */
    public ProductionPatternElement getElement(int pos) {
        return (ProductionPatternElement) elements.get(pos);
    }

    /**
     * Adds a token to this alternative. The token is appended to the
     * end of the element list. The multiplicity values specified
     * define if the token is optional or required, and if it can be
     * repeated.
     *
     * @param id             the token (pattern) id
     * @param min            the minimum number of occurancies
     * @param max            the maximum number of occurancies, or
     *                       -1 for infinite
     */
    public void addToken(int id, int min, int max) {
        addElement(new ProductionPatternElement(true, id, min, max));
    }

    /**
     * Adds a production to this alternative. The production is
     * appended to the end of the element list. The multiplicity
     * values specified define if the production is optional or
     * required, and if it can be repeated.
     *
     * @param id             the production (pattern) id
     * @param min            the minimum number of occurancies
     * @param max            the maximum number of occurancies, or
     *                       -1 for infinite
     */
    public void addProduction(int id, int min, int max) {
        addElement(new ProductionPatternElement(false, id, min, max));
    }

    /**
     * Adds a production pattern element to this alternative. The
     * element is appended to the end of the element list.
     *
     * @param elem           the production pattern element
     */
    public void addElement(ProductionPatternElement elem) {
        elements.add(elem);
    }

    /**
     * Adds a production pattern element to this alternative. The
     * multiplicity values in the element will be overridden with the
     * specified values. The element is appended to the end of the
     * element list.
     *
     * @param elem           the production pattern element
     * @param min            the minimum number of occurancies
     * @param max            the maximum number of occurancies, or
     *                       -1 for infinite
     */
    public void addElement(ProductionPatternElement elem,
                           int min,
                           int max) {

        if (elem.isToken()) {
            addToken(elem.getId(), min, max);
        } else {
            addProduction(elem.getId(), min, max);
        }
    }

    /**
     * Checks if this object is equal to another. This method only
     * returns true for another production pattern alternative with
     * identical elements in the same order.
     *
     * @param obj            the object to compare with
     *
     * @return true if the object is identical to this one, or
     *         false otherwise
     */
    public boolean equals(Object obj) {
        ProductionPatternAlternative  alt;

        if (obj instanceof ProductionPatternAlternative) {
            alt = (ProductionPatternAlternative) obj;
            return elements.equals(alt.elements);
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
        return elements.hashCode();
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a token string representation
     */
    public String toString() {
        StringBuffer  buffer = new StringBuffer();

        for (int i = 0; i < elements.size(); i++) {
            if (i > 0) {
                buffer.append(" ");
            }
            buffer.append(elements.get(i));
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
