/*
 * TreePrinter.java
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

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import net.percederberg.grammatica.parser.Analyzer;
import net.percederberg.grammatica.parser.Node;

/**
 * A parse tree printer. This class prints the parse tree while it is
 * being parsed.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.4
 * @since    1.4
 */
public class TreePrinter extends Analyzer {

    /**
     * The current indentation level.
     */
    private int indentation = 0;

    /**
     * The output stream to use.
     */
    private PrintWriter output;

    /**
     * Creates a new parse tree printer.
     *
     * @param output         the output stream to use
     */
    public TreePrinter(OutputStream output) {
        this(new PrintWriter(output));
    }

    /**
     * Creates a new parse tree printer.
     *
     * @param output         the output stream to use
     */
    public TreePrinter(Writer output) {
        if (output instanceof PrintWriter) {
            this.output = (PrintWriter) output;
        } else {
            this.output = new PrintWriter(output);
        }
    }

    /**
     * Called when entering a parse tree node. By default this method
     * does nothing. A subclass can override this method to handle
     * each node separately.
     *
     * @param node           the node being entered
     */
    protected void enter(Node node) {
        for (int i = 0; i < indentation; i++) {
            output.print("  ");
        }
        output.println(node.toString());
        output.flush();
        indentation++;
    }

    /**
     * Called when exiting a parse tree node. By default this method
     * returns the node. A subclass can override this method to handle
     * each node separately. If no parse tree should be created, this
     * method should return null.
     *
     * @param node           the node being exited
     *
     * @return the node to add to the parse tree, or
     *         null to remove the node
     */
    protected Node exit(Node node) {
        indentation--;
        return null;
    }
}
