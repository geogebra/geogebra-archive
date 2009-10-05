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

import org.mathpiper.lisp.printers.MathPiperPrinter;

import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsTraverser;
import org.mathpiper.lisp.cons.AtomCons;
import org.mathpiper.lisp.tokenizers.MathPiperTokenizer;
import org.mathpiper.io.MathPiperInputStream;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.cons.SublistCons;
import org.mathpiper.lisp.InfixOperator;
import org.mathpiper.lisp.collections.OperatorMap;

public class MathPiperParser extends Parser
{

    public OperatorMap iPrefixOperators;
    public OperatorMap iInfixOperators;
    public OperatorMap iPostfixOperators;
    public OperatorMap iBodiedOperators;
    
    boolean iError;
    boolean iEndOfFile;
    String iLookAhead;
    public ConsPointer iSExpressionResult = new ConsPointer();

    public MathPiperParser(MathPiperTokenizer aTokenizer,
            MathPiperInputStream aInput,
            Environment aEnvironment,
            OperatorMap aPrefixOperators,
            OperatorMap aInfixOperators,
            OperatorMap aPostfixOperators,
            OperatorMap aBodiedOperators)
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

    public void parse(Environment aEnvironment,ConsPointer aResult) throws Exception
    {
        parse(aEnvironment);
        aResult.setCons(iSExpressionResult.getCons());
    }

    public void parse(Environment aEnvironment) throws Exception
    {
        readToken();
        if (iEndOfFile)
        {
            iSExpressionResult.setCons(iEnvironment.iEndOfFileAtom.copy( aEnvironment, true));
            return;
        }

        readExpression(aEnvironment,MathPiperPrinter.KMaxPrecedence);  // least precedence

        if (iLookAhead != iEnvironment.iEndStatementAtom.car())
        {
            fail();
        }
        if (iError)
        {
            while (iLookAhead.length() > 0 && iLookAhead != iEnvironment.iEndStatementAtom.car())
            {
                readToken();
            }
        }

        if (iError)
        {
            iSExpressionResult.setCons(null);
        }
        LispError.check(!iError, LispError.INVALID_EXPRESSION);
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

    void readExpression(Environment aEnvironment,int depth) throws Exception
    {
        readAtom(aEnvironment);

        for (;;)
        {
            //Handle special case: a[b]. a is matched with lowest precedence!!
            if (iLookAhead == iEnvironment.iProgOpenAtom.car())
            {
                // Match opening bracket
                matchToken(iLookAhead);
                // Read "index" argument
                readExpression(aEnvironment,MathPiperPrinter.KMaxPrecedence);
                // Match closing bracket
                if (iLookAhead != iEnvironment.iProgCloseAtom.car())
                {
                    LispError.raiseError("Expecting a ] close bracket for program block, but got " + iLookAhead + " instead.");
                    return;
                }
                matchToken(iLookAhead);
                // Build into Ntn(...)
                String theOperator = (String) iEnvironment.iNthAtom.car();
                insertAtom(theOperator);
                combine(aEnvironment,2);
            } else
            {
                InfixOperator op = (InfixOperator) iInfixOperators.lookUp(iLookAhead);
                if (op == null)
                {
                    //printf("op [%s]\n",iLookAhead.String());
                    if(iLookAhead.equals(""))
                    {

                       LispError.raiseError("Expression must end with a semi-colon (;)");
                        return;
                    }
                    if (MathPiperTokenizer.isSymbolic(iLookAhead.charAt(0)))
                    {
                        int origlen = iLookAhead.length();
                        int len = origlen;
                        //printf("IsSymbolic, len=%d\n",len);

                        while (len > 1)
                        {
                            len--;
                            String lookUp =
                                    (String) iEnvironment.getTokenHash().lookUp(iLookAhead.substring(0, len));

                            //printf("trunc %s\n",lookUp.String());
                            op = (InfixOperator) iInfixOperators.lookUp(lookUp);
                            //if (op) printf("FOUND\n");
                            if (op != null)
                            {
                                String toLookUp = iLookAhead.substring(len, origlen);
                                String lookUpRight =
                                       (String) iEnvironment.getTokenHash().lookUp(toLookUp);

                                //printf("right: %s (%d)\n",lookUpRight.String(),origlen-len);

                                if (iPrefixOperators.lookUp(lookUpRight) != null)
                                {
                                    //printf("ACCEPT %s\n",lookUp.String());
                                    iLookAhead = lookUp;
                                    MathPiperInputStream input = iInput;
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
                getOtherSide(aEnvironment,2, upper);
            }
        }
    }

    void readAtom(Environment aEnvironment) throws Exception
    {
        InfixOperator op;
        // parse prefix operators
        op = (InfixOperator) iPrefixOperators.lookUp(iLookAhead);
        if (op != null)
        {
            String theOperator = iLookAhead;
            matchToken(iLookAhead);
            {
                readExpression(aEnvironment,op.iPrecedence);
                insertAtom(theOperator);
                combine(aEnvironment,1);
            }
        } // Else parse brackets
        else if (iLookAhead == iEnvironment.iBracketOpenAtom.car())
        {
            matchToken(iLookAhead);
            readExpression(aEnvironment,MathPiperPrinter.KMaxPrecedence);  // least precedence
            matchToken( (String) iEnvironment.iBracketCloseAtom.car());
        } //parse lists
        else if (iLookAhead == iEnvironment.iListOpenAtom.car())
        {
            int nrargs = 0;
            matchToken(iLookAhead);
            while (iLookAhead != iEnvironment.iListCloseAtom.car())
            {
                readExpression(aEnvironment,MathPiperPrinter.KMaxPrecedence);  // least precedence
                nrargs++;

                if (iLookAhead == iEnvironment.iCommaAtom.car())
                {
                    matchToken(iLookAhead);
                } else if (iLookAhead != iEnvironment.iListCloseAtom.car())
                {
                    LispError.raiseError("Expecting a } close bracket for a list, but got " + iLookAhead + " instead.");
                    return;
                }
            }
            matchToken(iLookAhead);
            String theOperator = (String) iEnvironment.iListAtom.car();
            insertAtom(theOperator);
            combine(aEnvironment, nrargs);

        } // parse prog bodies
        else if (iLookAhead == iEnvironment.iProgOpenAtom.car())
        {
            int nrargs = 0;

            matchToken(iLookAhead);
            while (iLookAhead != iEnvironment.iProgCloseAtom.car())
            {
                readExpression(aEnvironment,MathPiperPrinter.KMaxPrecedence);  // least precedence
                nrargs++;

                if (iLookAhead == iEnvironment.iEndStatementAtom.car())
                {
                    matchToken(iLookAhead);
                } else
                {
                    LispError.raiseError("Expecting ; end of statement in program block, but got " + iLookAhead + " instead.");
                    return;
                }
            }
            matchToken(iLookAhead);
            String theOperator = (String) iEnvironment.iProgAtom.car();
            insertAtom(theOperator);

            combine(aEnvironment, nrargs);
        } // Else we have an atom.
        else
        {
            String theOperator = iLookAhead;
            matchToken(iLookAhead);

            int nrargs = -1;
            if (iLookAhead == iEnvironment.iBracketOpenAtom.car())
            {
                nrargs = 0;
                matchToken(iLookAhead);
                while (iLookAhead != iEnvironment.iBracketCloseAtom.car())
                {
                    readExpression(aEnvironment,MathPiperPrinter.KMaxPrecedence);  // least precedence
                    nrargs++;

                    if (iLookAhead == iEnvironment.iCommaAtom.car())
                    {
                        matchToken(iLookAhead);
                    } else if (iLookAhead != iEnvironment.iBracketCloseAtom.car())
                    {
                        LispError.raiseError("Expecting ) closing bracket for sub-expression, but got " + iLookAhead + " instead.");
                        return;
                    }
                }
                matchToken(iLookAhead);

                op = (InfixOperator) iBodiedOperators.lookUp(theOperator);
                if (op != null)
                {
                    readExpression(aEnvironment,op.iPrecedence); // MathPiperPrinter.KMaxPrecedence
                    nrargs++;
                }
            }
            insertAtom(theOperator);
            if (nrargs >= 0)
            {
                combine(aEnvironment, nrargs);
            }
        }

        // parse postfix operators

        while ((op = (InfixOperator) iPostfixOperators.lookUp(iLookAhead)) != null)
        {
            insertAtom(iLookAhead);
            matchToken(iLookAhead);
            combine(aEnvironment,1);
        }
    }

    void getOtherSide(Environment aEnvironment,int aNrArgsToCombine, int depth) throws Exception
    {
        String theOperator = iLookAhead;
        matchToken(iLookAhead);
        readExpression(aEnvironment, depth);
        insertAtom(theOperator);
        combine(aEnvironment, aNrArgsToCombine);
    }

    void combine(Environment aEnvironment,int aNrArgsToCombine) throws Exception
    {
        ConsPointer subList = new ConsPointer();
        subList.setCons(SublistCons.getInstance(aEnvironment,iSExpressionResult.getCons()));
        ConsTraverser consTraverser = new ConsTraverser(iSExpressionResult);
        int i;
        for (i = 0; i < aNrArgsToCombine; i++)
        {
            if (consTraverser.getCons() == null)
            {
                fail();
                return;
            }
            consTraverser.goNext();
        }
        if (consTraverser.getCons() == null)
        {
            fail();
            return;
        }
        subList.cdr().setCons(consTraverser.cdr().getCons());
        consTraverser.cdr().setCons(null);

        Utility.reverseList(((ConsPointer) subList.car()).cdr(),
                ((ConsPointer) subList.car()).cdr());
        iSExpressionResult.setCons(subList.getCons());
    }

    void insertAtom(String aString) throws Exception
    {
        ConsPointer ptr = new ConsPointer();
        ptr.setCons(AtomCons.getInstance(iEnvironment, aString));
        ptr.cdr().setCons(iSExpressionResult.getCons());
        iSExpressionResult.setCons(ptr.getCons());
    }

    void fail() throws Exception // called when parsing fails, raising an exception
    {
        iError = true;
        if (iLookAhead != null)
        {
            LispError.raiseError("Error parsing expression, near token " + iLookAhead + ".");
        }
        LispError.raiseError("Error parsing expression.");
    }
};
