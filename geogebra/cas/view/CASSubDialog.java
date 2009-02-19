package geogebra.cas.view;

import geogebra.cas.GeoGebraCAS;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

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

public class CASSubDialog extends JDialog implements WindowFocusListener,
		ActionListener {

	private static final long serialVersionUID = 1L;

	private static final int SUB = 0;
	private static final int SUBSIM = 1;

	private boolean replaceAllFlag;

	private JButton btSub, btSubSim, btCancel;
	private JPanel optionPane, btPanel, cbPanel, captionPanel;
	private JTextField valueTextField;
	private Checkbox allReplaced;

	private CASTableCellValue cellValue;
	private CASView casView;
	private Application app;
	private int editRow;
	private String subStr;
	private String inputStr;
	private boolean selectedEnabled;
	private JTextField subStrfield;

	/**
	 * Input Dialog for a GeoText object
	 */
	public CASSubDialog(CASView casView, CASTableCellValue cellValue, String subStr, int editRow) {
		setModal(false);
		
		this.casView = casView;
		this.app = casView.getApp();
		this.cellValue = cellValue;
		this.subStr = subStr;
		this.editRow = editRow;
		selectedEnabled = true;

		replaceAllFlag = false;
		createGUI(app.getMenu("SubstituteDialog"));
		pack();
		setLocationRelativeTo(app.getMainComponent());
	}

	protected void createGUI(String title) {
		setTitle(title);
		setResizable(false);

		// create label panel
		JPanel subTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		if (subStr != null) {
			JLabel subLabel = new JLabel(app.getPlain("SubstituteForAinB",
					subStr, inputStr));
			subTitlePanel.add(subLabel);
		} else {
			selectedEnabled = false;
			String temp = app.getPlain("SubstituteForAinB",
					"QuanIsGreat", inputStr);
			String[] strLabel = temp.split("QuanIsGreat");
			JLabel subLabel = new JLabel(strLabel[0]);
			subStrfield = new JTextField(4);
			JLabel subLabel2 = new JLabel(strLabel[1]);
			subTitlePanel.add(subLabel);
			subTitlePanel.add(subStrfield);
			subTitlePanel.add(subLabel2);
		}

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

		// create checkbox panel
		if (subStr != null)
			allReplaced = new Checkbox(app
					.getPlain("SubstituteforAllA", subStr));
		else
			allReplaced = new Checkbox(app.getPlain("SubstituteforAllA", "..."));
		cbPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		cbPanel.add(allReplaced);

		// buttons
		btSub = new JButton(app.getPlain("Substitute"));
		btSub.setActionCommand("Substitute");
		btSub.addActionListener(this);

		btSubSim = new JButton(app.getPlain("SubstituteSimplify"));
		btSubSim.setActionCommand("Subsim");
		btSubSim.addActionListener(this);

		btCancel = new JButton(app.getPlain("Cancel"));
		btCancel.setActionCommand("Cancel");
		btCancel.addActionListener(this);
		btPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		btPanel.add(btSub);
		btPanel.add(btSubSim);
		btPanel.add(btCancel);

		// Create the JOptionPane.
		optionPane = new JPanel(new BorderLayout(5, 5));

		// create object list
		optionPane.add(captionPanel, BorderLayout.NORTH);
		optionPane.add(cbPanel, BorderLayout.CENTER);
		optionPane.add(btPanel, BorderLayout.SOUTH);
		optionPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Make this dialog display it.
		setContentPane(optionPane);
	}

	public void actionPerformed(ActionEvent ae) {
		Object src = ae.getSource();

		if (src == btCancel) {
			setVisible(false);
		} else if (src == btSubSim) {
			apply(SUBSIM);
			setVisible(false);
		} else if (src == btSub) {
			apply(SUB);
			setVisible(false);
		}
	}

	private void apply(int mod) {
		String newExpression = valueTextField.getText();
		CASTable table = casView.getConsoleTable();
	
		if (allReplaced.getState())
			replaceAllFlag = true;

		if (!selectedEnabled) {
			subStr = subStrfield.getText();
		}

		if (subStr.length() == 0)
			return;

		CASTableCellValue newRow;
		
		switch (mod) {
		case SUB:
			// Replace the sub in the input string
			newRow = new CASTableCellValue();

			if (replaceAllFlag)
				newRow.setInput(inputStr.replaceAll(subStr, newExpression));
			else
				newRow.setInput(inputStr.replaceFirst(subStr, newExpression));

			table.insertRow(editRow, CASPara.contCol, newRow);
			break;
			
		case SUBSIM:
			// Replace the sub in the input string
			String preString;
			newRow = new CASTableCellValue();
			
			if (replaceAllFlag)
				preString = inputStr.replaceAll(subStr, newExpression);
			else
				preString = inputStr.replaceFirst(subStr, newExpression);

//			// get YacasString
//			String yacasString = null;
//			try {
//				GeoGebraCAS cas = casView.getCAS();
//				yacasString = cas.toMathPiperString(cas.parseGeoGebraCASInput(preString), false);
//			}
//			catch (Throwable th) {
//				th.printStackTrace();
//				return;
//			}
			
			newRow.setInput(preString);
			table.insertRow(editRow, CASPara.contCol, newRow);
			break;
		default:
			break;
		}
	}

	public void windowGainedFocus(WindowEvent arg0) {
		// TODO
	}

	public void windowLostFocus(WindowEvent arg0) {
	}

	public void setVisible(boolean flag) {
		if (!isModal()) {
			if (flag) { // set old mode again
				addWindowFocusListener(this);
			} else {
				removeWindowFocusListener(this);
				app.setSelectionListenerMode(null);
			}
		}
		super.setVisible(flag);
	}
}