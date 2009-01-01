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
package org.mathpiper;

import  org.mathpiper.interpreters.Interpreters;
import  org.mathpiper.interpreters.Interpreter;
import  org.mathpiper.interpreters.EvaluationResponse;

/**
	This class demonstrates how to use the synchronous interpreter interface to MathPiper.
	A text based console is also available and can be launched by running the
	org.mathpiper.ui.text.consoles.Console class.
*/
public class HowToUse
{
	
	public static void printResponse(EvaluationResponse response)
	{
		
		System.out.println("Result: " + response.getResult() + "\n");
			
		if(!response.getSideEffects().equalsIgnoreCase(""))
		{
			System.out.println("Side Effects: " + response.getSideEffects() + "\n");
		}
		
		if(response.isExceptionThrown())
		{
			System.out.println("Exception: " + response.getExceptionMessage() + "\n\n");
		}
		
		System.out.println("==========================\n");
		
	}//end method.

	
	
	public static void main(String[] args)
	{
		//Information on how to use the Interpreters class is in its JavaDocs.
		Interpreter interpreter = Interpreters.getSynchronousInterpreter();
		
		//Information on how to use the EvaluationResponse class is in its JavaDocs.
		EvaluationResponse response ;
		
		System.out.println("\n==========================\n");
		
		System.out.println("Result-only evaluation: \"2 + 3;\"\n");
		response = interpreter.evaluate("2 + 3;");
		printResponse(response);
		
		System.out.println("Side effects evaluation: \"Echo(Test);\"\n");
		response = interpreter.evaluate("Echo(Test);");
		printResponse(response);
		
		System.out.println("Evaluation which generates an exception: \"2 + (;\"\n");
		response = interpreter.evaluate("2 + (;");
		printResponse(response);
		
	}//end main.

}//end class.
