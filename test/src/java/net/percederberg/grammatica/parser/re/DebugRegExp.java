/*
 * DebugRegExp.java
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the BSD license.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * LICENSE.txt file for more details.
 *
 * Copyright (c) 2003-2015 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.parser.re;

import java.io.FileReader;

/**
 * A test program for the RegExp class.
 *
 * @author   Per Cederberg
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
