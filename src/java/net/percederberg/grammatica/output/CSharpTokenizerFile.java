/*
 * CSharpTokenizerFile.java
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
 * Copyright (c) 2003-2009 Per Cederberg. All rights reserved.
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
 * @version  1.6
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
     * The newToken method comment.
     */
    private static final String NT_COMMENT =
        "<summary>Factory method to create a new token node. This\n" +
        "method has been overridden to return the correct node class,\n" +
        "since the '--specialize' flag was used to generate this code.</summary>\n\n" +
        "@param pattern        the token pattern\n" +
        "@param image          the token image (i.e. characters)\n" +
        "@param line           the line number of the first character\n" +
        "@param column         the column number of the first character\n" +
        "@return the token created";

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
     * The newToken method.
     */
    private CSharpMethod newToken;

    /**
     * The NodeClassesDir, for getting specialized node information iff the
     * '--specialize' flag is set.
     */
    private CSharpNodeClassesDir dir;

    /**
     * Creates a new tokenizer file.  Note: DO NOT use this constructor if this
     * is a specialized node; instead, use
     * {@link #CSharpTokenizerFile(CSharpParserGenerator, CSharpNodeClassesDir)}.
     *
     * @param gen            the parser generator to use
     */
    public CSharpTokenizerFile(CSharpParserGenerator gen) {
        this (gen, null);
    }

    /**
     * Creates a new tokenizer file for C# output.  Use this constructor only
     * if the '--specialize' flag is set.
     *
     * @param gen            the parser generator to use
     * @param dir            the NodeClassesDir object to use to extract
     *                       production information
     */
    public CSharpTokenizerFile(CSharpParserGenerator gen, CSharpNodeClassesDir dir) {
        String  name = gen.getBaseName() + "Tokenizer";
        int     modifiers;

        this.dir = dir;
        this.gen = gen;
        this.file = new CSharpFile(gen.getBaseDir(), name);
        if (gen.getPublicAccess()) {
            modifiers = CSharpClass.PUBLIC;
        } else {
            modifiers = CSharpClass.INTERNAL;
        }
        this.cls = new CSharpClass(modifiers, name, "Tokenizer");
        if (gen.specialize() && (dir != null)) {
            this.newToken = new CSharpMethod(CSharpMethod.PROTECTED + CSharpMethod.OVERRIDE,
                                           "NewToken",
                                           "TokenPattern pattern, string image, " +
                                               "int line, int column",
                                           "Token");
        }
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
        file.addUsing(new CSharpUsing("PerCederberg.Grammatica.Runtime"));

        // Add namespace
        if (gen.getNamespace() == null) {
            file.addClass(cls);
        } else {
            CSharpNamespace n = new CSharpNamespace(gen.getNamespace());
            n.addClass(cls);
            file.addNamespace(n);
            if (gen.specialize() && (dir != null)) {
                file.addUsing(new CSharpUsing(gen.getNamespace() + ".Nodes"));
            }
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
        constr.addInitializer("base(input, " +
                              !gen.getGrammar().getCaseSensitive() +
                              ")");
        constr.addCode("CreatePatterns();");

        // Add the newToken method
        if (gen.specialize() && (dir != null)) {
            newToken.addComment(new CSharpComment(NT_COMMENT));
            newToken.addCode("switch (pattern.Id) {");
            cls.addMethod(newToken);
        }

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
        String        constant = constants.getConstant(pattern.getId());
        String        str;

        if (gen.specialize() && (dir != null)) {
            addNewTokenCase(constant, pattern);
        }

        // Create new pattern
        code.append("pattern = new TokenPattern((int) ");
        code.append(constant);
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
            if (pattern.getErrorMessage() == null) {
                code.append("pattern.Error = true");
            } else {
                code.append("pattern.ErrorMessage = ");
                str = pattern.getErrorMessage();
                code.append(gen.getCodeStyle().getStringConstant(str, '\\'));
            }
            code.append(";\n");
        }
        if (pattern.isIgnore()) {
            if (pattern.getIgnoreMessage() == null) {
                code.append("pattern.Ignore = true");
            } else {
                code.append("pattern.IgnoreMessage = ");
                str = pattern.getIgnoreMessage();
                code.append(gen.getCodeStyle().getStringConstant(str, '\\'));
            }
            code.append(";\n");
        }

        // Add pattern to tokenizer
        code.append("AddPattern(pattern);");
        initMethod.addCode("");
        initMethod.addCode(code.toString());
    }

    /**
     * Add a newToken method switch case.
     *
     * @param constant       the node constant
     * @param pattern        the node pattern
     */
    public void addNewTokenCase(String constant, TokenPattern pattern) {
        String name = dir.getTokenDescriptors().get(pattern).name;
        newToken.addCode("case (int)" + constant + ":");
        newToken.addCode("    return new " + name + "(pattern, image, line, column);");
    }

    /**
     * Returns the class name for this tokenizer.
     *
     * @return the class name for this tokenizer
     */
    protected String getClassName() {
        return cls.toString();
    }

    /**
     * Writes the file source code.
     *
     * @throws IOException if the output file couldn't be created
     *             correctly
     */
    public void writeCode() throws IOException {
        if (gen.specialize() && (dir != null)) {
            newToken.addCode("}");
            newToken.addCode("return new Token(pattern, image, line, column);");
        }
        file.writeCode(gen.getCodeStyle());
    }
}
