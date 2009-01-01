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

import org.mathpiper.builtin.BigNumber;
import org.mathpiper.*;


/**
 * 
 *  
 */
public class Number extends Cons
{
        /* Note: Since Number is a LispAtom, shouldn't it extend LispAtom instead of Cons? tk
        */
    
	/// number object; NULL if not yet converted from string
	BigNumber iNumber;
	/// string representation in decimal; NULL if not yet converted from BigNumber
	String iString;

	/// constructors:
	/// construct from another Number
	public Number(BigNumber aNumber,String aString)
	{
		iString = aString;
		iNumber = aNumber;
	}

	/// construct from a BigNumber; the string representation will be absent
	public Number(BigNumber aNumber)
	{
		iString = null;
		iNumber =aNumber;
	}

	/// construct from a decimal string representation (also create a number object) and use aBasePrecision decimal digits
	public Number(String aString, int aBasePrecision)
	{
		iString = aString;
		iNumber = null;  // purge whatever it was
		// create a new BigNumber object out of iString, set its precision in digits
		//TODO FIXME enable this in the end    Number(aBasePrecision);
	}

	public Cons copy(boolean aRecursed)
	{
		return new Number(iNumber, iString);
	}
        
        public Object first()
        {
            return iNumber;
        }

	/// return a string representation in decimal with maximum decimal precision allowed by the inherent accuracy of the number
	public String string() throws Exception
	{
		if (iString == null)
		{
			LispError.lispAssert(iNumber != null);  // either the string is null or the number but not both
			iString = iNumber.ToString(0/*TODO FIXME*/,10);
			// export the current number to string and store it as Number::iString
		}
		return iString;
	}
        
        public String toString() 
        {
            String stringRepresentation = null;
            try
            {
                stringRepresentation = string();
                
            }
            catch(Exception e)
            {
                e.printStackTrace();  //Todo:fixme.
            }
            return stringRepresentation;
            
        }

	/// give access to the BigNumber object; if necessary, will create a BigNumber object out of the stored string, at given precision (in decimal?)
	public BigNumber number(int aPrecision) throws Exception
	{
		if (iNumber == null)
		{  // create and store a BigNumber out of string
			LispError.lispAssert(iString != null);
			String str;
			str = iString;
			// aBasePrecision is in digits, not in bits, ok
			iNumber = new BigNumber(str, aPrecision, 10/*TODO FIXME BASE10*/);
		}

		// check if the BigNumber object has enough precision, if not, extend it
		// (applies only to floats). Note that iNumber->GetPrecision() might be < 0

		else if (!iNumber.IsInt() && iNumber.GetPrecision() < aPrecision)
		{
			if (iString != null)
			{// have string representation, can extend precision
				iNumber.SetTo(iString,aPrecision, 10);
			}
			else
			{
				// do not have string representation, cannot extend precision!
			}
		}

		return iNumber;
	}

	/// annotate
	public Cons setExtraInfo(ConsPointer aData)
	{
		/*TODO FIXME
		Cons* result = NEW LispAnnotatedObject<Number>(this);
		result->SetExtraInfo(aData);
		return result;
		*/
		return null;
	}

}
