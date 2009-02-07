/*
 * JavaFile.java
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import net.percederberg.grammatica.code.CodeElement;
import net.percederberg.grammatica.code.CodeElementContainer;
import net.percederberg.grammatica.code.CodeStyle;

/**
 * A class generating a Java code file.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
public class JavaFile extends CodeElementContainer {

    /**
     * The directory to write to.
     */
    private File dir;

    /**
     * The first class or interface added.
     */
    private CodeElement first = null;

    /**
     * Creates a new Java code file in the specified file.
     *
     * @param basedir        the base output directory
     */
    public JavaFile(File basedir) {
        this.dir = basedir;
    }

    /**
     * Creates a new Java code file in the specified base directory
     * and package.  The file name will be retrieved from the first
     * class or interface added to this file.
     *
     * @param basedir        the base output directory
     * @param filePackage    the package the file belongs to
     */
    public JavaFile(File basedir, JavaPackage filePackage) {
        this.dir = filePackage.toFile(basedir);
        addElement(filePackage);
    }

    /**
     * Returns the file name. Note that if no class has been added to
     * the file, a default file name will be returned.
     *
     * @return the file name
     */
    public String toString() {
        if (first == null) {
            return "UnknownFileName.java";
        } else {
            return first.toString() + ".java";
        }
    }

    /**
     * Adds a comment to this file.
     *
     * @param comment        the new file comment
     */
    public void addComment(JavaComment comment) {
        addElement(comment);
    }

    /**
     * Adds an import to the file.
     *
     * @param imp            the import to add
     */
    public void addImport(JavaImport imp) {
        addElement(imp);
    }

    /**
     * Adds a class to the file.
     *
     * @param cls            the class to add
     */
    public void addClass(JavaClass cls) {
        if (first == null) {
            first = cls;
        }
        addElement(cls);
    }

    /**
     * Adds an interface to the file.
     *
     * @param ifc            the interface to add
     */
    public void addInterface(JavaInterface ifc) {
        if (first == null) {
            first = ifc;
        }
        addElement(ifc);
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
     * Writes the source code for this file. Any previous file with
     * this name will be overwritten.
     *
     * @param style          the code style to use
     *
     * @throws IOException if the file could not be written properly
     */
    public void writeCode(CodeStyle style) throws IOException {
        File         file = new File(dir, toString());
        PrintWriter  out;

        createFile(file);
        out = new PrintWriter(new FileWriter(file));
        print(out, style, 0);
        out.close();
    }

    /**
     * Prints the file contents to the specified output stream.
     *
     * @param out            the output stream
     * @param style          the code style to use
     * @param indent         the indentation level
     */
    public void print(PrintWriter out, CodeStyle style, int indent) {
        printContents(out, style, indent);
    }
}
