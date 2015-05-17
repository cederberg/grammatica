/*
 * CSharpUsing.java
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

package net.percederberg.grammatica.code.csharp;

import java.io.PrintWriter;
import net.percederberg.grammatica.code.CodeElement;
import net.percederberg.grammatica.code.CodeStyle;

/**
 * A class generating a C# using declaration.
 *
 * @author   Per Cederberg
 * @version  1.0
 */
public class CSharpUsing extends CodeElement {

    /**
     * The namespace being used.
     */
    private String namespace;

    /**
     * Creates a new C# using declaration.
     *
     * @param namespace      the namespace being used
     */
    public CSharpUsing(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Compares this object to another one. The comparison is based
     * primarily on the code element category, and secondarily on the
     * namespace.
     *
     * @param obj            the object to compare to
     *
     * @return negative if this object preceeds the other one,
     *         zero (0) if the objects are equal, or
     *         positive if this object succeeds the other one
     */
    public int compareTo(Object obj) {
        int  value = super.compareTo(obj);

        if (value == 0) {
            return namespace.compareTo(obj.toString());
        } else {
            return value;
        }
    }

    /**
     * Returns true if this object is equal to another.
     *
     * @param obj       the object to compare to
     *
     * @return true if the objects are equal, or
     *         false otherwise
     */
    public boolean equals(Object obj) {
        return compareTo(obj) == 0;
    }

    /**
     * Returns the namespace to use.
     *
     * @return the namespace to use
     */
    public String toString() {
        return namespace;
    }

    /**
     * Returns a numeric category number for the code element. A lower
     * category number implies that the code element should be placed
     * before code elements with a higher category number within a
     * declaration.
     *
     * @return the category number
     */
    public int category() {
        return namespace.startsWith("System") ? 1 : 2;
    }

    /**
     * Prints the code element to the specified output stream.
     *
     * @param out            the output stream
     * @param style          the code style to use
     * @param indent         the indentation level
     */
    public void print(PrintWriter out, CodeStyle style, int indent) {
        out.println(style.getIndent(indent) + "using " + namespace + ";");
    }
}
