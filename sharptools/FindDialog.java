package sharptools;
/*
 * @(#)FindDialog.java
 *
 * $Id: FindDialog.java,v 1.1 2007-02-20 13:58:21 hohenwarter Exp $
 *
 * Created on November 28, 2000, 02:12 AM
 */

import javax.swing.*;
import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.awt.event.*;

/** 
 * This class provides a find dialog.
 * User is prompted to choose find options.
 * 
 * @author Andrei Scudder
 * @author Hua Zhong (use SharpDialog)
 * @version $Revision: 1.1 $
 */
public class FindDialog extends SharpDialog {
    
    private JLabel label;
    private JFrame frame;
    final private JTextField textField = new JTextField(10);
    private JCheckBox caseSensitiveBox;
    private JCheckBox matchCellBox;
    private String typedText = null;
    private boolean caseSensitive = false;
    private boolean matchCell = false;
    final private ImageIcon findIcon = SharpTools.getImageIcon("find32.gif");

    public FindDialog(JFrame aFrame, String findValue, boolean mCase,
		      boolean mCell) {
	super(aFrame, "Find", true);
	
	textField.setText(findValue);
	
	caseSensitiveBox = new JCheckBox("Match Case");
	caseSensitiveBox.setMnemonic(KeyEvent.VK_M);
	caseSensitiveBox.setSelected(mCase);
	
	matchCellBox = new JCheckBox("Match Entire Cell Only");
	matchCellBox.setMnemonic(KeyEvent.VK_E); 
	matchCellBox.setSelected(mCell);

	JPanel box = new JPanel(new BorderLayout(0, 5));

	box.add(textField, BorderLayout.NORTH);
	box.add(caseSensitiveBox, BorderLayout.WEST);
	box.add(matchCellBox, BorderLayout.EAST);
	
	setOptionPane(box, 
		      JOptionPane.PLAIN_MESSAGE,
		      JOptionPane.OK_CANCEL_OPTION,
		      findIcon);	
    }
    
    public boolean isCaseSensitive(){
	return caseSensitive;		
    }
    
    public boolean isCellMatching(){
	return matchCell;
    }
    
    public String getString(){
	return typedText;
    }

    protected void onOpen() {
	textField.selectAll();
	textField.requestFocus();
    }

    protected boolean onOK() {
	typedText = textField.getText();
	caseSensitive = caseSensitiveBox.isSelected();
	matchCell = matchCellBox.isSelected();
	return true;
    }
}
