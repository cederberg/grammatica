/*
 * GrammaticaTask.java
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

package net.percederberg.grammatica.ant;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Vector;

import net.percederberg.grammatica.Grammar;
import net.percederberg.grammatica.GrammarException;
import net.percederberg.grammatica.parser.ParserLogException;

/**
 * A Grammatica Ant task.
 *
 * @author   Per Cederberg
 * @version  1.5
 * @since    1.4
 */
public class GrammaticaTask {

    /**
     * The grammar file to process.
     */
    private File file = null;

    /**
     * The fail on error flag.
     */
    private boolean failOnError = true;

    /**
     * The list of processing elements.
     */
    private Vector processors = new Vector();

    /**
     * Creates a new Grammatica Ant task.
     */
    public GrammaticaTask() {
        // Nothing to do here
    }

    /**
     * Sets the grammar file.
     *
     * @param file           the new grammar file
     */
    public void setGrammar(File file) {
        this.file = file;
    }

    /**
     * Sets the fail on error flag. This flag defaults to true.
     *
     * @param failOnError    the new fail on error flag value
     */
    public void setFailonerror(boolean failOnError) {
        this.failOnError = failOnError;
    }

    /**
     * Adds a new validation inner element.
     *
     * @param elem           the validation element
     */
    public void addValidation(ValidationElement elem) {
        processors.add(elem);
    }

    /**
     * Adds a new C# code generation inner element.
     *
     * @param elem           the C# code generation element
     */
    public void addCSharp(CSharpElement elem) {
        processors.add(elem);
    }

    /**
     * Adds a new Java code generation inner element.
     *
     * @param elem           the Java code generation element
     */
    public void addJava(JavaElement elem) {
        processors.add(elem);
    }

    /**
     * Adds a new Visual Basic code generation inner element.
     *
     * @param elem           the Visual Basic code generation element
     */
    public void addVisualBasic(VisualBasicElement elem) {
        processors.add(elem);
    }

    /**
     * Executes the task.
     *
     * @throws RuntimeException if the task execution failed
     */
    public void execute() throws RuntimeException {
        Grammar  grammar;
        int      i;

        // Validate all elements
        if (file == null) {
            throw new RuntimeException("missing 'grammar' attribute");
        }
        if (processors.size() <= 0) {
            throw new RuntimeException(
                "missing <validate>, <java>, <csharp> or <visualbasic> " +
                "inner element");
        }
        for (i = 0; i < processors.size(); i++) {
            ((ProcessingElement) processors.get(i)).validate();
        }

        // Read grammar file
        try {
            grammar = new Grammar(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (ParserLogException e) {
            handleError(e);
            return;
        } catch (GrammarException e) {
            handleError(e);
            return;
        }

        // Process grammar file
        for (i = 0; i < processors.size(); i++) {
            try {
                ((ProcessingElement) processors.get(i)).process(grammar);
            } catch (RuntimeException e) {
                handleError(e);
            }
        }
    }

    /**
     * Handles an error. This will either print the error or throw
     * a build exception, depending of the failOnError flag.
     *
     * @param e              the error exception
     *
     * @throws RuntimeException if the build should fail on errors
     */
    private void handleError(Exception e) throws RuntimeException {
        if (failOnError) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
        System.err.println("ERROR: " + e.getMessage());
    }
}
