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

package org.mathpiper.lisp.stacks;

import org.mathpiper.lisp.*;
import org.mathpiper.lisp.cons.ConsPointerArray;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.cons.Cons;

/** 
 * Implements a stack of pointers to CONS that can be used to pass
 * arguments to functions, and receive results back.
 */
public class ArgumentStack
{

    ConsPointerArray iArgumentStack;
    int iStackTopIndex;

    //TODO appropriate constructor?
    public ArgumentStack(int aStackSize)
    {
        iArgumentStack = new ConsPointerArray(aStackSize, null);
        iStackTopIndex = 0;
    //printf("STACKSIZE %d\n",aStackSize);
    }

    public int getStackTopIndex()
    {
        return iStackTopIndex;
    }

    public void raiseStackOverflowError() throws Exception
    {
        LispError.raiseError("Argument stack reached maximum. Please extend argument stack with --stack argument on the command line.");
    }

    public void pushArgumentOnStack(Cons aCons) throws Exception
    {
        if (iStackTopIndex >= iArgumentStack.size())
        {
            raiseStackOverflowError();
        }
        iArgumentStack.setElement(iStackTopIndex, aCons);
        iStackTopIndex++;
    }

    public void pushNulls(int aNr) throws Exception
    {
        if (iStackTopIndex + aNr > iArgumentStack.size())
        {
            raiseStackOverflowError();
        }
        iStackTopIndex += aNr;
    }

    public ConsPointer getElement(int aPos) throws Exception
    {
        LispError.lispAssert(aPos >= 0 && aPos < iStackTopIndex);
        return iArgumentStack.getElement(aPos);
    }

    public void popTo(int aTop) throws Exception
    {
        LispError.lispAssert(aTop <= iStackTopIndex);
        while (iStackTopIndex > aTop)
        {
            iStackTopIndex--;
            iArgumentStack.setElement(iStackTopIndex, null);
        }
    }
    
    public void dump() throws Exception
    {
        for(int x=0; x <= iStackTopIndex; x++)
        {
            //try
            //{
                 ConsPointer consPointer = getElement(x);
                  Cons cons = consPointer.getCons();
            //}
            //catch(Exception e)
            //{
              //  e.printStackTrace();
            //}
           
            //System.out.println()
        }
    }//end method.


    public ConsPointer[]  getElements(int quantity) throws IndexOutOfBoundsException
    {
        int last = iStackTopIndex;
        int first = last - quantity;
        return iArgumentStack.getElements(first, last);
    }//end method.
    
    
    
}//end class.
