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

import org.mathpiper.lisp.printers.Printer;
import org.mathpiper.io.MathPiperInputStream;
import org.mathpiper.io.MathPiperOutputStream;
import org.mathpiper.builtin.BuiltinFunctionInitialize;
import org.mathpiper.lisp.tokenizers.XmlTokenizer;
import org.mathpiper.io.InputStatus;

import org.mathpiper.io.InputDirectories;

import org.mathpiper.lisp.tokenizers.MathPiperTokenizer;

import org.mathpiper.lisp.userfunctions.MultipleArityUserFunction;

import org.mathpiper.lisp.userfunctions.MacroUserFunction;

import org.mathpiper.lisp.userfunctions.UserFunction;

import org.mathpiper.lisp.userfunctions.ListedBranchingUserFunction;

import org.mathpiper.lisp.userfunctions.BranchingUserFunction;

import org.mathpiper.lisp.userfunctions.ListedMacroUserFunction;

import org.mathpiper.printers.InfixPrinter;


public class Environment
{

    public ExpressionEvaluator iEvaluator = new LispExpressionEvaluator();
    private int iPrecision = 10;
    private TokenHash iTokenHash = new TokenHash();
    public Cons iTrueAtom;
    public Cons iFalseAtom;
    public Cons iEndOfFileAtom;
    public Cons iEndStatementAtom;
    public Cons iProgOpenAtom;
    public Cons iProgCloseAtom;
    public Cons iNthAtom;
    public Cons iBracketOpenAtom;
    public Cons iBracketCloseAtom;
    public Cons iListOpenAtom;
    public Cons iListCloseAtom;
    public Cons iCommaAtom;
    public Cons iListAtom;
    public Cons iProgAtom;
    public Operators iPrefixOperators = new Operators();
    public Operators iInfixOperators = new Operators();
    public Operators iPostfixOperators = new Operators();
    public Operators iBodiedOperators = new Operators();
    public int iEvalDepth = 0;
    public int iMaxEvalDepth = 10000;
    //TODO FIXME
    public ArgumentStack iArgumentStack;
    public LocalVariableFrame iLocalsList;
    public boolean iSecure = false;
    public int iLastUniqueId = 1;
    public MathPiperOutputStream iCurrentOutput = null;
    public MathPiperOutputStream iInitialOutput = null;
    public Printer iCurrentPrinter = null;
    public MathPiperInputStream iCurrentInput = null;
    public InputStatus iInputStatus = new InputStatus();
    public MathPiperTokenizer iCurrentTokenizer;
    public MathPiperTokenizer iDefaultTokenizer = new MathPiperTokenizer();
    public MathPiperTokenizer iXmlTokenizer = new XmlTokenizer();
    public AssociatedHash iGlobalState = new AssociatedHash();
    public AssociatedHash iUserFunctions = new AssociatedHash();
    AssociatedHash iBuiltinFunctions = new AssociatedHash();
    public String iError = null;
    public DefFiles iDefFiles = new DefFiles();
    public InputDirectories iInputDirectories = new InputDirectories();
    public String iPrettyReader = null;
    public String iPrettyPrinter = null;

    public Environment(MathPiperOutputStream aCurrentOutput/*TODO FIXME*/) throws Exception
    {
        iCurrentTokenizer = iDefaultTokenizer;
        iInitialOutput = aCurrentOutput;
        iCurrentOutput = aCurrentOutput;
        iCurrentPrinter = new InfixPrinter(iPrefixOperators, iInfixOperators, iPostfixOperators, iBodiedOperators);

        iTrueAtom = Atom.getInstance(this, "True");
        iFalseAtom = Atom.getInstance(this, "False");

        iEndOfFileAtom = Atom.getInstance(this, "EndOfFile");
        iEndStatementAtom = Atom.getInstance(this, ";");
        iProgOpenAtom = Atom.getInstance(this, "[");
        iProgCloseAtom = Atom.getInstance(this, "]");
        iNthAtom = Atom.getInstance(this, "Nth");
        iBracketOpenAtom = Atom.getInstance(this, "(");
        iBracketCloseAtom = Atom.getInstance(this, ")");
        iListOpenAtom = Atom.getInstance(this, "{");
        iListCloseAtom = Atom.getInstance(this, "}");
        iCommaAtom = Atom.getInstance(this, ",");
        iListAtom = Atom.getInstance(this, "List");
        iProgAtom = Atom.getInstance(this, "Prog");

        iArgumentStack = new ArgumentStack(50000 /*TODO FIXME*/);
        //org.mathpiper.builtin.Functions mc = new org.mathpiper.builtin.Functions();
        //mc.addFunctions(this);

        BuiltinFunctionInitialize.addFunctions(this);

        pushLocalFrame(true);
    }

    public TokenHash getTokenHash()
    {
        return iTokenHash;
    }



    public AssociatedHash getGlobalState()
    {
        return iGlobalState;
    }

    public AssociatedHash getUserFunctions()
    {
        return iUserFunctions;
    }
    
   public AssociatedHash getBuiltinFunctions()
    {
        return iBuiltinFunctions;
    }

    public int getPrecision()
    {
        return iPrecision;
    }

    public void setPrecision(int aPrecision) throws Exception
    {
        iPrecision = aPrecision;    // getPrecision in decimal digits
    }


    public ConsPointer findLocal(String aVariable) throws Exception
    {
        LispError.check(iLocalsList != null, LispError.KLispErrInvalidStack);
        //    check(iLocalsList.iFirst != null,KLispErrInvalidStack);
        LispLocalVariable t = iLocalsList.iFirst;

        while (t != null)
        {
            if (t.iVariable == aVariable)
            {
                return t.iValue;
            }
            t = t.iNext;
        }
        return null;
    }

    public void setVariable(String aVariable, ConsPointer aValue, boolean aGlobalLazyVariable) throws Exception
    {
        ConsPointer local = findLocal(aVariable);
        if (local != null)
        {
            local.setCons(aValue.getCons());
            return;
        }
        GlobalVariable global = new GlobalVariable(aValue);
        iGlobalState.setAssociation(global, aVariable);
        if (aGlobalLazyVariable)
        {
            global.setEvalBeforeReturn(true);
        }
    }

    public void getVariable(String aVariable, ConsPointer aResult) throws Exception
    {
        aResult.setCons(null);
        ConsPointer local = findLocal(aVariable);
        if (local != null)
        {
            aResult.setCons(local.getCons());
            return;
        }
        GlobalVariable l = (GlobalVariable) iGlobalState.lookUp(aVariable);
        if (l != null)
        {
            if (l.iEvalBeforeReturn)
            {
                iEvaluator.evaluate(this, aResult, l.iValue);
                l.iValue.setCons(aResult.getCons());
                l.iEvalBeforeReturn = false;
                return;
            } else
            {
                aResult.setCons(l.iValue.getCons());
                return;
            }
        }
    }

    public void unsetVariable(String aString) throws Exception
    {
        ConsPointer local = findLocal(aString);
        if (local != null)
        {
            local.setCons(null);
            return;
        }
        iGlobalState.release(aString);
    }

    public void pushLocalFrame(boolean aFenced)
    {
        if (aFenced)
        {
            LocalVariableFrame newFrame =
                    new LocalVariableFrame(iLocalsList, null);
            iLocalsList = newFrame;
        } else
        {
            LocalVariableFrame newFrame =
                    new LocalVariableFrame(iLocalsList, iLocalsList.iFirst);
            iLocalsList = newFrame;
        }
    }

    public void popLocalFrame() throws Exception
    {
        LispError.lispAssert(iLocalsList != null);
        LocalVariableFrame nextFrame = iLocalsList.iNext;
        iLocalsList.delete();
        iLocalsList = nextFrame;
    }

    public void newLocal(String aVariable, Cons aValue) throws Exception
    {
        LispError.lispAssert(iLocalsList != null);
        iLocalsList.add(new LispLocalVariable(aVariable, aValue));
    }

    class LispLocalVariable
    {

        public LispLocalVariable(String aVariable, Cons aValue)
        {
            iNext = null;
            iVariable = aVariable;
            iValue.setCons(aValue);

        }
        LispLocalVariable iNext;
        String iVariable;
        ConsPointer iValue = new ConsPointer();
    }

    class LocalVariableFrame
    {

        public LocalVariableFrame(LocalVariableFrame aNext, LispLocalVariable aFirst)
        {
            iNext = aNext;
            iFirst = aFirst;
            iLast = aFirst;
        }

        void add(LispLocalVariable aNew)
        {
            aNew.iNext = iFirst;
            iFirst = aNew;
        }

        void delete()
        {
            LispLocalVariable t = iFirst;
            LispLocalVariable next;
            while (t != iLast)
            {
                next = t.iNext;
                t = next;
            }
        }
        LocalVariableFrame iNext;
        LispLocalVariable iFirst;
        LispLocalVariable iLast;
    }

    public int getUniqueId()
    {
        return iLastUniqueId++;
    }

    public void holdArgument(String aOperator, String aVariable) throws Exception
    {
        MultipleArityUserFunction multiUserFunc = (MultipleArityUserFunction) iUserFunctions.lookUp(aOperator);
        LispError.check(multiUserFunc != null, LispError.KLispErrInvalidArg);
        multiUserFunc.holdArgument(aVariable);
    }

    public void retract(String aOperator, int aArity) throws Exception
    {
        MultipleArityUserFunction multiUserFunc = (MultipleArityUserFunction) iUserFunctions.lookUp(aOperator);
        if (multiUserFunc != null)
        {
            multiUserFunc.deleteBase(aArity);
        }
    }

    public UserFunction userFunction(ConsPointer aArguments) throws Exception
    {
        MultipleArityUserFunction multiUserFunc =
                (MultipleArityUserFunction) iUserFunctions.lookUp(aArguments.getCons().string());
        if (multiUserFunc != null)
        {
            int arity = UtilityFunctions.internalListLength(aArguments) - 1;
            return multiUserFunc.userFunction(arity);
        }
        return null;
    }

    public UserFunction userFunction(String aName, int aArity) throws Exception
    {
        MultipleArityUserFunction multiUserFunc = (MultipleArityUserFunction) iUserFunctions.lookUp(aName);
        if (multiUserFunc != null)
        {
            return multiUserFunc.userFunction(aArity);
        }
        return null;
    }

    public void unFenceRule(String aOperator, int aArity) throws Exception
    {
        MultipleArityUserFunction multiUserFunc = (MultipleArityUserFunction) iUserFunctions.lookUp(aOperator);

        LispError.check(multiUserFunc != null, LispError.KLispErrInvalidArg);
        UserFunction userFunc = multiUserFunc.userFunction(aArity);
        LispError.check(userFunc != null, LispError.KLispErrInvalidArg);
        userFunc.unFence();
    }

    public MultipleArityUserFunction multiUserFunction(String aOperator) throws Exception
    {
        // Find existing multiuser func.
        MultipleArityUserFunction multiUserFunc = (MultipleArityUserFunction) iUserFunctions.lookUp(aOperator);

        // If none exists, add one to the user functions list
        if (multiUserFunc == null)
        {
            MultipleArityUserFunction newMulti = new MultipleArityUserFunction();
            iUserFunctions.setAssociation(newMulti, aOperator);
            multiUserFunc = (MultipleArityUserFunction) iUserFunctions.lookUp(aOperator);
            LispError.check(multiUserFunc != null, LispError.KLispErrCreatingUserFunction);
        }
        return multiUserFunc;
    }

    public void declareRuleBase(String aOperator, ConsPointer aParameters, boolean aListed) throws Exception
    {
        MultipleArityUserFunction multiUserFunc = multiUserFunction(aOperator);

        // add an operator with this arity to the multiuserfunc.
        BranchingUserFunction newFunc;
        if (aListed)
        {
            newFunc = new ListedBranchingUserFunction(aParameters);
        } else
        {
            newFunc = new BranchingUserFunction(aParameters);
        }
        multiUserFunc.defineRuleBase(newFunc);
    }

    public void defineRule(String aOperator, int aArity,
            int aPrecedence, ConsPointer aPredicate,
            ConsPointer aBody) throws Exception
    {
        // Find existing multiuser func.
        MultipleArityUserFunction multiUserFunc =
                (MultipleArityUserFunction) iUserFunctions.lookUp(aOperator);
        LispError.check(multiUserFunc != null, LispError.KLispErrCreatingRule);

        // Get the specific user function with the right arity
        UserFunction userFunc = (UserFunction) multiUserFunc.userFunction(aArity);
        LispError.check(userFunc != null, LispError.KLispErrCreatingRule);

        // Declare a new evaluation rule


        if (UtilityFunctions.isTrue(this, aPredicate))
        {
            //        printf("FastPredicate on %s\n",aOperator->String());
            userFunc.declareRule(aPrecedence, aBody);
        } else
        {
            userFunc.declareRule(aPrecedence, aPredicate, aBody);
        }
    }

    public void declareMacroRuleBase(String aOperator, ConsPointer aParameters, boolean aListed) throws Exception
    {
        MultipleArityUserFunction multiUserFunc = multiUserFunction(aOperator);
        MacroUserFunction newFunc;
        if (aListed)
        {
            newFunc = new ListedMacroUserFunction(aParameters);
        } else
        {
            newFunc = new MacroUserFunction(aParameters);
        }
        multiUserFunc.defineRuleBase(newFunc);
    }

    public void defineRulePattern(String aOperator, int aArity, int aPrecedence, ConsPointer aPredicate, ConsPointer aBody) throws Exception
    {
        // Find existing multiuser func.
        MultipleArityUserFunction multiUserFunc = (MultipleArityUserFunction) iUserFunctions.lookUp(aOperator);
        LispError.check(multiUserFunc != null, LispError.KLispErrCreatingRule);

        // Get the specific user function with the right arity
        UserFunction userFunc = multiUserFunc.userFunction(aArity);
        LispError.check(userFunc != null, LispError.KLispErrCreatingRule);

        // Declare a new evaluation rule
        userFunc.declarePattern(aPrecedence, aPredicate, aBody);
    }
    
    /**
     * Write data to the current output.
     * @param aString
     * @throws java.lang.Exception
     */
    public void write(String aString) throws Exception
    {
        iCurrentOutput.write(aString);
    }
}

