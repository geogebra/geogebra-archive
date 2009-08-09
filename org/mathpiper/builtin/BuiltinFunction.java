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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import org.mathpiper.builtin.functions.core.Abs;
import org.mathpiper.builtin.functions.core.Add;
import org.mathpiper.builtin.functions.core.ApplyPure;
import org.mathpiper.builtin.functions.core.Atom;
import org.mathpiper.builtin.functions.core.BackQuote;
import org.mathpiper.builtin.functions.core.BitAnd;
import org.mathpiper.builtin.functions.core.BitCount;
import org.mathpiper.builtin.functions.core.BitOr;
import org.mathpiper.builtin.functions.core.BitXor;
import org.mathpiper.builtin.functions.core.BitsToDigits;
import org.mathpiper.builtin.functions.core.Bodied;
import org.mathpiper.builtin.functions.core.BuiltinAssoc;
import org.mathpiper.builtin.functions.core.BuiltinPrecisionGet;
import org.mathpiper.builtin.functions.core.BuiltinPrecisionSet;
import org.mathpiper.builtin.functions.core.Ceil;
import org.mathpiper.builtin.functions.core.CharString;
import org.mathpiper.builtin.functions.core.Check;
import org.mathpiper.builtin.functions.core.Clear;
import org.mathpiper.builtin.functions.core.CommonLispTokenizer;
import org.mathpiper.builtin.functions.core.Concatenate;
import org.mathpiper.builtin.functions.core.ConcatenateStrings;
import org.mathpiper.builtin.functions.core.CurrentFile;
import org.mathpiper.builtin.functions.core.CurrentLine;
import org.mathpiper.builtin.functions.core.CustomEval;
import org.mathpiper.builtin.functions.core.CustomEvalExpression;
import org.mathpiper.builtin.functions.core.CustomEvalLocals;
import org.mathpiper.builtin.functions.core.CustomEvalResult;
import org.mathpiper.builtin.functions.core.CustomEvalStop;
import org.mathpiper.builtin.functions.core.DebugFile;
import org.mathpiper.builtin.functions.core.DebugLine;
import org.mathpiper.builtin.functions.core.DefLoad;
import org.mathpiper.builtin.functions.core.DefLoadFunction;
import org.mathpiper.builtin.functions.core.DefMacroRulebase;
import org.mathpiper.builtin.functions.core.DefMacroRulebaseListed;
import org.mathpiper.builtin.functions.core.DefaultDirectory;
import org.mathpiper.builtin.functions.core.DefaultTokenizer;
import org.mathpiper.builtin.functions.core.Delete;
import org.mathpiper.builtin.functions.core.DestructiveDelete;
import org.mathpiper.builtin.functions.core.DestructiveInsert;
import org.mathpiper.builtin.functions.core.DestructiveReplace;
import org.mathpiper.builtin.functions.core.DestructiveReverse;
import org.mathpiper.builtin.functions.core.DigitsToBits;
import org.mathpiper.builtin.functions.core.Div;
import org.mathpiper.builtin.functions.core.Divide;
import org.mathpiper.builtin.functions.core.DumpBigNumberDebugInfo;
import org.mathpiper.builtin.functions.core.Equals;
import org.mathpiper.builtin.functions.core.Eval;
import org.mathpiper.builtin.functions.core.Exit;
import org.mathpiper.builtin.functions.core.ExitRequested;
import org.mathpiper.builtin.functions.core.XmlExplodeTag;
import org.mathpiper.builtin.functions.core.ExtraInfoGet;
import org.mathpiper.builtin.functions.core.ExtraInfoSet;
import org.mathpiper.builtin.functions.core.Fac;
import org.mathpiper.builtin.functions.core.FastArcSin;
import org.mathpiper.builtin.functions.core.FastIsPrime;
import org.mathpiper.builtin.functions.core.FastLog;
import org.mathpiper.builtin.functions.core.FastPower;
import org.mathpiper.builtin.functions.core.FileSize;
import org.mathpiper.builtin.functions.core.FindFile;
import org.mathpiper.builtin.functions.core.FindFunction;
import org.mathpiper.builtin.functions.core.FlatCopy;
import org.mathpiper.builtin.functions.core.Floor;
import org.mathpiper.builtin.functions.core.FromBase;
import org.mathpiper.builtin.functions.core.FromFile;
import org.mathpiper.builtin.functions.core.FromString;
import org.mathpiper.builtin.functions.core.FullForm;
import org.mathpiper.builtin.functions.core.GarbageCollect;
import org.mathpiper.builtin.functions.core.Gcd;
import org.mathpiper.builtin.functions.core.ArrayCreate;
import org.mathpiper.builtin.functions.core.ArrayGet;
import org.mathpiper.builtin.functions.core.ArraySet;
import org.mathpiper.builtin.functions.core.ArraySize;
import org.mathpiper.builtin.functions.core.PatternCreate;
import org.mathpiper.builtin.functions.core.PatternMatches;
import org.mathpiper.builtin.functions.core.GenericTypeName;
import org.mathpiper.builtin.functions.core.GetCoreError;
import org.mathpiper.builtin.functions.core.GetExactBits;
import org.mathpiper.builtin.functions.core.OpLeftPrecedence;
import org.mathpiper.builtin.functions.core.OpPrecedence;
import org.mathpiper.builtin.functions.core.OpRightPrecedence;
import org.mathpiper.builtin.functions.core.GreaterThan;
import org.mathpiper.builtin.functions.core.First;
import org.mathpiper.builtin.functions.core.HistorySize;
import org.mathpiper.builtin.functions.core.HoldArg;
import org.mathpiper.builtin.functions.core.If;
import org.mathpiper.builtin.functions.core.InDebugMode;
import org.mathpiper.builtin.functions.core.Infix;
import org.mathpiper.builtin.functions.core.Insert;
import org.mathpiper.builtin.functions.core.IsAtom;
import org.mathpiper.builtin.functions.core.IsBodied;
import org.mathpiper.builtin.functions.core.IsBound;
import org.mathpiper.builtin.functions.core.IsFunction;
import org.mathpiper.builtin.functions.core.IsGeneric;
import org.mathpiper.builtin.functions.core.IsInfix;
import org.mathpiper.builtin.functions.core.IsInteger;
import org.mathpiper.builtin.functions.core.IsList;
import org.mathpiper.builtin.functions.core.IsNumber;
import org.mathpiper.builtin.functions.core.IsPostfix;
import org.mathpiper.builtin.functions.core.IsPrefix;
import org.mathpiper.builtin.functions.core.IsPromptShown;
import org.mathpiper.builtin.functions.core.IsString;
import org.mathpiper.builtin.functions.core.And;
import org.mathpiper.builtin.functions.core.ExpressionToString;
import org.mathpiper.builtin.functions.core.Or;
import org.mathpiper.builtin.functions.core.LeftPrecedence;
import org.mathpiper.builtin.functions.core.Length;
import org.mathpiper.builtin.functions.core.LessThan;
import org.mathpiper.builtin.functions.core.Listify;
import org.mathpiper.builtin.functions.core.Load;
import org.mathpiper.builtin.functions.core.LocalSymbols;
import org.mathpiper.builtin.functions.core.MacroRule;
import org.mathpiper.builtin.functions.core.MacroNewRulePattern;
import org.mathpiper.builtin.functions.core.MacroRulebase;
import org.mathpiper.builtin.functions.core.MacroRulebaseListed;
import org.mathpiper.builtin.functions.core.MacroSet;
import org.mathpiper.builtin.functions.core.MathIsSmall;
import org.mathpiper.builtin.functions.core.MathNegate;
import org.mathpiper.builtin.functions.core.MathSign;
import org.mathpiper.builtin.functions.core.MaxEvalDepth;
import org.mathpiper.builtin.functions.core.Mod;
import org.mathpiper.builtin.functions.core.Multiply;
import org.mathpiper.builtin.functions.core.Local;
import org.mathpiper.builtin.functions.core.Rule;
import org.mathpiper.builtin.functions.core.NewRulePattern;
import org.mathpiper.builtin.functions.core.Not;
import org.mathpiper.builtin.functions.core.Nth;
import org.mathpiper.builtin.functions.core.PatchLoad;
import org.mathpiper.builtin.functions.core.PatchString;
import org.mathpiper.builtin.functions.core.Postfix;
import org.mathpiper.builtin.functions.core.Prefix;
import org.mathpiper.builtin.functions.core.PrettyPrinterGet;
import org.mathpiper.builtin.functions.core.PrettyPrinterSet;
import org.mathpiper.builtin.functions.core.PrettyReaderGet;
import org.mathpiper.builtin.functions.core.PrettyReaderSet;
import org.mathpiper.builtin.functions.core.Prog;
import org.mathpiper.builtin.functions.core.Hold;
import org.mathpiper.builtin.functions.core.Read;
import org.mathpiper.builtin.functions.core.AskUser;
import org.mathpiper.builtin.functions.core.LispRead;
import org.mathpiper.builtin.functions.core.LispReadListed;
import org.mathpiper.builtin.functions.core.ReadToken;
import org.mathpiper.builtin.functions.core.Replace;
import org.mathpiper.builtin.functions.core.Retract;
import org.mathpiper.builtin.functions.core.RightAssociative;
import org.mathpiper.builtin.functions.core.RightPrecedence;
import org.mathpiper.builtin.functions.core.Rulebase;
import org.mathpiper.builtin.functions.core.RulebaseArgList;
import org.mathpiper.builtin.functions.core.RulebaseDefined;
import org.mathpiper.builtin.functions.core.RulebaseListed;
import org.mathpiper.builtin.functions.core.Secure;
import org.mathpiper.builtin.functions.core.SetExactBits;
import org.mathpiper.builtin.functions.core.SetGlobalLazyVariable;
import org.mathpiper.builtin.functions.core.Set;
import org.mathpiper.builtin.functions.core.ShiftLeft;
import org.mathpiper.builtin.functions.core.ShiftRight;
import org.mathpiper.builtin.functions.core.StackSize;
import org.mathpiper.builtin.functions.core.StringMidGet;
import org.mathpiper.builtin.functions.core.StringMidSet;
import org.mathpiper.builtin.functions.core.Stringify;
import org.mathpiper.builtin.functions.core.Subst;
import org.mathpiper.builtin.functions.core.Subtract;
import org.mathpiper.builtin.functions.core.SystemCall;
import org.mathpiper.builtin.functions.core.Rest;
import org.mathpiper.builtin.functions.core.TellUser;
import org.mathpiper.builtin.functions.core.ToBase;
import org.mathpiper.builtin.functions.core.ToFile;
import org.mathpiper.builtin.functions.core.ToStdout;
import org.mathpiper.builtin.functions.core.ToString;
import org.mathpiper.builtin.functions.core.TraceRule;
import org.mathpiper.builtin.functions.core.TraceStack;
import org.mathpiper.builtin.functions.core.TrapError;
import org.mathpiper.builtin.functions.core.UnFence;
import org.mathpiper.builtin.functions.core.UnList;
import org.mathpiper.builtin.functions.core.Use;
import org.mathpiper.builtin.functions.core.While;
import org.mathpiper.builtin.functions.core.Write;
import org.mathpiper.builtin.functions.core.WriteString;
import org.mathpiper.builtin.functions.core.XmlTokenizer;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.printers.MathPiperPrinter;

public abstract class BuiltinFunction {

    public static synchronized List addOptionalFunctions(Environment aEnvironment, String functionsPath) {

        //System.out.println("MATHPIPER: " + System.getProperty("java.class.path"));
        List failList = new ArrayList();

        for (String s : System.getProperty("java.class.path").split(System.getProperty("path.separator"))) {

            //System.out.println("MATHPIPER: " + s);
            if (s.endsWith("mathpiper.jar")) {
                try {
                    java.util.zip.ZipFile zip = new java.util.zip.ZipFile(new File(s));
                    Enumeration fileEnteries = zip.entries();

                    while (fileEnteries.hasMoreElements()) {
                        ZipEntry ze = (ZipEntry) fileEnteries.nextElement();
                        String fileName = ze.getName();
                        if (fileName.contains(functionsPath)) {
                            fileName = fileName.replace("/", ".");
                            if (fileName.endsWith(".class")) {
                                fileName = fileName.substring(0, fileName.length() - 6);
                                //System.out.println(fileName);
                                try {
                                    Class functionClass = Class.forName(fileName);
                                    
                                    Object functionObject = functionClass.newInstance();
                                    if (functionObject instanceof BuiltinFunction) {
                                        BuiltinFunction function = (BuiltinFunction) functionObject;
                                        function.plugIn(aEnvironment);
                                    }//end if.
                                } catch (ClassNotFoundException cnfe) {
                                    System.out.println("Class not found: " + fileName);
                                } catch (InstantiationException ie) {
                                    System.out.println("Can not instantiate class: " + fileName);
                                } catch (IllegalAccessException iae) {
                                    System.out.println("Illegal access of class: " + fileName);
                                }
                            }
                        }
                    }//end for.
                } catch (ZipException ze) {
                    System.out.println("Error opening " + s);
                } catch (IOException ioe) {
                    System.out.println("Error opening " + s);
                }


                break;
            } else if (!s.endsWith(".jar")) {
                File packageDirectoryFile = new File(s + "/" + functionsPath.substring(0, functionsPath.length() - 1));
                if (packageDirectoryFile.exists()) {

                    //System.out.println("package directory found");
                    java.io.File[] packageDirectoryContentsArray = packageDirectoryFile.listFiles(new java.io.FilenameFilter() {

                        public boolean accept(java.io.File file, String name) {
                            if (name.startsWith(".")) {
                                return (false);
                            } else {
                                return (true);
                            }
                        }
                    });

                    Arrays.sort(packageDirectoryContentsArray);

                    for (File file : packageDirectoryContentsArray) {
                        String fileName = file.getPath();
                        fileName = fileName.substring(s.length() + 1, fileName.length());
                        fileName = fileName.replace("/", ".");
                        fileName = fileName.substring(0, fileName.length() - 6);
                        //System.out.println(fileName);
                        try {
                            Class functionClass = Class.forName(fileName);

                            Object functionObject = functionClass.newInstance();
                            if (functionObject instanceof BuiltinFunction) {
                                BuiltinFunction function = (BuiltinFunction) functionObject;
                                function.plugIn(aEnvironment);
                            }//end if.
                        } catch (ClassNotFoundException cnfe) {
                            System.out.println("Class not found: " + fileName);
                        } catch (InstantiationException ie) {
                            System.out.println("Can not instantiate class: " + fileName);
                        } catch (IllegalAccessException iae) {
                            System.out.println("Illegal access of class: " + fileName);
                        } catch (NoClassDefFoundError ncdfe) {
                            //System.out.println("Class not found: " + fileName);
                            failList.add(fileName);
                        }

                    }//end for.
                    break;
                }//end if

            }//end if/else
        }//end for.

        return failList;
    }//end method.

    public abstract void evaluate(Environment aEnvironment, int aStackTop) throws Exception;

    public static ConsPointer getTopOfStackPointer(Environment aEnvironment, int aStackTop) throws Exception {
        return aEnvironment.iArgumentStack.getElement(aStackTop);
    }

    public static ConsPointer getArgumentPointer(Environment aEnvironment, int aStackTop, int argumentPosition) throws Exception {
        return aEnvironment.iArgumentStack.getElement(aStackTop + argumentPosition);
    }

    public static ConsPointer getArgumentPointer(ConsPointer cur, int n) throws Exception {
        LispError.lispAssert(n >= 0);

        ConsPointer loop = cur;
        while (n != 0) {
            n--;
            loop = loop.cdr();
        }
        return loop;
    }

    public void plugIn(Environment aEnvironment) {
    }//end method.

    public static void addCoreFunctions(Environment aEnvironment) {
        aEnvironment.iBodiedOperators.setOperator(MathPiperPrinter.KMaxPrecedence, "While");
        aEnvironment.iBodiedOperators.setOperator(MathPiperPrinter.KMaxPrecedence, "Rule");
        aEnvironment.iBodiedOperators.setOperator(MathPiperPrinter.KMaxPrecedence, "MacroRule");
        aEnvironment.iBodiedOperators.setOperator(MathPiperPrinter.KMaxPrecedence, "RulePattern");
        aEnvironment.iBodiedOperators.setOperator(MathPiperPrinter.KMaxPrecedence, "MacroRulePattern");
        aEnvironment.iBodiedOperators.setOperator(MathPiperPrinter.KMaxPrecedence, "FromFile");
        aEnvironment.iBodiedOperators.setOperator(MathPiperPrinter.KMaxPrecedence, "FromString");
        aEnvironment.iBodiedOperators.setOperator(MathPiperPrinter.KMaxPrecedence, "ToFile");
        aEnvironment.iBodiedOperators.setOperator(MathPiperPrinter.KMaxPrecedence, "ToString");
        aEnvironment.iBodiedOperators.setOperator(MathPiperPrinter.KMaxPrecedence, "ToStdout");
        aEnvironment.iBodiedOperators.setOperator(MathPiperPrinter.KMaxPrecedence, "TraceRule");
        aEnvironment.iBodiedOperators.setOperator(MathPiperPrinter.KMaxPrecedence, "Subst");
        aEnvironment.iBodiedOperators.setOperator(MathPiperPrinter.KMaxPrecedence, "LocalSymbols");
        aEnvironment.iBodiedOperators.setOperator(MathPiperPrinter.KMaxPrecedence, "BackQuote");
        aEnvironment.iPrefixOperators.setOperator(0, "`");
        aEnvironment.iPrefixOperators.setOperator(0, "@");
        aEnvironment.iPrefixOperators.setOperator(0, "_");
        aEnvironment.iInfixOperators.setOperator(0, "_");

        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Hold(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "Hold");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Eval(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Eval");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Write(), 1, BuiltinFunctionEvaluator.Variable | BuiltinFunctionEvaluator.Function),
                "Write");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new WriteString(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "WriteString");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new FullForm(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "FullForm");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new DefaultDirectory(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "DefaultDirectory");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new FromFile(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "FromFile");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new FromString(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "FromString");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Read(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Read");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new ReadToken(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "ReadToken");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new ToFile(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "ToFile");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new ToString(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "ToString");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new ToStdout(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "ToStdout");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Load(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Load");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Set(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "Set");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new MacroSet(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "MacroSet");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Clear(), 1, BuiltinFunctionEvaluator.Variable | BuiltinFunctionEvaluator.Macro),
                "Clear");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Clear(), 1, BuiltinFunctionEvaluator.Variable | BuiltinFunctionEvaluator.Function),
                "MacroClear");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Local(), 1, BuiltinFunctionEvaluator.Variable | BuiltinFunctionEvaluator.Macro),
                "Local");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Local(), 1, BuiltinFunctionEvaluator.Variable | BuiltinFunctionEvaluator.Function),
                "MacroLocal");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new First(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "First");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Nth(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "MathNth");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Rest(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Rest");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new DestructiveReverse(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "DestructiveReverse");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Length(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Length");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new org.mathpiper.builtin.functions.core.List(), 1, BuiltinFunctionEvaluator.Variable | BuiltinFunctionEvaluator.Macro),
                "List");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new UnList(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "UnList");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Listify(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Listify");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Concatenate(), 1, BuiltinFunctionEvaluator.Variable | BuiltinFunctionEvaluator.Function),
                "Concat");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new ConcatenateStrings(), 1, BuiltinFunctionEvaluator.Variable | BuiltinFunctionEvaluator.Function),
                "ConcatStrings");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Delete(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Delete");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new DestructiveDelete(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "DestructiveDelete");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Insert(), 3, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Insert");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new DestructiveInsert(), 3, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "DestructiveInsert");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Replace(), 3, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Replace");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new DestructiveReplace(), 3, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "DestructiveReplace");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Atom(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Atom");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Stringify(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "String");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new ExpressionToString(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "ExpressionToString");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new CharString(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "CharString");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new FlatCopy(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "FlatCopy");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Prog(), 1, BuiltinFunctionEvaluator.Variable | BuiltinFunctionEvaluator.Macro),
                "Prog");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new While(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "While");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new If(), 2, BuiltinFunctionEvaluator.Variable | BuiltinFunctionEvaluator.Macro),
                "If");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Check(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "Check");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new TrapError(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "TrapError");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new GetCoreError(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "GetCoreError");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Prefix(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Prefix");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Infix(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Infix");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Postfix(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Postfix");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Bodied(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Bodied");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Rulebase(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "RuleBase");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new MacroRulebase(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "MacroRuleBase");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new RulebaseListed(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "RuleBaseListed");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new MacroRulebaseListed(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "MacroRuleBaseListed");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new DefMacroRulebase(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "DefMacroRuleBase");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new DefMacroRulebaseListed(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "DefMacroRuleBaseListed");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new HoldArg(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "HoldArg");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Rule(), 5, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "Rule");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new MacroRule(), 5, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "MacroRule");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new UnFence(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "UnFence");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Retract(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Retract");
       /* aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Not(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "NotN");*/
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Not(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Not"); //Alias.
        /*aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new And(), 1, BuiltinFunctionEvaluator.Variable | BuiltinFunctionEvaluator.Macro),
                "AndN");*/
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new And(), 1, BuiltinFunctionEvaluator.Variable | BuiltinFunctionEvaluator.Macro),
                "And"); //Alias.
        /*aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Or(), 1, BuiltinFunctionEvaluator.Variable | BuiltinFunctionEvaluator.Macro),
                "OrN");*/
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Or(), 1, BuiltinFunctionEvaluator.Variable | BuiltinFunctionEvaluator.Macro),
                "Or"); //Alias.
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Equals(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Equals");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Equals(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "="); //Alias.
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new LessThan(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "LessThan");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new GreaterThan(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "GreaterThan");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new IsFunction(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "IsFunction");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new IsAtom(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "IsAtom");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new IsNumber(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "IsNumber");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new IsInteger(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "IsInteger");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new IsList(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "IsList");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new IsString(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "IsString");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new IsBound(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "IsBound");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Multiply(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "MultiplyN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Add(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "AddN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Subtract(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "SubtractN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Divide(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "DivideN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new BuiltinPrecisionSet(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "BuiltinPrecisionSet");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new GetExactBits(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "GetExactBitsN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new SetExactBits(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "SetExactBitsN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new BitCount(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "MathBitCount");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new MathSign(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "MathSign");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new MathIsSmall(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "MathIsSmall");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new MathNegate(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "MathNegate");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Floor(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "FloorN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Ceil(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "CeilN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Abs(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "AbsN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Mod(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "ModN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Div(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "DivN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new BitsToDigits(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "BitsToDigits");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new DigitsToBits(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "DigitsToBits");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Gcd(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "GcdN");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new SystemCall(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "SystemCall");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new FastArcSin(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "FastArcSin");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new FastLog(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "FastLog");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new FastPower(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "FastPower");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new ShiftLeft(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "ShiftLeft");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new ShiftRight(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "ShiftRight");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new FromBase(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "FromBase");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new ToBase(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "ToBase");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new MaxEvalDepth(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "MaxEvalDepth");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new DefLoad(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "DefLoad");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Use(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Use");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new RightAssociative(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "RightAssociative");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new LeftPrecedence(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "LeftPrecedence");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new RightPrecedence(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "RightPrecedence");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new IsBodied(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "IsBodied");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new IsInfix(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "IsInfix");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new IsPrefix(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "IsPrefix");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new IsPostfix(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "IsPostfix");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new OpPrecedence(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "OpPrecedence");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new OpLeftPrecedence(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "OpLeftPrecedence");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new OpRightPrecedence(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "OpRightPrecedence");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new BuiltinPrecisionGet(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "BuiltinPrecisionGet");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new BitAnd(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "BitAnd");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new BitOr(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "BitOr");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new BitXor(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "BitXor");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Secure(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "Secure");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new FindFile(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "FindFile");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new FindFunction(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "FindFunction");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new IsGeneric(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "IsGeneric");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new GenericTypeName(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "GenericTypeName");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new ArrayCreate(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "ArrayCreate");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new ArraySize(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "ArraySize");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new ArrayGet(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "ArrayGet");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new ArraySet(), 3, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "ArraySet");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new CustomEval(), 4, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "CustomEval");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new CustomEvalExpression(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "CustomEval'Expression");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new CustomEvalResult(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "CustomEval'Result");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new CustomEvalLocals(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "CustomEval'Locals");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new CustomEvalStop(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "CustomEval'Stop");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new TraceRule(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "TraceRule");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new TraceStack(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "TraceStack");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new LispRead(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "LispRead");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new LispReadListed(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "LispReadListed");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new org.mathpiper.builtin.functions.core.Type(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Type");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new StringMidGet(), 3, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "StringMidGet");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new StringMidSet(), 3, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "StringMidSet");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new PatternCreate(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Pattern'Create");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new PatternMatches(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Pattern'Matches");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new RulebaseDefined(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "RuleBaseDefined");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new DefLoadFunction(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "DefLoadFunction");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new RulebaseArgList(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "RuleBaseArgList");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new NewRulePattern(), 5, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "RulePattern");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new MacroNewRulePattern(), 5, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "MacroRulePattern");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Subst(), 3, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Subst");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new LocalSymbols(), 1, BuiltinFunctionEvaluator.Variable | BuiltinFunctionEvaluator.Macro),
                "LocalSymbols");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new FastIsPrime(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "FastIsPrime");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Fac(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "MathFac");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new ApplyPure(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "ApplyPure");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new PrettyReaderSet(), 1, BuiltinFunctionEvaluator.Variable | BuiltinFunctionEvaluator.Function),
                "PrettyReader'Set");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new PrettyPrinterSet(), 1, BuiltinFunctionEvaluator.Variable | BuiltinFunctionEvaluator.Function),
                "PrettyPrinter'Set");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new PrettyPrinterGet(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "PrettyPrinter'Get");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new PrettyReaderGet(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "PrettyReader'Get");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new GarbageCollect(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "GarbageCollect");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new SetGlobalLazyVariable(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "SetGlobalLazyVariable");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new PatchLoad(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "PatchLoad");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new PatchString(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "PatchString");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new ExtraInfoSet(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "ExtraInfoSet");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new ExtraInfoGet(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "ExtraInfo'Get");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new DefaultTokenizer(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "DefaultTokenizer");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new CommonLispTokenizer(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "CommonLispTokenizer");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new XmlTokenizer(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "XmlTokenizer");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new XmlExplodeTag(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "XmlExplodeTag");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new BuiltinAssoc(), 2, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Builtin'Assoc");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new CurrentFile(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "CurrentFile");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new CurrentLine(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "CurrentLine");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new BackQuote(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "`");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new DumpBigNumberDebugInfo(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "MathDebugInfo");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new InDebugMode(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "InDebugMode");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new DebugFile(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "DebugFile");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new DebugLine(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "DebugLine");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new org.mathpiper.builtin.functions.core.Version(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Version");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new Exit(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Exit");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new ExitRequested(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "IsExitRequested");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new HistorySize(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "HistorySize");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new StackSize(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "StaSiz");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new IsPromptShown(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "IsPromptShown");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new AskUser(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "AskUser");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new TellUser(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "TellUser");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new org.mathpiper.builtin.functions.core.GetTime(aEnvironment), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Macro),
                "GetTime");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new FileSize(), 1, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "FileSize");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new org.mathpiper.builtin.functions.core.CurrentTime(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "CurrentTime");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new org.mathpiper.builtin.functions.core.Break(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Break");
        aEnvironment.getBuiltinFunctions().setAssociation(
                new BuiltinFunctionEvaluator(new org.mathpiper.builtin.functions.core.Continue(), 0, BuiltinFunctionEvaluator.Fixed | BuiltinFunctionEvaluator.Function),
                "Continue");


    }//end method.
}//end class.

