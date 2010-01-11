package geogebra.cas.view;

import geogebra.main.Application;
import geogebra.util.Util;


public class CASTableCellValue {
	private String input, prefix, eval, postfix, output, latex;
	private boolean error = false;
	private boolean allowLaTeX = true;
		
	private String evalCmd;
	private CASView view;

	public CASTableCellValue(CASView view) {
		this.view = view;
		
		input = "";
		output = "";
		prefix = "";
		eval = "";
		postfix = "";
		evalCmd = "";
	}

	public String getInput() {
		return input;
	}

	public String getOutput() {
		return output;
	}
	
	public void setAllowLaTeX(boolean flag) {
		allowLaTeX = flag;
	}
	
	public String getLaTeXOutput() {
		if (error || !allowLaTeX)
			return null;
		else if (latex == null) {
			try {
				latex = view.getCAS().convertGeoGebraToLaTeXString(output);
			} catch (Throwable th) {
				System.err.println("no latex for: " + output);
				latex = "";
			}
		}
		
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
	
	public void setEvalInformation(String prefix, String eval, String postfix) {
		this.prefix = prefix;
		this.eval = eval;
		this.postfix = postfix;
		
		// extract command from eval
		int bracketPos = eval.indexOf('[');
		evalCmd = bracketPos > 0 ? eval.substring(0, bracketPos) : "";
	}
	
	final public String getEvalCommand() {
		return evalCmd;
	}

	public void setOutput(String inValue) {
		setOutput(inValue, false);
	}
	
	public void setOutput(String output, boolean isError) {
		this.output = output;
		error = isError;
		latex = null;
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
		
		StringBuilder sb = new StringBuilder();
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
			sb.append("\"");
			if (error) {
				sb.append(" error=\"true\"");
			}
			sb.append("/>\n");
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
