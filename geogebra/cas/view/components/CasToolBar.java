package geogebra.cas.view.components;


/**
 * CasToolBar
 * <pre>
 * Special variants of needed methods:
 * 		getModeText(int mode)
 * 		getToolBarDefinition();
 * Factory for commands:
 * 		getCommand(int mode,CASView casview)
 * 
 * </pre>
 * @author      Hans-Petter Ulven 
 * @version     11.03.09
 */
import geogebra.cas.view.CASView;

public class CasToolBar{

	/** Menu/Mode id's */
	public static final int		MODE_SIMPLIFY	=	1;
	public static final int		MODE_EXPAND		=	2;
	public static final int		MODE_NUMERIC	=	3;

    /// --- Properties --- ///
    
    /// --- Interface --- ///
	
	/** Cas getModeText */
	public static String getModeText(int mode) {
		switch(mode) {
		case MODE_SIMPLIFY:				return		"Simplify";
		case MODE_EXPAND:				return		"Expand";
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
		sb.append(MODE_EXPAND);
		sb.append(" | ");
		sb.append(MODE_NUMERIC);
		//...
		return sb.toString();
	}//getToolbarDefinition()
	
	/** Factory for commands */
	public static Command_ABS getCommand(int mode,CASView casview) {
		switch(mode) {
		case(MODE_SIMPLIFY):
				return CmdEval.getInstance(casview);
		case (MODE_EXPAND):
				return CmdExpand.getInstance(casview);
			default:
				return null;
		}//switch
	}//getCommand(mode)

}//class CasToolBar
