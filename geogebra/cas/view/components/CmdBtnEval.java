package geogebra.cas.view.components;

/**
 * CmdBtnEval - Button for Simplify
 * 
 * @author      Hans-Petter Ulven
 * @version     01.03.09
 */
public class CmdBtnEval extends CommandButton_ABS{

    /// --- Properties --- ///
    
    private static CmdBtnEval   singleton   =   null;
    
    /// --- Interface --- ///
    public static CmdBtnEval   getInstance(){
        if(singleton==null){singleton=new CmdBtnEval();}
        return singleton;
    }//getInstance()

}//class CmdBtnEval
