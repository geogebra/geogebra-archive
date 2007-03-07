package sharptools;
/*
 * @ (#)ConnectDialog.java
 *
 * $Id: ConnectDialog.java,v 1.2 2007-03-07 06:24:32 hohenwarter Exp $
 *
 * Created on May 19, 2001, 09:10:28 PM
 *
 * Changelogs:
 *
 * first version - Shiraz Kanga
 * various fixes and enhancements - Hua Zhong
 */

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * This class provides a database conection dialog.
 * User is prompted to choose various database options.
 * 
 * @author Shiraz Kanga (first version)
 * @author Hua Zhong (ported to SharpDialog and various enhancements)
 * @version $Revision: 1.2 $
 */

public class ConnectDialog extends SharpDialog {
    
    private JFrame frame;
    private JOptionPane optionPane;
    private JCheckBox lockTableBox;
    private JCheckBox verifySaveBox;

    final private static ImageIcon removeIcon = SharpTools.getImageIcon ("no.gif");
    final private static ImageIcon databaseIcon = SharpTools.getImageIcon ("database32.gif");
    final private static ImageIcon dbErrorIcon = null;
    
    private Connection dbConnection = null;
    
    final private JTextField connectnameField = new JTextField ();
    final private JTextField usernameField = new JTextField ();
    final private JPasswordField passwordField = new JPasswordField ();
    final private JTextField driverField = new JTextField ();
    final private JTextField urlField = new JTextField ();
    //    final private JCheckBox save = new JCheckBox("Save connection", true);
    final private JCheckBox saveConnection = new JCheckBox("Save Connection", true);
    final private JCheckBox savePassword = new JCheckBox("Save Password", false);

    private int maxConn = 0;
    
    final private JButton removeButton = new JButton("Remove", removeIcon);
    private JComboBox box;
    
    public ConnectDialog (JFrame aFrame) {
	super (aFrame, "Connect to Database", true);
	
	frame = aFrame;

	//various properties of the dialog labels and text fields	
	final String msgString0 = "Connection Name:";
	final String msgString1 = "Username:";
	final String msgString2 = "Password:";
	final String msgString3 = "Driver:";
	final String msgString4 = "URL:";

	final Config config = SharpTools.getConfig();
	maxConn = config.getInt ("NUMCONNECTIONS");

	final Vector possibleValues = new Vector();
	possibleValues.add("Previous connections");
	
	// use Vector so that we won't enter null pointers in - huaz
	for (int i=1; i<=maxConn; i++) {
	    String entry = config.get ("CONNECTION." + i + ".NAME");
	    if (entry != null && entry.length() > 0)
		possibleValues.add(entry);
	    else
		break;    
	}

	box = new JComboBox(possibleValues);
	box.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
		    if (e.getStateChange() == ItemEvent.SELECTED) {
			    
			//			Config config = SharpTools.getConfig();
			int index = box.getSelectedIndex();
			if (index <= 0)
			    return;
			String key = "CONNECTION."+index;
			connectnameField.setText(config.get(key+".NAME"));
			usernameField.setText(config.get(key+".USERNAME"));
			passwordField.setText(config.get(key+".PASSWORD"));
			urlField.setText(config.get(key+".URL"));
			driverField.setText(config.get(key+".DRIVER"));
		    }
		}
	    });

	removeButton.setToolTipText("Remove the selected connection");
	removeButton.setMnemonic(KeyEvent.VK_R);
	removeButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    int index = box.getSelectedIndex();
		    if (index <= 0)
			return;
		    //		    Config config = SharpTools.getConfig();

		    // we need to move the following connections upward
		    for (int i = index; i < maxConn; i++) {
			config.set("CONNECTION."+i+".NAME",
				   config.get("CONNECTION."+(i+1)+".NAME"));
			config.set("CONNECTION."+i+".USERNAME",
				   config.get("CONNECTION."+(i+1)+".USERNAME"));
			config.set("CONNECTION."+i+".PASSWORD",
				   config.get("CONNECTION."+(i+1)+".PASSWORD"));
			config.set("CONNECTION."+i+".URL",
				   config.get("CONNECTION."+(i+1)+".URL"));
			config.set("CONNECTION."+i+".DRIVER",
				   config.get("CONNECTION."+(i+1)+".DRIVER"));
		    }

		    String maxkey = "CONNECTION."+maxConn;
		    config.set(maxkey+".NAME", "");

		    // remove from item
		    box.setSelectedIndex(0);
		    box.removeItemAt(index);
		}
	    });

	JPanel panel = new JPanel(new FlowLayout());
	panel.add(box);
	panel.add(removeButton);

	JPanel cbPanel = new JPanel(new FlowLayout());
	cbPanel.add(saveConnection);
	cbPanel.add(savePassword);
	
	Object[] input = {
	    panel,
	    msgString0, connectnameField, msgString1, usernameField,
	    msgString2, passwordField, msgString3, driverField,
	    msgString4, urlField, cbPanel
	};
	
	setOptionPane (input,
		       JOptionPane.PLAIN_MESSAGE,
		       JOptionPane.OK_CANCEL_OPTION,
		       databaseIcon);
	
    }

    public Connection getConnection() {
	return dbConnection;
    }

    protected boolean onOK() {
        String connectName = connectnameField.getText ().trim();
        String dbUsername = usernameField.getText ().trim();
        String dbPassword = String.copyValueOf(passwordField.getPassword ());
        String dbDriver = driverField.getText ().trim();
	String dbUrl = urlField.getText ().trim();	
	
	// validation moved from Database.java - huaz

	if ((dbUsername == null || dbUsername.length () == 0) &&
	    (dbUrl == null || dbUrl.length () == 0) &&
	    (dbDriver == null || dbDriver.length () == 0)) {
	    SharpOptionPane.showMessageDialog(this,
					      "You must provide values for Username, Url and Driver.\n",
					      "Connect",
					      JOptionPane.INFORMATION_MESSAGE,
					      databaseIcon);
	    return false;
	}

	try {
	    Class.forName (dbDriver);
	}
	catch (ClassNotFoundException e) {
	    SharpOptionPane.showMessageDialog (this, "Unable to load class " + dbDriver + ". Please ensure that it is in your classpath.\n" + e.toString (),
					       "Class Not Found", JOptionPane.ERROR_MESSAGE, dbErrorIcon);
	    return false;
	}
	
	try {
	    dbConnection =  DriverManager.getConnection (dbUrl, dbUsername, dbPassword);
	}
	catch (SQLException e) {
	    SharpOptionPane.showMessageDialog (this, "Unable to connect to the database at " + dbUrl + ".\nPlease ensure that the URL, Username and Password are correct.\n\n" + e.toString (),
					       "Connect", JOptionPane.ERROR_MESSAGE, dbErrorIcon);
	    return false;
	}	
	
	// now we could try to save the connection - huaz
	
	// if saveConnection is not checked we just return
	if (! saveConnection.isSelected())
	    return true;
	
	// if no name provided we don't save
	if (connectName.length() == 0) {
	    SharpOptionPane.showMessageDialog(this, "You did not enter a connection name.\n\nThis connection succeeded but will not be saved.\n", "Save Connection", JOptionPane.WARNING_MESSAGE);
	    return true;
	}
	    

	// first check whether the connectName is already saved
	int index;
	
	for (index = 1; index < box.getItemCount(); index++)
	    if (connectName.equals(box.getItemAt(index).toString()))
		break;

	if ((index != box.getItemCount() || box.getItemCount() <= maxConn)) {
	    String key = "CONNECTION."+index;
	    Debug.println("Saving "+key);
	
	    Config config = SharpTools.getConfig();
	    config.set(key+".NAME", connectName);
	    config.set(key+".USERNAME", dbUsername);
	    if (savePassword.isSelected())
		config.set(key+".PASSWORD", dbPassword);
	    config.set(key+".URL", dbUrl);
	    config.set(key+".DRIVER", dbDriver);
	}
	else
	    SharpOptionPane.showMessageDialog(this, "You have defined "+maxConn+" connections.\nYour current connection setting cannot be saved.\n\nRefer to the manual for advanced configuration.\n", "Save Connection", JOptionPane.WARNING_MESSAGE);
	    
        return true;
    }

    protected void onOpen() {
        // set the initial focus to textField
        connectnameField.requestFocus ();
    }
}


