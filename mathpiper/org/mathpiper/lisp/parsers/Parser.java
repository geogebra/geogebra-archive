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

package org.mathpiper.lisp.parsers;

import org.mathpiper.lisp.tokenizers.MathPiperTokenizer;
import org.mathpiper.io.InputStream;
import org.mathpiper.lisp.*;


public class Parser
{
	public MathPiperTokenizer iTokenizer;
	public InputStream iInput;
	public Environment iEnvironment;
	public boolean iListed;
	
	public Parser(MathPiperTokenizer aTokenizer, InputStream aInput,
	                  Environment aEnvironment)
	{
		iTokenizer = aTokenizer;
		iInput = aInput;
		iEnvironment = aEnvironment;
		iListed = false;
	}
	
	public void parse(ConsPointer aResult ) throws Exception
	{
		aResult.setCons(null);

		String token;
		// Get token.
		token = iTokenizer.nextToken(iInput,iEnvironment.getTokenHash());
		if (token.length() == 0) //TODO FIXME either token == null or token.length() == 0?
		{
			aResult.setCons(Atom.getInstance(iEnvironment,"EndOfFile"));
			return;
		}
		parseAtom(aResult,token);
	}

	void parseList(ConsPointer aResult) throws Exception
	{
		String token;

		ConsPointer iter = aResult;
		if (iListed)
		{
			aResult.setCons(Atom.getInstance(iEnvironment,"List"));
			iter  = (aResult.getCons().rest()); //TODO FIXME
		}
		for (;;)
		{
			//Get token.
			token = iTokenizer.nextToken(iInput,iEnvironment.getTokenHash());
			// if token is empty string, error!
			LispError.check(token.length() > 0,LispError.KInvalidToken); //TODO FIXME
			// if token is ")" return result.
			if (token == iEnvironment.getTokenHash().lookUp(")"))
			{
				return;
			}
			// else parse simple atom with parse, and append it to the
			// results list.

			parseAtom(iter,token);
			iter = (iter.getCons().rest()); //TODO FIXME
		}
	}

	void parseAtom(ConsPointer aResult,String aToken) throws Exception
	{
		// if token is empty string, return null pointer (no expression)
		if (aToken.length() == 0) //TODO FIXME either token == null or token.length() == 0?
			return;
		// else if token is "(" read in a whole array of objects until ")",
		//   and make a sublist
		if (aToken == iEnvironment.getTokenHash().lookUp("("))
		{
			ConsPointer subList = new ConsPointer();
			parseList(subList);
			aResult.setCons(SubList.getInstance(subList.getCons()));
			return;
		}
		// else make a simple atom, and return it.
		aResult.setCons(Atom.getInstance(iEnvironment,aToken));
	}
	
}

