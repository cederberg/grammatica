/*
 * RegExpException.java
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
 * Copyright (c) 2003 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.parser.re;

/**
 * A regular expression exception. This exception is thrown if a
 * regular expression couldn't be processed (or "compiled") properly.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
public class RegExpException extends Exception {

    /**
     * The unexpected character error constant. This error is used
     * when a character was read that didn't match the allowed set of
     * characters at the given position.
     */
    public static final int UNEXPECTED_CHARACTER = 1;

    /**
     * The unterminated pattern error constant. This error is used
     * when more characters were expected in the pattern.
     */
    public static final int UNTERMINATED_PATTERN = 2;

    /**
     * The unsupported special character error constant. This error
     * is used when special regular expression characters are used in
     * the pattern, but not supported in this implementation.
     */
    public static final int UNSUPPORTED_SPECIAL_CHARACTER = 3;

    /**
     * The unsupported escape character error constant. This error is
     * used when an escape character construct is used in the pattern,
     * but not supported in this implementation.
     */
    public static final int UNSUPPORTED_ESCAPE_CHARACTER = 4;

    /**
     * The invalid repeat count error constant. This error is used
     * when a repetition count of zero is specified, or when the
     * minimum exceeds the maximum.
     */
    public static final int INVALID_REPEAT_COUNT = 5;

    /**
     * The error type constant.
     */
    private int type;

    /**
     * The error position.
     */
    private int position;

    /**
     * The regular expression pattern.
     */
    private String pattern;

    /**
     * Creates a new regular expression exception.
     *
     * @param type           the error type constant
     * @param pos            the error position
     * @param pattern        the regular expression pattern
     */
    public RegExpException(int type, int pos, String pattern) {
        this.type = type;
        this.position = pos;
        this.pattern = pattern;
    }

    /**
     * Returns the exception error message.
     *
     * @return the exception error message
     */
    public String getMessage() {
        StringBuffer  buffer = new StringBuffer();

        // Append error type name
        switch (type) {
        case UNEXPECTED_CHARACTER:
            buffer.append("unexpected character");
            break;
        case UNTERMINATED_PATTERN:
            buffer.append("unterminated pattern");
            break;
        case UNSUPPORTED_SPECIAL_CHARACTER:
            buffer.append("unsupported character");
            break;
        case UNSUPPORTED_ESCAPE_CHARACTER:
            buffer.append("unsupported escape character");
            break;
        case INVALID_REPEAT_COUNT:
            buffer.append("invalid repeat count");
            break;
        default:
            buffer.append("internal error");
            break;
        }

        // Append erroneous character
        buffer.append(": ");
        if (position < pattern.length()) {
            buffer.append('\'');
            buffer.append(pattern.substring(position));
            buffer.append('\'');
        } else {
            buffer.append("<end of pattern>");
        }

        // Append position
        buffer.append(" at position ");
        buffer.append(position);

        return buffer.toString();
    }
}
