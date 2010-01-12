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


import org.mathpiper.io.StringOutput;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.printers.LispPrinter;


public class SublistCons extends Cons {

    ConsPointer iCar = new ConsPointer();
    ConsPointer iCdr = new ConsPointer();


    public static SublistCons getInstance(Environment aEnvironment, Cons aSubList) throws Exception {
        return new SublistCons(aEnvironment, aSubList);
    }


    public Object car() {
        return iCar;
    }


    /*
    public String toString()
    {
    return iCar.toString();
    }*/
    public Cons copy(Environment aEnvironment, boolean aRecursed) throws Exception {
        //TODO recursed copy needs to be implemented still
        LispError.lispAssert(aRecursed == false);

        Cons copied = new SublistCons(aEnvironment, iCar.getCons());

        copied.setMetadataMap(this.getMetadataMap());
        
        return copied;
    }


    SublistCons(Environment aEnvironment, Cons aSubList) throws Exception {
        super(aEnvironment);
        iCar.setCons(aSubList);
    }


    public ConsPointer cdr() {
        return iCdr;
    }//end method.


    public String toString() {
        StringOutput out = new StringOutput();
        LispPrinter printer = new LispPrinter();
        try {
            printer.print(new ConsPointer(this), out, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.toString();
    }//end method.


    public int type() {
        return Utility.SUBLIST;
    }//end method.


}//end class.

