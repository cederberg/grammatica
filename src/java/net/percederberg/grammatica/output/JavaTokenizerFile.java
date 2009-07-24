/*
 * JavaTokenizerFile.java
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

import net.percederberg.grammatica.code.java.JavaClass;
import net.percederberg.grammatica.code.java.JavaComment;
import net.percederberg.grammatica.code.java.JavaConstructor;
import net.percederberg.grammatica.code.java.JavaFile;
import net.percederberg.grammatica.code.java.JavaImport;
import net.percederberg.grammatica.code.java.JavaMethod;
import net.percederberg.grammatica.code.java.JavaPackage;
import net.percederberg.grammatica.parser.TokenPattern;

/**
 * The Java tokenizer file generator. This class encapsulates all the
 * Java code necessary for creating a tokenizer.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.6
 */
class JavaTokenizerFile {

    /**
     * The tokenizer class comment.
     */
    private static final String CLASS_COMMENT =
        "A character stream tokenizer.";

    /**
     * The tokenizer constructor comment.
     */
    private static final String CONSTRUCTOR_COMMENT =
        "Creates a new tokenizer for the specified input stream.\n\n" +
        "@param input          the input stream to read\n\n" +
        "@throws ParserCreationException if the tokenizer couldn't be\n" +
        "            initialized correctly";

    /**
     * The newToken method comment.
     */
    private static final String NT_COMMENT =
        "Factory method to create a new token node. This method has\n" +
        "been overridden to return the correct node class, since the\n" +
        "'--specialize' flag was used to generate this code.\n\n" +
        "@param pattern        the token pattern\n" +
        "@param image          the token image (i.e. characters)\n" +
        "@param line           the line number of the first character\n" +
        "@param column         the column number of the first character\n" +
        "@return the token created";

    /**
     * The init method comment.
     */
    private static final String INIT_METHOD_COMMENT =
        "Initializes the tokenizer by creating all the token patterns.\n\n" +
        "@throws ParserCreationException if the tokenizer couldn't be\n" +
        "            initialized correctly";

    /**
     * The Java parser generator.
     */
    private JavaParserGenerator gen;

    /**
     * The Java file to write.
     */
    private JavaFile file;

    /**
     * The Java class to write.
     */
    private JavaClass cls;

    /**
     * The Java class initializer method.
     */
    private JavaMethod initMethod;

    /**
     * The Java newToken method.
     */
    private JavaMethod newToken;

    /**
     * The JavaNodeClassesDir, for getting specialized node information iff the
     * '--specialize' flag is set.
     */
    private JavaNodeClassesDir dir;

    /**
     * Creates a new tokenizer file.  Note: DO NOT use this constructor if this
     * is a specialized node; instead, use
     * {@link #JavaTokenizerFile(JavaParserGenerator, JavaNodeClassesDir)}.
     *
     * @param gen            the parser generator to use
     */
    public JavaTokenizerFile(JavaParserGenerator gen) {
        this (gen, null);
    }

    /**
     * Creates a new tokenizer file for java output.  Use this constructor only
     * if the '--specialize' flag is set.
     *
     * @param gen            the parser generator to use
     * @param dir            the NodeClassesDir object to use to extract
     *                       production information
     */
    public JavaTokenizerFile(JavaParserGenerator gen, JavaNodeClassesDir dir) {
        String  name = gen.getBaseName() + "Tokenizer";
        int  modifiers;

        this.dir = dir;
        this.gen = gen;
        this.file = new JavaFile(gen.getBaseDir(), name);
        if (gen.getPublicAccess()) {
            modifiers = JavaClass.PUBLIC;
        } else {
            modifiers = JavaClass.PACKAGE_LOCAL;
        }
        this.cls = new JavaClass(modifiers,
                                 gen.getBaseName() + "Tokenizer",
                                 "Tokenizer");
        if (gen.specialize() && (dir != null)) {
            this.newToken = new JavaMethod(JavaMethod.PROTECTED,
                                           "newToken",
                                           "TokenPattern pattern, String image, " +
                                               "int line, int column",
                                           "Token");
        }
        this.initMethod = new JavaMethod(JavaMethod.PRIVATE,
                                         "createPatterns",
                                         "",
                                         "void");
        initializeCode();
    }

    /**
     * Initializes the source code objects.
     */
    private void initializeCode() {
        JavaConstructor  constr;
        String  str;

        // Add imports
        file.addImport(new JavaImport("java.io", "Reader"));
        file.addImport(new JavaImport("net.percederberg.grammatica.parser",
                                      "ParserCreationException"));
        if (gen.specialize() && (dir != null)) {
            file.addImport(new JavaImport("net.percederberg.grammatica.parser",
                                          "Token"));
        }
        file.addImport(new JavaImport("net.percederberg.grammatica.parser",
                                      "TokenPattern"));
        file.addImport(new JavaImport("net.percederberg.grammatica.parser",
                                      "Tokenizer"));

        // Add package
        if (gen.getBasePackage() != null) {
            JavaPackage p = new JavaPackage(gen.getBasePackage());
            file.addPackage(p);
            if (gen.specialize() && (dir != null)) {
                file.addImport(new JavaImport(gen.getBasePackage() + ".nodes"));
            }
        }

        // Add class
        file.addClass(cls);
        str = CLASS_COMMENT;
        if (gen.getClassComment() != null) {
            str += "\n\n" + gen.getClassComment();
        }
        cls.addComment(new JavaComment(str));

        // Add file comment
        str = file.toString() + "\n\n" + gen.getFileComment();
        file.addComment(new JavaComment(JavaComment.BLOCK, str));

        // Add constructor
        constr = new JavaConstructor("Reader input");
        cls.addConstructor(constr);
        constr.addComment(new JavaComment(CONSTRUCTOR_COMMENT));
        constr.addThrows("ParserCreationException");
        constr.addCode("super(input, " +
                       !gen.getGrammar().getCaseSensitive() +
                       ");");
        constr.addCode("createPatterns();");

        // Add the newToken method
        if (gen.specialize() && (dir != null)) {
            newToken.addComment(new JavaComment(NT_COMMENT));
            newToken.addCode("switch (pattern.getId()) {");
            cls.addMethod(newToken);
        }

        // Add init method
        cls.addMethod(initMethod);
        initMethod.addComment(new JavaComment(INIT_METHOD_COMMENT));
        initMethod.addThrows("ParserCreationException");
        initMethod.addCode("TokenPattern  pattern;");
    }

    /**
     * Adds a token pattern definition to this file.
     *
     * @param pattern        the token pattern
     * @param constants      the constants file generator
     */
    public void addToken(TokenPattern pattern, JavaConstantsFile constants) {
        StringBuffer  code = new StringBuffer();
        String        constant = constants.getConstant(pattern.getId());
        String        str;

        if (gen.specialize() && (dir != null)) {
            addNewTokenCase(constant, pattern);
        }

        // Create new pattern
        code.append("pattern = new TokenPattern(");
        code.append(constant);
        code.append(",\n");
        code.append("                           \"");
        code.append(pattern.getName());
        code.append("\",\n");
        code.append("                           TokenPattern.");
        switch (pattern.getType()) {
        case TokenPattern.STRING_TYPE:
            code.append("STRING_TYPE");
            break;
        case TokenPattern.REGEXP_TYPE:
            code.append("REGEXP_TYPE");
            break;
        }
        code.append(",\n");
        code.append("                           ");
        str = pattern.getPattern();
        code.append(gen.getCodeStyle().getStringConstant(str, '\\'));
        code.append(");\n");

        // Add error and ignore messages
        if (pattern.isError()) {
            code.append("pattern.setError(");
            if (pattern.getErrorMessage() != null) {
                str = pattern.getErrorMessage();
                code.append(gen.getCodeStyle().getStringConstant(str, '\\'));
            }
            code.append(");\n");
        }
        if (pattern.isIgnore()) {
            code.append("pattern.setIgnore(");
            if (pattern.getIgnoreMessage() != null) {
                str = pattern.getIgnoreMessage();
                code.append(gen.getCodeStyle().getStringConstant(str, '\\'));
            }
            code.append(");\n");
        }

        // Add pattern to tokenizer
        code.append("addPattern(pattern);");
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
        newToken.addCode("case " + constant + ":");
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
