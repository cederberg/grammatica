/*
 * NodeClassesDir.java
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

package net.percederberg.grammatica.output;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import net.percederberg.grammatica.Grammar;
import net.percederberg.grammatica.code.CodeFile;
import net.percederberg.grammatica.parser.ProductionPattern;
import net.percederberg.grammatica.parser.ProductionPatternAlternative;
import net.percederberg.grammatica.parser.ProductionPatternElement;
import net.percederberg.grammatica.parser.TokenPattern;

/**
 * This class is an abstract base class for those responsible for
 * handling node specification.
 *
 * @author   Connor Prussin, <cprussin at vt dot edu>
 * @version  1.0
 * @since    1.6
 */
abstract class NodeClassesDir extends File {

    /**
     * The list of classes to write on a writeOut();
     */
    private LinkedList<CodeFile> files;

    /**
     * The ParserGenerater to use.
     */
    ParserGenerator gen;

    /**
     * A map of production node descriptors.  See {@link ProdDescriptor} for
     * more information.
     */
    HashMap<ProductionPattern, ProdDescriptor> prodDescriptors;

    /**
     * A map of alternative node descriptors.  See {@link AltDescriptor} for
     * more information.
     */
    HashMap<ProductionPatternAlternative, AltDescriptor> altDescriptors;

    /**
     * A map of token node descriptors.  See {@link ToenDescriptor} for more
     * information.
     */
    HashMap<TokenPattern, TokenDescriptor> tokenDescriptors;

    /**
     * Make a NodeClassesDir off the speficied parser generator.
     *
     * @param gen               the ParserGenerator to use
     * @param append            what to
     */
    NodeClassesDir(ParserGenerator generator, String append) {
        super(generator.getBaseDir().toString() + 
                (append != null ? System.getProperty("file.separator") + append : null));
        // Initialize the variables.
        this.gen                = generator;
        this.files              = new LinkedList<CodeFile>();
        this.prodDescriptors    = new HashMap<ProductionPattern, ProdDescriptor>(gen.getGrammar().getProductionPatternCount());
        this.altDescriptors     = new HashMap<ProductionPatternAlternative, AltDescriptor>();
        this.tokenDescriptors   = new HashMap<TokenPattern, TokenDescriptor>(gen.getGrammar().getTokenPatternCount());

        // Local variables.
        Grammar gram = gen.getGrammar();

        // Populate tokenDescriptors, prodDescriptors and altDescriptors.
        for (TokenPattern tok : gram.getTokenPatterns()) {
            tokenDescriptors.put(tok, new TokenDescriptor(tok));
        }
        for (ProductionPattern pat : gram.getProductionPatterns()) {
            // Only add the production if it is not synthetic.  Synthetic
            // productions are added in the AltDescriptor constructor.
            if (!pat.isSynthetic()) {
                populateProduction(pat);
            }
        }

        // Set up comments and inheritance now that the descriptors are made.
        for (AltDescriptor desc : altDescriptors.values()) {
            desc.setComment();
        }
        for (ProdDescriptor desc : prodDescriptors.values()) {
            for (ProductionPatternAlternative alt : desc.prod.getAlternatives()) {
                if (alt.isSingleElement()) {
                    if (alt.getElement(0).isProduction()) {
                        ProductionPattern pat = gen.getGrammar().getProductionPatternById(alt.getElement(0).getId());
                        if (pat.isSingleAlt() && !pat.isSingleElement()) {
                            altDescriptors.get(pat.getAlternative(0)).inherits.add(desc);
                        } else {
                            prodDescriptors.get(pat).inherits.add(desc);
                        }
                    } else {
                        TokenPattern pat = gen.getGrammar().getTokenPatternById(alt.getElement(0).getId());
                        tokenDescriptors.get(pat).inherits.add(desc);
                    }
                } else {
                    altDescriptors.get(alt).inherits.add(desc);
                }
            }
            desc.setComment();
        }
    }

    /**
     * Adds all necessasry class files.
     *
     * @param pat           the production
     */
    void buildClasses() {
        for (ProdDescriptor desc : prodDescriptors.values()) {
            files.add(buildClassFile(desc));
        }
        for (AltDescriptor desc : altDescriptors.values()) {
            files.add(buildClassFile(desc, getDescriptorList(desc.alt)));
        }
        for (TokenDescriptor desc : tokenDescriptors.values()) {
            files.add(buildClassFile(desc));
        }
    }

    /**
     * Get the set of production descriptors, including information like name,
     * comment, etc.
     *
     * @return the list of descriptors
     */
    HashMap<ProductionPattern, ProdDescriptor> getProdDescriptors() {
        return prodDescriptors;
    }

    /**
     * Get the set of alternative descriptors, including information like name,
     * comment, etc.
     *
     * @return the list of descriptors
     */
    HashMap<ProductionPatternAlternative, AltDescriptor> getAltDescriptors() {
        return altDescriptors;
    }

    /**
     * Get the set of token descriptors, including information like name,
     * comment, etc.
     *
     * @return the list of descriptors
     */
    HashMap<TokenPattern, TokenDescriptor> getTokenDescriptors() {
        return tokenDescriptors;
    }

    /**
     * Writes everything to disk.  Creates the directory and all of the class
     * files.
     *
     * @throws java.io.IOException  on CodeFile.writeCode() exception
     */
    void writeCode() throws IOException {
        mkdirs();
        for (CodeFile f : files) {
            f.writeCode(gen.getCodeStyle());
        }
    }

    /**
     * Return a new token class corresponding to a token described by the given
     * TokenDescriptor.  See {@link TokenDescriptor} for more information.
     *
     * @param desc              the new class's descriptor
     * @return                  the new class file
     */
    abstract CodeFile buildClassFile(TokenDescriptor desc);

    /**
     * Return a new production class corresponding to a production described by
     * the given ProdDescriptor.  See {@link ProdDescriptor} for more
     * information.
     *
     * @param desc              the new class's descriptor
     * @return                  the new class file
     */
    abstract CodeFile buildClassFile(ProdDescriptor desc);

    /**
     * Return a new alternative class corresponding to an alternative described
     * by the given AltDescriptor.  See {@link AltDescriptor} javadoc for more
     * information.
     *
     * @param desc              the new class's descriptor
     * @param accessors         a list of descriptors contaning information on
     *                          each accessor that the new class should contain.
     * @return                  the new class file
     */
    abstract CodeFile buildClassFile(AltDescriptor desc, ArrayList<AccessorDescriptor> accessors);

    /**
     * Populate prodDescriptors and altDescriptors with the appropriate
     * production and alternative descriptors relating to a given
     * ProductionPattern.
     * 
     * @param pat               build the alternative and production descriptors
     *                          from this ProductionPattern
     */
    private void populateProduction(ProductionPattern pat) {
        populateProduction(pat, null);
    }

    /**
     * Populate prodDescriptors and altDescriptors with the appropriate
     * production and alternative descriptors relating to a given
     * ProductionPattern.
     *
     * @param pat               build the alternative and production descriptors
     *                          from this ProductionPattern
     * @param syntheticNumber   if -1, treats the names as a non-synthetic
     *                          production; otherwise, appends "_S" + syntheticNumber
     *                          to the names
     */
    private void populateProduction(ProductionPattern pat, String name) {
        if (pat.isSingleAlt() && !pat.isSingleElement()) {
            // prod = token prod2 ;  =>  class a_prod extends SpecializedProduction
            ProductionPatternAlternative alt = pat.getAlternative(0);
            altDescriptors.put(alt, new AltDescriptor(alt, -1, name));
        } else {
            // prod = token ;  =>  class t_token extends Token implements i_prod
            // prod = prod1 ;  =>  interface i_prod1 extends i_prod  // if prod1 has > 1 alternatives
            //                     class a_prod1 extends SpecializedProduction implements i_prod  // if prod1 has one alternative
            prodDescriptors.put(pat, new ProdDescriptor(pat, name));
            if (!pat.isSingleAlt()) {
                for (int i = 0; i < pat.getAlternativeCount(); i++) {
                    ProductionPatternAlternative alt = pat.getAlternative(i);
                    if (!alt.isSingleElement()) {
                        // prod = prod2 prod3 | ... ;  =>  class a_prod_A1 extends SpecializedProduction implements i_prod
                        altDescriptors.put(alt, new AltDescriptor(alt, i, name));
                    }
                }
            }
        }
    }

    /**
     * Build the list of AccessorDescriptors for a given alternative.
     *
     * @param alt               the alternative for which to get the accessors
     * @return                  the ArrayList of AccessorDescriptors to return
     */
    private ArrayList<AccessorDescriptor> getDescriptorList(ProductionPatternAlternative alt) {

        // Build a hashmap mapping id number to whether there are more than one
        // occurrences.
        HashMap<Integer, Boolean> moreThanOne = new HashMap<Integer, Boolean>();
        for (int i = 0; i < alt.getElementCount(); i++) {
            int id = alt.getElement(i).getId();
            moreThanOne.put(id, moreThanOne.containsKey(id));
        }

        // Build a list of AccessorDescriptors, one item for each child element.
        ArrayList<AccessorDescriptor> descriptorList = new ArrayList<AccessorDescriptor>(alt.getElementCount());
        for (int i = 0; i < alt.getElementCount(); i++) {
            ProductionPatternElement elem = alt.getElement(i);
            // Get the index number
            int append = 0;
            // Add the ID number to the method name, if necessary
            if (moreThanOne.get(elem.getId())) {
                for (append = 1;
                     descriptorList.contains(new AccessorDescriptor(elem, append));
                     append++) {}
            }
            // Odd the item to the list
            descriptorList.add(i, new AccessorDescriptor(elem, append));
        }

        // Return the list
        return descriptorList;
    }

    /**
     * A base class for the descriptor classes.  These descriptors help the
     * overall flow of the program.
     */
    abstract class NodeDescriptor {
        /**
         * The class name, as it should be written to source.
         */
        String name;

        /**
         * The class comment, as it should be written to source.
         */
        String comment;

        /**
         * The list of other ProdDescriptors from which this class should
         * inherit.
         */
        LinkedList<ProdDescriptor> inherits;
    }

    /**
     * The descriptors of production classes.  The classes based off these
     * descriptors should have no accessors, but are rather a base for the
     * classes based off AltDescriptors.
     */
    class ProdDescriptor extends NodeDescriptor {

        /**
         * The production pattern that this descriptor corresponds to.
         */
        ProductionPattern prod;

        /**
         * Build a new production node descriptor.
         *
         * @param pattern       the pattern that this descripter is based off
         * @param name          use this name instead of generating one - null
         *                      here generates a name
         */
        public ProdDescriptor(ProductionPattern pattern, String name) {
            this.prod = pattern;
            this.inherits = new LinkedList<ProdDescriptor>();

            // Build the name and comment.
            this.name = "i_";
            if (name == null) {
                this.name += gen.getCodeStyle().getMixedCase(pattern.getName(), true);
            } else {
                this.name += name;
            }
        }

        /**
         * Add in the comments from each alternative to the comments of this
         * class.
         */
        public void setComment() {
            String header =
                    "This class is a generated node specification class.\n\n" +
                    "NODE TYPE: Production Alternation Class\n" +
                    "NODE PATTERN INFORMATION:\n" +
                    "  " + this.name.substring(2) + " = ";
            String patterns = "\n";
            for (ProductionPatternAlternative alt : prod.getAlternatives()) {
                if (!alt.isSingleElement()) {
                    AltDescriptor desc = altDescriptors.get(alt);
                    header += desc.name.substring(2);
                    patterns += desc.name.substring(2) + " = " + desc.pattern + " ;\n";
                } else if (alt.getElement(0).isProduction()) {
                    ProductionPattern pat = gen.getGrammar().getProductionPatternById(alt.getElement(0).getId());
                    if (pat.isSingleAlt() && !pat.isSingleElement()) {
                        AltDescriptor desc = altDescriptors.get(pat.getAlternative(0));
                        header += desc.name.substring(2);
                        patterns += desc.name.substring(2) + " = " + desc.pattern + " ;\n";
                    } else {
                        ProdDescriptor desc = prodDescriptors.get(pat);
                        header += desc.name.substring(2);
                    }
                } else {
                    TokenPattern pat = gen.getGrammar().getTokenPatternById(alt.getElement(0).getId());
                    TokenDescriptor desc = tokenDescriptors.get(pat);
                    header += desc.name.substring(2);
                    patterns += desc.name.substring(2) + " = \"" + desc.tok.getPattern() + "\"\n";
                }
                header += "\n        |";
            }
            this.comment = header.substring(0, header.length() - 10) + " ;" + patterns.substring(0, patterns.length() - 1);
        }
    }

    /**
     * The descriptors of alternative classes.  The classes based off these
     * descriptors should have well-named accessors, and any time there are more
     * than one alternatives, should inherit from a class based off a
     * ProdDescriptor.
     */
    class AltDescriptor extends NodeDescriptor {

        /**
         * The production pattern alternative that this descriptor corresponds
         * to.
         */
        ProductionPatternAlternative alt;

        /**
         * The pattern, rebuilt to a string.  Makes comments easy.
         */
        String pattern;

        /**
         * Build a new production alternative node descriptor.
         *
         * @param alt       the pattern that this descripter is based off
         * @param altNumber the alternative number - used when building the
         *                  name - ignored if -1
         * @param name      use this name instead of generating one - null
         *                  here generates a name.  This still appends an
         *                  altNumber if applicable
         */
        public AltDescriptor(ProductionPatternAlternative alt, int altNumber, String name) {
            this.alt        = alt;
            this.inherits   = new LinkedList<ProdDescriptor>();

            // Build the name.
            this.name = "a_";
            if (name == null) {
                this.name += gen.getCodeStyle().getMixedCase(alt.getPattern().getName(), true);
            } else {
                this.name += name;
            }
            if (altNumber != -1 ) {
                this.name += "_A" + altNumber;
            }

            // Add the synthetic productions.
            int n = 0;
            Grammar gram = gen.getGrammar();
            for (ProductionPatternElement e : alt.getElements()) {
                if (e.isProduction() && gram.getProductionPatternById(e.getId()).isSynthetic()) {
                    populateProduction(gram.getProductionPatternById(e.getId()), this.name.substring(2) + "_S" + (n++));
                }
            }
        }

        /**
         * Sets up the comment and pattern string on this descriptor
         */
        public void setComment() {
            // Build the pattern.
            String str = "";
            for (int i = 0; i < alt.getElementCount(); i++) {
                ProductionPatternElement elem = alt.getElement(i);
                if (elem.isProduction()) {
                    ProductionPattern pat = gen.getGrammar().getProductionPatternById(elem.getId());
                    if (pat.isSingleAlt() && !pat.isSingleElement()) {
                        str += altDescriptors.get(pat.getAlternative(0)).name.substring(2);
                    } else {
                        str += prodDescriptors.get(pat).name.substring(2);
                    }
                } else {
                    TokenPattern pat = gen.getGrammar().getTokenPatternById(elem.getId());
                    str += tokenDescriptors.get(pat).name.substring(2);
                }

                // Add the proper element repeator symbol, if applicable
                if (elem.getMinCount() == 0) {
                    if (elem.getMaxCount() == 1) {
                        str += "? ";
                    } else {
                        str += "* ";
                    }
                } else if (elem.getMaxCount() == 1) {
                    str += " ";
                } else {
                    str += "+ ";
                }
            }
            this.pattern = str.substring(0, str.length() - 1);

            // Build the comment
            this.comment =
                    "This class is a generated node specification class.\n\n" +
                    "NODE TYPE: Production\n" +
                    "NODE PATTERN: " + this.pattern;
        }
    }

    /**
     * The descriptors of token classes.  The classes based off this should
     * not have any methods, they should all extend Token and only really exist
     * for ease of type checking.
     */
    class TokenDescriptor extends NodeDescriptor {

        /**
         * The token pattern that this descriptor corresponds to.
         */
        TokenPattern tok;

        /**
         * Makes a new token descriptor.
         *
         * @param pattern           the pattern to make the new descriptor
         *                          correspond to.
         */
        public TokenDescriptor(TokenPattern pattern) {
            this.tok        = pattern;
            this.name       = "t_" + gen.getCodeStyle().getMixedCase(pattern.getName(), true);
            this.inherits   = new LinkedList<ProdDescriptor>();
            this.comment    =
                    "This class is a generated node specification class.\n\n" +
                    "NODE TYPE: Token\n" +
                    "NODE PATTERN: \"" + pattern.getPattern() + "\"";
        }
    }

    /**
     * This class stores data about an accessor to be written
     */
    class AccessorDescriptor
    {
        /**
         * The name of the accessor return type.  Note this only stores the
         * type as if the accessor was for a non-repeating element.  If it is
         * repeating, however, it is trivial to derive the correct type names
         * from this.
         */
        String typeName;

        /**
         * The basic name of the accessor method.  Note this only stores the
         * name as if the accessor was for a non-repeating element.  If it is
         * repeating, however, it is trivial to derive the correct method names
         * from this.
         */
        String methodName;

        /**
         * The method comment.  Comment for the accessor that takes an index
         * number in the case of a repeating element.
         */
        String comment;

        /**
         * Auxiliarry method comment.  Used if this is a repeating element to
         * describe the collection accessor.
         */
        String comment2;

        /**
         * Is the corresponding element a repeating one?
         */
        boolean repeating;

        /**
         * Build a new accessor descriptor.
         *
         * @param e             the element to which the accessor corresponds
         * @param append        the number to append to the name of the method
         *                      note that 0 is ignored
         */
        public AccessorDescriptor(ProductionPatternElement e, int append) {
            int id = e.getId();

            // Set the repeating flag
            this.repeating = e.getMaxCount() != 1;

            if (e.isProduction()) {
                ProductionPattern pat = gen.getGrammar().getProductionPatternById(id);
                if (pat.isSingleAlt() && !pat.isSingleElement()) {
                    this.typeName = altDescriptors.get(pat.getAlternative(0)).name;
                    if (!repeating) {
                        this.comment =
                                "Returns a production alternative node of type " + typeName + ".\n" +
                                "The pattern of this node looks like:\n" +
                                this.typeName + " = " + altDescriptors.get(pat.getAlternative(0)).pattern;
                    } else {
                        this.comment =
                                "Returns a production alternative node of type " + typeName + "\n" +
                                "at the given index location.  The pattern of this node looks like:\n" +
                                this.typeName + " = " + altDescriptors.get(pat.getAlternative(0)).pattern;
                        this.comment2 =
                                "Returns a list of production alternative nodes of type " + typeName + ".\n" +
                                "The pattern of these nodes looks like:\n" +
                                this.typeName + " = " + altDescriptors.get(pat.getAlternative(0)).pattern;
                    }
                } else {
                    this.typeName = prodDescriptors.get(pat).name;
                    if (!repeating) {
                        this.comment =
                                "Returns a prduction alternating node of type " + typeName + ".  See\n" +
                                "the documentation on that class for more information.  Note that this\n" +
                                "class is just an interface, and the actual object returned will be of\n" +
                                "one of the implementing classes.";
                    } else {
                        this.comment =
                                "Returns a prduction alternating node of type " + typeName + ".\n" +
                                "This object is the one at the given index within the list of objects of\n" +
                                "this type.  See the documentation on that class for more information.\n" +
                                "Note that this class is just an interface, and the actual object returned\n" +
                                "will be of one of the implementing classes.";
                        this.comment2 =
                                "Returns a list of prduction alternating nodes of type " + typeName + ".\n" +
                                "See\n the documentation on that class for more information.  Note that\n" +
                                "this class is just an interface, and the actual object returned will be\n" +
                                "of one of the implementing classes.";
                    }
                }
            } else {
                TokenPattern pat = gen.getGrammar().getTokenPatternById(id);
                this.typeName = tokenDescriptors.get(pat).name;
                if (!repeating) {
                    this.comment =
                            "Returns a token node of type " + typeName + ".  The pattern of this\n" +
                            "node looks like:\n" +
                            this.typeName + " = " + tokenDescriptors.get(pat).tok.getPattern();
                } else {
                    this.comment =
                            "Returns a token node of type " + typeName + " at a given index.  The\n" +
                            "pattern of this node looks like:\n" +
                            this.typeName + " = " + tokenDescriptors.get(pat).tok.getPattern();
                    this.comment2 =
                            "Returns a list of token nodes of type " + typeName + ".  The pattern\n" +
                            "of these node looks like:\n" +
                            this.typeName + " = " + tokenDescriptors.get(pat).tok.getPattern();
                }
            }

            // Create the method name
            this.methodName = typeName.substring(2);
            if (append != 0) {
                this.methodName += append;
            }
        }

        /**
         * Test for equality.
         *
         * @param o             the object to test
         * @return whether or not o equals this
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if ((o != null) && (o.getClass() == getClass())) {
                AccessorDescriptor a = (AccessorDescriptor) o;
                return (methodName.equals(a.methodName));
            } else {
                return false;
            }
        }

        /**
         * Get a unique hashcode for this.  Used in hashmaps, etc.
         *
         * @return the hashcode
         */
        @Override
        public int hashCode() {
            int hash = 3;
            hash = 71 * hash + (this.methodName != null ? this.methodName.hashCode() : 0);
            return hash;
        }
    }
}