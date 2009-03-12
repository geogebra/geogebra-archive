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

package org.mathpiper.parametermatchers;


/// \file
/// Pattern matching code.
///
/// General idea: have a class that can match function parameters
/// to a pattern, check for predicates on the arguments, and return
/// whether there was a match.
///
/// First the pattern is mapped onto the arguments. Then local variables
/// are setCons. Then the predicates are called. If they all return true,
/// Then the pattern matches, and the locals can stay (the body is expected
/// to use these variables).


import org.mathpiper.lisp.Cons;
import org.mathpiper.lisp.UtilityFunctions;
import org.mathpiper.lisp.ConsPointer;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.ConsTraverser;
//import org.mathpiper.lisp.Atom;
import org.mathpiper.lisp.Environment;
//import org.mathpiper.lisp.SubList;
import java.util.*;

/// Class that matches function arguments to a pattern.
/// This class (specifically, the matches() member function) can match
/// function parameters to a pattern, check for predicates on the
/// arguments, and return whether there was a match.

public class Pattern
{
	/// List of parameter matches, one for every parameter.
	protected ArrayList iParamMatchers = new ArrayList(); //CDeletingArrayGrower<Parameter*> iParamMatchers;

	/// List of variables appearing in the pattern.
	protected ArrayList iVariables = new ArrayList(); //CArrayGrower<String>

	/// List of predicates which need to be true for a match.
	protected ArrayList iPredicates = new ArrayList(); //CDeletingArrayGrower<ConsPointer[] >

	/// Constructor.
	/// \param aEnvironment the underlying Lisp environment
	/// \param aPattern Lisp expression containing the pattern
	/// \param aPostPredicate Lisp expression containing the
	/// postpredicate
	///
	/// The function MakePatternMatcher() is called for every argument
	/// in \a aPattern, and the resulting pattern matchers are
	/// collected in #iParamMatchers. Additionally, \a aPostPredicate
	/// is copied, and the copy is added to #iPredicates.
	public Pattern(Environment  aEnvironment,
	                                 ConsPointer  aPattern,
	                                 ConsPointer  aPostPredicate) throws Exception
	{
		ConsTraverser iter = new ConsTraverser(aPattern);

		while (iter.getCons() != null)
		{
			Parameter matcher = makeParamMatcher(aEnvironment,iter.getCons());
			LispError.lispAssert(matcher!=null);
			iParamMatchers.add(matcher);
			iter.goNext();
		}
		ConsPointer  post = new ConsPointer();
		post.setCons(aPostPredicate.getCons());
		iPredicates.add(post);
	}


	/// Try to match the pattern against \a aArguments.
	/// First, every argument in \a aArguments is matched against the
	/// corresponding Parameter in #iParamMatches. If any
	/// match fails, matches() returns false. Otherwise, a temporary
	/// LispLocalFrame is constructed, then setPatternVariables() and
	/// checkPredicates() are called, and then the LispLocalFrame is
	/// immediately deleted. If checkPredicates() returns false, this
	/// function also returns false. Otherwise, setPatternVariables()
	/// is called again, but now in the current LispLocalFrame, and
	/// this function returns true.
	public boolean matches(Environment  aEnvironment, ConsPointer  aArguments) throws Exception
	{
		int i;

		ConsPointer[]  arguments = null;
		if (iVariables.size() > 0)
		{
			arguments = new ConsPointer[iVariables.size()];
			for (i=0;i<iVariables.size();i++)
			{
				arguments[i] = new ConsPointer();
			}

		}
		ConsTraverser iter = new ConsTraverser(aArguments);

		for (i=0;i<iParamMatchers.size();i++)
		{
			if (iter.getCons() == null)
				return false;
			ConsPointer  ptr = iter.ptr();
			if (ptr==null)
				return false;
			if (!((Parameter)iParamMatchers.get(i)).argumentMatches(aEnvironment,ptr,arguments))
			{
				return false;
			}
			iter.goNext();
		}
		if (iter.getCons() != null)
			return false;

		{
			// setCons the local variables.
			aEnvironment.pushLocalFrame(false);
			try
			{
				setPatternVariables(aEnvironment,arguments);

				// do the predicates
				if (!checkPredicates(aEnvironment))
					return false;
			}
			catch (Exception e)
			{
				throw e;
			}
			finally
			{
				aEnvironment.popLocalFrame();
			}
		}

		// setCons the local variables for sure now
		setPatternVariables(aEnvironment,arguments);

		return true;
	}

	/// Try to match the pattern against \a aArguments.
	/// This function does the same as matches(Environment ,ConsPointer ),
	/// but differs in the type of the arguments.
	public boolean matches(Environment  aEnvironment, ConsPointer[]  aArguments) throws Exception
	{
		int i;

		ConsPointer[]  arguments = null;
		if (iVariables.size() > 0)
			arguments = new ConsPointer[iVariables.size()];
		for (i=0;i<iVariables.size();i++)
		{
			arguments[i] = new ConsPointer();
		}

		for (i=0;i<iParamMatchers.size();i++)
		{
			if (!((Parameter)iParamMatchers.get(i)).argumentMatches(aEnvironment,aArguments[i],arguments))
			{
				return false;
			}
		}

		{
			// setCons the local variables.
			aEnvironment.pushLocalFrame(false);
			try
			{
				setPatternVariables(aEnvironment,arguments);

				// do the predicates
				if (!checkPredicates(aEnvironment))
					return false;
			}
			catch (Exception e)
			{
				throw e;
			}
			finally
			{
				aEnvironment.popLocalFrame();
			}
		}

		// setCons the local variables for sure now
		setPatternVariables(aEnvironment,arguments);
		return true;
	}

	/// Construct a pattern matcher out of a Lisp expression.
	/// The result of this function depends on the value of \a aPattern:
	/// - If \a aPattern is a number, the corresponding Number is
	///   constructed and returned.
	/// - If \a aPattern is an atom, the corresponding Atom is
	///   constructed and returned.
	/// - If \a aPattern is a list of the form <tt>( _ var )<tt>,
	///   where \c var is an atom, lookUp() is called on \c var. Then
	///   the correspoding Variable is constructed and returned.
	/// - If \a aPattern is a list of the form <tt>( _ var expr )<tt>,
	///   where \c var is an atom, lookUp() is called on \c var. Then,
	///   \a expr is appended to #iPredicates. Finally, the
	///   correspoding Variable is constructed and returned.
	/// - If \a aPattern is a list of another form, this function
	///   calls itself on any of the entries in this list. The
	///   resulting Parameter objects are collected in a
	///   SubList, which is returned.
	/// - Otherwise, this function returns #null.
	protected Parameter makeParamMatcher(Environment  aEnvironment, Cons aPattern) throws Exception
	{
		if (aPattern == null)
			return null;
		if (aPattern.getNumber(aEnvironment.getPrecision()) != null)
		{
			return new Number(aPattern.getNumber(aEnvironment.getPrecision()));
		}
		// Deal with atoms
		if (aPattern.string() != null)
		{
			return new Atom(aPattern.string());
		}

		// Else it must be a sublist
		if (aPattern.getSubList() != null)
		{
			// See if it is a variable template:
			ConsPointer  sublist = aPattern.getSubList();
			LispError.lispAssert(sublist != null);

			int num = UtilityFunctions.internalListLength(sublist);

			// variable matcher here...
			if (num>1)
			{
				Cons head = sublist.getCons();
				if (head.string() == aEnvironment.getTokenHash().lookUp("_"))
				{
					Cons second = head.rest().getCons();
					if (second.string() != null)
					{
						int index = lookUp(second.string());

						// Make a predicate for the type, if needed
						if (num>2)
						{
							ConsPointer third = new ConsPointer();

							Cons predicate = second.rest().getCons();
							if (predicate.getSubList() != null)
							{
								UtilityFunctions.internalFlatCopy(third, predicate.getSubList());
							}
							else
							{
								third.setCons(second.rest().getCons().copy(false));
							}

							String str = second.string();
							Cons last = third.getCons();
							while (last.rest().getCons() != null)
								last = last.rest().getCons();

							last.rest().setCons(org.mathpiper.lisp.Atom.getInstance(aEnvironment,str));

							ConsPointer pred = new ConsPointer();
							pred.setCons(org.mathpiper.lisp.SubList.getInstance(third.getCons()));

							iPredicates.add(pred);
						}
						return new Variable(index);
					}
				}
			}

			Parameter[] matchers = new Parameter[num];

			int i;
			ConsTraverser iter = new ConsTraverser(sublist);
			for (i=0;i<num;i++)
			{
				matchers[i] = makeParamMatcher(aEnvironment,iter.getCons());
				LispError.lispAssert(matchers[i] != null);
				iter.goNext();
			}
			return new SubList(matchers, num);
		}

		return null;
	}

	/// Look up a variable name in #iVariables
	/// \returns index in #iVariables array where \a aVariable
	/// appears.
	///
	/// If \a aVariable is not in #iVariables, it is added.
	protected int lookUp(String aVariable)
	{
		int i;
		for (i=0;i<iVariables.size();i++)
		{
			if (iVariables.get(i) == aVariable)
			{
				return i;
			}
		}
		iVariables.add(aVariable);
		return iVariables.size()-1;
	}

	/// Set local variables corresponding to the pattern variables.
	/// This function goes through the #iVariables array. A local
	/// variable is made for every entry in the array, and the
	/// corresponding argument is assigned to it.
	protected void setPatternVariables(Environment  aEnvironment, ConsPointer[]  arguments) throws Exception
	{
		int i;
		for (i=0;i<iVariables.size();i++)
		{
			// setCons the variable to the new value
			aEnvironment.newLocal((String)iVariables.get(i),arguments[i].getCons());
		}
	}

	/// check whether all predicates are true.
	/// This function goes through all predicates in #iPredicates, and
	/// evaluates them. It returns #false if at least one
	/// of these results IsFalse(). An error is raised if any result
	/// neither IsTrue() nor IsFalse().
	protected boolean checkPredicates(Environment  aEnvironment) throws Exception
	{
		int i;
		for (i=0;i<iPredicates.size();i++)
		{
			ConsPointer pred = new ConsPointer();
			aEnvironment.iEvaluator.evaluate(aEnvironment, pred, ((ConsPointer)iPredicates.get(i)));
			if (UtilityFunctions.isFalse(aEnvironment, pred))
			{
				return false;
			}


			// If the result is not False, it should be True, else probably something is wrong (the expression returned unevaluated)
			boolean isTrue = UtilityFunctions.isTrue(aEnvironment, pred);
			if (!isTrue)
			{
				//TODO this is probably not the right way to generate an error, should we perhaps do a full throw new MathPiperException here?
				String strout;
				aEnvironment.write("The predicate\n\t");
				strout = UtilityFunctions.printExpression(((ConsPointer)iPredicates.get(i)), aEnvironment, 60);
				aEnvironment.write(strout);
				aEnvironment.write("\nevaluated to\n\t");
				strout = UtilityFunctions.printExpression(pred, aEnvironment, 60);
				aEnvironment.write(strout);
				aEnvironment.write("\n");

				LispError.check(isTrue,LispError.KLispErrNonBooleanPredicateInPattern);
			}
		}
		return true;
	}

}

