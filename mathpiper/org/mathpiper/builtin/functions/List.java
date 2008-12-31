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

package org.mathpiper.builtin.functions;

import org.mathpiper.builtin.BuiltinFunctionInitialize;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.ConsTraverser;
import org.mathpiper.lisp.ConsPointer;
import org.mathpiper.lisp.SubList;

/**
 *
 *  
 */
	public class List extends BuiltinFunctionInitialize
	{
		public void eval(Environment aEnvironment,int aStackTop) throws Exception
		{
			ConsPointer all = new ConsPointer();
			all.setCons(aEnvironment.iListAtom.copy(false));
			ConsTraverser tail = new ConsTraverser(all);
			tail.goNext();
			ConsTraverser iter = new ConsTraverser(argumentPointer(aEnvironment, aStackTop, 1).getCons().subList());
			iter.goNext();
			while (iter.getCons() != null)
			{
				ConsPointer evaluated = new ConsPointer();
				aEnvironment.iEvaluator.evaluate(aEnvironment,evaluated,iter.ptr());
				tail.ptr().setCons(evaluated.getCons());
				tail.goNext();
				iter.goNext();
			}
			result(aEnvironment, aStackTop).setCons(SubList.getInstance(all.getCons()));
		}
	}
