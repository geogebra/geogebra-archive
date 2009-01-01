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
import org.mathpiper.lisp.behaviours.BackQuote;
import org.mathpiper.lisp.UtilityFunctions;
import org.mathpiper.lisp.ConsPointer;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.ConsTraverser;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispExpressionEvaluator;
import org.mathpiper.lisp.SubList;

public class MacroUserFunction extends BranchingUserFunction
{

    public MacroUserFunction(ConsPointer aParameters) throws Exception
    {
        super(aParameters);
        ConsTraverser iter = new ConsTraverser(aParameters);
        int i = 0;
        while (iter.getCons() != null)
        {
            LispError.check(iter.getCons().string() != null, LispError.KLispErrCreatingUserFunction);
            ((BranchParameter) iParameters.get(i)).iHold = true;
            iter.goNext();
            i++;
        }
        unFence();
    }

    public void evaluate(ConsPointer aResult, Environment aEnvironment,
            ConsPointer aArguments) throws Exception
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
        }

        // Walk over all arguments, evaluating them as necessary
        for (i = 0; i < arity; i++)
        {
            arguments[i] = new ConsPointer();
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

        ConsPointer substedBody = new ConsPointer();
        {
            // declare a new local stack.
            aEnvironment.pushLocalFrame(false);
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
                    //TODO remove            CHECKPTR(thisRule);
                    LispError.lispAssert(thisRule != null);

                    st.iRulePrecedence = thisRule.precedence();
                    boolean matches = thisRule.matches(aEnvironment, arguments);
                    if (matches)
                    {
                        st.iSide = 1;

                        BackQuote behaviour = new BackQuote(aEnvironment);
                        UtilityFunctions.internalSubstitute(substedBody, thisRule.body(), behaviour);
                        //              aEnvironment.iEvaluator.Eval(aEnvironment, aResult, thisRule.body());
                        break;
                    }

                    // If rules got inserted, walk back
                    while (thisRule != ((BranchRuleBase) iRules.get(i)) && i > 0)
                    {
                        i--;
                    }
                }
            } catch (Exception e)
            {
                throw e;
            } finally
            {
                aEnvironment.popLocalFrame();
            }
        }


        if (substedBody.getCons() != null)
        {
            aEnvironment.iEvaluator.evaluate(aEnvironment, aResult, substedBody);
        } else
        // No predicate was true: return a new expression with the evaluated
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
        //FINISH:

        /*Trace code */
        if (isTraced())
        {
            ConsPointer tr = new ConsPointer();
            tr.setCons(SubList.getInstance(aArguments.getCons()));
            LispExpressionEvaluator.traceShowLeave(aEnvironment, aResult, tr);
            tr.setCons(null);
        }

    }
}


