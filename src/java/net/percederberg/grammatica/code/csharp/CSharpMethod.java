/*
 * CSharpMethod.java
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
 * A class generating a C# method declaration.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
public class CSharpMethod extends CodeElement {

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
     * The private access modifier constant. Cannot be combined with
     * virtual, override, or abstract.
     */
    public static final int PRIVATE = CSharpModifier.PRIVATE;

    /**
     * The static modifier constant. Cannot be combined with virtual,
     * override, or abstract.
     */
    public static final int STATIC = CSharpModifier.STATIC;

    /**
     * The new modifier constant. Cannot be combined with override.
     */
    public static final int NEW = CSharpModifier.NEW;

    /**
     * The virtual modifier constant. Cannot be combined with private,
     * static, override, or abstract.
     */
    public static final int VIRTUAL = CSharpModifier.VIRTUAL;

    /**
     * The sealed modifier constant. Cannot be combined with abstract.
     */
    public static final int SEALED = CSharpModifier.SEALED;

    /**
     * The override modifier constant. Cannot be combined with private,
     * static, virtual, or new.
     */
    public static final int OVERRIDE = CSharpModifier.OVERRIDE;

    /**
     * The abstract modifier constant. Cannot be combined with private,
     * static, virtual, sealed, or extern.
     */
    public static final int ABSTRACT = CSharpModifier.ABSTRACT;

    /**
     * The extern modifier constant. Cannot be combined with abstract.
     */
    public static final int EXTERN = CSharpModifier.EXTERN;

    /**
     * The method modifier flags.
     */
    private int modifiers;

    /**
     * The method name.
     */
    private String name;

    /**
     * The argument list.
     */
    private String args;

    /**
     * The return type.
     */
    private String returnType;

    /**
     * The implementing code.
     */
    private LinkedList code;

    /**
     * The method comment.
     */
    private CSharpComment comment;

    /**
     * The print code flag.
     */
    private boolean printCode;

    /**
     * Creates a new method with the specified name. The method will
     * not take any arguments and will return void.
     *
     * @param name     the method name
     */
    public CSharpMethod(String name) {
        this(name, "");
    }

    /**
     * Creates a new method with the specified name and arguments. The
     * method will return void.
     *
     * @param name    the method name
     * @param args    the argument list, excluding parenthesis
     */
    public CSharpMethod(String name, String args) {
        this(name, args, "void");
    }

    /**
     * Creates a new method with the specified arguments.
     *
     * @param name         the method name
     * @param args         the argument list, excluding parenthesis
     * @param returnType   the return type
     */
    public CSharpMethod(String name, String args, String returnType) {
        this(PUBLIC, name, args, returnType);
    }

    /**
     * Creates a new method with the specified arguments.
     *
     * @param modifiers    the modifier flags to use
     * @param name         the method name
     * @param args         the argument list, excluding parenthesis
     * @param returnType   the return type
     */
    public CSharpMethod(int modifiers,
                        String name,
                        String args,
                        String returnType) {

        this.modifiers = modifiers;
        this.name = name;
        this.args = args;
        this.returnType = returnType;
        this.code = new LinkedList();
        this.comment = null;
        this.printCode = true;
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
            code.add(codeLines.substring(0, pos));
            codeLines = codeLines.substring(pos + 1);
            pos = codeLines.indexOf('\n');
        }
        code.add(codeLines);
    }

    /**
     * Sets a comment for this method.
     *
     * @param comment       the new method comment
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
        return ((modifiers & STATIC) > 0) ? 6 : 8;
    }

    /**
     * Checks if the method source code can the printed. This method
     * will return false if the method is abstract or if the print
     * code flag is set to false.
     *
     * @return true if method source code can be printed, or
     *         false otherwise
     */
    public boolean canPrintCode() {
        return printCode && (modifiers & ABSTRACT) == 0;
    }

    /**
     * Sets the print code flag.
     *
     * @param value          the new print code flag value
     */
    public void setPrintCode(boolean value) {
        this.printCode = value;
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
        res.append(returnType);
        res.append(" ");
        res.append(name);
        res.append("(");
        res.append(args);
        res.append(")");

        // Handle code
        if (canPrintCode()) {
            res.append(" {\n");
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
        } else {
            res.append(";");
        }

        // Print method
        out.println(res.toString());
    }
}
