package geogebra.cas.view.components;

import geogebra.cas.view.CASView;
/**
 * EvalCommand - Simplify[...]
 * 
 * @author  Hans-Petter Ulven
 * @version 01.03.09
 */
public class CmdEval extends Command_ABS {

    private     static  CmdEval		singleton   =   null;
    private		static	CASView		casview		=	null;
    
    /// --- Interface --- ///
    
    /** Singleton constructor */
    public static CmdEval getInstance(CASView casview){
        if(singleton==null){singleton=new CmdEval();}
        CmdEval.casview=casview;
        return singleton;
    }//getInstance()
    
    /** Implementing Command_IF */
    public void execute(){
        process("Simplify",null);	//only command, no parameters
    }//execute()


}//class EvalCommand

