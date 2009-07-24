/*
 * JavaAnalyzerFile.java
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
import net.percederberg.grammatica.code.java.JavaFile;
import net.percederberg.grammatica.code.java.JavaImport;
import net.percederberg.grammatica.code.java.JavaMethod;
import net.percederberg.grammatica.code.java.JavaPackage;
import net.percederberg.grammatica.parser.ProductionPattern;
import net.percederberg.grammatica.parser.ProductionPatternAlternative;
import net.percederberg.grammatica.parser.TokenPattern;

/**
 * The Java analyzer file generator. This class encapsulates all the
 * Java code necessary for creating a analyzer class file.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.6
 */
class JavaAnalyzerFile {

    /**
     * The class comment.
     */
    private static final String TYPE_COMMENT =
        "A class providing callback methods for the parser.";

    /**
     * The newProduction method comment.
     */
    private static final String NP_COMMENT =
        "Factory method to create a new production node. This method\n" +
        "has been overridden to return the correct node class, since the\n" +
        "'--specialize' flag was used to generate this code.\n\n" +
        "@param alt               the production pattern alternative\n" +
        "@return the new production node";

    /**
     * The enter method comment.
     */
    private static final String ENTER_COMMENT =
        "Called when entering a parse tree node.\n\n" +
        "@param node           the node being entered\n\n" +
        "@throws ParseException if the node analysis discovered errors";

    /**
     * The exit method comment.
     */
    private static final String EXIT_COMMENT =
        "Called when exiting a parse tree node.\n\n" +
        "@param node           the node being exited\n\n" +
        "@return the node to add to the parse tree, or\n" +
        "        null if no parse tree should be created\n\n" +
        "@throws ParseException if the node analysis discovered errors";

    /**
     * The child method comment.
     */
    private static final String CHILD_COMMENT =
        "Called when adding a child to a parse tree node.\n\n" +
        "@param node           the parent node\n" +
        "@param child          the child node, or null\n\n" +
        "@throws ParseException if the node analysis discovered errors";

    /**
     * The Java parser generator.
     */
    private JavaParserGenerator gen;

    /**
     * The Java file to write.
     */
    private JavaFile file;

    /**
     * The Java class.
     */
    private JavaClass cls;

    /**
     * The Java enter method.
     */
    private JavaMethod enter;

    /**
     * The Java exit method.
     */
    private JavaMethod exit;

    /**
     * The Java child method.
     */
    private JavaMethod child;

    /**
     * The Java newProduction method.
     */
    private JavaMethod newProduction;

    /**
     * The JavaNodeClassesDir, for getting specialized node information iff the
     * '--specialize' flag is set.
     */
    private JavaNodeClassesDir dir;

    /**
     * Creates a new analyzer file.  Note: DO NOT use this constructor if this
     * is a specialized node; instead, use
     * {@link #JavaAnalyzerFile(JavaParserGenerator, JavaNodeClassesDir)}.
     *
     * @param gen            the parser generator to use
     */
    public JavaAnalyzerFile(JavaParserGenerator gen) {
        this (gen, null);
    }

    /**
     * Creates a new analyzer file for java output.  Use this constructor only
     * if the '--specialize' flag is set.
     *
     * @param gen            the parser generator to use
     * @param dir            the NodeClassesDir object to use to extract
     *                       production information
     */
    public JavaAnalyzerFile(JavaParserGenerator gen, JavaNodeClassesDir dir) {
        String  name = gen.getBaseName() + "Analyzer";
        int     modifiers;

        this.dir = dir;
        this.gen = gen;
        this.file = new JavaFile(gen.getBaseDir(), name);
        if (gen.getPublicAccess()) {
            modifiers = JavaClass.PUBLIC + JavaClass.ABSTRACT;
        } else {
            modifiers = JavaClass.PACKAGE_LOCAL + JavaClass.ABSTRACT;
        }
        this.cls = new JavaClass(modifiers,
                                 gen.getBaseName() + "Analyzer",
                                 "Analyzer");
        if (gen.specialize() && (dir != null)) {
            this.newProduction = new JavaMethod(JavaMethod.PROTECTED,
                                                "newProduction",
                                                "ProductionPatternAlternative alt",
                                                "Production");
        }
        this.enter = new JavaMethod(JavaMethod.PROTECTED,
                                    "enter",
                                    "Node node",
                                    "void");
        this.exit = new JavaMethod(JavaMethod.PROTECTED,
                                   "exit",
                                   "Node node",
                                   "Node");
        this.child = new JavaMethod(JavaMethod.PROTECTED,
                                    "child",
                                    "Production node, Node child",
                                    "void");
        initializeCode();
    }

    /**
     * Initializes the source code objects.
     */
    private void initializeCode() {
        String  str;

        // Add file comment
        str = file.toString() + "\n\n" + gen.getFileComment();
        file.addComment(new JavaComment(JavaComment.BLOCK, str));

        // Add package
        if (gen.getBasePackage() != null) {
            JavaPackage p = new JavaPackage(gen.getBasePackage());
            file.addPackage(p);
            if (gen.specialize() && (dir != null)) {
                file.addImport(new JavaImport(gen.getBasePackage() + ".nodes"));
            }
        }
        file.addClass(cls);

        // Add imports
        file.addImport(new JavaImport("net.percederberg.grammatica.parser",
                                      "Analyzer"));
        file.addImport(new JavaImport("net.percederberg.grammatica.parser",
                                      "Node"));
        file.addImport(new JavaImport("net.percederberg.grammatica.parser",
                                      "ParseException"));
        file.addImport(new JavaImport("net.percederberg.grammatica.parser",
                                      "Production"));
        if (gen.specialize() && (dir != null)) {
            file.addImport(new JavaImport("net.percederberg.grammatica.parser",
                                          "ProductionPatternAlternative"));
        }
        file.addImport(new JavaImport("net.percederberg.grammatica.parser",
                                      "Token"));

        // Add class comment
        str = TYPE_COMMENT;
        if (gen.getClassComment() != null) {
            str += "\n\n" + gen.getClassComment();
        }
        cls.addComment(new JavaComment(str));

        // Add the newProduction method
        if (gen.specialize() && (dir != null)) {
            newProduction.addComment(new JavaComment(NP_COMMENT));
            newProduction.addCode("switch (alt.getPattern().getId()) {");
            cls.addMethod(newProduction);
        }

        // Add enter method
        enter.addComment(new JavaComment(ENTER_COMMENT));
        enter.addThrows("ParseException");
        enter.addCode("switch (node.getId()) {");
        cls.addMethod(enter);

        // Add exit method
        exit.addComment(new JavaComment(EXIT_COMMENT));
        exit.addThrows("ParseException");
        exit.addCode("switch (node.getId()) {");
        cls.addMethod(exit);

        // Add child method
        child.addComment(new JavaComment(CHILD_COMMENT));
        child.addThrows("ParseException");
        child.addCode("switch (node.getId()) {");
        cls.addMethod(child);
    }

    /**
     * Adds the token analysis methods to this file.
     *
     * @param pattern        the token pattern
     * @param constants      the constants file
     */
    public void addToken(TokenPattern pattern,
                         JavaConstantsFile constants) {

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
                              JavaConstantsFile constants) {

        String   constant = constants.getConstant(pattern.getId());
        String   name = gen.getCodeStyle().getMixedCase(pattern.getName(), true);

        if (gen.specialize() && (dir != null)) {
            addNewProductionCase(constant, pattern);
        }

        if (!pattern.isSynthetic()) {
            addEnterCase(constant, name, "Production");
            addEnterMethod(name, "Production");
            addExitCase(constant, name, "Production");
            addExitMethod(name, "Production");
            addChildCase(constant, name);
            addChildMethod(name);
        }
    }

    /**
     * Add a newProduction method switch case.
     *
     * @param constant       the node constant
     * @param name           the name of the pattern
     * @param pattern        the node pattern
     */
    public void addNewProductionCase(String constant, ProductionPattern pattern) {
        newProduction.addCode("case " + constant + ":");

        // If this only has one alternative, the calss is the trivial pattern name.
        if (pattern.getAlternativeCount() == 1) {
            ProductionPatternAlternative alt = pattern.getAlternative(0);
            if ((alt.getElementCount() > 1) || (alt.getElement(0).getMaxCount() > 1)) {
                String name = dir.getAltDescriptors().get(pattern.getAlternative(0)).name;
                newProduction.addCode("    return new " + name + "(alt);");
            } else {
                newProduction.addCode("    return new Production(alt);");
            }
        } else {
            // Switch on the alterative to find out which it is.
            newProduction.addCode("    switch (alt.getPattern().getAlternativeIndex(alt)) {");
            for (int i = 0; i < pattern.getAlternativeCount(); i++) {
                ProductionPatternAlternative alt = pattern.getAlternative(i);
                    newProduction.addCode("    case " + i + ":");
                if ((alt.getElementCount() > 1) || (alt.getElement(0).getMaxCount() > 1)) {
                    String name = dir.getAltDescriptors().get(alt).name;
                    newProduction.addCode("        return new " + name + "(alt);");
                } else {
                    newProduction.addCode("        return new Production(alt);");
                }
            }
            newProduction.addCode("    }");
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
        enter.addCode("case " + constant + ":");
        enter.addCode("    enter" + name + "((" + type + ") node);");
        enter.addCode("    break;");
    }

    /**
     * Adds an exit method switch case.
     *
     * @param constant       the node constant
     * @param name           the node name
     * @param type           the node type
     */
    private void addExitCase(String constant, String name, String type) {
        exit.addCode("case " + constant + ":");
        exit.addCode("    return exit" + name + "((" + type + ") node);");
    }

    /**
     * Adds a child method switch case.
     *
     * @param constant       the node constant
     * @param name           the node name
     */
    private void addChildCase(String constant, String name) {
        child.addCode("case " + constant + ":");
        child.addCode("    child" + name + "(node, child);");
        child.addCode("    break;");
    }

    /**
     * Adds an enter node method to this file.
     *
     * @param name           the node name
     * @param type           the node type
     */
    private void addEnterMethod(String name, String type) {
        JavaMethod  m;

        m = new JavaMethod(JavaMethod.PROTECTED,
                           "enter" + name,
                           type + " node",
                           "void");
        m.addComment(new JavaComment(ENTER_COMMENT));
        m.addThrows("ParseException");
        cls.addMethod(m);
    }

    /**
     * Adds an exit node method to this file.
     *
     * @param name           the node name
     * @param type           the node type
     */
    private void addExitMethod(String name, String type) {
        JavaMethod  m;

        m = new JavaMethod(JavaMethod.PROTECTED,
                           "exit" + name,
                           type + " node",
                           "Node");
        m.addComment(new JavaComment(EXIT_COMMENT));
        m.addThrows("ParseException");
        m.addCode("return node;");
        cls.addMethod(m);
    }

    /**
     * Adds an add child method to this file.
     *
     * @param name           the node name
     */
    private void addChildMethod(String name) {
        JavaMethod  m;

        m = new JavaMethod(JavaMethod.PROTECTED,
                           "child" + name,
                           "Production node, Node child",
                           "void");
        m.addComment(new JavaComment(CHILD_COMMENT));
        m.addThrows("ParseException");
        if (gen.specialize() && (dir != null)) {
            m.addCode("if ((child != null) && (child.getChildCount() == 1)) {");
            m.addCode("    node.addChild(child.getChildAt(0));");
            m.addCode("} else {");
            m.addCode("    node.addChild(child);");
            m.addCode("}");
        } else {
            m.addCode("node.addChild(child);");
        }
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
        if (gen.specialize() && (dir != null)) {
            newProduction.addCode("}");
            newProduction.addCode("return new Production(alt);");
        }
        enter.addCode("}");
        exit.addCode("}");
        exit.addCode("return node;");
        child.addCode("}");
        file.writeCode(gen.getCodeStyle());
    }
}
