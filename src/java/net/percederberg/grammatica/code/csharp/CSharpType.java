/*
 * CSharpType.java
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
import net.percederberg.grammatica.code.CodeElementContainer;
import net.percederberg.grammatica.code.CodeStyle;

/**
 * An abstract superclass for the various C# type code generators.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
abstract class CSharpType extends CodeElementContainer {

    /**
     * The type modifier flags.
     */
    private int modifiers;

    /**
     * The type name.
     */
    private String name;

    /**
     * The name of the type that this type extends and/or implements.
     */
    private String[] extendTypes;

    /**
     * The type comment.
     */
    private CSharpComment comment = null;

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
    protected CSharpType(int modifiers, String name, String extendType) {
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
    protected CSharpType(int modifiers, String name, String[] extendTypes) {
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
    public void addComment(CSharpComment comment) {
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
        buf.append(CSharpModifier.createModifierDecl(modifiers));
        buf.append(type);
        buf.append(" ");
        buf.append(name);
        for (int i = 0; i < extendTypes.length; i++) {
            if (i == 0) {
                buf.append(" : ");
            } else {
                buf.append(", ");
            }
            buf.append(extendTypes[i]);
        }
        buf.append(" {");
        out.println(buf.toString());

        // Print type contents
        printContents(out, style, indent + 1);

        // Print end of type
        out.println(indentStr + "}");
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
