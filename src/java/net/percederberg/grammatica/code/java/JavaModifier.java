/*
 * JavaModifier.java
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

package net.percederberg.grammatica.code.java;

/**
 * A class containing the Java modifier constants. This class
 * shouldn't be used directly, but each class should declare it's own
 * constants being equal to the constants here.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
abstract class JavaModifier {

    /**
     * The public access modifier constant.
     */
    public static final int PUBLIC = 0;

    /**
     * The protected access modifier constant.
     */
    public static final int PROTECTED = 1;

    /**
     * The package local access modifier constant (i.e. no modifier).
     */
    public static final int PACKAGE_LOCAL = 2;

    /**
     * The private access modifier constant.
     */
    public static final int PRIVATE = 3;

    /**
     * The static modifier.
     */
    public static final int STATIC = 4;

    /**
     * The abstract modifier.
     */
    public static final int ABSTRACT = 8;

    /**
     * The final modifier.
     */
    public static final int FINAL = 16;

    /**
     * The synchronized modifier.
     */
    public static final int SYNCHRONIZED = 32;

    /**
     * The abstract modifier.
     */
    public static final int NATIVE = 64;

    /**
     * The transient modifier.
     */
    public static final int TRANSIENT = 128;

    /**
     * The volatile modifier.
     */
    public static final int VOLATILE = 256;

    /**
     * The synchronized modifier.
     */
    public static final int STRICTFP = 512;

    /**
     * Creates a string with the specified modifiers.
     *
     * @param modifiers      the modfier values
     *
     * @return a string description of the modfiers
     */
    public static String createModifierDecl(int modifiers) {
        StringBuffer  res = new StringBuffer();

        // Set access modifier
        switch (modifiers % 4) {
        case PUBLIC:
            res.append("public ");
            break;
        case PROTECTED:
            res.append("protected ");
            break;
        case PACKAGE_LOCAL:
            break;
        case PRIVATE:
            res.append("private ");
            break;
        }

        // Set other modifiers
        if ((modifiers & STATIC) > 0) {
            res.append("static ");
        }
        if ((modifiers & ABSTRACT) > 0) {
            res.append("abstract ");
        }
        if ((modifiers & FINAL) > 0) {
            res.append("final ");
        }
        if ((modifiers & SYNCHRONIZED) > 0) {
            res.append("synchronized ");
        }
        if ((modifiers & NATIVE) > 0) {
            res.append("native ");
        }
        if ((modifiers & TRANSIENT) > 0) {
            res.append("transient ");
        }
        if ((modifiers & VOLATILE) > 0) {
            res.append("volatile ");
        }
        if ((modifiers & STRICTFP) > 0) {
            res.append("strictfp ");
        }

        return res.toString();
    }
}
