/*
 * Token.cs
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
 * Copyright (c) 2003-2005 Per Cederberg. All rights reserved.
 */

using System.Text;

namespace PerCederberg.Grammatica.Runtime {

    /**
     * A token node. This class represents a token (i.e. a set of adjacent
     * characters) in a parse tree. The tokens are created by a tokenizer,
     * that groups characters together into tokens according to a set of
     * token patterns.
     *
     * @author   Per Cederberg, <per at percederberg dot net>
     * @version  1.5
     */
    public class Token : Node {

        /**
         * The token pattern used for this token.
         */
        private TokenPattern pattern;

        /**
         * The characters that constitute this token. This is normally
         * referred to as the token image.
         */
        private string image;

        /**
         * The line number of the first character in the token image.
         */
        private int startLine;

        /**
         * The column number of the first character in the token image.
         */
        private int startColumn;

        /**
         * The line number of the last character in the token image.
         */
        private int endLine;

        /**
         * The column number of the last character in the token image.
         */
        private int endColumn;

        /**
         * The previous token in the list of tokens.
         */
        private Token previous = null;

        /**
         * The next token in the list of tokens.
         */
        private Token next = null;

        /**
         * Creates a new token.
         *
         * @param pattern        the token pattern
         * @param image          the token image (i.e. characters)
         * @param line           the line number of the first character
         * @param col            the column number of the first character
         */
        public Token(TokenPattern pattern, string image, int line, int col) {
            this.pattern = pattern;
            this.image = image;
            this.startLine = line;
            this.startColumn = col;
            this.endLine = line;
            this.endColumn = col + image.Length - 1;
            for (int pos = 0; image.IndexOf('\n', pos) >= 0;) {
                pos = image.IndexOf('\n', pos) + 1;
                this.endLine++;
                endColumn = image.Length - pos;
            }
        }

        /**
         * The node type id property (read-only). This value is set as
         * a unique identifier for each type of node, in order to
         * simplify later identification.
         *
         * @since 1.5
         */
        public override int Id {
            get {
                return pattern.Id;
            }
        }

        /**
         * The node name property (read-only).
         *
         * @since 1.5
         */
        public override string Name {
            get {
                return pattern.Name;
            }
        }

        /**
         * The line number property of the first character in this
         * node (read-only). If the node has child elements, this
         * value will be fetched from the first child.
         *
         * @since 1.5
         */
        public override int StartLine {
            get {
                return startLine;
            }
        }

        /**
         * The column number property of the first character in this
         * node (read-only). If the node has child elements, this
         * value will be fetched from the first child.
         *
         * @since 1.5
         */
        public override int StartColumn {
            get {
                return startColumn;
            }
        }

        /**
         * The line number property of the last character in this node
         * (read-only). If the node has child elements, this value
         * will be fetched from the last child.
         *
         * @since 1.5
         */
        public override int EndLine {
            get {
                return endLine;
            }
        }

        /**
         * The column number property of the last character in this
         * node (read-only). If the node has child elements, this
         * value will be fetched from the last child.
         *
         * @since 1.5
         */
        public override int EndColumn {
            get {
                return endColumn;
            }
        }

        /**
         * The token image property (read-only). The token image
         * consists of the input characters matched to form this
         * token.
         *
         * @since 1.5
         */
        public string Image {
            get {
                return image;
            }
        }

        /**
         * Returns the token image. The token image consists of the
         * input characters matched to form this token.
         *
         * @return the token image
         *
         * @see #Image
         *
         * @deprecated Use the Image property instead.
         */
        public string GetImage() {
            return Image;
        }

        /**
         * The token pattern property (read-only).
         */
        internal TokenPattern Pattern {
            get {
                return pattern;
            }
        }

        /**
         * The previous token property. If the token list feature is
         * used in the tokenizer, all tokens found will be chained
         * together in a double-linked list. The previous token may be
         * a token that was ignored during the parsing, due to it's
         * ignore flag being set. If there is no previous token or if
         * the token list feature wasn't used in the tokenizer (the
         * default), the previous token will always be null.
         *
         * @see #Next
         * @see Tokenizer#UseTokenList
         *
         * @since 1.5
         */
        public Token Previous {
            get {
                return previous;
            }
            set {
                if (previous != null) {
                    previous.next = null;
                }
                previous = value;
                if (previous != null) {
                    previous.next = this;
                }
            }
        }

        /**
         * Returns the previous token. The previous token may be a token
         * that has been ignored in the parsing. Note that if the token
         * list feature hasn't been used in the tokenizer, this method
         * will always return null. By default the token list feature is
         * not used.
         *
         * @return the previous token, or
         *         null if no such token is available
         *
         * @see #Previous
         * @see #GetNextToken
         * @see Tokenizer#UseTokenList
         *
         * @since 1.4
         *
         * @deprecated Use the Previous property instead.
         */
        public Token GetPreviousToken() {
            return Previous;
        }

        /**
         * The next token property. If the token list feature is used
         * in the tokenizer, all tokens found will be chained together
         * in a double-linked list. The next token may be a token that
         * was ignored during the parsing, due to it's ignore flag
         * being set. If there is no next token or if the token list
         * feature wasn't used in the tokenizer (the default), the
         * next token will always be null.
         *
         * @see #Previous
         * @see Tokenizer#UseTokenList
         *
         * @since 1.5
         */
        public Token Next {
            get {
                return next;
            }
            set {
                if (next != null) {
                    next.previous = null;
                }
                next = value;
                if (next != null) {
                    next.previous = this;
                }
            }
        }

        /**
         * Returns the next token. The next token may be a token that has
         * been ignored in the parsing. Note that if the token list
         * feature hasn't been used in the tokenizer, this method will
         * always return null. By default the token list feature is not
         * used.
         *
         * @return the next token, or
         *         null if no such token is available
         *
         * @see #Next
         * @see #GetPreviousToken
         * @see Tokenizer#UseTokenList
         *
         * @since 1.4
         *
         * @deprecated Use the Next property instead.
         */
        public Token GetNextToken() {
            return Next;
        }

        /**
         * Returns a string representation of this token.
         *
         * @return a string representation of this token
         */
        public override string ToString() {
            StringBuilder  buffer = new StringBuilder();
            int            newline = image.IndexOf('\n');

            buffer.Append(pattern.Name);
            buffer.Append("(");
            buffer.Append(pattern.Id);
            buffer.Append("): \"");
            if (newline >= 0) {
                if (newline > 0 && image[newline - 1] == '\r') {
                    newline--;
                }
                buffer.Append(image.Substring(0, newline));
                buffer.Append("(...)");
            } else {
                buffer.Append(image);
            }
            buffer.Append("\", line: ");
            buffer.Append(startLine);
            buffer.Append(", col: ");
            buffer.Append(startColumn);

            return buffer.ToString();
        }

        /**
         * Returns a short string representation of this token. The
         * string will only contain the token image and possibly the
         * token pattern name.
         *
         * @return a short string representation of this token
         */
        public string ToShortString() {
            StringBuilder  buffer = new StringBuilder();
            int            newline = image.IndexOf('\n');

            buffer.Append('"');
            if (newline >= 0) {
                if (newline > 0 && image[newline - 1] == '\r') {
                    newline--;
                }
                buffer.Append(image.Substring(0, newline));
                buffer.Append("(...)");
            } else {
                buffer.Append(image);
            }
            buffer.Append('"');
            if (pattern.Type == TokenPattern.PatternType.REGEXP) {
                buffer.Append(" <");
                buffer.Append(pattern.Name);
                buffer.Append(">");
            }

            return buffer.ToString();
        }
    }
}
