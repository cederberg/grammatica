/*
 * Token.java
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

package net.percederberg.grammatica.parser;

/**
 * A token node. This class represents a token (i.e. a set of adjacent 
 * characters) in a parse tree. The tokens are created by a tokenizer, 
 * that groups characters together into tokens according to a set of 
 * token patterns.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.1
 */
public class Token extends Node {
    
    /**
     * The token pattern used for this token.
     */
    private TokenPattern pattern;

    /**
     * The characters that constitute this token. This is normally 
     * referred to as the token image.
     */
    private String image;
    
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
     * Creates a new token.
     * 
     * @param pattern        the token pattern
     * @param image          the token image (i.e. characters)
     * @param line           the line number of the first character
     * @param col            the column number of the first character
     */
    public Token(TokenPattern pattern, String image, int line, int col) {
        this.pattern = pattern;
        this.image = image;
        this.startLine = line;
        this.startColumn = col;
        this.endLine = line;
        this.endColumn = col + image.length() - 1;
        for (int pos = 0; image.indexOf('\n', pos) >= 0;) {
            pos = image.indexOf('\n', pos) + 1;
            this.endLine++;
            this.endColumn = image.length() - pos;
        }
    }
    
    /**
     * Returns the token (pattern) id. This value is set as a unique
     * identifier when creating the token pattern to simplify later
     * identification.
     * 
     * @return the token id
     */
    public int getId() {
        return pattern.getId();    
    }

    /**
     * Returns the token node name.
     * 
     * @return the token node name
     */
    public String getName() {
        return pattern.getName();
    }

    /**
     * Returns the token image (i.e. the characters).
     * 
     * @return the token characters
     */
    public String getImage() {
        return image;
    }

    /**
     * The line number of the first character in the token image. 
     * 
     * @return the line number of the first token character
     */
    public int getStartLine() {
        return startLine;
    }
    
    /**
     * The column number of the first character in the token image. 
     * 
     * @return the column number of the first token character
     */
    public int getStartColumn() {
        return startColumn;
    }
    
    /**
     * The line number of the last character in the token image. 
     * 
     * @return the line number of the last token character
     */
    public int getEndLine() {
        return endLine;
    }
    
    /**
     * The column number of the last character in the token image. 
     * 
     * @return the column number of the last token character
     */
    public int getEndColumn() {
        return endColumn;
    }

    /**
     * Returns the token pattern.
     * 
     * @return the token pattern
     */
    TokenPattern getPattern() {
        return pattern;
    }

    /**
     * Returns a detailed string representation of this token.
     * 
     * @return a detailed string representation of this token
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer  buffer = new StringBuffer();
        int           newline = image.indexOf('\n');
        
        buffer.append(pattern.getName());
        buffer.append("(");
        buffer.append(pattern.getId());
        buffer.append("): \"");
        if (newline >= 0) {
            if (newline > 0 && image.charAt(newline - 1) == '\r') {
                newline--;
            }
            buffer.append(image.substring(0, newline));
            buffer.append("(...)");
        } else {
            buffer.append(image);
        }
        buffer.append("\", line: ");
        buffer.append(startLine);
        buffer.append(", col: ");
        buffer.append(startColumn);
        
        return buffer.toString();
    }
    
    /**
     * Returns a short string representation of this token. The string
     * will only contain the token image and possibly the token 
     * pattern name.
     * 
     * @return a short string representation of this token
     */
    public String toShortString() {
        StringBuffer  buffer = new StringBuffer();
        int           newline = image.indexOf('\n');

        buffer.append('"');
        if (newline >= 0) {
            if (newline > 0 && image.charAt(newline - 1) == '\r') {
                newline--;
            }
            buffer.append(image.substring(0, newline));
            buffer.append("(...)");
        } else {
            buffer.append(image);
        }
        buffer.append('"');
        if (pattern.getType() == TokenPattern.REGEXP_TYPE) {
            buffer.append(" <");
            buffer.append(pattern.getName());
            buffer.append(">");
        }
        
        return buffer.toString();
    }
}
