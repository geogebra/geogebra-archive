package geogebra.cas.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class CASOutputPanel extends JPanel {
	
	private static Color TEXT_COLOR = Color.blue;
	private static Color ERROR_COLOR = Color.red;

	private JLabel outputSign;
	private JLabel outputArea;

	public CASOutputPanel() {
		outputSign = new JLabel(" <<");
		outputArea = new JLabel();

		setLayout(new BorderLayout(5,5));
		add(outputSign, BorderLayout.WEST);
		add(outputArea, BorderLayout.CENTER);
		setBackground(Color.white);
	}

	public void setOutput(String inValue, boolean showsError) {
		outputArea.setText(inValue);
		if (showsError)
			outputArea.setForeground(ERROR_COLOR);
		else
			outputArea.setForeground(TEXT_COLOR);			
	}

	public String getOutput() {
		return outputArea.getText();
	}

	final public void setFont(Font ft) {
		super.setFont(ft);
		
		if (outputArea != null)
			outputArea.setFont(ft);
		if (outputSign != null)
			outputSign.setFont(ft);
	}
}