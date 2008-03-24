package geogebra.cas.view;

public class CASTableCellValue {
	private String	command;
	private String	output;
	private boolean	isOutputVisible;
	//private boolean	isLineBorderVisible;
	
	public CASTableCellValue(){
		command = new String("");
		output = new String("");
		isOutputVisible = false;
		//isLineBorderVisible = false;
	}
	
	public CASTableCellValue(String inCom, String inOut){
		command = new String(inCom);
		output = new String(inOut);
		isOutputVisible = false;
		//isLineBorderVisible = false;
	}
	
	public CASTableCellValue(String inCom){
		command = new String(inCom);
		output = new String("");
		isOutputVisible = false;
		//isLineBorderVisible = false;
	}
	
	public void initialize(){
		isOutputVisible = false;
		//isLineBorderVisible = false;
	}
	
	public String getCommand(){
		return	command;
	}
	
	public String getOutput(){
		return	output;
	}
	
	public boolean isOutputVisible( ){
		return isOutputVisible;
	}
	
//	public boolean isLineBorderVisible( ){
//		return isLineBorderVisible;
//	}
	
	public void setCommand(String inValue){
		command = inValue;
	}
	
	public void setOutput(String inValue){
		output = inValue;
	}
	
	public void setOutputAreaInclude(boolean inV){
		isOutputVisible = inV;
	}
	
//	public void setLineBorderVisible(boolean inV){
//		isLineBorderVisible = inV;
//	}
}
