package geogebra.cas.view.components;

import geogebra.cas.view.CASView;

/**
 * ExpandCommand - Expand[...]
 * 
 * @author      Hans-Petter Ulven
 * @version     01.03.09
 */
public class CmdExpand extends Command_ABS {

    private     static  CmdExpand   singleton   =   null;
    
    /// --- Interface --- ///
    
    /** Singleton constructor */
    public static CmdExpand getInstance(CASView casview){
        if(singleton==null){singleton=new CmdExpand();}
        CmdExpand.casview=casview;
        return singleton;
    }//getInstance()
    
    /** Implementing Command_IF */
    public void execute(){
        process("Expand",null);		//only prefix, no params
    }//execute()


}//class CmdExpand