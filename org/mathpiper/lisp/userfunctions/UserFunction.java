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



/**
 * Provides the base user function API.
 */
public abstract class UserFunction extends EvalFuncBase
{
/// Abstract class providing the basic user function API.
/// Instances of this class are associated to the name of the function
/// via an associated hash table. When obtained, they can be used to
/// evaluate the function with some arguments.
    
	boolean iFenced;
	public static boolean iTraced = false;
	
        /**
         * 
         */
        public UserFunction()
	{
		iFenced = true;
		//iTraced = false;
	}
	public abstract void evaluate(ConsPointer aResult,Environment aEnvironment, ConsPointer aArguments) throws Exception;
	public abstract void holdArgument(String aVariable);
	public abstract void declareRule(int aPrecedence, ConsPointer aPredicate, ConsPointer aBody) throws Exception;
	public abstract void declareRule(int aPrecedence, ConsPointer aBody) throws Exception;
	public abstract void declarePattern(int aPrecedence, ConsPointer aPredicate, ConsPointer aBody) throws Exception;
	public abstract ConsPointer argList();

	public void unFence()
	{
		iFenced = false;
	}
	
	public boolean fenced()
	{
		return iFenced;
	}

	public static void traceOn()
	{
		iTraced = true;
	}
	
	public static void traceOff()
	{
		iTraced = false;
	}
	
	public static boolean isTraced()
	{
		return iTraced;
	}

};
