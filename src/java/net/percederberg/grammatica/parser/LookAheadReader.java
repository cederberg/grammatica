/*
 * LookAheadReader.java
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
 * Copyright (c) 2004 Per Cederberg. All rights reserved.
 */

package net.percederberg.grammatica.parser;

import java.io.IOException;
import java.io.Reader;

/**
 * A look-ahead character stream reader. This class provides the
 * functionalities of a buffered line-number reader, but with the
 * additional possibility of peeking an unlimited number of characters
 * ahead. When looking further and further ahead in the character
 * stream, the buffer is continously enlarged to contain all the
 * required characters from the current position an onwards. This
 * means that looking more characters ahead requires more memory, and
 * thus becomes unviable in the end.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 * @since    1.5
 */
public class LookAheadReader extends Reader {

    /**
     * The character stream block size. All reads from the underlying
     * character stream will be made in multiples of this block size.
     */
    private static final int STREAM_BLOCK_SIZE = 4096;

    /**
     * The buffer block size. The size of the internal buffer will
     * always be a multiple of this block size.
     */
    private static final int BUFFER_BLOCK_SIZE = 1024;

    /**
     * The character buffer.
     */
    private char[] buffer = new char[STREAM_BLOCK_SIZE];

    /**
     * The current character buffer position.
     */
    private int pos = 0;

    /**
     * The number of characters in the buffer.
     */
    private int length = 0;

    /**
     * The underlying character stream reader.
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
     * Creates a new look-ahead character stream reader.
     *
     * @param input           the character stream reader to wrap
     */
    public LookAheadReader(Reader input) {
        super();
        this.input = input;
    }

    /**
     * Returns the current line number. This number is the line number
     * of the next character to read.
     *
     * @return the current line number
     */
    public int getLineNumber() {
        return line;
    }

    /**
     * Returns the current column number. This number is the column
     * number of the next character to read.
     *
     * @return the current column number
     */
    public int getColumnNumber() {
        return column;
    }

    /**
     * Reads a single character.
     *
     * @return the character read as an integer in the range 0 to
     * 65535 (0x00-0xffff), or -1 if the end of the stream was reached
     *
     * @throws IOException if an I/O error occurred
     */
    public int read() throws IOException {
        readAhead(1);
        if (pos >= length) {
            return -1;
        } else {
            updateLineColumnNumbers(1);
            return buffer[pos++];
        }
    }

    /**
     * Reads characters into an array. This method will always return
     * any remaining characters to read before returning -1.
     *
     * @param cbuf            the destination buffer
     *
     * @return the number of characters read, or -1 if the end of the
     * stream was reached
     *
     * @throws IOException if an I/O error occurred
     */
    public int read(char[] cbuf) throws IOException {
        return read(cbuf, 0, cbuf.length);
    }

    /**
     * Reads characters into an array. This method will always return
     * any remaining characters to read before returning -1.
     *
     * @param cbuf            the destination buffer
     * @param off             the offset at which to start storing chars
     * @param len             the maximum number of characters to read
     *
     * @return the number of characters read, or -1 if the end of the
     * stream was reached
     *
     * @throws IOException if an I/O error occurred
     */
    public int read(char[] cbuf, int off, int len) throws IOException {
        int  count;

        readAhead(len);
        if (pos >= length) {
            return -1;
        } else {
            count = length - pos;
            if (count > len) {
                count = len;
            }
            updateLineColumnNumbers(count);
            System.arraycopy(buffer, pos, cbuf, off, count);
            pos += count;
            return count;
        }
    }

    /**
     * Reads characters into a string. This method will always return
     * any remaining characters to read before returning null.
     *
     * @param len             the maximum number of characters to read
     *
     * @return the string containing the characters read, or null if
     * the end of the stream was reached
     *
     * @throws IOException if an I/O error occurred
     */
    public String readString(int len) throws IOException {
        int     count;
        String  result;

        readAhead(len);
        if (pos >= length) {
            return null;
        } else {
            count = length - pos;
            if (count > len) {
                count = len;
            }
            updateLineColumnNumbers(count);
            result = new String(buffer, pos, count);
            pos += count;
            return result;
        }
    }

    /**
     * Returns a character not yet read. This method will read
     * characters up until the specified offset and store them for
     * future retrieval in an internal buffer. The character offset
     * must be positive, but is allowed to span the entire size of the
     * input character stream. Note that the internal buffer must hold
     * all the intermediate characters, which may be wasteful of
     * memory if offset is too large.
     *
     * @param off             the character offset, from 0 and up
     *
     * @return the character found as an integer in the range 0 to
     * 65535 (0x00-0xffff), or -1 if the end of the stream was reached
     *
     * @throws IOException if an I/O error occurred
     */
    public int peek(int off) throws IOException {
        readAhead(off + 1);
        if (pos + off >= length) {
            return -1;
        } else {
            return buffer[pos + off];
        }
    }

    /**
     * Returns a string of characters not yet read. This method will
     * read characters up until the specified offset (plus length) and
     * store them for future retrieval in an internal buffer. The
     * character offset must be positive, but is allowed to span the
     * entire size of the input character stream. Note that the
     * internal buffer must hold all the intermediate characters,
     * which may be wasteful of memory if offset is too large.
     *
     * @param off             the character offset, from 0 and up
     * @param len             the maximum number of characters to read
     *
     * @return the string containing the characters read, or null if
     * the end of the stream was reached
     *
     * @throws IOException if an I/O error occurred
     */
    public String peekString(int off, int len) throws IOException {
        int  count;

        readAhead(off + len + 1);
        if (pos + off >= length) {
            return null;
        } else {
            count = length - (pos + off);
            if (count > len) {
                count = len;
            }
            return new String(buffer, pos + off, count);
        }
    }

    /**
     * Close the stream. Once a stream has been closed, further reads
     * will throw an IOException. Closing a previously-closed stream,
     * however, has no effect.
     *
     * @throws IOException if an I/O error occurs
     */
    public void close() throws IOException {
        buffer = null;
        pos = 0;
        length = 0;
        if (input != null) {
            try {
                input.close();
            } finally {
                input = null;
            }
        }
    }

    /**
     * Reads characters from the input stream and appends them to the
     * input buffer. This method is safe to call even though the end
     * of file has been reached. As a side effect, this method may
     * also remove
     *
     * @throws ParseException if an error was encountered while
     *             reading the input stream
     */
    private void readAhead(int offset) throws IOException {
        char  newbuf[];
        int   size;
        int   readSize;

        // Check for end of stream or already read characters
        if (input == null || pos + offset < length) {
            return;
        }

        // Remove old characters from buffer
        if (pos > BUFFER_BLOCK_SIZE) {
            System.arraycopy(buffer, pos, buffer, 0, length - pos);
            length -= pos;
            pos = 0;
        }

        // Calculate number of characters to read
        size = pos + offset - length;
        if (size % STREAM_BLOCK_SIZE != 0) {
            size = (size / STREAM_BLOCK_SIZE) * STREAM_BLOCK_SIZE;
            size += STREAM_BLOCK_SIZE;
        }
        ensureBufferCapacity(length + size);

        // Read characters
        try {
            readSize = input.read(buffer, length, size);
        } catch (IOException e) {
            input = null;
            throw e;
        }

        // Append characters to buffer
        if (readSize > 0) {
            length += readSize;
        }
        if (readSize < size) {
            try {
                input.close();
            } finally {
                input = null;
            }
        }
    }

    /**
     * Ensures that the buffer has at least the specified capacity.
     *
     * @param size           the minimum buffer size
     */
    private void ensureBufferCapacity(int size) {
        char[]  newbuf;

        if (buffer.length >= size) {
            return;
        }
        if (size % BUFFER_BLOCK_SIZE != 0) {
            size = (size / BUFFER_BLOCK_SIZE) * BUFFER_BLOCK_SIZE;
            size += BUFFER_BLOCK_SIZE;
        }
        newbuf = new char[size];
        System.arraycopy(buffer, 0, newbuf, 0, length);
        buffer = newbuf;
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
}
