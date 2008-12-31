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
package org.mathpiper.lisp.parsers;

import org.mathpiper.printers.InfixPrinter;

import org.mathpiper.lisp.UtilityFunctions;
import org.mathpiper.lisp.ConsPointer;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.ConsTraverser;
import org.mathpiper.lisp.Atom;
import org.mathpiper.lisp.tokenizers.MathPiperTokenizer;
import org.mathpiper.io.InputStream;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.SubList;
import org.mathpiper.lisp.InfixOperator;
import org.mathpiper.lisp.Operators;

public class MathPiperParser extends Parser
{

    public Operators iPrefixOperators;
    public Operators iInfixOperators;
    public Operators iPostfixOperators;
    public Operators iBodiedOperators;
    
    boolean iError;
    boolean iEndOfFile;
    String iLookAhead;
    public ConsPointer iResult = new ConsPointer();

    public MathPiperParser(MathPiperTokenizer aTokenizer,
            InputStream aInput,
            Environment aEnvironment,
            Operators aPrefixOperators,
            Operators aInfixOperators,
            Operators aPostfixOperators,
            Operators aBodiedOperators)
    {
        super(aTokenizer, aInput, aEnvironment);
        iPrefixOperators = aPrefixOperators;
        iInfixOperators = aInfixOperators;
        iPostfixOperators = aPostfixOperators;
        iBodiedOperators = aBodiedOperators;

        iError = false;
        iEndOfFile = false;
        iLookAhead = null;
    }

    public void parse(ConsPointer aResult) throws Exception
    {
        parse();
        aResult.setCons(iResult.getCons());
    }

    public void parse() throws Exception
    {
        readToken();
        if (iEndOfFile)
        {
            iResult.setCons(iEnvironment.iEndOfFileAtom.copy(true));
            return;
        }

        readExpression(InfixPrinter.KMaxPrecedence);  // least precedence

        if (iLookAhead != iEnvironment.iEndStatementAtom.string())
        {
            fail();
        }
        if (iError)
        {
            while (iLookAhead.length() > 0 && iLookAhead != iEnvironment.iEndStatementAtom.string())
            {
                readToken();
            }
        }

        if (iError)
        {
            iResult.setCons(null);
        }
        LispError.check(!iError, LispError.KLispErrInvalidExpression);
    }

    void readToken() throws Exception
    {
        // Get token.
        iLookAhead = iTokenizer.nextToken(iInput,
                iEnvironment.getTokenHash());
        if (iLookAhead.length() == 0)
        {
            iEndOfFile = true;
        }
    }

    void matchToken(String aToken) throws Exception
    {
        if (aToken != iLookAhead)
        {
            fail();
        }
        readToken();
    }

    void readExpression(int depth) throws Exception
    {
        readAtom();

        for (;;)
        {
            //Handle special case: a[b]. a is matched with lowest precedence!!
            if (iLookAhead == iEnvironment.iProgOpenAtom.string())
            {
                // Match opening bracket
                matchToken(iLookAhead);
                // Read "index" argument
                readExpression(InfixPrinter.KMaxPrecedence);
                // Match closing bracket
                if (iLookAhead != iEnvironment.iProgCloseAtom.string())
                {
                    LispError.raiseError("Expecting a ] close bracket for program block, but got " + iLookAhead + " instead.",iEnvironment);
                    return;
                }
                matchToken(iLookAhead);
                // Build into Ntn(...)
                String theOperator = iEnvironment.iNthAtom.string();
                insertAtom(theOperator);
                combine(2);
            } else
            {
                InfixOperator op = (InfixOperator) iInfixOperators.lookUp(iLookAhead);
                if (op == null)
                {
                    //printf("op [%s]\n",iLookAhead.String());
                    if (MathPiperTokenizer.isSymbolic(iLookAhead.charAt(0)))
                    {
                        int origlen = iLookAhead.length();
                        int len = origlen;
                        //printf("IsSymbolic, len=%d\n",len);

                        while (len > 1)
                        {
                            len--;
                            String lookUp =
                                    iEnvironment.getTokenHash().lookUp(iLookAhead.substring(0, len));

                            //printf("trunc %s\n",lookUp.String());
                            op = (InfixOperator) iInfixOperators.lookUp(lookUp);
                            //if (op) printf("FOUND\n");
                            if (op != null)
                            {
                                String toLookUp = iLookAhead.substring(len, origlen);
                                String lookUpRight =
                                        iEnvironment.getTokenHash().lookUp(toLookUp);

                                //printf("right: %s (%d)\n",lookUpRight.String(),origlen-len);

                                if (iPrefixOperators.lookUp(lookUpRight) != null)
                                {
                                    //printf("ACCEPT %s\n",lookUp.String());
                                    iLookAhead = lookUp;
                                    InputStream input = iInput;
                                    int newPos = input.position() - (origlen - len);
                                    input.setPosition(newPos);
                                    //printf("Pushhback %s\n",&input.startPtr()[input.position()]);
                                    break;
                                } else
                                {
                                    op = null;
                                }
                            }
                        }
                        if (op == null)
                        {
                            return;
                        }
                    } else
                    {
                        return;
                    }




                //              return;
                }
                if (depth < op.iPrecedence)
                {
                    return;
                }
                int upper = op.iPrecedence;
                if (op.iRightAssociative == 0)
                {
                    upper--;
                }
                getOtherSide(2, upper);
            }
        }
    }

    void readAtom() throws Exception
    {
        InfixOperator op;
        // parse prefix operators
        op = (InfixOperator) iPrefixOperators.lookUp(iLookAhead);
        if (op != null)
        {
            String theOperator = iLookAhead;
            matchToken(iLookAhead);
            {
                readExpression(op.iPrecedence);
                insertAtom(theOperator);
                combine(1);
            }
        } // Else parse brackets
        else if (iLookAhead == iEnvironment.iBracketOpenAtom.string())
        {
            matchToken(iLookAhead);
            readExpression(InfixPrinter.KMaxPrecedence);  // least precedence
            matchToken(iEnvironment.iBracketCloseAtom.string());
        } //parse lists
        else if (iLookAhead == iEnvironment.iListOpenAtom.string())
        {
            int nrargs = 0;
            matchToken(iLookAhead);
            while (iLookAhead != iEnvironment.iListCloseAtom.string())
            {
                readExpression(InfixPrinter.KMaxPrecedence);  // least precedence
                nrargs++;

                if (iLookAhead == iEnvironment.iCommaAtom.string())
                {
                    matchToken(iLookAhead);
                } else if (iLookAhead != iEnvironment.iListCloseAtom.string())
                {
                    LispError.raiseError("Expecting a } close bracket for a list, but got " + iLookAhead + " instead.",iEnvironment);
                    return;
                }
            }
            matchToken(iLookAhead);
            String theOperator = iEnvironment.iListAtom.string();
            insertAtom(theOperator);
            combine(nrargs);

        } // parse prog bodies
        else if (iLookAhead == iEnvironment.iProgOpenAtom.string())
        {
            int nrargs = 0;

            matchToken(iLookAhead);
            while (iLookAhead != iEnvironment.iProgCloseAtom.string())
            {
                readExpression(InfixPrinter.KMaxPrecedence);  // least precedence
                nrargs++;

                if (iLookAhead == iEnvironment.iEndStatementAtom.string())
                {
                    matchToken(iLookAhead);
                } else
                {
                    LispError.raiseError("Expecting ; end of statement in program block, but got " + iLookAhead + " instead.",iEnvironment);
                    return;
                }
            }
            matchToken(iLookAhead);
            String theOperator = iEnvironment.iProgAtom.string();
            insertAtom(theOperator);

            combine(nrargs);
        } // Else we have an atom.
        else
        {
            String theOperator = iLookAhead;
            matchToken(iLookAhead);

            int nrargs = -1;
            if (iLookAhead == iEnvironment.iBracketOpenAtom.string())
            {
                nrargs = 0;
                matchToken(iLookAhead);
                while (iLookAhead != iEnvironment.iBracketCloseAtom.string())
                {
                    readExpression(InfixPrinter.KMaxPrecedence);  // least precedence
                    nrargs++;

                    if (iLookAhead == iEnvironment.iCommaAtom.string())
                    {
                        matchToken(iLookAhead);
                    } else if (iLookAhead != iEnvironment.iBracketCloseAtom.string())
                    {
                        LispError.raiseError("Expecting ) closing bracket for sub-expression, but got " + iLookAhead + " instead.",iEnvironment);
                        return;
                    }
                }
                matchToken(iLookAhead);

                op = (InfixOperator) iBodiedOperators.lookUp(theOperator);
                if (op != null)
                {
                    readExpression(op.iPrecedence); // InfixPrinter.KMaxPrecedence
                    nrargs++;
                }
            }
            insertAtom(theOperator);
            if (nrargs >= 0)
            {
                combine(nrargs);
            }
        }

        // parse postfix operators

        while ((op = (InfixOperator) iPostfixOperators.lookUp(iLookAhead)) != null)
        {
            insertAtom(iLookAhead);
            matchToken(iLookAhead);
            combine(1);
        }
    }

    void getOtherSide(int aNrArgsToCombine, int depth) throws Exception
    {
        String theOperator = iLookAhead;
        matchToken(iLookAhead);
        readExpression(depth);
        insertAtom(theOperator);
        combine(aNrArgsToCombine);
    }

    void combine(int aNrArgsToCombine) throws Exception
    {
        ConsPointer subList = new ConsPointer();
        subList.setCons(SubList.getInstance(iResult.getCons()));
        ConsTraverser iter = new ConsTraverser(iResult);
        int i;
        for (i = 0; i < aNrArgsToCombine; i++)
        {
            if (iter.getCons() == null)
            {
                fail();
                return;
            }
            iter.goNext();
        }
        if (iter.getCons() == null)
        {
            fail();
            return;
        }
        subList.getCons().rest().setCons(iter.getCons().rest().getCons());
        iter.getCons().rest().setCons(null);

        UtilityFunctions.internalReverseList(subList.getCons().subList().getCons().rest(),
                subList.getCons().subList().getCons().rest());
        iResult.setCons(subList.getCons());
    }

    void insertAtom(String aString) throws Exception
    {
        ConsPointer ptr = new ConsPointer();
        ptr.setCons(Atom.getInstance(iEnvironment, aString));
        ptr.getCons().rest().setCons(iResult.getCons());
        iResult.setCons(ptr.getCons());
    }

    void fail() throws Exception // called when parsing fails, raising an exception
    {
        iError = true;
        if (iLookAhead != null)
        {
            LispError.raiseError("Error parsing expression, near token " + iLookAhead + ".", iEnvironment);
        }
        LispError.raiseError("Error parsing expression.",iEnvironment);
    }
};
