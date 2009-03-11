package geogebra.cas.view.components;

import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import geogebra.main.Application;

/**
 * CommandMenuItem
 * <pre>
 * JMenuItems who
 *      -listens to themselves, implementing ActionListener
 *      -implements Command_IF
 *      -should be singletons
 * Saves a long case-statement.
 * Can adjust all visual properties in this class.
 * </pre>
 * @author      Hans-Petter Ulven
 * @version     11.03.09
 */
public  class CommandMenuItem extends JMenuItem implements ActionListener{
	private final static  long serialVersionUID	=0L;

    /// --- Properties --- ///
    private Command_ABS  command =   null;
    
    /// --- Interface --- ///
    
    /** Constructor with command setting */
    
    /** To set the command for the JMenuItem. */
    public void setCommand(Command_ABS command){
        this.command=command;
        this.addActionListener(this);
    }//setCommand(Command_IF)
    
    /** Implementing ActionListener */
    public void actionPerformed(ActionEvent ae){
        if(command!=null){
            command.execute();
        }else{
            Application.debug("No command installed!");
        }//if command installed
    }//actionPerformed(ActionEvent)
    
}//abstract class CommandMenuItem
