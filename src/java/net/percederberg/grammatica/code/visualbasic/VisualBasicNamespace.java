/*
 * VisualBasicNamespace.java
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the BSD license.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * LICENSE.txt file for more details.
 *
 * Copyright (c) 2004 Adrian Moore. All rights reserved.
 * Copyright (c) 2003-2015 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.code.visualbasic;

import java.io.PrintWriter;
import net.percederberg.grammatica.code.CodeElementContainer;
import net.percederberg.grammatica.code.CodeStyle;

/**
 * A class generating a Visual Basic namespace declaration.
 *
 * @author   Adrian Moore
 * @author   Per Cederberg
 * @version  1.5
 * @since    1.5
 */
public class VisualBasicNamespace extends CodeElementContainer {

    /**
     * The fully qualified namespace name.
     */
    private String name;

    /**
     * Creates a new namespace.
     *
     * @param name           the fully qualified namespace name
     */
    public VisualBasicNamespace(String name) {
        this.name = name;
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
        return 11;
    }

    /**
     * Adds a class declaration to the namespace.
     *
     * @param c              the class declaration to add
     */
    public void addClass(VisualBasicClass c) {
        addElement(c);
    }

    /**
     * Adds an enumeration to the namespace.
     *
     * @param e              the enumeration to add
     */
    public void addEnumeration(VisualBasicEnumeration e) {
        addElement(e);
    }

    /**
     * Prints the code element to the specified output stream.
     *
     * @param out            the output stream
     * @param style          the code style to use
     * @param indent         the indentation level
     */
    public void print(PrintWriter out, CodeStyle style, int indent) {
        String  indentStr = style.getIndent(indent);

        out.println(indentStr + "Namespace " + name );
        out.println();
        printContents(out, style, indent + 1);
        out.println(indentStr + "End Namespace");
    }
}
