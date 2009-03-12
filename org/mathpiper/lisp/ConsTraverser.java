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

/**
 * Works almost like ConsPointer, but doesn't enforce
 * reference counting, so it should be slightly faster. This one
 * should be used instead of ConsPointer if you are going to traverse
 * a lisp expression in a non-destructive way.
 */
public class ConsTraverser
{
	ConsPointer iPtr;
	
	public ConsTraverser(ConsPointer aPtr)
	{
		iPtr = aPtr;
	}
	
	public Cons getCons()
	{
		return iPtr.getCons();
	}
	
	public ConsPointer ptr()
	{
		return iPtr;
	}
	
	public void goNext() throws Exception
	{
		LispError.check(iPtr.getCons() != null,LispError.KLispErrListNotLongEnough);
		iPtr = (iPtr.getCons().rest());
	}
	
	public void goSub() throws Exception
	{
		LispError.check(iPtr.getCons() != null,LispError.KLispErrInvalidArg);
		LispError.check(iPtr.getCons().getSubList() != null,LispError.KLispErrNotList);
		iPtr = iPtr.getCons().getSubList();
	}

};

