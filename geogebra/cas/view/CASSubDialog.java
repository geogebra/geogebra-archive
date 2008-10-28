package geogebra.cas.view;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import geogebra.cas.GeoGebraCAS;
import geogebra.main.Application;

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

	private CASTableCell tableCell;
	private Application app;
	private GeoGebraCAS cas;
	private int editRow;
	private String subStr;
	private String inputStr;
	private boolean selectedEnabled;
	private JTextField subStrfield;

	/**
	 * Input Dialog for a GeoText object
	 */
	public CASSubDialog(Application app, GeoGebraCAS cas, CASTableCell inCell,
			String subStr, int editRow) {
		super(app.getCasFrame(), false);
		this.app = app;
		this.cas = cas;
		this.tableCell = inCell;
		this.subStr = subStr;
		this.editRow = editRow;
		this.inputStr = tableCell.getInput();
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
			JLabel subLabel = new JLabel(app.getPlain("Substitute for ")
					+ subStr + app.getPlain(" in ") + inputStr);
			subTitlePanel.add(subLabel);
		} else {
			selectedEnabled = false;
			JLabel subLabel = new JLabel(app.getPlain("Substitute for "));
			subStrfield = new JTextField(4);
			JLabel subLabel2 = new JLabel(app.getPlain(" in ") + inputStr);
			subTitlePanel.add(subLabel);
			subTitlePanel.add(subStrfield);
			subTitlePanel.add(subLabel2);
		}

		// create caption panel
		JLabel captionLabel = new JLabel(app.getPlain("New Expression") + ":");
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
		allReplaced = new Checkbox("Substitute for All");
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
		CASTable table = tableCell.getConsoleTable();
		CASTableCellValue value;

		if (allReplaced.getState())
			replaceAllFlag = true;

		if (!selectedEnabled) {
			subStr = subStrfield.getText();
		}

		if (subStr.length() == 0)
			return;

		switch (mod) {
		case SUB:
			// Replace the sub in the input string
			value = new CASTableCellValue();

			if (replaceAllFlag)
				value.setCommand(inputStr.replaceAll(subStr, newExpression));
			else
				value.setCommand(inputStr.replaceFirst(subStr, newExpression));

			table.insertRow(editRow, CASPara.contCol, value);
			break;
		case SUBSIM:
			// Replace the sub in the input string
			String preString;
			value = new CASTableCellValue();
			if (replaceAllFlag)
				preString = inputStr.replaceAll(subStr, newExpression);
			else
				preString = inputStr.replaceFirst(subStr, newExpression);

			value.setCommand(cas.simplifyYACAS(preString));
			table.insertRow(editRow, CASPara.contCol, value);
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