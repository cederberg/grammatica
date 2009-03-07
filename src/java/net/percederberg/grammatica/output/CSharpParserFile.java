/*
 * CSharpParserFile.java
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
import java.util.HashMap;

import net.percederberg.grammatica.code.csharp.CSharpClass;
import net.percederberg.grammatica.code.csharp.CSharpComment;
import net.percederberg.grammatica.code.csharp.CSharpConstructor;
import net.percederberg.grammatica.code.csharp.CSharpEnumeration;
import net.percederberg.grammatica.code.csharp.CSharpFile;
import net.percederberg.grammatica.code.csharp.CSharpMethod;
import net.percederberg.grammatica.code.csharp.CSharpNamespace;
import net.percederberg.grammatica.code.csharp.CSharpUsing;
import net.percederberg.grammatica.parser.ProductionPattern;
import net.percederberg.grammatica.parser.ProductionPatternAlternative;
import net.percederberg.grammatica.parser.ProductionPatternElement;

/**
 * The C# parser file generator. This class encapsulates all the
 * C# code necessary for creating a parser.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
class CSharpParserFile {

    /**
     * The type comment.
     */
    private static final String TYPE_COMMENT =
        "<remarks>A token stream parser.</remarks>";

    /**
     * The production enumeration comment.
     */
    private static final String ENUM_COMMENT =
        "<summary>An enumeration with the generated production node\n" +
        "identity constants.</summary>";

    /**
     * The first constructor comment.
     */
    private static final String CONSTRUCTOR1_COMMENT =
        "<summary>Creates a new parser with a default analyzer.</summary>\n\n" +
        "<param name='input'>the input stream to read from</param>\n\n" +
        "<exception cref='ParserCreationException'>if the parser\n" +
        "couldn't be initialized correctly</exception>";

    /**
     * The second constructor comment.
     */
    private static final String CONSTRUCTOR2_COMMENT =
        "<summary>Creates a new parser.</summary>\n\n" +
        "<param name='input'>the input stream to read from</param>\n\n" +
        "<param name='analyzer'>the analyzer to parse with</param>\n\n" +
        "<exception cref='ParserCreationException'>if the parser\n" +
        "couldn't be initialized correctly</exception>";

    /**
     * The tokenizer factory method comment.
     */
    private static final String FACTORY_COMMENT =
        "<summary>Creates a new tokenizer for this parser. Can be overridden\n" +
        "by a subclass to provide a custom implementation.</summary>\n\n" +
        "<param name='input'>the input stream to read from</param>\n\n" +
        "<returns>the tokenizer created</returns>\n\n" +
        "<exception cref='ParserCreationException'>if the tokenizer\n" +
        "couldn't be initialized correctly</exception>";

    /**
     * The init method comment.
     */
    private static final String INIT_METHOD_COMMENT =
        "<summary>Initializes the parser by creating all the production\n" +
        "patterns.</summary>\n\n" +
        "<exception cref='ParserCreationException'>if the parser\n" +
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
     * The syntetic contants enumeration.
     */
    private CSharpEnumeration enm;

    /**
     * The class initializer method.
     */
    private CSharpMethod initMethod;

    /**
     * A map with the production constants in this class. This map
     * is indexed with the pattern id and contains the production
     * constant name.
     */
    private HashMap constantNames = new HashMap();

    /**
     * The first available constant id number.
     */
    private int constantId = 1;

    /**
     * Creates a new parser file.
     *
     * @param gen            the parser generator to use
     * @param tokenizer      the tokenizer file generator
     * @param analyzer       the analyzer file generator
     */
    public CSharpParserFile(CSharpParserGenerator gen,
                            CSharpTokenizerFile tokenizer,
                            CSharpAnalyzerFile analyzer) {

        String  name = gen.getBaseName() + "Parser";
        int     modifiers;

        this.gen = gen;
        this.file = new CSharpFile(gen.getBaseDir(), name);
        if (gen.getPublicAccess()) {
            modifiers = CSharpClass.PUBLIC;
        } else {
            modifiers = CSharpClass.INTERNAL;
        }
        this.cls = new CSharpClass(modifiers,
                                   name,
                                   "RecursiveDescentParser");
        this.enm = new CSharpEnumeration(CSharpEnumeration.PRIVATE,
                                          "SynteticPatterns");
        this.initMethod = new CSharpMethod(CSharpMethod.PRIVATE,
                                           "CreatePatterns",
                                           "",
                                           "void");
        initializeCode(tokenizer, analyzer);
    }

    /**
     * Initializes the source code objects.
     *
     * @param tokenizer      the tokenizer file generator
     * @param analyzer       the analyzer file generator
     */
    private void initializeCode(CSharpTokenizerFile tokenizer,
                                CSharpAnalyzerFile analyzer) {
        CSharpConstructor  constr;
        CSharpMethod       method;
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
        }

        // Add file comment
        str = file.toString() + "\n\n" + gen.getFileComment();
        file.addComment(new CSharpComment(CSharpComment.BLOCK, str));

        // Add type comment
        cls.addComment(new CSharpComment(TYPE_COMMENT));

        // Add enumeration
        cls.addEnumeration(enm);
        enm.addComment(new CSharpComment(ENUM_COMMENT));

        // Add constructor
        constr = new CSharpConstructor("TextReader input");
        cls.addConstructor(constr);
        constr.addComment(new CSharpComment(CONSTRUCTOR1_COMMENT));
        constr.addInitializer("base(input)");
        constr.addCode("CreatePatterns();");

        // Add constructor
        constr = new CSharpConstructor("TextReader input, " +
                                       analyzer.getClassName() + " analyzer");
        cls.addConstructor(constr);
        constr.addComment(new CSharpComment(CONSTRUCTOR2_COMMENT));
        constr.addInitializer("base(input, analyzer)");
        constr.addCode("CreatePatterns();");

        // Add tokenizer factory method
        method = new CSharpMethod(CSharpMethod.PROTECTED + CSharpMethod.OVERRIDE,
                                  "NewTokenizer",
                                  "TextReader input",
                                  "Tokenizer");
        method.addComment(new CSharpComment(FACTORY_COMMENT));
        method.addCode("return new " + tokenizer.getClassName() + "(input);");
        cls.addMethod(method);

        // Add init method
        cls.addMethod(initMethod);
        initMethod.addComment(new CSharpComment(INIT_METHOD_COMMENT));
        initMethod.addCode("ProductionPattern             pattern;");
        initMethod.addCode("ProductionPatternAlternative  alt;");
    }

    /**
     * Adds a production constant definition to this file.
     *
     * @param pattern        the production pattern
     */
    public void addProductionConstant(ProductionPattern pattern) {
        String   constant;

        if (pattern.isSynthetic()) {
            constant = "SUBPRODUCTION_" + constantId;
            enm.addConstant(constant, String.valueOf(constantId + 3000));
            constantNames.put(new Integer(pattern.getId()), constant);
            constantId++;
        }
    }

    /**
     * Adds a production pattern definition to this file.
     *
     * @param pattern        the production pattern
     * @param constants      the constants file generator
     */
    public void addProduction(ProductionPattern pattern,
                              CSharpConstantsFile constants) {
        StringBuffer  code;
        String        str;

        // Create new pattern
        code = new StringBuffer();
        code.append("pattern = new ProductionPattern((int) ");
        code.append(getConstant(constants, pattern.getId()));
        code.append(",\n");
        code.append("                                \"");
        if (pattern.isSynthetic()) {
            str = (String) constantNames.get(new Integer(pattern.getId()));
            code.append(gen.getCodeStyle().getMixedCase(str, true));
        } else {
            code.append(pattern.getName());
        }
        code.append("\");");
        initMethod.addCode("");
        initMethod.addCode(code.toString());

        // Set syntetic flag
        if (pattern.isSynthetic()) {
            initMethod.addCode("pattern.Synthetic = true;");
        }

        // Create pattern rules
        for (int i = 0; i < pattern.getAlternativeCount(); i++) {
            addProductionAlternative(pattern.getAlternative(i),
                                     constants);
        }

        // Add pattern to parser
        initMethod.addCode("AddPattern(pattern);");
    }

    /**
     * Adds a production pattern alternative definition to the init
     * method.
     *
     * @param alt            the production pattern alternative
     * @param constants      the constants file generator
     */
    private void addProductionAlternative(ProductionPatternAlternative alt,
                                          CSharpConstantsFile constants) {

        ProductionPatternElement  elem;
        StringBuffer              code;

        initMethod.addCode("alt = new ProductionPatternAlternative();");
        for (int i = 0; i < alt.getElementCount(); i++) {
            elem = alt.getElement(i);
            code = new StringBuffer();
            code.append("alt.");
            if (elem.isToken()) {
                code.append("AddToken(");
            } else {
                code.append("AddProduction(");
            }
            code.append("(int) ");
            code.append(getConstant(constants, elem.getId()));
            code.append(", ");
            code.append(elem.getMinCount());
            code.append(", ");
            if (elem.getMaxCount() == Integer.MAX_VALUE) {
                code.append("-1");
            } else {
                code.append(elem.getMaxCount());
            }
            code.append(");");
            initMethod.addCode(code.toString());
        }
        initMethod.addCode("pattern.AddAlternative(alt);");
    }

    /**
     * Returns the constant name for a specified pattern or token id.
     *
     * @param constants      the constants file
     * @param id             the pattern id
     *
     * @return the constant name to use
     */
    private String getConstant(CSharpConstantsFile constants, int id) {
        Integer  value = new Integer(id);

        if (constantNames.containsKey(value)) {
            return "SynteticPatterns." + constantNames.get(value);
        } else {
            return constants.getConstant(id);
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
