/*
 * VisualBasicFile.java
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
 * Copyright (c) 2004 Adrian Moore. All rights reserved.
 * Copyright (c) 2004 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.code.visualbasic;

import java.io.File;

import net.percederberg.grammatica.code.CodeFile;

/**
 * A class generating a Visual Basic source code file.
 *
 * @author   Adrian Moore, <adrianrob at hotmail dot com>
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.6
 * @since    1.5
 */
public class VisualBasicFile extends CodeFile {

    /**
     * Creates a new Visual Basic source code file.
     *
     * @param basedir        the base output directory
     * @param basename       the base file name (without extension)
     */
    public VisualBasicFile(File basedir, String basename) {
        super(basedir, basename, ".vb");
    }

    /**
     * Adds a file comment.
     *
     * @param comment        the file comment to add
     */
    public void addComment(VisualBasicComment comment) {
        addElement(comment);
    }

    /**
     * Adds an imports declaration to the file.
     *
     * @param imports         the imports declaration to add
     */
    public void addImports(VisualBasicImports imports) {
        addElement(imports);
    }

    /**
     * Adds a namespace declaration to the file.
     *
     * @param namespace       the namespace declaration to add
     */
    public void addNamespace(VisualBasicNamespace namespace) {
        addElement(namespace);
    }

    /**
     * Adds a class declaration to the file.
     *
     * @param c              the class declaration to add
     */
    public void addClass(VisualBasicClass c) {
        addElement(c);
    }

    /**
     * Adds an enumeration declaration to the file.
     *
     * @param e              the enumeration to add
     */
    public void addEnumeration(VisualBasicEnumeration e) {
        addElement(e);
    }
}