package geogebra.cas.view;


public class CASTableCellValue {
	private String input;
	private String output;
	private boolean isOutputVisible;
	private boolean error = false;

	// private boolean isLineBorderVisible;

	public CASTableCellValue() {
		input = "";
		output = "";
		isOutputVisible = false;
		// isLineBorderVisible = false;
	}	

	public CASTableCellValue(String inCom) {
		input = new String(inCom);
		output = new String("");
		isOutputVisible = false;
		// isLineBorderVisible = false;
	}

	public void initialize() {
		isOutputVisible = false;
		// isLineBorderVisible = false;
	}

	public String getInput() {
		return input;
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

	public void setInput(String inValue) {
		input = inValue;
	}

	public void setOutput(String inValue) {
		setOutput(inValue, false);
	}
	
	public void setOutput(String inValue, boolean isError) {
		output = inValue;
		error = true;
	}
	
	public boolean isOutputError() {
		return error;
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
		sb.append(this.getInput());
		sb.append("\"/>\n");
//		sb.append("\t\t\t");
//		sb.append("<color r=\"0\" g=\"1\" b=\"0\"/>\n");
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
//			sb.append("\t\t\t");
//			sb.append("<color r=\"0\" g=\"1\" b=\"0\"/>\n");
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
