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

package org.mathpiper.parametermatchers;

import org.mathpiper.lisp.UtilityFunctions;
import org.mathpiper.lisp.ConsPointer;
import org.mathpiper.lisp.Environment;


/// Class for matching against a pattern variable.
public class Variable extends Parameter
{
	/// Index of variable in MathPiperPatternPredicateBase.iVariables.
	protected int iVarIndex;

	/// Not used.
	protected String iString;

	public Variable(int aVarIndex)
	{
		iVarIndex = aVarIndex;
	}

	/// Matches an expression against the pattern variable.
	/// \param aEnvironment the underlying Lisp environment.
	/// \param aExpression the expression to test.
	/// \param arguments (input/output) actual values of the pattern
	/// variables for \a aExpression.
	///
	/// If entry #iVarIndex in \a arguments is still empty, the
	/// pattern matches and \a aExpression is stored in this
	/// entry. Otherwise, the pattern only matches if the entry equals
	/// \a aExpression.
	public boolean argumentMatches(Environment  aEnvironment,
	                               ConsPointer  aExpression,
	                               ConsPointer[]  arguments) throws Exception
	{
		// this should not be necessary
		//    if (arguments[iVarIndex] == null)
		//    {
		//      arguments[iVarIndex] = new ConsPointer();
		//    }
		if (arguments[iVarIndex].getCons() == null)
		{
			arguments[iVarIndex].setCons(aExpression.getCons());
			//        LogPrintf("Set var %d\n",iVarIndex);
			return true;
		}
		else
		{
			if (UtilityFunctions.internalEquals(aEnvironment, aExpression, arguments[iVarIndex]))
			{
				//            LogPrintf("Matched var %d\n",iVarIndex);
				return true;
			}
			return false;
		}
		//    return false;
	}

};
