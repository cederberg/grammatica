/*
 * CSharpElement.java
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
import java.io.IOException;

import org.apache.tools.ant.BuildException;

import net.percederberg.grammatica.Grammar;
import net.percederberg.grammatica.output.CSharpParserGenerator;

/**
 * A C# output element. This element creates C# source code for a
 * parser.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.4
 * @since    1.4
 */
public class CSharpElement implements ProcessingElement {

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
     * Creates a new C# output element. 
     */
    public CSharpElement() {
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
     * @throws BuildException if some attribute was missing or had an
     *             invalid value
     */
    public void validate() throws BuildException {
        if (dir == null) {
            throw new BuildException(
                "missing 'dir' attribute in <csharp> element");
        }
    }
    
    /**
     * Proceses the specified grammar.
     * 
     * @param grammar        the grammar to process
     * 
     * @throws BuildException if the grammar couldn't be processed 
     *             correctly
     */
    public void process(Grammar grammar) throws BuildException {
        CSharpParserGenerator gen = new CSharpParserGenerator(grammar);

        gen.setBaseDir(dir);
        if (namespace != null) {
            gen.setNamespace(namespace);
        }
        if (prefix != null) {
            gen.setBaseName(prefix);
        }
        gen.setPublicAccess(publicAccess);
        try {
            System.out.println("Writing C# parser source code...");
            gen.write();
            System.out.println("Done.");
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }
}
