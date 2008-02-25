package org.freehep.swing;
import java.io.File;
import java.util.Vector;

import javax.swing.filechooser.FileFilter;

/**
 * A FileFilter which accepts any file which is accepted by
 * any of its vector of FileFilters.
 * @author Tony Johnson
 * @version $Id: AllSupportedFileFilter.java,v 1.1 2008-02-25 21:17:48 murkle Exp $
 */
public class AllSupportedFileFilter extends FileFilter
{
	public void add(FileFilter f)
	{
		vector.addElement(f);
	}
	public void remove(FileFilter f)
	{
		vector.remove(f);
	}
	public void reset()
	{
		vector.removeAllElements();
	}
   public boolean accept(File f) 
	{
		for (int i=0; i<vector.size(); i++)
		{
			FileFilter ff = (FileFilter) vector.elementAt(i);
			if (ff.accept(f)) return true;
		}
		return false;
	}
	public String getDescription() 
	{
 	   return "All Supported File Types";
	}
	private Vector vector = new Vector();
}
