/*
 * JavaParserGenerator.java
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
import net.percederberg.grammatica.code.java.JavaFile;
import net.percederberg.grammatica.code.java.JavaPackage;
import net.percederberg.grammatica.parser.ProductionPattern;
import net.percederberg.grammatica.parser.TokenPattern;

/**
 * A Java parser generator. This class generates the source code files
 * needed for a Java parser.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
public class JavaParserGenerator extends ParserGenerator {

    /**
     * The fully qualified Java package name.
     */
    private String basePackage = null;

    /**
     * The Java class name prefix.
     */
    private String baseName = null;

    /**
     * The public class and interface access flag.
     */
    private boolean publicAccess = false;

    /**
     * The Java class comment.
     */
    private String classComment = null;

    /**
     * Creates a new Java parser generator.
     *
     * @param grammar        the grammar to use
     */
    public JavaParserGenerator(Grammar grammar) {
        super(grammar);
        initialize();
    }

    /**
     * Initializes various instance variables.
     */
    private void initialize() {
        StringBuffer  buffer;
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

        // Create class comment
        buffer = new StringBuffer();
        str = getGrammar().getDeclaration(Grammar.AUTHOR_DECLARATION);
        if (str != null) {
            buffer.append("@author   ");
            buffer.append(str);
        }
        str = getGrammar().getDeclaration(Grammar.VERSION_DECLARATION);
        if (str != null) {
            if (buffer.length() > 0) {
                buffer.append("\n");
            }
            buffer.append("@version  ");
            buffer.append(str);
        }
        classComment = buffer.toString();
    }

    /**
     * Returns the Java package where the classes will be created.
     *
     * @return the fully qualified Java package name
     */
    public String getBasePackage() {
        return basePackage;
    }

    /**
     * Sets the Java package name where the classes will be created.
     *
     * @param pkg            the fully qualified package name
     */
    public void setBasePackage(String pkg) {
        this.basePackage = pkg;
    }

    /**
     * Returns the Java class name prefix.
     *
     * @return the Java class name prefix
     */
    public String getBaseName() {
        return baseName;
    }

    /**
     * Sets the Java class name prefix.
     *
     * @param name           the Java class name prefix
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
     * Returns the Java code style to use.
     *
     * @return the Java code style to use
     */
    public CodeStyle getCodeStyle() {
        return CodeStyle.JAVA;
    }

    /**
     * Returns the Java class comment.
     *
     * @return the Java class comment
     */
    public String getClassComment() {
        return classComment;
    }

    /**
     * Writes the Java source code files.
     *
     * @throws IOException if the files couldn't be written correctly
     */
    public void write() throws IOException {
        Grammar            grammar = getGrammar();
        JavaConstantsFile  constants = new JavaConstantsFile(this);
        JavaTokenizerFile  tokenizer = new JavaTokenizerFile(this);
        JavaAnalyzerFile   analyzer = new JavaAnalyzerFile(this);
        JavaParserFile     parser = new JavaParserFile(this, tokenizer, analyzer);
        TokenPattern       token;
        ProductionPattern  production;
        int                i;

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

        // Create production definitions
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

    /**
     * Creates a Java file in the correct base directory. The package
     * will be set if applicable.
     *
     * @return a new Java file
     */
    public JavaFile createJavaFile() {
        if (basePackage == null) {
            return new JavaFile(getBaseDir());
        } else {
            return new JavaFile(getBaseDir(),
                                new JavaPackage(getBasePackage()));
        }
    }
}
