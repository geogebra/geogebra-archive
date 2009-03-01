package geogebra.cas.view.components;

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
    public static CmdExpand getInstance(String s){
        if(singleton==null){singleton=new CmdExpand();}
        g=s;
        return singleton;
    }//getInstance()
    
    /** Implementing Command_IF */
    public void execute(){
        process("Expand"+g);
    }//execute()


}//class CmdExpand