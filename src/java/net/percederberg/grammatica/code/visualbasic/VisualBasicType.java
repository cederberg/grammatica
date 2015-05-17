/*
 * VisualBasicType.java
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

import net.percederberg.grammatica.code.CodeElement;
import net.percederberg.grammatica.code.CodeElementContainer;
import net.percederberg.grammatica.code.CodeStyle;

/**
 * An abstract superclass for the various Visual Basic type code
 * generators.
 *
 * @author   Adrian Moore
 * @author   Per Cederberg
 * @version  1.5
 * @since    1.5
 */
public abstract class VisualBasicType extends CodeElementContainer {

    /**
     * The type modifier flags.
     */
    protected int modifiers;

    /**
     * The type name.
     */
    protected String name;

    /**
     * The name of the type that this type extends and/or implements.
     */
    protected String[] extendTypes;

    /**
     * The type comment.
     */
    protected VisualBasicComment comment = null;

    /**
     * Creates a new type code generator with the specified access
     * modifier that extends a specified type. If the extend type
     * null or "" is specified, no extends declaration will be
     * printed.
     *
     * @param modifiers      the modifier flag constants
     * @param name           the type name
     * @param extendType     the type to extend and/or implement
     */
    protected VisualBasicType(int modifiers, String name, String extendType) {
        this.modifiers = modifiers;
        this.name = name;
        if (extendType == null || extendType.equals("")) {
            this.extendTypes = new String[0];
        } else {
            this.extendTypes = new String[1];
            this.extendTypes[0] = extendType;
        }
    }

    /**
     * Creates a new type code generator with the specified access
     * modifier that extends a specified type.
     *
     * @param modifiers      the modifier flag constants
     * @param name           the type name
     * @param extendTypes    the types to extend and/or implement
     */
    protected VisualBasicType(int modifiers,
                              String name,
                              String[] extendTypes) {

        this.modifiers = modifiers;
        this.name = name;
        this.extendTypes = extendTypes;
    }

    /**
     * Returns the type name.
     *
     * @return the type name
     */
    public String toString() {
        return name;
    }

    /**
     * Sets the type comment. This method will remove any previous
     * type comment.
     *
     * @param comment        the new type comment
     */
    public void addComment(VisualBasicComment comment) {
        this.comment = comment;
    }

    /**
     * Prints the type to the specified stream.
     *
     * @param out            the output stream
     * @param style          the code style to use
     * @param indent         the indentation level
     * @param type           the type name
     */
    protected void print(PrintWriter out,
                         CodeStyle style,
                         int indent,
                         String type) {

        StringBuffer  buf = new StringBuffer();
        String        indentStr = style.getIndent(indent);

        // Print type comment
        if (comment != null) {
            comment.print(out, style, indent);
        }

        // Print type declaration
        buf.append(indentStr);
        buf.append(VisualBasicModifier.createModifierDecl(modifiers));
        buf.append(type);
        buf.append(" ");
        buf.append(name);
        buf.append("\n");
        for (int i = 0; i < extendTypes.length; i++) {
            buf.append(style.getIndent(indent+1));
            buf.append("Inherits ");
            buf.append(extendTypes[i]);
            buf.append("\n");
        }
        out.print(buf.toString());

        // Print type contents
        printContents(out, style, indent + 1);

        // Print end of type
        out.println(indentStr + "End " + type);
    }

    /**
     * Prints the lines separating two elements.
     *
     * @param out            the output stream
     * @param style          the code style to use
     * @param prev           the previous element, or null if first
     * @param next           the next element, or null if last
     */
    protected void printSeparator(PrintWriter out,
                                  CodeStyle style,
                                  CodeElement prev,
                                  CodeElement next) {

        if (prev == null || next == null) {
            // Do nothing
        } else {
            out.println();
        }
    }
}
