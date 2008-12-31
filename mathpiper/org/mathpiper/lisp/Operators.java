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


public class Operators extends AssociatedHash // <InfixOperator>
{
	
	public void SetOperator(int aPrecedence,String aString)
	{
		InfixOperator op = new InfixOperator(aPrecedence);
		setAssociation(op, aString);
	}
	
	public void SetRightAssociative(String aString) throws Exception
	{
		InfixOperator op = (InfixOperator)lookUp(aString);
		LispError.check(op != null,LispError.KLispErrNotAnInFixOperator);
		op.SetRightAssociative();
	}
	
	public void SetLeftPrecedence(String aString,int aPrecedence) throws Exception
	{
		InfixOperator op = (InfixOperator)lookUp(aString);
		LispError.check(op != null,LispError.KLispErrNotAnInFixOperator);
		op.SetLeftPrecedence(aPrecedence);
	}
	
	public void SetRightPrecedence(String aString,int aPrecedence) throws Exception
	{
		InfixOperator op = (InfixOperator)lookUp(aString);
		LispError.check(op != null,LispError.KLispErrNotAnInFixOperator);
		op.SetRightPrecedence(aPrecedence);
	}
	
}
