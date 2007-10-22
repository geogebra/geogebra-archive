package geogebra.cas.view;

import java.util.ArrayList;
import java.util.Arrays;

import geogebra.cas.view.GeoTDS;

/**
 * @author James King
 * 
 * This class is for the CAS Command Object -- it manages such functions such as:
 * split(); <-- splits incoming strings into separate commands
 * parse(); <-- parses commands and formats their text using GeoTDS.
 * execute(); <-- runs the command object
 * set(); <-- changes the initial 
 * 
 * for reference,
 * regexp: "([^"\\]|\\.)*"
 * matches all strings...
 */

public class CASCommandObj {
	private ArrayList commands;
	private ArrayList responses;
	private String commandString;
	private CASSession parentSession;
	
	public CASCommandObj(CASSession parentSession)
	{
		this.parentSession = parentSession;
		//do nothing?
		commands = new ArrayList();
		responses = new ArrayList();
		//commandString = new String(); dont need this
	}
	
	public CASCommandObj(CASSession parentSession, String tCommand)
	{
		//do nothing?
		commands = new ArrayList();
		responses = new ArrayList();
		commandString = tCommand;
		split(tCommand); 	// split it into separate commands
		parse();			// parse our commands (update formatting, etc..)
	}
	
	public String get()
	{
		return (String) get(true, true);
	}
	
	public String get(boolean command)
	{
		return (String) get(command, true);
	}
	
	public Object get(boolean command, boolean string)
	{
		if (command)
		{
			if (string) return commandString;
			return commands;
		} else {
			if (!string) return responses;
			String retstr = responses.toString();
			if (retstr.length() == 2) retstr = "";
			if (retstr.length() > 2) retstr.substring(1, retstr.length() - 2);
			// TODO: Note that this returns the responses as:
			// response1,response2,response3 instead of using \n!
			return retstr;
		}
	}
	
	public void set(String tCommand)
	{
		split(tCommand);
		parse();
	}
	
	public void split(String tCommand)
	{
		//Split, add commands.
		commands.clear();
		commands.addAll(Arrays.asList(commandString.split(";")));
	}
	
	public void execute()
	{
		for (int i = 0; i < commands.size(); i++)
		responses.add(parentSession.evaluate(((GeoTDS) commands.get(i)).get()));
		// evaluate all commands and send them to the responses list.
	}
	
	public void parse()
	{
		//do color and formatting...
		for (int i = 0; i < commands.size(); i++)
		{
			GeoTDS pCommand = new GeoTDS((String) commands.get(i));
			//apply styles command here.
			commands.set(i, pCommand);
		}
		for (int i = 0; i < responses.size(); i++)
		{
			GeoTDS pResponse = new GeoTDS((String) responses.get(i));
			//apply styles command here.
			responses.set(i, pResponse);
		}
	}
}
