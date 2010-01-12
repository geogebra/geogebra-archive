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

package org.mathpiper.lisp.cons;

import org.mathpiper.lisp.*;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.cons.Cons;

/**
 * Works almost like ConsPointer, but doesn't enforce
 * reference counting, so it should be slightly faster. This one
 * should be used instead of ConsPointer if you are going to traverse
 * a lisp expression in a non-destructive way.
 */
public class ConsTraverser
{
	ConsPointer iPointer;
	
	public ConsTraverser(ConsPointer aPtr)
	{
		iPointer = aPtr;
	}

     public Object car() throws Exception
    {
        return iPointer.car();
    }

    public ConsPointer cdr()
    {
        return iPointer.cdr();
    }
	
	public Cons getCons()
	{
		return iPointer.getCons();
	}
	
	public ConsPointer getPointer()
	{
		return iPointer;
	}
	
	public void goNext() throws Exception
	{
		LispError.check(iPointer.getCons() != null,LispError.NOT_LONG_ENOUGH);
		iPointer = (iPointer.cdr());
	}
	
	public void goSub() throws Exception
	{
		LispError.check(iPointer.getCons() != null,LispError.INVALID_ARGUMENT);
		LispError.check(iPointer.car() instanceof ConsPointer,LispError.NOT_A_LIST);
		iPointer = (ConsPointer) iPointer.car();
	}

};

