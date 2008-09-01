package geogebra.cas.view;

import geogebra.util.Util;

public class CASTableCellValue {
	private String command;
	private String output;
	private boolean isOutputVisible;

	// private boolean isLineBorderVisible;

	public CASTableCellValue() {
		command = new String("");
		output = new String("");
		isOutputVisible = false;
		// isLineBorderVisible = false;
	}

	public CASTableCellValue(String inCom, String inOut) {
		command = new String(inCom);
		output = new String(inOut);
		isOutputVisible = false;
		// isLineBorderVisible = false;
	}

	public CASTableCellValue(String inCom) {
		command = new String(inCom);
		output = new String("");
		isOutputVisible = false;
		// isLineBorderVisible = false;
	}

	public void initialize() {
		isOutputVisible = false;
		// isLineBorderVisible = false;
	}

	public String getCommand() {
		return command;
	}

	public String getOutput() {
		return output;
	}

	public boolean isOutputVisible() {
		return isOutputVisible;
	}

	// public boolean isLineBorderVisible( ){
	// return isLineBorderVisible;
	// }

	public void setCommand(String inValue) {
		command = inValue;
	}

	public void setOutput(String inValue) {
		output = inValue;
	}

	public void setOutputAreaInclude(boolean inV) {
		isOutputVisible = inV;
	}

	// generate the XML file for this CASTableCellValue
	public String getXML() {
		StringBuffer sb = new StringBuffer();
		sb.append("\t<cellPair>\n");

		// inputCell
		sb.append("\t\t");
		sb.append("<inputCell>\n");
		sb.append("\t\t\t");
		sb.append("<expression");
		sb.append(" value=\"");
		sb.append(this.getCommand());
		sb.append("\"/>\n");
		sb.append("\t\t\t");
		sb.append("<color r=\"0\" g=\"1\" b=\"0\"/>\n");
		sb.append("\t\t");
		sb.append("</inputCell>\n");

		// outputCell
		if (isOutputVisible) {
			sb.append("\t\t");
			sb.append("<outputCell>\n");
			sb.append("\t\t\t");
			sb.append("<expression");
			sb.append(" value=\"");
			sb.append(this.getOutput());
			sb.append("\"/>\n");
			sb.append("\t\t\t");
			sb.append("<color r=\"0\" g=\"1\" b=\"0\"/>\n");
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
