/*
 * CSharpComment.java
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

package net.percederberg.grammatica.code.csharp;

import java.io.PrintWriter;
import net.percederberg.grammatica.code.CodeElement;
import net.percederberg.grammatica.code.CodeStyle;

/**
 * A class generating a C# comment.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
public class CSharpComment extends CodeElement {

    /**
     * The block documentation comment type.
     */
    public static final int DOCUMENTATION = 0;

    /**
     * The single line documentation comment type. Note that this type
     * may be used even if the comment spans several lines, as the
     * /// characters will be duplicated for each line.
     */
    public static final int DOCUMENTATION_SINGLE = 1;

    /**
     * The block comment type.
     */
    public static final int BLOCK = 2;

    /**
     * The single line comment type. Note that this type may be used
     * even if the comment spans several lines, as the // characters
     * will be duplicated for each line.
     */
    public static final int SINGLELINE = 3;

    /**
     * The comment type.
     */
    private int type;

    /**
     * The comment text.
     */
    private String comment;

    /**
     * Creates a new documentation comment with no indentation.
     *
     * @param comment        the comment text
     */
    public CSharpComment(String comment) {
        this(DOCUMENTATION, comment);
    }

    /**
     * Creates a new comment of the specified type.
     *
     * @param type           the comment type
     * @param comment        the comment text
     *
     * @see #DOCUMENTATION
     * @see #BLOCK
     * @see #SINGLELINE
     */
    public CSharpComment(int type, String comment) {
        if (DOCUMENTATION <= type && type <= SINGLELINE) {
            this.type = type;
        } else {
            this.type = DOCUMENTATION;
        }
        this.comment = comment;
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
        return 0;
    }

    /**
     * Prints the comment to the specified stream.
     *
     * @param out            the output stream
     * @param style          the code style to use
     * @param indent         the indentation level
     */
    public void print(PrintWriter out, CodeStyle style, int indent) {

        String  indentStr = style.getIndent(indent);
        String  firstLine;
        String  restLines;
        int     pos;

        // Comment head
        if (type == DOCUMENTATION) {
            out.println(indentStr + "/**");
        } else if (type == BLOCK) {
            out.println(indentStr + "/*");
        }

        // Comment body
        restLines = comment;
        while ((pos = restLines.indexOf('\n')) >= 0) {
            firstLine = restLines.substring(0, pos);
            restLines = restLines.substring(pos + 1);
            printLine(out, indentStr, firstLine);
        }
        printLine(out, indentStr, restLines);

        // Comment tail
        if (type == DOCUMENTATION || type == BLOCK) {
            out.println(indentStr + " */");
        }
    }

    /**
     * Prints a single comment line.
     *
     * @param out            the output stream
     * @param indent         the indentation string
     * @param line           the comment line to print
     */
    private void printLine(PrintWriter out, String indent, String line) {
        if (type == DOCUMENTATION || type == BLOCK) {
            out.print(indent + " *");
        } else if (type == DOCUMENTATION_SINGLE) {
            out.print(indent + "///");
        } else {
            out.print(indent + "//");
        }
        if (line.equals("")) {
            out.println();
        } else {
            out.println(" " + line);
        }
    }
}
