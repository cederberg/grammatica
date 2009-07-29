/*
 * CSharpNodeClassesDir.java
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
 * Copyright (c) 2003-2005 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.output;

import net.percederberg.grammatica.code.csharp.CSharpInterface;
import java.util.ArrayList;
import net.percederberg.grammatica.code.CodeFile;
import net.percederberg.grammatica.code.csharp.CSharpClass;
import net.percederberg.grammatica.code.csharp.CSharpComment;
import net.percederberg.grammatica.code.csharp.CSharpConstructor;
import net.percederberg.grammatica.code.csharp.CSharpFile;
import net.percederberg.grammatica.code.csharp.CSharpMethod;
import net.percederberg.grammatica.code.csharp.CSharpNamespace;
import net.percederberg.grammatica.code.csharp.CSharpProperty;
import net.percederberg.grammatica.code.csharp.CSharpUsing;

/**
 * This class handles the node specification in C#.
 *
 * @author   Connor Prussin, <cprussin at vt dot edu>
 * @version  1.0
 * @since    1.6
 */
class CSharpNodeClassesDir extends NodeClassesDir {

    /**
     * Basic constructor
     *
     * @param gen               The CSharpParserGenerator to use.
     * @param dir               The name of the directory within the rest of
     *                          the output to put the files.  This should only
     *                          be nonnull if the package is set.
     */
    CSharpNodeClassesDir(CSharpParserGenerator gen) {
        super(gen, gen.getNamespace() != null ? "Nodes" : null);
    }

    /**
     * Return a new token class corresponding to a token described by the given
     * TokenDescriptor.  See {@link TokenDescriptor} for more information.
     *
     * @param desc              the new class's descriptor
     * @return                  the new class file
     */
    @Override
    CodeFile buildClassFile(TokenDescriptor desc) {
        // Build the file, add the comment, and add usings.
        CSharpFile f = new CSharpFile(this, desc.name);
        f.addComment(new CSharpComment(CSharpComment.BLOCK,
                f.toString() + "\n\n" + gen.getFileComment()));
        f.addUsing(new CSharpUsing("PerCederberg.Grammatica.Runtime"));

        // Build the class with a comment.
        String[] implList = new String[desc.inherits.size() + 1];
        implList[0] = "Token";
        for (int i = 0; i < desc.inherits.size(); i++) {
            implList[i + 1] = desc.inherits.get(i).name;
        }
        CSharpClass cls = new CSharpClass(CSharpClass.PUBLIC, desc.name, implList);
        cls.addComment(new CSharpComment("<summary>" + desc.comment.replace("/", "&#47;") + "</summary>"));

        // Add a constructor with a comment.
        CSharpConstructor con = new CSharpConstructor("TokenPattern pattern, string image, int line, int col");
        con.addInitializer("base (pattern, image, line, col)");
        con.addComment(new CSharpComment(
                "<summary>\n" +
                "Creates a new " + desc.name.substring(2) + " node.\n" +
                "NODE PATTERN: \"" + desc.tok.getPattern().replace("/", "&#47;") + "\"\n" +
                "</summary>\n\n" +
                "<param name='pattern'>the token pattern</param>\n" +
                "<param name='image'>the token image (i.e. characters)</param>\n" +
                "<param name='line'>the line number of the first character</param>\n" +
                "<param name='col'>the column number of the first character</param>"));
        cls.addConstructor(con);

        // Add the class and namespace and return the new file
        if (((CSharpParserGenerator)gen).getNamespace() == null) {
            f.addClass(cls);
        } else {
            CSharpNamespace n = new CSharpNamespace(((CSharpParserGenerator)gen).getNamespace() + ".Nodes");
            n.addClass(cls);
            f.addNamespace(n);
        }
        return f;
    }

    /**
     * Return a new production class corresponding to a production described by
     * the given ProdDescriptor.  See {@link ProdDescriptor} for more
     * information.
     *
     * @param desc              the new class's descriptor
     * @return                  the new class file
     */
    @Override
    CodeFile buildClassFile(ProdDescriptor desc) {
        // Build the file and add a comment.
        CSharpFile f = new CSharpFile(this, desc.name);
        f.addComment(new CSharpComment(CSharpComment.BLOCK,
                f.toString() + "\n\n" + gen.getFileComment()));

        // Build the interface with a comment.
        String[] implList = new String[desc.inherits.size()];
        for (int i = 0; i < desc.inherits.size(); i++) {
            implList[i] = desc.inherits.get(i).name;
        }
        CSharpInterface ifc = new CSharpInterface(CSharpInterface.PUBLIC, desc.name, implList);
        ifc.addComment(new CSharpComment("<summary>" + desc.comment.replace("/", "&#47;") + "</summary>"));

        // Add the interface and namespace and return the new file
        if (((CSharpParserGenerator)gen).getNamespace() == null) {
            f.addInterface(ifc);
        } else {
            CSharpNamespace n = new CSharpNamespace(((CSharpParserGenerator)gen).getNamespace() + ".Nodes");
            n.addInterface(ifc);
            f.addNamespace(n);
        }
        return f;
    }

    /**
     * Return a new alternative class corresponding to an alternative described
     * by the given AltDescriptor.  See {@link AltDescriptor} javadoc for more
     * information.
     *
     * @param desc              the new class's descriptor
     * @param accessors         a list of descriptors contaning information on
     *                          each accessor that the new class should contain.
     * @return                  the new class file
     */
    @Override
    CodeFile buildClassFile(AltDescriptor desc, ArrayList<AccessorDescriptor> accessors) {
        // Build the file, add the comment, and add usings.
        CSharpFile f = new CSharpFile(this, desc.name);
        f.addComment(new CSharpComment(CSharpComment.BLOCK,
                f.toString() + "\n\n" + gen.getFileComment()));
        f.addUsing(new CSharpUsing("PerCederberg.Grammatica.Runtime"));

        // Build the class with a comment.
        String[] implList = new String[desc.inherits.size() + 1];
        implList[0] = "SpecializedProduction";
        for (int i = 0; i < desc.inherits.size(); i++) {
            implList[i + 1] = desc.inherits.get(i).name;
        }
        CSharpClass cls = new CSharpClass(CSharpClass.PUBLIC, desc.name, implList);
        cls.addComment(new CSharpComment("<summary>" + desc.comment.replace("/", "&#47;") + "</summary>"));

        // Add a constructor with a comment.
        CSharpConstructor con = new CSharpConstructor("ProductionPatternAlternative alt");
        con.addInitializer("base (alt)");
        con.addComment(new CSharpComment(
                "<summary>\n" +
                "Creates a new " + desc.name.substring(2) + " node.\n" +
                "NODE PATTERN: " + desc.pattern.replace("/", "&#47;") + "\n\n" +
                "</summary>\n\n" +
                "<param name='alt'>the alternative to which this node corresponds</param>"));
        cls.addConstructor(con);
        // Add the accessor methods.
        boolean genericAdded = false;
        for (AccessorDescriptor descriptor : accessors) {
            int elemIndicesIndex = accessors.indexOf(descriptor);
            if (!descriptor.repeating) {
                // Add the property
                CSharpProperty prop = new CSharpProperty(CSharpProperty.PUBLIC, descriptor.methodName, descriptor.typeName);
                prop.addGetCode("return (" + descriptor.typeName + ") GetChildAt(elementIndices[" + elemIndicesIndex + "]);");
                prop.addComment(new CSharpComment(descriptor.comment.replace("/", "&#47;").replace("\n","<br />\n")));
                cls.addProperty(prop);
            } else {
                // Add the using, if necessary
                if (!genericAdded) {
                    f.addUsing(new CSharpUsing("System.Collections.Generic"));
                    genericAdded = true;
                }

                // Add the get(index) method
                CSharpMethod method = new CSharpMethod(CSharpProperty.PUBLIC, descriptor.methodName, "int index", descriptor.typeName);
                method.addCode("if (elementIndices[" + elemIndicesIndex + "] + index < elementIndices[" + (elemIndicesIndex + 1) + "]) {");
                method.addCode("    return (" + descriptor.typeName + ") GetChildAt(elementIndices[" + elemIndicesIndex + " + index]);");
                method.addCode("} else {");
                method.addCode("    return null;");
                method.addCode("}");
                method.addComment(new CSharpComment(descriptor.comment.replace("/", "&#47;").replace("\n","<br />\n")));
                cls.addMethod(method);

                // Add the getList property
                CSharpProperty prop = new CSharpProperty(CSharpProperty.PUBLIC, descriptor.methodName + "List", "List<" + descriptor.typeName + ">");
                prop.addGetCode("List<" + descriptor.typeName + "> ret = new List<" + descriptor.typeName + ">();");
                prop.addGetCode("for (int i = elementIndices[" + elemIndicesIndex + "]; i < elementIndices[" + (elemIndicesIndex + 1) + "]; i++) {");
                prop.addGetCode("    ret.Add(( "+ descriptor.typeName + ") GetChildAt(i));");
                prop.addGetCode("}");
                prop.addGetCode("return ret;");
                prop.addComment(new CSharpComment(descriptor.comment2.replace("/", "&#47;").replace("\n","<br />\n")));
                cls.addProperty(prop);
            }
        }

        // Add the class and namespace and return the new file
        if (((CSharpParserGenerator)gen).getNamespace() == null) {
            f.addClass(cls);
        } else {
            CSharpNamespace n = new CSharpNamespace(((CSharpParserGenerator)gen).getNamespace() + ".Nodes");
            n.addClass(cls);
            f.addNamespace(n);
        }
        return f;
    }
}