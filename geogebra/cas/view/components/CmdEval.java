package geogebra.cas.view.components;

/**
 * EvalCommand - Simplify[...]
 * 
 * @author  Hans-Petter Ulven
 * @version 01.03.09
 */
public class CmdEval extends Command_ABS {

    private     static  CmdEval   singleton   =   null;
    
    /// --- Interface --- ///
    
    /** Singleton constructor */
    public static CmdEval getInstance(String x){
        if(singleton==null){singleton=new CmdEval();}
        g=x;
        return singleton;
    }//getInstance()
    
    /** Implementing Command_IF */
    public void execute(){
        process("Simplify"+g);
    }//execute()


}//class EvalCommand

