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
package org.mathpiper.lisp.printers;


import java.util.ArrayList;
import java.util.List;
import org.mathpiper.builtin.BuiltinContainer;
import org.mathpiper.io.MathPiperOutputStream;
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsTraverser;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.tokenizers.MathPiperTokenizer;
import org.mathpiper.lisp.InfixOperator;
import org.mathpiper.lisp.collections.OperatorMap;
import org.mathpiper.lisp.cons.Cons;


public class MathPiperPrinter extends LispPrinter {

    StringBuilder spaces = new StringBuilder();
    public static int KMaxPrecedence = 60000;
    OperatorMap iPrefixOperators;
    OperatorMap iInfixOperators;
    OperatorMap iPostfixOperators;
    OperatorMap iBodiedOperators;
    char iPrevLastChar;
    Environment iCurrentEnvironment;

    private List<Cons> visitedLists = new ArrayList<Cons>();


    public MathPiperPrinter(OperatorMap aPrefixOperators,
            OperatorMap aInfixOperators,
            OperatorMap aPostfixOperators,
            OperatorMap aBodiedOperators) {
        iPrefixOperators = aPrefixOperators;
        iInfixOperators = aInfixOperators;
        iPostfixOperators = aPostfixOperators;
        iBodiedOperators = aBodiedOperators;
        iPrevLastChar = 0;
    }




    public void print(ConsPointer aExpression, MathPiperOutputStream aOutput, Environment aEnvironment) throws Exception {
        iCurrentEnvironment = aEnvironment;


        Print(aExpression, aOutput, KMaxPrecedence);

        visitedLists.clear();
    }


    public void rememberLastChar(char aChar) {
        iPrevLastChar = aChar;
    }


    void Print(ConsPointer aExpression, MathPiperOutputStream aOutput, int iPrecedence) throws Exception {

        LispError.lispAssert(aExpression.getCons() != null);

        String string;
        if (aExpression.car() instanceof String) {
            string = (String) aExpression.car();
            boolean bracket = false;
            if (iPrecedence < KMaxPrecedence &&
                    string.charAt(0) == '-' &&
                    (MathPiperTokenizer.isDigit(string.charAt(1)) || string.charAt(1) == '.')) {
                bracket = true;
            }
            if (bracket) {
                WriteToken(aOutput, "(");
            }
            WriteToken(aOutput, string);
            if (bracket) {
                WriteToken(aOutput, ")");
            }
            return;
        }

        if (aExpression.car() instanceof BuiltinContainer) {
            //TODO display genericclass
            WriteToken(aOutput, ((BuiltinContainer) aExpression.car()).typeName());
            return;
        }

        ConsPointer subList = (ConsPointer) aExpression.car();

        LispError.check(subList != null, LispError.UNPRINTABLE_TOKEN);

        if (subList.getCons() == null) {
            WriteToken(aOutput, "( )");
        } else {
            int length = Utility.listLength(subList);
            string = (String) subList.car();
            InfixOperator prefix = (InfixOperator) iPrefixOperators.lookUp(string);
            InfixOperator infix = (InfixOperator) iInfixOperators.lookUp(string);
            InfixOperator postfix = (InfixOperator) iPostfixOperators.lookUp(string);
            InfixOperator bodied = (InfixOperator) iBodiedOperators.lookUp(string);
            InfixOperator op = null;

            if (length != 2) {
                prefix = null;
                postfix = null;
            }
            if (length != 3) {
                infix = null;
            }
            if (prefix != null) {
                op = prefix;
            }
            if (postfix != null) {
                op = postfix;
            }
            if (infix != null) {
                op = infix;
            }

            if (op != null) {
                ConsPointer left = null;
                ConsPointer right = null;

                if (prefix != null) {
                    right = subList.cdr();
                } else if (infix != null) {
                    left = subList.cdr();
                    right = subList.cdr().cdr();
                } else if (postfix != null) {
                    left = subList.cdr();
                }

                if (iPrecedence < op.iPrecedence) {
                    WriteToken(aOutput, "(");
                } else {
                    //Vladimir?    aOutput.write(" ");
                }
                if (left != null) {
                    Print(left, aOutput, op.iLeftPrecedence);
                }
                WriteToken(aOutput, string);
                if (right != null) {
                    Print(right, aOutput, op.iRightPrecedence);
                }
                if (iPrecedence < op.iPrecedence) {
                    WriteToken(aOutput, ")");
                }
            } else {
                ConsTraverser consTraverser = new ConsTraverser(subList.cdr());
                if (string == iCurrentEnvironment.iListAtom.car()) {

                    Cons atomCons = (Cons) subList.getCons();
                    if (visitedLists.contains(atomCons)) {
                            WriteToken(aOutput, "{CYCLE_LIST}");
                            return;

                    } else {

                        visitedLists.add(atomCons);

                        WriteToken(aOutput, "{");

                        while (consTraverser.getCons() != null) {
                            Print(consTraverser.getPointer(), aOutput, KMaxPrecedence);
                            consTraverser.goNext();
                            if (consTraverser.getCons() != null) {
                                WriteToken(aOutput, ",");
                            }
                        }//end while.

                        WriteToken(aOutput, "}");

                    }//end else.
                } else if (string == iCurrentEnvironment.iProgAtom.car()) // Program block brackets.
                {
                    WriteToken(aOutput, "[");
                    aOutput.write("\n");
                    spaces.append("    ");

                    while (consTraverser.getCons() != null) {
                        aOutput.write(spaces.toString());
                        Print(consTraverser.getPointer(), aOutput, KMaxPrecedence);
                        consTraverser.goNext();
                        WriteToken(aOutput, ";");
                        aOutput.write("\n");
                    }

                    WriteToken(aOutput, "]");
                    aOutput.write("\n");
                    spaces.delete(0, 4);
                } else if (string == iCurrentEnvironment.iNthAtom.car()) {
                    Print(consTraverser.getPointer(), aOutput, 0);
                    consTraverser.goNext();
                    WriteToken(aOutput, "[");
                    Print(consTraverser.getPointer(), aOutput, KMaxPrecedence);
                    WriteToken(aOutput, "]");
                } else {
                    boolean bracket = false;
                    if (bodied != null) {
                        //printf("%d > %d\n",iPrecedence, bodied.iPrecedence);
                        if (iPrecedence < bodied.iPrecedence) {
                            bracket = true;
                        }
                    }
                    if (bracket) {
                        WriteToken(aOutput, "(");
                    }
                    if (string != null) {
                        WriteToken(aOutput, string);
                    } else {
                        Print(subList, aOutput, 0);
                    }
                    WriteToken(aOutput, "(");

                    ConsTraverser counter = new ConsTraverser(consTraverser.getPointer());
                    int nr = 0;

                    while (counter.getCons() != null) {
                        counter.goNext();
                        nr++;
                    }

                    if (bodied != null) {
                        nr--;
                    }
                    while (nr-- != 0) {
                        Print(consTraverser.getPointer(), aOutput, KMaxPrecedence);

                        consTraverser.goNext();
                        if (nr != 0) {
                            WriteToken(aOutput, ",");
                        }
                    }
                    WriteToken(aOutput, ")");
                    if (consTraverser.getCons() != null) {
                        Print(consTraverser.getPointer(), aOutput, bodied.iPrecedence);
                    }

                    if (bracket) {
                        WriteToken(aOutput, ")");
                    }
                }
            }
        }//end sublist if.
    }


    void WriteToken(MathPiperOutputStream aOutput, String aString) throws Exception {
        /*if (MathPiperTokenizer.isAlNum(iPrevLastChar) && (MathPiperTokenizer.isAlNum(aString.charAt(0)) || aString.charAt(0)=='_'))
        {
        aOutput.write(" ");
        }
        else if (MathPiperTokenizer.isSymbolic(iPrevLastChar) && MathPiperTokenizer.isSymbolic(aString.charAt(0)))
        {
        aOutput.write(" ");
        }*/
        aOutput.write(aString);
        rememberLastChar(aString.charAt(aString.length() - 1));
    }


}
