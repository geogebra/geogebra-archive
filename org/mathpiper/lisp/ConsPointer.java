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
 * Provides a smart pointer type to CONS
 *  that can be inserted into linked lists. They do the actual
 *  reference counting, and consequent destruction of the object if
 *  nothing points to it. ConsPointer is used in Cons as a pointer
 *  to the next object, and in diverse parts of the built-in internal
 *  functions to hold temporary values.
 */
public class ConsPointer
{

    Cons iCons;

    public ConsPointer()
    {
        iCons = null;
    }
    
    public ConsPointer(Cons aCons)
    {
        iCons = aCons;
    }

   public void setCons(Cons aNext)
    {
        iCons = aNext;
    }

    public Cons getCons()
    {
        return iCons;
    }

    public void goNext()
    {
        iCons = iCons.iCdr.iCons;
    }
    
    public String toString()
    {
        return iCons.toString();
    }
    
}
