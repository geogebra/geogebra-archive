package geogebra.util;

public class Unicode {

	final public static char minus = '\u2212';
	final public static char LESS_EQUAL = '\u2264';
	final public static char GREATER_EQUAL = '\u2265';
	final public static char Infinity = '\u221e';
	final public static String MinusInfinity = "-\u221e";
	final public static char Superscript_Minus = '\u207b';
	final public static char Superscript_0 = '\u2070';
	final public static char Superscript_1 = '\u00b9';
	final public static char Superscript_2 = '\u00b2';
	final public static char Superscript_3 = '\u00b3';
	final public static char Superscript_4 = '\u2074';
	final public static char Superscript_5 = '\u2075';
	final public static char Superscript_6 = '\u2076';
	final public static char Superscript_7 = '\u2077';
	final public static char Superscript_8 = '\u2078';
	final public static char Superscript_9 = '\u2079';
	final public static char RightToLeftMark = '\u200f';
	final public static char LeftToRightMark = '\u200e';
	final public static String superscriptMinusOneBracket = "\u207b\u00b9(";
	final public static char degreeChar = '\u00b0';
	final public static String degree = degreeChar+"";
	public static final Object oneDegree = "1"+degreeChar;
	/** Unicode symbol for e */
	final public static String EULER_STRING = "\u212f"; // "\u0435";
	/** Unicode symbol for pi */
	final public static String PI_STRING = "\u03c0";
	public static final String alphaBetaGamma = "\u03b1\u03b2\u03b3";	
	
	final public static char multiplicationDot =  '\u2219'; // bullet
	final public static String multiplicationDotStr = multiplicationDot+"";
	final public static char FEMININE_ORDINAL_INDICATOR = '\u00aa';
	
	/*
	 * converts an integer to a unicode superscript string (including minus sign)
	 * eg for use as a power
	 * @author Michael
	 */
	final public static String numberToIndex(int i) {

		StringBuilder sb = new StringBuilder();
		 if (i < 0)
		 {
			 sb.append(Superscript_Minus); // superscript minus sign
			 i = -i;
		 }
		 
		 if (i == 0) sb.append(Superscript_0); // zero     					 
		 else while (i>0) {
			 switch (i%10) {
	            case 0: sb.insert(0, Superscript_0); break;
	            case 1: sb.insert(0, Superscript_1); break;
	            case 2: sb.insert(0, Superscript_2); break;
	            case 3: sb.insert(0, Superscript_3); break;
	            case 4: sb.insert(0, Superscript_4); break;
	            case 5: sb.insert(0, Superscript_5); break;
	            case 6: sb.insert(0, Superscript_6); break;
	            case 7: sb.insert(0, Superscript_7); break;
	            case 8: sb.insert(0, Superscript_8); break;
	            case 9: sb.insert(0, Superscript_9); break;
			 
			 }
			 i = i / 10;
		 }
		 
		 return sb.toString();

	}

	final public static boolean isSuperscriptDigit(char c) {
		return (c >= Superscript_0 && c <= Superscript_9) || c == Superscript_1 || c == Superscript_2 || c == Superscript_3;
	}
	
}
