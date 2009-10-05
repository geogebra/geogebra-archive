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
import org.mathpiper.builtin.BuiltinContainer;


public class BuiltinObjectCons extends Cons {

    BuiltinContainer iCarBuiltin;
    ConsPointer iCdr = new ConsPointer();


    public static BuiltinObjectCons getInstance(Environment aEnvironment,BuiltinContainer aClass) throws Exception {
        LispError.lispAssert(aClass != null);
        BuiltinObjectCons self = new BuiltinObjectCons(aEnvironment, aClass);
        LispError.check(self != null, LispError.NOT_ENOUGH_MEMORY);
        return self;
    }


    public Object car() {
        return iCarBuiltin;
    }


    public Cons copy(Environment aEnvironment, boolean aRecursed) throws Exception  {

        Cons copied = new BuiltinObjectCons(aEnvironment, iCarBuiltin);

        copied.setMetadataMap(this.getMetadataMap());

        return copied;
        
    }


    BuiltinObjectCons(Environment aEnvironment, BuiltinContainer aClass) throws Exception  {
        super(aEnvironment);
        iCarBuiltin = aClass;
    }


    public ConsPointer cdr() {
        return iCdr;
    }


    public int type() {
        return Utility.OBJECT;
    }//end method.
};
