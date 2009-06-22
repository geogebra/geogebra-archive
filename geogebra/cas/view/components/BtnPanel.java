package geogebra.cas.view.components;

import geogebra.cas.view.CASView;

import java.awt.FlowLayout;

import javax.swing.JPanel;

/**
 * JPanel with buttons
 * 
 * @author      Hans-Petter Ulven 
 * @version     01.03.09
 */

public class BtnPanel extends JPanel{

    private static  BtnPanel    singleton   =   null;
    
    /// --- Properties --- ///
    private static 	CASView		casview		=	null;
    
    /// --- Interface --- ///
    /** Enforcing singleton */
    private BtnPanel(){}
    
    /** Singleton constructor */
    public static BtnPanel getInstance(CASView casview){
        if(singleton==null){singleton=new BtnPanel();}
        BtnPanel.casview=casview;
        singleton.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        // Set up buttons:
        
        CommandButton_ABS btn;
        btn=CmdBtnEval.getInstance();     		//Make button   
        btn.setText("=");         
        btn.setCommand(CmdEval.getInstance(casview));	//Set command
        singleton.add(btn);
        
        btn=CmdBtnExpand.getInstance();      btn.setText("Expand");    
        btn.setCommand(CmdExpand.getInstance(casview));     singleton.add(btn);   
        
        //...
        
        return singleton;
    }//getInstance()
}//class BtnPanel
