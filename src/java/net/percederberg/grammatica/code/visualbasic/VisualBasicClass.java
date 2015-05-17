/*
 * VisualBasicClass.java
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
import net.percederberg.grammatica.code.CodeStyle;

/**
 * A class generating a Visual Basic class declaration.
 *
 * @author   Adrian Moore
 * @author   Per Cederberg
 * @version  1.5
 * @since    1.5
 */
public class VisualBasicClass extends VisualBasicType {

    /**
     * The public access modifier constant.
     */
    public static final int PUBLIC = VisualBasicModifier.PUBLIC;

    /**
     * The protected internal access modifier constant. May only be
     * used when declared inside another type.
     */
    public static final int PROTECTED_FRIEND =
        VisualBasicModifier.PROTECTED_FRIEND;

    /**
     * The protected access modifier constant. May only be used when
     * declared inside another type.
     */
    public static final int PROTECTED = VisualBasicModifier.PROTECTED;

    /**
     * The internal access modifier constant.
     */
    public static final int FRIEND = VisualBasicModifier.FRIEND;

    /**
     * The private access modifier constant. May only be used when
     * declared inside another type.
     */
    public static final int PRIVATE = VisualBasicModifier.PRIVATE;

    /**
     * The must inherit modifier constant.
     */
    public static final int MUST_INHERIT = VisualBasicModifier.MUST_INHERIT;

    /**
     * The not inheritable modifier constant.
     */
    public static final int NOT_INHERITABLE =
        VisualBasicModifier.NOT_INHERITABLE;

    /**
     * The shadows modifier constant. May only be used when declared
     * inside another type.
     */
    public static final int SHADOWS = VisualBasicModifier.SHADOWS;

    /**
     * Creates a new class code generator with a public access
     * modifier.
     *
     * @param name           the class name
     */
    public VisualBasicClass(String name) {
        this(PUBLIC, name);
    }

    /**
     * Creates a new class code generator with the specified
     * modifiers.
     *
     * @param modifiers      the modifier flag constants
     * @param name           the class name
     */
    public VisualBasicClass(int modifiers, String name) {
        this(modifiers, name, "");
    }

    /**
     * Creates a new class code generator with the specified access
     * modifier that extends the specified class.
     *
     * @param modifiers      the modifier flag constants
     * @param name           the class name
     * @param extendsClass   the class to extend or implement
     */
    public VisualBasicClass(int modifiers, String name, String extendsClass) {
        super(modifiers, name, extendsClass);
    }

    /**
     * Creates a new class code generator with the specified access
     * modifier that extends and implements the specified classes or
     * interfaces.
     *
     * @param modifiers      the modifier flag constants
     * @param name           the class name
     * @param extendClasses  the classes to extend or implement
     */
    public VisualBasicClass(int modifiers,
                            String name,
                            String[] extendClasses) {

        super(modifiers, name, extendClasses);
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
        return 10;
    }

    /**
     * Adds an inner class as a member.
     *
     * @param member         the inner class to add
     */
    public void addClass(VisualBasicClass member) {
        addElement(member);
    }

    /**
     * Adds an enumeration as a member.
     *
     * @param member         the enumeration to add
     */
    public void addEnumeration(VisualBasicEnumeration member) {
        addElement(member);
    }

    /**
     * Adds a constructor to the class.
     *
     * @param member         the member to add
     */
    public void addConstructor(VisualBasicConstructor member) {
        member.setVisualBasicClass(this);
        addElement(member);
    }

    /**
     * Adds a method to the class.
     *
     * @param member         the member to add
     */
    public void addMethod(VisualBasicMethod member) {
        addElement(member);
    }

    /**
     * Prints the class to the specified stream.
     *
     * @param out            the output stream
     * @param style          the code style to use
     * @param indent         the indentation level
     */
    public void print(PrintWriter out, CodeStyle style, int indent) {
        print(out, style, indent, "Class");
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

        if (next == null) {
            // Do nothing
        } else {
            out.println();
        }
    }
}
