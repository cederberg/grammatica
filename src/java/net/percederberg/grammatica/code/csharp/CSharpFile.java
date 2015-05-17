/*
 * CSharpFile.java
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

package net.percederberg.grammatica.code.csharp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import net.percederberg.grammatica.code.CodeElementContainer;
import net.percederberg.grammatica.code.CodeStyle;

/**
 * A class generating a C# source code file.
 *
 * @author   Per Cederberg
 * @version  1.5
 */
public class CSharpFile extends CodeElementContainer {

    /**
     * The file to write to.
     */
    private File file;

    /**
     * Creates a new C# source code file.
     *
     * @param basedir        the base output directory
     * @param basename       the base file name (without extension)
     */
    public CSharpFile(File basedir, String basename) {
        this.file = new File(basedir, basename + ".cs");
    }

    /**
     * Returns the file name.
     *
     * @return the file name.
     */
    public String toString() {
        return file.getName();
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
     * Adds a file comment.
     *
     * @param comment        the file comment to add
     */
    public void addComment(CSharpComment comment) {
        addElement(comment);
    }

    /**
     * Adds a using declaration to the file.
     *
     * @param u              the using declaration to add
     */
    public void addUsing(CSharpUsing u) {
        addElement(u);
    }

    /**
     * Adds a namespace declaration to the file.
     *
     * @param n              the namespace declaration to add
     */
    public void addNamespace(CSharpNamespace n) {
        addElement(n);
    }

    /**
     * Adds a class declaration to the file.
     *
     * @param c              the class declaration to add
     */
    public void addClass(CSharpClass c) {
        addElement(c);
    }

    /**
     * Adds an enumeration declaration to the file.
     *
     * @param e              the enumeration to add
     */
    public void addEnumeration(CSharpEnumeration e) {
        addElement(e);
    }

    /**
     * Writes the source code for this file. Any previous file with
     * this name will be overwritten.
     *
     * @param style          the code style to use
     *
     * @throws IOException if the file could not be written properly
     */
    public void writeCode(CodeStyle style) throws IOException {
        PrintWriter  out;

        createFile(file);
        out = new PrintWriter(new FileWriter(file));
        print(out, style, 0);
        out.close();
    }

    /**
     * Prints the file contents to the specified output stream.
     *
     * @param out            the output stream
     * @param style          the code style to use
     * @param indent         the indentation level
     */
    public void print(PrintWriter out, CodeStyle style, int indent) {
        printContents(out, style, indent);
    }
}
