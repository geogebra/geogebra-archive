package geogebra.gui.util;

import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;
import geogebra.util.Unicode;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingConstants;

/**
 * Symbol table for quick pasting of symbols into text fields. See MyTextField.
 * 
 * @author G Sturr
 *
 */
public class SymbolTable extends SelectionTable {

	
	public final static String [] displayChars = { 	
		"\u2245", // congruent	
		"\u2261",  // equivalent
		"\u2221",  // angle
		"\u2206"  // triangle
	};

	public final static String [] specialChars = { 	
		"\u00b2",  // exponents ^2 
		"\u00b3",  // exponents ^3 
		"\u00b0", // degree	
		"\u03c0", // pi	
		Unicode.EULER_STRING, // e
		"\u221e", // infinity
		ExpressionNode.strVECTORPRODUCT, //  "\u2297", // vector product (circled times)
		"sqrt(x)",
		"cbrt(x)",
		"abs(x)",
		"sgn(x)",
		"ln(x)",
		"lg(x)",
		"ld(x)",
		"sin(x)",
		"cos(x)",
		"tan(x)",
		"asin(x)",
		"acos(x)",
		"atan(x)",
		"sinh(x)",
		"cosh(x)",
		"tanh(x)",
		"asinh(x)",
		"acosh(x)",
		"atanh(x)",
		"floor(x)",
		"ceil(x)",
		"round(x)",
		"gamma(x)",
		"random()",
		ExpressionNode.strEQUAL_BOOLEAN,
		ExpressionNode.strNOT_EQUAL,
		ExpressionNode.strLESS_EQUAL,
		ExpressionNode.strGREATER_EQUAL,
		ExpressionNode.strNOT,
		ExpressionNode.strAND,
		ExpressionNode.strOR, 
		ExpressionNode.strPARALLEL,
		ExpressionNode.strPERPENDICULAR,
		ExpressionNode.strIS_ELEMENT_OF,
		ExpressionNode.strCONTAINS,
		ExpressionNode.strCONTAINS_STRICT,
	};

	
	// spaces either side (for multiply when inserted into the input bar)
	public final static String [] functions = { 	
		" sqrt(x) ",
		" cbrt(x) ",
		" abs(x) ",
		" sgn(x) ",
		" arg(x) ",
		" conjugate(x) ",
		" ln(x) ",
		" lg(x) ",
		" ld(x) ",
		" floor(x) ",
		" sin(x) ",
		" sinh(x) ",
		" cos(x) ",
		" cosh(x) ",
		" tan(x) ",
		" tanh(x) ",
		" asin(x) ",
		" asinh(x) ",
		" acos(x) ",
		" acosh(x) ",
		" atan(x) ",
		" atanh(x) ",
		" atan2(x, y) ", "",
		" sec(x) ",
		" sech(x) ",
		" cosec(x) ",
		" cosech(x) ",
		" cot(x) ",
		" coth(x) ",
		" ceil(x) ",
		" round(x) ",
		" gamma(x) ",
		" random() ",
	};

	
	public final static String [] symbols = { 	
		"\u03c0", // pi	
		Unicode.EULER_STRING, // e
		
		"\u00b2",  // exponents ^2 
		"\u00b3",  // exponents ^3 
		"\u00b0", // degree			
		"\u221e", // infinity
		ExpressionNode.strVECTORPRODUCT, //  "\u2297", // vector product (circled times)
		ExpressionNode.strEQUAL_BOOLEAN,
		ExpressionNode.strNOT_EQUAL,
		ExpressionNode.strLESS_EQUAL,
		ExpressionNode.strGREATER_EQUAL,
		ExpressionNode.strNOT,
		ExpressionNode.strAND,
		ExpressionNode.strOR, 
		ExpressionNode.strPARALLEL,
		ExpressionNode.strPERPENDICULAR,
		ExpressionNode.strIS_ELEMENT_OF,
		ExpressionNode.strCONTAINS,
		ExpressionNode.strCONTAINS_STRICT,
	};

	
	
	
	
	public final static String [] greekLowerCase = {
		"\u03b1", "\u03b2", "\u03b3", "\u03b4", "\u03b5", "\u03b6", "\u03b7", "\u03b8",
		"\u03b9", "\u03ba", "\u03bb", "\u03bc", "\u03bd", "\u03be", "\u03bf", "\u03c0", 
		"\u03c1", "\u03c3", "\u03c4", "\u03c5", "\u03c6", "\u03c7", "\u03c8",
		"\u03c9"
	};

	public final static String [] greekUpperCase = {
		"\u0393", // Gamma
		"\u0394", // Delta
		"\u0398", // Theta
		"\u039b", // Lambda
		"\u039e", // Xi
		"\u03a0", // Pi
		"\u03a3", // Sigma
		"\u03a6", // Phi
		"\u03a8", // Psi
		"\u03a9"  // Omega
	};
	
	
	public final static String [] greek = {
		"\u03b1", "\u03b2", "\u03b3", "\u03b4", "\u03b5", "\u03b6", "\u03b7", "\u03b8",
		"\u03b9", "\u03ba", "\u03bb", "\u03bc", "\u03bd", "\u03be", "\u03bf", "\u03c0", 
		"\u03c1", "\u03c3", "\u03c4", "\u03c5", "\u03c6", "\u03c7", "\u03c8",
		"\u03c9",
	
		"\u0393", // Gamma
		"\u0394", // Delta
		"\u0398", // Theta
		"\u039b", // Lambda
		"\u039e", // Xi
		"\u03a0", // Pi
		"\u03a3", // Sigma
		"\u03a6", // Phi
		"\u03a8", // Psi
		"\u03a9"  // Omega
	};
	
	
	
	public final static String [] tableSymbols = {
		"\u03b1", "\u03b2", "\u03b3", "\u03b4", "\u03b5", "\u03b6", "\u03b7", "\u03b8",
		"\u03b9", "\u03ba", "\u03bb", "\u03bc", "\u03bd", "\u03be", "\u03bf", 
		"\u03c1", "\u03c3", "\u03c4", "\u03c5", "\u03c6", "\u03c7", "\u03c8",
		"\u03c9",
	
		"\u0393", // Gamma
		"\u0394", // Delta
		"\u0398", // Theta
		"\u039b", // Lambda
		"\u039e", // Xi
		"\u03a0", // Pi
		"\u03a3", // Sigma
		"\u03a6", // Phi
		"\u03a8", // Psi
		"\u03a9",  // Omega
		
		
		Unicode.PI_STRING, // pi	
		Unicode.EULER_STRING, // e
		
		"\u00b2",  // exponents ^2 
		"\u00b3",  // exponents ^3 
		"\u00b0", // degree			
		"\u221e", // infinity
		ExpressionNode.strVECTORPRODUCT, //  "\u2297", // vector product (circled times)
		ExpressionNode.strEQUAL_BOOLEAN,
		ExpressionNode.strNOT_EQUAL,
		ExpressionNode.strLESS_EQUAL,
		ExpressionNode.strGREATER_EQUAL,
		ExpressionNode.strNOT,
		ExpressionNode.strAND,
		ExpressionNode.strOR, 
		ExpressionNode.strPARALLEL,
		ExpressionNode.strPERPENDICULAR,
		ExpressionNode.strIS_ELEMENT_OF,
		ExpressionNode.strCONTAINS,
		ExpressionNode.strCONTAINS_STRICT,
			
	};

	private MyTextField inputField;
	
		
	public SymbolTable(Application app, MyTextField inputField) {
		super(app, symbols, -1,6, new Dimension(20,16), SelectionTable.MODE_TEXT);
		setShowGrid(true);
		setHorizontalAlignment(SwingConstants.CENTER);
		setSelectedIndex(1);
		
		this.inputField = inputField;
		
	}

}
