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


import org.mathpiper.lisp.DefFile;
import org.mathpiper.lisp.*;
import java.util.*;



/**
 * Holds a set of {@link SingleArityBranchingUserFunction} which are associated with one function name.
 * A specific SingleArityBranchingUserFunction can be selected by providing its name.  The
 * name of the file in which the function is defined can also be specified.
 */
public class MultipleArityUserFunction
{

	/// Set of SingleArityBranchingUserFunction's provided by this MultipleArityUserFunction.
	List<SingleArityBranchingUserFunction> iFunctions = new ArrayList();//

	/// File to read for the definition of this function.
	public DefFile iFileToOpen;
    
    public String iFileLocation;

	/// Constructor.
	public MultipleArityUserFunction()
	{
		iFileToOpen = null;
	}

	/// Return user function with given arity.
	public SingleArityBranchingUserFunction getUserFunction(int aArity) throws Exception
	{
		int ruleIndex;
		//Find function body with the right arity
		int numberOfRules=iFunctions.size();
		for (ruleIndex =0; ruleIndex<numberOfRules; ruleIndex++)
		{
			LispError.lispAssert(iFunctions.get(ruleIndex) != null);

			if (((SingleArityBranchingUserFunction)iFunctions.get(ruleIndex)).isArity(aArity))
			{
				return (SingleArityBranchingUserFunction)iFunctions.get(ruleIndex);
			}
		}

		// if function not found, just unaccept!
		// User-defined function not found! Returning null
		return null;
	}

	/// Specify that some argument should be held.
	public void holdArgument(String aVariable) throws Exception
	{
		int ruleIndex ;
		for (ruleIndex =0;ruleIndex <iFunctions.size();ruleIndex ++)
		{
			LispError.lispAssert(iFunctions.get(ruleIndex ) != null);
			((SingleArityBranchingUserFunction)iFunctions.get(ruleIndex )).holdArgument(aVariable);
		}
	}

	/// Add another SingleArityBranchingUserFunction to #iFunctions.
	public  void addRulebaseEntry(SingleArityBranchingUserFunction aNewFunction) throws Exception
	{
		int ruleIndex;
		//Find function body with the right arity
		int numberOfRules =iFunctions.size();
		for (ruleIndex=0; ruleIndex<numberOfRules; ruleIndex++)
		{
			LispError.lispAssert(((SingleArityBranchingUserFunction)iFunctions.get(ruleIndex)) != null);
			LispError.lispAssert(aNewFunction != null);
			LispError.check(!((SingleArityBranchingUserFunction)iFunctions.get(ruleIndex)).isArity(aNewFunction.arity()),LispError.ARITY_ALREADY_DEFINED);
			LispError.check(!aNewFunction.isArity(((SingleArityBranchingUserFunction)iFunctions.get(ruleIndex)).arity()),LispError.ARITY_ALREADY_DEFINED);
		}
		iFunctions.add(aNewFunction);
	}

	/// Delete user function with given arity.  If arity is -1 then delete all functions regardless of arity.
	public  void deleteRulebaseEntry(int aArity) throws Exception
	{
        if(aArity == -1) //Retract all functions regardless of arity.
        {
            iFunctions.clear();
            return;
        }//end if.

		int ruleIndex;
		//Find function body with the right arity
		int numberOfRules =iFunctions.size();
		for (ruleIndex=0; ruleIndex<numberOfRules; ruleIndex++)
		{
			LispError.lispAssert(((SingleArityBranchingUserFunction)iFunctions.get(ruleIndex)) != null);

            if (((SingleArityBranchingUserFunction)iFunctions.get(ruleIndex)).isArity(aArity))
			{
				iFunctions.remove(ruleIndex);
				return;
			}
		}
	}


    public Iterator getFunctions()
    {
        return this.iFunctions.iterator();
    }


}
