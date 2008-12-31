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
 * Similar to ConsPointer, but implements an array of pointers to CONS.
 *  
 */
public class ConsPointerArray
{
	int iSize;
	ConsPointer iArray[];
	
	public ConsPointerArray(int aSize,Cons aInitialItem)
	{
		iArray = new ConsPointer[aSize];
		iSize = aSize;
		int i;
		for(i=0;i<aSize;i++)
		{
			iArray[i] = new ConsPointer();
			iArray[i].setCons(aInitialItem);
		}
	}
	
	public int size()
	{
		return iSize;
	}
	
	public ConsPointer getElement(int aItem)
	{
		return iArray[aItem];
	}
	
	public void setElement(int aItem,Cons aCons)
	{
		iArray[aItem].setCons(aCons);
	}

}
