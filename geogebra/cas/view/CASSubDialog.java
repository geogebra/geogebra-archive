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

	private JButton btSub, btSubSim, btCancel;
	private JPanel optionPane, btPanel, cbPanel, captionPanel;

	private String subString;
	private Application app;

	/**
	 * Input Dialog for a GeoText object
	 */
	public CASSubDialog(Application app, String inStr) {
		super(app.getCasFrame(), false);
		this.app = app;
		this.subString = inStr;

		createGUI(app.getMenu("Substitute Dialog"));
		pack();
		setLocationRelativeTo(app.getMainComponent());
	}

	protected void createGUI(String title) {
		setTitle(title);
		setResizable(false);

		// create caption panel
		JLabel captionLabel = new JLabel(app.getPlain("New Value") + ":");
		JTextField valueTextField = new JTextField();
		valueTextField.setColumns(20);

		captionLabel.setLabelFor(valueTextField);
		captionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		captionPanel.add(captionLabel);
		captionPanel.add(valueTextField);

		// create checkbox panel
		Checkbox allReplaced = new Checkbox("Replace all");
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
		switch (mod) {
		case SUB:
			System.out.println("This is for sub action");
			break;
		case SUBSIM:
			System.out.println("This is for sub and simplify action");
			break;
		default:
			System.out.println("Action cancelled");
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

	public String getSubString() {
		return subString;
	}

	public void setSubString(String subString) {
		this.subString = subString;
	}

}