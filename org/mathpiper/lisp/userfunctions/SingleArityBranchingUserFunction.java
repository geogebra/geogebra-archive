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

import org.mathpiper.lisp.stacks.UserStackInformation;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.cons.SublistCons;
import java.util.*;
import org.mathpiper.exceptions.EvaluationException;
import org.mathpiper.lisp.Evaluator;

/**
 * A function (usually mathematical) which is defined by one or more rules.
 * This is the basic class which implements functions.  Evaluation is done
 * by consulting a set of rewritng rules.  The body of the car rule that
 * matches is evaluated and its result is returned as the function's result.
 */
public class SingleArityBranchingUserFunction extends Evaluator {
    /// List of arguments, with corresponding \c iHold property.

    protected List<FunctionParameter> iParameters = new ArrayList(); //CArrayGrower<FunctionParameter>

    /// List of rules, sorted on precedence.
    protected List<Branch> iBranchRules = new ArrayList();//CDeletingArrayGrower<BranchRuleBase*>

    /// List of arguments
    ConsPointer iParameterList = new ConsPointer();
/// Abstract class providing the basic user function API.
/// Instances of this class are associated to the name of the function
/// via an associated hash table. When obtained, they can be used to
/// evaluate the function with some arguments.
    boolean iFenced = true;
    boolean showFlag = false;
    protected String functionType = "**** user rulebase";
    protected String functionName;

    /**
     * Constructor.
     *
     * @param aParameters linked list constaining the names of the arguments
     * @throws java.lang.Exception
     */
    public SingleArityBranchingUserFunction(ConsPointer aParameters, String functionName) throws Exception {
        this.functionName = functionName;
        // iParameterList and #iParameters are set from \a aParameters.
        iParameterList.setCons(aParameters.getCons());

        ConsPointer parameterTraverser = new ConsPointer(aParameters.getCons());

        while (parameterTraverser.getCons() != null) {

            try{
                LispError.check(parameterTraverser.car() instanceof String, LispError.CREATING_USER_FUNCTION);
            }catch(EvaluationException ex)
            {
                throw new EvaluationException(ex.getMessage() + " Function: " + this.functionName + "  ",-1) ;
            }//end catch.

            FunctionParameter parameter = new FunctionParameter( (String) parameterTraverser.car(), false);
            iParameters.add(parameter);
            parameterTraverser.goNext();
        }
    }

    /**
     * Evaluate the function with the given arguments.
     * First, all arguments are evaluated by the evaluator associated
     * with aEnvironment, unless the iHold flag of the
     * corresponding parameter is true. Then a new LocalFrame is
     * constructed, in which the actual arguments are assigned to the
     * names of the formal arguments, as stored in iParameter. Then
     * all rules in <b>iRules</b> are tried one by one. The body of the
     * getFirstPointer rule that matches is evaluated, and the result is put in
     * aResult. If no rule matches, aResult will recieve a new
     * expression with evaluated arguments.
     * 
     * @param aResult (on output) the result of the evaluation
     * @param aEnvironment the underlying Lisp environment
     * @param aArguments the arguments to the function
     * @throws java.lang.Exception
     */
    public void evaluate(Environment aEnvironment, ConsPointer aResult, ConsPointer aArgumentsPointer) throws Exception {
        int arity = arity();
        ConsPointer[] argumentsResultPointerArray = evaluateArguments(aEnvironment, aArgumentsPointer);
        int parameterIndex;

        // Create a new local variables frame that has the same fenced state as this function.
        aEnvironment.pushLocalFrame(fenced(), this.functionName);



        try {
            // define the local variables.
            for (parameterIndex = 0; parameterIndex < arity; parameterIndex++) {
                String variableName = ((FunctionParameter) iParameters.get(parameterIndex)).iParameter;
                // set the variable to the new value
                aEnvironment.newLocalVariable(variableName, argumentsResultPointerArray[parameterIndex].getCons());
            }

            // walk the rules database, returning the evaluated result if the
            // predicate is true.
            int numberOfRules = iBranchRules.size();

            UserStackInformation userStackInformation = aEnvironment.iLispExpressionEvaluator.stackInformation();

            for (parameterIndex = 0; parameterIndex < numberOfRules; parameterIndex++) {
                Branch thisRule = ((Branch) iBranchRules.get(parameterIndex));
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

                    aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aResult, thisRule.getBodyPointer());

                    /*Leave trace code */
                    if (isTraced() && showFlag) {
                        ConsPointer argumentsPointer2 = new ConsPointer();
                        argumentsPointer2.setCons(SublistCons.getInstance(aArgumentsPointer.getCons()));
                        String localVariables = aEnvironment.getLocalVariables();
                        Evaluator.traceShowLeave(aEnvironment, aResult, argumentsPointer2, functionType, localVariables);
                        argumentsPointer2.setCons(null);
                    }//end if.

                    return;
                }//end if matches.

                // If rules got inserted, walk back
                while (thisRule != ((Branch) iBranchRules.get(parameterIndex)) && parameterIndex > 0) {
                    parameterIndex--;
                }
            }//end for.


            // No predicate was true: return a new expression with the evaluated
            // arguments.
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


            /* Trace code */
            if (isTraced() && showFlag) {
                ConsPointer argumentsPointer3 = new ConsPointer();
                argumentsPointer3.setCons(SublistCons.getInstance(aArgumentsPointer.getCons()));
                String localVariables = aEnvironment.getLocalVariables();
                Evaluator.traceShowLeave(aEnvironment, aResult, argumentsPointer3, functionType, localVariables);
                argumentsPointer3.setCons(null);
            }

        } catch (Exception e) {
            throw e;
        } finally {
            aEnvironment.popLocalFrame();
        }
    }

    protected ConsPointer[] evaluateArguments(Environment aEnvironment, ConsPointer aArgumentsPointer) throws Exception {
        int arity = arity();
        int parameterIndex;

        /*Enter trace code*/
        if (isTraced()) {
            ConsPointer argumentsPointer = new ConsPointer();
            argumentsPointer.setCons(SublistCons.getInstance(aArgumentsPointer.getCons()));
            String functionName = "";
            if (argumentsPointer.car() instanceof ConsPointer) {
                ConsPointer sub = (ConsPointer) argumentsPointer.car();
                if (sub.car() instanceof String) {
                    functionName = (String) sub.car();
                }
            }//end function.
            if (Evaluator.isTraceFunction(functionName)) {
                showFlag = true;
                Evaluator.traceShowEnter(aEnvironment, argumentsPointer, functionType);
            } else {
                showFlag = false;
            }//
            argumentsPointer.setCons(null);
        }

        ConsPointer argumentsTraverser = new ConsPointer(aArgumentsPointer.getCons());

        //Strip the function name from the head of the list.
        argumentsTraverser.goNext();

        //Creat an array which holds pointers to each argument.
        ConsPointer[] argumentsResultPointerArray;
        if (arity == 0) {
            argumentsResultPointerArray = null;
        } else {
            LispError.lispAssert(arity > 0);
            argumentsResultPointerArray = new ConsPointer[arity];
        }

        // Walk over all arguments, evaluating them as necessary ********************************************************
        for (parameterIndex = 0; parameterIndex < arity; parameterIndex++) {

            argumentsResultPointerArray[parameterIndex] = new ConsPointer();

            LispError.check(argumentsTraverser.getCons() != null, LispError.WRONG_NUMBER_OF_ARGUMENTS);

            if (((FunctionParameter) iParameters.get(parameterIndex)).iHold) {
                //If the parameter is on hold, don't evaluate it and place a copy of it in argumentsPointerArray.
                argumentsResultPointerArray[parameterIndex].setCons(argumentsTraverser.getCons().copy(false));
            } else {
                //If the parameter is not on hold:

                //Verify that the pointer to the arguments is not null.
                LispError.check(argumentsTraverser != null, LispError.WRONG_NUMBER_OF_ARGUMENTS);

                //Evaluate each argument and place the result into argumentsResultPointerArray[i];
                aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, argumentsResultPointerArray[parameterIndex], argumentsTraverser);
            }
            argumentsTraverser.goNext();
        }//end for.

        /*Argument trace code */
        if (isTraced() && argumentsResultPointerArray != null && showFlag)  {
            //ConsTraverser consTraverser2 = new ConsTraverser(aArguments);
            //ConsPointer traceArgumentPointer = new ConsPointer(aArgumentsPointer.getCons());

            //ConsTransverser traceArgumentPointer new ConsTraverser(this.iParameterList);
            ConsPointer traceParameterPointer = new ConsPointer(this.iParameterList.getCons());

            //traceArgumentPointer.goNext();
            for (parameterIndex = 0; parameterIndex < argumentsResultPointerArray.length; parameterIndex++) {
                Evaluator.traceShowArg(aEnvironment, traceParameterPointer, argumentsResultPointerArray[parameterIndex]);

                traceParameterPointer.goNext();
            }//end for.
        }//end if.

        return argumentsResultPointerArray;

    }//end method.

    /**
     * Put an argument on hold.
     * The \c iHold flag of the corresponding argument is setCons. This
     * implies that this argument is not evaluated by evaluate().
     * 
     * @param aVariable name of argument to put un hold
     */
    public void holdArgument(String aVariable) {
        int i;
        int nrc = iParameters.size();
        for (i = 0; i < nrc; i++) {
            if (((FunctionParameter) iParameters.get(i)).iParameter == aVariable) {
                ((FunctionParameter) iParameters.get(i)).iHold = true;
            }
        }
    }

    /**
     * Return true if the arity of the function equals \a aArity.
     * 
     * @param aArity
     * @return true of the arities match.
     */
    public boolean isArity(int aArity) {
        return (arity() == aArity);
    }

    /**
     * Return the arity (number of arguments) of the function.
     *
     * @return the arity of the function
     */
    public int arity() {
        return iParameters.size();
    }

    /**
     *  Add a RuleBranch to the list of rules.
     * See: insertRule()
     * 
     * @param aPrecedence
     * @param aPredicate
     * @param aBody
     * @throws java.lang.Exception
     */
    public void declareRule(int aPrecedence, ConsPointer aPredicate, ConsPointer aBody) throws Exception {
        // New branching rule.
        RuleBranch newRule = new RuleBranch(aPrecedence, aPredicate, aBody);
        LispError.check(newRule != null, LispError.CREATING_RULE);

        insertRule(aPrecedence, newRule);
    }

    /**
     * Add a TruePredicateRuleBranch to the list of rules.
     * See: insertRule()
     * 
     * @param aPrecedence
     * @param aBody
     * @throws java.lang.Exception
     */
    public void declareRule(int aPrecedence, ConsPointer aBody) throws Exception {
        // New branching rule.
        RuleBranch newRule = new TruePredicateRuleBranch(aPrecedence, aBody);
        LispError.check(newRule != null, LispError.CREATING_RULE);

        insertRule(aPrecedence, newRule);
    }

    /**
     *  Add a PatternBranch to the list of rules.
     *  See: insertRule()
     * 
     * @param aPrecedence
     * @param aPredicate
     * @param aBody
     * @throws java.lang.Exception
     */
    public void declarePattern(int aPrecedence, ConsPointer aPredicate, ConsPointer aBody) throws Exception {
        // New branching rule.
        PatternBranch newRule = new PatternBranch(aPrecedence, aPredicate, aBody);
        LispError.check(newRule != null, LispError.CREATING_RULE);

        insertRule(aPrecedence, newRule);
    }

    /**
     * Insert any Branch object in the list of rules.
     * This function does the real work for declareRule() and
     * declarePattern(): it inserts the rule in <b>iRules</b>, while
     * keeping it sorted. The algorithm is O(log n), where
     * n denotes the number of rules.
     * 
     * @param aPrecedence
     * @param newRule
     */
    void insertRule(int aPrecedence, Branch newRule) {
        // Find place to insert
        int low, high, mid;
        low = 0;
        high = iBranchRules.size();

        // Constant time: find out if the precedence is before any of the
        // currently defined rules or past them.
        if (high > 0) {
            if (((Branch) iBranchRules.get(0)).getPrecedence() > aPrecedence) {
                mid = 0;
                // Insert it
                iBranchRules.add(mid, newRule);
                return;
            }
            if (((Branch) iBranchRules.get(high - 1)).getPrecedence() < aPrecedence) {
                mid = high;
                // Insert it
                iBranchRules.add(mid, newRule);
                return;
            }
        }

        // Otherwise, O(log n) search algorithm for place to insert
        for (;;) {
            if (low >= high) {
                mid = low;
                // Insert it
                iBranchRules.add(mid, newRule);
                return;
            }
            mid = (low + high) >> 1;

            if (((Branch) iBranchRules.get(mid)).getPrecedence() > aPrecedence) {
                high = mid;
            } else if (((Branch) iBranchRules.get(mid)).getPrecedence() < aPrecedence) {
                low = (++mid);
            } else {
                // Insert it
                iBranchRules.add(mid, newRule);
                return;
            }
        }
    }

    /**
     * Return the argument list, stored in #iParameterList.
     * 
     * @return a ConsPointer
     */
    public ConsPointer argList() {
        return iParameterList;
    }

    public Iterator getRules() {
        return iBranchRules.iterator();
    }

    public Iterator getParameters() {
        return iParameters.iterator();
    }

    public void unFence() {
        iFenced = false;
    }

    public boolean fenced() {
        return iFenced;
    }
}//end class.

