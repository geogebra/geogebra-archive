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

package org.mathpiper.lisp.printers;

import org.mathpiper.lisp.*;
import org.mathpiper.io.OutputStream;


public class Printer
{
	public void print(ConsPointer aExpression, OutputStream aOutput, Environment aEnvironment) throws Exception
	{
		printExpression(aExpression, aOutput, aEnvironment,0);
	}
	public void rememberLastChar(char aChar)
	{
	}

	void printExpression(ConsPointer aExpression, OutputStream aOutput,
	                     Environment aEnvironment,int aDepth /* =0 */) throws Exception
	{
		ConsPointer iter = new ConsPointer();
		iter.setCons(aExpression.getCons());
		int item = 0;
		while (iter.getCons() != null)
		{
			// if String not null pointer: print string
			String string = iter.getCons().string();

			if (string != null)
			{
				aOutput.write(string);
				aOutput.putChar(' ');
			}
			// else print "(", print sublist, and print ")"
			else if (iter.getCons().subList() != null)
			{
				if (item != 0)
				{
					indent(aOutput,aDepth+1);
				}
				aOutput.write("(");
				printExpression((iter.getCons().subList()),aOutput, aEnvironment,aDepth+1);
				aOutput.write(")");
				item=0;
			}
			else
			{
				aOutput.write("[BuiltinObject]");
			}
			iter = (iter.getCons().rest());
			item++;
		} // print rest element
	}

	void indent(OutputStream aOutput, int aDepth) throws Exception
	{
		aOutput.write("\n");
		int i;
		for (i=aDepth;i>0;i--)
		{
			aOutput.write("  ");
		}
	}
};


