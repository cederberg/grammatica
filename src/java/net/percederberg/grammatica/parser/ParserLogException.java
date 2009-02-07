/*
 * ParserLogException.java
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
