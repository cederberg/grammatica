/*
 * JavaType.java
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

package net.percederberg.grammatica.code.java;

import java.io.PrintWriter;

import net.percederberg.grammatica.code.CodeElement;
import net.percederberg.grammatica.code.CodeElementContainer;
import net.percederberg.grammatica.code.CodeStyle;

/**
 * An abstract superclass for the Java class and interface code
 * generators.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
abstract class JavaType extends CodeElementContainer {

    /**
     * The type modifier flags.
     */
    private int modifiers;

    /**
     * The type name.
     */
    private String name;

    /**
     * The name of the type that this type extends.
     */
    private String extendType;

    /**
     * The set of classes or interfaces that this type implements.
     */
    private String[] implementTypes = null;

    /**
     * The type comment.
     */
    private JavaComment comment = null;

    /**
     * Creates a new type code generator.
     *
     * @param modifiers      the modifier constant flags
     * @param name           the type name
     * @param extendsType    the class or interface to extend
     * @param implementType  the class or interface to implement
     */
    protected JavaType(int modifiers,
                       String name,
                       String extendsType,
                       String implementType) {

        this.modifiers = modifiers;
        this.name = name;
        this.extendType = extendsType;
        if (implementType == null || implementType.equals("")) {
            this.implementTypes = new String[0];
        } else {
            this.implementTypes = new String[1];
            this.implementTypes[0] = implementType;
        }
    }

    /**
     * Creates a new type code generator.
     *
     * @param modifiers      the modifier constant flags
     * @param name           the type name
     * @param extendsType    the class or interface to extend
     * @param implementTypes the classes or interfaces to implement
     */
    protected JavaType(int modifiers,
                       String name,
                       String extendsType,
                       String[] implementTypes) {

        this.modifiers = modifiers;
        this.name = name;
        this.extendType = extendsType;
        this.implementTypes = implementTypes;
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
     * Adds a comment to this type.
     *
     * @param comment     the new type comment
     */
    public void addComment(JavaComment comment) {
        this.comment = comment;
    }

    /**
     * Prints the type to the specified stream.
     *
     * @param out            the output stream
     * @param style          the code style to use
     * @param indent         the indentation level
     * @param type           the type name (i.e. "class" or "interface")
     */
    protected void print(PrintWriter out,
                         CodeStyle style,
                         int indent,
                         String type) {

        String        indentStr = style.getIndent(indent);
        String        codeIndentStr = style.getIndent(indent + 1);
        StringBuffer  buf = new StringBuffer();
        String        str;

        // Print type comment
        if (this.comment != null) {
            this.comment.print(out, style, indent);
        }

        // Print class declaration
        buf.append(indentStr);
        buf.append(JavaModifier.createModifierDecl(modifiers));
        buf.append(type);
        buf.append(" ");
        buf.append(name);
        if (extendType != null && !extendType.equals("")) {
            buf.append(" extends ");
            buf.append(extendType);
        }
        str = createImplDecl();
        if (str.length() > 0) {
            if (buf.length() + str.length() > style.getMargin()) {
                buf.append("\n");
                buf.append(codeIndentStr);
            } else {
                buf.append(" ");
            }
            buf.append(str);
        }
        buf.append(" {");
        out.println(buf.toString());
        out.println();

        // Print class contents
        printContents(out, style, indent + 1);

        // Print end of class
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

    /**
     * Creates a string with the implements declaration.
     *
     * @return a string with the implements declararation, or
     *         null for no implement declaration
     */
    private String createImplDecl() {
        StringBuffer  res = new StringBuffer("implements ");

        if (implementTypes == null || implementTypes.length <= 0) {
            return "";
        }
        for (int i = 0; i < implementTypes.length; i++) {
            res.append(implementTypes[i]);
            if (i + 1 < implementTypes.length) {
                res.append(", ");
            }
        }

        return res.toString();
    }
}
