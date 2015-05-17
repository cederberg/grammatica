/*
 * VisualBasicMethod.java
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
import java.util.LinkedList;

import net.percederberg.grammatica.code.CodeElement;
import net.percederberg.grammatica.code.CodeStyle;

/**
 * A class generating a Visual Basic method declaration.
 *
 * @author   Adrian Moore
 * @author   Per Cederberg
 * @version  1.5
 * @since    1.5
 */
public class VisualBasicMethod extends CodeElement {

    /**
     * The public access modifier constant.
     */
    public static final int PUBLIC = VisualBasicModifier.PUBLIC;

    /**
     * The protected friend access modifier constant.
     */
    public static final int PROTECTED_FRIEND =
        VisualBasicModifier.PROTECTED_FRIEND;

    /**
     * The protected access modifier constant.
     */
    public static final int PROTECTED = VisualBasicModifier.PROTECTED;

    /**
     * The friend access modifier constant.
     */
    public static final int FRIEND = VisualBasicModifier.FRIEND;

    /**
     * The private access modifier constant. Cannot be combined with
     * virtual, overrides, or must override.
     */
    public static final int PRIVATE = VisualBasicModifier.PRIVATE;

    /**
     * The shared modifier constant. Cannot be combined with overridable,
     * overrides, or must override.
     */
    public static final int SHARED = VisualBasicModifier.SHARED;

    /**
     * The shadows modifier constant. Cannot be combined with overrides.
     */
    public static final int SHADOWS = VisualBasicModifier.SHADOWS;

    /**
     * The overridable modifier constant. Cannot be combined with
     * private, shared, overrides, or must override.
     */
    public static final int OVERRIDABLE = VisualBasicModifier.OVERRIDABLE;

    /**
     * The not overridable modifier constant. Cannot be combined with
     * must override.
     */
    public static final int NOT_OVERRIDABLE =
        VisualBasicModifier.NOT_OVERRIDABLE;

    /**
     * The overrides modifier constant. Cannot be combined with
     * private, shared, overridable, or shadows.
     */
    public static final int OVERRIDES = VisualBasicModifier.OVERRIDES;

    /**
     * The must override modifier constant. Cannot be combined with
     * private, shared, overridable, not overridable, or extern.
     */
    public static final int MUST_OVERRIDE =
        VisualBasicModifier.MUST_OVERRIDE;

    /**
     * The overloads modifier constant.
     */
    public static final int OVERLOADS = VisualBasicModifier.OVERLOADS;

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
    private VisualBasicComment comment;

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
    public VisualBasicMethod(String name) {
        this(name, "");
    }

    /**
     * Creates a new method with the specified name and arguments. The
     * method will return void.
     *
     * @param name    the method name
     * @param args    the argument list, excluding parenthesis
     */
    public VisualBasicMethod(String name, String args) {
        this(name, args, "");
    }

    /**
     * Creates a new method with the specified arguments.
     *
     * @param name         the method name
     * @param args         the argument list, excluding parenthesis
     * @param returnType   the return type
     */
    public VisualBasicMethod(String name, String args, String returnType) {
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
    public VisualBasicMethod(int modifiers,
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
    public void addComment(VisualBasicComment comment) {
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
        return ((modifiers & SHARED) > 0) ? 6 : 8;
    }

    /**
     * Checks if the method source code can the printed. This method
     * will return false if the method is must override or if the print
     * code flag is set to false.
     *
     * @return true if method source code can be printed, or
     *         false otherwise
     */
    public boolean canPrintCode() {
        return printCode && (modifiers & MUST_OVERRIDE) == 0;
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
        res.append(VisualBasicModifier.createModifierDecl(modifiers));
        if (returnType.equals("")) {
            res.append("Sub");
        } else {
            res.append("Function");
        }
        res.append(" ");
        res.append(name);
        res.append("(");
        res.append(args);
        if (returnType.equals("")) {
            res.append(")");
        } else {
            res.append(") As ");
            res.append(returnType);
        }

        // Handle code
        if (canPrintCode()) {
            res.append("\n");
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
            if (returnType.equals("")) {
                res.append("End Sub");
            } else {
                res.append("End Function");
            }
        }

        // Print method
        out.println(res.toString());
    }
}
