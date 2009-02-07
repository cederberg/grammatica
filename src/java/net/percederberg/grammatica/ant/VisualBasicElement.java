/*
 * VisualBasicElement.java
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 * Copyright (c) 2004-2005 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.ant;

import java.io.File;
import java.io.IOException;

import net.percederberg.grammatica.Grammar;
import net.percederberg.grammatica.output.VisualBasicParserGenerator;

/**
 * A Visual Basic output element. This element creates Visual
 * Basic.NET source code for a parser.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 * @since    1.5
 */
public class VisualBasicElement implements ProcessingElement {

    /**
     * The output directory.
     */
    private File dir = null;

    /**
     * The namespace name.
     */
    private String namespace = null;

    /**
     * The class name prefix.
     */
    private String prefix = null;

    /**
     * The public access flag.
     */
    private boolean publicAccess = false;

    /**
     * Creates a new Visual Basic output element.
     */
    public VisualBasicElement() {
        // Nothing to do here
    }

    /**
     * Sets the output directory.
     *
     * @param dir            the new output directory
     */
    public void setDir(File dir) {
        this.dir = dir;
    }

    /**
     * Sets the output namespace. By default no namespace declaration
     * will be used.
     *
     * @param namespace      the new output namespace
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Sets the output class name prefix. By default the grammar file
     * name will be used.
     *
     * @param prefix         the new output class name prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Sets the public access to types flag. By default only internal
     * access is allowed.
     *
     * @param publicAccess   the public access flag
     */
    public void setPublic(boolean publicAccess) {
        this.publicAccess = publicAccess;
    }

    /**
     * Validates all attributes in the element.
     *
     * @throws RuntimeException if some attribute was missing or had an
     *             invalid value
     */
    public void validate() throws RuntimeException {
        if (dir == null) {
            throw new RuntimeException(
                "missing 'dir' attribute in <visualbasic> element");
        }
    }

    /**
     * Proceses the specified grammar.
     *
     * @param grammar        the grammar to process
     *
     * @throws RuntimeException if the grammar couldn't be processed
     *             correctly
     */
    public void process(Grammar grammar) throws RuntimeException {
        VisualBasicParserGenerator gen;

        gen = new VisualBasicParserGenerator(grammar);
        gen.setBaseDir(dir);
        if (namespace != null) {
            gen.setNamespace(namespace);
        }
        if (prefix != null) {
            gen.setBaseName(prefix);
        }
        gen.setPublicAccess(publicAccess);
        try {
            System.out.println("Writing Visual Basic parser source code...");
            gen.write();
            System.out.println("Done.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
