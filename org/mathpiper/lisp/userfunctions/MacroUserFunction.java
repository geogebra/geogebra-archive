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

import org.mathpiper.exceptions.EvaluationException;
import org.mathpiper.lisp.stacks.UserStackInformation;
import org.mathpiper.lisp.behaviours.BackQuoteSubstitute;
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsTraverser;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.Evaluator;
import org.mathpiper.lisp.LispExpressionEvaluator;
import org.mathpiper.lisp.cons.SublistCons;

public class MacroUserFunction extends SingleArityBranchingUserFunction {

    public MacroUserFunction(ConsPointer aParameters, String functionName) throws Exception {
        super(aParameters, functionName);
        ConsTraverser parameterTraverser = new ConsTraverser(aParameters);
        int i = 0;
        while (parameterTraverser.getCons() != null) {

            //LispError.check(parameterTraverser.car() != null, LispError.CREATING_USER_FUNCTION);
            try{
                LispError.check(parameterTraverser.car() instanceof String, LispError.CREATING_USER_FUNCTION);
            }catch(EvaluationException ex)
            {
                throw new EvaluationException(ex.getMessage() + " Function: " + this.functionName + "  ",-1) ;
            }//end catch.


            ((FunctionParameter) iParameters.get(i)).iHold = true;
            parameterTraverser.goNext();
            i++;
        }
        //Macros are all unfenced.
        unFence();

        this.functionType = "macro";
    }

    public void evaluate(Environment aEnvironment, ConsPointer aResult, ConsPointer aArgumentsPointer) throws Exception {
         int arity = arity();
        ConsPointer[] argumentsResultPointerArray = evaluateArguments(aEnvironment, aArgumentsPointer);
        int parameterIndex;

       

        ConsPointer substitutedBodyPointer = new ConsPointer();

        //Create a new local variable frame that is unfenced (false = unfenced).
        aEnvironment.pushLocalFrame(false, this.functionName);

        try {
            // define the local variables.
            for (parameterIndex = 0; parameterIndex < arity; parameterIndex++) {
                String variable = ((FunctionParameter) iParameters.get(parameterIndex)).iParameter;

                // setCons the variable to the new value
                aEnvironment.newLocalVariable(variable, argumentsResultPointerArray[parameterIndex].getCons());
            }

            // walk the rules database, returning the evaluated result if the
            // predicate is true.
            int numberOfRules = iBranchRules.size();
            UserStackInformation userStackInformation = aEnvironment.iLispExpressionEvaluator.stackInformation();
            for (parameterIndex = 0; parameterIndex < numberOfRules; parameterIndex++) {
                Branch thisRule = ((Branch) iBranchRules.get(parameterIndex));
                //TODO remove            CHECKPTR(thisRule);
                LispError.lispAssert(thisRule != null);

                userStackInformation.iRulePrecedence = thisRule.getPrecedence();

                boolean matches = thisRule.matches(aEnvironment, argumentsResultPointerArray);

                if (matches) {
                    /* Rule dump trace code. */
                    if (isTraced() && showFlag) {
                        ConsPointer argumentsPointer = new ConsPointer();
                        argumentsPointer.setCons(SublistCons.getInstance(aArgumentsPointer.getCons()));
                        String ruleDump = org.mathpiper.lisp.Utility.dumpRule(thisRule, aEnvironment, this);
                        Evaluator.traceShowRule(aEnvironment, argumentsPointer, ruleDump);
                    }
                    userStackInformation.iSide = 1;

                    BackQuoteSubstitute backQuoteSubstitute = new BackQuoteSubstitute(aEnvironment);

                    ConsPointer originalBodyPointer =  thisRule.getBodyPointer();
                    Utility.substitute(substitutedBodyPointer,originalBodyPointer, backQuoteSubstitute);
                    //              aEnvironment.iLispExpressionEvaluator.Eval(aEnvironment, aResult, thisRule.body());
                    break;
                }

                // If rules got inserted, walk back
                while (thisRule != ((Branch) iBranchRules.get(parameterIndex)) && parameterIndex > 0) {
                    parameterIndex--;
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            aEnvironment.popLocalFrame();
        }



        if (substitutedBodyPointer.getCons() != null) {
            //Note:tk:substituted body must be evaluated after the local frame has been popped.
            aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aResult, substitutedBodyPointer);
        } else // No predicate was true: return a new expression with the evaluated
        // arguments.
        {
            ConsPointer full = new ConsPointer();
            full.setCons(aArgumentsPointer.getCons().copy(false));
            if (arity == 0) {
                full.cdr().setCons(null);
            } else {
                full.cdr().setCons(argumentsResultPointerArray[0].getCons());
                for (parameterIndex = 0; parameterIndex < arity - 1; parameterIndex++) {
                    argumentsResultPointerArray[parameterIndex].cdr().setCons(argumentsResultPointerArray[parameterIndex + 1].getCons());
                }
            }
            aResult.setCons(SublistCons.getInstance(full.getCons()));
        }
        //FINISH:

        /*Leave trace code */
        if (isTraced() && showFlag) {
            ConsPointer tr = new ConsPointer();
            tr.setCons(SublistCons.getInstance(aArgumentsPointer.getCons()));
            String localVariables = aEnvironment.getLocalVariables();
            LispExpressionEvaluator.traceShowLeave(aEnvironment, aResult, tr, "macro", localVariables);
            tr.setCons(null);
        }

    }
}


