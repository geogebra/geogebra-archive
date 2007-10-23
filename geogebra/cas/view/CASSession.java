package geogebra.cas.view;

import java.util.ArrayList;

import geogebra.cas.GeoGebraCAS;
import geogebra.cas.view.CASCommandObj;

/**
 * @author James King
 * 
 * This class establishes a session within CAS. Sessions should be managed within CASView.?
 */

public class CASSession {
	private ArrayList cnrs;
	//Command aNd Response Sessions
	private String curCommand;
	private int curFocus;
	private GeoGebraCAS ggbCAS;
	
	public CASSession()
	{
		ggbCAS = new GeoGebraCAS();
		cnrs = new ArrayList();
		CASCommandObj tObj = new CASCommandObj(this);
		cnrs.add(tObj);
		setFocus(0);
	}
	
	public String evaluate(String input) {
		return ggbCAS.evaluateYACAS(input);
	}
	
	public String get(int idx, boolean command)
	{
		return ((CASCommandObj) cnrs.get(idx)).get(command);
	}
	
	public String get(int idx)
	{
		return get(idx, true);
	}
	
	private void setFocus(int command)
	{
		curFocus = command;
	}
	
	public void alter(String text)
	{
		alter(curFocus, text);
	}
	
	public void alter(int idx, String text)
	{
		if ((idx + 1) > cnrs.size())
		{
			//append to the end
			//Add new object
			CASCommandObj tObj = new CASCommandObj(this, text);
			cnrs.add(tObj);
			//set focus to the next command..
			setFocus(idx);
		} else {
			CASCommandObj tObj = (CASCommandObj) cnrs.get(idx);
			tObj.set(text);
		}
	}
	
	public void send()
	{
		send(false);
	}
	
	public void send(boolean changeAll)
	{
		/*	1) Send current command string to our history (and update the view)...
		 * 	2) Send current command to our CAS interpreter, which will return:
		 * 		a) Command results
		 * 		b) Updates to the command string (if necessary). (e.g. color changes)
		 * 	3) Update the view again
		 */ 
		CASCommandObj thisCmd = (CASCommandObj) cnrs.get(curFocus);
		thisCmd.execute();
		cnrs.set(curFocus, thisCmd);
		/*// note: we should delete all following commands (since we just changed everything?
		ArrayList tCnrs = new ArrayList();
		tCnrs.addAll(cnrs.subList(0, curFocus));
		cnrs.clear();
		cnrs.addAll(tCnrs);*/
		// ok, we're doing something different -- we're going to run down the list of commands
		// and re-evaluate them
		if (changeAll)
		{
			if (curFocus < (cnrs.size() - 1))
			{
				// we are changing an entry up above some other entries.
				int i = curFocus + 1;
				while (i < cnrs.size())
				{
					CASCommandObj tCmd = (CASCommandObj) cnrs.get(i);
					tCmd.execute();
					i++;
				}
			}
		}
	}
	
	public void sendAll()
	{
		send(true);
	}
	
	public void update()
	{
		CASCommandObj thisCmd = (CASCommandObj) cnrs.get(curFocus);
		thisCmd.set(curCommand);
		cnrs.set(curFocus, thisCmd);
	}
	
	public int count()
	{
		return cnrs.size();
	}
}
