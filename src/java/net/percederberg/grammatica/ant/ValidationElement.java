/*
 * ValidationElement.java
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

import org.apache.tools.ant.BuildException;

import net.percederberg.grammatica.Grammar;

/**
 * A grammar validation element. This element validates or tests the
 * grammar in various ways.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.4
 * @since    1.4
 */
public class ValidationElement implements ProcessingElement {
    
    /**
     * The validation type.
     */
    private String type = null;
    
    /**
     * The input test file.
     */
    private File file = null;

    /**
     * Creates a new validation element.
     */
    public ValidationElement() {
    }

    /**
     * Sets the validation type. The type must be one of "debug", 
     * "tokenize", "parse", or "profile".
     * 
     * @param type           the validation type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Sets the input test file. The test file is not needed for the
     * debug validation type.
     * 
     * @param file           the input test file
     */
    public void setInputfile(File file) {
        this.file = file;
    }

    /**
     * Validates all attributes in the element.
     * 
     * @throws BuildException if some attribute was missing or had an
     *             invalid value
     */
    public void validate() throws BuildException {
        if (type == null) {
            throw new BuildException(
                "missing 'type' attribute in <validate>");
        }
        if (!type.equals("debug")
         && !type.equals("tokenize")
         && !type.equals("parse")
         && !type.equals("profile")) {

            throw new BuildException(
                "value of 'type' attribute in <validate> must be one " +
                "of 'debug', 'tokenize', 'parse', or 'profile'");
        }
        if (file == null && !type.equals("debug")) {
            throw new BuildException(
                "missing 'inputfile' attribute in <validate>");
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
        // TODO: implement this
        throw new BuildException("<validation> not implemented yet");
    }
}
