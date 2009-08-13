/*
 * CSharpAnalyzer.java
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
import net.percederberg.grammatica.code.csharp.CSharpMethod;
import net.percederberg.grammatica.code.csharp.CSharpNamespace;
import net.percederberg.grammatica.code.csharp.CSharpUsing;
import net.percederberg.grammatica.parser.ProductionPattern;
import net.percederberg.grammatica.parser.ProductionPatternAlternative;
import net.percederberg.grammatica.parser.TokenPattern;

/**
 * This is a base class for all C# analyzer generators.
 *
 * @author Connor Prussin, <cprussin at vt dot edu>
 * @version 1.0
 */
abstract class CSharpAnalyzer {

    /**
     * The class comment.
     */
    protected static final String TYPE_COMMENT =
        "<remarks>A class providing callback methods for the\n" +
        "parser.</remarks>";

    /**
     * The newProduction method comment.
     */
    protected static final String NP_COMMENT =
        "<spmmary>Factory method to create a new production node.\n" +
        "This method has been overridden to return the correct node\n" +
        "class, since the '--specialize' flag was used to generate\n" +
        "this code.</summary>\n\n" +
        "<param name='alt'>the production pattern alternative</param>\n" +
        "<returns>the new production node</return>";

    /**
     * The enter method comment.
     */
    protected static final String ENTER_COMMENT =
        "<summary>Called when entering a parse tree node.</summary>\n\n" +
        "<param name='node'>the node being entered</param>\n\n" +
        "<exception cref='ParseException'>if the node analysis\n" +
        "discovered errors</exception>";

    /**
     * The exit method comment.
     */
    protected static final String EXIT_COMMENT =
        "<summary>Called when exiting a parse tree node.</summary>\n\n" +
        "<param name='node'>the node being exited</param>\n\n" +
        "<returns>the node to add to the parse tree, or\n" +
        "         null if no parse tree should be created</returns>\n\n" +
        "<exception cref='ParseException'>if the node analysis\n" +
        "discovered errors</exception>";

    /**
     * The child method comment.
     */
    protected static final String CHILD_COMMENT =
        "<summary>Called when adding a child to a parse tree\n" +
        "node.</summary>\n\n" +
        "<param name='node'>the parent node</param>\n" +
        "<param name='child'>the child node, or null</param>\n\n" +
        "<exception cref='ParseException'>if the node analysis\n" +
        "discovered errors</exception>";

    /**
     * The parser generator.
     */
    protected CSharpParserGenerator gen;

    /**
     * The file to write.
     */
    protected CSharpFile file;

    /**
     * The class.
     */
    protected CSharpClass cls;

    /**
     * The enter method.
     */
    protected CSharpMethod enter;

    /**
     * The exit method.
     */
    protected CSharpMethod exit;

    /**
     * The child method.
     */
    protected CSharpMethod child;

    /**
     * The newProduction method.
     */
    protected CSharpMethod newProduction;

    /**
     * The NodeClassesDir, for getting specialized node information iff the
     * '--specialize' flag is set.
     */
    protected CSharpNodeClassesDir dir;

    /**
     * Creates a new analyzer file.  Note: DO NOT use this constructor if this
     * is a specialized node; instead, use
     * {@link #CSharpAnalyzerFile(CSharpParserGenerator, CSharpNodeClassesDir)}.
     *
     * @param gen            the parser generator to use
     */
    public CSharpAnalyzer(CSharpParserGenerator gen) {
        this (gen, null);
    }

    /**
     * Creates a new analyzer file for C# output.  Use this constructor only
     * if the '--specialize' flag is set.
     *
     * @param gen            the parser generator to use
     * @param dir            the NodeClassesDir object to use to extract
     *                       production information
     */
    public CSharpAnalyzer(CSharpParserGenerator gen, CSharpNodeClassesDir dir) {
        String  name = gen.getBaseName() + "Analyzer";
        int     modifiers;

        this.dir = dir;
        this.gen = gen;
        this.file = new CSharpFile(gen.getBaseDir(), name);
        if (gen.getPublicAccess()) {
            modifiers = CSharpClass.PUBLIC + CSharpClass.ABSTRACT;
        } else {
            modifiers = CSharpClass.INTERNAL + CSharpClass.ABSTRACT;
        }
        this.cls = new CSharpClass(modifiers, name, "Analyzer");

        // Set up constructors
        CSharpConstructor cons = new CSharpConstructor("AnalyzerStrategy strategy");
        cons.addComment(new CSharpComment(
                "Builds a new analyzer using the given strategy.\n\n" +
                "@param strategy            the strategy to use"));
        cons.addInitializer("base(strategy)");
        this.cls.addConstructor(cons);
        cons = new CSharpConstructor();
        cons.addComment(new CSharpComment(
                "Builds a new analyzer using the strategy BUILD."));
        cons.addInitializer("base(AnalyzerStrategy.BUILD)");
        this.cls.addConstructor(cons);

        // Initialize methods
        modifiers = CSharpMethod.PUBLIC + CSharpMethod.OVERRIDE;
        if (gen.specialize() && (dir != null)) {
            this.newProduction = new CSharpMethod(modifiers,
                                                "NewProduction",
                                                "ProductionPatternAlternative alt",
                                                "Production");
        }
        this.enter = new CSharpMethod(modifiers,
                                      "Enter",
                                      "Node node",
                                      "void");
        this.exit = new CSharpMethod(modifiers,
                                     "Exit",
                                     "Node node",
                                     "Node");
        this.child = new CSharpMethod(modifiers,
                                      "Child",
                                      "Production node, Node child",
                                      "void");
        initializeCode();
    }

    /**
     * Initializes the source code objects.
     */
    private void initializeCode() {
        String  str;

        // Add using
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

        // Add the newProduction method
        if (gen.specialize() && (dir != null)) {
            newProduction.addComment(new CSharpComment(NP_COMMENT));
            newProduction.addCode("switch (alt.Pattern.Id) {");
            cls.addMethod(newProduction);
        }

        // Add enter method
        enter.addComment(new CSharpComment(ENTER_COMMENT));
        enter.addCode("switch (node.Id) {");
        cls.addMethod(enter);

        // Add exit method
        exit.addComment(new CSharpComment(EXIT_COMMENT));
        exit.addCode("switch (node.Id) {");
        cls.addMethod(exit);

        // Add child method
        child.addComment(new CSharpComment(CHILD_COMMENT));
        child.addCode("switch (node.Id) {");
        cls.addMethod(child);
    }

    /**
     * Adds the token analysis methods to this file.
     *
     * @param pattern        the token pattern
     * @param constants      the constants file
     */
    public void addToken(TokenPattern pattern,
                         CSharpConstantsFile constants) {

        String constant = constants.getConstant(pattern.getId());

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
                              CSharpConstantsFile constants) {

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
        CSharpMethod  m = new CSharpMethod(CSharpMethod.PUBLIC + CSharpMethod.VIRTUAL,
                                           name,
                                           args,
                                           retType);
        if (action != null) {
            m.addCode(action);
        } else {
            m.setModifiers(CSharpMethod.PUBLIC + CSharpMethod.ABSTRACT);
        }
        m.addComment(new CSharpComment(comment));
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
                "switch (alt.Pattern.GetAlternativeIndex(alt)) {",
                "", false);
        addCase(enter, constant,
                "Production prod = (Production)node;\n" +
                "    switch (prod.Pattern.GetAlternativeIndex(prod.Alternative)) {",
                "", false);
        addCase(exit, constant,
                "Production prod = (Production)node;\n" +
                "    switch (prod.Pattern.GetAlternativeIndex(prod.Alternative)) {",
                "", false);
        addCase(child, constant,
                "switch (node.Pattern.GetAlternativeIndex(node.Alternative)) {",
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
    protected void addCase(CSharpMethod method, String constant, String action,
            String prefix, boolean br) {
        try {
            method.addCode(prefix + "case " + Integer.parseInt(constant) + ":");
        } catch (NumberFormatException e) {
            method.addCode(prefix + "case (int)" + constant + ":");
        }
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
