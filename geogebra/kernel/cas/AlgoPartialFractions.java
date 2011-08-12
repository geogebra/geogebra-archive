/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.cas;

import geogebra.cas.GeoGebraCAS;
import geogebra.kernel.CasEvaluableFunction;
import geogebra.kernel.Construction;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.main.Application;

public class AlgoPartialFractions extends AlgoCasBase {
   
	public AlgoPartialFractions(Construction cons,  String label, CasEvaluableFunction f) {
		super(cons, label, f);
	}

	@Override
	public String getClassName() {
		return "AlgoCasPartialFractions";
	}

	@Override
	protected void applyCasCommand() {
		
		// f.getVarString() can return a number in wrong alphabet (need ASCII)
		boolean internationalizeDigits = kernel.internationalizeDigits;
		kernel.internationalizeDigits = false;
		
		// get variable string with tmp prefix, 
		// e.g. "x" becomes "ggbtmpvarx" here
		boolean isUseTempVariablePrefix = kernel.isUseTempVariablePrefix();
		kernel.setUseTempVariablePrefix(true);
		String varStr = f.getVarString();
		kernel.setUseTempVariablePrefix(isUseTempVariablePrefix);

		 kernel.internationalizeDigits = internationalizeDigits;
		
		 sb.setLength(0);
		 sb.append("PartialFractions(%");
		 sb.append(",");
		 sb.append(varStr);		
		 sb.append(")");
		 		
		g.setUsingCasCommand(sb.toString(), f, false);		
	}

}
