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

package org.mathpiper.builtin.functions.core;

import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.cons.ConsPointer;

/**
 *
 *  
 */
public class ExtraInfoSet extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer object = new ConsPointer();
        object.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());

        ConsPointer info = new ConsPointer();
        info.setCons(getArgumentPointer(aEnvironment, aStackTop, 2).getCons());

        getTopOfStackPointer(aEnvironment, aStackTop).setCons(object.getCons().setExtraInfo(info));
    }
}



/*
%mathpiper_docs,name="ExtraInfoSet;ExtraInfoGet",categories="Programmer Functions;Programming"
*A object properties
*CMD ExtraInfoGet/Set --- annotate objects with additional information
*CORE
*CALL
	ExtraInfoSet(expr,tag)
	ExtraInfoGet(expr)

*PARMS

{expr} -- any expression

{tag} -- tag information (any other expression)

*DESC

Sometimes it is useful to be able to add extra tag information to "annotate"
objects or to label them as having certain "properties". The functions
{ExtraInfoSet} and {ExtraInfoGet} enable this.

The function {ExtraInfoSet} returns the tagged expression, leaving
the original expression alone. This means there is a common pitfall:
be sure to assign the returned value to a variable, or the tagged
expression is lost when the temporary object is destroyed.

The original expression is left unmodified, and the tagged expression
returned, in order to keep the atomic objects small. To tag an
object, a new type of object is created from the old object, with
one added property (the tag). The tag can be any expression whatsoever.

The function {ExtraInfoGet(x)} retrieves this tag expression from an object
{x}. If an object has no tag, it looks the same as if it had a tag with value
{False}.

No part of the MathPiper core uses tags in a way that is visible to the outside
world, so for specific purposes a programmer can devise a format to use for tag
information. Association lists (hashes) are a natural fit for this, although it
is not required and a tag can be any object (except the atom {False} because it
is indistinguishable from having no tag information). Using association lists
is highly advised since it is most likely to be the format used by other parts
of the library, and one needs to avoid clashes with other library code.
Typically, an object will either have no tag or a tag which is an associative
list (perhaps empty). A script that uses tagged objects will check whether an
object has a tag and if so, will add or modify certain entries of the
association list, preserving any other tag information.

Note that {FlatCopy} currently does <i>not</i> copy the tag information (see
examples).

*E.G.

	In> a:=2*b
	Out> 2*b;
	In> a:=ExtraInfoSet(a,{{"type","integer"}})
	Out> 2*b;
	In> a
	Out> 2*b;
	In> ExtraInfoGet(a)
	Out> {{"type","integer"}};
	In> ExtraInfoGet(a)["type"]
	Out> "integer";
	In> c:=a
	Out> 2*b;
	In> ExtraInfoGet(c)
	Out> {{"type","integer"}};
	In> c
	Out> 2*b;
	In> d:=FlatCopy(a);
	Out> 2*b;
	In> ExtraInfo(d)
	Out> False;

*SEE Assoc, :=
%/mathpiper_docs
*/
