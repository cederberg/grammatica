/*
 * CSharpConstantsFile.java
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

package net.percederberg.grammatica.output;

import java.io.IOException;
import java.util.HashMap;

import net.percederberg.grammatica.code.csharp.CSharpComment;
import net.percederberg.grammatica.code.csharp.CSharpEnumeration;
import net.percederberg.grammatica.code.csharp.CSharpFile;
import net.percederberg.grammatica.code.csharp.CSharpNamespace;
import net.percederberg.grammatica.parser.ProductionPattern;
import net.percederberg.grammatica.parser.TokenPattern;

/**
 * The C# constants file generator. This class encapsulates all the
 * C# code necessary for creating a constants enumeration file.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
class CSharpConstantsFile {

    /**
     * The enumeration comment.
     */
    private static final String TYPE_COMMENT =
        "<remarks>An enumeration with token and production node\n" +
        "constants.</remarks>";

    /**
     * The parser generator.
     */
    private CSharpParserGenerator gen;

    /**
     * The file to write.
     */
    private CSharpFile file;

    /**
     * The enumeration declaration.
     */
    private CSharpEnumeration enm;

    /**
     * The mapping from id to constant name. This map contains all 
     * tokens and productions added to the file. 
     */
    private HashMap constantNames = new HashMap();

    /**
     * Creates a new constants file.
     * 
     * @param gen            the parser generator to use
     */
    public CSharpConstantsFile(CSharpParserGenerator gen) {
        String  name = gen.getBaseName() + "Constants";
        int     modifiers;

        this.gen = gen;
        this.file = new CSharpFile(gen.getBaseDir(), name);
        if (gen.getPublicAccess()) {
            modifiers = CSharpEnumeration.PUBLIC;
        } else {
            modifiers = CSharpEnumeration.INTERNAL;
        }
        this.enm = new CSharpEnumeration(modifiers, name);
        initializeCode();
    }
    
    /**
     * Initializes the source code objects.
     */    
    private void initializeCode() {
        String  str;

        // Add namespace
        if (gen.getNamespace() == null) {
            file.addEnumeration(enm);
        } else {
            CSharpNamespace n = new CSharpNamespace(gen.getNamespace());
            n.addEnumeration(enm);
            file.addNamespace(n);
        }

        // Add file comment
        str = file.toString() + "\n\n" + gen.getFileComment();
        file.addComment(new CSharpComment(CSharpComment.BLOCK, str));

        // Add type comment
        enm.addComment(new CSharpComment(TYPE_COMMENT));
    }

    /**
     * Adds a token constant definition to this file.
     * 
     * @param pattern        the token pattern
     */
    public void addToken(TokenPattern pattern) {
        String  constant;

        constant = gen.getCodeStyle().getUpperCase(pattern.getName());
        enm.addConstant(constant, String.valueOf(pattern.getId()));
        constantNames.put(new Integer(pattern.getId()), constant);
    }

    /**
     * Adds a production constant definition to this file. This method
     * checks if the production pattern has already been added.
     * 
     * @param pattern        the production pattern
     */
    public void addProduction(ProductionPattern pattern) {
        String  constant;

        if (!pattern.isSyntetic()) { 
            constant = gen.getCodeStyle().getUpperCase(pattern.getName());
            enm.addConstant(constant, String.valueOf(pattern.getId()));
            constantNames.put(new Integer(pattern.getId()), constant);
        }
    }

    /**
     * Creates source code for accessing one of the constants in this
     * file.
     * 
     * @param id             the node type (pattern) id
     * 
     * @return the constant name, or
     *         null if not found
     */
    public String getConstant(int id) {
        String  name = (String) constantNames.get(new Integer(id));
        
        if (name == null) {
            return null;
        } else {
            return enm.toString() + "." + name;
        }
    }

    /**
     * Writes the file source code.
     * 
     * @throws IOException if the output file couldn't be created 
     *             correctly 
     */
    public void writeCode() throws IOException {
        file.writeCode(gen.getCodeStyle());
    }
}
