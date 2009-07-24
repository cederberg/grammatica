/*
 * CodeFile.java
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

package net.percederberg.grammatica.code;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This class is an abstract base class for those responsible for
 * handling code file creation.
 *
 * @author   Connor Prussin, <cprussin at vt dot edu>
 * @version  1.0
 * @since    1.6
 */
public abstract class CodeFile extends CodeElementContainer {

    /**
     * The file to write to.
     */
    File file;

    /**
     * Creates a new source code file
     *
     * @param basedir        the base output directory
     * @param basename       the base file name (without extension)
     * @param extension      the extension to use
     */
    protected CodeFile(File basedir, String basename, String extension) {
        this.file = new File(basedir, basename + extension);
    }

    /**
     * Returns the file name.
     *
     * @return the file name.
     */
    @Override
    public String toString() {
        return file.getName();
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