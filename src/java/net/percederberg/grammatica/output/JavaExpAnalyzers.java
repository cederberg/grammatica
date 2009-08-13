/*
 * JavaExpAnalyzers.java
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
import net.percederberg.grammatica.parser.Analyzer.AnalyzerStrategy;

/**
 * The Java expanded analyzer file generator. This class encapsulates all the
 * Java code necessary for creating a set of expanded analyzers.
 *
 * @author   Connor Prussin, <cprussin at vt dot edu>
 * @version  1.0
 */
class JavaExpAnalyzers extends JavaAnalyzer {

    /**
     * The default enter method comment.
     */
    private static final String DEFAULT_ENTER_COMMENT =
        "Reserved for possible future use.\n\n" +
        "@param node          piped from node-specific exit methods";

    /**
     * The default exit method comment.
     */
    private static final String DEFAULT_EXIT_COMMENT =
        "Reserved for possible future use.\n\n" +
        "@param node          piped from node-specific exit methods\n" +
        "@return the exited node";

    /**
     * The default child method comment
     */
    private static final String DEFAULT_CHILD_COMMENT =
        "Adds the child to the node.\n\n" +
        "@param node          the node to add to\n" +
        "@param node          the node to add-piped from\n" +
        "                     node-specific child methods";

    /**
     * The comment for the constructors
     */
    private static final String CONSTRUCTOR_COMMENT =
        "Build a new analyzer using the strategy ";

    /**
     * Creates a new analyzer file.  Note: DO NOT use this constructor if this
     * is a specialized node; instead, use
     * {@link #JavaAnalyzerFile(JavaParserGenerator, JavaNodeClassesDir)}.
     *
     * @param gen            the parser generator to use
     */
    public JavaExpAnalyzers(JavaParserGenerator gen) {
        super(gen);
    }

    /**
     * Creates a new analyzer file for java output.  Use this constructor only
     * if the '--specialize' flag is set.
     *
     * @param gen            the parser generator to use
     * @param dir            the NodeClassesDir object to use to extract
     *                       production information
     */
    public JavaExpAnalyzers(JavaParserGenerator gen, JavaNodeClassesDir dir) {
        super(gen, dir);
    }

    /**
     * Adds all cases and methods necessary for a node.  Use if "--specialize."
     *
     * @param constant       the node constant
     * @param type           the node type (get from descriptors).
     * @param prefix         place this before each line (use "//" to comment
     *                       the lines or "    " to indent them)
     */
    @Override
    protected void addCasesSpecialized(String constant, String type,
            boolean isProduction, String prefix) {
        if (isProduction) {
            addCase(newProduction, constant, "return new " + type + "(alt);",
                    prefix, false);
            addCase(child, constant, "child = child((" + type + ") node, child);",
                    prefix, true);
            addMethod(CHILD_COMMENT, "child", type + " node, Node child",
                    "Node", "return child;");
        }
        addCase(enter, constant, "node = enter((" + type + ") node);",
                prefix, true);
        addCase(exit, constant, "node = exit((" + type + ") node);",
                prefix, true);
        addMethod(ENTER_COMMENT, "enter", type + " node", type,
                "return node;");
        addMethod(EXIT_COMMENT, "exit", type + " node", type,
                "return node;");
    }

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
    @Override
    protected void addCasesUnspecialized(String constant, String name,
            boolean type, String prefix) {
        String t;
        if (type) {
            addCase(child, constant, "child = child" + name + "(node, child);",
                    prefix, true);
            addMethod(CHILD_COMMENT, "child" + name, "Production node, Node child",
                    "Node", "return child;");
            t = "Production";
        } else {
            t = "Token";
        }
        addCase(enter, constant, "node = enter" + name + "((" + t + ")node);",
                prefix, true);
        addCase(exit, constant, "node = exit" + name + "((" + t + ")node);",
                prefix, true);
        addMethod(ENTER_COMMENT, "enter" + name, t + " node", t,
                "return node;");
        addMethod(EXIT_COMMENT, "exit" + name, t + " node", t,
                "return node;");
    }

    /**
     * Writes the file source code.
     *
     * @throws IOException if the output file couldn't be created
     *             correctly
     */
    @Override
    public void writeCode() throws IOException {
        enter.addCode("}");
        enter.addCode("defaultEnter(node);");
        addMethod(DEFAULT_ENTER_COMMENT,
                  "defaultEnter", "Node node", "void");
        exit.addCode("}");
        exit.addCode("return defaultExit(node);");
        addMethod(DEFAULT_EXIT_COMMENT,
                  "defaultExit", "Node node", "Node");
        child.addCode("}");
        child.addCode("defaultChild(node, child);");
        addMethod(DEFAULT_CHILD_COMMENT,
                  "defaultChild", "Production node, Node child", "void");

        // Write out the extended analyzers.
        String str;
        if (gen.specialize() && (dir != null)) {
            newProduction.addCode("}");
            newProduction.addCode("return new SpecializedProduction(alt);");
            str = "if ((child instanceof Production) &&\n" +
                  "        ((Production)child).getAlternative().isSingleElement()) {\n" +
                  "    node.addChild(child.getChildAt(0));\n" +
                  "} else {\n" +
                  "    node.addChild(child);\n" +
                  "}";
        } else {
            str = "node.addChild(child);";
        }
        writeAnalyzer("TreeBuilder", AnalyzerStrategy.BUILD, "",
                "return node;", str);
        writeAnalyzer("TreeTransformer", AnalyzerStrategy.TRANSFORM, "",
                "return node;", str);
        writeAnalyzer("TreeAnalyzer", AnalyzerStrategy.ANALYZE, "",
                "return null;", "");
        file.writeCode(gen.getCodeStyle());
    }

    /**
     * Write an analyzer that extends our abstract one.
     *
     * @param name              the extension name of the class ("MyGrammar" + name)
     * @param strategy          the strategy in use for this class
     * @param enterAction       the code for the overriden enter method
     * @param exitAction        the code for the overriden exit method
     * @param childAction       the code for the overriden child method
     * @throws java.io.IOException if the output file couldn't be created
     *             correctly
     */
    private void writeAnalyzer(String name, AnalyzerStrategy strategy,
            String enterAction, String exitAction, String childAction)
            throws IOException {

        // Set up the file.
        JavaFile f = new JavaFile(gen.getBaseDir(), gen.getBaseName() + name);
        f.addComment(new JavaComment(JavaComment.BLOCK, f.toString() + "\n\n" + gen.getFileComment()));
        f.addImport(new JavaImport("net.percederberg.grammatica.parser",
                                   "Node"));
        f.addImport(new JavaImport("net.percederberg.grammatica.parser",
                                   "Production"));
        f.addImport(new JavaImport("net.percederberg.grammatica.parser",
                                   "ParseException"));
        if (gen.getBasePackage() != null) {
            f.addPackage(new JavaPackage(gen.getBasePackage()));
        }

        // Set up the class.
        int modifiers;
        if (gen.getPublicAccess()) {
            modifiers = JavaClass.PUBLIC;
        } else {
            modifiers = JavaClass.PACKAGE_LOCAL;
        }
        JavaClass c = new JavaClass(modifiers, gen.getBaseName() + name,
                                    gen.getBaseName() + "Analyzer");
        f.addClass(c);

        // Add the class constructor
        JavaConstructor con = new JavaConstructor();
        con.addComment(new JavaComment(CONSTRUCTOR_COMMENT + strategy.toString() + "."));
        con.addCode("super(AnalyzerStrategy." + strategy.name() + ");");
        c.addConstructor(con);

        // Add the defaultEnter method.
        JavaMethod m = new JavaMethod(JavaMethod.PROTECTED, "defaultEnter",
                                      "Node node", "void");
        m.addThrows("ParseException");
        m.addComment(new JavaComment(DEFAULT_ENTER_COMMENT));
        m.addCode(enterAction);
        c.addMethod(m);

        // Add the defaultExit method.
        m = new JavaMethod(JavaMethod.PROTECTED, "defaultExit",
                "Node node", "Node");
        m.addThrows("ParseException");
        m.addComment(new JavaComment(DEFAULT_EXIT_COMMENT));
        m.addCode(exitAction);
        c.addMethod(m);

        // Add the defaultChild method.
        m = new JavaMethod(JavaMethod.PROTECTED, "defaultChild",
                "Production node, Node child", "void");
        m.addThrows("ParseException");
        m.addComment(new JavaComment(DEFAULT_CHILD_COMMENT));
        m.addCode(childAction);
        c.addMethod(m);

        // Write the method out.
        f.writeCode(gen.getCodeStyle());
    }
}
