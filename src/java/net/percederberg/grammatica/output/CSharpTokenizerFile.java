/*
 * CSharpTokenizerFile.java
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
 * Copyright (c) 2003-2004 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.output;

import java.io.IOException;

import net.percederberg.grammatica.code.csharp.CSharpClass;
import net.percederberg.grammatica.code.csharp.CSharpComment;
import net.percederberg.grammatica.code.csharp.CSharpConstructor;
import net.percederberg.grammatica.code.csharp.CSharpFile;
import net.percederberg.grammatica.code.csharp.CSharpNamespace;
import net.percederberg.grammatica.code.csharp.CSharpMethod;
import net.percederberg.grammatica.code.csharp.CSharpUsing;
import net.percederberg.grammatica.parser.TokenPattern;

/**
 * The C# tokenizer file generator. This class encapsulates all the
 * C# code necessary for creating a tokenizer.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
class CSharpTokenizerFile {

    /**
     * The tokenizer type comment.
     */
    private static final String TYPE_COMMENT =
        "<remarks>A character stream tokenizer.</remarks>";

    /**
     * The tokenizer constructor comment.
     */
    private static final String CONSTRUCTOR_COMMENT =
        "<summary>Creates a new tokenizer for the specified input\n" +
        "stream.</summary>\n\n" +
        "<param name='input'>the input stream to read</param>\n\n" +
        "<exception cref='ParserCreationException'>if the tokenizer\n" +
        "couldn't be initialized correctly</exception>";

    /**
     * The init method comment.
     */
    private static final String INIT_METHOD_COMMENT =
        "<summary>Initializes the tokenizer by creating all the token\n" +
        "patterns.</summary>\n\n" +
        "<exception cref='ParserCreationException'>if the tokenizer\n" +
        "couldn't be initialized correctly</exception>";

    /**
     * The parser generator.
     */
    private CSharpParserGenerator gen;

    /**
     * The file to write.
     */
    private CSharpFile file;
    
    /**
     * The class to write.
     */
    private CSharpClass cls;
    
    /**
     * The class initializer method.
     */
    private CSharpMethod initMethod;
    
    /**
     * Creates a new tokenizer file.
     * 
     * @param gen            the parser generator to use
     */
    public CSharpTokenizerFile(CSharpParserGenerator gen) {
        String  name = gen.getBaseName() + "Tokenizer";
        int     modifiers;

        this.gen = gen;
        this.file = new CSharpFile(gen.getBaseDir(), name);
        if (gen.getPublicAccess()) {
            modifiers = CSharpClass.PUBLIC;
        } else {
            modifiers = CSharpClass.INTERNAL;
        }
        this.cls = new CSharpClass(modifiers, name, "Tokenizer");
        this.initMethod = new CSharpMethod(CSharpMethod.PRIVATE,
                                           "CreatePatterns",
                                           "",
                                           "void");
        initializeCode();
    }

    /**
     * Initializes the source code objects.
     */    
    private void initializeCode() {
        CSharpConstructor  constr;
        String             str;

        // Add using
        file.addUsing(new CSharpUsing("System.IO"));
        file.addUsing(new CSharpUsing("PerCederberg.Grammatica.Parser"));

        // Add namespace
        if (gen.getNamespace() == null) {
            file.addClass(cls);
        } else {
            CSharpNamespace n = new CSharpNamespace(gen.getNamespace());
            n.addClass(cls);
            file.addNamespace(n);
        }

        // Add file comment
        str = file.toString() + "\n\n" + gen.getFileComment();
        file.addComment(new CSharpComment(CSharpComment.BLOCK, str));

        // Add type comment
        cls.addComment(new CSharpComment(TYPE_COMMENT));

        // Add constructor
        constr = new CSharpConstructor("TextReader input");
        cls.addConstructor(constr);
        constr.addComment(new CSharpComment(CONSTRUCTOR_COMMENT));
        constr.addInitializer("base(input)");
        constr.addCode("CreatePatterns();");
        
        // Add init method
        cls.addMethod(initMethod);
        initMethod.addComment(new CSharpComment(INIT_METHOD_COMMENT));
        initMethod.addCode("TokenPattern  pattern;");
    }
    
    /**
     * Adds a token pattern definition to this file.
     * 
     * @param pattern        the token pattern
     * @param constants      the constants file generator
     */
    public void addToken(TokenPattern pattern, 
                         CSharpConstantsFile constants) {

        StringBuffer  code = new StringBuffer();
        String        str;
        
        // Create new pattern
        code.append("pattern = new TokenPattern((int) ");
        code.append(constants.getConstant(pattern.getId()));
        code.append(",\n");
        code.append("                           \"");
        code.append(pattern.getName());
        code.append("\",\n"); 
        code.append("                           TokenPattern.PatternType.");
        switch (pattern.getType()) {
        case TokenPattern.STRING_TYPE:
            code.append("STRING");
            break;
        case TokenPattern.REGEXP_TYPE:
            code.append("REGEXP");
            break;
        }
        code.append(",\n"); 
        code.append("                           ");
        str = pattern.getPattern();
        code.append(gen.getCodeStyle().getStringConstant(str, '\\'));
        code.append(");\n");

        // Add error and ignore messages
        if (pattern.isError()) {
            code.append("pattern.SetError(");
            if (pattern.getErrorMessage() != null) {
                str = pattern.getErrorMessage();
                code.append(gen.getCodeStyle().getStringConstant(str, '\\'));
            }
            code.append(");\n");            
        }
        if (pattern.isIgnore()) {
            code.append("pattern.SetIgnore(");
            if (pattern.getIgnoreMessage() != null) {
                str = pattern.getIgnoreMessage();
                code.append(gen.getCodeStyle().getStringConstant(str, '\\'));
            }
            code.append(");\n");            
        }

        // Add pattern to tokenizer
        code.append("AddPattern(pattern);");
        initMethod.addCode("");
        initMethod.addCode(code.toString());
    }

    /**
     * Creates source code performing a call to the constructor for
     * the tokenizer.
     * 
     * @param param          the input parameters
     * 
     * @return the source code for the call
     */
    protected String getConstructorCall(String param) {
        return "new " + gen.getBaseName() + "Tokenizer(" + param + ")";
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
