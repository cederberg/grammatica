/*
 * ValidationElement.java
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import net.percederberg.grammatica.Grammar;
import net.percederberg.grammatica.GrammarException;
import net.percederberg.grammatica.TreePrinter;
import net.percederberg.grammatica.parser.Analyzer;
import net.percederberg.grammatica.parser.Node;
import net.percederberg.grammatica.parser.ParseException;
import net.percederberg.grammatica.parser.Parser;
import net.percederberg.grammatica.parser.ParserCreationException;
import net.percederberg.grammatica.parser.ParserLogException;
import net.percederberg.grammatica.parser.Token;
import net.percederberg.grammatica.parser.Tokenizer;

/**
 * A grammar validation element. This element validates or tests the
 * grammar in various ways.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.4
 * @since    1.4
 */
public class ValidationElement implements ProcessingElement {

    /**
     * The validation type.
     */
    private String type = null;

    /**
     * The input test file.
     */
    private File file = null;

    /**
     * The quiet output flag.
     */
    private boolean quiet = false;

    /**
     * Creates a new validation element.
     */
    public ValidationElement() {
        // Nothing to do here
    }

    /**
     * Sets the validation type. The type must be one of "debug",
     * "tokenize", "parse", or "profile".
     *
     * @param type           the validation type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Sets the input test file. The test file is not needed for the
     * debug validation type.
     *
     * @param file           the input test file
     */
    public void setInputfile(File file) {
        this.file = file;
    }

    /**
     * Sets the quiet output flag.
     *
     * @param quiet          the quiet output flag
     */
    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    /**
     * Validates all attributes in the element.
     *
     * @throws RuntimeException if some attribute was missing or had an
     *             invalid value
     */
    public void validate() throws RuntimeException {
        if (type == null) {
            throw new RuntimeException(
                "missing 'type' attribute in <validate>");
        }
        if (!type.equals("debug")
         && !type.equals("tokenize")
         && !type.equals("parse")
         && !type.equals("profile")) {

            throw new RuntimeException(
                "value of 'type' attribute in <validate> must be one " +
                "of 'debug', 'tokenize', 'parse', or 'profile'");
        }
        if (file == null && !type.equals("debug")) {
            throw new RuntimeException(
                "missing 'inputfile' attribute in <validate>");
        }
    }

    /**
     * Proceses the specified grammar.
     *
     * @param grammar        the grammar to process
     *
     * @throws RuntimeException if the grammar couldn't be processed
     *             correctly
     */
    public void process(Grammar grammar) throws RuntimeException {
        if (type.equals("debug")) {
            debug(grammar);
        } else if (type.equals("tokenize")) {
            tokenize(grammar);
        } else if (type.equals("parse")) {
            parse(grammar);
        } else if (type.equals("profile")) {
            profile(grammar);
        } else {
            throw new RuntimeException("unknown <validation> type: " + type);
        }
    }

    /**
     * Debugs a grammar by printing the internal representation.
     *
     * @param grammar        the grammar to use
     *
     * @throws RuntimeException if a parser couldn't be created
     */
    private void debug(Grammar grammar) throws RuntimeException {
        Tokenizer  tokenizer = null;
        Parser     parser = null;

        // Create tokenizer and parser
        try {
            tokenizer = grammar.createTokenizer(null);
            parser = grammar.createParser(tokenizer);
        } catch (GrammarException e) {
            throw new RuntimeException("in grammar " + grammar.getFileName() +
                                       ": " + e.getMessage());
        }

        // Print tokenizer and parser
        if (!quiet) {
            System.out.println("Contents of " + grammar.getFileName() + ":");
            System.out.println();
            System.out.println("Token Declarations:");
            System.out.println("-------------------");
            System.out.print(tokenizer);
            System.out.println("Production Declarations:");
            System.out.println("------------------------");
            System.out.print(parser);
        }
    }

    /**
     * Tokenizes the input file with the token patterns from the
     * grammar.
     *
     * @param grammar        the grammar to use
     *
     * @throws RuntimeException if the input file couldn't be tokenized
     *             correctly
     */
    private void tokenize(Grammar grammar) throws RuntimeException {
        Tokenizer  tokenizer;
        Token      token;

        try {
            tokenizer = grammar.createTokenizer(new FileReader(file));
            if (!quiet) {
                System.out.println("Tokens from " + file + ":");
            }
            while ((token = tokenizer.next()) != null) {
                if (!quiet) {
                    System.out.println(token);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        } catch (GrammarException e) {
            throw new RuntimeException("in grammar " + grammar.getFileName() +
                                       ": " + e.getMessage());
        } catch (ParseException e) {
            throw new RuntimeException("in file " + file + ": " +
                                       e.getMessage());
        }
    }

    /**
     * Parses the input file with the grammar.
     *
     * @param grammar        the grammar to use
     *
     * @throws RuntimeException if the input file couldn't be parsed
     *             correctly
     */
    private void parse(Grammar grammar) throws RuntimeException {
        Tokenizer  tokenizer;
        Analyzer   analyzer;
        Parser     parser;

        try {
            tokenizer = grammar.createTokenizer(new FileReader(file));
            if (quiet) {
                analyzer = null;
            } else {
                analyzer = new TreePrinter(System.out);
            }
            parser = grammar.createParser(tokenizer, analyzer);
            if (!quiet) {
                System.out.println("Parse tree from " + file + ":");
            }
            parser.parse();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        } catch (GrammarException e) {
            throw new RuntimeException("in grammar " + grammar.getFileName() +
                                       ": " + e.getMessage());
        } catch (ParserCreationException e) {
            throw new RuntimeException("in grammar " + grammar.getFileName() +
                                       ": " + e.getMessage());
        } catch (ParserLogException e) {
            throw new RuntimeException("in file " + file + ": " +
                                       e.getMessage());
        }
    }

    /**
     * Parses the input file with the grammar and prints profiling
     * information.
     *
     * @param grammar        the grammar to use
     *
     * @throws RuntimeException if the input file couldn't be profiled
     *             correctly
     */
    private void profile(Grammar grammar) throws RuntimeException {
        Tokenizer  tokenizer;
        Parser     parser;
        Node       node;
        long       time;
        int        counter;

        // Profile tokenizer
        try {
            tokenizer = grammar.createTokenizer(new FileReader(file));
            System.out.println("Tokenizing " + file);
            time = System.currentTimeMillis();
            counter = 0;
            while (tokenizer.next() != null) {
                counter++;
            }
            time = System.currentTimeMillis() - time;
            System.out.println("  Time elapsed:  " + time + " millisec");
            System.out.println("  Tokens found:  " + counter);
            System.out.println("  Average speed: " + (counter / time) +
                               " tokens/millisec");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        } catch (GrammarException e) {
            throw new RuntimeException("in grammar " + grammar.getFileName() +
                                       ": " + e.getMessage());
        } catch (ParseException e) {
            throw new RuntimeException("in file " + file + ": " +
                                       e.getMessage());
        }

        // Profile parser
        try {
            tokenizer = grammar.createTokenizer(new FileReader(file));
            parser = grammar.createParser(tokenizer);
            System.out.println("Parsing " + file);
            time = System.currentTimeMillis();
            node = parser.parse();
            time = System.currentTimeMillis() - time;
            counter = 1 + node.getDescendantCount();
            System.out.println("  Time elapsed:  " + time + " millisec");
            System.out.println("  Nodes found:   " + counter);
            System.out.println("  Average speed: " + (counter / time) +
                               " nodes/millisec");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        } catch (GrammarException e) {
            throw new RuntimeException("in grammar " + grammar.getFileName() +
                                       ": " + e.getMessage());
        } catch (ParserCreationException e) {
            throw new RuntimeException("in grammar " + grammar.getFileName() +
                                       ": " + e.getMessage());
        } catch (ParserLogException e) {
            throw new RuntimeException("in file " + file + ": " +
                                       e.getMessage());
        }
    }
}
