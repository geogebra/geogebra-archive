/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mathpiper.builtin.functions;

import org.mathpiper.builtin.BuiltinFunctionInitialize;
import org.mathpiper.lisp.Environment;

/**
 *
 *  
 */
	public class MacroRuleBaseListed extends BuiltinFunctionInitialize
	{
		public void eval(Environment aEnvironment,int aStackTop) throws Exception
		{
			org.mathpiper.lisp.UtilityFunctions.internalRuleBase(aEnvironment, aStackTop, true);
		}
	}
