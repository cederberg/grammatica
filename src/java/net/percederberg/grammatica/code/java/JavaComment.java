/*
 * JavaComment.java
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

import net.percederberg.grammatica.code.CodeElement;
import net.percederberg.grammatica.code.CodeStyle;

/**
 * A class generating a Java comment.
 *
 * @author   Per Cederberg
 * @version  1.5
 */
public class JavaComment extends CodeElement {

    /**
     * The JavaDoc documentation comment type.
     */
    public static final int DOCUMENTATION = 0;

    /**
     * The block comment type.
     */
    public static final int BLOCK = 1;

    /**
     * The single line comment type. Note that this may be used even
     * if the comment spans several lines, as the // characters will
     * be duplicated for each line.
     */
    public static final int SINGLELINE = 2;

    /**
     * The comment type.
     */
    private int type;

    /**
     * The comment text.
     */
    private String comment;

    /**
     * Creates a new documentation comment with no indentation.
     *
     * @param comment        the comment text
     */
    public JavaComment(String comment) {
        this(DOCUMENTATION, comment);
    }

    /**
     * Creates a new comment of the specified type.
     *
     * @param type           the comment type
     * @param comment        the comment text
     *
     * @see #DOCUMENTATION
     * @see #BLOCK
     * @see #SINGLELINE
     */
    public JavaComment(int type, String comment) {
        if (type == BLOCK || type == SINGLELINE) {
            this.type = type;
        } else {
            this.type = DOCUMENTATION;
        }
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
        return 0;
    }

    /**
     * Prints the code element to the specified output stream.
     *
     * @param out            the output stream
     * @param style          the code style to use
     * @param indent         the indentation level
     */
    public void print(PrintWriter out, CodeStyle style, int indent) {
        String  indentStr = style.getIndent(indent);
        String  firstLine;
        String  restLines;
        int     pos;

        // Comment head
        if (type == DOCUMENTATION) {
            out.println(indentStr + "/**");
        } else if (type == BLOCK) {
            out.println(indentStr + "/*");
        }

        // Comment body
        restLines = comment;
        while ((pos = restLines.indexOf('\n')) >= 0) {
            firstLine = restLines.substring(0, pos);
            restLines = restLines.substring(pos + 1);
            printLine(out, indentStr, firstLine);
        }
        printLine(out, indentStr, restLines);

        // Comment tail
        if (type != SINGLELINE) {
            out.println(indentStr + " */");
        }
    }

    /**
     * Prints a single comment line.
     *
     * @param out            the output stream
     * @param indent         the indentation string
     * @param line           the comment line to print
     */
    private void printLine(PrintWriter out, String indent, String line) {
        if (type == SINGLELINE) {
            out.print(indent + "//");
        } else {
            out.print(indent + " *");
        }
        if (line.equals("")) {
            out.println();
        } else {
            out.println(" " + line);
        }
    }
}
