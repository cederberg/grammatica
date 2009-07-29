/*
 * CSharpProperty.java
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
 * A class generating a C# property declaration.
 *
 * @author   Connor Prussin, <cprussin at vt dot edu>
 * @version  1.5
 */
public class CSharpProperty extends CodeElement {

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
     * The method modifier flags.
     */
    private int modifiers;

    /**
     * The get modifier flags.
     */
    private int getModifiers;

    /**
     * The set modifier flags.
     */
    private int setModifiers;

    /**
     * The method name.
     */
    private String name;

    /**
     * The return type.
     */
    private String returnType;

    /**
     * The implementing "get" code.
     */
    private LinkedList<String> getCode;

    /**
     * The implementing "set" code.
     */
    private LinkedList<String> setCode;

    /**
     * The property comment.
     */
    private CSharpComment comment;

    /**
     * The print code flag.
     */
    private boolean printCode;

    /**
     * Creates a new property with the specified arguments.
     *
     * @param name         the method name
     * @param returnType   the return type
     */
    public CSharpProperty(String name, String returnType) {
        this(PUBLIC, name, returnType);
    }

    /**
     * Creates a new property with the specified arguments.
     *
     * @param modifiers    the modifier flags to use
     * @param name         the method name
     * @param returnType   the return type
     */
    public CSharpProperty(int modifiers,
                        String name,
                        String returnType) {
        this(modifiers, 0, 0, name, returnType);
    }

    /**
     * Creates a new property with the specified arguments.
     *
     * @param modifiers    the modifier flags to use
     * @param getModifiers the modifiers specific to the "get" part
     * @param setModifiers the modifiers specific to the "set" part
     * @param name         the method name
     * @param returnType   the return type
     */
    public CSharpProperty(int modifiers,
                        int getModifiers,
                        int setModifiers,
                        String name,
                        String returnType) {
        this.modifiers = modifiers;
        this.getModifiers = getModifiers;
        this.setModifiers = setModifiers;
        this.name = name;
        this.returnType = returnType;
        this.getCode = new LinkedList<String>();
        this.setCode = new LinkedList<String>();
        this.comment = null;
        this.printCode = true;
    }

    /**
     * Adds one or more lines of actual code for the "get" part.
     *
     * @param codeLines     the lines of Java code to add
     */
    public void addGetCode(String codeLines) {
        int  pos;

        pos = codeLines.indexOf('\n');
        while (pos >= 0) {
            getCode.add(codeLines.substring(0, pos));
            codeLines = codeLines.substring(pos + 1);
            pos = codeLines.indexOf('\n');
        }
        getCode.add(codeLines);
    }

    /**
     * Adds one or more lines of actual code for the "set" part.
     *
     * @param codeLines     the lines of Java code to add
     */
    public void addSetCode(String codeLines) {
        int  pos;

        pos = codeLines.indexOf('\n');
        while (pos >= 0) {
            setCode.add(codeLines.substring(0, pos));
            codeLines = codeLines.substring(pos + 1);
            pos = codeLines.indexOf('\n');
        }
        setCode.add(codeLines);
    }

    /**
     * Sets a comment for this property.
     *
     * @param comment       the new property comment
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
     * Checks if the property source code can the printed. This method
     * will return false if the property is abstract or if the print
     * code flag is set to false.
     *
     * @return true if property source code can be printed, or
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
        String        partIndentStr = style.getIndent(indent + 1);
        String        codeIndentStr = style.getIndent(indent + 2);
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

        // Handle code
        if (canPrintCode()) {
            res.append(" {\n");
            if (getCode.size() > 0) {
                res.append(partIndentStr);
                if (getModifiers != 0) {
                    res.append(CSharpModifier.createModifierDecl(getModifiers));
                }
                res.append("get {\n");
                for (int i = 0; i < getCode.size(); i++) {
                    if (getCode.get(i).toString().length() > 0) {
                        res.append(codeIndentStr);
                        res.append(getCode.get(i).toString());
                        res.append("\n");
                    } else {
                        res.append("\n");
                    }
                }
                res.append(partIndentStr);
                res.append("}\n");
            }
            if (setCode.size() > 0) {
                res.append(partIndentStr);
                if (setModifiers != 0) {
                    res.append(CSharpModifier.createModifierDecl(setModifiers));
                }
                res.append("set {\n");
                for (int i = 0; i < setCode.size(); i++) {
                    if (setCode.get(i).toString().length() > 0) {
                        res.append(codeIndentStr);
                        res.append(setCode.get(i).toString());
                        res.append("\n");
                    } else {
                        res.append("\n");
                    }
                }
                res.append(partIndentStr);
                res.append("}\n");
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
