/*
 * VisualBasicAnalyzerFile.java
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
import net.percederberg.grammatica.code.visualbasic.VisualBasicFile;
import net.percederberg.grammatica.code.visualbasic.VisualBasicImports;
import net.percederberg.grammatica.code.visualbasic.VisualBasicMethod;
import net.percederberg.grammatica.code.visualbasic.VisualBasicNamespace;

import net.percederberg.grammatica.parser.ProductionPattern;
import net.percederberg.grammatica.parser.TokenPattern;

/**
 * The Visual Basic analyzer file generator. This class encapsulates
 * all the Visual Basic (.NET) code necessary for creating a analyzer
 * class file.
 *
 * @author   Adrian Moore, <adrianrob at hotmail dot com>
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 * @since    1.5
 */
class VisualBasicAnalyzerFile {

    /**
     * The class comment.
     */
    private static final String TYPE_COMMENT =
        "<remarks>A class providing callback methods for the\n" +
        "parser.</remarks>";

    /**
     * The enter method comment.
     */
    private static final String ENTER_COMMENT =
        "<summary>Called when entering a parse tree node.</summary>\n\n" +
        "<param name='node'>the node being entered</param>\n\n" +
        "<exception cref='ParseException'>if the node analysis\n" +
        "discovered errors</exception>";

    /**
     * The exit method comment.
     */
    private static final String EXIT_COMMENT =
        "<summary>Called when exiting a parse tree node.</summary>\n\n" +
        "<param name='node'>the node being exited</param>\n\n" +
        "<returns>the node to add to the parse tree, or\n" +
        "         null if no parse tree should be created</returns>\n\n" +
        "<exception cref='ParseException'>if the node analysis\n" +
        "discovered errors</exception>";

    /**
     * The child method comment.
     */
    private static final String CHILD_COMMENT =
        "<summary>Called when adding a child to a parse tree\n" +
        "node.</summary>\n\n" +
        "<param name='node'>the parent node</param>\n" +
        "<param name='child'>the child node, or null</param>\n\n" +
        "<exception cref='ParseException'>if the node analysis\n" +
        "discovered errors</exception>";

    /**
     * The parser generator.
     */
    private VisualBasicParserGenerator gen;

    /**
     * The file to write.
     */
    private VisualBasicFile file;

    /**
     * The class.
     */
    private VisualBasicClass cls;

    /**
     * The enter method.
     */
    private VisualBasicMethod enter;

    /**
     * The exit method.
     */
    private VisualBasicMethod exit;

    /**
     * The child method.
     */
    private VisualBasicMethod child;

    /**
     * Creates a new analyzer file.
     *
     * @param gen            the parser generator to use
     */
    public VisualBasicAnalyzerFile(VisualBasicParserGenerator gen) {
        String  name = gen.getBaseName() + "Analyzer";
        int     modifiers;

        this.gen = gen;
        this.file = new VisualBasicFile(gen.getBaseDir(), name);
        modifiers = VisualBasicClass.MUST_INHERIT;
        if (gen.getPublicAccess()) {
            modifiers += VisualBasicClass.PUBLIC;
        } else {
            modifiers += VisualBasicClass.FRIEND;
        }
        this.cls = new VisualBasicClass(modifiers, name, "Analyzer");
        modifiers = VisualBasicMethod.PUBLIC + VisualBasicMethod.OVERRIDES;
        this.enter = new VisualBasicMethod(modifiers,
                                           "Enter",
                                           "ByVal node As Node",
                                           "");
        this.exit = new VisualBasicMethod(modifiers,
                                          "[Exit]",
                                          "ByVal node As Node",
                                          "Node");
        this.child = new VisualBasicMethod(modifiers,
                                           "Child",
                                           "ByVal node As Production, " +
                                           "ByVal child As Node",
                                           "");
        initializeCode();
    }

    /**
     * Initializes the source code objects.
     */
    private void initializeCode() {
        VisualBasicNamespace  n;
        String                str;

        // Add using
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

        // Add enter method
        enter.addComment(new VisualBasicComment(ENTER_COMMENT));
        enter.addCode("Select Case node.Id");
        cls.addMethod(enter);

        // Add exit method
        exit.addComment(new VisualBasicComment(EXIT_COMMENT));
        exit.addCode("Select Case node.Id");
        cls.addMethod(exit);

        // Add child method
        child.addComment(new VisualBasicComment(CHILD_COMMENT));
        child.addCode("Select Case node.Id");
        cls.addMethod(child);
    }

    /**
     * Adds the token analysis methods to this file.
     *
     * @param pattern        the token pattern
     * @param constants      the constants file
     */
    public void addToken(TokenPattern pattern,
                         VisualBasicConstantsFile constants) {

        String  constant = constants.getConstant(pattern.getId());
        String  name;

        if (!pattern.isIgnore()) {
            name = gen.getCodeStyle().getMixedCase(pattern.getName(), true);
            addEnterCase(constant, name, "Token");
            addEnterMethod(name, "Token");
            addExitCase(constant, name, "Token");
            addExitMethod(name, "Token");
        }
    }

    /**
     * Adds the production analysis methods to this file.
     *
     * @param pattern        the production pattern
     * @param constants      the constants file
     */
    public void addProduction(ProductionPattern pattern,
                              VisualBasicConstantsFile constants) {

        String   constant = constants.getConstant(pattern.getId());
        String   name;

        if (!pattern.isSynthetic()) {
            name = gen.getCodeStyle().getMixedCase(pattern.getName(),
                                                   true);
            addEnterCase(constant, name, "Production");
            addEnterMethod(name, "Production");
            addExitCase(constant, name, "Production");
            addExitMethod(name, "Production");
            addChildCase(constant, name);
            addChildMethod(name);
        }
    }

    /**
     * Adds an enter method switch case.
     *
     * @param constant       the node constant
     * @param name           the node name
     * @param type           the node type
     */
    private void addEnterCase(String constant, String name, String type) {
        enter.addCode("Case " + constant );
        enter.addCode("    Enter" + name + "(CType(node," + type + "))");
        enter.addCode("");
    }

    /**
     * Adds an exit method switch case.
     *
     * @param constant       the node constant
     * @param name           the node name
     * @param type           the node type
     */
    private void addExitCase(String constant, String name, String type) {
        exit.addCode("Case " + constant );
        exit.addCode("    return Exit" + name + "(CType(node," + type + "))");
        exit.addCode("");
    }

    /**
     * Adds a child method switch case.
     *
     * @param constant       the node constant
     * @param name           the node name
     */
    private void addChildCase(String constant, String name) {
        child.addCode("Case " + constant );
        child.addCode("    Child" + name + "(node, child)");
        child.addCode("");
    }

    /**
     * Adds an enter node method to this file.
     *
     * @param name           the node name
     * @param type           the node type
     */
    private void addEnterMethod(String name, String type) {
        VisualBasicMethod  m;

        m = new VisualBasicMethod(VisualBasicMethod.PUBLIC +
                                  VisualBasicMethod.OVERRIDABLE,
                                  "Enter" + name,
                                  "ByVal node As " + type,
                                  "");
        m.addComment(new VisualBasicComment(ENTER_COMMENT));
        cls.addMethod(m);
    }

    /**
     * Adds an exit node method to this file.
     *
     * @param name           the node name
     * @param type           the node type
     */
    private void addExitMethod(String name, String type) {
        VisualBasicMethod  m;

        m = new VisualBasicMethod(VisualBasicMethod.PUBLIC +
                                  VisualBasicMethod.OVERRIDABLE,
                                  "Exit" + name,
                                  "ByVal node As " + type,
                                  "Node");
        m.addComment(new VisualBasicComment(EXIT_COMMENT));
        m.addCode("Return node");
        cls.addMethod(m);
    }

    /**
     * Adds an add child method to this file.
     *
     * @param name           the node name
     */
    private void addChildMethod(String name) {
        VisualBasicMethod  m;

        m = new VisualBasicMethod(VisualBasicMethod.PUBLIC +
                                  VisualBasicMethod.OVERRIDABLE,
                                  "Child" + name,
                                  "ByVal node As Production, " +
                                  "ByVal child As Node",
                                  "");
        m.addComment(new VisualBasicComment(CHILD_COMMENT));
        m.addCode("node.AddChild(child)");
        cls.addMethod(m);
    }

    /**
     * Returns the class name for this analyzer.
     *
     * @return the class name for this analyzer
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
        enter.addCode("End Select");
        exit.addCode("End Select");
        exit.addCode("Return node");
        child.addCode("End Select");
        file.writeCode(gen.getCodeStyle());
    }
}
