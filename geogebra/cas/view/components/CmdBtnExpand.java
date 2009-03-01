package geogebra.cas.view.components;

/**
 * CmdBtnExpand
 * 
 * @author      Hans-Petter Ulven
 * @version     01.03.09
 */
public class CmdBtnExpand extends CommandButton_ABS {

    /// --- Properties --- ///
    
    private static CmdBtnExpand   singleton   =   null;
    
    /// --- Interface --- ///
    public static CmdBtnExpand   getInstance(){
        if(singleton==null){singleton=new CmdBtnExpand();}
        return singleton;
    }//getInstance()
    
}//class CmdBtnExpand
