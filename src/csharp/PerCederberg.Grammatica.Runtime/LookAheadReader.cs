/*
 * LookAheadReader.cs
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
 * Copyright (c) 2004-2005 Per Cederberg. All rights reserved.
 */

using System;
using System.IO;

namespace PerCederberg.Grammatica.Runtime {

    /**
     * A look-ahead character stream reader. This class provides the
     * functionalities of a buffered line-number reader, but with the
     * additional possibility of peeking an unlimited number of
     * characters ahead. When looking further and further ahead in the
     * character stream, the buffer is continously enlarged to contain
     * all the required characters from the current position an
     * onwards. This means that looking more characters ahead requires
     * more memory, and thus becomes unviable in the end.
     *
     * @author   Per Cederberg, <per at percederberg dot net>
     * @version  1.5
     * @since    1.5
     */
    public class LookAheadReader : TextReader {

        /**
         * The character stream block size. All reads from the
         * underlying character stream will be made in multiples of
         * this block size.
         */
        private const int STREAM_BLOCK_SIZE = 4096;

        /**
         * The buffer block size. The size of the internal buffer will
         * always be a multiple of this block size.
         */
        private const int BUFFER_BLOCK_SIZE = 1024;

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
        private TextReader input = null;

        /**
         * The line number of the next character to read. This value
         * will be incremented when reading past line breaks.
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
        public LookAheadReader(TextReader input)
            : base() {

            this.input = input;
        }

        /**
         * The current line number property (read-only). This number
         * is the line number of the next character to read.
         */
        public int LineNumber {
            get {
                return line;
            }
        }

        /**
         * The current column number property (read-only). This number
         * is the column number of the next character to read.
         */
        public int ColumnNumber {
            get {
                return column;
            }
        }

        /**
         * Reads a single character.
         *
         * @return the character in the range 0 to 65535
         * (0x00-0xffff), or -1 if the end of the stream was reached
         *
         * @throws IOException if an I/O error occurred
         */
        public override int Read() {
            ReadAhead(1);
            if (pos >= length) {
                return -1;
            } else {
                UpdateLineColumnNumbers(1);
                return buffer[pos++];
            }
        }

        /**
         * Reads characters into an array. This method will always
         * return any remaining characters to read before returning
         * -1.
         *
         * @param cbuf            the destination buffer
         * @param off             the offset at which to start storing chars
         * @param len             the maximum number of characters to read
         *
         * @return the number of characters read, or -1 if the end of
         * the stream was reached
         *
         * @throws IOException if an I/O error occurred
         */
        public override int Read(char[] cbuf, int off, int len) {
            int  count;

            ReadAhead(len);
            if (pos >= length) {
                return -1;
            } else {
                count = length - pos;
                if (count > len) {
                    count = len;
                }
                UpdateLineColumnNumbers(count);
                Array.Copy(buffer, pos, cbuf, off, count);
                pos += count;
                return count;
            }
        }

        /**
         * Reads characters into a string. This method will always
         * return any remaining characters to read before returning
         * null.
         *
         * @param len             the maximum number of characters to read
         *
         * @return the string containing the characters read, or null
         * if the end of the stream was reached
         *
         * @throws IOException if an I/O error occurred
         */
        public string ReadString(int len) {
            int     count;
            string  result;

            ReadAhead(len);
            if (pos >= length) {
                return null;
            } else {
                count = length - pos;
                if (count > len) {
                    count = len;
                }
                UpdateLineColumnNumbers(count);
                result = new string(buffer, pos, count);
                pos += count;
                return result;
            }
        }

        /**
         * Returns the next character to read.
         *
         * @return the character found in the range 0 to 65535
         * (0x00-0xffff), or -1 if the end of the stream was reached
         *
         * @throws IOException if an I/O error occurred
         */
        public override int Peek() {
            return Peek(0);
        }

        /**
         * Returns a character not yet read. This method will read
         * characters up until the specified offset and store them for
         * future retrieval in an internal buffer. The character
         * offset must be positive, but is allowed to span the entire
         * size of the input character stream. Note that the internal
         * buffer must hold all the intermediate characters, which may
         * be wasteful of memory if offset is too large.
         *
         * @param off             the character offset, from 0 and up
         *
         * @return the character found in the range 0 to 65535
         * (0x00-0xffff), or -1 if the end of the stream was reached
         *
         * @throws IOException if an I/O error occurred
         */
        public int Peek(int off) {
            ReadAhead(off + 1);
            if (pos + off >= length) {
                return -1;
            } else {
                return buffer[pos + off];
            }
        }

        /**
         * Returns a string of characters not yet read. This method
         * will read characters up until the specified offset (plus
         * length) and store them for future retrieval in an internal
         * buffer. The character offset must be positive, but is
         * allowed to span the entire size of the input character
         * stream. Note that the internal buffer must hold all the
         * intermediate characters, which may be wasteful of memory if
         * offset is too large.
         *
         * @param off             the character offset, from 0 and up
         * @param len             the maximum number of characters to read
         *
         * @return the string containing the characters read, or null
         * if the end of the stream was reached
         *
         * @throws IOException if an I/O error occurred
         */
        public string PeekString(int off, int len) {
            int  count;

            ReadAhead(off + len + 1);
            if (pos + off >= length) {
                return null;
            } else {
                count = length - (pos + off);
                if (count > len) {
                    count = len;
                }
                return new string(buffer, pos + off, count);
            }
        }

        /**
         * Close the stream. Once a stream has been closed, further
         * reads will throw an IOException. Closing a
         * previously-closed stream, however, has no effect.
         */
        public override void Close() {
            buffer = null;
            pos = 0;
            length = 0;
            if (input != null) {
                input.Close();
                input = null;
            }
        }

        /**
         * Reads characters from the input stream and appends them to
         * the input buffer. This method is safe to call even though
         * the end of file has been reached. As a side effect, this
         * method may also remove characters at the beginning of the
         * buffer. It will enlarge the buffer as needed.
         *
         * @param offset         the read offset, from 0 and up
         *
         * @throws IOException if an error was encountered while
         *             reading the input stream
         */
        private void ReadAhead(int offset) {
            int  size;
            int  readSize;

            // Check for end of stream or already read characters
            if (input == null || pos + offset < length) {
                return;
            }

            // Remove old characters from buffer
            if (pos > BUFFER_BLOCK_SIZE) {
                Array.Copy(buffer, pos, buffer, 0, length - pos);
                length -= pos;
                pos = 0;
            }

            // Calculate number of characters to read
            size = pos + offset - length + 1;
            if (size % STREAM_BLOCK_SIZE != 0) {
                size = (size / STREAM_BLOCK_SIZE) * STREAM_BLOCK_SIZE;
                size += STREAM_BLOCK_SIZE;
            }
            EnsureBufferCapacity(length + size);

            // Read characters
            try {
                while (input != null && size > 0) {
                    readSize = input.Read(buffer, length, size);
                    if (readSize > 0) {
                        length += readSize;
                        size -= readSize;
                    } else {
                        input.Close();
                        input = null;
                    }
                }
            } catch (IOException e) {
                input = null;
                throw e;
            }
        }

        /**
         * Ensures that the buffer has at least the specified
         * capacity.
         *
         * @param size           the minimum buffer size
         */
        private void EnsureBufferCapacity(int size) {
            char[]  newbuf;

            if (buffer.Length >= size) {
                return;
            }
            if (size % BUFFER_BLOCK_SIZE != 0) {
                size = (size / BUFFER_BLOCK_SIZE) * BUFFER_BLOCK_SIZE;
                size += BUFFER_BLOCK_SIZE;
            }
            newbuf = new char[size];
            Array.Copy(buffer, 0, newbuf, 0, length);
            buffer = newbuf;
        }

        /**
         * Updates the line and column numbers counters. This method
         * requires all the characters to be processed (i.e. returned
         * as read) to be present in the buffer, starting at the
         * current buffer position.
         *
         * @param offset          the number of characters to process
         */
        private void UpdateLineColumnNumbers(int offset) {
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
}
