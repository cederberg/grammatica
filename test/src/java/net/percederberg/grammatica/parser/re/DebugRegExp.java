/*
 * DebugRegExp.java
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

package net.percederberg.grammatica.parser.re;

import java.io.FileReader;

/**
 * A test program for the RegExp class.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
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
            System.out.println(new RegExp(str));
        } catch (RegExpException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
