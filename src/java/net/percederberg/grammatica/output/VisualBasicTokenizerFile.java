/*
 * VisualBasicTokenizerFile.java
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
 * Copyright (c) 2004 Adrian Moore. All rights reserved.
 * Copyright (c) 2004-2009 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.output;

import java.io.IOException;

import net.percederberg.grammatica.code.visualbasic.VisualBasicClass;
import net.percederberg.grammatica.code.visualbasic.VisualBasicComment;
import net.percederberg.grammatica.code.visualbasic.VisualBasicConstructor;
import net.percederberg.grammatica.code.visualbasic.VisualBasicFile;
import net.percederberg.grammatica.code.visualbasic.VisualBasicImports;
import net.percederberg.grammatica.code.visualbasic.VisualBasicNamespace;
import net.percederberg.grammatica.code.visualbasic.VisualBasicMethod;
import net.percederberg.grammatica.parser.TokenPattern;

/**
 * The Visual Basic tokenizer file generator. This class encapsulates
 * all the Visual Basic (.NET) code necessary for creating a tokenizer.
 *
 * @author   Adrian Moore, <adrianrob at hotmail dot com>
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 * @since    1.5
 */
class VisualBasicTokenizerFile {

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
    private VisualBasicParserGenerator gen;

    /**
     * The file to write.
     */
    private VisualBasicFile file;

    /**
     * The class to write.
     */
    private VisualBasicClass cls;

    /**
     * The class initializer method.
     */
    private VisualBasicMethod initMethod;

    /**
     * Creates a new tokenizer file.
     *
     * @param gen            the parser generator to use
     */
    public VisualBasicTokenizerFile(VisualBasicParserGenerator gen) {
        String  name = gen.getBaseName() + "Tokenizer";
        int     modifiers;

        this.gen = gen;
        this.file = new VisualBasicFile(gen.getBaseDir(), name);
        if (gen.getPublicAccess()) {
            modifiers = VisualBasicClass.PUBLIC;
        } else {
            modifiers = VisualBasicClass.FRIEND;
        }
        this.cls = new VisualBasicClass(modifiers, name, "Tokenizer");
        this.initMethod = new VisualBasicMethod(VisualBasicMethod.PRIVATE,
                                                "CreatePatterns",
                                                "",
                                                "");
        initializeCode();
    }

    /**
     * Initializes the source code objects.
     */
    private void initializeCode() {
        VisualBasicConstructor  constr;
        VisualBasicNamespace    n;
        String                  str;

        // Add using
        file.addImports(new VisualBasicImports("System.IO"));
        file.addImports(new VisualBasicImports("PerCederberg.Grammatica.Runtime"));

        // Add namespace
        if (gen.getNamespace() == null) {
            file.addClass(cls);
        } else {
            n = new VisualBasicNamespace(gen.getNamespace());
            n.addClass(cls);
            file.addNamespace(n);
        }

        // Add file comment
        str = file.toString() + "\n\n" + gen.getFileComment();
        file.addComment(new VisualBasicComment(VisualBasicComment.SINGLELINE,
                                               str));

        // Add type comment
        cls.addComment(new VisualBasicComment(TYPE_COMMENT));

        // Add constructor
        constr = new VisualBasicConstructor("ByVal input As TextReader");
        cls.addConstructor(constr);
        constr.addComment(new VisualBasicComment(CONSTRUCTOR_COMMENT));
        constr.addCode("MyBase.New(input, " +
                       getBoolean(!gen.getGrammar().getCaseSensitive()) +
                       ")");
        constr.addCode("CreatePatterns()");

        // Add init method
        cls.addMethod(initMethod);
        initMethod.addComment(new VisualBasicComment(INIT_METHOD_COMMENT));
        initMethod.addCode("Dim pattern as TokenPattern");
    }

    /**
     * Adds a token pattern definition to this file.
     *
     * @param pattern        the token pattern
     * @param constants      the constants file generator
     */
    public void addToken(TokenPattern pattern,
                         VisualBasicConstantsFile constants) {

        StringBuffer  code = new StringBuffer();
        String        str;

        // Create new pattern
        code.append("pattern = New TokenPattern(CInt(");
        code.append(constants.getConstant(pattern.getId()));
        code.append("), \"");
        code.append(pattern.getName());
        code.append("\", TokenPattern.PatternType.");
        switch (pattern.getType()) {
        case TokenPattern.STRING_TYPE:
            code.append("STRING");
            break;
        case TokenPattern.REGEXP_TYPE:
            code.append("REGEXP");
            break;
        }
        code.append(", ");
        str = pattern.getPattern();
        code.append(gen.getCodeStyle().getStringConstant(str, '"'));
        code.append(")\n");

        // Add error and ignore messages
        if (pattern.isError()) {
            if (pattern.getErrorMessage() == null) {
                code.append("pattern.Error = True");
            } else {
                code.append("pattern.ErrorMessage = ");
                str = pattern.getErrorMessage();
                code.append(gen.getCodeStyle().getStringConstant(str, '"'));
            }
            code.append("\n");
        }
        if (pattern.isIgnore()) {
            if (pattern.getIgnoreMessage() == null) {
                code.append("pattern.Ignore = True");
            } else {
                code.append("pattern.IgnoreMessage = ");
                str = pattern.getIgnoreMessage();
                code.append(gen.getCodeStyle().getStringConstant(str, '"'));
            }
            code.append("\n");
        }

        // Add pattern to tokenizer
        code.append("AddPattern(pattern)");
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
     * Returns the source code representation of a boolean value.
     *
     * @param value           the boolean value
     *
     * @return the source code for the boolean constant
     */
    private String getBoolean(boolean value) {
        return value ? "True" : "False";
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
