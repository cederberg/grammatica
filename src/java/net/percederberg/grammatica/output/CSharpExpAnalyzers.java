/*
 * CSharpExpAnalyzers.java
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
import net.percederberg.grammatica.parser.Analyzer.AnalyzerStrategy;

/**
 * The C# expanded analyzer file generator. This class encapsulates all the
 * code necessary for creating a set of expanded analyzers.
 *
 * @author   Connor Prussin, <cprussin at vt dot edu>
 * @version  1.0
 */
class CSharpExpAnalyzers extends CSharpAnalyzer {

    /**
     * The default enter method comment.
     */
    private static final String DEFAULT_ENTER_COMMENT =
        "<summary>Reserved for possible future use.</summary>\n\n" +
        "<param name='node'>piped from node-specific exit methods</param>";

    /**
     * The default exit method comment.
     */
    private static final String DEFAULT_EXIT_COMMENT =
        "<summary>Reserved for possible future use.</summary>\n\n" +
        "<param name='node'>piped from node-specific exit methods</param>\n" +
        "<returns>the exited node</returns>";

    /**
     * The default child method comment
     */
    private static final String DEFAULT_CHILD_COMMENT =
        "<summary>Adds the child to the node.</summary>\n\n" +
        "<param name='node'>the node to add to</param>\n" +
        "<param name='node'>the node to add-piped from\n" +
        "        node-specific child methods</param>";

    /**
     * The comment for the constructors
     */
    private static final String CONSTRUCTOR_COMMENT =
        "<summary>Build a new analyzer using the strategy ";

    /**
     * Creates a new analyzer file.  Note: DO NOT use this constructor if this
     * is a specialized node; instead, use
     * {@link #CSharpAnalyzerFile(CSharpParserGenerator, CSharpNodeClassesDir)}.
     *
     * @param gen            the parser generator to use
     */
    public CSharpExpAnalyzers(CSharpParserGenerator gen) {
        super(gen);
    }

    /**
     * Creates a new analyzer file for C# output.  Use this constructor only
     * if the '--specialize' flag is set.
     *
     * @param gen            the parser generator to use
     * @param dir            the NodeClassesDir object to use to extract
     *                       production information
     */
    public CSharpExpAnalyzers(CSharpParserGenerator gen, CSharpNodeClassesDir dir) {
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
            addCase(child, constant, "child = Child((" + type + ") node, child);",
                    prefix, true);
            addMethod(CHILD_COMMENT, "Child", type + " node, Node child",
                    "Node", "return child;");
        }
        addCase(enter, constant, "node = Enter((" + type + ") node);",
                prefix, true);
        addCase(exit, constant, "node = Exit((" + type + ") node);",
                prefix, true);
        addMethod(ENTER_COMMENT, "Enter", type + " node", type,
                "return node;");
        addMethod(EXIT_COMMENT, "Exit", type + " node", type,
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
            addCase(child, constant, "child = Child" + name + "(node, child);",
                    prefix, true);
            addMethod(CHILD_COMMENT, "Child" + name, "Production node, Node child",
                    "Node", "return child;");
            t = "Production";
        } else {
            t = "Token";
        }
        addCase(enter, constant, "node = Enter" + name + "((" + t + ") node);",
                prefix, true);
        addCase(exit, constant, "node = Exit" + name + "((" + t + ") node);",
                prefix, true);
        addMethod(ENTER_COMMENT, "Enter" + name, t + " node", t,
                "return node;");
        addMethod(EXIT_COMMENT, "Exit" + name, t + " node", t,
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
        enter.addCode("DefaultEnter(node);");
        addMethod(DEFAULT_ENTER_COMMENT,
                  "DefaultEnter", "Node node", "void");
        exit.addCode("}");
        exit.addCode("return DefaultExit(node);");
        addMethod(DEFAULT_EXIT_COMMENT,
                  "DefaultExit", "Node node", "Node");
        child.addCode("}");
        child.addCode("DefaultChild(node, child);");
        addMethod(DEFAULT_CHILD_COMMENT,
                  "DefaultChild", "Production node, Node child", "void");

        // Write out the extended analyzers.
        String str;
        if (gen.specialize() && (dir != null)) {
            newProduction.addCode("}");
            newProduction.addCode("return new SpecializedProduction(alt);");
            str = "if ((child is Production) &&\n" +
                  "        (((Production)child).Alternative.IsSingleElement())) {\n" +
                  "    node.AddChild(child.GetChildAt(0));\n" +
                  "} else {\n" +
                  "    node.AddChild(child);\n" +
                  "}";
        } else {
            str = "if (child != null) {\n" +
                  "    if (child.IsSynthetic()) {\n" +
                  "        for (int i = 0; i < child.GetChildCount(); i++) {\n" +
                  "            Child(node, child.GetChildAt(i));\n" +
                  "        }\n" +
                  "    } else {\n" +
                  "        node.AddChild(child);\n" +
                  "    }\n" +
                  "}";
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
        CSharpFile f = new CSharpFile(gen.getBaseDir(), gen.getBaseName() + name);
        f.addComment(new CSharpComment(CSharpComment.BLOCK, f.toString() +
                "\n\n" + gen.getFileComment()));
        f.addUsing(new CSharpUsing("PerCederberg.Grammatica.Runtime"));

        // Set up the class.
        int modifiers;
        if (gen.getPublicAccess()) {
            modifiers = CSharpClass.PUBLIC;
        } else {
            modifiers = CSharpClass.INTERNAL;
        }
        CSharpClass c = new CSharpClass(modifiers, gen.getBaseName() + name,
                                        gen.getBaseName() + "Analyzer");

        // Set up the namespace
        if (gen.getNamespace() == null) {
            f.addClass(c);
        } else {
            CSharpNamespace n = new CSharpNamespace(gen.getNamespace());
            n.addClass(c);
            f.addNamespace(n);
        }

        // Add the class constructor
        CSharpConstructor con = new CSharpConstructor();
        con.addComment(new CSharpComment(CONSTRUCTOR_COMMENT +
                                         strategy.toString() + ".</summary>"));
        con.addInitializer("base(AnalyzerStrategy." + strategy.name() + ")");
        c.addConstructor(con);

        // Add the defaultEnter method.
        CSharpMethod m = new CSharpMethod(CSharpMethod.PUBLIC + CSharpMethod.OVERRIDE,
                "DefaultEnter", "Node node", "void");
        m.addComment(new CSharpComment(DEFAULT_ENTER_COMMENT));
        m.addCode(enterAction);
        c.addMethod(m);

        // Add the defaultExit method.
        m = new CSharpMethod(CSharpMethod.PUBLIC + CSharpMethod.OVERRIDE,
                "DefaultExit", "Node node", "Node");
        m.addComment(new CSharpComment(DEFAULT_EXIT_COMMENT));
        m.addCode(exitAction);
        c.addMethod(m);

        // Add the defaultChild method.
        m = new CSharpMethod(CSharpMethod.PUBLIC + CSharpMethod.OVERRIDE,
                "DefaultChild", "Production node, Node child", "void");
        m.addComment(new CSharpComment(DEFAULT_CHILD_COMMENT));
        m.addCode(childAction);
        c.addMethod(m);

        // Write the method out.
        f.writeCode(gen.getCodeStyle());
    }
}
