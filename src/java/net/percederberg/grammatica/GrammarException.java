/*
 * GrammarException.java
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

package net.percederberg.grammatica;

/**
 * A grammar validation exception. This exception is used for
 * signalling an error in the grammar file.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
public class GrammarException extends Exception {

    /**
     * The grammar file name.
     */
    private String file;

    /**
     * The detailed error message.
     */
    private String message;

    /**
     * The first error line, or -1 for unknown.
     */
    private int startLine;

    /**
     * The last error line, or -1 for unknown.
     */
    private int endLine;

    /**
     * Creates a new grammar exception.
     *
     * @param file           the grammar file name
     * @param message        the detailed error message
     */
    public GrammarException(String file, String message) {
        this(file, message, -1, -1);
    }

    /**
     * Creates a new grammar exception.
     *
     * @param file           the grammar file name
     * @param message        the detailed error message
     * @param startLine      the starting line number, or -1 for unknown
     * @param endLine        the ending line number, or -1 for unknown
     */
    public GrammarException(String file,
                            String message,
                            int startLine,
                            int endLine) {

        this.file = file;
        this.message = message;
        this.startLine = startLine;
        this.endLine = endLine;
    }

    /**
     * Returns the grammar file name.
     *
     * @return the grammar file name
     */
    public String getFile() {
        return file;
    }

    /**
     * Returns the start line number for the error.
     *
     * @return the starting line number, or
     *         -1 if unknown
     */
    public int getStartLine() {
        return startLine;
    }

    /**
     * Returns the end line number for the error.
     *
     * @return the ending line number, or
     *         -1 if unknown
     */
    public int getEndLine() {
        return endLine;
    }

    /**
     * Returns the detailed error message. This message will contain
     * the same string as getErrorMessage(), but with line number
     * information appended.
     *
     * @return the detailed error message
     */
    public String getMessage() {
        StringBuffer  buffer = new StringBuffer();

        // Add error description
        buffer.append(getErrorMessage());

        // Add line numbers
        if (startLine > 0 && endLine > 0) {
            if (startLine == endLine) {
                buffer.append(", on line ");
                buffer.append(startLine);
            } else {
                buffer.append(", on lines ");
                buffer.append(startLine);
                buffer.append("-");
                buffer.append(endLine);
            }
        }

        return buffer.toString();
    }

    /**
     * Returns the error message.
     *
     * @return the error message.
     */
    public String getErrorMessage() {
        return message;
    }
}
