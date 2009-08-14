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


/**
 * The Java analyzer file generator. This class encapsulates all the
 * Java code necessary for creating a analyzer class file.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.6
 */
class JavaAnalyzerFile extends JavaAnalyzer {

    /**
     * Creates a new analyzer file.  Note: DO NOT use this constructor if this
     * is a specialized node; instead, use
     * {@link #JavaAnalyzerFile(JavaParserGenerator, JavaNodeClassesDir)}.
     *
     * @param gen            the parser generator to use
     */
    public JavaAnalyzerFile(JavaParserGenerator gen) {
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
    public JavaAnalyzerFile(JavaParserGenerator gen, JavaNodeClassesDir dir) {
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
            addCase(child, constant, "child((" + type + ") node, child);",
                    prefix, true);
            addMethod(CHILD_COMMENT, "child", type + " node, Node child",
                      "void",
                      "if ((child instanceof Production) &&\n" +
                      "        ((Production)child).getAlternative().isSingleElement()) {\n" +
                      "    node.addChild(child.getChildAt(0));\n" +
                      "} else {\n" +
                      "    node.addChild(child);\n" +
                      "}");
        }
        addCase(enter, constant, "enter((" + type + ") node);",
                prefix, true);
        addCase(exit, constant, "return exit((" + type + ") node);",
                prefix, false);
        addMethod(ENTER_COMMENT, "enter", type + " node", "void",
                "");
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
            addCase(child, constant, "child" + name + "(node, child);",
                    prefix, true);
            addMethod(CHILD_COMMENT, "child" + name,
                      "Production node, Node child", "void",
                      "if (child != null) {\n" +
                      "    if (child.isSynthetic()) {\n" +
                      "        for (int i = 0; i < child.getChildCount(); i++) {\n" +
                      "            child(node, child.getChildAt(i));\n" +
                      "        }\n" +
                      "    } else {\n" +
                      "        node.addChild(child);\n" +
                      "    }\n" +
                      "}");
            t = "Production";
        } else {
            t = "Token";
        }
        addCase(enter, constant, "enter" + name + "((" + t + ") node);",
                prefix, true);
        addCase(exit, constant, "return exit" + name + "((" + t + ") node);",
                prefix, false);
        addMethod(ENTER_COMMENT, "enter" + name, t + " node", "void", "");
        addMethod(EXIT_COMMENT, "exit" + name, t + " node", t, "return node;");
    }

    /**
     * Writes the file source code.
     *
     * @throws IOException if the output file couldn't be created
     *             correctly
     */
    @Override
    public void writeCode() throws IOException {
        if (gen.specialize() && (dir != null)) {
            newProduction.addCode("}");
            newProduction.addCode("return new SpecializedProduction(alt);");
        }
        enter.addCode("}");
        exit.addCode("}");
        exit.addCode("return node;");
        child.addCode("default:");
        child.addCode("    node.addChild(child);");
        child.addCode("}");
        file.writeCode(gen.getCodeStyle());
    }
}
