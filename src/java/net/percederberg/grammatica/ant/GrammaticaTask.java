/*
 * GrammaticaTask.java
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
 * Copyright (c) 2003 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.ant;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import net.percederberg.grammatica.Grammar;
import net.percederberg.grammatica.GrammarException;
import net.percederberg.grammatica.parser.ParserLogException;

/**
 * A Grammatica Ant task.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.4
 * @since    1.4
 */
public class GrammaticaTask extends Task {

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
    public void addValidate(ValidationElement elem) {
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
     * Executes the task.
     * 
     * @throws BuildException if the task execution failed
     */
    public void execute() throws BuildException {
        Grammar  grammar;
        int      i;

        // Validate all elements
        if (file == null) {
            throw new BuildException("missing 'grammar' attribute");
        }
        if (processors.size() <= 0) {
            throw new BuildException(
                "missing <validate>, <java>, or <csharp> inner element");
        }
        for (i = 0; i < processors.size(); i++) {
            ((ProcessingElement) processors.get(i)).validate();
        }

        // Read grammar file
        try {
            grammar = new Grammar(file);
        } catch (FileNotFoundException e) {
            throw new BuildException(e);
        } catch (ParserLogException e) {
            if (failOnError) {
                throw new BuildException(e);
            } else {
                return;
            }
        } catch (GrammarException e) {
            if (failOnError) {
                throw new BuildException(e);
            } else {
                return;
            }
        }

        // Process grammar file
        for (i = 0; i < processors.size(); i++) {
            try {
                ((ProcessingElement) processors.get(i)).process(grammar);
            } catch (BuildException e) {
                if (failOnError) {
                    throw e;
                }
            }
        }
    }
}
