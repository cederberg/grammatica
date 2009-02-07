/*
 * ParserGenerator.java
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

import java.io.File;
import java.io.IOException;

import net.percederberg.grammatica.Grammar;

/**
 * The grammar parser generator base class.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
public abstract class ParserGenerator {

    /**
     * The default file comment string.
     */
    protected static final String FILE_COMMENT =
        "THIS FILE HAS BEEN GENERATED AUTOMATICALLY. DO NOT EDIT!";

    /**
     * The grammar to generate a parser for.
     */
    private Grammar grammar;

    /**
     * The base directory where to write the files.
     */
    private File baseDir = null;

    /**
     * The file comment.
     */
    private String fileComment = null;

    /**
     * Creates a new parser generator.
     *
     * @param grammar        the grammar to use
     */
    protected ParserGenerator(Grammar grammar) {
        this.grammar = grammar;
        initialize();
    }

    /**
     * Initializes various instance variables.
     */
    private void initialize() {
        StringBuffer  buffer;
        String        str;
        int           pos;

        // Create file comment
        buffer = new StringBuffer();
        buffer.append(FILE_COMMENT);
        str = grammar.getDeclaration(Grammar.LICENSE_DECLARATION);
        if (str != null) {
            buffer.append("\n\n");
            pos = str.indexOf('\n');
            while (pos >= 0) {
                buffer.append(str.substring(0, pos).trim());
                buffer.append('\n');
                str = str.substring(pos + 1);
                pos = str.indexOf('\n');
            }
            buffer.append(str.trim());
        }
        str = grammar.getDeclaration(Grammar.COPYRIGHT_DECLARATION);
        if (str != null) {
            buffer.append("\n\n");
            buffer.append(str);
        }
        fileComment = buffer.toString();
    }

    /**
     * Returns the grammar that this parser generator works on.
     *
     * @return the parser generator grammar
     */
    public Grammar getGrammar() {
        return grammar;
    }

    /**
     * Returns the base directory where files will be created.
     *
     * @return the base directory
     */
    public File getBaseDir() {
        return baseDir;
    }

    /**
     * Sets the base directory where files will be created.
     *
     * @param dir            the base directory
     */
    public void setBaseDir(File dir) {
        this.baseDir = dir;
    }

    /**
     * Returns the file comment.
     *
     * @return the file comment
     */
    public String getFileComment() {
        return fileComment;
    }

    /**
     * Writes the source code files.
     *
     * @throws IOException if the files couldn't be written correctly
     */
    public abstract void write() throws IOException;
}
