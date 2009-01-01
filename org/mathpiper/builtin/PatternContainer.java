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

//import org.mathpiper.parametermatchers.PatternContainer;
import org.mathpiper.lisp.ConsPointer;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.ArgList;


/**
 * Allows a org.mathpiper.parametermatchers.Pattern to be placed into a org.mathpiper.lisp.BuiltinObject.
 * 
 */
public class PatternContainer extends BuiltinContainer
{
	protected org.mathpiper.parametermatchers.Pattern iPatternMatcher;
	
	public PatternContainer(org.mathpiper.parametermatchers.Pattern aPatternMatcher)
	{
		iPatternMatcher = aPatternMatcher;
	}

	public boolean matches(Environment  aEnvironment, ConsPointer aArguments) throws Exception
	{
		LispError.lispAssert(iPatternMatcher != null);
		boolean result;
		result = iPatternMatcher.matches(aEnvironment, aArguments);
		return result;
	}
	
	public boolean matches(Environment  aEnvironment, ConsPointer[] aArguments) throws Exception
	{
		LispError.lispAssert(iPatternMatcher != null);
		boolean result;
		result = iPatternMatcher.matches(aEnvironment, aArguments);
		return result;
	}
	
	//From BuiltinContainer
	public String send(ArgList aArgList)
	{
		return null;
	}
	
	public String typeName()
	{
		return "\"Pattern\"";
	}

     public Object getJavaObject()
    {
        return null;
    }

}


