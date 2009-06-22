package geogebra.cas.view.components;

import geogebra.main.Application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

/**
 * Abstract CommandButton_ABS
 * <pre>
 * Parent for buttons who
 *      -listens to themselves, implementing ActionListener
 *      -implements Command_IF
 *      -should be singletons
 * Saves a long case-statement.
 * Can adjust all visual properties in this class.
 * </pre>
 * @author      Hans-Petter Ulven
 * @version     01.03.09
 */
public abstract class CommandButton_ABS extends JButton implements ActionListener{

    /// --- Properties --- ///
    private Command_ABS  command =   null;
    
    /// --- Interface --- ///
    
    /** Constructor with command setting */
    
    /** To set the command for the button. */
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
    
}//abstract class CommandButton
