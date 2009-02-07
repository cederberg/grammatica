/*
 * DebugRegExp.java
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

package net.percederberg.grammatica.parser.re;

import java.io.FileReader;

/**
 * A test program for the RegExp class.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
public class DebugRegExp {

    /**
     * The application entry point.
     *
     * @param args           the command-line parameters
     */
    public static void main(String[] args) {
        char[]      buffer = new char[1024];
        FileReader  in;
        int         length;
        String      str = "";

        // Check command-line arguments
        if (args.length != 1) {
            System.err.println("Syntax: DebugRegExp <regexpfile>");
            System.exit(1);
        }

        // Read file contents
        try {
            in = new FileReader(args[0]);
            length = in.read(buffer);
            str = new String(buffer, 0, length);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Create regular expression
        try {
            System.out.println(new RegExp(str, false));
        } catch (RegExpException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
