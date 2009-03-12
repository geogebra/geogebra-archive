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

import org.mathpiper.exceptions.EvaluationException;
import org.mathpiper.builtin.BuiltinFunctionInitialize;
import org.mathpiper.*;

public class LispError
{

    public static int KLispErrNone = 0;
    public static int KLispErrInvalidArg = 1;
    public static int KLispErrWrongNumberOfArgs = 2;
    public static int KLispErrNotList = 3;
    public static int KLispErrListNotLongEnough = 4;
    public static int KLispErrInvalidStack = 5;
    public static int KQuitting = 6;
    public static int KLispErrNotEnoughMemory = 7;
    public static int KInvalidToken = 8;
    public static int KLispErrInvalidExpression = 9;
    public static int KLispErrUnprintableToken = 10;
    public static int KLispErrFileNotFound = 11;
    public static int KLispErrReadingFile = 12;
    public static int KLispErrCreatingUserFunction = 13;
    public static int KLispErrCreatingRule = 14;
    public static int KLispErrArityAlreadyDefined = 15;
    public static int KLispErrCommentToEndOfFile = 16;
    public static int KLispErrNotString = 17;
    public static int KLispErrNotInteger = 18;
    public static int KLispErrParsingInput = 19;
    public static int KLispErrMaxRecurseDepthReached = 20;
    public static int KLispErrDefFileAlreadyChosen = 21;
    public static int KLispErrDivideByZero = 22;
    public static int KLispErrNotAnInFixOperator = 23;
    public static int KLispErrIsNotInFix = 24;
    public static int KLispErrSecurityBreach = 25;
    public static int KLispErrLibraryNotFound = 26;
    public static int KLispErrUserInterrupt = 27;
    public static int KLispErrNonBooleanPredicateInPattern = 28;
    public static int KLispErrGenericFormat = 29;
    public static int KLispNrErrors = 30;

    public static String errorString(int aError) throws Exception
    {
        lispAssert(aError >= 0 && aError < KLispNrErrors);
        //    switch (aError)
        {
            if (aError == KLispErrNone)
            {
                return "No error.";
            }
            if (aError == KLispErrInvalidArg)
            {
                return "Invalid argument.";
            }
            if (aError == KLispErrWrongNumberOfArgs)
            {
                return "Wrong number of arguments.";
            }
            if (aError == KLispErrNotList)
            {
                return "Argument is not a list.";
            }
            if (aError == KLispErrListNotLongEnough)
            {
                return "List not long enough.";
            }
            if (aError == KLispErrInvalidStack)
            {
                return "Invalid stack.";
            }
            if (aError == KQuitting)
            {
                return "Quitting...";
            }
            if (aError == KLispErrNotEnoughMemory)
            {
                return "Not enough memory.";
            }
            if (aError == KInvalidToken)
            {
                return "Empty token during parsing.";
            }
            if (aError == KLispErrInvalidExpression)
            {
                return "Error parsing expression.";
            }
            if (aError == KLispErrUnprintableToken)
            {
                return "Unprintable atom.";
            }
            if (aError == KLispErrFileNotFound)
            {
                return "File not found.";
            }
            if (aError == KLispErrReadingFile)
            {
                return "Error reading file.";
            }
            if (aError == KLispErrCreatingUserFunction)
            {
                return "Could not create user function.";
            }
            if (aError == KLispErrCreatingRule)
            {
                return "Could not create rule.";
            }
            if (aError == KLispErrArityAlreadyDefined)
            {
                return "Rule base with this arity already defined.";
            }
            if (aError == KLispErrCommentToEndOfFile)
            {
                return "Reaching end of file within a comment block.";
            }
            if (aError == KLispErrNotString)
            {
                return "Argument is not a string.";
            }
            if (aError == KLispErrNotInteger)
            {
                return "Argument is not an integer.";
            }
            if (aError == KLispErrParsingInput)
            {
                return "Error while parsing input.";
            }
            if (aError == KLispErrMaxRecurseDepthReached)
            {
                return "Max evaluation stack depth reached.\nPlease use MaxEvalDepth to increase the stack size as needed.";
            }
            if (aError == KLispErrDefFileAlreadyChosen)
            {
                return "DefFile already chosen for function.";
            }
            if (aError == KLispErrDivideByZero)
            {
                return "Divide by zero.";
            }
            if (aError == KLispErrNotAnInFixOperator)
            {
                return "Trying to make a non-infix operator right-associative.";
            }
            if (aError == KLispErrIsNotInFix)
            {
                return "Trying to get precedence of non-infix operator.";
            }
            if (aError == KLispErrSecurityBreach)
            {
                return "Trying to perform an insecure action.";
            }
            if (aError == KLispErrLibraryNotFound)
            {
                return "Could not find library.";
            }
            if (aError == KLispErrUserInterrupt)
            {
                return "User interrupted calculation.";
            }
            if (aError == KLispErrNonBooleanPredicateInPattern)
            {
                return "Predicate doesn't evaluate to a boolean in pattern.";
            }
            if (aError == KLispErrGenericFormat)
            {
                return "Generic format.";
            }
        }
        return "Unspecified Error.";
    }

    public static void check(boolean hastobetrue, int aError) throws Exception
    {
        if (!hastobetrue)
        {
            String error = errorString(aError);//"LispError number "+aError+" (//TODO FIXME still need to port over the string table)";
            throw new EvaluationException(error,-1);
        }
    }

    public static void raiseError(String str,Environment aEnvironment) throws Exception
    {
        int lineNumber = -1;  
        if(aEnvironment != null)
        {
                lineNumber = aEnvironment.iInputStatus.lineNumber();
        }
        
        throw new EvaluationException(str,lineNumber);
    }

    public static void checkNumberOfArguments(int n, ConsPointer aArguments, Environment aEnvironment) throws Exception
    {
        int nrArguments = UtilityFunctions.internalListLength(aArguments);
        if (nrArguments != n)
        {
            errorNumberOfArguments(n - 1, nrArguments - 1, aArguments, aEnvironment);
        }
    }

    public static void errorNumberOfArguments(int needed, int passed, ConsPointer aArguments, Environment aEnvironment) throws Exception
    {
        if (aArguments.getCons() == null)
        {
            throw new EvaluationException("Error in compiled code.",-1);
        } else
        {
            //TODO FIXME      ShowStack(aEnvironment);
            String error = showFunctionError(aArguments, aEnvironment) + "expected " + needed + " arguments, got " + passed;
            throw new EvaluationException(error,-1);

        /*TODO FIXME
        LispChar str[20];
        aEnvironment.iErrorOutput.Write("expected ");
        InternalIntToAscii(str,needed);
        aEnvironment.iErrorOutput.Write(str);
        aEnvironment.iErrorOutput.Write(" arguments, got ");
        InternalIntToAscii(str,passed);
        aEnvironment.iErrorOutput.Write(str);
        aEnvironment.iErrorOutput.Write("\n");
        LispError.check(passed == needed,LispError.KLispErrWrongNumberOfArgs);
         */
        }
    }

    public static String showFunctionError(ConsPointer aArguments, Environment aEnvironment) throws Exception
    {
        if (aArguments.getCons() == null)
        {
            return "Error in compiled code. ";
        } else
        {
            String string = aArguments.getCons().string();
            if (string != null)
            {
                return "In function \"" + string + "\" : ";
            }
        }
        return "[Atom]";
    }

    public static void checkCore(Environment aEnvironment, int aStackTop, boolean aPredicate, int errNo) throws Exception
    {
        if (!aPredicate)
        {
            ConsPointer arguments = BuiltinFunctionInitialize.getArgumentPointer(aEnvironment, aStackTop, 0);
            if (arguments.getCons() == null)
            {
                throw new EvaluationException("Error in compiled code\n",-1);
            } else
            {
                String error = "";
                //TODO FIXME          ShowStack(aEnvironment);
                error = error + showFunctionError(arguments, aEnvironment) + "generic error.";
                throw new EvaluationException(error,-1);
            }
        }
    }

    public static void lispAssert(boolean aPredicate) throws Exception
    {
        if (!aPredicate)
        {
            throw new EvaluationException("Assertion failed.",-1);
        }
    }

    public static void checkArgumentCore(Environment aEnvironment, int aStackTop, boolean aPredicate, int aArgNr) throws Exception
    {
        checkArgumentTypeWithError(aEnvironment, aStackTop, aPredicate, aArgNr, "");
    }

    public static void checkIsListCore(Environment aEnvironment, int aStackTop, ConsPointer evaluated, int aArgNr) throws Exception
    {
        checkArgumentTypeWithError(aEnvironment, aStackTop, UtilityFunctions.internalIsList(evaluated), aArgNr, "argument is not a list.");
    }

    public static void checkIsStringCore(Environment aEnvironment, int aStackTop, ConsPointer evaluated, int aArgNr) throws Exception
    {
        checkArgumentTypeWithError(aEnvironment, aStackTop, UtilityFunctions.internalIsString(evaluated.getCons().string()), aArgNr, "argument is not a string.");
    }

    public static void checkArgumentTypeWithError(Environment aEnvironment, int aStackTop, boolean aPredicate, int aArgNr, String aErrorDescription) throws Exception
    {
        if (!aPredicate)
        {
            ConsPointer arguments = BuiltinFunctionInitialize.getArgumentPointer(aEnvironment, aStackTop, 0);
            if (arguments.getCons() == null)
            {
                throw new EvaluationException("Error in compiled code\n",-1);
            } else
            {
                String error = "";
                //TODO FIXME          ShowStack(aEnvironment);
                error = error + showFunctionError(arguments, aEnvironment) + "\nbad argument number " + aArgNr + "(counting from 1) : \n" + aErrorDescription + "\n";
                ConsPointer arg = BuiltinFunctionInitialize.getArgumentPointer(arguments, aArgNr);
                String strout;

                error = error + "The offending argument ";
                strout = UtilityFunctions.printExpression(arg, aEnvironment, 60);
                error = error + strout;

                ConsPointer eval = new ConsPointer();
                aEnvironment.iEvaluator.evaluate(aEnvironment, eval, arg);
                error = error + " evaluated to ";
                strout = UtilityFunctions.printExpression(eval, aEnvironment, 60);
                error = error + strout;
                error = error + "\n";

                throw new EvaluationException(error,-1);
            }
        }
    }
}