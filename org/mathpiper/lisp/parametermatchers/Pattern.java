/* {{{ License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */ //}}}

// :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:folding=explicit:collapseFolds=0:
package org.mathpiper.lisp.parametermatchers;

import org.mathpiper.lisp.cons.Cons;
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsTraverser;
//import org.mathpiper.lisp.AtomCons;
import org.mathpiper.lisp.Environment;
//import org.mathpiper.lisp.SublistCons;
import java.util.*;
import org.mathpiper.builtin.BigNumber;

/**
 *Pattern matching code.
 *
 *General idea: have a class that can match function parameters
 *to a pattern, check for predicates on the arguments, and return
 *whether there was a match.
 *
 *First the pattern is mapped onto the arguments. Then local variables
 *are set. Then the predicates are called. If they all return true,
 *Then the pattern matches, and the locals can stay (the body is expected
 *to use these variables).
 *
 *Class that matches function arguments to a pattern.
 *This class (specifically, the matches() member function) can match
 *function parameters to a pattern, check for predicates on the
 *arguments, and return whether there was a match.
 */
public class Pattern {
    /// List of parameter matches, one for every parameter.
    protected List iParamMatchers = new ArrayList(); //CDeletingArrayGrower<PatternParameter*> iParamMatchers;

    /// List of variables appearing in the pattern.
    protected List iVariables = new ArrayList(); //CArrayGrower<String>

    /// List of predicates which need to be true for a match.
    protected List iPredicates = new ArrayList(); //CDeletingArrayGrower<ConsPointer[] >

    /// Constructor.
    /// \param aEnvironment the underlying Lisp environment
    /// \param aPattern Lisp expression containing the pattern
    /// \param aPostPredicate Lisp expression containing the
    /// postpredicate
    ///
    /// The function MakePatternMatcher() is called for every argument
    /// in \a aPattern, and the resulting pattern matchers are
    /// collected in #iParamMatchers. Additionally, \a aPostPredicate
    /// is copied, and the copy is added to #iPredicates.
    public Pattern(Environment aEnvironment,
            ConsPointer aPattern,
            ConsPointer aPostPredicate) throws Exception {
        ConsTraverser consTraverser = new ConsTraverser(aPattern);

        while (consTraverser.getCons() != null) {
            PatternParameter matcher = makeParamMatcher(aEnvironment, consTraverser.getCons());
            LispError.lispAssert(matcher != null);
            iParamMatchers.add(matcher);
            consTraverser.goNext();
        }
        ConsPointer post = new ConsPointer();
        post.setCons(aPostPredicate.getCons());
        iPredicates.add(post);
    }


    /// Try to match the pattern against \a aArguments.
    /// First, every argument in \a aArguments is matched against the
    /// corresponding PatternParameter in #iParamMatches. If any
    /// match fails, matches() returns false. Otherwise, a temporary
    /// LispLocalFrame is constructed, then setPatternVariables() and
    /// checkPredicates() are called, and then the LispLocalFrame is
    /// immediately deleted. If checkPredicates() returns false, this
    /// function also returns false. Otherwise, setPatternVariables()
    /// is called again, but now in the current LispLocalFrame, and
    /// this function returns true.
    public boolean matches(Environment aEnvironment, ConsPointer aArguments) throws Exception {
        int i;

        ConsPointer[] argumentsPointer = null;
        if (iVariables.size() > 0) {
            argumentsPointer = new ConsPointer[iVariables.size()];
            for (i = 0; i < iVariables.size(); i++) {
                argumentsPointer[i] = new ConsPointer();
            }

        }
        ConsTraverser argumentsTraverser = new ConsTraverser(aArguments);

        for (i = 0; i < iParamMatchers.size(); i++) {
            if (argumentsTraverser.getCons() == null) {
                return false;
            }
            ConsPointer argumentsPointer2 = argumentsTraverser.getPointer();
            if (argumentsPointer2 == null) {
                return false;
            }
            if (!((PatternParameter) iParamMatchers.get(i)).argumentMatches(aEnvironment, argumentsPointer2, argumentsPointer)) {
                return false;
            }
            argumentsTraverser.goNext();
        }
        if (argumentsTraverser.getCons() != null) {
            return false;
        }

        {
            // setCons the local variables.
            aEnvironment.pushLocalFrame(false, "Pattern");
            try {
                setPatternVariables(aEnvironment, argumentsPointer);

                // do the predicates
                if (!checkPredicates(aEnvironment)) {
                    return false;
                }
            } catch (Exception e) {
                throw e;
            } finally {
                aEnvironment.popLocalFrame();
            }
        }

        // setCons the local variables for sure now
        setPatternVariables(aEnvironment, argumentsPointer);

        return true;
    }

    /// Try to match the pattern against \a aArguments.
    /// This function does the same as matches(Environment ,ConsPointer ),
    /// but differs in the type of the arguments.
    public boolean matches(Environment aEnvironment, ConsPointer[] aArguments) throws Exception {
        int i;

        ConsPointer[] arguments = null;
        if (iVariables.size() > 0) {
            arguments = new ConsPointer[iVariables.size()];
        }
        for (i = 0; i < iVariables.size(); i++) {
            arguments[i] = new ConsPointer();
        }


        
        for (i = 0; i < iParamMatchers.size(); i++) {
            LispError.check(i < aArguments.length, "Listed function definitions need at least two parameters.");
            PatternParameter patternParameter = (PatternParameter) iParamMatchers.get(i);
            ConsPointer argument = aArguments[i];
            if (! patternParameter.argumentMatches(aEnvironment, argument, arguments)) {
                return false;
            }
        }

        {
            // setCons the local variables.
            aEnvironment.pushLocalFrame(false, "Pattern");
            try {
                setPatternVariables(aEnvironment, arguments);

                // do the predicates
                if (!checkPredicates(aEnvironment)) {
                    return false;
                }
            } catch (Exception e) {
                throw e;
            } finally {
                aEnvironment.popLocalFrame();
            }
        }

        // setCons the local variables for sure now
        setPatternVariables(aEnvironment, arguments);
        return true;
    }

    /// Construct a pattern matcher out of a Lisp expression.
    /// The result of this function depends on the value of \a aPattern:
    /// - If \a aPattern is a number, the corresponding Number is
    ///   constructed and returned.
    /// - If \a aPattern is an atom, the corresponding AtomCons is
    ///   constructed and returned.
    /// - If \a aPattern is a list of the form <tt>( _ var )<tt>,
    ///   where \c var is an atom, lookUp() is called on \c var. Then
    ///   the correspoding Variable is constructed and returned.
    /// - If \a aPattern is a list of the form <tt>( _ var expr )<tt>,
    ///   where \c var is an atom, lookUp() is called on \c var. Then,
    ///   \a expr is appended to #iPredicates. Finally, the
    ///   correspoding Variable is constructed and returned.
    /// - If \a aPattern is a list of another form, this function
    ///   calls itself on any of the entries in this list. The
    ///   resulting PatternParameter objects are collected in a
    ///   SublistCons, which is returned.
    /// - Otherwise, this function returns #null.
    protected PatternParameter makeParamMatcher(Environment aEnvironment, Cons aPattern) throws Exception {
        if (aPattern == null) {
            return null;
        }
        //LispError.check(aPattern.type().equals("Number"), LispError.INVALID_ARGUMENT);
        if (aPattern.getNumber(aEnvironment.getPrecision()) != null) {
            return new Number((BigNumber) aPattern.getNumber(aEnvironment.getPrecision()));
        }
        // Deal with atoms
        if (aPattern.car() instanceof String) {
            return new Atom( (String) aPattern.car());
        }

        // Else it must be a sublist
        if (aPattern.car() instanceof ConsPointer) {
            // See if it is a variable template:
            ConsPointer sublist = (ConsPointer) aPattern.car();
            //LispError.lispAssert(sublist != null);

            int num = Utility.listLength(sublist);

            // variable matcher here...
            if (num > 1) {
                Cons head = sublist.getCons();
                if (((String) head.car()) == aEnvironment.getTokenHash().lookUp("_")) {
                    Cons second = head.cdr().getCons();
                    if (second.car() instanceof String) {
                        int index = lookUp( (String) second.car());

                        // Make a predicate for the type, if needed
                        if (num > 2) {
                            ConsPointer third = new ConsPointer();

                            Cons predicate = second.cdr().getCons();
                            if ( (predicate.car() instanceof ConsPointer)) {
                                Utility.flatCopy(aEnvironment, third, (ConsPointer) predicate.car());
                            } else {
                                third.setCons(second.cdr().getCons().copy( aEnvironment, false));
                            }

                            String str = (String) second.car();
                            Cons last = third.getCons();
                            while (last.cdr().getCons() != null) {
                                last = last.cdr().getCons();
                            }

                            last.cdr().setCons(org.mathpiper.lisp.cons.AtomCons.getInstance(aEnvironment, str));

                            ConsPointer pred = new ConsPointer();
                            pred.setCons(org.mathpiper.lisp.cons.SublistCons.getInstance(aEnvironment,third.getCons()));

                            iPredicates.add(pred);
                        }
                        return new Variable(index);
                    }
                }
            }

            PatternParameter[] matchers = new PatternParameter[num];

            int i;
            ConsTraverser consTraverser = new ConsTraverser(sublist);
            for (i = 0; i < num; i++) {
                matchers[i] = makeParamMatcher(aEnvironment, consTraverser.getCons());
                LispError.lispAssert(matchers[i] != null);
                consTraverser.goNext();
            }
            return new Sublist(matchers, num);
        }

        return null;
    }

    /// Look up a variable name in #iVariables
    /// \returns index in #iVariables array where \a aVariable
    /// appears.
    ///
    /// If \a aVariable is not in #iVariables, it is added.
    protected int lookUp(String aVariable) {
        int i;
        for (i = 0; i < iVariables.size(); i++) {
            if (iVariables.get(i) == aVariable) {
                return i;
            }
        }
        iVariables.add(aVariable);
        return iVariables.size() - 1;
    }

    /// Set local variables corresponding to the pattern variables.
    /// This function goes through the #iVariables array. A local
    /// variable is made for every entry in the array, and the
    /// corresponding argument is assigned to it.
    protected void setPatternVariables(Environment aEnvironment, ConsPointer[] arguments) throws Exception {
        int i;
        for (i = 0; i < iVariables.size(); i++) {
            // setCons the variable to the new value
            aEnvironment.newLocalVariable((String) iVariables.get(i), arguments[i].getCons());
        }
    }

    /// check whether all predicates are true.
    /// This function goes through all predicates in #iPredicates, and
    /// evaluates them. It returns #false if at least one
    /// of these results IsFalse(). An error is raised if any result
    /// neither IsTrue() nor IsFalse().
    protected boolean checkPredicates(Environment aEnvironment) throws Exception {
        int i;
        for (i = 0; i < iPredicates.size(); i++) {
            ConsPointer pred = new ConsPointer();
            aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, pred, ((ConsPointer) iPredicates.get(i)));
            if (Utility.isFalse(aEnvironment, pred)) {
                return false;
            }


            // If the result is not False, it should be True, else probably something is wrong (the expression returned unevaluated)
            boolean isTrue = Utility.isTrue(aEnvironment, pred);
            if (!isTrue) {
                //TODO this is probably not the right way to generate an error, should we perhaps do a full throw new MathPiperException here?
                String strout;
                aEnvironment.write("The predicate\n\t");
                strout = Utility.printExpression(((ConsPointer) iPredicates.get(i)), aEnvironment, 60);
                aEnvironment.write(strout);
                aEnvironment.write("\nevaluated to\n\t");
                strout = Utility.printExpression(pred, aEnvironment, 60);
                aEnvironment.write(strout);
                aEnvironment.write("\n");

                LispError.check(isTrue, LispError.NON_BOOLEAN_PREDICATE_IN_PATTERN);
            }
        }
        return true;
    }

    public List getParameterMatchers() {
        return iParamMatchers;
    }

    public List getPredicates() {
        return iPredicates;
    }

    public List getVariables() {
        return iVariables;
    }
}

