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
import net.percederberg.grammatica.parser.TokenPattern;

/**
 * The Java tokenizer file generator. This class encapsulates all the
 * Java code necessary for creating a tokenizer.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
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
     * Creates a new tokenizer file.
     *
     * @param gen            the parser generator to use
     */
    public JavaTokenizerFile(JavaParserGenerator gen) {
        int  modifiers;

        this.gen = gen;
        this.file = gen.createJavaFile();
        if (gen.getPublicAccess()) {
            modifiers = JavaClass.PUBLIC;
        } else {
            modifiers = JavaClass.PACKAGE_LOCAL;
        }
        this.cls = new JavaClass(modifiers,
                                 gen.getBaseName() + "Tokenizer",
                                 "Tokenizer");
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
        String           str;

        // Add imports
        file.addImport(new JavaImport("java.io", "Reader"));
        file.addImport(new JavaImport("net.percederberg.grammatica.parser",
                                      "ParserCreationException"));
        file.addImport(new JavaImport("net.percederberg.grammatica.parser",
                                      "TokenPattern"));
        file.addImport(new JavaImport("net.percederberg.grammatica.parser",
                                      "Tokenizer"));

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
        String        str;

        // Create new pattern
        code.append("pattern = new TokenPattern(");
        code.append(constants.getConstant(pattern.getId()));
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
        file.writeCode(gen.getCodeStyle());
    }
}
