package geogebra.cas.view;

import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Dialog to substitude a string in a CAS input.
 * 
 * Quan Yuan
 */

public class CASSubDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JButton btSub, btEval, btCancel;
	private JPanel optionPane, btPanel, cbPanel, captionPanel;
	private JTextField valueTextField;

	private CASView casView;
	private Application app;
	private int editRow;
	private String prefix, evalText, postifx;
	private JTextField subStrfield;

	/**
	 * Substitute dialog for CAS.
	 */
	public CASSubDialog(CASView casView, String prefix, String evalText, String postfix, int editRow) {
		setModal(false);
		
		this.casView = casView;
		this.app = casView.getApp();
		this.prefix = prefix;
		this.evalText = evalText;
		this.postifx = postfix;
		
		this.editRow = editRow;
	
		createGUI();
		pack();
		setLocationRelativeTo(null);
	}

	protected void createGUI() {
		setTitle(app.getPlain("Substitute"));
		setResizable(false);

		// create label panel
		JPanel subTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		String temp = app.getPlain("SubstituteForAinB",
				"ThisIsJustTheSplitString", evalText);
		String[] strLabel = temp.split("ThisIsJustTheSplitString");
		JLabel subLabel = new JLabel(strLabel[0]);
		subStrfield = new JTextField(4);
		JLabel subLabel2 = new JLabel(strLabel[1]);
		subTitlePanel.add(subLabel);
		subTitlePanel.add(subStrfield);
		subTitlePanel.add(subLabel2);		

		// create caption panel
		JLabel captionLabel = new JLabel(app.getPlain("NewExpression") + ":");
		valueTextField = new JTextField();
		valueTextField.setColumns(20);
		captionLabel.setLabelFor(valueTextField);

		JPanel subPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		subPanel.add(captionLabel);
		subPanel.add(valueTextField);

		captionPanel = new JPanel(new BorderLayout(5, 5));
		captionPanel.add(subTitlePanel, BorderLayout.CENTER);
		captionPanel.add(subPanel, BorderLayout.SOUTH);

		

		// buttons
		btSub = new JButton(app.getPlain("Substitute"));
		btSub.setActionCommand("Substitute");
		btSub.addActionListener(this);

		btEval = new JButton("=");
		btEval.setActionCommand("Eval");
		btEval.addActionListener(this);

		btCancel = new JButton(app.getPlain("Cancel"));
		btCancel.setActionCommand("Cancel");
		btCancel.addActionListener(this);
		btPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		btPanel.add(btSub);
		btPanel.add(btEval);
		btPanel.add(btCancel);

		// Create the JOptionPane.
		optionPane = new JPanel(new BorderLayout(5, 5));

		// create object list
		optionPane.add(captionPanel, BorderLayout.NORTH);
		
		optionPane.add(btPanel, BorderLayout.SOUTH);
		optionPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		// Make this dialog display it.
		setContentPane(optionPane);
	}

	public void actionPerformed(ActionEvent ae) {
		Object src = ae.getSource();

		if (src == btCancel) {
			setVisible(false);
		} else if (src == btEval) {
			if (apply(btEval.getActionCommand()))
				setVisible(false);
		} else if (src == btSub) {
			if (apply(btSub.getActionCommand()))
				setVisible(false);
		}
	}

	private boolean apply(String actionCommand) {
		
		CASTable table = casView.getConsoleTable();
			
		// substitute from
		String	fromExp = subStrfield.getText();
		String toExp = valueTextField.getText();
		if (fromExp.length() == 0 || toExp.length() == 0) return false;				
		
		// substitute command
		String subCmd = "Substitute[" + evalText + "," + fromExp + ", " +  toExp + "]"; 
		if (actionCommand.equals("Eval")) {
			subCmd = "Eval[" + subCmd + "]"; 
		}
			
		try {
			CASTableCellValue currCell = table.getCASTableCellValue(editRow);
			String result = casView.getCAS().processCASInput(subCmd, casView.isUseGeoGebraVariableValues());
			currCell.setOutput(result);
			table.startEditingRow(editRow + 1);
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}
		
}