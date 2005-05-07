/*
 * VisualBasicParserFile.java
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
 * Copyright (c) 2004 Adrian Moore. All rights reserved.
 * Copyright (c) 2004-2005 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.output;

import java.io.IOException;
import java.util.HashMap;

import net.percederberg.grammatica.code.visualbasic.VisualBasicClass;
import net.percederberg.grammatica.code.visualbasic.VisualBasicComment;
import net.percederberg.grammatica.code.visualbasic.VisualBasicConstructor;
import net.percederberg.grammatica.code.visualbasic.VisualBasicEnumeration;
import net.percederberg.grammatica.code.visualbasic.VisualBasicFile;
import net.percederberg.grammatica.code.visualbasic.VisualBasicImports;
import net.percederberg.grammatica.code.visualbasic.VisualBasicMethod;
import net.percederberg.grammatica.code.visualbasic.VisualBasicNamespace;
import net.percederberg.grammatica.parser.ProductionPattern;
import net.percederberg.grammatica.parser.ProductionPatternAlternative;
import net.percederberg.grammatica.parser.ProductionPatternElement;

/**
 * The Visual Basic parser file generator. This class encapsulates all
 * the Visual Basic (.NET) code necessary for creating a parser.
 *
 * @author   Adrian Moore, <adrianrob at hotmail dot com>
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 * @since    1.5
 */
class VisualBasicParserFile {

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
        "<summary>Creates a new parser.</summary>\n\n" +
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
    private VisualBasicParserGenerator gen;

    /**
     * The tokenizer file generator.
     */
    private VisualBasicTokenizerFile tokenizer;

    /**
     * The file to write.
     */
    private VisualBasicFile file;

    /**
     * The class to write.
     */
    private VisualBasicClass cls;

    /**
     * The syntetic contants enumeration.
     */
    private VisualBasicEnumeration enm;

    /**
     * The class initializer method.
     */
    private VisualBasicMethod initMethod;

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
     */
    public VisualBasicParserFile(VisualBasicParserGenerator gen,
                                 VisualBasicTokenizerFile tokenizer) {

        String  name = gen.getBaseName() + "Parser";
        int     modifiers;

        this.gen = gen;
        this.tokenizer = tokenizer;
        this.file = new VisualBasicFile(gen.getBaseDir(), name);
        if (gen.getPublicAccess()) {
            modifiers = VisualBasicClass.PUBLIC;
        } else {
            modifiers = VisualBasicClass.FRIEND;
        }
        this.cls = new VisualBasicClass(modifiers,
                                        name,
                                        "RecursiveDescentParser");
        this.enm = new VisualBasicEnumeration(VisualBasicEnumeration.PRIVATE,
                                              "SynteticPatterns");
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
        file.addImports(new VisualBasicImports("PerCederberg.Grammatica.Parser"));

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

        // Add enumeration
        cls.addEnumeration(enm);
        enm.addComment(new VisualBasicComment(ENUM_COMMENT));

        // Add constructor
        constr = new VisualBasicConstructor("ByVal input As TextReader");
        cls.addConstructor(constr);
        constr.addComment(new VisualBasicComment(CONSTRUCTOR1_COMMENT));
        constr.addCode("MyBase.New(" + tokenizer.getConstructorCall("input") +
                       ")");
        constr.addCode("CreatePatterns()");

        // Add constructor
        constr = new VisualBasicConstructor("ByVal input As TextReader, " +
                                            "ByVal analyzer As Analyzer");
        cls.addConstructor(constr);
        constr.addComment(new VisualBasicComment(CONSTRUCTOR2_COMMENT));
        constr.addCode("MyBase.New(" + tokenizer.getConstructorCall("input") +
                       ", analyzer)");
        constr.addCode("CreatePatterns()");

        // Add init method
        cls.addMethod(initMethod);
        initMethod.addComment(new VisualBasicComment(INIT_METHOD_COMMENT));
        initMethod.addCode("Dim pattern As ProductionPattern");
        initMethod.addCode("Dim alt As ProductionPatternAlternative");
    }

    /**
     * Adds a production constant definition to this file.
     *
     * @param pattern        the production pattern
     */
    public void addProductionConstant(ProductionPattern pattern) {
        String   constant;

        if (pattern.isSyntetic()) {
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
                              VisualBasicConstantsFile constants) {
        StringBuffer  code;
        String        str;

        // Create new pattern
        code = new StringBuffer();
        code.append("pattern = New ProductionPattern(CInt(");
        code.append(getConstant(constants, pattern.getId()));
        code.append("), \"");
        if (pattern.isSyntetic()) {
            str = (String) constantNames.get(new Integer(pattern.getId()));
            code.append(gen.getCodeStyle().getMixedCase(str, true));
        } else {
            code.append(pattern.getName());
        }
        code.append("\")");
        initMethod.addCode("");
        initMethod.addCode(code.toString());

        // Set syntetic flag
        if (pattern.isSyntetic()) {
            initMethod.addCode("pattern.Syntetic = True");
        }

        // Create pattern rules
        for (int i = 0; i < pattern.getAlternativeCount(); i++) {
            addProductionAlternative(pattern.getAlternative(i),
                                     constants);
        }

        // Add pattern to parser
        initMethod.addCode("AddPattern(pattern)");
    }

    /**
     * Adds a production pattern alternative definition to the init
     * method.
     *
     * @param alt            the production pattern alternative
     * @param constants      the constants file generator
     */
    private void addProductionAlternative(ProductionPatternAlternative alt,
                                          VisualBasicConstantsFile constants) {

        ProductionPatternElement  elem;
        StringBuffer              code;

        initMethod.addCode("alt = New ProductionPatternAlternative()");
        for (int i = 0; i < alt.getElementCount(); i++) {
            elem = alt.getElement(i);
            code = new StringBuffer();
            code.append("alt.");
            if (elem.isToken()) {
                code.append("AddToken(");
            } else {
                code.append("AddProduction(");
            }
            code.append("CInt(");
            code.append(getConstant(constants, elem.getId()));
            code.append("), ");
            code.append(elem.getMinCount());
            code.append(", ");
            if (elem.getMaxCount() == Integer.MAX_VALUE) {
                code.append("-1");
            } else {
                code.append(elem.getMaxCount());
            }
            code.append(")");
            initMethod.addCode(code.toString());
        }
        initMethod.addCode("pattern.AddAlternative(alt)");
    }

    /**
     * Returns the constant name for a specified pattern or token id.
     *
     * @param constants      the constants file
     * @param id             the pattern id
     *
     * @return the constant name to use
     */
    private String getConstant(VisualBasicConstantsFile constants, int id) {
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
