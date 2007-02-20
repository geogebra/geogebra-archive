package sharptools;
/*
 * @(#)HelpOp.java
 *
 * $Id: HelpOp.java,v 1.1 2007-02-20 13:58:21 hohenwarter Exp $
 *
 * Created on October 22, 2000, 2:46 AM
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

/**
 * Code taken from 
 * http://www.inquiry.com/techtips/java_pro/answer.asp?pro=java_pro&docID=2282.
 * Code for HTML Browser originally from DisplayHTML.java 
 * by Daniel Savarese on 9/15/98.
 * This html browser allows for the browsing of local help files. 
 *
 * @author Daniel Medina, Daniel Goldberg
 * @version $Revision: 1.1 $
 */
public class HelpOp {

    private JEditorPane htmlPane;
    private JScrollPane scrollPane;
    private JFrame frame;
    private Container container;
    private WindowListener exitListener;
    private HyperlinkListener linkListener;
    private URL url;

    public HelpOp(URL url) {

	frame = new JFrame("Help Browser: " + url);
	container = frame.getContentPane();
	container.setLayout(new BorderLayout());
	
	try {
	    htmlPane = new JEditorPane(url);
	} catch(IOException e) {
	    e.printStackTrace();
	    return;
	}
	
	// We only want to display the file, not edit it.
	htmlPane.setEditable(false);
	
	linkListener = new HyperlinkListener() {
		public void hyperlinkUpdate(HyperlinkEvent e) {
		    URL newURL;
		    Document currentDocument;
		    
		    if(e.getEventType() != HyperlinkEvent.EventType.ACTIVATED)
	  return;
		    
		    if((newURL = e.getURL()) == null)
			return;
		    
		    currentDocument = htmlPane.getDocument();
		    
		    try {
			htmlPane.setPage(newURL);
		    } catch(IOException ex) {
			htmlPane.setDocument(currentDocument);
		    }
		}
	    };
	
	htmlPane.addHyperlinkListener(linkListener);

	scrollPane = new JScrollPane(htmlPane);
	
	container.add(scrollPane);
	
	scrollPane.setPreferredSize(new Dimension(500, 500));
	
	exitListener = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		    Window window = e.getWindow();
		    window.setVisible(false);
		    window.dispose();
		}
	    };
	
	frame.addWindowListener(exitListener);
	
	frame.pack();
	frame.show();
    }

    final static String message =
	"<html><font size=2 color=black><p><font size=+1 color=blue><b>Sharp Tools Spreadsheet</b></font> Version 1.0</p>"+
	"<p><p>a product of the Sharp Tools Software Engineering Team"+
	"<p><p><font color=green><b>Credits:</b><font></p>"+
	"<ul><li>Hua Zhong</li><li>Ricky Chin</li><li>Daniel Goldberg</li>"+
	"<li>Daniel Medina</li><li>Andrei Scudder</li></ul></p>"+
	"<font color=blue><u><p>http://www.cs.columbia.edu/sharptools/</p>"+
	"<p>mailto:sharp@cs.columbia.edu</p></u></font></font>";

    // display the About message box
    public static void showAboutBox(JFrame frame) {
	
	SharpOptionPane.showMessageDialog
	    (frame,
	     message,
	     "About Sharp Tools Spreadsheet",		 
	     JOptionPane.INFORMATION_MESSAGE,
	     SharpTools.getImageIcon("skull.gif"));
    }

    // return a component that can do function name insertion!
    static public JComponent createFunctionListComponent(JFrame frame, JTable table) {

	JPanel comboxPanel = new JPanel();
	comboxPanel.setLayout(new BorderLayout());
	String[] funcNames = {	    
	    "Functions", "ABS", "AVERAGE", "COUNT", "E", "INT",
	    "LOG", "MAX", "MEANDEV", "MEDIAN", "MIN", "PI", "RANGE", "ROUND",
	    "SQRT", "SUM", "STDDEV",
	    "SIN", "COS", "TAN", "ASIN", "ACOS", "ATAN"	    
	};
	
	JComboBox funcList = new JComboBox(funcNames);
	Dimension combodim = funcList.getSize();
	funcList.setPreferredSize
	    (new Dimension(100, (int)combodim.getHeight()));
	comboxPanel.add(funcList, BorderLayout.WEST);

	// if a cell is being edited then insert the function name into the cell
	funcList.addActionListener(new FunctionListListener(frame, table));
	return comboxPanel;
    }
	
}

/**
 * Implements help on functions
 *
 * When the user selects a function and he is editing, insert the function
 * name to the editing point; otherwise, pop up a help box to briefly
 * explain the usage of this function.
 */
class FunctionListListener implements ActionListener {

    JFrame frame;
    JTable table;
    final private ImageIcon helpIcon = SharpTools.getImageIcon("help32.gif");

    FunctionListListener(JFrame frame, JTable table) {
	this.frame = frame;
	this.table = table;
    }
    
    public void actionPerformed(ActionEvent e) {
	JComboBox cb = (JComboBox)e.getSource();
	String funcName = (String)cb.getSelectedItem();
		
	try {
	    if (table.isEditing()) {
		int row = table.getEditingRow();
		int col = table.getEditingRow();
		// get the editor
		JTextField text = ((SharpCellEditor)table.getCellEditor(row, col)).getTextField();

		if (!funcName.equals("Functions")) {
		    // insert our function name
		    text.replaceSelection(funcName+"()");
		    // set caret between parentheses if the user needs to input parameters
		    Function fh = Formula.getFuncHandler(funcName);
		    if (fh != null && fh.requireParams())
			text.setCaretPosition(text.getCaretPosition()-1);
		}
		else
		    showGeneralFunctionHelp();
		text.requestFocus();
	    }	    	
	    else {
		// pop up help message box!
		Function fh = Formula.getFuncHandler(funcName);
		if (fh != null) {
		    
		    String message = "<html><font size=2 color=black><b>"+
			fh.getUsage()+"</b><p><p>"+fh.getDescription()+"<p></font>";

		    SharpOptionPane.showMessageDialog(frame,
						      message,
						      "Function",
						      JOptionPane.INFORMATION_MESSAGE,
						      helpIcon);
		}
		else
		    showGeneralFunctionHelp();
		
		table.requestFocus();
	    }
	}
	catch (Exception exception) {};
	// reset the selection and focus
	cb.setSelectedIndex(0);		
    }

    private void showGeneralFunctionHelp() {
	String message = "<html><font size=2 color=black><b><font size=3>Functions</font></b><p>"+
	    "<p>Functions are predefined formulas that perform calculations by"+
	    "<p>using specific values, called arguments, in a particular order,"+"<p>or structure.<p>"+
	    "<p><b>Arguments</b> Arguments can be numbers or cell references. A cell"+
	    "<p>reference can be a single cell or a range of cells.  A single cell can"+
	    "<p>use relative addressing (A1) or absolute addressing ($B$5).  A range"+
	    "<p>of cell is specified by a pair of diagonal cells (A1:C5, $A$1:$C$5)."+
	    "<p>Different functions may have different requirements on the number"+
	    "<p>or type of parameters.<p>"+
	    "<p><b>Structure</b> The structure of a function begins with the function"+
	    "<p>name, followed by an opening parenthesis, the arguments for"+
	    "<p>the function separated by commas, and a closing parenthesis."+
	    "<p>If the function starts a formula, type an equal sign (=) before"+
	    "<p>the function name.  As you are editing a formula that contains"+
	    "<p>a function, you can click on a function in this list to insert the"+
	    "<p>function name into the cell.<p>"+
	    "<p><b>Examples:</b><p>"+
	    "<p>=SUM(A10,B5:B10,50,37)"+
	    "<p>=AVERAGE($B$5:$B$10)/10</font>";

	SharpOptionPane.showMessageDialog(frame,
					  message,
					  "Function",
					  JOptionPane.INFORMATION_MESSAGE,
					  helpIcon);

    }
}







