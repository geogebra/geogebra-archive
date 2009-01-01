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
 */

//}}}
// :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:folding=explicit:collapseFolds=0:
package org.mathpiper.lisp.tokenizers;

import org.mathpiper.lisp.TokenHash;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.tokenizers.MathPiperTokenizer;
import org.mathpiper.io.InputStream;

public class XmlTokenizer
			extends MathPiperTokenizer
{

	/// NextToken returns a string representing the next token,
	/// or an empty list.
	public String nextToken(InputStream aInput, TokenHash aHashTable)
	throws Exception
	{

		char c;
		int firstpos = 0;

		if (aInput.endOfStream())

			return (String) aHashTable.lookUp(aInput.startPtr().substring(firstpos, aInput.position()));

		//skipping spaces
		while (IsSpace(aInput.peek()))
			aInput.next();

		firstpos = aInput.position();
		c = aInput.next();

		if (c == '<')
		{

			while (c != '>')
			{
				c = aInput.next();
				LispError.check(!aInput.endOfStream(), LispError.KLispErrCommentToEndOfFile);
			}
		}
		else
		{

			while (aInput.peek() != '<' && !aInput.endOfStream())
			{
				c = aInput.next();
			}
		}

		return (String) aHashTable.lookUp(aInput.startPtr().substring(firstpos, aInput.position()));
	}

	private static boolean IsSpace(int c)
	{

		switch (c)
		{

		case 0x20:
		case 0x0D:
		case 0x0A:
		case 0x09:
			return true;

		default:
			return false;
		}
	}
}
