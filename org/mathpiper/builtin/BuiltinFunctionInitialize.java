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

package org.mathpiper.builtin;

import org.mathpiper.builtin.functions.*;
import org.mathpiper.lisp.ConsPointer;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.userfunctions.Evaluator;
import org.mathpiper.printers.InfixPrinter;


public abstract class BuiltinFunctionInitialize
{
	public abstract void eval(Environment aEnvironment,int aStackTop) throws Exception;

	public static ConsPointer getResult(Environment aEnvironment,int aStackTop) throws Exception
	{
		return aEnvironment.iArgumentStack.getElement(aStackTop);
	}
	
	public static ConsPointer getArgumentPointer(Environment aEnvironment,int aStackTop, int argumentPosition)  throws Exception
	{
		return aEnvironment.iArgumentStack.getElement(aStackTop+argumentPosition);
	}

	public static ConsPointer getArgumentPointer(ConsPointer cur, int n) throws Exception
	{
		LispError.lispAssert(n>=0);

		ConsPointer loop = cur;
		while(n != 0)
		{
			n--;
			loop = loop.getCons().rest();
		}
		return loop;
	}
        
        
     public static void addFunctions(Environment aEnvironment)
    {
        aEnvironment.iBodiedOperators.SetOperator(InfixPrinter.KMaxPrecedence, "While");
        aEnvironment.iBodiedOperators.SetOperator(InfixPrinter.KMaxPrecedence, "Rule");
        aEnvironment.iBodiedOperators.SetOperator(InfixPrinter.KMaxPrecedence, "MacroRule");
        aEnvironment.iBodiedOperators.SetOperator(InfixPrinter.KMaxPrecedence, "RulePattern");
        aEnvironment.iBodiedOperators.SetOperator(InfixPrinter.KMaxPrecedence, "MacroRulePattern");
        aEnvironment.iBodiedOperators.SetOperator(InfixPrinter.KMaxPrecedence, "FromFile");
        aEnvironment.iBodiedOperators.SetOperator(InfixPrinter.KMaxPrecedence, "FromString");
        aEnvironment.iBodiedOperators.SetOperator(InfixPrinter.KMaxPrecedence, "ToFile");
        aEnvironment.iBodiedOperators.SetOperator(InfixPrinter.KMaxPrecedence, "ToString");
        aEnvironment.iBodiedOperators.SetOperator(InfixPrinter.KMaxPrecedence, "ToStdout");
        aEnvironment.iBodiedOperators.SetOperator(InfixPrinter.KMaxPrecedence, "TraceRule");
        aEnvironment.iBodiedOperators.SetOperator(InfixPrinter.KMaxPrecedence, "Subst");
        aEnvironment.iBodiedOperators.SetOperator(InfixPrinter.KMaxPrecedence, "LocalSymbols");
        aEnvironment.iBodiedOperators.SetOperator(InfixPrinter.KMaxPrecedence, "BackQuote");
        aEnvironment.iPrefixOperators.SetOperator(0, "`");
        aEnvironment.iPrefixOperators.SetOperator(0, "@");
        aEnvironment.iPrefixOperators.SetOperator(0, "_");
        aEnvironment.iInfixOperators.SetOperator(0, "_");

        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Quote(), 1, Evaluator.Fixed | Evaluator.Macro),
                "Hold");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Eval(), 1, Evaluator.Fixed | Evaluator.Function),
                "Eval");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Write(), 1, Evaluator.Variable | Evaluator.Function),
                "Write");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new WriteString(), 1, Evaluator.Fixed | Evaluator.Function),
                "WriteString");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new FullForm(), 1, Evaluator.Fixed | Evaluator.Function),
                "FullForm");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new DefaultDirectory(), 1, Evaluator.Fixed | Evaluator.Function),
                "DefaultDirectory");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new FromFile(), 2, Evaluator.Fixed | Evaluator.Macro),
                "FromFile");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new FromString(), 2, Evaluator.Fixed | Evaluator.Macro),
                "FromString");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Read(), 0, Evaluator.Fixed | Evaluator.Function),
                "Read");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new ReadToken(), 0, Evaluator.Fixed | Evaluator.Function),
                "ReadToken");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new ToFile(), 2, Evaluator.Fixed | Evaluator.Macro),
                "ToFile");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new ToString(), 1, Evaluator.Fixed | Evaluator.Macro),
                "ToString");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new ToStdout(), 1, Evaluator.Fixed | Evaluator.Macro),
                "ToStdout");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Load(), 1, Evaluator.Fixed | Evaluator.Function),
                "Load");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new SetVar(), 2, Evaluator.Fixed | Evaluator.Macro),
                "Set");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new MacroSetVar(), 2, Evaluator.Fixed | Evaluator.Macro),
                "MacroSet");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new ClearVar(), 1, Evaluator.Variable | Evaluator.Macro),
                "Clear");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new ClearVar(), 1, Evaluator.Variable | Evaluator.Function),
                "MacroClear");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new NewLocal(), 1, Evaluator.Variable | Evaluator.Macro),
                "Local");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new NewLocal(), 1, Evaluator.Variable | Evaluator.Function),
                "MacroLocal");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Head(), 1, Evaluator.Fixed | Evaluator.Function),
                "Head");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Nth(), 2, Evaluator.Fixed | Evaluator.Function),
                "MathNth");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Tail(), 1, Evaluator.Fixed | Evaluator.Function),
                "Tail");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new DestructiveReverse(), 1, Evaluator.Fixed | Evaluator.Function),
                "DestructiveReverse");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Length(), 1, Evaluator.Fixed | Evaluator.Function),
                "Length");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new org.mathpiper.builtin.functions.List(), 1, Evaluator.Variable | Evaluator.Macro),
                "List");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new UnList(), 1, Evaluator.Fixed | Evaluator.Function),
                "UnList");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Listify(), 1, Evaluator.Fixed | Evaluator.Function),
                "Listify");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Concatenate(), 1, Evaluator.Variable | Evaluator.Function),
                "Concat");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new ConcatenateStrings(), 1, Evaluator.Variable | Evaluator.Function),
                "ConcatStrings");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Delete(), 2, Evaluator.Fixed | Evaluator.Function),
                "Delete");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new DestructiveDelete(), 2, Evaluator.Fixed | Evaluator.Function),
                "DestructiveDelete");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Insert(), 3, Evaluator.Fixed | Evaluator.Function),
                "Insert");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new DestructiveInsert(), 3, Evaluator.Fixed | Evaluator.Function),
                "DestructiveInsert");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Replace(), 3, Evaluator.Fixed | Evaluator.Function),
                "Replace");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new DestructiveReplace(), 3, Evaluator.Fixed | Evaluator.Function),
                "DestructiveReplace");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Atomize(), 1, Evaluator.Fixed | Evaluator.Function),
                "Atom");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Stringify(), 1, Evaluator.Fixed | Evaluator.Function),
                "String");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new CharString(), 1, Evaluator.Fixed | Evaluator.Function),
                "CharString");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new FlatCopy(), 1, Evaluator.Fixed | Evaluator.Function),
                "FlatCopy");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new ProgBody(), 1, Evaluator.Variable | Evaluator.Macro),
                "Prog");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new While(), 2, Evaluator.Fixed | Evaluator.Macro),
                "While");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new If(), 2, Evaluator.Variable | Evaluator.Macro),
                "If");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Check(), 2, Evaluator.Fixed | Evaluator.Macro),
                "Check");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new TrapError(), 2, Evaluator.Fixed | Evaluator.Macro),
                "TrapError");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new GetCoreError(), 0, Evaluator.Fixed | Evaluator.Function),
                "GetCoreError");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new PreFix(), 2, Evaluator.Fixed | Evaluator.Function),
                "Prefix");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new InFix(), 2, Evaluator.Fixed | Evaluator.Function),
                "Infix");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new PostFix(), 2, Evaluator.Fixed | Evaluator.Function),
                "Postfix");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Bodied(), 2, Evaluator.Fixed | Evaluator.Function),
                "Bodied");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new RuleBase(), 2, Evaluator.Fixed | Evaluator.Macro),
                "RuleBase");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new MacroRuleBase(), 2, Evaluator.Fixed | Evaluator.Function),
                "MacroRuleBase");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new RuleBaseListed(), 2, Evaluator.Fixed | Evaluator.Macro),
                "RuleBaseListed");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new MacroRuleBaseListed(), 2, Evaluator.Fixed | Evaluator.Function),
                "MacroRuleBaseListed");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new DefMacroRuleBase(), 2, Evaluator.Fixed | Evaluator.Macro),
                "DefMacroRuleBase");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new DefMacroRuleBaseListed(), 2, Evaluator.Fixed | Evaluator.Macro),
                "DefMacroRuleBaseListed");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new HoldArg(), 2, Evaluator.Fixed | Evaluator.Macro),
                "HoldArg");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new NewRule(), 5, Evaluator.Fixed | Evaluator.Macro),
                "Rule");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new MacroNewRule(), 5, Evaluator.Fixed | Evaluator.Function),
                "MacroRule");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new UnFence(), 2, Evaluator.Fixed | Evaluator.Function),
                "UnFence");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Retract(), 2, Evaluator.Fixed | Evaluator.Function),
                "Retract");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Not(), 1, Evaluator.Fixed | Evaluator.Function),
                "NotN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Not(), 1, Evaluator.Fixed | Evaluator.Function),
                "Not");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new LazyAnd(), 1, Evaluator.Variable | Evaluator.Macro),
                "AndN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new LazyAnd(), 1, Evaluator.Variable | Evaluator.Macro),
                "And");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new LazyOr(), 1, Evaluator.Variable | Evaluator.Macro),
                "OrN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new LazyOr(), 1, Evaluator.Variable | Evaluator.Macro),
                "Or");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Equals(), 2, Evaluator.Fixed | Evaluator.Function),
                "Equals");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Equals(), 2, Evaluator.Fixed | Evaluator.Function),
                "=");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new LessThan(), 2, Evaluator.Fixed | Evaluator.Function),
                "LessThan");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new GreaterThan(), 2, Evaluator.Fixed | Evaluator.Function),
                "GreaterThan");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new IsFunction(), 1, Evaluator.Fixed | Evaluator.Function),
                "IsFunction");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new IsAtom(), 1, Evaluator.Fixed | Evaluator.Function),
                "IsAtom");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new IsNumber(), 1, Evaluator.Fixed | Evaluator.Function),
                "IsNumber");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new IsInteger(), 1, Evaluator.Fixed | Evaluator.Function),
                "IsInteger");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new IsList(), 1, Evaluator.Fixed | Evaluator.Function),
                "IsList");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new IsString(), 1, Evaluator.Fixed | Evaluator.Function),
                "IsString");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new IsBound(), 1, Evaluator.Fixed | Evaluator.Macro),
                "IsBound");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Multiply(), 2, Evaluator.Fixed | Evaluator.Function),
                "MultiplyN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Add(), 2, Evaluator.Fixed | Evaluator.Function),
                "AddN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Subtract(), 2, Evaluator.Fixed | Evaluator.Function),
                "SubtractN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Divide(), 2, Evaluator.Fixed | Evaluator.Function),
                "DivideN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new BuiltinPrecisionSet(), 1, Evaluator.Fixed | Evaluator.Function),
                "BuiltinPrecisionSet");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new GetExactBits(), 1, Evaluator.Fixed | Evaluator.Function),
                "GetExactBitsN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new SetExactBits(), 2, Evaluator.Fixed | Evaluator.Function),
                "SetExactBitsN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new BitCount(), 1, Evaluator.Fixed | Evaluator.Function),
                "MathBitCount");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new MathSign(), 1, Evaluator.Fixed | Evaluator.Function),
                "MathSign");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new MathIsSmall(), 1, Evaluator.Fixed | Evaluator.Function),
                "MathIsSmall");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new MathNegate(), 1, Evaluator.Fixed | Evaluator.Function),
                "MathNegate");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Floor(), 1, Evaluator.Fixed | Evaluator.Function),
                "FloorN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Ceil(), 1, Evaluator.Fixed | Evaluator.Function),
                "CeilN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Abs(), 1, Evaluator.Fixed | Evaluator.Function),
                "AbsN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Mod(), 2, Evaluator.Fixed | Evaluator.Function),
                "ModN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Div(), 2, Evaluator.Fixed | Evaluator.Function),
                "DivN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new BitsToDigits(), 2, Evaluator.Fixed | Evaluator.Function),
                "BitsToDigits");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new DigitsToBits(), 2, Evaluator.Fixed | Evaluator.Function),
                "DigitsToBits");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Gcd(), 2, Evaluator.Fixed | Evaluator.Function),
                "GcdN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new SystemCall(), 1, Evaluator.Fixed | Evaluator.Function),
                "SystemCall");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new FastArcSin(), 1, Evaluator.Fixed | Evaluator.Function),
                "FastArcSin");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new FastLog(), 1, Evaluator.Fixed | Evaluator.Function),
                "FastLog");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new FastPower(), 2, Evaluator.Fixed | Evaluator.Function),
                "FastPower");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new ShiftLeft(), 2, Evaluator.Fixed | Evaluator.Function),
                "ShiftLeft");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new ShiftRight(), 2, Evaluator.Fixed | Evaluator.Function),
                "ShiftRight");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new FromBase(), 2, Evaluator.Fixed | Evaluator.Function),
                "FromBase");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new ToBase(), 2, Evaluator.Fixed | Evaluator.Function),
                "ToBase");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new MaxEvalDepth(), 1, Evaluator.Fixed | Evaluator.Function),
                "MaxEvalDepth");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new DefLoad(), 1, Evaluator.Fixed | Evaluator.Function),
                "DefLoad");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Use(), 1, Evaluator.Fixed | Evaluator.Function),
                "Use");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new RightAssociative(), 1, Evaluator.Fixed | Evaluator.Function),
                "RightAssociative");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new LeftPrecedence(), 2, Evaluator.Fixed | Evaluator.Function),
                "LeftPrecedence");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new RightPrecedence(), 2, Evaluator.Fixed | Evaluator.Function),
                "RightPrecedence");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new IsBodied(), 1, Evaluator.Fixed | Evaluator.Function),
                "IsBodied");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new IsInFix(), 1, Evaluator.Fixed | Evaluator.Function),
                "IsInfix");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new IsPreFix(), 1, Evaluator.Fixed | Evaluator.Function),
                "IsPrefix");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new IsPostFix(), 1, Evaluator.Fixed | Evaluator.Function),
                "IsPostfix");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new GetPrecedence(), 1, Evaluator.Fixed | Evaluator.Function),
                "OpPrecedence");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new GetLeftPrecedence(), 1, Evaluator.Fixed | Evaluator.Function),
                "OpLeftPrecedence");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new GetRightPrecedence(), 1, Evaluator.Fixed | Evaluator.Function),
                "OpRightPrecedence");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new BuiltinPrecisionGet(), 0, Evaluator.Fixed | Evaluator.Function),
                "BuiltinPrecisionGet");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new BitAnd(), 2, Evaluator.Fixed | Evaluator.Function),
                "BitAnd");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new BitOr(), 2, Evaluator.Fixed | Evaluator.Function),
                "BitOr");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new BitXor(), 2, Evaluator.Fixed | Evaluator.Function),
                "BitXor");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Secure(), 1, Evaluator.Fixed | Evaluator.Macro),
                "Secure");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new FindFile(), 1, Evaluator.Fixed | Evaluator.Function),
                "FindFile");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new FindFunction(), 1, Evaluator.Fixed | Evaluator.Function),
                "FindFunction");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new IsGeneric(), 1, Evaluator.Fixed | Evaluator.Function),
                "IsGeneric");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new GenericTypeName(), 1, Evaluator.Fixed | Evaluator.Function),
                "GenericTypeName");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new GenArrayCreate(), 2, Evaluator.Fixed | Evaluator.Function),
                "ArrayCreate");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new GenArraySize(), 1, Evaluator.Fixed | Evaluator.Function),
                "ArraySize");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new GenArrayGet(), 2, Evaluator.Fixed | Evaluator.Function),
                "ArrayGet");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new GenArraySet(), 3, Evaluator.Fixed | Evaluator.Function),
                "ArraySet");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new CustomEval(), 4, Evaluator.Fixed | Evaluator.Macro),
                "CustomEval");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new CustomEvalExpression(), 0, Evaluator.Fixed | Evaluator.Function),
                "CustomEval'Expression");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new CustomEvalResult(), 0, Evaluator.Fixed | Evaluator.Function),
                "CustomEval'Result");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new CustomEvalLocals(), 0, Evaluator.Fixed | Evaluator.Function),
                "CustomEval'Locals");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new CustomEvalStop(), 0, Evaluator.Fixed | Evaluator.Function),
                "CustomEval'Stop");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new TraceRule(), 2, Evaluator.Fixed | Evaluator.Macro),
                "TraceRule");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new TraceStack(), 1, Evaluator.Fixed | Evaluator.Macro),
                "TraceStack");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new LispRead(), 0, Evaluator.Fixed | Evaluator.Function),
                "LispRead");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new ReadLispListed(), 0, Evaluator.Fixed | Evaluator.Function),
                "LispReadListed");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new org.mathpiper.builtin.functions.Type(), 1, Evaluator.Fixed | Evaluator.Function),
                "Type");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new StringMidGet(), 3, Evaluator.Fixed | Evaluator.Function),
                "StringMidGet");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new StringMidSet(), 3, Evaluator.Fixed | Evaluator.Function),
                "StringMidSet");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new GenPatternCreate(), 2, Evaluator.Fixed | Evaluator.Function),
                "Pattern'Create");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new GenPatternMatches(), 2, Evaluator.Fixed | Evaluator.Function),
                "Pattern'Matches");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new RuleBaseDefined(), 2, Evaluator.Fixed | Evaluator.Function),
                "RuleBaseDefined");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new DefLoadFunction(), 1, Evaluator.Fixed | Evaluator.Function),
                "DefLoadFunction");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new RuleBaseArgList(), 2, Evaluator.Fixed | Evaluator.Function),
                "RuleBaseArgList");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new NewRulePattern(), 5, Evaluator.Fixed | Evaluator.Macro),
                "RulePattern");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new MacroNewRulePattern(), 5, Evaluator.Fixed | Evaluator.Function),
                "MacroRulePattern");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Subst(), 3, Evaluator.Fixed | Evaluator.Function),
                "Subst");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new LocalSymbols(), 1, Evaluator.Variable | Evaluator.Macro),
                "LocalSymbols");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new FastIsPrime(), 1, Evaluator.Fixed | Evaluator.Function),
                "FastIsPrime");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Fac(), 1, Evaluator.Fixed | Evaluator.Function),
                "MathFac");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new ApplyPure(), 2, Evaluator.Fixed | Evaluator.Function),
                "ApplyPure");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new PrettyReaderSet(), 1, Evaluator.Variable | Evaluator.Function),
                "PrettyReader'Set");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new PrettyPrinterSet(), 1, Evaluator.Variable | Evaluator.Function),
                "PrettyPrinter'Set");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new PrettyPrinterGet(), 0, Evaluator.Fixed | Evaluator.Function),
                "PrettyPrinter'Get");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new PrettyReaderGet(), 0, Evaluator.Fixed | Evaluator.Function),
                "PrettyReader'Get");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new GarbageCollect(), 0, Evaluator.Fixed | Evaluator.Function),
                "GarbageCollect");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new SetGlobalLazyVariable(), 2, Evaluator.Fixed | Evaluator.Macro),
                "SetGlobalLazyVariable");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new PatchLoad(), 1, Evaluator.Fixed | Evaluator.Function),
                "PatchLoad");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new PatchString(), 1, Evaluator.Fixed | Evaluator.Function),
                "PatchString");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new ExtraInfoSet(), 2, Evaluator.Fixed | Evaluator.Function),
                "ExtraInfoSet");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new ExtraInfoGet(), 1, Evaluator.Fixed | Evaluator.Function),
                "ExtraInfo'Get");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new DefaultTokenizer(), 0, Evaluator.Fixed | Evaluator.Function),
                "DefaultTokenizer");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new CommonLispTokenizer(), 0, Evaluator.Fixed | Evaluator.Function),
                "CommonLispTokenizer");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new XmlTokenizer(), 0, Evaluator.Fixed | Evaluator.Function),
                "XmlTokenizer");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new ExplodeTag(), 1, Evaluator.Fixed | Evaluator.Function),
                "XmlExplodeTag");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new BuiltinAssoc(), 2, Evaluator.Fixed | Evaluator.Function),
                "Builtin'Assoc");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new CurrentFile(), 0, Evaluator.Fixed | Evaluator.Function),
                "CurrentFile");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new CurrentLine(), 0, Evaluator.Fixed | Evaluator.Function),
                "CurrentLine");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new BackQuote(), 1, Evaluator.Fixed | Evaluator.Macro),
                "`");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new DumpBigNumberDebugInfo(), 1, Evaluator.Fixed | Evaluator.Function),
                "MathDebugInfo");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new InDebugMode(), 0, Evaluator.Fixed | Evaluator.Function),
                "InDebugMode");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new DebugFile(), 1, Evaluator.Fixed | Evaluator.Function),
                "DebugFile");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new DebugLine(), 1, Evaluator.Fixed | Evaluator.Function),
                "DebugLine");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new org.mathpiper.builtin.functions.Version(), 0, Evaluator.Fixed | Evaluator.Function),
                "Version");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new Exit(), 0, Evaluator.Fixed | Evaluator.Function),
                "Exit");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new ExitRequested(), 0, Evaluator.Fixed | Evaluator.Function),
                "IsExitRequested");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new HistorySize(), 1, Evaluator.Fixed | Evaluator.Function),
                "HistorySize");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new StackSize(), 0, Evaluator.Fixed | Evaluator.Function),
                "StaSiz");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new IsPromptShown(), 0, Evaluator.Fixed | Evaluator.Function),
                "IsPromptShown");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new ReadCmdLineString(), 1, Evaluator.Fixed | Evaluator.Function),
                "ReadCmdLineString");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new org.mathpiper.builtin.functions.Time(), 1, Evaluator.Fixed | Evaluator.Macro),
                "GetTime");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new FileSize(), 1, Evaluator.Fixed | Evaluator.Function),
                "FileSize");
	
        //Note:tk:The functions below this point need to have documentation created for them.
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new org.mathpiper.builtin.functions.TraceOn(), 0, Evaluator.Fixed | Evaluator.Function),
                "TraceOn");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new Evaluator(new org.mathpiper.builtin.functions.TraceOff(), 0, Evaluator.Fixed | Evaluator.Function),
                "TraceOff");

    }


}
