package geogebra.cas.view.components;


/**
 * CasToolBar
 * <pre>
 * Special variants of needed methods:
 * 		getModeText(int mode)
 * 		getToolBarDefinition();
 * 
 * </pre>
 * @author      Hans-Petter Ulven 
 * @version     11.03.09
 */

public class CasToolBar{

    /// --- Properties --- ///
	/** Menu/Mode id's */
	public static final int		MODE_SIMPLIFY	=	1;
	public static final int		MODE_NUMERIC	=	2;
	
    
    /// --- Interface --- ///
	public static String getModeText(int mode) {
		switch(mode) {
		case MODE_SIMPLIFY:				return		"Simplify";
		case MODE_NUMERIC:				return		"Numeric";
		default:						return		"";
		}//switch(mode)
		
		
	}//getModeText(mode)
	
	/**
	 * "1 , 2 3 | 4 5 6"
	 * , separator
	 * | new menu
	 * || separator before new menu
	 */
	public static String getToolbarDefinition() {
		StringBuffer sb	=	new StringBuffer();
		sb.append(MODE_SIMPLIFY);
		sb.append(" | ");
		sb.append(MODE_NUMERIC);
		//...
		return sb.toString();
	}//getToolbarDefinition()
	
}//class CasToolBar
