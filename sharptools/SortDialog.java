package sharptools;
/*
 * @(#)SortDialog.java
 *
 * $Id: SortDialog.java,v 1.1 2007-02-20 13:58:20 hohenwarter Exp $
 *
 * Created on November 16, 2000, 12:00 AM
 */

import java.util.Vector;
import javax.swing.*;
import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.awt.event.*;

/** 
 * This class provides a sort dialog.
 * User is prompted to choose sort criteria.
 * 
 * @author Andrei Scudder
 * @author Hua Zhong (use SharpDialog)
 * @version $Revision: 1.1 $
 */
public class SortDialog extends SharpDialog {
    
    private JLabel label;
    private JFrame frame;
    private JComboBox primary;
    private JComboBox tiebreaker;
    private boolean ascending1 = true;
    private boolean ascending2 = true;
    private int criteriaA = -1;
    private int criteriaB = -1;

    final private static ImageIcon sortIcon = SharpTools.getImageIcon("sort32.gif");

    final private ButtonGroup group = new ButtonGroup();	
    final private ButtonGroup group2 = new ButtonGroup();
    final private String ascending_1 = "ascending_1";
    final private String descending_1 = "decending_1";
    final private String ascending_2 = "ascending_2";
    final private String descending_2 = "decending_2";

    public SortDialog(JFrame aFrame, Vector first, Vector second) {
	super(aFrame, "Sort", true);	
	        
        primary = new JComboBox(first);
        primary.setSelectedIndex(0);
        
        tiebreaker = new JComboBox(second);
        tiebreaker.setSelectedIndex(0);
        
	JRadioButton[] radioButtons = new JRadioButton[4];
	JButton enter = null;
	JButton close = null;
	JPanel box = new JPanel();
	
	radioButtons[0] = new JRadioButton("Ascending");
        radioButtons[0].setActionCommand(ascending_1);
	
        radioButtons[1] = new JRadioButton("Descending");
        radioButtons[1].setActionCommand(descending_1);
	
        radioButtons[2] = new JRadioButton("Ascending");
        radioButtons[2].setActionCommand(ascending_2);
	
        radioButtons[3] = new JRadioButton("Descending");
        radioButtons[3].setActionCommand(descending_2);

	
	radioButtons[0].setSelected(true);
	group.add(radioButtons[0]);
        group.add(radioButtons[1]);

	
	radioButtons[2].setSelected(true);
	group2.add(radioButtons[2]);
	group2.add(radioButtons[3]);
	
       
        box.setLayout(new GridLayout(0, 3, 10, 5));

	// define key shortcut
        JLabel sortLabel = new JLabel("Sort By:");
	sortLabel.setLabelFor(primary);
	sortLabel.setDisplayedMnemonic(KeyEvent.VK_S);
	radioButtons[0].setMnemonic(KeyEvent.VK_A);
	radioButtons[1].setMnemonic(KeyEvent.VK_D);
	
	box.add(sortLabel);
	box.add(new JLabel(""));
	box.add(new JLabel(""));
	box.add(primary);
	box.add(radioButtons[0]);
	box.add(radioButtons[1]);

	// define key shortcut
	sortLabel = new JLabel("Then By:");
	sortLabel.setLabelFor(tiebreaker);
	sortLabel.setDisplayedMnemonic(KeyEvent.VK_T);
	radioButtons[2].setMnemonic(KeyEvent.VK_C);
	radioButtons[3].setMnemonic(KeyEvent.VK_E);	

	box.add(sortLabel);
	box.add(new JLabel(""));
	box.add(new JLabel(""));
	box.add(tiebreaker);
	box.add(radioButtons[2]);
	box.add(radioButtons[3]);
	
	Border padding = BorderFactory.createEmptyBorder(20,20,20,0);
	box.setBorder(padding);
	
	
	Object setting = box;
	
	setOptionPane(box, 
		      JOptionPane.PLAIN_MESSAGE,
		      JOptionPane.YES_NO_OPTION,
		      sortIcon);
	

        
        primary.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    JComboBox cb = (JComboBox)e.getSource();
		    criteriaA = cb.getSelectedIndex();
		}
	    });
	
	
	
	tiebreaker.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    JComboBox cb = (JComboBox)e.getSource();
		    criteriaB = cb.getSelectedIndex();
		}
	    });

    }

    private JPanel setPanel(){
	return null;
    }
 
    public boolean firstAscending(){
	return ascending1;		
    }
    
    public boolean secondAscending(){
	return ascending2;
    }
    
    public int getCriteriaA(){
	return criteriaA;
    }
    
    public int getCriteriaB(){
	return criteriaB;
    }
    
    void setLabel(String newText) {
        label.setText(newText);
    }

    
    protected boolean onOK() {
	criteriaA = primary.getSelectedIndex();
	criteriaB = tiebreaker.getSelectedIndex();
	String command = group.getSelection().getActionCommand();
	String command2 = group2.getSelection().getActionCommand();
		    
	//Set the sort order of the first column
	if (command == ascending_1) {
	    ascending1 = true;
	} else if (command == descending_1) {
	    ascending1 = false;
	    // Set the sort order of the scond column
	}
	
	if (command2 == ascending_2) {
	    ascending2 = true;
	    
	} else if (command2 == descending_2) {
	    ascending2 = false;
	}
	
	return true;
    }

    protected boolean onCancel() {
	criteriaA = -1;
	criteriaB = -1;
	return true;
    }

}


