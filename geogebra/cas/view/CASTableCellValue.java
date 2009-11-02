package geogebra.cas.view;

import geogebra.util.Util;


public class CASTableCellValue {
	private String input, output, latex;
	private boolean error = false;
		

	private CASView view;

	public CASTableCellValue(CASView view) {
		this.view = view;
		
		input = "";
		output = "";
	}	

	public CASTableCellValue(String inCom) {
		input = inCom;
		output = "";
	}

	public String getInput() {
		return input;
	}

	public String getOutput() {
		return output;
	}
	
	public String getLaTeXOutput() {
		return latex;
	}

	public boolean isOutputVisible() {
		return output == null || output.length() == 0;
	}
	
	public boolean isEmpty() {
		return isInputEmpty() && isOutputEmpty();
	}
	
	public boolean isInputEmpty() {
		return (input == null || input.length() == 0);
	}
	
	public boolean isOutputEmpty() {
		return (output == null || output.length() == 0);
	}

	public void setInput(String inValue) {
		input = inValue;
	}

	public void setOutput(String inValue) {
		setOutput(inValue, false);
	}
	
	public void setOutput(String output, boolean isError) {
		this.output = output;
		error = isError;
		latex = isError ? null : view.getCAS().convertGeoGebraToLaTeXString(output);
	}
	
	public boolean isOutputError() {
		return error;
	}

	// generate the XML file for this CASTableCellValue
	public String getXML() {
		String input = getInput();
		String output = getOutput();
		
		boolean inputEmpty = input == null || input.length() == 0;
		boolean outputEmpty = output == null || output.length() == 0;
		
		// check if cell pair is empty
		if (inputEmpty && outputEmpty)
			return "";
		
		StringBuffer sb = new StringBuffer();
		sb.append("\t<cellPair>\n");

		// inputCell
		if (!inputEmpty) {
			sb.append("\t\t");
			sb.append("<inputCell>\n");
			sb.append("\t\t\t");
			sb.append("<expression");
			sb.append(" value=\"");
			sb.append(Util.encodeXML(input));
			sb.append("\"/>\n");
			sb.append("\t\t");
			sb.append("</inputCell>\n");
		}

		// outputCell
		if (!outputEmpty) {
			sb.append("\t\t");
			sb.append("<outputCell>\n");
			sb.append("\t\t\t");
			sb.append("<expression");
			sb.append(" value=\"");
			sb.append(Util.encodeXML(output));
			sb.append("\"/>\n");
			sb.append("\t\t");
			sb.append("</outputCell>\n");
		}
		
		sb.append("\t</cellPair>\n");

		return sb.toString();
	}

	// public void setLineBorderVisible(boolean inV){
	// isLineBorderVisible = inV;
	// }
}
