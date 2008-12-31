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
package org.mathpiper.lisp.userfunctions;

import org.mathpiper.lisp.UserStackInformation;
import org.mathpiper.lisp.ConsPointer;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.ConsTraverser;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.SubList;
import java.util.*;
import org.mathpiper.lisp.LispExpressionEvaluator;

/**
 * A function (usually mathematical) which is defined by one or more rules.
 * This is the basic class which implements functions.  Evaluation is done
 * by consulting a set of rewritng rules.  The body of the first rule that
 * matches is evaluated and its result is returned as the function's result.
 */
public class BranchingUserFunction extends SingleArityUserFunction
{
    /// List of arguments, with corresponding \c iHold property.
    protected Vector iParameters = new Vector(); //CArrayGrower<BranchParameter>

    /// List of rules, sorted on precedence.
    protected Vector iRules = new Vector();//CDeletingArrayGrower<BranchRuleBase*>

    /// List of arguments
    ConsPointer iParamList = new ConsPointer();

    /**
     * Constructor.
     *
     * @param aParameters linked list constaining the names of the arguments
     * @throws java.lang.Exception
     */
    public BranchingUserFunction(ConsPointer aParameters) throws Exception
    {
        // iParamList and #iParameters are setCons from \a aParameters.
        iParamList.setCons(aParameters.getCons());
        ConsTraverser iter = new ConsTraverser(aParameters);
        while (iter.getCons() != null)
        {
            LispError.check(iter.getCons().string() != null, LispError.KLispErrCreatingUserFunction);
            BranchParameter param = new BranchParameter(iter.getCons().string(), false);
            iParameters.add(param);
            iter.goNext();
        }
    }

    /**
     * Evaluate the function with the given arguments.
     * First, all arguments are evaluated by the evaluator associated
     * with aEnvironment, unless the iHold flag of the
     * corresponding parameter is true. Then a new LispLocalFrame is
     * constructed, in which the actual arguments are assigned to the
     * names of the formal arguments, as stored in iParameter. Then
     * all rules in <b>iRules</b> are tried one by one. The body of the
     * first rule that matches is evaluated, and the result is put in
     * aResult. If no rule matches, aResult will recieve a new
     * expression with evaluated arguments.
     * 
     * @param aResult (on output) the result of the evaluation
     * @param aEnvironment the underlying Lisp environment
     * @param aArguments the arguments to the function
     * @throws java.lang.Exception
     */
    public void evaluate(ConsPointer aResult, Environment aEnvironment, ConsPointer aArguments) throws Exception
    {
        int arity = arity();
        int i;

        /*Trace code*/
        if (isTraced())
        {
            ConsPointer tr = new ConsPointer();
            tr.setCons(SubList.getInstance(aArguments.getCons()));
            LispExpressionEvaluator.traceShowEnter(aEnvironment, tr);
            tr.setCons(null);
        }

        ConsTraverser iter = new ConsTraverser(aArguments);
        iter.goNext();

        // unrollable arguments
        ConsPointer[] arguments;
        if (arity == 0)
        {
            arguments = null;
        } else
        {
            LispError.lispAssert(arity > 0);
            arguments = new ConsPointer[arity];
            for (i = 0; i < arity; i++)
            {
                arguments[i] = new ConsPointer();
            }
        }

        // Walk over all arguments, evaluating them as necessary
        for (i = 0; i < arity; i++)
        {
            LispError.check(iter.getCons() != null, LispError.KLispErrWrongNumberOfArgs);
            if (((BranchParameter) iParameters.get(i)).iHold)
            {
                arguments[i].setCons(iter.getCons().copy(false));
            } else
            {
                LispError.check(iter.ptr() != null, LispError.KLispErrWrongNumberOfArgs);
                aEnvironment.iEvaluator.evaluate(aEnvironment, arguments[i], iter.ptr());
            }
            iter.goNext();
        }
	
        /*Trace code */
        if (isTraced())
        {
            //ConsTraverser iter2 = new ConsTraverser(aArguments);
            ConsPointer iter2 = new ConsPointer(aArguments.getCons());

            iter2.goNext();
            for (i = 0; i < arity; i++)
            {
                LispExpressionEvaluator.traceShowArg(aEnvironment, iter2, arguments[i]);

                iter2.goNext();
            }
        }

        // declare a new local stack.
        aEnvironment.pushLocalFrame(fenced());
        try
        {
            // define the local variables.
            for (i = 0; i < arity; i++)
            {
                String variable = ((BranchParameter) iParameters.get(i)).iParameter;
                // setCons the variable to the new value
                aEnvironment.newLocal(variable, arguments[i].getCons());
            }

            // walk the rules database, returning the evaluated result if the
            // predicate is true.
            int nrRules = iRules.size();
            UserStackInformation st = aEnvironment.iEvaluator.stackInformation();
            for (i = 0; i < nrRules; i++)
            {
                BranchRuleBase thisRule = ((BranchRuleBase) iRules.get(i));
                LispError.lispAssert(thisRule != null);

                st.iRulePrecedence = thisRule.precedence();
                boolean matches = thisRule.matches(aEnvironment, arguments);
                if (matches)
                {
                    st.iSide = 1;
                    aEnvironment.iEvaluator.evaluate(aEnvironment, aResult, thisRule.body());

                    /*Trace code */
                    if (isTraced())
                    {
                        ConsPointer tr = new ConsPointer();
                        tr.setCons(SubList.getInstance(aArguments.getCons()));
                        LispExpressionEvaluator.traceShowLeave(aEnvironment, aResult, tr);
                        tr.setCons(null);
                    }

                    return;
                }

                // If rules got inserted, walk back
                while (thisRule != ((BranchRuleBase) iRules.get(i)) && i > 0)
                {
                    i--;
                }
            }            // No predicate was true: return a new expression with the evaluated
            // arguments.
            {
                ConsPointer full = new ConsPointer();
                full.setCons(aArguments.getCons().copy(false));
                if (arity == 0)
                {
                    full.getCons().rest().setCons(null);
                } else
                {
                    full.getCons().rest().setCons(arguments[0].getCons());
                    for (i = 0; i < arity - 1; i++)
                    {
                        arguments[i].getCons().rest().setCons(arguments[i + 1].getCons());
                    }
                }
                aResult.setCons(SubList.getInstance(full.getCons()));
            }

            /* Trace code */
            if (isTraced())
            {
                ConsPointer tr = new ConsPointer();
                tr.setCons(SubList.getInstance(aArguments.getCons()));
                LispExpressionEvaluator.traceShowLeave(aEnvironment, aResult, tr);
                tr.setCons(null);
            }

        } catch (Exception e)
        {
            throw e;
        } finally
        {
            aEnvironment.popLocalFrame();
        }
    }

    /**
     * Put an argument on hold.
     * The \c iHold flag of the corresponding argument is setCons. This
     * implies that this argument is not evaluated by evaluate().
     * 
     * @param aVariable name of argument to put un hold
     */
    public void holdArgument(String aVariable)
    {
        int i;
        int nrc = iParameters.size();
        for (i = 0; i < nrc; i++)
        {
            if (((BranchParameter) iParameters.get(i)).iParameter == aVariable)
            {
                ((BranchParameter) iParameters.get(i)).iHold = true;
            }
        }
    }

    /**
     * Return true if the arity of the function equals \a aArity.
     * 
     * @param aArity
     * @return true of the arities match.
     */
    public boolean isArity(int aArity)
    {
        return (arity() == aArity);
    }

    /**
     * Return the arity (number of arguments) of the function.
     *
     * @return the arity of the function
     */
    public int arity()
    {
        return iParameters.size();
    }

    /**
     *  Add a BranchRule to the list of rules.
     * See: insertRule()
     * 
     * @param aPrecedence
     * @param aPredicate
     * @param aBody
     * @throws java.lang.Exception
     */
    public void declareRule(int aPrecedence, ConsPointer aPredicate, ConsPointer aBody) throws Exception
    {
        // New branching rule.
        BranchRule newRule = new BranchRule(aPrecedence, aPredicate, aBody);
        LispError.check(newRule != null, LispError.KLispErrCreatingRule);

        insertRule(aPrecedence, newRule);
    }

    /**
     * Add a BranchRuleTruePredicate to the list of rules.
     * See: insertRule()
     * 
     * @param aPrecedence
     * @param aBody
     * @throws java.lang.Exception
     */
    public void declareRule(int aPrecedence, ConsPointer aBody) throws Exception
    {
        // New branching rule.
        BranchRule newRule = new BranchRuleTruePredicate(aPrecedence, aBody);
        LispError.check(newRule != null, LispError.KLispErrCreatingRule);

        insertRule(aPrecedence, newRule);
    }

    /**
     *  Add a BranchPattern to the list of rules.
     *  See: insertRule()
     * 
     * @param aPrecedence
     * @param aPredicate
     * @param aBody
     * @throws java.lang.Exception
     */
    public void declarePattern(int aPrecedence, ConsPointer aPredicate, ConsPointer aBody) throws Exception
    {
        // New branching rule.
        BranchPattern newRule = new BranchPattern(aPrecedence, aPredicate, aBody);
        LispError.check(newRule != null, LispError.KLispErrCreatingRule);

        insertRule(aPrecedence, newRule);
    }

    /**
     * Insert any BranchRuleBase object in the list of rules.
     * This function does the real work for declareRule() and
     * declarePattern(): it inserts the rule in <b>iRules</b>, while
     * keeping it sorted. The algorithm is O(log n), where
     * n denotes the number of rules.
     * 
     * @param aPrecedence
     * @param newRule
     */
    void insertRule(int aPrecedence, BranchRuleBase newRule)
    {
        // Find place to insert



         
         
         
          int low, high, mid;
        low = 0;
        high = iRules.size();

        // Constant time: find out if the precedence is before any of the
        // currently defined rules or past them.
        if (high > 0)
        {
            if (((BranchRuleBase) iRules.get(0)).precedence() > aPrecedence)
            {
                mid = 0;
                // Insert it
                iRules.add(mid, newRule);
                return;
            }
            if (((BranchRuleBase) iRules.get(high - 1)).precedence() < aPrecedence)
            {
                mid = high;
                // Insert it
                iRules.add(mid, newRule);
                return;
            }
        }

        // Otherwise, O(log n) search algorithm for place to insert
        for (;;)
        {
            if (low >= high)
            {
                mid = low;
                // Insert it
                iRules.add(mid, newRule);
                return;
            }
            mid = (low + high) >> 1;

            if (((BranchRuleBase) iRules.get(mid)).precedence() > aPrecedence)
            {
                high = mid;
            } else if (((BranchRuleBase) iRules.get(mid)).precedence() < aPrecedence)
            {
                low = (++mid);
            } else
            {
                // Insert it
                iRules.add(mid, newRule);
                return;
            }
        }
    }

    /**
     * Return the argument list, stored in #iParamList.
     * 
     * @return a ConsPointer
     */
    public ConsPointer argList()
    {
        return iParamList;
    }
}

