/*
 * ProcessingElement.java
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

package net.percederberg.grammatica.ant;

import net.percederberg.grammatica.Grammar;

/**
 * An grammar processing element. A processing element transforms the
 * grammar to some other form, normally source code. One or more
 * processing elements may be present in the Grammatica Ant task.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.4
 * @since    1.4
 */
public interface ProcessingElement {

    /**
     * Validates all attributes in the element.
     *
     * @throws RuntimeException if some attribute was missing or had an
     *             invalid value
     */
    void validate() throws RuntimeException;

    /**
     * Proceses the specified grammar.
     *
     * @param grammar        the grammar to process
     *
     * @throws RuntimeException if the grammar couldn't be processed
     *             correctly
     */
    void process(Grammar grammar) throws RuntimeException;
}
