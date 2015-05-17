/*
 * JavaPackage.java
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

import java.io.File;
import java.io.PrintWriter;

import net.percederberg.grammatica.code.CodeElement;
import net.percederberg.grammatica.code.CodeStyle;

/**
 * A class generating a Java package declaration.
 *
 * @author   Per Cederberg
 * @version  1.0
 */
public class JavaPackage extends CodeElement {

    /**
     * The base package.
     */
    private JavaPackage basePackage;

    /**
     * The package name.
     */
    private String name;

    /**
     * Creates a new Java package with the specified name.
     *
     * @param name     the package name (including dots '.')
     */
    public JavaPackage(String name) {
        this(null, name);
    }

    /**
     * Creates a new Java package with the specified base package and name.
     *
     * @param base     the base package
     * @param name     the package name (including dots '.')
     */
    public JavaPackage(JavaPackage base, String name) {
        this.basePackage = base;
        this.name = name;
    }

    /**
     * Returns a string representation of this package.
     *
     * @return a string representation of this package
     */
    public String toString() {
        if (basePackage == null) {
            return name;
        } else {
            return basePackage.toString() + "." + name;
        }
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
        return 1;
    }

    /**
     * Returns the directory file containing the package files.
     *
     * @param baseDir    the base output directory
     *
     * @return the package directory
     */
    public File toFile(File baseDir) {
        String   firstName;
        String   restName;
        int      pos;

        if (basePackage != null) {
            baseDir = basePackage.toFile(baseDir);
        }
        restName = this.name;
        while ((pos = restName.indexOf('.')) > 0) {
            firstName = restName.substring(0, pos);
            restName = restName.substring(pos + 1);
            baseDir = new File(baseDir, firstName);
        }
        return new File(baseDir, restName);
    }

    /**
     * Prints the code element to the specified output stream.
     *
     * @param out            the output stream
     * @param style          the code style to use
     * @param indent         the indentation level
     */
    public void print(PrintWriter out, CodeStyle style, int indent) {
        out.println("package " + toString() + ";");
    }
}
