/*
 * Grammatica.java
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

package net.percederberg.grammatica;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import net.percederberg.grammatica.output.CSharpParserGenerator;
import net.percederberg.grammatica.output.JavaParserGenerator;
import net.percederberg.grammatica.output.VisualBasicParserGenerator;
import net.percederberg.grammatica.parser.Analyzer;
import net.percederberg.grammatica.parser.Node;
import net.percederberg.grammatica.parser.ParseException;
import net.percederberg.grammatica.parser.Parser;
import net.percederberg.grammatica.parser.ParserCreationException;
import net.percederberg.grammatica.parser.ParserLogException;
import net.percederberg.grammatica.parser.Token;
import net.percederberg.grammatica.parser.Tokenizer;

/**
 * The main application. This class provides the command-line
 * interface for invoking the application. See separate documentation
 * for information on usage and command-line parameters.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
public class Grammatica extends Object {

    /**
     * The command-line help output.
     */
    private static final String COMMAND_HELP =
        "Generates source code for a C#, Java or Visual Basic parser from\n" +
        "a grammar file. This program comes with ABSOLUTELY NO WARRANTY;\n" +
        "for details see the LICENSE.txt file.\n" +
        "\n" +
        "Syntax: Grammatica <grammarfile> <action> [<options>]\n" +
        "\n" +
        "Actions:\n" +
        "  --debug\n" +
        "      Debugs the grammar by validating it and printing the\n" +
        "      internal representation.\n" +
        "  --tokenize <file>\n" +
        "      Debugs the grammar by using it to tokenize the specified\n" +
        "      file. No code has to be generated for this.\n" +
        "  --parse <file>\n" +
        "      Debugs the grammar by using it to parse the specified\n" +
        "      file. No code has to be generated for this.\n" +
        "  --profile <file(s)>\n" +
        "      Profiles the grammar by using it to parse the specified\n" +
        "      file(s) and printing a statistic summary.\n" +
        "  --csoutput <dir>\n" +
        "      Creates a C# parser for the grammar (in source code).\n" +
        "      The specified directory will be used as output directory\n" +
        "      for the source code files.\n" +
        "  --javaoutput <dir>\n" +
        "      Creates a Java parser for the grammar (in source code).\n" +
        "      The specified directory will be used as the base output\n" +
        "      directory for the source code files.\n" +
        "  --vboutput <dir>\n" +
        "      Creates a Visual Basic (.NET) parser for the grammar (in\n" +
        "      source code). The specified directory will be used as\n" +
        "      output directory for the source code files.\n" +
        "\n" +
        "C# Output Options:\n" +
        "  --csnamespace <package>\n" +
        "      Sets the C# namespace to use in generated source code\n" +
        "      files. By default no namespace declaration is included.\n" +
        "  --csclassname <name>\n" +
        "      Sets the C# class name prefix to use in generated source\n" +
        "      code files. By default the grammar file name is used.\n" +
        "  --cspublic\n" +
        "      Sets public access for all C# types generated. By default\n" +
        "      type access is internal.\n" +
        "\n" +
        "Java Output Options:\n" +
        "  --javapackage <package>\n" +
        "      Sets the Java package to use in generated source code\n" +
        "      files. By default no package declaration is included.\n" +
        "  --javaclassname <name>\n" +
        "      Sets the Java class name prefix to use in generated source\n" +
        "      code files. By default the grammar file name is used.\n" +
        "  --javapublic\n" +
        "      Sets public access for all Java types. By default type\n" +
        "      access is package local.\n" +
        "\n" +
        "Visual Basic Output Options:\n" +
        "  --vbnamespace <package>\n" +
        "      Sets the namespace to use in generated source code files.\n" +
        "      By default no namespace declaration is included.\n" +
        "  --vbclassname <name>\n" +
        "      Sets the class name prefix to use in generated source code\n" +
        "      files. By default the grammar file name is used.\n" +
        "  --vbpublic\n" +
        "      Sets public access for all types generated. By default type\n" +
        "      access is friend.";

    /**
     * The internal error message.
     */
    private static final String INTERNAL_ERROR =
        "INTERNAL ERROR: An internal error in Grammatica has been found.\n" +
        "    Please report this error to the maintainers (see the web\n" +
        "    site for instructions). Be sure to include the Grammatica\n" +
        "    version number, as well as the information below:\n";

    /**
     * The application entry point.
     *
     * @param args           the command-line parameters
     */
    public static void main(String[] args) {
        Grammar  grammar = null;

        // Parse command-line arguments
        if (args.length == 1 && args[0].equals("--help")) {
            printHelp(null);
            System.exit(1);
        }
        if (args.length < 2) {
            printHelp("Missing grammar file and/or action");
            System.exit(1);
        }

        // Read grammar file
        try {
            grammar = new Grammar(new File(args[0]));
        } catch (FileNotFoundException e) {
            printError(args[0], e);
            System.exit(1);
        } catch (ParserLogException e) {
            printError(args[0], e);
            System.exit(1);
        } catch (GrammarException e) {
            printError(e);
            System.exit(1);
        } catch (SecurityException e) {
            throw e;
        } catch (RuntimeException e) {
            printInternalError(e);
            System.exit(2);
        }

        // Check action parameter
        try {
            if (args[1].equals("--debug")) {
                debug(grammar);
            } else if (args.length < 3) {
                printHelp("missing action file parameter");
                System.exit(1);
            } else if (args[1].equals("--tokenize")) {
                tokenize(grammar, new File(args[2]));
            } else if (args[1].equals("--parse")) {
                parse(grammar, new File(args[2]));
            } else if (args[1].equals("--profile")) {
                profile(grammar, args, 2);
            } else if (args[1].equals("--javaoutput")) {
                writeJavaCode(args, grammar);
            } else if (args[1].equals("--csoutput")) {
                writeCSharpCode(args, grammar);
            } else if (args[1].equals("--vboutput")) {
                writeVisualBasicCode(args, grammar);
            } else {
                printHelp("unrecognized option: " + args[1]);
                System.exit(1);
            }
        } catch (SecurityException e) {
            throw e;
        } catch (RuntimeException e) {
            printInternalError(e);
            System.exit(2);
        }
    }

    /**
     * Prints command-line help information.
     *
     * @param error          an optional error message, or null
     */
    private static void printHelp(String error) {
        System.err.println(COMMAND_HELP);
        System.err.println();
        if (error != null) {
            System.err.print("Error: ");
            System.err.println(error);
            System.err.println();
        }
    }

    /**
     * Prints a general error message.
     *
     * @param e              the detailed exception
     */
    private static void printError(Exception e) {
        StringBuffer  buffer = new StringBuffer();

        buffer.append("Error: ");
        buffer.append(e.getMessage());
        System.err.println(buffer.toString());
    }

    /**
     * Prints a file not found error message.
     *
     * @param file           the file name not found
     * @param e              the detailed exception
     */
    private static void printError(String file, FileNotFoundException e) {
        StringBuffer  buffer = new StringBuffer();

        buffer.append("Error: couldn't open file:");
        buffer.append("\n    ");
        buffer.append(file);
        System.err.println(buffer.toString());
    }

    /**
     * Prints a parse error message.
     *
     * @param file           the input file name
     * @param e              the detailed exception
     */
    private static void printError(String file, ParseException e) {
        StringBuffer  buffer = new StringBuffer();
        String        line;

        // Handle normal parse error
        buffer.append("Error: in ");
        buffer.append(file);
        if (e.getLine() > 0) {
            buffer.append(": line ");
            buffer.append(e.getLine());
        }
        buffer.append(":\n");
        buffer.append(linebreakString(e.getErrorMessage(), "    ", 70));
        line = readLines(file, e.getLine(), e.getLine());
        if (line != null) {
            buffer.append("\n\n");
            buffer.append(line);
            for (int i = 1; i < e.getColumn(); i++) {
                if (line.charAt(i - 1) == '\t') {
                    buffer.append("\t");
                } else {
                    buffer.append(" ");
                }
            }
            buffer.append("^");
        }
        System.err.println(buffer.toString());
    }

    /**
     * Prints a list of parse error messages.
     *
     * @param file           the input file name
     * @param e              the parser log exception
     */
    private static void printError(String file, ParserLogException e) {
        for (int i = 0; i < e.getErrorCount(); i++) {
            printError(file, e.getError(i));
        }
    }

    /**
     * Prints a grammar error message.
     *
     * @param e              the detailed exception
     */
    private static void printError(GrammarException e) {
        StringBuffer  buffer = new StringBuffer();
        String        lines;

        buffer.append("Error: in ");
        buffer.append(e.getFile());
        if (e.getStartLine() > 0) {
            if (e.getStartLine() == e.getEndLine()) {
                buffer.append(": line ");
                buffer.append(e.getStartLine());
            } else {
                buffer.append(": lines ");
                buffer.append(e.getStartLine());
                buffer.append("-");
                buffer.append(e.getEndLine());
            }
        }
        buffer.append(":\n");
        buffer.append(linebreakString(e.getErrorMessage(), "    ", 70));
        lines = readLines(e.getFile(), e.getStartLine(), e.getEndLine());
        if (lines != null) {
            buffer.append("\n\n");
            buffer.append(lines);
        }
        System.err.println(buffer.toString());
    }

    /**
     * Prints an internal error message. This type of error should
     * only be reported when run-time exceptions occur, such as null
     * pointer and the likes. All these error should be reported as
     * bugs to the program maintainers.
     *
     * @param e              the exception to be reported
     */
    private static void printInternalError(Exception e) {
        System.err.println(INTERNAL_ERROR);
        e.printStackTrace();
    }

    /**
     * Breaks a string into multiple lines. This method will also add
     * a prefix to each line in the resulting string. The prefix
     * length will be taken into account when breaking the line. Line
     * breaks will only be inserted as replacements for space
     * characters.
     *
     * @param str            the string to line break
     * @param prefix         the prefix to add to each line
     * @param length         the maximum line length
     *
     * @return the new formatted string
     */
    private static String linebreakString(String str,
                                          String prefix,
                                          int length) {

        StringBuffer  buffer = new StringBuffer();
        int           pos;

        while (str.length() + prefix.length() > length) {
            pos = str.lastIndexOf(' ', length - prefix.length());
            if (pos < 0) {
                pos = str.indexOf(' ');
                if (pos < 0) {
                    break;
                }
            }
            buffer.append(prefix);
            buffer.append(str.substring(0, pos));
            str = str.substring(pos + 1);
            buffer.append("\n");
        }
        buffer.append(prefix);
        buffer.append(str);
        return buffer.toString();
    }

    /**
     * Reads a number of lines from a file. In the file couldn't be
     * opened or read correctly, null will be returned.
     *
     * @param file           the name of the file to read
     * @param start          the first line number to read, from one (1)
     * @param end            the last line number to read, from one (1)
     *
     * @return the lines read including newline characters
     */
    private static String readLines(String file, int start, int end) {
        BufferedReader  input;
        StringBuffer    buffer = new StringBuffer();
        String          str;

        // Check invalid line number
        if (start < 1 || end < start) {
            return null;
        }

        // Read line from file
        try {
            input = new BufferedReader(new FileReader(file));
            for (int i = 0; i < end; i++) {
                str = input.readLine();
                if (str == null) {
                    input.close();
                    return null;
                } else if (start <= i + 1) {
                    buffer.append(str);
                    buffer.append("\n");
                }
            }
            input.close();
        } catch (IOException e) {
            return null;
        }

        return buffer.toString();
    }

    /**
     * Debugs a grammar by printing the internal representation.
     *
     * @param grammar        the grammar to use
     */
    private static void debug(Grammar grammar) {
        Tokenizer  tokenizer = null;
        Parser     parser = null;

        // Create tokenizer and parser
        try {
            tokenizer = grammar.createTokenizer(null);
            parser = grammar.createParser(tokenizer);
        } catch (GrammarException e) {
            printInternalError(e);
            System.exit(2);
        }

        // Print tokenizer and parser
        System.out.println("Contents of " + grammar.getFileName() + ":");
        System.out.println();
        System.out.println("Token Declarations:");
        System.out.println("-------------------");
        System.out.print(tokenizer);
        System.out.println("Production Declarations:");
        System.out.println("------------------------");
        System.out.print(parser);
    }

    /**
     * Tokenizes the specified file with the token patterns from the
     * grammar.
     *
     * @param grammar        the grammar to use
     * @param file           the file to parse
     */
    private static void tokenize(Grammar grammar, File file) {
        Tokenizer  tokenizer;
        Token      token;

        try {
            tokenizer = grammar.createTokenizer(new FileReader(file));
            System.out.println("Tokens from " + file + ":");
            while ((token = tokenizer.next()) != null) {
                System.out.println(token);
            }
        } catch (FileNotFoundException e) {
            printError(file.toString(), e);
            System.exit(1);
        } catch (GrammarException e) {
            printInternalError(e);
            System.exit(2);
        } catch (ParseException e) {
            printError(file.toString(), e);
            System.exit(1);
        }
    }

    /**
     * Parses the specified file with the grammar.
     *
     * @param grammar        the grammar to use
     * @param file           the file to parse
     */
    private static void parse(Grammar grammar, File file) {
        Tokenizer  tokenizer;
        Analyzer   analyzer;
        Parser     parser;

        try {
            tokenizer = grammar.createTokenizer(new FileReader(file));
            analyzer = new TreePrinter(System.out);
            parser = grammar.createParser(tokenizer, analyzer);
            System.out.println("Parse tree from " + file + ":");
            parser.parse();
        } catch (FileNotFoundException e) {
            printError(file.toString(), e);
            System.exit(1);
        } catch (GrammarException e) {
            printInternalError(e);
            System.exit(2);
        } catch (ParserCreationException e) {
            printInternalError(e);
            System.exit(2);
        } catch (ParserLogException e) {
            printError(file.toString(), e);
            System.exit(1);
        }
    }

    /**
     * Parses the specified file with the grammar and prints
     * profiling information.
     *
     * @param grammar        the grammar to use
     * @param files          the files to parse
     * @param first          the index of the first file
     */
    private static void profile(Grammar grammar, String[] files, int first) {
        File       file = new File(files[first]);
        Tokenizer  tokenizer;
        Parser     parser;
        Node       node;
        int        fileCount = files.length - first;
        long       time;
        int        counter;

        // Profile tokenizer
        try {
            System.out.println("Tokenizing " + fileCount + " file(s)...");
            tokenizer = grammar.createTokenizer(new FileReader(file));
            time = System.currentTimeMillis();
            counter = 0;
            for (int i = first; i < files.length; i++) {
                if (i > first) {
                    file = new File(files[i]);
                    tokenizer.reset(new FileReader(file));
                }
                while (tokenizer.next() != null) {
                    counter++;
                }
            }
            time = System.currentTimeMillis() - time + 1;
            System.out.println("  Time elapsed:  " + time + " millisec");
            System.out.println("  Tokens found:  " + counter);
            System.out.println("  Average speed: " + (counter / time) +
                               " tokens/millisec");
            System.out.println();
        } catch (FileNotFoundException e) {
            printError(file.toString(), e);
            System.exit(1);
        } catch (GrammarException e) {
            printInternalError(e);
            System.exit(2);
        } catch (ParseException e) {
            printError(file.toString(), e);
            System.exit(1);
        }

        // Profile parser
        try {
            System.out.println("Parsing " + fileCount + " file(s)...");
            file = new File(files[first]);
            tokenizer = grammar.createTokenizer(new FileReader(file));
            parser = grammar.createParser(tokenizer);
            time = System.currentTimeMillis();
            counter = 0;
            for (int i = first; i < files.length; i++) {
                if (i > first) {
                    file = new File(files[i]);
                    parser.reset(new FileReader(file));
                }
                node = parser.parse();
                counter += 1 + node.getDescendantCount();
            }
            time = System.currentTimeMillis() - time + 1;
            System.out.println("  Time elapsed:  " + time + " millisec");
            System.out.println("  Nodes found:   " + counter);
            System.out.println("  Average speed: " + (counter / time) +
                               " nodes/millisec");
            System.out.println();
        } catch (FileNotFoundException e) {
            printError(file.toString(), e);
            System.exit(1);
        } catch (GrammarException e) {
            printInternalError(e);
            System.exit(2);
        } catch (ParserCreationException e) {
            printInternalError(e);
            System.exit(2);
        } catch (ParserLogException e) {
            printError(file.toString(), e);
            System.exit(1);
        }
    }

    /**
     * Parses the command-line arguments and generates the Java source
     * code for a parser.
     *
     * @param args           the command-line arguments
     * @param grammar        the grammar to use
     */
    private static void writeJavaCode(String[] args, Grammar grammar) {
        JavaParserGenerator gen = new JavaParserGenerator(grammar);

        // Read command-line arguments
        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--javaoutput")) {
                gen.setBaseDir(new File(args[++i]));
            } else if (args[i].equals("--javapackage")) {
                gen.setBasePackage(args[++i]);
            } else if (args[i].equals("--javaclassname")) {
                gen.setBaseName(args[++i]);
            } else if (args[i].equals("--javapublic")) {
                gen.setPublicAccess(true);
            } else {
                printHelp("unrecognized option: " + args[i]);
                System.exit(1);
            }
        }

        // Write parser source code
        try {
            System.out.println("Writing Java parser source code...");
            gen.write();
            System.out.println("Done.");
        } catch (IOException e) {
            printError(e);
            System.exit(1);
        }
    }

    /**
     * Parses the command-line arguments and generates the C# source
     * code for a parser.
     *
     * @param args           the command-line arguments
     * @param grammar        the grammar to use
     */
    private static void writeCSharpCode(String[] args, Grammar grammar) {
        CSharpParserGenerator gen = new CSharpParserGenerator(grammar);

        // Read command-line arguments
        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--csoutput")) {
                gen.setBaseDir(new File(args[++i]));
            } else if (args[i].equals("--csnamespace")) {
                gen.setNamespace(args[++i]);
            } else if (args[i].equals("--csclassname")) {
                gen.setBaseName(args[++i]);
            } else if (args[i].equals("--cspublic")) {
                gen.setPublicAccess(true);
            } else {
                printHelp("unrecognized option: " + args[i]);
                System.exit(1);
            }
        }

        // Write parser source code
        try {
            System.out.println("Writing C# parser source code...");
            gen.write();
            System.out.println("Done.");
        } catch (IOException e) {
            printError(e);
            System.exit(1);
        }
    }

    /**
     * Parses the command-line arguments and generates the Visual
     * Basic source code for a parser.
     *
     * @param args           the command-line arguments
     * @param grammar        the grammar to use
     */
    private static void writeVisualBasicCode(String[] args, Grammar grammar) {
        VisualBasicParserGenerator gen;

        // Read command-line arguments
        gen = new VisualBasicParserGenerator(grammar);
        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--vboutput")) {
                gen.setBaseDir(new File(args[++i]));
            } else if (args[i].equals("--vbnamespace")) {
                gen.setNamespace(args[++i]);
            } else if (args[i].equals("--vbclassname")) {
                gen.setBaseName(args[++i]);
            } else if (args[i].equals("--vbpublic")) {
                gen.setPublicAccess(true);
            } else {
                printHelp("unrecognized option: " + args[i]);
                System.exit(1);
            }
        }

        // Write parser source code
        try {
            System.out.println("Writing Visual Basic parser source code...");
            gen.write();
            System.out.println("Done.");
        } catch (IOException e) {
            printError(e);
            System.exit(1);
        }
    }
}
