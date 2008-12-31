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

import org.mathpiper.lisp.Cons;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.ConsPointerArray;
import org.mathpiper.lisp.ArgList;


public class Array extends BuiltinContainer
{
	ConsPointerArray iArray;

	public Array(int aSize,Cons aInitialItem)
	{
		iArray = new ConsPointerArray(aSize,aInitialItem);
	}
	public String send(ArgList aArgList)
	{
		return null;
	}
	public String typeName()
	{
		return "\"Array\"";
	}

	public int size()
	{
		return iArray.size();
	}
	public Cons getElement(int aItem) throws Exception
	{
		LispError.lispAssert(aItem>0 && aItem<=iArray.size());
		return iArray.getElement(aItem-1).getCons();
	}
	public void setElement(int aItem,Cons aObject) throws Exception
	{
		LispError.lispAssert(aItem>0 && aItem<=iArray.size());
		iArray.setElement(aItem-1,aObject);
	}

    public Object getJavaObject()
    {
        return null;
    }

}
