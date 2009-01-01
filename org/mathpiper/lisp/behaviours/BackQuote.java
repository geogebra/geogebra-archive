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

package org.mathpiper.lisp.behaviours;

import org.mathpiper.lisp.Cons;
import org.mathpiper.lisp.UtilityFunctions;
import org.mathpiper.lisp.ConsPointer;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.SubList;

/** subst behaviour for backquote mechanism as in LISP.
 * When typing `(...) all occurrences of @a will be
 * replaced with:
 * 1) a evaluated if a is an atom
 * 2) function call with function name replaced by evaluated
 *    head of function if a is a function. For instance, if
 *    a is f(x) and f is g, then f(x) gets replaced by g(x)
 */
public class BackQuote implements SubstBase
{
	Environment iEnvironment;

	public BackQuote(Environment aEnvironment)
	{
		iEnvironment = aEnvironment;
	}
	public boolean matches(ConsPointer aResult, ConsPointer aElement) throws Exception
	{
		if (aElement.getCons().subList() == null) return false;
		Cons ptr = aElement.getCons().subList().getCons();
		if (ptr == null) return false;
		if (ptr.string() == null) return false;

		if (ptr.string().equals("`"))
		{
			aResult.setCons(aElement.getCons());
			return true;
		}

		if (!ptr.string().equals("@"))
			return false;
		ptr = ptr.rest().getCons();
		if (ptr == null)
			return false;
		if (ptr.string() != null)
		{
			ConsPointer cur = new ConsPointer();
			cur.setCons(ptr);
			iEnvironment.iEvaluator.evaluate(iEnvironment, aResult, cur);
			return true;
		}
		else
		{
			ptr = ptr.subList().getCons();
			ConsPointer cur = new ConsPointer();
			cur.setCons(ptr);
			ConsPointer args = new ConsPointer();
			args.setCons(ptr.rest().getCons());
			ConsPointer result = new ConsPointer();
			iEnvironment.iEvaluator.evaluate(iEnvironment, result, cur);
			result.getCons().rest().setCons(args.getCons());
			ConsPointer result2 = new ConsPointer();
			result2.setCons(SubList.getInstance(result.getCons()));
			UtilityFunctions.internalSubstitute(aResult, result2,this);
			return true;
		}
		//      return false;
	}

};
