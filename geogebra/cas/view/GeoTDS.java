package geogebra.cas.view;

import java.util.ArrayList;

import javax.swing.tree.DefaultTreeModel;

/**
 * @author James King
 * 
 * This class is for GeoTDS or GeoGebra Text Data Structure, basically
 * a storage structure for formatted text (e.g. commands) which can easily
 * be turned into an XML-like structure, or rendered into a display.
 */

public class GeoTDS {
	ArrayList contents;
	
	public GeoTDS()
	{
		contents = new ArrayList();
	}

	public GeoTDS(String strText)
	{
		contents = new ArrayList();
		String[] tElement = new String[2];
		tElement[0] = strText;
		tElement[1] = null;
		contents.add(tElement);
	}

	public GeoTDS(String strText, String strParams)
	{
		contents = new ArrayList();
		String[] tElement = new String[2];
		tElement[0] = strText;
		tElement[1] = strParams;
		contents.add(tElement);
	}
	
	public void append(String strText)
	{
		String[] tElement = new String[2];
		tElement[0] = strText;
		tElement[1] = null;
		contents.add(tElement);
	}
	
	public void append(String strText, String strParams)
	{
		String[] tElement = new String[2];
		tElement[0] = strText;
		tElement[1] = strParams;
		contents.add(tElement);
	}
	
	public String get()
	{
		StringBuffer x = new StringBuffer();
		for (int i = 0; i < contents.size(); i++)
		x.append((String) contents.get(i));
		return x.toString();
	}
	
	public void trim()
	{
		//to do
	}
	
	//need some ways of accessing data later..
}
