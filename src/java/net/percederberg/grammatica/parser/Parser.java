/*
 * Parser.java
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

package net.percederberg.grammatica.parser;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * A base parser class. This class provides the standard parser
 * interface, as well as token handling.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
public abstract class Parser {

    /**
     * The parser initialization flag.
     */
    private boolean initialized = false;

    /**
     * The tokenizer to use.
     */
    private Tokenizer tokenizer;

    /**
     * The analyzer to use for callbacks.
     */
    private Analyzer analyzer;

    /**
     * The list of production patterns.
     */
    private ArrayList patterns = new ArrayList();

    /**
     * The map with production patterns and their id:s. This map
     * contains the production patterns indexed by their id:s.
     */
    private HashMap patternIds = new HashMap();

    /**
     * The list of buffered tokens. This list will contain tokens that
     * have been read from the tokenizer, but not yet consumed.
     */
    private ArrayList tokens = new ArrayList();

    /**
     * The error log. All parse errors will be added to this log as
     * the parser attempts to recover from the error. If the error
     * count is higher than zero (0), this log will be thrown as the
     * result from the parse() method.
     */
    private ParserLogException errorLog = new ParserLogException();

    /**
     * The error recovery counter. This counter is initially set to a
     * negative value to indicate that no error requiring recovery
     * has been encountered. When a parse error is found, the counter
     * is set to three (3), and is then decreased by one for each
     * correctly read token until it reaches zero (0).
     */
    private int errorRecovery = -1;

    /**
     * Creates a new parser.
     *
     * @param input          the input stream to read from
     *
     * @throws ParserCreationException if the tokenizer couldn't be
     *             initialized correctly
     *
     * @since 1.5
     */
    Parser(Reader input) throws ParserCreationException {
        this(input, null);
    }

    /**
     * Creates a new parser.
     *
     * @param input          the input stream to read from
     * @param analyzer       the analyzer callback to use
     *
     * @throws ParserCreationException if the tokenizer couldn't be
     *             initialized correctly
     *
     * @since 1.5
     */
    Parser(Reader input, Analyzer analyzer) throws ParserCreationException {
        this.tokenizer = newTokenizer(input);
        this.analyzer = (analyzer == null) ? newAnalyzer() : analyzer;
    }

    /**
     * Creates a new parser.
     *
     * @param tokenizer      the tokenizer to use
     */
    Parser(Tokenizer tokenizer) {
        this(tokenizer, null);
    }

    /**
     * Creates a new parser.
     *
     * @param tokenizer      the tokenizer to use
     * @param analyzer       the analyzer callback to use
     */
    Parser(Tokenizer tokenizer, Analyzer analyzer) {
        this.tokenizer = tokenizer;
        this.analyzer = (analyzer == null) ? newAnalyzer() : analyzer;
    }

    /**
     * Creates a new tokenizer for this parser. Can be overridden by
     * a subclass to provide a custom implementation.
     *
     * @param input          the input stream to read from
     *
     * @return the tokenizer created
     *
     * @throws ParserCreationException if the tokenizer couldn't be
     *             initialized correctly
     *
     * @since 1.5
     */
    protected Tokenizer newTokenizer(Reader input) throws ParserCreationException {
        // TODO: This method should really be abstract, but it isn't in this
        //       version due to backwards compatibility requirements.
        return new Tokenizer(input);
    }

    /**
     * Creates a new analyzer for this parser. Can be overridden by a
     * subclass to provide a custom implementation.
     *
     * @return the analyzer created
     *
     * @since 1.5
     */
    protected Analyzer newAnalyzer() {
        // TODO: This method should really be abstract, but it isn't in this
        //       version due to backwards compatibility requirements.
        return new Analyzer();
    }

    /**
     * Returns the tokenizer in use by this parser.
     *
     * @return the tokenizer in use by this parser
     *
     * @since 1.4
     */
    public Tokenizer getTokenizer() {
        return tokenizer;
    }

    /**
     * Returns the analyzer in use by this parser.
     *
     * @return the analyzer in use by this parser
     *
     * @since 1.4
     */
    public Analyzer getAnalyzer() {
        return analyzer;
    }

    /**
     * Sets the parser initialized flag. Normally this flag is set by
     * the prepare() method, but this method allows further
     * modifications to it.
     *
     * @param initialized    the new initialized flag
     */
    void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * Adds a new production pattern to the parser. The first pattern
     * added is assumed to be the starting point in the grammar. The
     * patterns added may be validated to some extent.
     *
     * @param pattern        the pattern to add
     *
     * @throws ParserCreationException if the pattern couldn't be
     *             added correctly to the parser
     */
    public void addPattern(ProductionPattern pattern)
        throws ParserCreationException {

        Integer  id = new Integer(pattern.getId());

        if (pattern.getAlternativeCount() <= 0) {
            throw new ParserCreationException(
                ParserCreationException.INVALID_PRODUCTION_ERROR,
                pattern.getName(),
                "no production alternatives are present (must have at " +
                "least one)");
        }
        if (patternIds.containsKey(id)) {
            throw new ParserCreationException(
                ParserCreationException.INVALID_PRODUCTION_ERROR,
                pattern.getName(),
                "another pattern with the same id (" + id +
                ") has already been added");
        }
        patterns.add(pattern);
        patternIds.put(id, pattern);
        setInitialized(false);
    }

    /**
     * Initializes the parser. All the added production patterns will
     * be analyzed for ambiguities and errors. This method also
     * initializes internal data structures used during the parsing.
     *
     * @throws ParserCreationException if the parser couldn't be
     *             initialized correctly
     */
    public void prepare() throws ParserCreationException {
        if (patterns.size() <= 0) {
            throw new ParserCreationException(
                ParserCreationException.INVALID_PARSER_ERROR,
                "no production patterns have been added");
        }
        for (int i = 0; i < patterns.size(); i++) {
            checkPattern((ProductionPattern) patterns.get(i));
        }
        setInitialized(true);
    }

    /**
     * Checks a production pattern for completeness. If some rule in
     * the pattern referenced an production pattern not added to this
     * parser, a parser creation exception will be thrown.
     *
     * @param pattern        the production pattern to check
     *
     * @throws ParserCreationException if the pattern referenced a
     *             pattern not added to this parser
     */
    private void checkPattern(ProductionPattern pattern)
        throws ParserCreationException {

        for (int i = 0; i < pattern.getAlternativeCount(); i++) {
            checkAlternative(pattern.getName(), pattern.getAlternative(i));
        }
    }

    /**
     * Checks a production pattern alternative for completeness. If
     * some element in the alternative referenced an production
     * pattern not added to this parser, a parser creation exception
     * will be thrown.
     *
     * @param name           the name of the pattern being checked
     * @param alt            the production pattern alternative
     *
     * @throws ParserCreationException if the alternative referenced a
     *             pattern not added to this parser
     */
    private void checkAlternative(String name,
                                  ProductionPatternAlternative alt)
        throws ParserCreationException {

        for (int i = 0; i < alt.getElementCount(); i++) {
            checkElement(name, alt.getElement(i));
        }
    }

    /**
     * Checks a production pattern element for completeness. If the
     * element references a production pattern not added to this
     * parser, a parser creation exception will be thrown.
     *
     * @param name           the name of the pattern being checked
     * @param elem           the production pattern element to check
     *
     * @throws ParserCreationException if the element referenced a
     *             pattern not added to this parser
     */
    private void checkElement(String name, ProductionPatternElement elem)
        throws ParserCreationException {

        if (elem.isProduction() && getPattern(elem.getId()) == null) {
            throw new ParserCreationException(
                ParserCreationException.INVALID_PRODUCTION_ERROR,
                name,
                "an undefined production pattern id (" + elem.getId() +
                ") is referenced");
        }
    }

    /**
     * Resets this parser for usage with another input stream. The
     * associated tokenizer and analyzer will also be reset. This
     * method will clear all the internal state and the error log in
     * the parser. It is normally called in order to reuse a parser
     * and tokenizer pair with multiple input streams, thereby
     * avoiding the cost of re-analyzing the grammar structures.
     *
     * @param input          the new input stream to read
     *
     * @see Tokenizer#reset(java.io.Reader)
     * @see Analyzer#reset()
     *
     * @since 1.5
     */
    public void reset(Reader input) {
        this.tokenizer.reset(input);
        this.analyzer.reset();
    }

    /**
     * Parses the token stream and returns a parse tree. This method
     * will call prepare() if not previously called. It will also call
     * the reset() method, to making sure that only the
     * Tokenizer.reset() method must be explicitly called in order to
     * reuse a parser for multiple input streams. In case of a parse
     * error, the parser will attempt to recover and throw all the
     * errors found in a parser log exception at the end of the
     * parsing.
     *
     * @return the parse tree
     *
     * @throws ParserCreationException if the parser couldn't be
     *             initialized correctly
     * @throws ParserLogException if the input couldn't be parsed
     *             correctly
     *
     * @see #prepare
     * @see #reset
     * @see Tokenizer#reset
     */
    public Node parse() throws ParserCreationException, ParserLogException {
        Node  root = null;

        // Initialize parser
        if (!initialized) {
            prepare();
        }
        this.tokens.clear();
        this.errorLog = new ParserLogException();
        this.errorRecovery = -1;

        // Parse input
        try {
            root = parseStart();
        } catch (ParseException e) {
            addError(e, true);
        }

        // Check for errors
        if (errorLog.getErrorCount() > 0) {
            throw errorLog;
        }

        return root;
    }

    /**
     * Parses the token stream and returns a parse tree.
     *
     * @return the parse tree
     *
     * @throws ParseException if the input couldn't be parsed
     *             correctly
     */
    protected abstract Node parseStart() throws ParseException;

    /**
     * Factory method to create a new production node. This method
     * can be overridden to provide other production implementations
     * than the default one.
     *
     * @param pattern        the production pattern
     *
     * @return the new production node
     *
     * @since 1.5
     */
    protected Production newProduction(ProductionPattern pattern) {
        return analyzer.newProduction(pattern);
    }

    /**
     * Adds an error to the error log. If the parser is in error
     * recovery mode, the error will not be added to the log. If the
     * recovery flag is set, this method will set the error recovery
     * counter thus enter error recovery mode. Only lexical or
     * syntactical errors require recovery, so this flag shouldn't be
     * set otherwise.
     *
     * @param e              the error to add
     * @param recovery       the recover flag
     */
    void addError(ParseException e, boolean recovery) {
        if (errorRecovery <= 0) {
            errorLog.addError(e);
        }
        if (recovery) {
            errorRecovery = 3;
        }
    }

    /**
     * Returns the production pattern with the specified id.
     *
     * @param id             the production pattern id
     *
     * @return the production pattern found, or
     *         null if non-existent
     */
    ProductionPattern getPattern(int id) {
        Integer  value = new Integer(id);

        return (ProductionPattern) patternIds.get(value);
    }

    /**
     * Returns the production pattern for the starting production.
     *
     * @return the start production pattern, or
     *         null if no patterns have been added
     */
    ProductionPattern getStartPattern() {
        if (patterns.size() <= 0) {
            return null;
        } else {
            return (ProductionPattern) patterns.get(0);
        }
    }

    /**
     * Returns the ordered set of production patterns.
     *
     * @return the ordered set of production patterns
     */
    Collection getPatterns() {
        return patterns;
    }

    /**
     * Handles the parser entering a production. This method calls the
     * appropriate analyzer callback if the node is not hidden. Note
     * that this method will not call any callback if an error
     * requiring recovery has ocurred.
     *
     * @param node           the parse tree node
     */
    void enterNode(Node node) {
        if (!node.isHidden() && errorRecovery < 0) {
            try {
                analyzer.enter(node);
            } catch (ParseException e) {
                addError(e, false);
            }
        }
    }

    /**
     * Handles the parser leaving a production. This method calls the
     * appropriate analyzer callback if the node is not hidden, and
     * returns the result. Note that this method will not call any
     * callback if an error requiring recovery has ocurred.
     *
     * @param node           the parse tree node
     *
     * @return the parse tree node, or
     *         null if no parse tree should be created
     */
    Node exitNode(Node node) {
        if (!node.isHidden() && errorRecovery < 0) {
            try {
                return analyzer.exit(node);
            } catch (ParseException e) {
                addError(e, false);
            }
        }
        return node;
    }

    /**
     * Handles the parser adding a child node to a production. This
     * method calls the appropriate analyzer callback. Note that this
     * method will not call any callback if an error requiring
     * recovery has ocurred.
     *
     * @param node           the parent parse tree node
     * @param child          the child parse tree node, or null
     */
    void addNode(Production node, Node child) {
        if (errorRecovery >= 0) {
            // Do nothing
        } else if (node.isHidden()) {
            node.addChild(child);
        } else if (child != null && child.isHidden()) {
            for (int i = 0; i < child.getChildCount(); i++) {
                addNode(node, child.getChildAt(i));
            }
        } else {
            try {
                analyzer.child(node, child);
            } catch (ParseException e) {
                addError(e, false);
            }
        }
    }

    /**
     * Reads and consumes the next token in the queue. If no token was
     * available for consumation, a parse error will be thrown.
     *
     * @return the token consumed
     *
     * @throws ParseException if the input stream couldn't be read or
     *             parsed correctly
     */
    Token nextToken() throws ParseException {
        Token  token = peekToken(0);

        if (token != null) {
            tokens.remove(0);
            return token;
        } else {
            throw new ParseException(
                ParseException.UNEXPECTED_EOF_ERROR,
                null,
                tokenizer.getCurrentLine(),
                tokenizer.getCurrentColumn());
        }
    }

    /**
     * Reads and consumes the next token in the queue. If no token was
     * available for consumation, a parse error will be thrown. A
     * parse error will also be thrown if the token id didn't match
     * the specified one.
     *
     * @param id             the expected token id
     *
     * @return the token consumed
     *
     * @throws ParseException if the input stream couldn't be parsed
     *             correctly, or if the token wasn't expected
     */
    Token nextToken(int id) throws ParseException {
        Token      token = nextToken();
        ArrayList  list;

        if (token.getId() == id) {
            if (errorRecovery > 0) {
                errorRecovery--;
            }
            return token;
        } else {
            list = new ArrayList(1);
            list.add(tokenizer.getPatternDescription(id));
            throw new ParseException(
                ParseException.UNEXPECTED_TOKEN_ERROR,
                token.toShortString(),
                list,
                token.getStartLine(),
                token.getStartColumn());
        }
    }

    /**
     * Returns a token from the queue. This method is used to check
     * coming tokens before they have been consumed. Any number of
     * tokens forward can be checked.
     *
     * @param steps          the token queue number, zero (0) for first
     *
     * @return the token in the queue, or
     *         null if no more tokens in the queue
     */
    Token peekToken(int steps) {
        Token  token;

        while (steps >= tokens.size()) {
            try {
                token = tokenizer.next();
                if (token == null) {
                    return null;
                } else {
                    tokens.add(token);
                }
            } catch (ParseException e) {
                addError(e, true);
            }
        }
        return (Token) tokens.get(steps);
    }

    /**
     * Returns a string representation of this parser. The string will
     * contain all the production definitions and various additional
     * information.
     *
     * @return a detailed string representation of this parser
     */
    public String toString() {
        StringBuffer  buffer = new StringBuffer();

        for (int i = 0; i < patterns.size(); i++) {
            buffer.append(toString((ProductionPattern) patterns.get(i)));
            buffer.append("\n");
        }
        return buffer.toString();
    }

    /**
     * Returns a string representation of a production pattern.
     *
     * @param prod           the production pattern
     *
     * @return a detailed string representation of the pattern
     */
    private String toString(ProductionPattern prod) {
        StringBuffer  buffer = new StringBuffer();
        StringBuffer  indent = new StringBuffer();
        LookAheadSet  set;
        int           i;

        buffer.append(prod.getName());
        buffer.append(" (");
        buffer.append(prod.getId());
        buffer.append(") ");
        for (i = 0; i < buffer.length(); i++) {
            indent.append(" ");
        }
        buffer.append("= ");
        indent.append("| ");
        for (i = 0; i < prod.getAlternativeCount(); i++) {
            if (i > 0) {
                buffer.append(indent);
            }
            buffer.append(toString(prod.getAlternative(i)));
            buffer.append("\n");
        }
        for (i = 0; i < prod.getAlternativeCount(); i++) {
            set = prod.getAlternative(i).getLookAhead();
            if (set.getMaxLength() > 1) {
                buffer.append("Using ");
                buffer.append(set.getMaxLength());
                buffer.append(" token look-ahead for alternative ");
                buffer.append(i + 1);
                buffer.append(": ");
                buffer.append(set.toString(tokenizer));
                buffer.append("\n");
            }
        }
        return buffer.toString();
    }

    /**
     * Returns a string representation of a production pattern
     * alternative.
     *
     * @param alt            the production pattern alternative
     *
     * @return a detailed string representation of the alternative
     */
    private String toString(ProductionPatternAlternative alt) {
        StringBuffer  buffer = new StringBuffer();

        for (int i = 0; i < alt.getElementCount(); i++) {
            if (i > 0) {
                buffer.append(" ");
            }
            buffer.append(toString(alt.getElement(i)));
        }
        return buffer.toString();
    }

    /**
     * Returns a string representation of a production pattern
     * element.
     *
     * @param elem           the production pattern element
     *
     * @return a detailed string representation of the element
     */
    private String toString(ProductionPatternElement elem) {
        StringBuffer  buffer = new StringBuffer();
        int           min = elem.getMinCount();
        int           max = elem.getMaxCount();

        if (min == 0 && max == 1) {
            buffer.append("[");
        }
        if (elem.isToken()) {
            buffer.append(getTokenDescription(elem.getId()));
        } else {
            buffer.append(getPattern(elem.getId()).getName());
        }
        if (min == 0 && max == 1) {
            buffer.append("]");
        } else if (min == 0 && max == Integer.MAX_VALUE) {
            buffer.append("*");
        } else if (min == 1 && max == Integer.MAX_VALUE) {
            buffer.append("+");
        } else if (min != 1 || max != 1) {
            buffer.append("{");
            buffer.append(min);
            buffer.append(",");
            buffer.append(max);
            buffer.append("}");
        }
        return buffer.toString();
    }

    /**
     * Returns a token description for a specified token.
     *
     * @param token          the token to describe
     *
     * @return the token description
     */
    String getTokenDescription(int token) {
        if (tokenizer == null) {
            return "";
        } else {
            return tokenizer.getPatternDescription(token);
        }
    }
}
