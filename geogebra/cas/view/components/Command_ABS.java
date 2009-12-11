package geogebra.cas.view.components;
import geogebra.cas.view.CASView;

/**
 * Command_ABS
 * <pre>
 *    Inherited in  EvalCommand, SimplifyCommand,...
 *    which can be set to menues and buttons in CASView.
 *    These should be singletons.
 * </pre>
 * @author  Hans-Petter Ulven
 * @version 01.03.09
 */
public abstract class  Command_ABS{

    static  CASView			casview	=	null;

    /// --- Interface --- ///
    
    /** To be overridden */
    public abstract void execute();
    
    /** Handler for processing the button's ggbcmdprefix */
    public void process(String ggbcmdprefix,String[] params){
    	//some processing
        if(casview!=null){
        	casview.processInput(ggbcmdprefix,params);
        }else{
        	geogebra.main.Application.debug("casview not initialized!");
        }//if
    }//process(String)
    
}//abstract class Command_ABS
