/*
 * CharacterSetElement.java
 *
 * This work is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * As a special exception, the copyright holders of this library give
 * you permission to link this library with independent modules to
 * produce an executable, regardless of the license terms of these
 * independent modules, and to copy and distribute the resulting
 * executable under terms of your choice, provided that you also meet,
 * for each linked independent module, the terms and conditions of the
 * license of that module. An independent module is a module which is
 * not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the
 * library, but you are not obligated to do so. If you do not wish to
 * do so, delete this exception statement from your version.
 *
 * Copyright (c) 2003-2004 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.parser.re;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import net.percederberg.grammatica.parser.LookAheadReader;

/**
 * A regular expression character set element. This element matches a
 * single character inside (or outside) a character set. The character
 * set is user defined and may contain ranges of characters. The set
 * may also be inverted, meaning that only characters not inside the
 * set will be considered to match.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
class CharacterSetElement extends Element {

    /**
     * The dot ('.') character set. This element matches a single
     * character that is not equal to a newline character.
     */
    public static final CharacterSetElement DOT =
        new CharacterSetElement(false);

    /**
     * The digit character set. This element matches a single
     * numeric character.
     */
    public static final CharacterSetElement DIGIT =
        new CharacterSetElement(false);

    /**
     * The non-digit character set. This element matches a single
     * non-numeric character.
     */
    public static final CharacterSetElement NON_DIGIT =
        new CharacterSetElement(true);

    /**
     * The whitespace character set. This element matches a single
     * whitespace character.
     */
    public static final CharacterSetElement WHITESPACE =
        new CharacterSetElement(false);

    /**
     * The non-whitespace character set. This element matches a single
     * non-whitespace character.
     */
    public static final CharacterSetElement NON_WHITESPACE =
        new CharacterSetElement(true);

    /**
     * The word character set. This element matches a single word
     * character.
     */
    public static final CharacterSetElement WORD =
        new CharacterSetElement(false);

    /**
     * The non-word character set. This element matches a single
     * non-word character.
     */
    public static final CharacterSetElement NON_WORD =
        new CharacterSetElement(true);

    /**
     * The inverted character set flag.
     */
    private boolean inverted;

    /**
     * The character set content. This array may contain either
     * range objects or Character objects.
     */
    private ArrayList contents = new ArrayList();

    /**
     * Creates a new character set element. If the inverted character
     * set flag is set, only characters NOT in the set will match.
     *
     * @param inverted       the inverted character set flag
     */
    public CharacterSetElement(boolean inverted) {
        this.inverted = inverted;
    }

    /**
     * Adds a single character to this character set.
     *
     * @param c              the character to add
     */
    public void addCharacter(char c) {
        contents.add(new Character(c));
    }

    /**
     * Adds multiple characters to this character set.
     *
     * @param str            the string with characters to add
     */
    public void addCharacters(String str) {
        for (int i = 0; i < str.length(); i++) {
            addCharacter(str.charAt(i));
        }
    }

    /**
     * Adds multiple characters to this character set.
     *
     * @param elem           the string element with characters to add
     */
    public void addCharacters(StringElement elem) {
        addCharacters(elem.getString());
    }

    /**
     * Adds a character range to this character set.
     *
     * @param min            the minimum character value
     * @param max            the maximum character value
     */
    public void addRange(char min, char max) {
        contents.add(new Range(min, max));
    }

    /**
     * Adds a character subset to this character set.
     *
     * @param elem           the character set to add
     */
    public void addCharacterSet(CharacterSetElement elem) {
        contents.add(elem);
    }

    /**
     * Returns this element as the character set shouldn't be modified
     * after creation. This partially breaks the contract of clone(),
     * but as new characters are not added to the character set after
     * creation, this will work correctly.
     *
     * @return this character set element
     */
    public Object clone() {
        return this;
    }

    /**
     * Returns the length of a matching string starting at the
     * specified position. The number of matches to skip can also be
     * specified, but numbers higher than zero (0) cause a failed
     * match for any element that doesn't attempt to combine other
     * elements.
     *
     * @param m              the matcher being used
     * @param input          the input character stream to match
     * @param start          the starting position
     * @param skip           the number of matches to skip
     *
     * @return the length of the longest matching string, or
     *         -1 if no match was found
     *
     * @throws IOException if a I/O error occurred
     */
    public int match(Matcher m, LookAheadReader input, int start, int skip)
        throws IOException {

        int  c;

        if (skip != 0) {
            return -1;
        }
        c = input.peek(start);
        if (c < 0) {
            m.setReadEndOfString();
            return -1;
        }
        if (m.isCaseInsensitive()) {
            c = (int) Character.toLowerCase((char) c);
        }
        return inSet((char) c) ? 1 : -1;
    }

    /**
     * Checks if the specified character matches this character set.
     * This method takes the inverted flag into account.
     *
     * @param value          the character to check
     *
     * @return true if the character matches, or
     *         false otherwise
     */
    private boolean inSet(char value) {
        if (this == DOT) {
            return inDotSet(value);
        } else if (this == DIGIT || this == NON_DIGIT) {
            return inDigitSet(value) != inverted;
        } else if (this == WHITESPACE || this == NON_WHITESPACE) {
            return inWhitespaceSet(value) != inverted;
        } else if (this == WORD || this == NON_WORD) {
            return inWordSet(value) != inverted;
        } else {
            return inUserSet(value) != inverted;
        }
    }

    /**
     * Checks if the specified character is present in the 'dot'  set.
     * This method does not consider the inverted flag.
     *
     * @param value          the character to check
     *
     * @return true if the character is present, or
     *         false otherwise
     */
    private boolean inDotSet(char value) {
        switch (value) {
        case '\n':
        case '\r':
        case '\u0085':
        case '\u2028':
        case '\u2029':
            return false;
        default:
            return true;
        }
    }

    /**
     * Checks if the specified character is a digit. This method does
     * not consider the inverted flag.
     *
     * @param value          the character to check
     *
     * @return true if the character is a digit, or
     *         false otherwise
     */
    private boolean inDigitSet(char value) {
        return '0' <= value && value <= '9';
    }

    /**
     * Checks if the specified character is a whitespace character.
     * This method does not consider the inverted flag.
     *
     * @param value          the character to check
     *
     * @return true if the character is a whitespace character, or
     *         false otherwise
     */
    private boolean inWhitespaceSet(char value) {
        switch (value) {
        case ' ':
        case '\t':
        case '\n':
        case '\f':
        case '\r':
        case 11:
            return true;
        default:
            return false;
        }
    }

    /**
     * Checks if the specified character is a word character. This
     * method does not consider the inverted flag.
     *
     * @param value          the character to check
     *
     * @return true if the character is a word character, or
     *         false otherwise
     */
    private boolean inWordSet(char value) {
        return ('a' <= value && value <= 'z')
            || ('A' <= value && value <= 'Z')
            || ('0' <= value && value <= '9')
            || value == '_';
    }

    /**
     * Checks if the specified character is present in the user-
     * defined set. This method does not consider the inverted flag.
     *
     * @param value          the character to check
     *
     * @return true if the character is present, or
     *         false otherwise
     */
    private boolean inUserSet(char value) {
        Object               obj;
        Character            c;
        Range                r;
        CharacterSetElement  e;

        for (int i = 0; i < contents.size(); i++) {
            obj = contents.get(i);
            if (obj instanceof Character) {
                c = (Character) obj;
                if (c.charValue() == value) {
                    return true;
                }
            } else if (obj instanceof Range) {
                r = (Range) obj;
                if (r.inside(value)) {
                    return true;
                }
            } else if (obj instanceof CharacterSetElement) {
                e = (CharacterSetElement) obj;
                if (e.inSet(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Prints this element to the specified output stream.
     *
     * @param output         the output stream to use
     * @param indent         the current indentation
     */
    public void printTo(PrintWriter output, String indent) {
        output.println(indent + toString());
    }

    /**
     * Returns a string description of this character set.
     *
     * @return a string description of this character set
     */
    public String toString() {
        StringBuffer  buffer;

        // Handle predefined character sets
        if (this == DOT) {
            return ".";
        } else if (this == DIGIT) {
            return "\\d";
        } else if (this == NON_DIGIT) {
            return "\\D";
        } else if (this == WHITESPACE) {
            return "\\s";
        } else if (this == NON_WHITESPACE) {
            return "\\S";
        } else if (this == WORD) {
            return "\\w";
        } else if (this == NON_WORD) {
            return "\\W";
        }

        // Handle user-defined character sets
        buffer = new StringBuffer();
        if (inverted) {
            buffer.append("^[");
        } else {
            buffer.append("[");
        }
        for (int i = 0; i < contents.size(); i++) {
            buffer.append(contents.get(i));
        }
        buffer.append("]");

        return buffer.toString();
    }


    /**
     * A character range class.
     */
    private class Range {

        /**
         * The minimum character value.
         */
        private char min;

        /**
         * The maximum character value.
         */
        private char max;

        /**
         * Creates a new character range.
         *
         * @param min        the minimum character value
         * @param max        the maximum character value
         */
        public Range(char min, char max) {
            this.min = min;
            this.max = max;
        }

        /**
         * Checks if the specified character is inside the range.
         *
         * @param c          the character to check
         *
         * @return true if the character is in the range, or
         *         false otherwise
         */
        public boolean inside(char c) {
            return c >= min && c <= max;
        }

        /**
         * Returns a string representation of this object.
         *
         * @return a string representation of this object
         */
        public String toString() {
            return min + "-" + max;
        }
    }
}
