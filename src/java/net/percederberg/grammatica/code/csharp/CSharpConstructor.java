/*
 * CSharpConstructor.java
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
 * Copyright (c) 2003-2004 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.code.csharp;

import java.io.PrintWriter;
import java.util.LinkedList;

import net.percederberg.grammatica.code.CodeElement;
import net.percederberg.grammatica.code.CodeStyle;

/**
 * A class generating a C# constructor declaration.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
public class CSharpConstructor extends CodeElement {

    /**
     * The public access modifier constant.
     */
    public static final int PUBLIC = CSharpModifier.PUBLIC;

    /**
     * The protected internal access modifier constant.
     */
    public static final int PROTECTED_INTERNAL =
        CSharpModifier.PROTECTED_INTERNAL;

    /**
     * The protected access modifier constant.
     */
    public static final int PROTECTED = CSharpModifier.PROTECTED;

    /**
     * The internal access modifier constant.
     */
    public static final int INTERNAL = CSharpModifier.INTERNAL;

    /**
     * The private access modifier constant.
     */
    public static final int PRIVATE = CSharpModifier.PRIVATE;

    /**
     * The extern modifier constant.
     */
    public static final int EXTERN = CSharpModifier.EXTERN;

    /**
     * The modifier flags.
     */
    private int modifiers;

    /**
     * The class to construct.
     */
    private CSharpClass cls;

    /**
     * The argument list.
     */
    private String args;

    /**
     * The initializer call code.
     */
    private String initializer;

    /**
     * The implementing code.
     */
    private LinkedList code;

    /**
     * The constructor comment.
     */
    private CSharpComment comment;

    /**
     * Creates a new empty constructor.
     */
    public CSharpConstructor() {
        this("");
    }

    /**
     * Creates a new constructor with the specified arguments.
     *
     * @param args           the argument list, excluding parenthesis
     */
    public CSharpConstructor(String args) {
        this(PUBLIC, args);
    }

    /**
     * Creates a new constructor with the specified arguments.
     *
     * @param modifiers      the modifier flags
     * @param args           the argument list, excluding parenthesis
     */
    public CSharpConstructor(int modifiers, String args) {
        this.modifiers = modifiers;
        this.cls = null;
        this.args = args;
        this.initializer = null;
        this.code = new LinkedList();
        this.comment = null;
    }

    /**
     * Returns the class for this constructor, or null.
     *
     * @return the class for this constructor, or
     *         null if none has been assigned
     */
    public CSharpClass getCSharpClass() {
        return this.cls;
    }

    /**
     * Sets the class for this constructor.
     *
     * @param cls      the class to add the constructor to
     */
    void setCSharpClass(CSharpClass cls) {
        this.cls = cls;
    }

    /**
     * Adds an initializer call, i.e. a call to another constructor.
     *
     * @param initializer    the initializer call
     */
    public void addInitializer(String initializer) {
        this.initializer = initializer;
    }

    /**
     * Adds one or more lines of actual code.
     *
     * @param codeLines     the lines of Java code to add
     */
    public void addCode(String codeLines) {
        int  pos;

        pos = codeLines.indexOf('\n');
        while (pos >= 0) {
            this.code.add(codeLines.substring(0, pos));
            codeLines = codeLines.substring(pos + 1);
            pos = codeLines.indexOf('\n');
        }
        this.code.add(codeLines);
    }

    /**
     * Sets a comment for this constructor.
     *
     * @param comment       the new constructor comment
     */
    public void addComment(CSharpComment comment) {
        this.comment = comment;
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
        return 7;
    }

    /**
     * Prints the code element to the specified output stream.
     *
     * @param out            the output stream
     * @param style          the code style to use
     * @param indent         the indentation level
     */
    public void print(PrintWriter out, CodeStyle style, int indent) {
        String        indentStr = style.getIndent(indent);
        String        codeIndentStr = style.getIndent(indent + 1);
        StringBuffer  res = new StringBuffer();

        // Print comment
        if (comment != null) {
            comment.print(out, style, indent);
        }

        // Handle declaration
        res.append(indentStr);
        res.append(CSharpModifier.createModifierDecl(modifiers));
        res.append(cls.toString());
        res.append("(");
        res.append(args);
        res.append(")");

        // Handle initializer
        if (initializer != null) {
            res.append("\n");
            res.append(codeIndentStr);
            res.append(": ");
            res.append(initializer);
        }
        res.append(" {\n");

        // Handle code
        if (initializer != null && code.size() > 0) {
            res.append("\n");
        }
        for (int i = 0; i < code.size(); i++) {
            if (code.get(i).toString().length() > 0) {
                res.append(codeIndentStr);
                res.append(code.get(i).toString());
                res.append("\n");
            } else {
                res.append("\n");
            }
        }
        res.append(indentStr);
        res.append("}");

        // Print method
        out.println(res.toString());
    }
}
