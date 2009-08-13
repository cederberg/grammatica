/*
 * JavaAnalyzer.java
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
import net.percederberg.grammatica.parser.ProductionPattern;
import net.percederberg.grammatica.parser.ProductionPatternAlternative;
import net.percederberg.grammatica.parser.TokenPattern;

/**
 * This is a base class for all Java analyzer generators.
 *
 * @author Connor Prussin, <cprussin at vt dot edu>
 * @version 1.0
 */
abstract class JavaAnalyzer {

    /**
     * The class comment.
     */
    protected static final String TYPE_COMMENT =
        "A class providing callback methods for the parser.";

    /**
     * The newProduction method comment.
     */
    protected static final String NP_COMMENT =
        "Factory method to create a new production node. This method\n" +
        "has been overridden to return the correct node class, since the\n" +
        "'--specialize' flag was used to generate this code.\n\n" +
        "@param alt               the production pattern alternative\n" +
        "@return the new production node";

    /**
     * The enter method comment.
     */
    protected static final String ENTER_COMMENT =
        "Called when entering a parse tree node.\n\n" +
        "@param node           the node being entered\n\n" +
        "@throws ParseException if the node analysis discovered errors";

    /**
     * The exit method comment.
     */
    protected static final String EXIT_COMMENT =
        "Called when exiting a parse tree node.\n\n" +
        "@param node           the node being exited\n\n" +
        "@return the node to add to the parse tree, or\n" +
        "        null if no parse tree should be created\n\n" +
        "@throws ParseException if the node analysis discovered errors";

    /**
     * The child method comment.
     */
    protected static final String CHILD_COMMENT =
        "Called when adding a child to a parse tree node.\n\n" +
        "@param node           the parent node\n" +
        "@param child          the child node, or null\n\n" +
        "@throws ParseException if the node analysis discovered errors";

    /**
     * The Java parser generator.
     */
    protected JavaParserGenerator gen;

    /**
     * The Java file to write.
     */
    protected JavaFile file;

    /**
     * The main Java class.
     */
    protected JavaClass cls;

    /**
     * The Java enter method.
     */
    protected JavaMethod enter;

    /**
     * The Java exit method.
     */
    protected JavaMethod exit;

    /**
     * The Java child method.
     */
    protected JavaMethod child;

    /**
     * The Java newProduction method.
     */
    protected JavaMethod newProduction;

    /**
     * The JavaNodeClassesDir, for getting specialized node information iff the
     * '--specialize' flag is set.
     */
    protected JavaNodeClassesDir dir;

    /**
     * Creates a new analyzer file.  Note: DO NOT use this constructor if this
     * is a specialized node; instead, use
     * {@link #JavaAnalyzerFile(JavaParserGenerator, JavaNodeClassesDir)}.
     *
     * @param gen            the parser generator to use
     */
    public JavaAnalyzer(JavaParserGenerator gen) {
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
    public JavaAnalyzer(JavaParserGenerator gen, JavaNodeClassesDir dir) {
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

        // Set up constructors
        JavaConstructor cons = new JavaConstructor("AnalyzerStrategy strategy");
        cons.addComment(new JavaComment(
                "Builds a new analyzer using the given strategy.\n\n" +
                "@param strategy            the strategy to use"));
        cons.addCode("super(strategy);");
        this.cls.addConstructor(cons);
        cons = new JavaConstructor();
        cons.addComment(new JavaComment(
                "Builds a new analyzer using the strategy BUILD."));
        cons.addCode("super();");
        this.cls.addConstructor(cons);

        // Initialize methods
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
            file.addImport(new JavaImport("net.percederberg.grammatica.parser",
                                          "SpecializedProduction"));
        } else {
            file.addImport(new JavaImport("net.percederberg.grammatica.parser",
                                          "Token"));
        }

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

        if (!pattern.isIgnore()) {
            if (gen.specialize() && (dir != null)) {
                String type = dir.getTokenDescriptors().get(pattern).name;
                addCasesSpecialized(constant, type, false, "");
            } else {
                String name = gen.getCodeStyle().getMixedCase(pattern.getName(), true);
                addCasesUnspecialized(constant, name, false, "");
            }
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

        if (gen.specialize() && (dir != null)) {
            // If this only has one alternative, the calss is the trivial pattern name.
            if (pattern.getAlternativeCount() == 1) {
                ProductionPatternAlternative alt = pattern.getAlternative(0);
                if ((alt.getElementCount() > 1) || (alt.getElement(0).getMaxCount() > 1)) {
                    String type = dir.getAltDescriptors().get(pattern.getAlternative(0)).name;
                    addCasesSpecialized(constant, type, true, "");
                }
            } else {
                // Switch on the alterative to find out which it is.
                boolean addedSwitch = false;
                for (int i = 0; i < pattern.getAlternativeCount(); i++) {
                    ProductionPatternAlternative alt = pattern.getAlternative(i);
                    if ((alt.getElementCount() > 1) || (alt.getElement(0).getMaxCount() > 1)) {
                        if (!addedSwitch) {
                            addSwitches(constant);
                            addedSwitch = true;
                        }
                        String type = dir.getAltDescriptors().get(alt).name;
                        addCasesSpecialized(String.valueOf(i), type, true, "    ");
                    }
                }
                if (addedSwitch) {
                    endSwitches();
                }
            }
        } else if (!pattern.isSynthetic()) {
            String name = gen.getCodeStyle().getMixedCase(pattern.getName(), true);
            addCasesUnspecialized(constant, name, true, "");
        }
    }

    /**
     * Adds an abstract method to the class.  Good to use this method
     * to compact code.
     *
     * @param comment        the comment for the method
     * @param name           the name of the method
     * @param args           the method arguments
     * @param retType        the return type of the method
     * @param action         the code to run in the method
     */
    protected void addMethod(String comment, String name, String args,
            String retType) {
        addMethod(comment, name, args, retType, null);
    }

    /**
     * Adds a method to the class.  Good to use this method for one line
     * of code.
     *
     * @param comment        the comment for the method
     * @param name           the name of the method
     * @param args           the method arguments
     * @param retType        the return type of the method
     * @param action         the code to run in the method
     */
    protected void addMethod(String comment, String name, String args,
            String retType, String action) {
        JavaMethod  m = new JavaMethod(JavaMethod.PROTECTED,
                               name,
                               args,
                               retType);
        if (action != null) {
            m.addCode(action);
        } else {
            m.setModifiers(JavaMethod.PROTECTED + JavaMethod.ABSTRACT);
        }
        m.addComment(new JavaComment(comment));
        m.addThrows("ParseException");
        cls.addMethod(m);
    }

    /**
     * Adds switch statements to all the methods.  This should only be called
     * when the constant is a production.
     *
     * @param constant
     */
    private void addSwitches(String constant) {
        addCase(newProduction, constant,
                "switch (alt.getPattern().getAlternativeIndex(alt)) {",
                "", false);
        addCase(enter, constant,
                "Production prod = (Production)node;\n" +
                "    switch (prod.getPattern().getAlternativeIndex(prod.getAlternative())) {",
                "", false);
        addCase(exit, constant,
                "Production prod = (Production)node;\n" +
                "    switch (prod.getPattern().getAlternativeIndex(prod.getAlternative())) {",
                "", false);
        addCase(child, constant,
                "switch (node.getPattern().getAlternativeIndex(node.getAlternative())) {",
                "", false);
    }

    /**
     * Adds a switch case to a method.
     *
     * @param method         the method to add to
     * @param constant       the node constant
     * @param actian         what to do after the case line
     * @param prefix         place this before each line (use "//" to comment
     *                       each lines or "    " to indent it)
     * @param br             should there be a break at the end of this clause?
     */
    protected void addCase(JavaMethod method, String constant, String action,
            String prefix, boolean br) {
        method.addCode(prefix + "case " + constant + ":");
        method.addCode(prefix + "    " + action);
        if (br) {
            method.addCode(prefix + "    break;");
        }
    }

    /**
     * Ends switch statements on all the methods.  This should only be called
     * when the constant is a production.
     *
     * @param constant
     */
    private void endSwitches() {
        String str = "    }\n    break;";
        newProduction.addCode(str);
        enter.addCode(str);
        exit.addCode(str);
        child.addCode(str);
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
     * Adds all cases and methods necessary for a node.  Use if "--specialize."
     *
     * @param constant       the node constant
     * @param type           the node type (get from descriptors).
     * @param prefix         place this before each line (use "//" to comment
     *                       the lines or "    " to indent them)
     */
    abstract protected void addCasesSpecialized(String constant, String type,
            boolean isProduction, String prefix);

    /**
     * Adds all cases and methods necessary for a node.  Use if not
     * "--specialize."
     *
     * @param constant       the node constant
     * @param name           the name of the pattern for this case
     * @param type           true for "Production," else "Token"
     * @param prefix         place this before each line (use "//" to comment
     *                       the lines or "    " to indent them)
     */
    abstract protected void addCasesUnspecialized(String constant, String name,
            boolean type, String prefix);

    /**
     * Writes the file source code.
     *
     * @throws IOException if the output file couldn't be created
     *             correctly
     */
    abstract public void writeCode() throws IOException;

}
