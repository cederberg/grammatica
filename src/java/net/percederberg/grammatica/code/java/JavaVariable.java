/*
 * JavaVariable.java
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
 * Copyright (c) 2003-2004 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.code.java;

import java.io.PrintWriter;
import java.util.LinkedList;

import net.percederberg.grammatica.code.CodeElement;
import net.percederberg.grammatica.code.CodeStyle;

/**
 * A class generating a Java variable declaration. The variable
 * declaration should be placed as a member in a class.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
public class JavaVariable extends CodeElement {

    /**
     * The public access modifier constant.
     */
    public static final int PUBLIC = JavaModifier.PUBLIC;

    /**
     * The protected access modifier constant.
     */
    public static final int PROTECTED = JavaModifier.PROTECTED;

    /**
     * The package local access modifier constant (i.e. no modifier).
     */
    public static final int PACKAGE_LOCAL = JavaModifier.PACKAGE_LOCAL;

    /**
     * The private access modifier constant.
     */
    public static final int PRIVATE = JavaModifier.PRIVATE;

    /**
     * The static modifier constant.
     */
    public static final int STATIC = JavaModifier.STATIC;

    /**
     * The final modifier constant.
     */
    public static final int FINAL = JavaModifier.FINAL;

    /**
     * The transient modifier constant.
     */
    public static final int TRANSIENT = JavaModifier.TRANSIENT;

    /**
     * The volatile modifier constant.
     */
    public static final int VOLATILE = JavaModifier.VOLATILE;

    /**
     * The variable modifiers.
     */
    private int modifiers;

    /**
     * The variable type.
     */
    private String type;

    /**
     * The variable name.
     */
    private String name;

    /**
     * The variable init value. This initialization value is only used
     * if a single initialization value is added. Otherwise the vector
     * initialization is used.
     *
     * @see #initValueList
     */
    private String initValue;

    /**
     * The list of init values. These initialization values are only
     * used for array initializations, not for single values.
     *
     * @see #initValue
     */
    private LinkedList initValueList;

    /**
     * The variable comment.
     */
    private JavaComment comment;

    /**
     * Creates a new variable with the specified type and name.
     *
     * @param type        the variable type
     * @param name        the variable name
     */
    public JavaVariable(String type, String name) {
        this(PUBLIC, type, name);
    }

    /**
     * Creates a new variable with the specified modifiers, type and
     * name.
     *
     * @param modifiers   the modifier flags to use
     * @param type        the variable type
     * @param name        the variable name
     */
    public JavaVariable(int modifiers, String type, String name) {
        this(modifiers, type, name, null);
    }

    /**
     * Creates a new variable with the specified type, name and
     * initializer.
     *
     * @param type        the variable type
     * @param name        the variable name
     * @param initValue   the initialize value
     */
    public JavaVariable(String type, String name, String initValue) {
        this(PUBLIC, type, name, initValue);
    }

    /**
     * Creates a new variable with the specified modifiers, type, name
     * and initializer.
     *
     * @param modifiers   the modifier flags to use
     * @param type        the variable type
     * @param name        the variable name
     * @param initValue   the initialize value
     */
    public JavaVariable(int modifiers,
                        String type,
                        String name,
                        String initValue) {

        this.modifiers = modifiers;
        this.type = type;
        this.name = name;
        this.initValue = initValue;
        this.initValueList = new LinkedList();
        this.comment = null;
    }

    /**
     * Adds a comment to this variable.
     *
     * @param comment         the comment to add
     */
    public void addComment(JavaComment comment) {
        this.comment = comment;
    }

    /**
     * Adds initialization code for an array element value. Each array
     * element value added will be added last in the list of
     * initialization values. If an init value has been specified with
     * the constructor, it will be added first.
     *
     * @param elementValue    the array element value
     */
    public void addArrayInit(String elementValue) {
        if (this.initValue != null) {
            this.initValueList.add(this.initValue);
            this.initValue = null;
        }
        this.initValueList.add(elementValue);
    }

    /**
     * Returns a numeric category number for the code element. A lower
     * category number implies that the code element should be placed
     * before code elements with a higher category number within a
     * declaration.
     *
     * @return the category number
     */
    public int category() {
        return ((modifiers & STATIC) > 0) ? 4 : 5;
    }

    /**
     * Prints the code element to the specified output stream.
     *
     * @param out            the output stream
     * @param style          the code style to use
     * @param indent         the indentation level
     */
    public void print(PrintWriter out, CodeStyle style, int indent) {
        String indentStr = style.getIndent(indent);
        String prefix = JavaModifier.createModifierDecl(modifiers);
        String init;

        if (comment != null) {
            comment.print(out, style, indent);
        }
        init = getInitCode(style, indent);
        if (init == null) {
            out.println(indentStr + prefix + type + " " + name + ";");
        } else {
            out.println(indentStr + prefix + type + " " + name + " = " +
                        init + ";");
        }
    }

    /**
     * Returns indented initialization code lines. If not init code is
     * available, the method returns null.
     *
     * @param style          the Java code style
     * @param indent         the indentation level
     *
     * @return the indented initialization code
     */
    private String getInitCode(CodeStyle style, int indent) {
        String       indentStr = style.getIndent(indent);
        String       codeIndentStr = style.getIndent(indent + 1);
        StringBuffer res;

        // Check for simple init values
        if (initValueList.size() == 0 && initValue == null) {
            return null;
        } else if (initValue != null) {
            return initValue;
        }

        // Create array of init values
        res = new StringBuffer("{\n");
        for (int i = 0; i < initValueList.size(); i++) {
            res.append(codeIndentStr);
            res.append(initValueList.get(i).toString());
            if (i + 1 < initValueList.size()) {
                res.append(",\n");
            } else {
                res.append("\n");
            }
        }
        res.append(indentStr);
        res.append("}");

        return res.toString();
    }
}
