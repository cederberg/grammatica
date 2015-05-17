/*
 * ProcessingElement.java
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

package net.percederberg.grammatica.ant;

import net.percederberg.grammatica.Grammar;

/**
 * An grammar processing element. A processing element transforms the
 * grammar to some other form, normally source code. One or more
 * processing elements may be present in the Grammatica Ant task.
 *
 * @author   Per Cederberg
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
