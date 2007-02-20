package sharptools;
/*
 * @(#)SharpTools.java
 * 
 * $Id: Config.java,v 1.1 2007-02-20 13:58:22 hohenwarter Exp $
 * 
 * Created on October 10, 2000, 1:15 AM
 */

import java.util.*;
import java.io.*;

/**
 * This class reads configuration file and provides get functions for
 * other classes to retrieve the information.
 *
 * This provides a central repository for global information.
 *
 * Note, Config only saves a pair when the key is already in the config file.
 * This is very important!!!
 * 
 * @author  Hua Zhong <huaz@cs.columbia.edu>
 * @version $Revision: 1.1 $
 */

final class Config {

    private HashMap map;
    private File file;
    private boolean modified;
    
    Config(String filename) {
	modified = false;
	map = new HashMap();
	file = new File(filename);
    }

    // load from file
    public void load() {
	Debug.println("Loading configuration file...");
    	try {
	    // open the file
	    BufferedReader in = new BufferedReader
		(new FileReader(file));
	    String line;
	    while ((line = in.readLine()) != null) {

		// convert to <key,value> pair
		String[] pair = getPair(line);
		
		if (pair == null)
		    continue;

		// key must not be empty
		if (pair[0] == null || pair[0].length() == 0)
		    continue;

		map.put(pair[0], pair[1]);
	    }
	}
	catch (IOException e) {
	}
	catch (Exception e) {
	    System.err.println("error parsing config file: "+e);
	}

	modified = false;
	//Debug.println(map);
    }

    // save to file
    public void save() {
	if (!modified)
	    return;
	
	Debug.println("Saving configuration file...");
	String tmpfilename = file+".tmp";
	File tmp = new File(tmpfilename);
	
    	try {
	    // open the file
	    BufferedReader in = new BufferedReader
		(new FileReader(file));
	    PrintWriter out = new PrintWriter
		(new BufferedWriter
		    (new FileWriter(tmp)));
	    
	    String line;
	    while ((line = in.readLine()) != null) {
		
		String[] pair = getPair(line.toUpperCase());

		if (pair != null && pair[0] != null &&
		    map.get(pair[0]) != null)
		    out.println(pair[0]+'='+map.get(pair[0]));
		else
		    out.println(line);

	    }

	    in.close();
	    out.close();
	}
	catch (IOException e) {
	    System.err.println("io error saving config file: "+e);
	    return;
	}
	catch (Exception e) {
	    System.err.println("error saving config file: "+e);
	    return;
	}

	// move tmp file bak
	file.delete();
	tmp.renameTo(file);
	modified = false;
    }
    
    /**
     * If a string is of form "Str1=Str2", return a pair
     *
     * @param line input string
     * @return a pair of (key, value); null if it's not a pair
     */
    private String[] getPair(String line) {
	
	int index = line.indexOf('=');
	if (index < 0)
	    return null;

	// ignore comments
	if (line.startsWith("#") || line.startsWith(";"))
	    return null;
	
	String[] pair = new String[2];
	pair[0] = line.substring(0, index).trim();
	pair[1] = line.substring(index+1).trim();
	return pair;
	
    }

    /**
     * get value by string name
     *
     * @param key the variable name
     * @return the value as a string
     */
    public String get(String key) {
	return (String)map.get(key);
    }

    /**
     * get integer value by string name
     *
     * @param key the variable name
     * @return the value as an integer; -1 if not available
     */
    public int getInt(String key) {
	String value = (String)map.get(key);
	if (value == null)
	    return -1;

	int intValue = -1;
	try {
	    intValue = Integer.parseInt(value);
	}
	catch (NumberFormatException e) {
	    //	    Debug.println("Return -1 for "+key+";"+value);
	    return -1;
	}

	return intValue;
    }

    /**
     * get boolean value by string name
     *
     * @param key the variable name
     * @return the value as boolean; FALSE by default
     */
    public boolean getBoolean(String key) {
	String value = (String)map.get(key);
	if (value == null)
	    return false;

	return value.equals("TRUE");
    }

    
    /**
     * set value by string name
     *
     * @param key the variable name
     * @param the value as a string
     */
    public void set(String key, String value) {
	modified = true;
	map.put(key, value);
    }

    /**
     * set integer value by string name
     *
     * @param key the variable name
     * @param the value as an integer
     */
    public void setInt(String key, int value) {
	set(key, String.valueOf(value));
    }

    /**
     * set boolean value by string name
     *
     * @param key the variable name     
     * @param value the boolean value
     */
    public void setBoolean(String key, boolean value) {
	set(key, value?"TRUE":"FALSE");
    }    
}











