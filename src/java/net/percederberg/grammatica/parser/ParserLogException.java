/*
 * ParserLogException.java
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

package net.percederberg.grammatica.parser;

import java.util.ArrayList;

/**
 * A parser log exception. This class contains a list of all the parse
 * errors encountered while parsing.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.1
 * @since    1.1
 */
public class ParserLogException extends Exception {

    /**
     * The list of errors found.
     */
    private ArrayList errors = new ArrayList();

    /**
     * Creates a new empty parser log exception.
     */
    public ParserLogException() {
    }

    /**
     * Returns the number of errors in this log.
     *
     * @return the number of errors in this log
     */
    public int getErrorCount() {
        return errors.size();
    }

    /**
     * Returns a specific error from the log.
     *
     * @param index          the error index, 0 <= index < count
     *
     * @return the parse error requested
     */
    public ParseException getError(int index) {
        return (ParseException) errors.get(index);
    }

    /**
     * Adds a parse error to the log.
     *
     * @param e              the parse error to add
     */
    public void addError(ParseException e) {
        errors.add(e);
    }

    /**
     * Returns the detailed error message. This message will contain
     * the error messages from all errors in this log, separated by
     * a newline.
     *
     * @return the detailed error message
     */
    public String getMessage() {
        StringBuffer  buffer = new StringBuffer();

        for (int i = 0; i < getErrorCount(); i++) {
            if (i > 0) {
                buffer.append("\n");
            }
            buffer.append(getError(i).getMessage());
        }
        return buffer.toString();
    }
}
