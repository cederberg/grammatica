/*
 * JavaImport.java
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

import java.io.PrintWriter;

import net.percederberg.grammatica.code.CodeElement;
import net.percederberg.grammatica.code.CodeStyle;

/**
 * A class generating a Java import declaration.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
public class JavaImport extends CodeElement {

    /**
     * The imported package name.
     */
    private String packageName;

    /**
     * The imported class name.
     */
    private String className;

    /**
     * Creates a new import declaration, importing all classes of a
     * specified package.
     *
     * @param pkg             the package to import
     */
    public JavaImport(JavaPackage pkg) {
        this(pkg.toString());
    }

    /**
     * Creates a new import declaration, importing all classes of a
     * specified package.
     *
     * @param packageName     the fully qualified package name
     */
    public JavaImport(String packageName) {
        this(packageName, "");
    }

    /**
     * Creates a new import declaration, importing the selected class
     * from the specified package.
     *
     * @param pkg           the package containing the specified class
     * @param cls           the class to import
     */
    public JavaImport(JavaPackage pkg, JavaClass cls) {
        this(pkg.toString(), cls.toString());
    }

    /**
     * Creates a new import declaration, importing the selected class
     * from the specified package.
     *
     * @param packageName     the fully qualified package name
     * @param className       the class name
     */
    public JavaImport(String packageName, String className) {
        this.packageName = packageName;
        this.className = className;
    }

    /**
     * Compares this object to another one. The comparison is based
     * primarily on the code element category, and secondarily on the
     * package name.
     *
     * @param obj            the object to compare to
     *
     * @return negative if this object preceeds the other one,
     *         zero (0) if the objects are equal, or
     *         positive if this object succeeds the other one
     */
    public int compareTo(Object obj) {
        int  value = super.compareTo(obj);

        if (value == 0) {
            return toString().compareTo(obj.toString());
        } else {
            return value;
        }
    }

    /**
     * Returns true if this object is equal to another import.
     *
     * @param obj       the object to compare to
     *
     * @return true if the objects are equal, or
     *         false otherwise
     */
    public boolean equals(Object obj) {
        return compareTo(obj) == 0;
    }

    /**
     * Returns a string description of the imported packages.
     *
     * @return the string representation of this import
     */
    public String toString() {
        if (className.equals("")) {
            return packageName + ".*";
        } else {
            return packageName + "." + className;
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
        return packageName.startsWith("java") ? 2 : 3;
    }

    /**
     * Prints the code element to the specified output stream.
     *
     * @param out            the output stream
     * @param style          the code style to use
     * @param indent         the indentation level
     */
    public void print(PrintWriter out, CodeStyle style, int indent) {
        out.println("import " + toString() + ";");
    }
}
