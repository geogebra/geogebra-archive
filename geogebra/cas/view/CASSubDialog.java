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

import geogebra.Application;
import geogebra.cas.GeoGebraCAS;

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

	private String subStr;
	private String inputStr;

	/**
	 * Input Dialog for a GeoText object
	 */
	public CASSubDialog(Application app, GeoGebraCAS cas, CASTableCell inCell,
			String subStr) {
		super(app.getCasFrame(), false);
		this.app = app;
		this.cas = cas;
		this.tableCell = inCell;
		this.subStr = subStr;
		this.inputStr = tableCell.getInput();

		replaceAllFlag = false;
		createGUI(app.getMenu("Substitute Dialog"));
		pack();
		setLocationRelativeTo(app.getMainComponent());
	}

	protected void createGUI(String title) {
		setTitle(title);
		setResizable(false);

		// create label panel
		JLabel subLabel = new JLabel(app.getPlain("Substitute for ") + subStr
				+ app.getPlain(" in ") + inputStr);
		JPanel subTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		subTitlePanel.add(subLabel);

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

		btSubSim = new JButton(app.getPlain("Substitute & Simplify"));
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
		if (allReplaced.getState())
			replaceAllFlag = true;

		switch (mod) {
		case SUB:
			// Replace the sub in the input string
			if (replaceAllFlag)
				tableCell.setInput(inputStr.replaceAll(subStr, newExpression));
			else
				tableCell
						.setInput(inputStr.replaceFirst(subStr, newExpression));
			break;
		case SUBSIM:
			// Replace the sub in the input string
			String preString;
			if (replaceAllFlag)
				preString = inputStr.replaceAll(subStr, newExpression);
			else
				preString = inputStr.replaceFirst(subStr, newExpression);
			
			tableCell.setInput(cas.evaluateYACAS(preString));
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