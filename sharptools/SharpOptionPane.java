package sharptools;
/** 
 * This class provides dialogs that can display a message or receive input
 * to the user. It has customized Buttons.
 *
 * The class is used to replace JOptionPane.
 * 
 * @author Hua Zhong
 * @version $Revision: 1.1 $
 *
 */
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class SharpOptionPane {

    /**
     * @param parentComponent the parent Component for the dialog
     * @param message the Object to display
     * @param title the String to display in the dialog title bar
     * @param messageType the type of message to be displayed
     * @param icon the Icon image to display
     * @param selectionValues an array of Objects that gives the possible selections
     * @param initialSelectionValue the value used to initialize the input field
     * @return the input object
     */
    public static Object showInputDialog(Component parentComponent,
					 Object message,
					 String title,
					 int messageType,
					 Icon icon,
					 Object[] selectionValues,
					 Object initialSelectionValue) {
	return showInputDialog(parentComponent, message, title, messageType,
			       icon, selectionValues, initialSelectionValue,
			       false);
    }

    /**
     * @param parentComponent the parent Component for the dialog
     * @param message the Object to display
     * @param title the String to display in the dialog title bar
     * @param messageType the type of message to be displayed
     * @param icon the Icon image to display
     * @param initialSelectionValue the value used to initialize the input field
     * @return the input string
     */
    public static Integer showIntegerInputDialog(Component parentComponent,
						  Object message,
						  String title,
						  int messageType,
						  Icon icon,
						  Object initialSelectionValue) {
	Object input = showInputDialog(parentComponent, message, title,
				       messageType, icon, null,
				       initialSelectionValue, true);
	try {
	    if (input != null)
		return Integer.getInteger(input.toString());
	}
	catch (Exception e) {

	}
	
	return null;
    }
    
    /**
     * This is used by showInputDialog and showIntegerInputDialog
     * 
     * @param parentComponent the parent Component for the dialog
     * @param message the Object to display
     * @param title the String to display in the dialog title bar
     * @param messageType the type of message to be displayed
     * @param icon the Icon image to display
     * @param selectionValues an array of Objects that gives the possible selections
     * @param initialSelectionValue the value used to initialize the input field
     * @param isInteger whether we only allow integer input
     * @return the input object
     */
    private static Object showInputDialog(Component parentComponent,
					  Object message,
					  String title,
					  int messageType,
					  Icon icon,
					  Object[] selectionValues,
					  Object initialSelectionValue,
					  boolean isInteger) {
	JPanel panel = new JPanel(new BorderLayout());
	JComponent com;
	int choice;

	if (selectionValues == null) {
	    // we use a JTextField
	    if (isInteger) {
		com = new NumberField(true, true);
		((JTextField)com).setText(initialSelectionValue.toString());
	    }
	    else
		com = new JTextField(initialSelectionValue.toString());

	    ((JTextField)com).selectAll();
	}
	else {
	    JComboBox box = new JComboBox(selectionValues);
	    if (initialSelectionValue != null)
		box.setSelectedItem(initialSelectionValue);
	    else
		box.setSelectedIndex(0);

	    com = box;
	
	}

	if (message instanceof Component)
	    panel.add((Component)message, BorderLayout.NORTH);
	else
	    panel.add(new Label(message.toString()), BorderLayout.NORTH);
	
	panel.add(com, BorderLayout.CENTER);
	
	choice = showOptionDialog(parentComponent, panel, title,
				  JOptionPane.OK_CANCEL_OPTION,
				  messageType, icon);

	if (choice == JOptionPane.OK_OPTION) {
	    if (com instanceof JTextField)
		return ((JTextField)com).getText();
	    else
		return ((JComboBox)com).getSelectedItem();
	}
	else
	    return null;
    }
          
    /**
     * @param parentComponent the parent component
     * @param message the message to be displayed
     */
    public static void showMessageDialog(Component parentComponent,
					 Object message) {
	showMessageDialog(parentComponent, message, null, 0, null);
    }
    
    /**
     * @param parentComponent the parent component
     * @param message the message to be displayed
     * @param title dialog title
     * @param messageType the message type
     * @param icon the icon to display
     */
    public static void showMessageDialog(Component parentComponent,
					 Object message,
					 String title,
					 int messageType) {
	showMessageDialog(parentComponent, message, title, messageType, null);
    }
    
    /**
     * @param parentComponent the parent component
     * @param message the message to be displayed
     * @param title dialog title
     * @param messageType the message type
     * @param icon the icon to display
     */
    public static void showMessageDialog(Component parentComponent,
					 Object message,
					 String title,
					 int messageType,
					 Icon icon) {	
	showOptionDialog(parentComponent, message, title,
			 JOptionPane.DEFAULT_OPTION,
			 messageType, icon, 0);
    }      

    /**
     * @param parentComponent the parent component
     * @param message the message to be displayed
     * @param title dialog title
     * @param optionType the option type
     * @param messageType the message type
     * @param icon the icon to display
     * @return user choice
     */
    public static int showOptionDialog(Component parentComponent,
				       Object message,
				       String title,
				       int optionType,
				       int messageType,
				       Icon icon) {
	return showOptionDialog(parentComponent, message, title, optionType,
				messageType, icon, 0);
    }
    
    /**
     * Note the interface is different from JOptionPane.showOptionDialog()
     * 
     * @param parentComponent the parent component
     * @param message the message to be displayed
     * @param title dialog title
     * @param optionType the option type
     * @param messageType the message type
     * @param icon the icon to display
     * @param defaultIndex defaultIndex
     * @return user choice
     */
    public static int showOptionDialog(Component parentComponent,
				       Object message,
				       String title,
				       int optionType,
				       int messageType,
				       Icon icon,			       
				       int defaultIndex) {

	SharpDialog dialog;

	if (parentComponent instanceof Frame)
	    dialog = new SharpDialog((Frame)parentComponent, title, true);
	else
	    dialog = new SharpDialog((Dialog)parentComponent, title, true);
	    
	dialog.setOptionPane(message, messageType,
			     optionType, icon,
			     defaultIndex);
	
	dialog.show();
	return dialog.getChoice();
    }
    
}    

