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

import net.percederberg.grammatica.code.CodeFile;

/**
 * A class generating a Java code file.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.6
 */
public class JavaFile extends CodeFile {

    /**
     * Creates a new Java code file in the specified file.
     *
     * @param basedir        the base output directory
     * @param basename       the base file name (without extension)
     */
    public JavaFile(File basedir, String basename) {
        super(basedir, basename, ".java");
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
        addElement(cls);
    }

    /**
     * Adds an interface to the file.
     *
     * @param ifc            the interface to add
     */
    public void addInterface(JavaInterface ifc) {
        addElement(ifc);
    }

    /**
     * Adds a package to the file.
     *
     * @param pkg            the package to add
     */
    public void addPackage(JavaPackage pkg) {
        addElement(pkg);
    }
}