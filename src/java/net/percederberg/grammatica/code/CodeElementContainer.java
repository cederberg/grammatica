/*
 * CodeElementContainer.java
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

package net.percederberg.grammatica.code;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedList;

/**
 * The abstract base class for all code element containers. The code
 * element containers contains other code elements.
 *
 * @author   Per Cederberg
 * @version  1.5
 */
public abstract class CodeElementContainer extends CodeElement {

    /**
     * The code element contents. This list contains the code elements
     * contained inside this element.
     */
    private LinkedList contents = new LinkedList();

    /**
     * Adds a code element to this container.
     *
     * @param elem           the code element to add
     */
    protected void addElement(CodeElement elem) {
        if (!contents.contains(elem)) {
            contents.add(elem);
        }
    }

    /**
     * Prints all the contained code elements to the specified output
     * stream. The code elements will be sorted by their category
     * number before printing.
     *
     * @param out            the output stream
     * @param style          the code style to use
     * @param indent         the indentation level
     */
    protected void printContents(PrintWriter out,
                                 CodeStyle style,
                                 int indent) {

        CodeElement  prev = null;
        CodeElement  next;

        Collections.sort(contents);
        for (int i = 0; i < contents.size(); i++) {
            next = (CodeElement) contents.get(i);
            printSeparator(out, style, prev, next);
            next.print(out, style, indent);
            prev = next;
        }
    }

    /**
     * Prints the lines separating two elements. By default this
     * method prints a newline before the first element, and between
     * elements with different category numbers.
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
        } else if (prev.category() != next.category()) {
            out.println();
        }
    }

    /**
     * Creates a file and the parent directories if they didn't exist.
     *
     * @param file            the file to create
     *
     * @throws IOException if the file couldn't be created properly
     */
    protected void createFile(File file) throws IOException {
        File dir = file.getParentFile();

        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new IOException("couldn't create " + file + ": " +
                                      e.getMessage());
            }
        }
    }
}
