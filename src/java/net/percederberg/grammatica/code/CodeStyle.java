/*
 * CodeStyle.java
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

package net.percederberg.grammatica.code;

/**
 * The abstract base class for all code styles. The code style classes
 * allows configuring some aspects of the source code generated. The
 * code style classes also contain helper methods for the code
 * generators.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
public class CodeStyle {

    /**
     * The default Java code style.
     */
    public static final CodeStyle JAVA = new CodeStyle(70, "    ");

    /**
     * The default C# code style.
     */
    public static final CodeStyle CSHARP = new CodeStyle(70, "    ");

    /**
     * The default Visual Basic code style.
     */
    public static final CodeStyle VISUAL_BASIC = new CodeStyle(70, "    ");

    /**
     * The right print margin.
     */
    private int margin;

    /**
     * The indentation string.
     */
    private String indentString;

    /**
     * Creates a new code style.
     *
     * @param margin         the print margin
     * @param indent         the indentation string
     */
    public CodeStyle(int margin, String indent) {
        this.margin = margin;
        this.indentString = indent;
    }

    /**
     * Returns the right print margin.
     *
     * @return the print margin
     */
    public int getMargin() {
        return margin;
    }

    /**
     * Returns the indentation string for the specified level.
     *
     * @param level          the indentation level
     *
     * @return a string containing the indentation
     */
    public String getIndent(int level) {
        StringBuffer  buffer = new StringBuffer();

        for (int i = 0; i < level; i++) {
            buffer.append(indentString);
        }
        return buffer.toString();
    }

    /**
     * Creates a string constant from the specified string. This will
     * add " characters to start and the end, and all " character
     * inside the string will be escaped. Also, any occurence of the
     * escape character itself will be doubled (i.e. "\" becomes "\\").
     *
     * @param str            the string to convert
     * @param escape         the escape character to use
     *
     * @return a string constant
     */
    public String getStringConstant(String str, char escape) {
        StringBuffer  res = new StringBuffer();

        res.append('"');
        res.append(addStringEscapes(str, escape));
        res.append('"');

        return res.toString();
    }

    /**
     * Returns the upper-case version of a string. This method also
     * handles transitions from a lower-case letter to an upper-case
     * letter (inside the string) by appending a '_'. This will
     * maintain the word stem separation, also in the upper-case
     * string. Any characters outside the [A-Za-z0-9_] range will be
     * removed from the string.
     *
     * @param str            the string to transform
     *
     * @return the all upper-case version of the string
     */
    public String getUpperCase(String str) {
        StringBuffer  res = new StringBuffer();
        char          last = 'A';

        for (int i = 0; i < str.length(); i++) {
            if (Character.isLowerCase(last)
             && Character.isUpperCase(str.charAt(i))) {

                res.append("_");
            }
            last = str.charAt(i);
            if (('A' <= last && last <= 'Z')
             || ('a' <= last && last <= 'z')
             || ('0' <= last && last <= '9')
             || last == '_') {

                res.append(Character.toUpperCase(last));
            }
        }

        return res.toString();
    }

    /**
     * Returns the lower-case version of a string. This method also
     * handles transitions from a lower-case letter to an upper-case
     * letter (inside the string) by appending a '_'. This will
     * maintain the word stem separation, also in the lower-case
     * string. Any characters outside the [A-Za-z0-9_] range will be
     * removed from the string.
     *
     * @param str            the string to transform
     *
     * @return the all lower-case version of the string
     */
    public String getLowerCase(String str) {
        StringBuffer  res = new StringBuffer();
        char          last = 'A';

        for (int i = 0; i < str.length(); i++) {
            if (Character.isLowerCase(last)
             && Character.isUpperCase(str.charAt(i))) {

                res.append("_");
            }
            last = str.charAt(i);
            if (('A' <= last && last <= 'Z')
             || ('a' <= last && last <= 'z')
             || ('0' <= last && last <= '9')
             || last == '_') {

                res.append(Character.toLowerCase(last));
            }
        }

        return res.toString();
    }

    /**
     * Returns the mixed-case version of a string. This method also
     * handles '_' characters by adding a transition from a lower-case
     * letter to an upper-case letter (in the result string). This
     * will maintain the word stem separation, in mixed-case. Any
     * characters outside the [A-Za-z0-9] range will be removed from
     * the string.
     *
     * @param str            the string to transform
     * @param initialUpper   the first character upper-case flag
     *
     * @return the mixed-case version of the string
     */
    public String getMixedCase(String str, boolean initialUpper) {
        StringBuffer  res = new StringBuffer();
        char          last = 'A';

        for (int i = 0; i < str.length(); i++) {
            if (Character.isLowerCase(last)
             && Character.isUpperCase(str.charAt(i))) {

                initialUpper = true;
            } else if (str.charAt(i) == '_') {
                initialUpper = true;
            }
            last = str.charAt(i);
            if (('A' <= last && last <= 'Z')
             || ('a' <= last && last <= 'z')
             || ('0' <= last && last <= '9')) {

                if (initialUpper) {
                    res.append(Character.toUpperCase(last));
                    initialUpper = false;
                } else {
                    res.append(Character.toLowerCase(last));
                }
            }
        }

        return res.toString();
    }

    /**
     * Adds escapes in front of all " characters in a string. Any
     * occurence of the escape character itself will also be escaped.
     *
     * @param str            the string to convert
     * @param escape         the escape character to use
     *
     * @return the converted string
     */
    public String addStringEscapes(String str, char escape) {
        StringBuffer  res = new StringBuffer();

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '"') {
                res.append(escape);
                res.append("\"");
            } else if (str.charAt(i) == escape) {
                res.append(escape);
                res.append(escape);
            } else {
                res.append(str.charAt(i));
            }
        }

        return res.toString();
    }
}
