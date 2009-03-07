/*
 * RecursiveDescentParser.java
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

package net.percederberg.grammatica.parser;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A recursive descent parser. This parser handles LL(n) grammars,
 * selecting the appropriate pattern to parse based on the next few
 * tokens. The parser is more efficient the fewer look-ahead tokens
 * that is has to consider.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.5
 */
public class RecursiveDescentParser extends Parser {

    /**
     * Creates a new parser.
     *
     * @param input          the input stream to read from
     *
     * @throws ParserCreationException if the tokenizer couldn't be
     *             initialized correctly
     *
     * @since 1.5
     */
    public RecursiveDescentParser(Reader input) throws ParserCreationException {
        super(input);
    }

    /**
     * Creates a new parser.
     *
     * @param input          the input stream to read from
     * @param analyzer       the analyzer callback to use
     *
     * @throws ParserCreationException if the tokenizer couldn't be
     *             initialized correctly
     *
     * @since 1.5
     */
    public RecursiveDescentParser(Reader input, Analyzer analyzer)
        throws ParserCreationException {

        super(input, analyzer);
    }

    /**
     * Creates a new parser.
     *
     * @param tokenizer      the tokenizer to use
     */
    public RecursiveDescentParser(Tokenizer tokenizer) {
        super(tokenizer);
    }

    /**
     * Creates a new parser.
     *
     * @param tokenizer      the tokenizer to use
     * @param analyzer       the analyzer callback to use
     */
    public RecursiveDescentParser(Tokenizer tokenizer, Analyzer analyzer) {
        super(tokenizer, analyzer);
    }

    /**
     * Adds a new production pattern to the parser. The pattern will
     * be added last in the list. The first pattern added is assumed
     * to be the starting point in the grammar. The pattern will be
     * validated against the grammar type to some extent.
     *
     * @param pattern        the pattern to add
     *
     * @throws ParserCreationException if the pattern couldn't be
     *             added correctly to the parser
     */
    public void addPattern(ProductionPattern pattern)
        throws ParserCreationException {

        // Check for empty matches
        if (pattern.isMatchingEmpty()) {
            throw new ParserCreationException(
                ParserCreationException.INVALID_PRODUCTION_ERROR,
                pattern.getName(),
                "zero elements can be matched (minimum is one)");
        }

        // Check for left-recusive patterns
        if (pattern.isLeftRecursive()) {
            throw new ParserCreationException(
                ParserCreationException.INVALID_PRODUCTION_ERROR,
                pattern.getName(),
                "left recursive patterns are not allowed");
        }

        // Add pattern
        super.addPattern(pattern);
    }

    /**
     * Initializes the parser. All the added production patterns will
     * be analyzed for ambiguities and errors. This method also
     * initializes the internal data structures used during the
     * parsing.
     *
     * @throws ParserCreationException if the parser couldn't be
     *             initialized correctly
     */
    public void prepare() throws ParserCreationException {
        Iterator  iter;

        // Performs production pattern checks
        super.prepare();
        setInitialized(false);

        // Calculate production look-ahead sets
        iter = getPatterns().iterator();
        while (iter.hasNext()) {
            calculateLookAhead((ProductionPattern) iter.next());
        }

        // Set initialized flag
        setInitialized(true);
    }

    /**
     * Parses the input stream and creates a parse tree.
     *
     * @return the parse tree
     *
     * @throws ParseException if the input couldn't be parsed
     *             correctly
     */
    protected Node parseStart() throws ParseException {
        Token      token;
        Node       node;
        ArrayList  list;

        node = parsePattern(getStartPattern());
        token = peekToken(0);
        if (token != null) {
            list = new ArrayList(1);
            list.add("<EOF>");
            throw new ParseException(
                ParseException.UNEXPECTED_TOKEN_ERROR,
                token.toShortString(),
                list,
                token.getStartLine(),
                token.getStartColumn());
        }
        return node;
    }

    /**
     * Parses a production pattern. A parse tree node may or may not
     * be created depending on the analyzer callbacks.
     *
     * @param pattern        the production pattern to parse
     *
     * @return the parse tree node created, or null
     *
     * @throws ParseException if the input couldn't be parsed
     *             correctly
     */
    private Node parsePattern(ProductionPattern pattern)
        throws ParseException {

        ProductionPatternAlternative  alt;
        ProductionPatternAlternative  defaultAlt;

        defaultAlt = pattern.getDefaultAlternative();
        for (int i = 0; i < pattern.getAlternativeCount(); i++) {
            alt = pattern.getAlternative(i);
            if (defaultAlt != alt && isNext(alt)) {
                return parseAlternative(alt);
            }
        }
        if (defaultAlt == null || !isNext(defaultAlt)) {
            throwParseException(findUnion(pattern));
        }
        return parseAlternative(defaultAlt);
    }

    /**
     * Parses a production pattern alternative. A parse tree node may
     * or may not be created depending on the analyzer callbacks.
     *
     * @param alt            the production pattern alternative
     *
     * @return the parse tree node created, or null
     *
     * @throws ParseException if the input couldn't be parsed
     *             correctly
     */
    private Node parseAlternative(ProductionPatternAlternative alt)
        throws ParseException {

        Production  node;

        node = newProduction(alt.getPattern());
        enterNode(node);
        for (int i = 0; i < alt.getElementCount(); i++) {
            try {
                parseElement(node, alt.getElement(i));
            } catch (ParseException e) {
                addError(e, true);
                nextToken();
                i--;
            }
        }
        return exitNode(node);
    }

    /**
     * Parses a production pattern element. All nodes parsed may or
     * may not be added to the parse tree node specified, depending
     * on the analyzer callbacks.
     *
     * @param node           the production parse tree node
     * @param elem           the production pattern element to parse
     *
     * @throws ParseException if the input couldn't be parsed
     *             correctly
     */
    private void parseElement(Production node,
                              ProductionPatternElement elem)
        throws ParseException {

        Node  child;

        for (int i = 0; i < elem.getMaxCount(); i++) {
            if (i < elem.getMinCount() || isNext(elem)) {
                if (elem.isToken()) {
                    child = nextToken(elem.getId());
                    enterNode(child);
                    addNode(node, exitNode(child));
                } else {
                    child = parsePattern(getPattern(elem.getId()));
                    addNode(node, child);
                }
            } else {
                break;
            }
        }
    }

    /**
     * Checks if the next tokens match a production pattern. The
     * pattern look-ahead set will be used if existing, otherwise
     * this method returns false.
     *
     * @param pattern        the pattern to check
     *
     * @return true if the next tokens match, or
     *         false otherwise
     */
    private boolean isNext(ProductionPattern pattern) {
        LookAheadSet  set = pattern.getLookAhead();

        if (set == null) {
            return false;
        } else {
            return set.isNext(this);
        }
    }

    /**
     * Checks if the next tokens match a production pattern
     * alternative. The pattern alternative look-ahead set will be
     * used if existing, otherwise this method returns false.
     *
     * @param alt            the pattern alternative to check
     *
     * @return true if the next tokens match, or
     *         false otherwise
     */
    private boolean isNext(ProductionPatternAlternative alt) {
        LookAheadSet  set = alt.getLookAhead();

        if (set == null) {
            return false;
        } else {
            return set.isNext(this);
        }
    }

    /**
     * Checks if the next tokens match a production pattern element.
     * If the element has a look-ahead set it will be used, otherwise
     * the look-ahead set of the referenced production or token will
     * be used.
     *
     * @param elem           the pattern element to check
     *
     * @return true if the next tokens match, or
     *         false otherwise
     */
    private boolean isNext(ProductionPatternElement elem) {
        LookAheadSet  set = elem.getLookAhead();

        if (set != null) {
            return set.isNext(this);
        } else if (elem.isToken()) {
            return elem.isMatch(peekToken(0));
        } else {
            return isNext(getPattern(elem.getId()));
        }
    }

    /**
     * Calculates the look-ahead needed for the specified production
     * pattern. This method attempts to resolve any conflicts and
     * stores the results in the pattern look-ahead object.
     *
     * @param pattern        the production pattern
     *
     * @throws ParserCreationException if the look-ahead set couldn't
     *             be determined due to inherent ambiguities
     */
    private void calculateLookAhead(ProductionPattern pattern)
        throws ParserCreationException {

        ProductionPatternAlternative  alt;
        LookAheadSet                  result;
        LookAheadSet[]                alternatives;
        LookAheadSet                  conflicts;
        LookAheadSet                  previous = new LookAheadSet(0);
        int                           length = 1;
        int                           i;
        CallStack                     stack = new CallStack();

        // Calculate simple look-ahead
        stack.push(pattern.getName(), 1);
        result = new LookAheadSet(1);
        alternatives = new LookAheadSet[pattern.getAlternativeCount()];
        for (i = 0; i < pattern.getAlternativeCount(); i++) {
            alt = pattern.getAlternative(i);
            alternatives[i] = findLookAhead(alt, 1, 0, stack, null);
            alt.setLookAhead(alternatives[i]);
            result.addAll(alternatives[i]);
        }
        if (pattern.getLookAhead() == null) {
            pattern.setLookAhead(result);
        }
        conflicts = findConflicts(pattern, 1);

        // Resolve conflicts
        while (!conflicts.isEmpty()) {
            length++;
            stack.clear();
            stack.push(pattern.getName(), length);
            conflicts.addAll(previous);
            for (i = 0; i < pattern.getAlternativeCount(); i++) {
                alt = pattern.getAlternative(i);
                if (alternatives[i].hasIntersection(conflicts)) {
                    alternatives[i] = findLookAhead(alt,
                                                    length,
                                                    0,
                                                    stack,
                                                    conflicts);
                    alt.setLookAhead(alternatives[i]);
                }
                if (alternatives[i].hasIntersection(conflicts)) {
                    if (pattern.getDefaultAlternative() == null) {
                        pattern.setDefaultAlternative(i);
                    } else if (pattern.getDefaultAlternative() != alt) {
                        result = alternatives[i].createIntersection(conflicts);
                        throwAmbiguityException(pattern.getName(),
                                                null,
                                                result);
                    }
                }
            }
            previous = conflicts;
            conflicts = findConflicts(pattern, length);
        }

        // Resolve conflicts inside rules
        for (i = 0; i < pattern.getAlternativeCount(); i++) {
            calculateLookAhead(pattern.getAlternative(i), 0);
        }
    }

    /**
     * Calculates the look-aheads needed for the specified pattern
     * alternative. This method attempts to resolve any conflicts in
     * optional elements by recalculating look-aheads for referenced
     * productions.
     *
     * @param alt            the production pattern alternative
     * @param pos            the pattern element position
     *
     * @throws ParserCreationException if the look-ahead set couldn't
     *             be determined due to inherent ambiguities
     */
    private void calculateLookAhead(ProductionPatternAlternative alt,
                                    int pos)
        throws ParserCreationException {

        ProductionPattern         pattern;
        ProductionPatternElement  elem;
        LookAheadSet              first;
        LookAheadSet              follow;
        LookAheadSet              conflicts;
        LookAheadSet              previous = new LookAheadSet(0);
        String                    location;
        int                       length = 1;

        // Check trivial cases
        if (pos >= alt.getElementCount()) {
            return;
        }

        // Check for non-optional element
        pattern = alt.getPattern();
        elem = alt.getElement(pos);
        if (elem.getMinCount() == elem.getMaxCount()) {
            calculateLookAhead(alt, pos + 1);
            return;
        }

        // Calculate simple look-aheads
        first = findLookAhead(elem, 1, new CallStack(), null);
        follow = findLookAhead(alt, 1, pos + 1, new CallStack(), null);

        // Resolve conflicts
        location = "at position " + (pos + 1);
        conflicts = findConflicts(pattern.getName(),
                                  location,
                                  first,
                                  follow);
        while (!conflicts.isEmpty()) {
            length++;
            conflicts.addAll(previous);
            first = findLookAhead(elem,
                                  length,
                                  new CallStack(),
                                  conflicts);
            follow = findLookAhead(alt,
                                   length,
                                   pos + 1,
                                   new CallStack(),
                                   conflicts);
            first = first.createCombination(follow);
            elem.setLookAhead(first);
            if (first.hasIntersection(conflicts)) {
                first = first.createIntersection(conflicts);
                throwAmbiguityException(pattern.getName(), location, first);
            }
            previous = conflicts;
            conflicts = findConflicts(pattern.getName(),
                                      location,
                                      first,
                                      follow);
        }

        // Check remaining elements
        calculateLookAhead(alt, pos + 1);
    }

    /**
     * Finds the look-ahead set for a production pattern. The maximum
     * look-ahead length must be specified. It is also possible to
     * specify a look-ahead set filter, which will make sure that
     * unnecessary token sequences will be avoided.
     *
     * @param pattern        the production pattern
     * @param length         the maximum look-ahead length
     * @param stack          the call stack used for loop detection
     * @param filter         the look-ahead set filter
     *
     * @return the look-ahead set for the production pattern
     *
     * @throws ParserCreationException if an infinite loop was found
     *             in the grammar
     */
    private LookAheadSet findLookAhead(ProductionPattern pattern,
                                       int length,
                                       CallStack stack,
                                       LookAheadSet filter)
        throws ParserCreationException {

        LookAheadSet  result;
        LookAheadSet  temp;

        // Check for infinite loop
        if (stack.contains(pattern.getName(), length)) {
            throw new ParserCreationException(
                ParserCreationException.INFINITE_LOOP_ERROR,
                pattern.getName(),
                (String) null);
        }

        // Find pattern look-ahead
        stack.push(pattern.getName(), length);
        result = new LookAheadSet(length);
        for (int i = 0; i < pattern.getAlternativeCount(); i++) {
            temp = findLookAhead(pattern.getAlternative(i),
                                 length,
                                 0,
                                 stack,
                                 filter);
            result.addAll(temp);
        }
        stack.pop();

        return result;
    }

    /**
     * Finds the look-ahead set for a production pattern alternative.
     * The pattern position and maximum look-ahead length must be
     * specified. It is also possible to specify a look-ahead set
     * filter, which will make sure that unnecessary token sequences
     * will be avoided.
     *
     * @param alt            the production pattern alternative
     * @param length         the maximum look-ahead length
     * @param pos            the pattern element position
     * @param stack          the call stack used for loop detection
     * @param filter         the look-ahead set filter
     *
     * @return the look-ahead set for the pattern alternative
     *
     * @throws ParserCreationException if an infinite loop was found
     *             in the grammar
     */
    private LookAheadSet findLookAhead(ProductionPatternAlternative alt,
                                       int length,
                                       int pos,
                                       CallStack stack,
                                       LookAheadSet filter)
        throws ParserCreationException {

        LookAheadSet  first;
        LookAheadSet  follow;
        LookAheadSet  overlaps;

        // Check trivial cases
        if (length <= 0 || pos >= alt.getElementCount()) {
            return new LookAheadSet(0);
        }

        // Find look-ahead for this element
        first = findLookAhead(alt.getElement(pos), length, stack, filter);
        if (alt.getElement(pos).getMinCount() == 0) {
            first.addEmpty();
        }

        // Find remaining look-ahead
        if (filter == null) {
            length -= first.getMinLength();
            if (length > 0) {
                follow = findLookAhead(alt, length, pos + 1, stack, null);
                first = first.createCombination(follow);
            }
        } else if (filter.hasOverlap(first)) {
            overlaps = first.createOverlaps(filter);
            length -= overlaps.getMinLength();
            filter = filter.createFilter(overlaps);
            follow = findLookAhead(alt, length, pos + 1, stack, filter);
            first.removeAll(overlaps);
            first.addAll(overlaps.createCombination(follow));
        }

        return first;
    }

    /**
     * Finds the look-ahead set for a production pattern element. The
     * maximum look-ahead length must be specified. This method takes
     * the element repeats into consideration when creating the
     * look-ahead set, but does NOT include an empty sequence even if
     * the minimum count is zero (0). It is also possible to specify a
     * look-ahead set filter, which will make sure that unnecessary
     * token sequences will be avoided.
     *
     * @param elem           the production pattern element
     * @param length         the maximum look-ahead length
     * @param stack          the call stack used for loop detection
     * @param filter         the look-ahead set filter
     *
     * @return the look-ahead set for the pattern element
     *
     * @throws ParserCreationException if an infinite loop was found
     *             in the grammar
     */
    private LookAheadSet findLookAhead(ProductionPatternElement elem,
                                       int length,
                                       CallStack stack,
                                       LookAheadSet filter)
        throws ParserCreationException {

        LookAheadSet  result;
        LookAheadSet  first;
        LookAheadSet  follow;
        int           max;

        // Find initial element look-ahead
        first = findLookAhead(elem, length, 0, stack, filter);
        result = new LookAheadSet(length);
        result.addAll(first);
        if (filter == null || !filter.hasOverlap(result)) {
            return result;
        }

        // Handle element repetitions
        if (elem.getMaxCount() == Integer.MAX_VALUE) {
            first = first.createRepetitive();
        }
        max = Math.min(length, elem.getMaxCount());
        for (int i = 1; i < max; i++) {
            first = first.createOverlaps(filter);
            if (first.isEmpty() || first.getMinLength() >= length) {
                break;
            }
            follow = findLookAhead(elem,
                                   length,
                                   0,
                                   stack,
                                   filter.createFilter(first));
            first = first.createCombination(follow);
            result.addAll(first);
        }

        return result;
    }

    /**
     * Finds the look-ahead set for a production pattern element. The
     * maximum look-ahead length must be specified. This method does
     * NOT take the element repeat into consideration when creating
     * the look-ahead set. It is also possible to specify a look-ahead
     * set filter, which will make sure that unnecessary token
     * sequences will be avoided.
     *
     * @param elem           the production pattern element
     * @param length         the maximum look-ahead length
     * @param dummy          a parameter to distinguish the method
     * @param stack          the call stack used for loop detection
     * @param filter         the look-ahead set filter
     *
     * @return the look-ahead set for the pattern element
     *
     * @throws ParserCreationException if an infinite loop was found
     *             in the grammar
     */
    private LookAheadSet findLookAhead(ProductionPatternElement elem,
                                       int length,
                                       int dummy,
                                       CallStack stack,
                                       LookAheadSet filter)
        throws ParserCreationException {

        LookAheadSet       result;
        ProductionPattern  pattern;

        if (elem.isToken()) {
            result = new LookAheadSet(length);
            result.add(elem.getId());
        } else {
            pattern = getPattern(elem.getId());
            result = findLookAhead(pattern, length, stack, filter);
            if (stack.contains(pattern.getName())) {
                result = result.createRepetitive();
            }
        }

        return result;
    }

    /**
     * Returns a look-ahead set with all conflics between alternatives
     * in a production pattern.
     *
     * @param pattern        the production pattern
     * @param maxLength      the maximum token sequence length
     *
     * @return a look-ahead set with the conflicts found
     *
     * @throws ParserCreationException if an inherent ambiguity was
     *             found among the look-ahead sets
     */
    private LookAheadSet findConflicts(ProductionPattern pattern,
                                       int maxLength)
        throws ParserCreationException {

        LookAheadSet  result = new LookAheadSet(maxLength);
        LookAheadSet  set1;
        LookAheadSet  set2;

        for (int i = 0; i < pattern.getAlternativeCount(); i++) {
            set1 = pattern.getAlternative(i).getLookAhead();
            for (int j = 0; j < i; j++) {
                set2 = pattern.getAlternative(j).getLookAhead();
                result.addAll(set1.createIntersection(set2));
            }
        }
        if (result.isRepetitive()) {
            throwAmbiguityException(pattern.getName(), null, result);
        }
        return result;
    }

    /**
     * Returns a look-ahead set with all conflicts between two
     * look-ahead sets.
     *
     * @param pattern        the pattern name being analyzed
     * @param location       the pattern location
     * @param set1           the first look-ahead set
     * @param set2           the second look-ahead set
     *
     * @return a look-ahead set with the conflicts found
     *
     * @throws ParserCreationException if an inherent ambiguity was
     *             found among the look-ahead sets
     */
    private LookAheadSet findConflicts(String pattern,
                                       String location,
                                       LookAheadSet set1,
                                       LookAheadSet set2)
        throws ParserCreationException {

        LookAheadSet  result;

        result = set1.createIntersection(set2);
        if (result.isRepetitive()) {
            throwAmbiguityException(pattern, location, result);
        }
        return result;
    }

    /**
     * Returns the union of all alternative look-ahead sets in a
     * production pattern.
     *
     * @param pattern        the production pattern
     *
     * @return a unified look-ahead set
     */
    private LookAheadSet findUnion(ProductionPattern pattern) {
        LookAheadSet  result;
        int           length = 0;
        int           i;

        for (i = 0; i < pattern.getAlternativeCount(); i++) {
            result = pattern.getAlternative(i).getLookAhead();
            if (result.getMaxLength() > length) {
                length = result.getMaxLength();
            }
        }
        result = new LookAheadSet(length);
        for (i = 0; i < pattern.getAlternativeCount(); i++) {
            result.addAll(pattern.getAlternative(i).getLookAhead());
        }

        return result;
    }

    /**
     * Throws a parse exception that matches the specified look-ahead
     * set. This method will take into account any initial matching
     * tokens in the look-ahead set.
     *
     * @param set            the look-ahead set to match
     *
     * @throws ParseException always thrown by this method
     */
    private void throwParseException(LookAheadSet set)
        throws ParseException {

        Token      token;
        ArrayList  list = new ArrayList();
        int[]      initials;

        // Read tokens until mismatch
        while (set.isNext(this, 1)) {
            set = set.createNextSet(nextToken().getId());
        }

        // Find next token descriptions
        initials = set.getInitialTokens();
        for (int i = 0; i < initials.length; i++) {
            list.add(getTokenDescription(initials[i]));
        }

        // Create exception
        token = nextToken();
        throw new ParseException(ParseException.UNEXPECTED_TOKEN_ERROR,
                                 token.toShortString(),
                                 list,
                                 token.getStartLine(),
                                 token.getStartColumn());
    }

    /**
     * Throws a parser creation exception for an ambiguity. The
     * specified look-ahead set contains the token conflicts to be
     * reported.
     *
     * @param pattern        the production pattern name
     * @param location       the production pattern location, or null
     * @param set            the look-ahead set with conflicts
     *
     * @throws ParserCreationException always thrown by this method
     */
    private void throwAmbiguityException(String pattern,
                                         String location,
                                         LookAheadSet set)
        throws ParserCreationException {

        ArrayList  list = new ArrayList();
        int[]      initials;

        // Find next token descriptions
        initials = set.getInitialTokens();
        for (int i = 0; i < initials.length; i++) {
            list.add(getTokenDescription(initials[i]));
        }

        // Create exception
        throw new ParserCreationException(
            ParserCreationException.INHERENT_AMBIGUITY_ERROR,
            pattern,
            location,
            list);
    }


    /**
     * A name value stack. This stack is used to detect loops and
     * repetitions of the same production during look-ahead analysis.
     */
    class CallStack {

        /**
         * A stack with names.
         */
        private ArrayList nameStack = new ArrayList();

        /**
         * A stack with values.
         */
        private ArrayList valueStack = new ArrayList();

        /**
         * Checks if the specified name is on the stack.
         *
         * @param name           the name to search for
         *
         * @return true if the name is on the stack, or
         *         false otherwise
         */
        public boolean contains(String name) {
            return nameStack.contains(name);
        }

        /**
         * Checks if the specified name and value combination is on
         * the stack.
         *
         * @param name           the name to search for
         * @param value          the value to search for
         *
         * @return true if the combination is on the stack, or
         *         false otherwise
         */
        public boolean contains(String name, int value) {
            Integer  obj = new Integer(value);

            for (int i = 0; i < nameStack.size(); i++) {
                if (nameStack.get(i).equals(name)
                 && valueStack.get(i).equals(obj)) {

                     return true;
                }
            }
            return false;
        }

        /**
         * Clears the stack. This method removes all elements on the
         * stack.
         */
        public void clear() {
            nameStack.clear();
            valueStack.clear();
        }

        /**
         * Adds a new element to the top of the stack.
         *
         * @param name           the stack name
         * @param value          the stack value
         */
        public void push(String name, int value) {
            nameStack.add(name);
            valueStack.add(new Integer(value));
        }

        /**
         * Removes the top element of the stack.
         */
        public void pop() {
            if (nameStack.size() > 0) {
                nameStack.remove(nameStack.size() - 1);
                valueStack.remove(valueStack.size() - 1);
            }
        }
    }
}
