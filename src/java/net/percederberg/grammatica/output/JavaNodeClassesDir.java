/*
 * JavaNodeClassesDir.java
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

import java.util.ArrayList;
import net.percederberg.grammatica.code.CodeFile;
import net.percederberg.grammatica.code.java.JavaClass;
import net.percederberg.grammatica.code.java.JavaComment;
import net.percederberg.grammatica.code.java.JavaConstructor;
import net.percederberg.grammatica.code.java.JavaFile;
import net.percederberg.grammatica.code.java.JavaImport;
import net.percederberg.grammatica.code.java.JavaInterface;
import net.percederberg.grammatica.code.java.JavaMethod;
import net.percederberg.grammatica.code.java.JavaPackage;

/**
 * This class handles the node specification in Java.
 *
 * @author   Connor Prussin, <cprussin at vt dot edu>
 * @version  1.0
 * @since    1.6
 */
class JavaNodeClassesDir extends NodeClassesDir {

    /**
     * Basic constructor
     *
     * @param gen               The JavaParserGenerator to use.
     * @param dir               The name of the directory within the rest of
     *                          the output to put the files.  This should only
     *                          be nonnull if the package is set.
     */
    JavaNodeClassesDir(JavaParserGenerator gen) {
        super(gen, gen.getBasePackage() != null ? "nodes" : null);
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
        // Build the file, add a comment, add imports, and set the package.
        JavaFile f = new JavaFile(this, desc.name);
        f.addComment(new JavaComment(JavaComment.BLOCK,
                f.toString() + "\n\n" + gen.getFileComment()));
        f.addImport(new JavaImport("net.percederberg.grammatica.parser",
                                   "Token"));
        f.addImport(new JavaImport("net.percederberg.grammatica.parser",
                                   "TokenPattern"));
        if (((JavaParserGenerator)gen).getBasePackage() != null) {
            f.addPackage(new JavaPackage(((JavaParserGenerator)gen).getBasePackage() + ".nodes"));
        }

        // Add the class with a comment and a constructor.
        String[] implList = new String[desc.inherits.size()];
        for (int i = 0; i < desc.inherits.size(); i++) {
            implList[i] = desc.inherits.get(i).name;
        }
        JavaClass cls = new JavaClass(JavaClass.PUBLIC, desc.name, "Token", implList);
        f.addClass(cls);
        String comment = desc.comment.replace("/", "&#47;").replace("\n", "<br />\n");
        if (((JavaParserGenerator)gen).getClassComment() != null) {
            comment += "\n\n" + ((JavaParserGenerator)gen).getClassComment();
        }
        cls.addComment(new JavaComment(comment));
        JavaConstructor con = new JavaConstructor("TokenPattern pattern, String image, int line, int col");
        con.addCode("super(pattern, image, line, col);");
        cls.addConstructor(con);

        // Add a constructor comment
        con.addComment(new JavaComment(
                "Creates a new " + desc.name.substring(2) + " node.<br />\n" +
                "NODE PATTERN: \"" + desc.tok.getPattern().replace("/", "&#47;") + "\"\n\n" +
                "@param pattern        the token pattern\n" +
                "@param image          the token image (i.e. characters)\n" +
                "@param line           the line number of the first character\n" +
                "@param col            the column number of the first character"));

        // Return the new file
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
        // Build the file, add a comment, and set the package.
        JavaFile f = new JavaFile(this, desc.name);
        f.addComment(new JavaComment(JavaComment.BLOCK,
                f.toString() + "\n\n" + gen.getFileComment()));
        if (((JavaParserGenerator)gen).getBasePackage() != null) {
            f.addPackage(new JavaPackage(((JavaParserGenerator)gen).getBasePackage() + ".nodes"));
        }

        // Add the class with a comment and a constructor.
        JavaInterface ifc = new JavaInterface(desc.name);
        f.addInterface(ifc);
        String comment = desc.comment.replace("/", "&#47;").replace("\n", "<br />\n");
        if (((JavaParserGenerator)gen).getClassComment() != null) {
            comment += "\n\n" + ((JavaParserGenerator)gen).getClassComment();
        }
        ifc.addComment(new JavaComment(comment));

        // Return the new file
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
        // Build the file, add a comment, add imports, and set the package.
        JavaFile f = new JavaFile(this, desc.name);
        f.addComment(new JavaComment(JavaComment.BLOCK,
                f.toString() + "\n\n" + gen.getFileComment()));
        f.addImport(new JavaImport("net.percederberg.grammatica.parser",
                                   "ProductionPatternAlternative"));
        f.addImport(new JavaImport("net.percederberg.grammatica.parser",
                                   "SpecializedProduction"));
        if (((JavaParserGenerator)gen).getBasePackage() != null) {
            f.addPackage(new JavaPackage(((JavaParserGenerator)gen).getBasePackage() + ".nodes"));
        }

        // Add the class with a comment and a constructor.
        String[] implList = new String[desc.inherits.size()];
        for (int i = 0; i < desc.inherits.size(); i++) {
            implList[i] = desc.inherits.get(i).name;
        }
        JavaClass cls = new JavaClass(JavaClass.PUBLIC, desc.name, "SpecializedProduction", implList);
        f.addClass(cls);
        String comment = desc.comment.replace("/", "&#47;").replace("\n", "<br />\n");
        if (((JavaParserGenerator)gen).getClassComment() != null) {
            comment += "\n\n" + ((JavaParserGenerator)gen).getClassComment();
        }
        cls.addComment(new JavaComment(comment));
        JavaConstructor con = new JavaConstructor("ProductionPatternAlternative alt");
        con.addCode("super(alt);");
        cls.addConstructor(con);

        // Add a constructor comment
        con.addComment(new JavaComment(
                "Creates a new " + desc.name.substring(2) + " node.<br />\n" +
                "NODE PATTERN: " + desc.pattern.replace("/", "&#47;") + "\n\n" +
                "@param alt            the alternative to which this node corresponds"));

        // Add the accessor methods.
        boolean arrayListAdded = false;
        for (AccessorDescriptor descriptor : accessors) {
            int elemIndicesIndex = accessors.indexOf(descriptor);
            if (!descriptor.repeating) {
                // Add the get() method
                JavaMethod method = new JavaMethod(JavaMethod.PUBLIC, descriptor.methodName, "", descriptor.typeName);
                method.addCode("return (" + descriptor.typeName + ") getChildAt(elementIndices.get(" + elemIndicesIndex + "));");
                method.addComment(new JavaComment(descriptor.comment.replace("/", "&#47;").replace("\n","<br />\n")));
                cls.addMethod(method);
            } else {
                // Add the import, if necessary
                if (!arrayListAdded) {
                    f.addImport(new JavaImport("java.util", "ArrayList"));
                    arrayListAdded = true;
                }

                // Add the get(index) method
                JavaMethod method = new JavaMethod(JavaMethod.PUBLIC, descriptor.methodName, "int index", descriptor.typeName);
                method.addCode("if (elementIndices.get(" + elemIndicesIndex + ") + index < elementIndices.get(" + (elemIndicesIndex + 1) + ")) {");
                method.addCode("    return (" + descriptor.typeName + ") getChildAt(elementIndices.get(" + elemIndicesIndex + " + index));");
                method.addCode("} else {");
                method.addCode("    return null;");
                method.addCode("}");
                method.addComment(new JavaComment(descriptor.comment.replace("/", "&#47;").replace("\n","<br />\n")));
                cls.addMethod(method);

                // Add the getList() method
                method = new JavaMethod(JavaMethod.PUBLIC, descriptor.methodName + "List", "", "ArrayList<" + descriptor.typeName + ">");
                method.addCode("ArrayList<" + descriptor.typeName + "> ret = new ArrayList<" + descriptor.typeName + ">(elementIndices.get(" + (elemIndicesIndex + 1) + ") - elementIndices.get(" + elemIndicesIndex + "));");
                method.addCode("for (int i = elementIndices.get(" + elemIndicesIndex + "); i < elementIndices.get(" + (elemIndicesIndex + 1) + "); i++) {");
                method.addCode("    ret.add(i, ( "+ descriptor.typeName + ") getChildAt(i));");
                method.addCode("}");
                method.addCode("return ret;");
                method.addComment(new JavaComment(descriptor.comment2.replace("/", "&#47;").replace("\n","<br />\n")));
                cls.addMethod(method);
            }
        }

        // Add the new class and return the new file
        f.addClass(cls);
        return f;
    }
}