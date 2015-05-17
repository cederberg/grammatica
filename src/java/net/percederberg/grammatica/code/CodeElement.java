/*
 * CodeElement.java
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the BSD license.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * LICENSE.txt file for more details.
 *
 * Copyright (c) 2003-2015 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.code;

import java.io.PrintWriter;

/**
 * The abstract base class for all code elements. The code element
 * classes handles some source code construct and allows printing and
 * comparing it to other constructs.
 *
 * @author   Per Cederberg
 * @version  1.0
 */
public abstract class CodeElement implements Comparable {

    /**
     * Compares this object to another one. The comparison is based
     * on the code element category, reporting equality between
     * elements within the same category. Objects not being code
     * elements will cause zero (0) to be returned.
     *
     * @param obj            the object to compare to
     *
     * @return negative if this object preceeds the other one,
     *         zero (0) if the objects are equal, or
     *         positive if this object succeeds the other one
     */
    public int compareTo(Object obj) {
        if (obj instanceof CodeElement) {
            return category() - ((CodeElement) obj).category();
        } else {
            return 0;
        }
    }

    /**
     * Returns a numeric category number for the code element. A lower
     * category number implies that the code element should be placed
     * before code elements with a higher category number within a
     * declaration.
     *
     * @return the category number
     */
    public abstract int category();

    /**
     * Prints the code element to the specified output stream.
     *
     * @param out            the output stream
     * @param style          the code style to use
     * @param indent         the indentation level
     */
    public abstract void print(PrintWriter out,
                               CodeStyle style,
                               int indent);
}
