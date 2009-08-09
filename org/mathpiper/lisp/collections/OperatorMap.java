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

package org.mathpiper.lisp.collections;

import org.mathpiper.lisp.*;


public class OperatorMap extends Map // <InfixOperator>
{
	
	public void setOperator(int aPrecedence,String aString)
	{
		InfixOperator op = new InfixOperator(aPrecedence);
		setAssociation(op, aString);
	}
	
	public void setRightAssociative(String aString) throws Exception
	{
		InfixOperator op = (InfixOperator)lookUp(aString);
		LispError.check(op != null,LispError.NOT_AN_INFIX_OPERATOR);
		op.setRightAssociative();
	}
	
	public void setLeftPrecedence(String aString,int aPrecedence) throws Exception
	{
		InfixOperator op = (InfixOperator)lookUp(aString);
		LispError.check(op != null,LispError.NOT_AN_INFIX_OPERATOR);
		op.setLeftPrecedence(aPrecedence);
	}
	
	public void setRightPrecedence(String aString,int aPrecedence) throws Exception
	{
		InfixOperator op = (InfixOperator)lookUp(aString);
		LispError.check(op != null,LispError.NOT_AN_INFIX_OPERATOR);
		op.setRightPrecedence(aPrecedence);
	}
	
}
