/*
 * JavaPackage.java
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
 * Copyright (c) 2003 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.code.java;

import java.io.File;
import java.io.PrintWriter;

import net.percederberg.grammatica.code.CodeElement;
import net.percederberg.grammatica.code.CodeStyle;

/**
 * A class generating a Java package declaration.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
public class JavaPackage extends CodeElement {

    /**
     * The base package.
     */
    private JavaPackage basePackage;

    /**
     * The package name.
     */
    private String name;

    /**
     * Creates a new Java package with the specified name.
     *
     * @param name     the package name (including dots '.')
     */
    public JavaPackage(String name) {
        this(null, name);
    }

    /**
     * Creates a new Java package with the specified base package and name.
     *
     * @param base     the base package
     * @param name     the package name (including dots '.')
     */
    public JavaPackage(JavaPackage base, String name) {
        this.basePackage = base;
        this.name = name;
    }

    /**
     * Returns a string representation of this package.
     *
     * @return a string representation of this package
     */
    public String toString() {
        if (basePackage == null) {
            return name;
        } else {
            return basePackage.toString() + "." + name;
        }
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
        return 1;
    }

    /**
     * Returns the directory file containing the package files.
     *
     * @param baseDir    the base output directory
     *
     * @return the package directory
     */
    public File toFile(File baseDir) {
        String   firstName;
        String   restName;
        int      pos;

        if (basePackage != null) {
            baseDir = basePackage.toFile(baseDir);
        }
        restName = this.name;
        while ((pos = restName.indexOf('.')) > 0) {
            firstName = restName.substring(0, pos);
            restName = restName.substring(pos + 1);
            baseDir = new File(baseDir, firstName);
        }
        return new File(baseDir, restName);
    }

    /**
     * Prints the code element to the specified output stream.
     *
     * @param out            the output stream
     * @param style          the code style to use
     * @param indent         the indentation level
     */
    public void print(PrintWriter out, CodeStyle style, int indent) {
        out.println("package " + toString() + ";");
    }
}
