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


public class SubList extends Cons
{
	ConsPointer iSubList = new ConsPointer();
	
	public static SubList getInstance(Cons aSubList)
	{
		return new SubList(aSubList);
	}
        
        public Object first()
        {
            return iSubList;
        }
        
        
	public ConsPointer getSubList()
	{
		return iSubList;
	}
        
        
	public String string()
	{
		return null;
	}
        
        public String toString()
        {
            return iSubList.toString();
        }
        
        
	public Cons copy(boolean aRecursed) throws Exception
	{
		//TODO recursed copy needs to be implemented still
		LispError.lispAssert(aRecursed == false);
		Cons copied = new SubList(iSubList.getCons());
		return copied;
	}
        
        
	public Cons setExtraInfo(ConsPointer aData)
	{
		//TODO FIXME
		/*
		    Cons* result = NEW LispAnnotatedObject<SubList>(this);
		    result->SetExtraInfo(aData);
		    return result;
		*/
		return null;
	}
        
        
	SubList(Cons aSubList)
	{
		iSubList.setCons(aSubList);
	}
	
}
