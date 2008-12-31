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
package org.mathpiper.lisp.userfunctions;

import org.mathpiper.builtin.BuiltinContainer;
import org.mathpiper.builtin.PatternContainer;
import org.mathpiper.lisp.ConsPointer;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;

/**
 * A rule which matches if the corresponding {@link PatternContainer} matches.
 */
class BranchPattern extends BranchRuleBase
{
    /**
    /// Constructor.
     * 
     * @param aPrecedence precedence of the rule
     * @param aPredicate generic object of type PatternContainer
     * @param aBody body of the rule
     */
    
    public BranchPattern(int aPrecedence, ConsPointer aPredicate, ConsPointer aBody) throws Exception
    {
        iPatternClass = null;
        iPrecedence = aPrecedence;
        iPredicate.setCons(aPredicate.getCons());

        BuiltinContainer gen = aPredicate.getCons().generic();
        LispError.check(gen != null, LispError.KLispErrInvalidArg);
        LispError.check(gen.typeName().equals("\"Pattern\""), LispError.KLispErrInvalidArg);

        iPatternClass = (PatternContainer) gen;
        iBody.setCons(aBody.getCons());
    }

    /// Return true if the corresponding pattern matches.
    public boolean matches(Environment aEnvironment, ConsPointer[] aArguments) throws Exception
    {
        return iPatternClass.matches(aEnvironment, aArguments);
    }

    /// Access #iPrecedence
    public int precedence()
    {
        return iPrecedence;
    }

    /// Access #iBody
    public ConsPointer body()
    {
        return iBody;
    }    /// The precedence of this rule.
    protected int iPrecedence;    /// The body of this rule.
    protected ConsPointer iBody = new ConsPointer();    /// Generic object of type \c PatternContainer containing #iPatternClass
    protected ConsPointer iPredicate = new ConsPointer();    /// The pattern that decides whether this rule matches.
    protected PatternContainer iPatternClass;
}
