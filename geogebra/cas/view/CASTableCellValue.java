package geogebra.cas.view;

public class CASTableCellValue {
	private String	command;
	private String	output;
	
	public CASTableCellValue(){
		command = new String("");
		output = new String("");
	}
	
	public CASTableCellValue(String inCom, String inOut){
		command = new String(inCom);
		output = new String(inOut);
	}
	
	public String getCommand(){
		return	command;
	}
	
	public String getOutput(){
		return	output;
	}
	
	public void setCommand(String inValue){
		command = inValue;
	}
	
	public void setOutput(String inValue){
		output = inValue;
	}
}
