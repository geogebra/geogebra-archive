package geogebra.cas.view.components;

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
    
    /// --- Interface --- ///
    /** Enforcing singleton */
    private BtnPanel(){}
    
    /** Singleton constructor */
    public static BtnPanel getInstance(){
        if(singleton==null){singleton=new BtnPanel();}
        singleton.setLayout(new FlowLayout(FlowLayout.LEFT));
        CommandButton_ABS btn;
        btn=CmdBtnEval.getInstance();        btn.setText("=");         btn.setCommand(new CmdEval());
        singleton.add(btn);
        btn=CmdBtnExpand.getInstance();      btn.setText("Expand");    btn.setCommand(new CmdExpand());
        singleton.add(btn);   
        return singleton;
    }//getInstance()
}//class BtnPanel
