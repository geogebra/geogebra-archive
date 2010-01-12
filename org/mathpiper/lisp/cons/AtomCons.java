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

public class AtomCons extends Cons
{

    private String iCar;

    ConsPointer iCdr = new ConsPointer();

    private AtomCons(Environment aEnvironment,String aString) throws Exception
    {
        super(aEnvironment);
        iCar = aString;
    }

    public static Cons getInstance(Environment aEnvironment, String aString) throws Exception
    {
        Cons self = null;
        if (Utility.isNumber(aString, true))  // check if aString is a number (int or float)
        {
            /// construct a number from a decimal string representation (also create a number object)
            self = new NumberCons(aEnvironment, aString, aEnvironment.getPrecision());
        } else
        {
            self = new AtomCons(aEnvironment,(String)aEnvironment.getTokenHash().lookUp(aString));
        }
        
        LispError.check(self != null, LispError.NOT_ENOUGH_MEMORY);
        
        return self;
    }
    
    public Object car()
    {
        return iCar;
    }

    
        /*public String toString()
        {
            return car();
        }*/

    public Cons copy( Environment aEnvironment, boolean aRecursed) throws Exception 
    {
        Cons atomCons = new AtomCons(aEnvironment, iCar);

        atomCons.setMetadataMap(this.getMetadataMap());
        
        return atomCons;
    }



    public ConsPointer cdr() {
        return iCdr;
    }

    public String toString()
    {
        return iCar;
    }//end method.

    public int type()
    {
        return Utility.ATOM;
    }//end method.
   

}//end class.
