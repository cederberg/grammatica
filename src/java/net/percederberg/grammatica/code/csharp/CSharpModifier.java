/*
 * CSharpModifier.java
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

package net.percederberg.grammatica.code.csharp;

/**
 * A class containing the C# modifier constants. This class shouldn't
 * be used directly, but each class should declare it's own constants
 * being equal to the constants here.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
abstract class CSharpModifier {

    /**
     * The public access modifier constant.
     */
    public static final int PUBLIC = 1;

    /**
     * The protected internal access modifier constant.
     */
    public static final int PROTECTED_INTERNAL = 2;

    /**
     * The protected access modifier constant.
     */
    public static final int PROTECTED = 3;

    /**
     * The internal access modifier constant.
     */
    public static final int INTERNAL = 4;

    /**
     * The private access modifier constant.
     */
    public static final int PRIVATE = 5;

    /**
     * The static modifier constant.
     */
    public static final int STATIC = 8;

    /**
     * The new modifier constant.
     */
    public static final int NEW = 16;

    /**
     * The virtual modifier constant.
     */
    public static final int VIRTUAL = 32;

    /**
     * The sealed modifier constant.
     */
    public static final int SEALED = 64;

    /**
     * The override modifier constant.
     */
    public static final int OVERRIDE = 128;

    /**
     * The abstract modifier constant.
     */
    public static final int ABSTRACT = 256;

    /**
     * The extern modifier constant.
     */
    public static final int EXTERN = 512;

    /**
     * The const modifier constant.
     */
    public static final int CONST = 1024;

    /**
     * The readonly modifier constant.
     */
    public static final int READONLY = 2048;

    /**
     * The volatile modifier constant.
     */
    public static final int VOLATILE = 4096;

    /**
     * Creates a string with the specified modifiers.
     *
     * @param modifiers      the modifier flags
     *
     * @return a string representation of the modfier flags
     */
    public static String createModifierDecl(int modifiers) {
        StringBuffer  res = new StringBuffer();

        // Append access modifier
        switch (modifiers % 8) {
        case PUBLIC:
            res.append("public ");
            break;
        case PROTECTED_INTERNAL:
            res.append("protected internal ");
            break;
        case PROTECTED:
            res.append("protected ");
            break;
        case INTERNAL:
            res.append("internal ");
            break;
        case PRIVATE:
            res.append("private ");
            break;
        }

        // Append other modifiers
        if ((modifiers & STATIC) > 0) {
            res.append("static ");
        }
        if ((modifiers & NEW) > 0) {
            res.append("new ");
        }
        if ((modifiers & VIRTUAL) > 0) {
            res.append("virtual ");
        }
        if ((modifiers & SEALED) > 0) {
            res.append("sealed ");
        }
        if ((modifiers & OVERRIDE) > 0) {
            res.append("override ");
        }
        if ((modifiers & ABSTRACT) > 0) {
            res.append("abstract ");
        }
        if ((modifiers & EXTERN) > 0) {
            res.append("extern ");
        }
        if ((modifiers & CONST) > 0) {
            res.append("const ");
        }
        if ((modifiers & READONLY) > 0) {
            res.append("readonly ");
        }
        if ((modifiers & VOLATILE) > 0) {
            res.append("volatile ");
        }

        return res.toString();
    }
}
