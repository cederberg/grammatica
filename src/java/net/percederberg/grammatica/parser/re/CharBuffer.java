/*
 * CharBuffer.java
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

/**
 * A character buffer. This class provides an API identical to
 * StringBuffer, with the exception that none of the methods in this
 * class are synchronized.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 *
 * @deprecated The CharBuffer class has been deprecated in favor
 * of ReaderBuffer as of version 1.5.
 */
public class CharBuffer {

    /**
     * The character buffer length.
     */
    private int length = 0;

    /**
     * The character buffer.
     */
    private char[] contents = null;

    /**
     * Creates a new character buffer.
     */
    public CharBuffer() {
        this(16);
    }

    /**
     * Creates a new character buffer with the specified initial size.
     *
     * @param initialSize    the initial size of the buffer
     */
    public CharBuffer(int initialSize) {
        contents = new char[initialSize];
    }

    /**
     * Creates a new character buffer from the specified string.
     *
     * @param str            the string to copy
     */
    public CharBuffer(String str) {
        length = str.length();
        contents = str.toCharArray();
    }

    /**
     * Creates a new character buffer from the specified string
     * buffer.
     *
     * @param str            the string buffer to copy
     */
    public CharBuffer(StringBuffer str) {
        length = str.length();
        contents = new char[length];
        str.getChars(0, length, contents, 0);
    }

    /**
     * Appends the string representation of a boolean value to the
     * end of the buffer.
     *
     * @param b              the boolean value to append
     *
     * @return this character buffer
     */
    public CharBuffer append(boolean b) {
        return append(String.valueOf(b));
    }

    /**
     * Appends a character to the end of the buffer.
     *
     * @param c              the character to append
     *
     * @return this character buffer
     */
    public CharBuffer append(char c) {
        ensureCapacity(length + 1);
        contents[length++] = c;
        return this;
    }

    /**
     * Appends a character array to the end of the buffer.
     *
     * @param str            the characters to append
     *
     * @return this character buffer
     */
    public CharBuffer append(char[] str) {
        return append(str, 0, str.length);
    }

    /**
     * Appends a character array to the end of the buffer.
     *
     * @param str            the character array to append
     * @param offset         the starting position in the array
     * @param length         the number of characters to copy
     *
     * @return this character buffer
     */
    public CharBuffer append(char[] str, int offset, int length) {
        ensureCapacity(this.length + length);
        System.arraycopy(str, offset, contents, this.length, length);
        this.length += length;
        return this;
    }

    /**
     * Appends the string representation of a double value to the end
     * of the buffer.
     *
     * @param d              the double value to append
     *
     * @return this character buffer
     */
    public CharBuffer append (double d) {
        return append(String.valueOf(d));
    }

    /**
     * Appends the string representation of a float value to the end
     * of the buffer.
     *
     * @param f              the float value to append
     *
     * @return this character buffer
     */
    public CharBuffer append(float f) {
        return append(String.valueOf(f));
    }

    /**
     * Appends the string representation of an int value to the end of
     * the buffer.
     *
     * @param i              the int value to append
     *
     * @return this character buffer
     */
    public CharBuffer append(int i) {
        return append(String.valueOf(i));
    }

    /**
     * Appends the string representation of a long value to the end of
     * the buffer.
     *
     * @param l              the long value to append
     *
     * @return this character buffer
     */
    public CharBuffer append(long l) {
        return append(String.valueOf(l));
    }

    /**
     * Appends the string representation of an object to the end of
     * the buffer.
     *
     * @param obj            the object to append
     *
     * @return this character buffer
     */
    public CharBuffer append(Object obj) {
        return append(obj.toString());
    }

    /**
     * Appends a string to the end of the buffer.
     *
     * @param str            the string to append
     *
     * @return this character buffer
     */
    public CharBuffer append(String str) {
        ensureCapacity(length + str.length());
        str.getChars(0, str.length(), contents, length);
        length += str.length();
        return this;
    }

    /**
     * Appends a string buffer to the end of the buffer.
     *
     * @param str            the string buffer to append
     *
     * @return this character buffer
     */
    public CharBuffer append(StringBuffer str) {
        ensureCapacity(length + str.length());
        str.getChars(0, str.length(), contents, length);
        length += str.length();
        return this;
    }

    /**
     * Returns a character in the buffer.
     *
     * @param index          the character position, 0 <= index < length
     *
     * @return the character found
     *
     * @throws StringIndexOutOfBoundsException if the character
     *             position was negative or higher or equal to the
     *             buffer length
     */
    public char charAt(int index) throws StringIndexOutOfBoundsException {
        if (index < 0 || index >= length) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return contents[index];
    }

    /**
     * Removes characters from this buffer.
     *
     * @param start          the starting position (inclusive)
     * @param end            the ending position (exclusive)
     *
     * @return this character buffer
     *
     * @throws StringIndexOutOfBoundsException if the start or end
     *             indexes were out of bounds
     */
    public CharBuffer delete(int start, int end)
        throws StringIndexOutOfBoundsException {

        if (start < 0) {
            throw new StringIndexOutOfBoundsException(start);
        }
        if (end > length) {
            end = length;
        }
        if (start > end) {
            throw new StringIndexOutOfBoundsException();
        }
        if (end - start > 0) {
            System.arraycopy(contents, end, contents, start, length - end);
            length -= (end - start);
        }
        return this;
    }

    /**
     * Ensures that this buffer has at least the specified capacity.
     *
     * @param size           the minimum buffer size
     */
    public void ensureCapacity(int size) {
        char[]  newContents;

        if (contents.length >= size) {
            return;
        }
        if (size < 2 * contents.length + 2) {
            size = 2 * contents.length + 2;
        }
        newContents = new char[size];
        System.arraycopy(contents, 0, newContents, 0, length);
        contents = newContents;
    }

    /**
     * Returns the number of characters in the buffer.
     *
     * @return the length of the buffer
     */
    public int length() {
        return length;
    }

    /**
     * Returns a string containing a sequence of characters from this
     * buffer.
     *
     * @param start          the start index, inclusive
     *
     * @return the new substring
     *
     * @throws StringIndexOutOfBoundsException if the start index was
     *             negative, or higher than the length of the string
     */
    public String substring(int start)
        throws StringIndexOutOfBoundsException {

        return substring(start, length);
    }

    /**
     * Returns a string containing a sequence of characters from this
     * buffer.
     *
     * @param start          the start index, inclusive
     * @param end            end end index, exclusive
     *
     * @return the new substring
     *
     * @throws StringIndexOutOfBoundsException if the start index was
     *             negative, or higher than the length of the string
     */
    public String substring(int start, int end)
        throws StringIndexOutOfBoundsException {

        if (start < 0) {
            throw new StringIndexOutOfBoundsException(start);
        }
        if (end > length) {
            throw new StringIndexOutOfBoundsException(end);
        }
        if (start > end) {
            throw new StringIndexOutOfBoundsException();
        }
        return new String(contents, start, end - start);
    }

    /**
     * Returns a string containing all character in this buffer.
     *
     * @return a string containing the characters in this buffer
     */
    public String toString() {
        return new String(contents, 0, length);
    }
}
