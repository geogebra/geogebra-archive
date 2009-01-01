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

import org.mathpiper.lisp.ConsPointer;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.UtilityFunctions;

/**
 * A rule with a predicate (the rule matches if the predicate evaluates to True.).
 */
class BranchRule extends BranchRuleBase
{

    public BranchRule(int aPrecedence, ConsPointer aPredicate, ConsPointer aBody)
    {
        iPrecedence = aPrecedence;
        iPredicate.setCons(aPredicate.getCons());
        iBody.setCons(aBody.getCons());
    }

    /**
     *  Return true if the rule matches.
     * 
     * @param aEnvironment
     * @param aArguments
     * @return
     * @throws java.lang.Exception
     */

    // iPredicate is evaluated in \a Environment. If the result
    /// IsTrue(), this function returns true
    public boolean matches(Environment aEnvironment, ConsPointer[] aArguments) throws Exception
    {
        ConsPointer pred = new ConsPointer();
        aEnvironment.iEvaluator.evaluate(aEnvironment, pred, iPredicate);
        return UtilityFunctions.isTrue(aEnvironment, pred);
    }

    /// Access #iPrecedence.
    public int precedence()
    {
        return iPrecedence;
    }

    /// Access #iBody.
    public ConsPointer body()
    {
        return iBody;
    }

    protected BranchRule()
    {
    }
    protected int iPrecedence;
    protected ConsPointer iBody = new ConsPointer();
    protected ConsPointer iPredicate = new ConsPointer();
}

