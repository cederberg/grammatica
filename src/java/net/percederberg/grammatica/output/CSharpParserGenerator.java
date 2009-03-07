/*
 * CSharpParserGenerator.java
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
 * Copyright (c) 2003-2009 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.output;

import java.io.IOException;

import net.percederberg.grammatica.Grammar;
import net.percederberg.grammatica.code.CodeStyle;
import net.percederberg.grammatica.parser.ProductionPattern;
import net.percederberg.grammatica.parser.TokenPattern;

/**
 * A C# parser generator. This class generates the source code files
 * needed for a C# parser.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
public class CSharpParserGenerator extends ParserGenerator {

    /**
     * The class name prefix.
     */
    private String baseName = null;

    /**
     * The namespace to use.
     */
    private String namespace = null;

    /**
     * The public class and interface access flag.
     */
    private boolean publicAccess = false;

    /**
     * Creates a new parser generator.
     *
     * @param grammar        the grammar to use
     */
    public CSharpParserGenerator(Grammar grammar) {
        super(grammar);
        initialize();
    }

    /**
     * Initializes various instance variables.
     */
    private void initialize() {
        String        str;

        // Set base name
        str = getGrammar().getFileName();
        if (str.indexOf('/') >= 0) {
            str = str.substring(str.lastIndexOf('/') + 1);
        }
        if (str.indexOf('\\') >= 0) {
            str = str.substring(str.lastIndexOf('\\') + 1);
        }
        if (str.indexOf('.') > 0) {
            str = str.substring(0, str.indexOf('.'));
        }
        if (Character.isLowerCase(str.charAt(0))) {
            str = Character.toUpperCase(str.charAt(0)) + str.substring(1);
        }
        baseName = str;
    }

    /**
     * Returns the namespace used for the classes.
     *
     * @return the fully qualified namespace, or
     *         null for none
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Sets the namespace to use for the classes.
     *
     * @param namespace      the fully qualified namespace
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Returns the class name prefix.
     *
     * @return the class name prefix
     */
    public String getBaseName() {
        return baseName;
    }

    /**
     * Sets the class name prefix.
     *
     * @param name           the class name prefix
     */
    public void setBaseName(String name) {
        this.baseName = name;
    }

    /**
     * Returns the public access flag.
     *
     * @return true if the classes should have public access, or
     *         false otherwise
     */
    public boolean getPublicAccess() {
        return publicAccess;
    }

    /**
     * Sets the public access flag.
     *
     * @param flag           the new public access flag value
     */
    public void setPublicAccess(boolean flag) {
        publicAccess = flag;
    }

    /**
     * Returns the code style to use.
     *
     * @return the code style to use
     */
    public CodeStyle getCodeStyle() {
        return CodeStyle.CSHARP;
    }

    /**
     * Writes the source code files.
     *
     * @throws IOException if the files couldn't be written correctly
     */
    public void write() throws IOException {
        Grammar              grammar = getGrammar();
        CSharpConstantsFile  constants = new CSharpConstantsFile(this);
        CSharpTokenizerFile  tokenizer = new CSharpTokenizerFile(this);
        CSharpAnalyzerFile   analyzer = new CSharpAnalyzerFile(this);
        CSharpParserFile     parser = new CSharpParserFile(this, tokenizer, analyzer);
        TokenPattern         token;
        ProductionPattern    production;
        int                  i;

        // Create token declarations
        for (i = 0; i < grammar.getTokenPatternCount(); i++) {
            token = grammar.getTokenPattern(i);
            constants.addToken(token);
            tokenizer.addToken(token, constants);
            analyzer.addToken(token, constants);
        }

        // Create production constants
        for (i = 0; i < grammar.getProductionPatternCount(); i++) {
            production = grammar.getProductionPattern(i);
            constants.addProduction(production);
            parser.addProductionConstant(production);
            analyzer.addProduction(production, constants);
        }

        // Create production declarations
        for (i = 0; i < grammar.getProductionPatternCount(); i++) {
            production = grammar.getProductionPattern(i);
            parser.addProduction(production, constants);
        }

        // Write source code files
        constants.writeCode();
        tokenizer.writeCode();
        parser.writeCode();
        analyzer.writeCode();
    }
}
