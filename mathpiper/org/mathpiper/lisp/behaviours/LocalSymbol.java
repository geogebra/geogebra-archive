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

import org.mathpiper.lisp.ConsPointer;
import org.mathpiper.lisp.Atom;
import org.mathpiper.lisp.Environment;

/** subst behaviour for changing the local variables to have unique
 * names.
 */
public class LocalSymbol implements SubstBase
{
	Environment iEnvironment;
	String[] iOriginalNames;
	String[] iNewNames;
	int iNrNames;
	
	public LocalSymbol(Environment aEnvironment,
	                            String[] aOriginalNames,
	                            String[] aNewNames, int aNrNames)
	{
		iEnvironment = aEnvironment;
		iOriginalNames = aOriginalNames;
		iNewNames = aNewNames;
		iNrNames = aNrNames;
	}
	public boolean matches(ConsPointer aResult, ConsPointer aElement) throws Exception
	{
		String name = aElement.getCons().string();
		if (name == null)
			return false;

		int i;
		for (i=0;i<iNrNames;i++)
		{
			if (name == iOriginalNames[i])
			{
				aResult.setCons(Atom.getInstance(iEnvironment,iNewNames[i]));
				return true;
			}
		}
		return false;
	}

};
