/* Generated By:JavaCC: Do not edit this line. ParserConstants.java */
package geogebra.kernel.parser;


/** 
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface ParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int EOL = 5;
  /** RegularExpression Id. */
  int ASSIGNMENT = 6;
  /** RegularExpression Id. */
  int CARTESIAN_SPECIAL_SEPERATOR = 7;
  /** RegularExpression Id. */
  int NOT = 8;
  /** RegularExpression Id. */
  int OR = 9;
  /** RegularExpression Id. */
  int AND = 10;
  /** RegularExpression Id. */
  int EQUAL_BOOLEAN = 11;
  /** RegularExpression Id. */
  int NOT_EQUAL = 12;
  /** RegularExpression Id. */
  int LESS = 13;
  /** RegularExpression Id. */
  int GREATER = 14;
  /** RegularExpression Id. */
  int LESS_EQUAL = 15;
  /** RegularExpression Id. */
  int GREATER_EQUAL = 16;
  /** RegularExpression Id. */
  int PARALLEL = 17;
  /** RegularExpression Id. */
  int PERPENDICULAR = 18;
  /** RegularExpression Id. */
  int EQUAL = 19;
  /** RegularExpression Id. */
  int PLUS = 20;
  /** RegularExpression Id. */
  int MINUS = 21;
  /** RegularExpression Id. */
  int MULTIPLY = 22;
  /** RegularExpression Id. */
  int COMPLEXMULTIPLY = 23;
  /** RegularExpression Id. */
  int DIVIDE = 24;
  /** RegularExpression Id. */
  int POWER = 25;
  /** RegularExpression Id. */
  int FACTORIAL = 26;
  /** RegularExpression Id. */
  int UNDEFINED = 27;
  /** RegularExpression Id. */
  int POWER0 = 28;
  /** RegularExpression Id. */
  int POWER1 = 29;
  /** RegularExpression Id. */
  int SQUARED = 30;
  /** RegularExpression Id. */
  int CUBED = 31;
  /** RegularExpression Id. */
  int POWER4 = 32;
  /** RegularExpression Id. */
  int POWER5 = 33;
  /** RegularExpression Id. */
  int POWER6 = 34;
  /** RegularExpression Id. */
  int POWER7 = 35;
  /** RegularExpression Id. */
  int POWER8 = 36;
  /** RegularExpression Id. */
  int POWER9 = 37;
  /** RegularExpression Id. */
  int PI = 38;
  /** RegularExpression Id. */
  int E = 39;
  /** RegularExpression Id. */
  int DEGREE = 40;
  /** RegularExpression Id. */
  int INFINITY = 41;
  /** RegularExpression Id. */
  int RAD = 42;
  /** RegularExpression Id. */
  int DERIVATIVE = 43;
  /** RegularExpression Id. */
  int TRUE = 44;
  /** RegularExpression Id. */
  int FALSE = 45;
  /** RegularExpression Id. */
  int VARX = 46;
  /** RegularExpression Id. */
  int VARY = 47;
  /** RegularExpression Id. */
  int PARAMETRICVAR = 48;
  /** RegularExpression Id. */
  int FLOAT = 49;
  /** RegularExpression Id. */
  int EFLOAT = 50;
  /** RegularExpression Id. */
  int INTEGER = 51;
  /** RegularExpression Id. */
  int DIGIT = 52;
  /** RegularExpression Id. */
  int SPREADSHEET_LABEL = 53;
  /** RegularExpression Id. */
  int LABEL = 54;
  /** RegularExpression Id. */
  int INDEX = 55;
  /** RegularExpression Id. */
  int LETTER = 56;
  /** RegularExpression Id. */
  int X_FUNC = 57;
  /** RegularExpression Id. */
  int Y_FUNC = 58;
  /** RegularExpression Id. */
  int COS_FUNC = 59;
  /** RegularExpression Id. */
  int SIN_FUNC = 60;
  /** RegularExpression Id. */
  int TAN_FUNC = 61;
  /** RegularExpression Id. */
  int ACOS_FUNC = 62;
  /** RegularExpression Id. */
  int ASIN_FUNC = 63;
  /** RegularExpression Id. */
  int ATAN_FUNC = 64;
  /** RegularExpression Id. */
  int COSH_FUNC = 65;
  /** RegularExpression Id. */
  int SINH_FUNC = 66;
  /** RegularExpression Id. */
  int TANH_FUNC = 67;
  /** RegularExpression Id. */
  int ACOSH_FUNC = 68;
  /** RegularExpression Id. */
  int ASINH_FUNC = 69;
  /** RegularExpression Id. */
  int ATANH_FUNC = 70;
  /** RegularExpression Id. */
  int EXP_FUNC = 71;
  /** RegularExpression Id. */
  int LOG_FUNC = 72;
  /** RegularExpression Id. */
  int LD_FUNC = 73;
  /** RegularExpression Id. */
  int LG_FUNC = 74;
  /** RegularExpression Id. */
  int SQRT_FUNC = 75;
  /** RegularExpression Id. */
  int CBRT_FUNC = 76;
  /** RegularExpression Id. */
  int ABS_FUNC = 77;
  /** RegularExpression Id. */
  int SGN_FUNC = 78;
  /** RegularExpression Id. */
  int FLOOR_FUNC = 79;
  /** RegularExpression Id. */
  int CEIL_FUNC = 80;
  /** RegularExpression Id. */
  int ROUND_FUNC = 81;
  /** RegularExpression Id. */
  int GAMMA_FUNC = 82;
  /** RegularExpression Id. */
  int RANDOM_FUNC = 83;
  /** RegularExpression Id. */
  int FUNCTION_LABEL = 84;
  /** RegularExpression Id. */
  int TEXT = 85;
  /** RegularExpression Id. */
  int CHAR = 86;

  /** Lexical state. */
  int DEFAULT = 0;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\r\"",
    "\"\\t\"",
    "\"\\n\"",
    "\";\"",
    "\":=\"",
    "\"|\"",
    "\"\\u00ac\"",
    "<OR>",
    "<AND>",
    "<EQUAL_BOOLEAN>",
    "<NOT_EQUAL>",
    "\"<\"",
    "\">\"",
    "<LESS_EQUAL>",
    "<GREATER_EQUAL>",
    "\"\\u2225\"",
    "\"\\u22a5\"",
    "\"=\"",
    "\"+\"",
    "<MINUS>",
    "<MULTIPLY>",
    "\"\\u2297\"",
    "\"/\"",
    "\"^\"",
    "\"!\"",
    "<UNDEFINED>",
    "\"\\u2070\"",
    "\"\\u00b9\"",
    "\"\\u00b2\"",
    "\"\\u00b3\"",
    "\"\\u2074\"",
    "\"\\u2075\"",
    "\"\\u2076\"",
    "\"\\u2077\"",
    "\"\\u2078\"",
    "\"\\u2079\"",
    "<PI>",
    "\"\\u212f\"",
    "\"\\u00b0\"",
    "\"\\u221e\"",
    "\"rad\"",
    "\"\\\'\"",
    "<TRUE>",
    "<FALSE>",
    "\"x\"",
    "\"y\"",
    "\"X\"",
    "<FLOAT>",
    "<EFLOAT>",
    "<INTEGER>",
    "<DIGIT>",
    "<SPREADSHEET_LABEL>",
    "<LABEL>",
    "<INDEX>",
    "<LETTER>",
    "\"x(\"",
    "\"y(\"",
    "<COS_FUNC>",
    "<SIN_FUNC>",
    "<TAN_FUNC>",
    "<ACOS_FUNC>",
    "<ASIN_FUNC>",
    "<ATAN_FUNC>",
    "<COSH_FUNC>",
    "<SINH_FUNC>",
    "<TANH_FUNC>",
    "<ACOSH_FUNC>",
    "<ASINH_FUNC>",
    "<ATANH_FUNC>",
    "<EXP_FUNC>",
    "<LOG_FUNC>",
    "\"ld(\"",
    "\"lg(\"",
    "<SQRT_FUNC>",
    "\"cbrt(\"",
    "<ABS_FUNC>",
    "<SGN_FUNC>",
    "<FLOOR_FUNC>",
    "<CEIL_FUNC>",
    "<ROUND_FUNC>",
    "<GAMMA_FUNC>",
    "\"random()\"",
    "<FUNCTION_LABEL>",
    "<TEXT>",
    "<CHAR>",
    "\":\"",
    "\"{\"",
    "\"}\"",
    "\",\"",
    "\"[\"",
    "\"]\"",
    "\")\"",
    "\"(\"",
  };

}
