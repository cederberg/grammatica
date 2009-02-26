/*
 * GrammarTokenizer.java
 *
 * THIS FILE HAS BEEN GENERATED AUTOMATICALLY. DO NOT EDIT!
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

package net.percederberg.grammatica;

import java.io.Reader;

import net.percederberg.grammatica.parser.ParserCreationException;
import net.percederberg.grammatica.parser.TokenPattern;
import net.percederberg.grammatica.parser.Tokenizer;

/**
 * A character stream tokenizer.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
class GrammarTokenizer extends Tokenizer {

    /**
     * Creates a new tokenizer for the specified input stream.
     *
     * @param input          the input stream to read
     *
     * @throws ParserCreationException if the tokenizer couldn't be
     *             initialized correctly
     */
    public GrammarTokenizer(Reader input)
        throws ParserCreationException {

        super(input, false);
        createPatterns();
    }

    /**
     * Initializes the tokenizer by creating all the token patterns.
     *
     * @throws ParserCreationException if the tokenizer couldn't be
     *             initialized correctly
     */
    private void createPatterns() throws ParserCreationException {
        TokenPattern  pattern;

        pattern = new TokenPattern(GrammarConstants.HEADER,
                                   "HEADER",
                                   TokenPattern.STRING_TYPE,
                                   "%header%");
        addPattern(pattern);

        pattern = new TokenPattern(GrammarConstants.TOKENS,
                                   "TOKENS",
                                   TokenPattern.STRING_TYPE,
                                   "%tokens%");
        addPattern(pattern);

        pattern = new TokenPattern(GrammarConstants.PRODUCTIONS,
                                   "PRODUCTIONS",
                                   TokenPattern.STRING_TYPE,
                                   "%productions%");
        addPattern(pattern);

        pattern = new TokenPattern(GrammarConstants.IGNORE,
                                   "IGNORE",
                                   TokenPattern.REGEXP_TYPE,
                                   "%ignore([^%]*)%");
        addPattern(pattern);

        pattern = new TokenPattern(GrammarConstants.ERROR,
                                   "ERROR",
                                   TokenPattern.REGEXP_TYPE,
                                   "%error([^%]*)%");
        addPattern(pattern);

        pattern = new TokenPattern(GrammarConstants.UNTERMINATED_DIRECTIVE,
                                   "UNTERMINATED_DIRECTIVE",
                                   TokenPattern.REGEXP_TYPE,
                                   "%[^%\\n\\r]*");
        pattern.setError("unterminated directive");
        addPattern(pattern);

        pattern = new TokenPattern(GrammarConstants.EQUALS,
                                   "EQUALS",
                                   TokenPattern.STRING_TYPE,
                                   "=");
        addPattern(pattern);

        pattern = new TokenPattern(GrammarConstants.LEFT_PAREN,
                                   "LEFT_PAREN",
                                   TokenPattern.STRING_TYPE,
                                   "(");
        addPattern(pattern);

        pattern = new TokenPattern(GrammarConstants.RIGHT_PAREN,
                                   "RIGHT_PAREN",
                                   TokenPattern.STRING_TYPE,
                                   ")");
        addPattern(pattern);

        pattern = new TokenPattern(GrammarConstants.LEFT_BRACE,
                                   "LEFT_BRACE",
                                   TokenPattern.STRING_TYPE,
                                   "{");
        addPattern(pattern);

        pattern = new TokenPattern(GrammarConstants.RIGHT_BRACE,
                                   "RIGHT_BRACE",
                                   TokenPattern.STRING_TYPE,
                                   "}");
        addPattern(pattern);

        pattern = new TokenPattern(GrammarConstants.LEFT_BRACKET,
                                   "LEFT_BRACKET",
                                   TokenPattern.STRING_TYPE,
                                   "[");
        addPattern(pattern);

        pattern = new TokenPattern(GrammarConstants.RIGHT_BRACKET,
                                   "RIGHT_BRACKET",
                                   TokenPattern.STRING_TYPE,
                                   "]");
        addPattern(pattern);

        pattern = new TokenPattern(GrammarConstants.QUESTION_MARK,
                                   "QUESTION_MARK",
                                   TokenPattern.STRING_TYPE,
                                   "?");
        addPattern(pattern);

        pattern = new TokenPattern(GrammarConstants.PLUS_SIGN,
                                   "PLUS_SIGN",
                                   TokenPattern.STRING_TYPE,
                                   "+");
        addPattern(pattern);

        pattern = new TokenPattern(GrammarConstants.ASTERISK,
                                   "ASTERISK",
                                   TokenPattern.STRING_TYPE,
                                   "*");
        addPattern(pattern);

        pattern = new TokenPattern(GrammarConstants.VERTICAL_BAR,
                                   "VERTICAL_BAR",
                                   TokenPattern.STRING_TYPE,
                                   "|");
        addPattern(pattern);

        pattern = new TokenPattern(GrammarConstants.SEMICOLON,
                                   "SEMICOLON",
                                   TokenPattern.STRING_TYPE,
                                   ";");
        addPattern(pattern);

        pattern = new TokenPattern(GrammarConstants.IDENTIFIER,
                                   "IDENTIFIER",
                                   TokenPattern.REGEXP_TYPE,
                                   "[A-Za-z][A-Za-z0-9_]*");
        addPattern(pattern);

        pattern = new TokenPattern(GrammarConstants.QUOTED_STRING,
                                   "QUOTED_STRING",
                                   TokenPattern.REGEXP_TYPE,
                                   "\"[^\"]*\"|'[^']*'");
        addPattern(pattern);

        pattern = new TokenPattern(GrammarConstants.REGEXP,
                                   "REGEXP",
                                   TokenPattern.REGEXP_TYPE,
                                   "<<([^\\\\>]|(\\\\.)|(>[^>]))*>>");
        addPattern(pattern);

        pattern = new TokenPattern(GrammarConstants.SINGLE_LINE_COMMENT,
                                   "SINGLE_LINE_COMMENT",
                                   TokenPattern.REGEXP_TYPE,
                                   "//.*");
        pattern.setIgnore();
        addPattern(pattern);

        pattern = new TokenPattern(GrammarConstants.MULTI_LINE_COMMENT,
                                   "MULTI_LINE_COMMENT",
                                   TokenPattern.REGEXP_TYPE,
                                   "/\\*([^*]|\\*+[^*/])*\\*+/");
        pattern.setIgnore();
        addPattern(pattern);

        pattern = new TokenPattern(GrammarConstants.WHITESPACE,
                                   "WHITESPACE",
                                   TokenPattern.REGEXP_TYPE,
                                   "[ \\t\\n\\r]+");
        pattern.setIgnore();
        addPattern(pattern);
    }
}
