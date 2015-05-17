/*
 * VisualBasicModifier.java
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the BSD license.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * LICENSE.txt file for more details.
 *
 * Copyright (c) 2004 Adrian Moore. All rights reserved.
 * Copyright (c) 2003-2015 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.code.visualbasic;

/**
 * A class containing the Visual Basic modifier constants. This class
 * shouldn't be used directly, but each class should declare it's own
 * constants being equal to the constants here.
 *
 * @author   Adrian Moore
 * @author   Per Cederberg
 * @version  1.5
 * @since    1.5
 */
abstract class VisualBasicModifier {

    /**
     * The public access modifier constant.
     */
    public static final int PUBLIC = 1;

    /**
     * The protected friend access modifier constant.
     */
    public static final int PROTECTED_FRIEND = 2;

    /**
     * The protected access modifier constant.
     */
    public static final int PROTECTED = 3;

    /**
     * The friend access modifier constant.
     */
    public static final int FRIEND = 4;

    /**
     * The private access modifier constant.
     */
    public static final int PRIVATE = 5;

    /**
     * The must inherit modifier constant.
     */
    public static final int MUST_INHERIT = 8;

    /**
     * The not inheritable modifier constant.
     */
    public static final int NOT_INHERITABLE = 16;

    /**
     * The shared modifier constant.
     */
    public static final int SHARED = 32;

    /**
     * The shadows modifier constant.
     */
    public static final int SHADOWS = 64;

    /**
     * The overridable modifier constant.
     */
    public static final int OVERRIDABLE = 128;

    /**
     * The not overridable modifier constant.
     */
    public static final int NOT_OVERRIDABLE = 256;

    /**
     * The overrides modifier constant.
     */
    public static final int OVERRIDES = 512;

    /**
     * The must override modifier constant.
     */
    public static final int MUST_OVERRIDE = 1024;

    /**
     * The overloads modifier constant.
     */
    public static final int OVERLOADS = 2048;

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
            res.append("Public ");
            break;
        case PROTECTED_FRIEND:
            res.append("Protected Friend ");
            break;
        case PROTECTED:
            res.append("Protected ");
            break;
        case FRIEND:
            res.append("Friend ");
            break;
        case PRIVATE:
            res.append("Private ");
            break;
        }

        // Append other modifiers
        if ((modifiers & MUST_INHERIT) > 0) {
            res.append("MustInherit ");
        }
        if ((modifiers & NOT_INHERITABLE) > 0) {
            res.append("NotInheritable ");
        }
        if ((modifiers & SHARED) > 0) {
            res.append("Shared ");
        }
        if ((modifiers & SHADOWS) > 0) {
            res.append("Shadows ");
        }
        if ((modifiers & OVERRIDABLE) > 0) {
            res.append("Overridable ");
        }
        if ((modifiers & NOT_OVERRIDABLE) > 0) {
            res.append("NotOverridable ");
        }
        if ((modifiers & OVERRIDES) > 0) {
            res.append("Overrides ");
        }
        if ((modifiers & MUST_OVERRIDE) > 0) {
            res.append("MustOverride ");
        }
        if ((modifiers & OVERLOADS) > 0) {
            res.append("Overloads ");
        }

        return res.toString();
    }
}
