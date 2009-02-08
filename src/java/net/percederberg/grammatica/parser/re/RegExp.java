/*
 * RegExp.java
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

package net.percederberg.grammatica.parser.re;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import net.percederberg.grammatica.parser.ReaderBuffer;

/**
 * A regular expression. This class creates and holds an internal
 * data structure representing a regular expression. It also allows
 * creating matchers. This class is thread-safe. Multiple matchers may
 * operate simultanously on the same regular expression.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
public class RegExp {

    /**
     * The base regular expression element.
     */
    private Element element;

    /**
     * The regular expression pattern.
     */
    private String pattern;

    /**
     * The character case ignore flag.
     */
    private boolean ignoreCase;

    /**
     * The current position in the pattern. This variable is used by
     * the parsing methods.
     */
    private int pos;

    /**
     * Creates a new case-sensitive regular expression.
     *
     * @param pattern        the regular expression pattern
     *
     * @throws RegExpException if the regular expression couldn't be
     *             parsed correctly
     */
    public RegExp(String pattern) throws RegExpException {
        this(pattern, false);
    }

    /**
     * Creates a new regular expression. The regular expression can be
     * either case-sensitive or case-insensitive.
     *
     * @param pattern        the regular expression pattern
     * @param ignoreCase     the character case ignore flag
     *
     * @throws RegExpException if the regular expression couldn't be
     *             parsed correctly
     *
     * @since 1.5
     */
    public RegExp(String pattern, boolean ignoreCase)
        throws RegExpException {

        this.pattern = pattern;
        this.ignoreCase = ignoreCase;
        this.pos = 0;
        this.element = parseExpr();
        if (pos < pattern.length()) {
            throw new RegExpException(
                RegExpException.UNEXPECTED_CHARACTER,
                pos,
                pattern);
        }
    }

    /**
     * Creates a new matcher for the specified string.
     *
     * @param str            the string to work with
     *
     * @return the regular expresion matcher
     *
     * @deprecated The CharBuffer class has been deprecated in favor
     * of ReaderBuffer as of version 1.5. Create a ReaderBuffer
     * and use the matcher(ReaderBuffer) method instead of this one.
     */
    public Matcher matcher(CharBuffer str) {
        return matcher(str.toString());
    }

    /**
     * Creates a new matcher for the specified string.
     *
     * @param str            the string to work with
     *
     * @return the regular expression matcher
     */
    public Matcher matcher(String str) {
        return matcher(new ReaderBuffer(new StringReader(str)));
    }

    /**
     * Creates a new matcher for the specified look-ahead character
     * input stream.
     *
     * @param buffer         the character input buffer
     *
     * @return the regular expression matcher
     *
     * @since 1.5
     */
    public Matcher matcher(ReaderBuffer buffer) {
        return new Matcher((Element) element.clone(), buffer, ignoreCase);
    }

    /**
     * Returns a string representation of the regular expression.
     *
     * @return a string representation of the regular expression
     */
    public String toString() {
        StringWriter  str;

        str = new StringWriter();
        str.write("Regular Expression\n");
        str.write("  Pattern: " + pattern + "\n");
        str.write("  Flags:");
        if (ignoreCase) {
            str.write(" caseignore");
        }
        str.write("\n");
        str.write("  Compiled:\n");
        element.printTo(new PrintWriter(str), "    ");
        return str.toString();
    }

    /**
     * Parses a regular expression. This method handles the Expr
     * production in the grammar (see regexp.grammar).
     *
     * @return the element representing this expression
     *
     * @throws RegExpException if an error was encountered in the
     *             pattern string
     */
    private Element parseExpr() throws RegExpException {
        Element  first;
        Element  second;

        first = parseTerm();
        if (peekChar(0) != '|') {
            return first;
        } else {
            readChar('|');
            second = parseExpr();
            return new AlternativeElement(first, second);
        }
    }

    /**
     * Parses a regular expression term. This method handles the
     * Term production in the grammar (see regexp.grammar).
     *
     * @return the element representing this term
     *
     * @throws RegExpException if an error was encountered in the
     *             pattern string
     */
    private Element parseTerm() throws RegExpException {
        ArrayList  list = new ArrayList();

        list.add(parseFact());
        while (true) {
            switch (peekChar(0)) {
            case -1:
            case ')':
            case ']':
            case '{':
            case '}':
            case '?':
            case '+':
            case '|':
                return combineElements(list);
            default:
                list.add(parseFact());
            }
        }
    }

    /**
     * Parses a regular expression factor. This method handles the
     * Fact production in the grammar (see regexp.grammar).
     *
     * @return the element representing this factor
     *
     * @throws RegExpException if an error was encountered in the
     *             pattern string
     */
    private Element parseFact() throws RegExpException {
        Element  elem;

        elem = parseAtom();
        switch (peekChar(0)) {
        case '?':
        case '*':
        case '+':
        case '{':
            return parseAtomModifier(elem);
        default:
            return elem;
        }
    }

    /**
     * Parses a regular expression atom. This method handles the
     * Atom production in the grammar (see regexp.grammar).
     *
     * @return the element representing this atom
     *
     * @throws RegExpException if an error was encountered in the
     *             pattern string
     */
    private Element parseAtom() throws RegExpException {
        Element  elem;

        switch (peekChar(0)) {
        case '.':
            readChar('.');
            return CharacterSetElement.DOT;
        case '(':
            readChar('(');
            elem = parseExpr();
            readChar(')');
            return elem;
        case '[':
            readChar('[');
            elem = parseCharSet();
            readChar(']');
            return elem;
        case -1:
        case ')':
        case ']':
        case '{':
        case '}':
        case '?':
        case '*':
        case '+':
        case '|':
            throw new RegExpException(
                RegExpException.UNEXPECTED_CHARACTER,
                pos,
                pattern);
        default:
            return parseChar();
        }
    }

    /**
     * Parses a regular expression atom modifier. This method handles
     * the AtomModifier production in the grammar (see regexp.grammar).
     *
     * @param elem           the element to modify
     *
     * @return the modified element
     *
     * @throws RegExpException if an error was encountered in the
     *             pattern string
     */
    private Element parseAtomModifier(Element elem) throws RegExpException {
        int  min = 0;
        int  max = -1;
        int  type = RepeatElement.GREEDY;
        int  firstPos;

        // Read min and max
        switch (readChar()) {
        case '?':
            min = 0;
            max = 1;
            break;
        case '*':
            min = 0;
            max = -1;
            break;
        case '+':
            min = 1;
            max = -1;
            break;
        case '{':
            firstPos = pos - 1;
            min = readNumber();
            max = min;
            if (peekChar(0) == ',') {
                readChar(',');
                max = -1;
                if (peekChar(0) != '}') {
                    max = readNumber();
                }
            }
            readChar('}');
            if (max == 0 || (max > 0 && min > max)) {
                throw new RegExpException(
                    RegExpException.INVALID_REPEAT_COUNT,
                    firstPos,
                    pattern);
            }
            break;
        default:
            throw new RegExpException(
                RegExpException.UNEXPECTED_CHARACTER,
                pos - 1,
                pattern);
        }

        // Read operator mode
        if (peekChar(0) == '?') {
            readChar('?');
            type = RepeatElement.RELUCTANT;
        } else if (peekChar(0) == '+') {
            readChar('+');
            type = RepeatElement.POSSESSIVE;
        }

        return new RepeatElement(elem, min, max, type);
    }

    /**
     * Parses a regular expression character set. This method handles
     * the contents of the '[...]' construct in a regular expression.
     *
     * @return the element representing this character set
     *
     * @throws RegExpException if an error was encountered in the
     *             pattern string
     */
    private Element parseCharSet() throws RegExpException {
        CharacterSetElement  charset;
        Element              elem;
        boolean              repeat = true;
        char                 start;
        char                 end;

        if (peekChar(0) == '^') {
            readChar('^');
            charset = new CharacterSetElement(true);
        } else {
            charset = new CharacterSetElement(false);
        }

        while (peekChar(0) > 0 && repeat) {
            start = (char) peekChar(0);
            switch (start) {
            case ']':
                repeat = false;
                break;
            case '\\':
                elem = parseEscapeChar();
                if (elem instanceof StringElement) {
                    charset.addCharacters((StringElement) elem);
                } else {
                    charset.addCharacterSet((CharacterSetElement) elem);
                }
                break;
            default:
                readChar(start);
                if (peekChar(0) == '-'
                 && peekChar(1) > 0
                 && peekChar(1) != ']') {

                    readChar('-');
                    end = readChar();
                    charset.addRange(fixChar(start), fixChar(end));
                } else {
                    charset.addCharacter(fixChar(start));
                }
            }
        }

        return charset;
    }

    /**
     * Parses a regular expression character. This method handles
     * a single normal character in a regular expression.
     *
     * @return the element representing this character
     *
     * @throws RegExpException if an error was encountered in the
     *             pattern string
     */
    private Element parseChar() throws RegExpException {
        switch (peekChar(0)) {
        case '\\':
            return parseEscapeChar();
        case '^':
        case '$':
            throw new RegExpException(
                RegExpException.UNSUPPORTED_SPECIAL_CHARACTER,
                pos,
                pattern);
        default:
            return new StringElement(fixChar(readChar()));
        }
    }

    /**
     * Parses a regular expression character escape. This method
     * handles a single character escape in a regular expression.
     *
     * @return the element representing this character escape
     *
     * @throws RegExpException if an error was encountered in the
     *             pattern string
     */
    private Element parseEscapeChar() throws RegExpException {
        char    c;
        String  str;

        readChar('\\');
        c = readChar();
        switch (c) {
        case '0':
            c = readChar();
            if (c < '0' || c > '3') {
                throw new RegExpException(
                    RegExpException.UNSUPPORTED_ESCAPE_CHARACTER,
                    pos - 3,
                    pattern);
            }
            str = String.valueOf(c);
            c = (char) peekChar(0);
            if ('0' <= c && c <= '7') {
                str += String.valueOf(readChar());
                c = (char) peekChar(0);
                if ('0' <= c && c <= '7') {
                    str += String.valueOf(readChar());
                }
            }
            try {
                c = (char) Integer.parseInt(str, 8);
                return new StringElement(fixChar(c));
            } catch (NumberFormatException e) {
                throw new RegExpException(
                    RegExpException.UNSUPPORTED_ESCAPE_CHARACTER,
                    pos - str.length() - 2,
                    pattern);
            }
        case 'x':
            str = String.valueOf(readChar()) +
                  String.valueOf(readChar());
            try {
                c = (char) Integer.parseInt(str, 16);
                return new StringElement(fixChar(c));
            } catch (NumberFormatException e) {
                throw new RegExpException(
                    RegExpException.UNSUPPORTED_ESCAPE_CHARACTER,
                    pos - str.length() - 2,
                    pattern);
            }
        case 'u':
            str = String.valueOf(readChar()) +
                  String.valueOf(readChar()) +
                  String.valueOf(readChar()) +
                  String.valueOf(readChar());
            try {
                c = (char) Integer.parseInt(str, 16);
                return new StringElement(fixChar(c));
            } catch (NumberFormatException e) {
                throw new RegExpException(
                    RegExpException.UNSUPPORTED_ESCAPE_CHARACTER,
                    pos - str.length() - 2,
                    pattern);
            }
        case 't':
            return new StringElement('\t');
        case 'n':
            return new StringElement('\n');
        case 'r':
            return new StringElement('\r');
        case 'f':
            return new StringElement('\f');
        case 'a':
            return new StringElement('\u0007');
        case 'e':
            return new StringElement('\u001B');
        case 'd':
            return CharacterSetElement.DIGIT;
        case 'D':
            return CharacterSetElement.NON_DIGIT;
        case 's':
            return CharacterSetElement.WHITESPACE;
        case 'S':
            return CharacterSetElement.NON_WHITESPACE;
        case 'w':
            return CharacterSetElement.WORD;
        case 'W':
            return CharacterSetElement.NON_WORD;
        default:
            if (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z')) {
                throw new RegExpException(
                    RegExpException.UNSUPPORTED_ESCAPE_CHARACTER,
                    pos - 2,
                    pattern);
            }
            return new StringElement(fixChar(c));
        }
    }

    /**
     * Adjusts a character for inclusion in a string or character set
     * element. For case-insensitive regular expressions, this
     * transforms the character to lower-case.
     *
     * @param c               the input character
     *
     * @return the adjusted character
     */
    private char fixChar(char c) {
        return ignoreCase ? Character.toLowerCase(c) : c;
    }

    /**
     * Reads a number from the pattern. If the next character isn't a
     * numeric character, an exception is thrown. This method reads
     * several consecutive numeric characters.
     *
     * @return the numeric value read
     *
     * @throws RegExpException if an error was encountered in the
     *             pattern string
     */
    private int readNumber() throws RegExpException {
        StringBuffer  buf = new StringBuffer();
        int           c;

        c = peekChar(0);
        while ('0' <= c && c <= '9') {
            buf.append(readChar());
            c = peekChar(0);
        }
        if (buf.length() <= 0) {
            throw new RegExpException(
                RegExpException.UNEXPECTED_CHARACTER,
                pos,
                pattern);
        }
        return Integer.parseInt(buf.toString());
    }

    /**
     * Reads the next character in the pattern. If no next character
     * exists, an exception is thrown.
     *
     * @return the character read
     *
     * @throws RegExpException if no next character was available in
     *             the pattern string
     */
    private char readChar() throws RegExpException {
        int  c = peekChar(0);

        if (c < 0) {
            throw new RegExpException(
                RegExpException.UNTERMINATED_PATTERN,
                pos,
                pattern);
        } else {
            pos++;
            return (char) c;
        }
    }

    /**
     * Reads the next character in the pattern. If the character
     * wasn't the specified one, an exception is thrown.
     *
     * @param c              the character to read
     *
     * @return the character read
     *
     * @throws RegExpException if the character read didn't match the
     *             specified one, or if no next character was
     *             available in the pattern string
     */
    private char readChar(char c) throws RegExpException {
        if (c != readChar()) {
            throw new RegExpException(
                RegExpException.UNEXPECTED_CHARACTER,
                pos - 1,
                pattern);
        }
        return c;
    }

    /**
     * Returns a character that has not yet been read from the
     * pattern. If the requested position is beyond the end of the
     * pattern string, -1 is returned.
     *
     * @param count          the preview position, from zero (0)
     *
     * @return the character found, or
     *         -1 if beyond the end of the pattern string
     */
    private int peekChar(int count) {
        if (pos + count < pattern.length()) {
            return pattern.charAt(pos + count);
        } else {
            return -1;
        }
    }

    /**
     * Combines a list of elements. This method takes care to always
     * concatenate adjacent string elements into a single string
     * element.
     *
     * @param list           the list with elements
     *
     * @return the combined element
     */
    private Element combineElements(ArrayList list) {
        Element  prev;
        Element  elem;
        String   str;
        int      i;

        // Concatenate string elements
        prev = (Element) list.get(0);
        for (i = 1; i < list.size(); i++) {
            elem = (Element) list.get(i);
            if (prev instanceof StringElement
             && elem instanceof StringElement) {

                str = ((StringElement) prev).getString() +
                      ((StringElement) elem).getString();
                elem = new StringElement(str);
                list.remove(i);
                list.set(i - 1, elem);
                i--;
            }
            prev = elem;
        }

        // Combine all remaining elements
        elem = (Element) list.get(list.size() - 1);
        for (i = list.size() - 2; i >= 0; i--) {
            prev = (Element) list.get(i);
            elem = new CombineElement(prev, elem);
        }
        return elem;
    }
}
