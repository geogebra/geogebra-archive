/**
 * This panel is for the output.
 */
package geogebra.cas.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.*;

/**
 * @author Quan
 * 
 */
public class CASOutputPanel extends JPanel {

	private JLabel outputSign = new JLabel("<<");
	// private JTextField outputArea;
	private JLabel outputArea;

	public CASOutputPanel() {
		// outputArea = new JTextField();
		outputArea = new JLabel();
		outputArea.setBorder(BorderFactory.createEmptyBorder());
		// this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		GridBagLayout gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		GridBagConstraints c = new GridBagConstraints();

		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		gridbag.setConstraints(outputSign, c);
		this.add(outputSign);
		outputSign.setBackground(Color.white);

		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		gridbag.setConstraints(outputArea, c);
		this.add(Box.createRigidArea(new Dimension(5, 2)));

		outputArea.setForeground(Color.gray);
		this.add(outputArea);
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setBackground(Color.white);
	}

	public void setOutput(String inValue) {
		outputArea.setText(inValue);
	}

	public String getOutput() {
		return outputArea.getText();
	}

	final public void setFont(Font ft) {
		if (outputArea != null)
			outputArea.setFont(ft);
		if (outputSign != null)
			outputSign.setFont(ft);
	}
}