/*
 * Grammar.java
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;

import net.percederberg.grammatica.parser.Analyzer;
import net.percederberg.grammatica.parser.Parser;
import net.percederberg.grammatica.parser.ParserCreationException;
import net.percederberg.grammatica.parser.ParserLogException;
import net.percederberg.grammatica.parser.ProductionPattern;
import net.percederberg.grammatica.parser.RecursiveDescentParser;
import net.percederberg.grammatica.parser.TokenPattern;
import net.percederberg.grammatica.parser.Tokenizer;

/**
 * A grammar definition object. This object supports parsing a grammar
 * file and create a lexical analyzer (tokenizer) for the grammar.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
public class Grammar extends Object {

    /**
     * The author grammar declaration constant.
     */
    public static final String AUTHOR_DECLARATION = "AUTHOR";

    /**
     * The case-sensitive grammar declaration constant.
     *
     * @since 1.5
     */
    public static final String CASE_SENSITIVE_DECLARATION = "CASESENSITIVE";

    /**
     * The copyright grammar declaration constant.
     */
    public static final String COPYRIGHT_DECLARATION = "COPYRIGHT";

    /**
     * The date grammar declaration constant.
     */
    public static final String DATE_DECLARATION = "DATE";

    /**
     * The description grammar declaration constant.
     */
    public static final String DESCRIPTION_DECLARATION = "DESCRIPTION";

    /**
     * The grammar type grammar declaration constant.
     */
    public static final String GRAMMAR_TYPE_DECLARATION = "GRAMMARTYPE";

    /**
     * The license grammar declaration constant.
     */
    public static final String LICENSE_DECLARATION = "LICENSE";

    /**
     * The version grammar declaration constant.
     */
    public static final String VERSION_DECLARATION = "VERSION";

    /**
     * The grammar file name.
     */
    private String fileName = "";

    /**
     * The grammar declarations. This is a hash map with all the name
     * value pairs in the header part of the grammar.
     */
    private HashMap declarations = new HashMap();

    /**
     * The tokens found in the processing.
     */
    private LinkedList tokens = new LinkedList();

    /**
     * The token id map. This is a map from the token pattern id to
     * the token pattern.
     */
    private HashMap tokenIds = new HashMap();

    /**
     * The token name map. This is map from the token pattern name to
     * the token pattern.
     */
    private HashMap tokenNames = new HashMap();

    /**
     * The token pattern map. This is map from the token pattern
     * string to the token pattern object.
     */
    private HashMap tokenPatterns = new HashMap();

    /**
     * The productions found in the processing.
     */
    private LinkedList productions = new LinkedList();

    /**
     * The production id map. This is a map from the production
     * pattern id to the production pattern.
     */
    private HashMap productionIds = new HashMap();

    /**
     * The production name map. This is map from the production
     * pattern name to the production pattern.
     */
    private HashMap productionNames = new HashMap();

    /**
     * The map from token or production pattern name to a line range.
     */
    private HashMap lines = new HashMap();

    /**
     * Creates a new grammar from the specified file.
     *
     * @param file     the grammar file to read
     *
     * @throws FileNotFoundException if the grammar file could not be
     *             found
     * @throws ParserLogException if the grammar file couldn't be
     *             parsed correctly
     * @throws GrammarException if the grammar wasn't valid
     */
    public Grammar(File file) throws FileNotFoundException,
        ParserLogException, GrammarException {

        GrammarParser       parser;
        FirstPassAnalyzer   first = new FirstPassAnalyzer(this);
        SecondPassAnalyzer  second = new SecondPassAnalyzer(this);

        fileName = file.toString();
        try {
            parser = new GrammarParser(new FileReader(file), first);
            second.analyze(parser.parse());
        } catch (ParserCreationException e) {
            throw new UnsupportedOperationException(
                "internal error in grammar parser: " + e.getMessage());
        }
        verify();
    }

    /**
     * Checks that the grammar is valid.
     *
     * @throws GrammarException if the grammar wasn't valid
     */
    private void verify() throws GrammarException {
        String  type;

        // Check grammar type
        type = (String) declarations.get(GRAMMAR_TYPE_DECLARATION);
        if (type == null) {
            throw new GrammarException(
                fileName,
                "grammar header missing " + GRAMMAR_TYPE_DECLARATION +
                " declaration");
        } else if (!type.equals("LL")) {
            throw new GrammarException(
                fileName,
                "unrecognized " + GRAMMAR_TYPE_DECLARATION + " value: '" +
                type + "', currently only 'LL' is supported");
        }

        // Check tokens and productions
        if (productions.size() > 0) {
            createParser(createTokenizer(null));
        }
    }

    /**
     * Creates a tokenizer from this grammar.
     *
     * @param in             the input stream to use
     *
     * @return the newly created tokenizer
     *
     * @throws GrammarException if the tokenizer couldn't be created
     *             or initialized correctly
     */
    public Tokenizer createTokenizer(Reader in)
        throws GrammarException {

        Tokenizer  tokenizer;

        try {
            tokenizer = new Tokenizer(in, !getCaseSensitive());
            for (int i = 0; i < tokens.size(); i++) {
                tokenizer.addPattern((TokenPattern) tokens.get(i));
            }
        } catch (ParserCreationException e) {
            if (e.getName() == null) {
                throw new GrammarException(fileName, e.getMessage());
            } else {
                LineRange range = (LineRange) lines.get(e.getName());
                throw new GrammarException(fileName,
                                           e.getMessage(),
                                           range.getStart(),
                                           range.getEnd());
            }
        }

        return tokenizer;
    }

    /**
     * Creates a parser from this grammar.
     *
     * @param tokenizer      the tokenizer to use
     *
     * @return the newly created parser
     *
     * @throws GrammarException if the parser couldn't be created or
     *             initialized correctly
     */
    public Parser createParser(Tokenizer tokenizer)
        throws GrammarException {

        return createParser(tokenizer, null);
    }

    /**
     * Creates a parser from this grammar.
     *
     * @param tokenizer      the tokenizer to use
     * @param analyzer       the analyzer to use
     *
     * @return the newly created parser
     *
     * @throws GrammarException if the parser couldn't be created or
     *             initialized correctly
     */
    public Parser createParser(Tokenizer tokenizer, Analyzer analyzer)
        throws GrammarException {

        Parser  parser;

        try {
            parser = new RecursiveDescentParser(tokenizer, analyzer);
            for (int i = 0; i < productions.size(); i++) {
                parser.addPattern((ProductionPattern) productions.get(i));
            }
            parser.prepare();
        } catch (ParserCreationException e) {
            LineRange range = (LineRange) lines.get(e.getName());
            if (range == null) {
                throw new GrammarException(fileName, e.getMessage());
            } else {
                throw new GrammarException(fileName,
                                           e.getMessage(),
                                           range.getStart(),
                                           range.getEnd());
            }
        }

        return parser;
    }

    /**
     * Returns the grammar file name and path.
     *
     * @return the grammar file name and path
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Returns the declaration value for the specified name.
     *
     * @param name           the declaration name
     *
     * @return the declaration value, or
     *         null if not specified in the grammar header
     */
    public String getDeclaration(String name) {
        return (String) declarations.get(name);
    }

    /**
     * Checks if the grammar tokenizer is case-sensitive. Unless an
     * explicit case-sensitive declaration in the grammar says
     * otherwise, a grammar is assumed to be case-sensitive.
     *
     * @return true if the grammar is case-sensitive (the default), or
     *         false otherwise
     *
     * @since 1.5
     */
    public boolean getCaseSensitive() {
        String  str = getDeclaration(CASE_SENSITIVE_DECLARATION);

        if (str == null) {
            return true;
        } else {
            return !str.equalsIgnoreCase("no")
                && !str.equalsIgnoreCase("false");
        }
    }

    /**
     * Returns the number of token patterns in the grammar.
     *
     * @return the number of token patterns
     */
    public int getTokenPatternCount() {
        return tokens.size();
    }

    /**
     * Returns a specific token pattern.
     *
     * @param pos            the pattern position, 0 <= pos < count
     *
     * @return the token pattern
     */
    public TokenPattern getTokenPattern(int pos) {
        return (TokenPattern) tokens.get(pos);
    }

    /**
     * Returns a token pattern identified by its id.
     *
     * @param id             the pattern id
     *
     * @return the token pattern, or null
     */
    public TokenPattern getTokenPatternById(int id) {
        return (TokenPattern) tokenIds.get(new Integer(id));
    }

    /**
     * Returns a token pattern identified by its name.
     *
     * @param name           the pattern name
     *
     * @return the token pattern, or null
     */
    public TokenPattern getTokenPatternByName(String name) {
        return (TokenPattern) tokenNames.get(name);
    }

    /**
     * Returns a token pattern identified by its pattern string. This
     * method will only return matches for patterns of string type.
     *
     * @param image          the pattern string
     *
     * @return the token pattern, or null
     */
    TokenPattern getTokenPatternByImage(String image) {
        return (TokenPattern) tokenPatterns.get(image);
    }

    /**
     * Returns the number of production patterns in the grammar.
     *
     * @return the number of production patterns
     */
    public int getProductionPatternCount() {
        return productions.size();
    }

    /**
     * Returns a specific production pattern.
     *
     * @param pos            the pattern position, 0 <= pos < count
     *
     * @return the production pattern
     */
    public ProductionPattern getProductionPattern(int pos) {
        return (ProductionPattern) productions.get(pos);
    }

    /**
     * Returns a production pattern identified by its id.
     *
     * @param id             the pattern id
     *
     * @return the production pattern, or null
     */
    public ProductionPattern getProductionPatternById(int id) {
        return (ProductionPattern) productionIds.get(new Integer(id));
    }

    /**
     * Returns a production pattern identified by its name.
     *
     * @param name           the pattern name
     *
     * @return the production pattern, or null
     */
    public ProductionPattern getProductionPatternByName(String name) {
        return (ProductionPattern) productionNames.get(name);
    }

    /**
     * Adds a grammar declaration name-value pair.
     *
     * @param name           the name part
     * @param value          the value part
     */
    void addDeclaration(String name, String value) {
        declarations.put(name, value);
    }

    /**
     * Adds a token pattern to this grammar.
     *
     * @param token          the token pattern to add
     * @param start          the starting line
     * @param end            the ending line
     */
    void addToken(TokenPattern token, int start, int end) {
        tokens.add(token);
        tokenIds.put(new Integer(token.getId()), token);
        tokenNames.put(token.getName(), token);
        if (token.getType() == TokenPattern.STRING_TYPE) {
            tokenPatterns.put(token.getPattern(), token);
        }
        lines.put(token.getName(), new LineRange(start, end));
    }

    /**
     * Adds a production pattern to this grammar.
     *
     * @param production     the production pattern to add
     * @param start          the starting line
     * @param end            the ending line
     */
    void addProduction(ProductionPattern production, int start, int end) {
        productions.add(production);
        productionIds.put(new Integer(production.getId()), production);
        productionNames.put(production.getName(), production);
        lines.put(production.getName(), new LineRange(start, end));
    }


    /**
     * A line number range.
     */
    private class LineRange {

        /**
         * The first line number.
         */
        private int start;

        /**
         * The last line number.
         */
        private int end;

        /**
         * Creates a new line number range.
         *
         * @param start      the first line number
         * @param end        the last line number
         */
        public LineRange(int start, int end) {
            this.start = start;
            this.end = end;
        }

        /**
         * Returns the first line number.
         *
         * @return the first line number
         */
        public int getStart() {
            return start;
        }

        /**
         * Returns the last line number.
         *
         * @return the last line number
         */
        public int getEnd() {
            return end;
        }
    }
}
