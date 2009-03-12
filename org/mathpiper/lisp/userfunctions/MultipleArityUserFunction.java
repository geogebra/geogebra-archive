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

package org.mathpiper.lisp.userfunctions;


import org.mathpiper.lisp.*;
import java.util.*;



/**
 * Holds a set of {@link SingleArityUserFunction} which are associated with one function name.
 * A specific SingleArityUserFunction can be selected by providing its name.  The
 * name of the file in which the function is defined can also be specified.
 */
public class MultipleArityUserFunction
{

	/// Set of SingleArityUserFunction's provided by this MultipleArityUserFunction.
	ArrayList iFunctions = new ArrayList();//<SingleArityUserFunction*>

	/// File to read for the definition of this function.
	public DefFile iFileToOpen;

	/// Constructor.
	public MultipleArityUserFunction()
	{
		iFileToOpen = null;
	}

	/// Return user function with given arity.
	public UserFunction userFunction(int aArity) throws Exception
	{
		int i;
		//Find function body with the right arity
		int nrc=iFunctions.size();
		for (i=0;i<nrc;i++)
		{
			LispError.lispAssert(iFunctions.get(i) != null);
			if (((SingleArityUserFunction)iFunctions.get(i)).isArity(aArity))
			{
				return (SingleArityUserFunction)iFunctions.get(i);
			}
		}

		// if function not found, just unaccept!
		// User-defined function not found! Returning null
		return null;
	}

	/// Specify that some argument should be held.
	public void holdArgument(String aVariable) throws Exception
	{
		int i;
		for (i=0;i<iFunctions.size();i++)
		{
			LispError.lispAssert(iFunctions.get(i) != null);
			((SingleArityUserFunction)iFunctions.get(i)).holdArgument(aVariable);
		}
	}

	/// Add another SingleArityUserFunction to #iFunctions.
	public  void defineRuleBase(SingleArityUserFunction aNewFunction) throws Exception
	{
		int i;
		//Find function body with the right arity
		int nrc=iFunctions.size();
		for (i=0;i<nrc;i++)
		{
			LispError.lispAssert(((SingleArityUserFunction)iFunctions.get(i)) != null);
			LispError.lispAssert(aNewFunction != null);
			LispError.check(!((SingleArityUserFunction)iFunctions.get(i)).isArity(aNewFunction.arity()),LispError.KLispErrArityAlreadyDefined);
			LispError.check(!aNewFunction.isArity(((SingleArityUserFunction)iFunctions.get(i)).arity()),LispError.KLispErrArityAlreadyDefined);
		}
		iFunctions.add(aNewFunction);
	}

	/// Deletet user function with given arity.
	public  void deleteBase(int aArity) throws Exception
	{
		int i;
		//Find function body with the right arity
		int nrc=iFunctions.size();
		for (i=0;i<nrc;i++)
		{
			LispError.lispAssert(((SingleArityUserFunction)iFunctions.get(i)) != null);
			if (((SingleArityUserFunction)iFunctions.get(i)).isArity(aArity))
			{
				iFunctions.remove(i);
				return;
			}
		}
	}


}
