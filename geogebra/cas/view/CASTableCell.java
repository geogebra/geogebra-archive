package geogebra.cas.view;

import java.awt.GridLayout;

import javax.swing.*;

public class CASTableCell extends JPanel{

	private JTextField	inputArea;
	private JTextField	outputArea;
	private String		input;
	private String		output;
	
	public CASTableCell() {
		inputArea = new JTextField();
		outputArea = new JTextField();
		
		inputArea.setText(">>");
		outputArea.setText("<<");
		
		//this.setLayout(new GridLayout(2, 1));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(inputArea);	
		this.add(outputArea);
		return;
	}			

	public void setInput(String inValue){
		this.input = inValue;
	}
	
	public void setOutput(String inValue){
		this.output = inValue;
	}
	
	public String getInput(){
		return input;
	}
	
	public String getOutput(){
		return output;
	}
	
}
