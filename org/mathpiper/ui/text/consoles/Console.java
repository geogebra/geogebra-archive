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
package org.mathpiper.ui.text.consoles;

//import org.mathpiper.lisp.UtilityFunctions;

import org.mathpiper.*;
import org.mathpiper.exceptions.EvaluationException;
import java.io.*;
import org.mathpiper.interpreters.EvaluationResponse;
import org.mathpiper.interpreters.Interpreter;
import org.mathpiper.interpreters.Interpreters;


/**
 * Provides a command line console which can be used to interact with a mathpiper instance.
 * 
 */
public class Console
{
	Interpreter interpreter;
                
	public Console()
	{
		//MathPiper needs an output stream to send "side effect" output to.
		//StandardFileOutputStream stdoutput = new StandardFileOutputStream(System.out);
		interpreter = Interpreters.getSynchronousInterpreter();
	}
        
        void addDirectory(String directory)
        {
            interpreter.addScriptsDirectory(directory);
        }
        
        String readLine(InputStream aStream)

	{
		StringBuffer line = new StringBuffer();
		try
		{
			int c = aStream.read();
			while (c != '\n')
			{
				line.append((char)c);
				c = aStream.read();
			}
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
		return line.toString();
	}
        
        String evaluate(String input)
        {
            //return (String) interpreter.evaluate(input);
            EvaluationResponse response = interpreter.evaluate(input);
            String responseString = "Result> " + response.getResult() +"\n";

			
			if(!response.getSideEffects().equalsIgnoreCase(""))
			{
				responseString = responseString + "Side Effects>\n" + response.getSideEffects() + "\n";
			}
			
			if(!response.getExceptionMessage().equalsIgnoreCase(""))
			{
				responseString = responseString + response.getExceptionMessage() + "\n" ;
			}
                        
                        
            return responseString;
        }

    


    /**
     * The normal entry point for running mathpiper from a command line.  It processes command line arguments,
     * sets mathpiper's standard output to System.out, then enters a REPL (Read, Evaluate, Print Loop).  Currently,
     * the console only supports the --rootdir and --archive command line options.
     *
     * @param argv
     */
    public static void main(String[] argv)
    {
        Console console = new Console();
        String defaultDirectory = null;
        String archive = null;
        
        
        
        int i = 0;
        while (i < argv.length)
        {
            if (argv[i].equals("--rootdir"))
            {
                i++;
                defaultDirectory = argv[i];
            }
            if (argv[i].equals("--archive"))
            {
                i++;
                archive = argv[i];
            } else
            {
                break;
            }
            i++;
        }
        int scriptsToRun = i;


        //Change the default directory. tk.
        if (defaultDirectory != null)
        {
            console.addDirectory(defaultDirectory );
        }
        

        System.out.println("\nMathPiper version '" + Version.version + "'.");

        System.out.println("See http://mathrider.org for more information and documentation on MathPiper.");

        System.out.println("\nTo exit MathPiper, enter \"Exit()\" or \"exit\" or \"quit\" or Ctrl-c.\n");
        /*TODO fixme
        System.out.println("Type ?? for help. Or type ?function for help on a function.\n");
        System.out.println("Type 'restart' to restart MathPiper.\n");
         */
        System.out.println("To see example commands, keep typing Example()\n");

        //piper.Evaluate("BubbleSort(N(PSolve(x^3-3*x^2+2*x,x)), \"<\");");

        //System.out.println("MathPiper in Java");
        boolean quitting = false;
        while (!quitting)
        {
            System.out.print("In> ");
            String input = console.readLine(System.in);
            input = input.trim();


              String responseString = console.evaluate(input);

            System.out.println(responseString);

            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit"))
            {

                quitting = true;
            }
        }
    }
}

