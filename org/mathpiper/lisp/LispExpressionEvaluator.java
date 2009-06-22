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
package org.mathpiper.lisp;

import org.mathpiper.io.MathPiperOutputStream;
import org.mathpiper.io.StringOutputStream;
import org.mathpiper.lisp.userfunctions.Evaluator;
import org.mathpiper.lisp.userfunctions.MultipleArityUserFunction;
import org.mathpiper.lisp.userfunctions.UserFunction;
import org.mathpiper.printers.InfixPrinter;


/**
 *  The basic evaluator for Lisp expressions.
 * 
 */
public class LispExpressionEvaluator extends ExpressionEvaluator
{
	public static boolean DEBUG = false;
	public static boolean VERBOSE_DEBUG = false;
    /**
     * <p>
     * First, the evaluation depth is checked. An error is raised if the maximum evaluation 
     * depth is exceeded.  The next step is the actual evaluation.  aExpression is a
     * Cons, so we can distinguish three cases:</p>
     * <ol>
     * <li value="1"><p>
     * If  aExpression is a string starting with " , it is
     * simply copied in aResult. If it starts with another
     * character (this includes the case where it represents a
     * number), the environment is checked to see whether a
     * variable with this name exists. If it does, its value is
     * copied in aResult, otherwise aExpression is copied.</p>
     *
     * <li value="2"><p>
     * If aExpression is a list, the head of the list is
     * examined. If the head is not a string. InternalApplyPure()
     * is called. If the head is a string, it is checked against
     * the core commands (if there is a check, the corresponding
     * evaluator is called). Then it is checked agaist the list of
     * user function with getUserFunction().   Again, the
     * corresponding evaluator is called if there is a check. If
     * all fails, ReturnUnEvaluated() is called.</p>
     * <li value="3"><p>
     * Otherwise (ie. if aExpression is a getGeneric object), it is
     * copied in aResult.</p>
     * </ol>
     * 
     * <p>
     * Note: The result of this operation must be a unique (copied)
     * element! Eg. its Next might be set...</p>
     * 
     * @param aEnvironment  the Lisp environment, in which the evaluation should take place
     * @param aResult             the result of the evaluation
     * @param aExpression     the expression to evaluate
     * @throws java.lang.Exception
     */
    public void evaluate(Environment aEnvironment, ConsPointer aResult, ConsPointer aExpression) throws Exception
    {
        LispError.lispAssert(aExpression.getCons() != null);
        aEnvironment.iEvalDepth++;
        if (aEnvironment.iEvalDepth >= aEnvironment.iMaxEvalDepth)
        {
            if (aEnvironment.iEvalDepth > aEnvironment.iMaxEvalDepth + 20)
            {
                LispError.check(aEnvironment.iEvalDepth < aEnvironment.iMaxEvalDepth,
                        LispError.KLispErrUserInterrupt);
            } else
            {
                LispError.check(aEnvironment.iEvalDepth < aEnvironment.iMaxEvalDepth, LispError.KLispErrMaxRecurseDepthReached);
            }
        }

        String str = aExpression.getCons().string();

        // evaluate an atom: find the bound value (treat it as a variable)
        if (str != null)
        {
            if (str.charAt(0) == '\"')
            {
                aResult.setCons(aExpression.getCons().copy(false));
                aEnvironment.iEvalDepth--;
                return;
            }

            ConsPointer val = new ConsPointer();
            aEnvironment.getVariable(str, val);
            if (val.getCons() != null)
            {
                aResult.setCons(val.getCons().copy(false));
                aEnvironment.iEvalDepth--;
                return;
            }
            aResult.setCons(aExpression.getCons().copy(false));
            aEnvironment.iEvalDepth--;
            return;
        }
        {
            ConsPointer subList = aExpression.getCons().getSubList();

            if (subList != null)
            {
                Cons head = subList.getCons();
                if (head != null)
                {
                    if (head.string() != null)
                    {
                        {
                            Evaluator evaluator = (Evaluator) aEnvironment.getBuiltinFunctions().lookUp(head.string());
                            // Try to find a built-in command
                            if (evaluator != null)
                            {
                                evaluator.evaluate(aResult, aEnvironment, subList);
                                aEnvironment.iEvalDepth--;
                                return;
                            }
                        }
                        {
                            UserFunction userFunc;
                            userFunc = getUserFunction(aEnvironment, subList);
                            if (userFunc != null)
                            {
                                userFunc.evaluate(aResult, aEnvironment, subList);
                                aEnvironment.iEvalDepth--;
                                return;
                            }
                        }
                    } else
                    {
                        //printf("ApplyPure!\n");
                        ConsPointer oper = new ConsPointer();
                        ConsPointer args2 = new ConsPointer();
                        oper.setCons(subList.getCons());
                        args2.setCons(subList.getCons().rest().getCons());
                        UtilityFunctions.internalApplyPure(oper, args2, aResult, aEnvironment);
                        aEnvironment.iEvalDepth--;
                        return;
                    }
                    //printf("**** Undef: %s\n",head.String().String());
                    UtilityFunctions.returnUnEvaluated(aResult, subList, aEnvironment);
                    aEnvironment.iEvalDepth--;
                    return;
                }
            }
            aResult.setCons(aExpression.getCons().copy(false));
        }
        aEnvironment.iEvalDepth--;
    }

    UserFunction getUserFunction(Environment aEnvironment, ConsPointer subList) throws Exception
    {
        Cons head = subList.getCons();
        UserFunction userFunc = null;

        userFunc = (UserFunction) aEnvironment.userFunction(subList);
        if (userFunc != null)
        {
            return userFunc;
        } else if (head.string() != null)
        {
            MultipleArityUserFunction multiUserFunc = aEnvironment.multiUserFunction(head.string());
            if (multiUserFunc.iFileToOpen != null)
            {
                DefFile def = multiUserFunc.iFileToOpen;
                
                				if(DEBUG)
				{
					/*Show loading... */
					
					if (VERBOSE_DEBUG)
					{
						/*char buf[1024];
						#ifdef HAVE_VSNPRINTF
						snprintf(buf,1024,"Debug> Loading file %s for function %s\n",def.iFileName.c_str(),head.String().c_str());
						#else
						sprintf(buf,      "Debug> Loading file %s for function %s\n",def.iFileName.c_str(),head.String().c_str());
						#endif
						aEnvironment.write(buf);*/
						
						aEnvironment.write("Debug> Loading file" + def.iFileName + " for function " + head.string() + "\n");
					}
				}
                
                
                
                multiUserFunc.iFileToOpen = null;
                UtilityFunctions.internalUse(aEnvironment, def.iFileName);
                
                				if(DEBUG)
				{
					//extern int VERBOSE_DEBUG;
					if (VERBOSE_DEBUG)
					{
						/*
						char buf[1024];
						#ifdef HAVE_VSNPRINTF
						snprintf(buf,1024,"Debug> Finished loading file %s\n",def.iFileName.c_str());
						#else
						sprintf(buf,      "Debug> Finished loading file %s\n",def.iFileName.c_str());
						#endif*/
						
						
						aEnvironment.write("Debug> Finished loading file " + def.iFileName +"\n");
					}
				}
            }
            userFunc = aEnvironment.userFunction(subList);
        }
        return userFunc;
    }//end method.
    
    	public static void showExpression(StringBuffer outString, Environment aEnvironment, ConsPointer aExpression) throws Exception
	{
		InfixPrinter infixprinter = new InfixPrinter(aEnvironment.iPrefixOperators,  aEnvironment.iInfixOperators, aEnvironment.iPostfixOperators,  aEnvironment.iBodiedOperators);
                
		// Print out the current expression
		//StringOutput stream(outString);
                MathPiperOutputStream stream = new StringOutputStream(outString);
                
                
		infixprinter.print(aExpression, stream,aEnvironment);

		// Escape quotes.
		for (int i = outString.length()-1; i >= 0; --i)
		{
                        char c = outString.charAt(i);
			if ( c == '\"')
                        {
				//outString.insert(i, '\\');
                               outString.deleteCharAt(i);
                        }
		}
                
	}//end method.

	public static void traceShowExpression(Environment aEnvironment, ConsPointer aExpression) throws Exception
	{
		StringBuffer outString = new StringBuffer();
		showExpression(outString, aEnvironment, aExpression);
		aEnvironment.write(outString.toString());
	}

	public static void traceShowArg(Environment aEnvironment,ConsPointer aParam, ConsPointer aValue) throws Exception
	{
		for (int i=0;i<aEnvironment.iEvalDepth+2;i++)
			aEnvironment.write("  ");
                
		aEnvironment.write("Arg(");
		traceShowExpression(aEnvironment, aParam);
		aEnvironment.write(",");
		traceShowExpression(aEnvironment, aValue);
		aEnvironment.write(");\n");
	}

	public static void traceShowEnter(Environment aEnvironment,  ConsPointer aExpression) throws Exception
	{
		for (int i=0;i<aEnvironment.iEvalDepth;i++)
			aEnvironment.write("  ");
		aEnvironment.write("Enter(");
		{
			String function = "";
			if (aExpression.getCons().getSubList() != null)
			{
				ConsPointer sub = aExpression.getCons().getSubList();
				if (sub.getCons().string() != null)
					function =sub.getCons().string();
			}
			aEnvironment.write(function);
		}
		aEnvironment.write(",");
		traceShowExpression(aEnvironment, aExpression);
		aEnvironment.write(",");
		if(DEBUG)
                {
		//aEnvironment.write( aExpression.iFileName ? aExpression.iFileName : ""); //file Note:tk.
		aEnvironment.write(",");
		//LispChar buf[30];
		//InternalIntToAscii(buf,aExpression.iLine);
		//aEnvironment.write(buf); //line
                }
                else
                {
		aEnvironment.write(""); //file
		aEnvironment.write(",");
		aEnvironment.write("0"); //line
                }

		aEnvironment.write(");\n");
	}

	public static void traceShowLeave(Environment aEnvironment, ConsPointer aResult,
	                    ConsPointer aExpression) throws Exception
	{
		for (int i=0;i<aEnvironment.iEvalDepth;i++)
			aEnvironment.write("  ");
		aEnvironment.write("Leave(");
		traceShowExpression(aEnvironment, aExpression);
		aEnvironment.write(",");
		traceShowExpression(aEnvironment, aResult);
		aEnvironment.write(");\n");
	}

        /*
	void TracedStackEvaluator::PushFrame()
	{
		UserStackInformation *op = NEW UserStackInformation;
		objs.Append(op);
	}

	void TracedStackEvaluator::PopFrame()
	{
		LISPASSERT (objs.Size() > 0);
		if (objs[objs.Size()-1])
		{
			delete objs[objs.Size()-1];
			objs[objs.Size()-1] = null;
		}
		objs.Delete(objs.Size()-1);
	}

	void TracedStackEvaluator::ResetStack()
	{
		while (objs.Size()>0)
		{
			PopFrame();
		}
	}

	UserStackInformation& TracedStackEvaluator::StackInformation()
	{
		return *(objs[objs.Size()-1]);
	}

	TracedStackEvaluator::~TracedStackEvaluator()
	{
		ResetStack();
	}

	void TracedStackEvaluator::ShowStack(Environment aEnvironment, LispOutput& aOutput)
	{
		LispLocalEvaluator local(aEnvironment,NEW BasicEvaluator);

		LispInt i;
		LispInt from=0;
		LispInt upto = objs.Size();

		for (i=from;i<upto;i++)
		{
			LispChar str[20];
			#ifdef YACAS_DEBUG
			aEnvironment.write(objs[i].iFileName);
			aEnvironment.write("(");
			InternalIntToAscii(str,objs[i].iLine);
			aEnvironment.write(str);
			aEnvironment.write(") : ");
			aEnvironment.write("Debug> ");
			#endif
			InternalIntToAscii(str,i);
			aEnvironment.write(str);
			aEnvironment.write(": ");
			aEnvironment.CurrentPrinter().Print(objs[i].iOperator, *aEnvironment.CurrentOutput(),aEnvironment);

			LispInt internal;
			internal = (null != aEnvironment.CoreCommands().LookUp(objs[i].iOperator.String()));
			if (internal)
			{
				aEnvironment.write(" (Internal function) ");
			}
			else
			{
				if (objs[i].iRulePrecedence>=0)
				{
					aEnvironment.write(" (Rule # ");
					InternalIntToAscii(str,objs[i].iRulePrecedence);
					aEnvironment.write(str);
					if (objs[i].iSide)
						aEnvironment.write(" in body) ");
					else
						aEnvironment.write(" in pattern) ");
				}
				else
					aEnvironment.write(" (User function) ");
			}
			if (!!objs[i].iExpression)
			{
				aEnvironment.write("\n      ");
				if (aEnvironment.iEvalDepth>(aEnvironment.iMaxEvalDepth-10))
				{
					LispString expr;
					PrintExpression(expr, objs[i].iExpression,aEnvironment,60);
					aEnvironment.write(expr.c_str());
				}
				else
				{
					LispPtr getSubList = objs[i].iExpression.SubList();
					if (!!getSubList && !!getSubList)
					{
						LispString expr;
						LispPtr out(objs[i].iExpression);
						PrintExpression(expr, out,aEnvironment,60);
						aEnvironment.write(expr.c_str());
					}
				}
			}
			aEnvironment.write("\n");
		}
	}

	void TracedStackEvaluator::Eval(Environment aEnvironment, ConsPointer aResult,
	                                ConsPointer aExpression)
	{
		if (aEnvironment.iEvalDepth>=aEnvironment.iMaxEvalDepth)
		{
			ShowStack(aEnvironment, *aEnvironment.CurrentOutput());
			CHK2(aEnvironment.iEvalDepth<aEnvironment.iMaxEvalDepth,
			     KLispErrMaxRecurseDepthReached);
		}

		LispPtr getSubList = aExpression.SubList();
		LispString * str = null;
		if (getSubList)
		{
			Cons head = getSubList;
			if (head)
			{
				str = head.String();
				if (str)
				{
					PushFrame();
					UserStackInformation& st = StackInformation();
					st.iOperator = (LispAtom::New(aEnvironment,str.c_str()));
					st.iExpression = (aExpression);
					#ifdef YACAS_DEBUG
					if (aExpression.iFileName)
					{
						st.iFileName = aExpression.iFileName;
						st.iLine = aExpression.iLine;
					}
					#endif
				}
			}
		}
		BasicEvaluator::Eval(aEnvironment, aResult, aExpression);
		if (str)
		{
			PopFrame();
		}
	}

	void TracedEvaluator::Eval(Environment aEnvironment, ConsPointer aResult,
	                           ConsPointer aExpression)
	{
		if(!aEnvironment.iDebugger) RaiseError("Internal error: debugging failing");
		if(aEnvironment.iDebugger.Stopped()) RaiseError("");

REENTER:
		errorStr.ResizeTo(1); errorStr[0] = '\0';
		LispTrap(aEnvironment.iDebugger.Enter(aEnvironment, aExpression),errorOutput,aEnvironment);
		if(aEnvironment.iDebugger.Stopped()) RaiseError("");
		if (errorStr[0])
		{
			aEnvironment.write(errorStr.c_str());
			aEnvironment.iEvalDepth=0;
			goto REENTER;
		}

		errorStr.ResizeTo(1); errorStr[0] = '\0';
		LispTrap(BasicEvaluator::Eval(aEnvironment, aResult, aExpression),errorOutput,aEnvironment);

		if (errorStr[0])
		{
			aEnvironment.write(errorStr.c_str());
			aEnvironment.iEvalDepth=0;
			aEnvironment.iDebugger.Error(aEnvironment);
			goto REENTER;
		}

		if(aEnvironment.iDebugger.Stopped()) RaiseError("");

		aEnvironment.iDebugger.Leave(aEnvironment, aResult, aExpression);
		if(aEnvironment.iDebugger.Stopped()) RaiseError("");
	}

	YacasDebuggerBase::~YacasDebuggerBase()
	{
	}

	void DefaultDebugger::Start()
	{
	}

	void DefaultDebugger::Finish()
	{
	}

	void DefaultDebugger::Enter(Environment aEnvironment,
	                            ConsPointer aExpression)
	{
		LispLocalEvaluator local(aEnvironment,NEW BasicEvaluator);
		iTopExpr = (aExpression.Copy());
		LispPtr result;
		defaultEval.Eval(aEnvironment, result, iEnter);
	}

	void DefaultDebugger::Leave(Environment aEnvironment, ConsPointer aResult,
	                            ConsPointer aExpression)
	{
		LispLocalEvaluator local(aEnvironment,NEW BasicEvaluator);
		LispPtr result;
		iTopExpr = (aExpression.Copy());
		iTopResult = (aResult);
		defaultEval.Eval(aEnvironment, result, iLeave);
	}

	LispBoolean DefaultDebugger::Stopped()
	{
		return iStopped;
	}

	void DefaultDebugger::Error(Environment aEnvironment)
	{
		LispLocalEvaluator local(aEnvironment,NEW BasicEvaluator);
		LispPtr result;
		defaultEval.Eval(aEnvironment, result, iError);
	}

    
    */
    
    
    
    
}//end class.
