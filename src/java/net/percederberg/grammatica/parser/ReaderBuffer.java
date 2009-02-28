/*
 * ReaderBuffer.java
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
 * Copyright (c) 2004-2009 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.parser;

import java.io.IOException;
import java.io.Reader;

/**
 * A character buffer that automatically reads from an input source
 * stream when needed. This class keeps track of the current position
 * in the buffer and its line and column number in the original input
 * source. It allows unlimited look-ahead of characters in the input,
 * reading and buffering the required data internally. As the
 * position is advanced, the buffer content prior to the current
 * position is subject to removal to make space for reading new
 * content. A few characters before the current position are always
 * kept to enable boundary condition checks.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 * @since    1.5
 */
public class ReaderBuffer implements CharSequence {

    /**
     * The stream reading block size. All reads from the underlying
     * character stream will be made in multiples of this block size.
     * Also the character buffer size will always be a multiple of
     * this factor.
     */
    public static final int BLOCK_SIZE = 1024;

    /**
     * The character buffer.
     */
    private char[] buffer = new char[BLOCK_SIZE * 4];

    /**
     * The current character buffer position.
     */
    private int pos = 0;

    /**
     * The number of characters in the buffer.
     */
    private int length = 0;

    /**
     * The input source character reader.
     */
    private Reader input = null;

    /**
     * The line number of the next character to read. This value will
     * be incremented when reading past line breaks.
     */
    private int line = 1;

    /**
     * The column number of the next character to read. This value
     * will be updated for every character read.
     */
    private int column = 1;

    /**
     * Creates a new tokenizer character buffer.
     *
     * @param input           the input source character reader
     */
    public ReaderBuffer(Reader input) {
        this.input = input;
    }

    /**
     * Discards all resources used by this buffer. This will also
     * close the source input stream. Disposing a previously disposed
     * buffer has no effect.
     */
    public void dispose() {
        buffer = null;
        pos = 0;
        length = 0;
        if (input != null) {
            try {
                input.close();
            } catch (Exception ignore) {
                // Do nothing
            }
            input = null;
        }
    }

    /**
     * Returns the current position in the buffer.
     *
     * @return the current position in the buffer
     */
    public int position() {
        return pos;
    }

    /**
     * Returns the current line number. This number is the input
     * source line number of the current position.
     *
     * @return the current position line number
     */
    public int lineNumber() {
        return line;
    }

    /**
     * Returns the current column number. This number is the input
     * source column number of the current position.
     *
     * @return the current position column number
     */
    public int columnNumber() {
        return column;
    }

    /**
     * Returns the current character buffer length. Note that the
     * length may increase (and decrease) as more characters are
     * read from the input source or removed to free up space.
     */
    public int length() {
        return length;
    }

    /**
     * Returns a character already in the buffer. Note that this
     * method may behave in unexpected ways when performing
     * operations that modifies the buffer content.
     *
     * @param index          the char index, 0 <= index < length()
     *
     * @return the character at the specified index
     *
     * @throws IndexOutOfBoundsException if the index is negative or
     *             not less than length()
     */
    public char charAt(int index) throws IndexOutOfBoundsException {
        return buffer[index];
    }

    /**
     * Returns a character sequence already in the buffer. Note that
     * this method may behave in unexpected ways when performing
     * operations that modifies the buffer content.
     *
     * @param start          the start index, inclusive
     * @param end            the end index, exclusive
     *
     * @return the character sequence specified
     *
     * @throws IndexOutOfBoundsException if one of the indices were
     *             negative or not less than (or equal) than length()
     */
    public CharSequence subSequence(int start, int end)
        throws IndexOutOfBoundsException {

        return new String(buffer, start, end - start);
    }

    /**
     * Returns the current content of the buffer as a string. Note
     * that content before the current position will also be
     * returned.
     *
     * @return the current buffer content
     */
    public String toString() {
        return new String(buffer, 0, length);
    }

    /**
     * Returns a character relative to the current position. This
     * method may read from the input source and may also trim the
     * buffer content prior to the current position. The result of
     * calling this method may therefore be that the buffer length
     * and content have been modified.<p>
     *
     * The character offset must be positive, but is allowed to span
     * the entire size of the input source stream. Note that the
     * internal buffer must hold all the intermediate characters,
     * which may be wasteful if the offset is too large.
     *
     * @param offset         the character offset, from 0 and up
     *
     * @return the character found as an integer in the range 0 to
     * 65535 (0x00-0xffff), or -1 if the end of the stream was reached
     *
     * @throws IOException if an I/O error occurred
     */
    public int peek(int offset) throws IOException {
        int  index = pos + offset;

        // Avoid most calls to ensureBuffered(), since we are in a
        // performance hotspot here. This check is not exhaustive,
        // but only present here to speed things up.
        if (index >= length) {
            ensureBuffered(offset + 1);
            index = pos + offset;
        }
        return (index >= length) ? -1 : buffer[index];
    }

    /**
     * Reads the specified number of characters from the current
     * position. This will also move the current position forward.
     * This method will not attempt to move beyond the end of the
     * input source stream. When reaching the end of file, the
     * returned string might be shorter than requested. Any
     * remaining characters will always be returned before returning
     * null.
     *
     * @param offset         the character offset, from 0 and up
     *
     * @return the string containing the characters read, or
     *         null no more characters remain in the buffer
     *
     * @throws IOException if an I/O error occurred
     */
    public String read(int offset) throws IOException {
        int     count;
        String  result;

        ensureBuffered(offset + 1);
        if (pos >= length) {
            return null;
        } else {
            count = length - pos;
            if (count > offset) {
                count = offset;
            }
            updateLineColumnNumbers(count);
            result = new String(buffer, pos, count);
            pos += count;
            return result;
        }
    }

    /**
     * Updates the line and column numbers counters. This method
     * requires all the characters to be processed (i.e. returned as
     * read) to be present in the buffer, starting at the current
     * buffer position.
     *
     * @param offset          the number of characters to process
     */
    private void updateLineColumnNumbers(int offset) {
        for (int i = 0; i < offset; i++) {
            if (buffer[pos + i] == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
        }
    }

    /**
     * Ensures that the specified offset is read into the buffer.
     * This method will read characters from the input stream and
     * appends them to the buffer if needed. This method is safe to
     * call even after end of file has been reached. This method also
     * handles removal of characters at the beginning of the buffer
     * once the current position is high enough. It will also enlarge
     * the buffer as needed.
     *
     * @param offset         the read offset, from 0 and up
     *
     * @throws IOException if an error was encountered while reading
     *             the input stream
     */
    private void ensureBuffered(int offset) throws IOException {
        int  size;
        int  readSize;

        // Check for end of stream or already read characters
        if (input == null || pos + offset < length) {
            return;
        }

        // Remove (almost all) old characters from buffer
        if (pos > BLOCK_SIZE) {
            length -= (pos - 16);
            System.arraycopy(buffer, pos - 16, buffer, 0, length);
            pos = 16;
        }

        // Calculate number of characters to read
        size = pos + offset - length + 1;
        if (size % BLOCK_SIZE != 0) {
            size = (1 + size / BLOCK_SIZE) * BLOCK_SIZE;
        }
        ensureCapacity(length + size);

        // Read characters
        try {
            while (input != null && size > 0) {
                readSize = input.read(buffer, length, size);
                if (readSize > 0) {
                    length += readSize;
                    size -= readSize;
                } else {
                    input.close();
                    input = null;
                }
            }
        } catch (IOException e) {
            input = null;
            throw e;
        }
    }

    /**
     * Ensures that the buffer has at least the specified capacity.
     *
     * @param size           the minimum buffer size
     */
    private void ensureCapacity(int size) {
        char[]  newbuf;

        if (buffer.length >= size) {
            return;
        }
        if (size % BLOCK_SIZE != 0) {
            size = (1 + size / BLOCK_SIZE) * BLOCK_SIZE;
        }
        newbuf = new char[size];
        System.arraycopy(buffer, 0, newbuf, 0, length);
        buffer = newbuf;
    }
}
