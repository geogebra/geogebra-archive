package geogebra;

import java.util.LinkedList;

import javax.swing.JComponent;

public interface CasManager {

	public void initCellPairs(LinkedList cellPairList);
	public String getSessionXML();
	public JComponent getCASViewComponent();
}
