/*
 * CSharpClass.java
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

package net.percederberg.grammatica.code.csharp;

import java.io.PrintWriter;

import net.percederberg.grammatica.code.CodeElement;
import net.percederberg.grammatica.code.CodeStyle;

/**
 * A class generating a C# class declaration.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
public class CSharpClass extends CSharpType {

    /**
     * The public access modifier constant.
     */
    public static final int PUBLIC = CSharpModifier.PUBLIC;

    /**
     * The protected internal access modifier constant. May only be
     * used when declared inside another type.
     */
    public static final int PROTECTED_INTERNAL =
        CSharpModifier.PROTECTED_INTERNAL;

    /**
     * The protected access modifier constant. May only be used when
     * declared inside another type.
     */
    public static final int PROTECTED = CSharpModifier.PROTECTED;

    /**
     * The internal access modifier constant.
     */
    public static final int INTERNAL = CSharpModifier.INTERNAL;

    /**
     * The private access modifier constant. May only be used when
     * declared inside another type.
     */
    public static final int PRIVATE = CSharpModifier.PRIVATE;

    /**
     * The abstract modifier constant.
     */
    public static final int ABSTRACT = CSharpModifier.ABSTRACT;

    /**
     * The sealed modifier constant.
     */
    public static final int SEALED = CSharpModifier.SEALED;

    /**
     * The new modifier constant. May only be used when declared
     * inside another type.
     */
    public static final int NEW = CSharpModifier.NEW;

    /**
     * Creates a new class code generator with a public access
     * modifier.
     *
     * @param name           the class name
     */
    public CSharpClass(String name) {
        this(PUBLIC, name);
    }

    /**
     * Creates a new class code generator with the specified
     * modifiers.
     *
     * @param modifiers      the modifier flag constants
     * @param name           the class name
     */
    public CSharpClass(int modifiers, String name) {
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
    public CSharpClass(int modifiers, String name, String extendsClass) {
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
    public CSharpClass(int modifiers, String name, String[] extendClasses) {
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
    public void addClass(CSharpClass member) {
        addElement(member);
    }

    /**
     * Adds an enumeration as a member.
     *
     * @param member         the enumeration to add
     */
    public void addEnumeration(CSharpEnumeration member) {
        addElement(member);
    }

    /**
     * Adds a constructor to the class.
     *
     * @param member         the member to add
     */
    public void addConstructor(CSharpConstructor member) {
        member.setCSharpClass(this);
        addElement(member);
    }

    /**
     * Adds a method to the class.
     *
     * @param member         the member to add
     */
    public void addMethod(CSharpMethod member) {
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
        print(out, style, indent, "class");
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
