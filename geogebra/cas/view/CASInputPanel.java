/**
 * This panel is for the input.
 */

package geogebra.cas.view;

import java.awt.Color;
import javax.swing.*;

public class CASInputPanel extends JPanel{
	
	private JLabel inputSign = new JLabel(">>");
	private JTextField inputArea;
	
	public CASInputPanel() {
		inputArea = new JTextField();
		
		inputArea.setBorder(BorderFactory.createEmptyBorder());
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.add(inputSign);
		inputSign.setBackground(Color.white);
		this.add(inputArea);
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setBackground(Color.white);
	}
	
	public void setInput(String inValue) {
		inputArea.setText(inValue);
	}
	
	public String getInput() {
		return inputArea.getText();
	}

	public JTextField getInputArea() {
		return inputArea;
	}
	
	public void setInputAreaFocused(){
		inputArea.requestFocus();
	}
}
