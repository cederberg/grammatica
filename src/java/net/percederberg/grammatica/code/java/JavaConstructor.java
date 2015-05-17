/*
 * JavaConstructor.java
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the BSD license.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * LICENSE.txt file for more details.
 *
 * Copyright (c) 2003-2015 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.code.java;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedList;

import net.percederberg.grammatica.code.CodeElement;
import net.percederberg.grammatica.code.CodeStyle;

/**
 * A class generating a Java constructor declaration.
 *
 * @author   Per Cederberg
 * @version  1.5
 */
public class JavaConstructor extends CodeElement {

    /**
     * The public access modifier constant.
     */
    public static final int PUBLIC = JavaModifier.PUBLIC;

    /**
     * The protected access modifier constant.
     */
    public static final int PROTECTED = JavaModifier.PROTECTED;

    /**
     * The package local access modifier constant (i.e. no modifier).
     */
    public static final int PACKAGE_LOCAL = JavaModifier.PACKAGE_LOCAL;

    /**
     * The private access modifier constant.
     */
    public static final int PRIVATE = JavaModifier.PRIVATE;

    /**
     * The modifier flags.
     */
    private int modifiers;

    /**
     * The class to construct.
     */
    private JavaClass cls;

    /**
     * The argument list.
     */
    private String args;

    /**
     * The exceptions declared to be thrown.
     */
    private LinkedList throwList;

    /**
     * The implementing code.
     */
    private LinkedList code;

    /**
     * The constructor comment.
     */
    private JavaComment comment;

    /**
     * Creates a new empty constructor.
     */
    public JavaConstructor() {
        this("");
    }

    /**
     * Creates a new constructor with the specified arguments.
     *
     * @param args           the argument list, excluding parenthesis
     */
    public JavaConstructor(String args) {
        this(PUBLIC, args);
    }

    /**
     * Creates a new constructor with the specified arguments.
     *
     * @param modifiers      the modifier flags
     * @param args           the argument list, excluding parenthesis
     */
    public JavaConstructor(int modifiers, String args) {
        this.modifiers = modifiers;
        this.cls = null;
        this.args = args;
        this.throwList = new LinkedList();
        this.code = new LinkedList();
        this.comment = null;
    }

    /**
     * Returns the class for this constructor, or null.
     *
     * @return the class for this constructor, or
     *         null if none has been assigned
     */
    public JavaClass getJavaClass() {
        return this.cls;
    }

    /**
     * Sets the class for this constructor.
     *
     * @param cls      the class to add the constructor to
     */
    void setJavaClass(JavaClass cls) {
        this.cls = cls;
    }

    /**
     * Adds a class to the list of exceptions thrown.
     *
     * @param className     the name of the exception thrown
     */
    public void addThrows(String className) {
        this.throwList.add(className);
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
    public void addComment(JavaComment comment) {
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
        String        str;
        boolean       brokenThrows = false;

        // Print comment
        if (comment != null) {
            comment.print(out, style, indent);
        }

        // Handle declaration
        res.append(indentStr);
        res.append(JavaModifier.createModifierDecl(modifiers));
        res.append(cls.toString());
        res.append("(");
        res.append(args);
        res.append(")");
        str = getThrowDecl();
        if (str.length() > 0) {
            if (res.length() + str.length() < style.getMargin()) {
                res.append(" ");
            } else {
                res.append("\n");
                res.append(codeIndentStr);
                brokenThrows = true;
            }
            res.append(str);
        }
        res.append(" {\n");

        // Handle code
        if (brokenThrows && code.size() > 0) {
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

    /**
     * Returns a 'throws' declaration. If there are no classes to throw,
     * an empty string will be returned.
     *
     * @return a throw declaration, or
     *         an empty string if no exceptions are thrown
     */
    private String getThrowDecl() {
        StringBuffer  res = new StringBuffer("throws ");

        if (throwList.size() == 0) {
            return "";
        }
        Collections.sort(throwList);
        for (int i = 0; i < throwList.size(); i++) {
            res.append(throwList.get(i).toString());
            if (i < throwList.size() - 1) {
                res.append(", ");
            }
        }
        return res.toString();
    }
}
