package geogebra;

import java.util.HashMap;

/**
 * Class to parse command line arguments. A list of possible arguments
 * for GeoGebra is available online at http://www.geogebra.org/wiki.
 * 
 * Arguments are accepted in the following format:
 * 	  --key1=value1 --key2=value2 ... file
 * 
 * The last argument "file" has no "--key=" prefix and
 * specifies the file to load. The value of this argument
 * is stored with "file" as the key.
 * 
 * If no value is specified (ie "--key=" or "--key") an empty string is
 * regarded as value.
 */
public class CommandLineArguments {
	/**
	 * Hash map to store the options.
	 */
	private HashMap<String, String> args;
	
	/**
	 * Parse the argument array created by Java.
	 * 
	 * @param cmdArgs
	 */
	public CommandLineArguments(String[] cmdArgs) {
		args = new HashMap<String, String>();
		
		if(cmdArgs == null)
			return;
		
		// loop through arguments
		for(int i = 0; i < cmdArgs.length; ++i) {
			// check if argument has the required "--" prefix
			if(cmdArgs[i].startsWith("--")) {
				int equalSignIndex = cmdArgs[i].lastIndexOf('=');
				
				if(equalSignIndex != -1) {
					args.put(
						cmdArgs[i].substring(2, equalSignIndex),
						cmdArgs[i].substring(equalSignIndex+1)
					);
				} else {
					args.put(cmdArgs[i].substring(2), "");
				}
			} else {
				// this is the filename
				if(i == cmdArgs.length - 1) {
					args.put("file", cmdArgs[i]);
				}
			}
		}
	}
	
	/**
	 * Returns the string value of the requested argument.
	 * 
	 * @param name
	 * @return The string value of the specified argument (or empty string)
	 */
	public String getStringValue(String name) {
		String strValue = args.get(name);
		return (strValue == null ? "" : strValue);
	}
	
	/**
	 * Returns the boolean value of the requested argument.
	 * 
	 * @param name
	 * @param defaultValue
	 * @return The boolean value or "default" in case this argument
	 * 		is missing or has an invalid format. 
	 */
	public boolean getBooleanValue(String name, boolean defaultValue) {
		String strValue = args.get(name);
		
		if(strValue == null || !isBoolean(name)) {
			return defaultValue;
		} else {
			return strValue.toLowerCase().equals("true");
		}
	}
	
	/**
	 * Check if the requested argument is a boolean ie the value is
	 * "true" or "false" (lettercase ignored).
	 * 
	 * @param name
	 * @return
	 */
	public boolean isBoolean(String name) {
		String strValue = args.get(name);
		
		if(strValue == null) {
			return false;
		} else {
			strValue = strValue.toLowerCase();
			return strValue.equals("true") || strValue.equals("false");
		}
	}
	
	public boolean containsArg(String name) {
		return args.containsKey(name);
	}
}
