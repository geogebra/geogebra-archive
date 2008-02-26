package geogebra.cas.view;

public class CASTableCellValue {
	private String	command;
	private String	output;
	private boolean	outputAreaInclude;
	private boolean	BBorderInclude;
	
	public CASTableCellValue(){
		command = new String("");
		output = new String("");
		outputAreaInclude = false;
		BBorderInclude = false;
	}
	
	public CASTableCellValue(String inCom, String inOut){
		command = new String(inCom);
		output = new String(inOut);
		outputAreaInclude = false;
		BBorderInclude = false;
	}
	
	public void initialize(){
		outputAreaInclude = false;
		BBorderInclude = false;
	}
	
	public String getCommand(){
		return	command;
	}
	
	public String getOutput(){
		return	output;
	}
	
	public boolean getOutputAreaInclude( ){
		return outputAreaInclude;
	}
	
	public boolean getBBorderInclude( ){
		return BBorderInclude;
	}
	
	public void setCommand(String inValue){
		command = inValue;
	}
	
	public void setOutput(String inValue){
		output = inValue;
	}
	
	public void setOutputAreaInclude(boolean inV){
		outputAreaInclude = inV;
	}
	
	public void setBBorderInclude(boolean inV){
		BBorderInclude = inV;
	}
}
