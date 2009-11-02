package geogebra.gui;

import geogebra.kernel.View;

import java.util.LinkedList;

import javax.swing.JComponent;

public interface CasManager extends View {

	public void initCellPairs(LinkedList cellPairList);
	public String getSessionXML();
	public JComponent getCASViewComponent();
	public Object setInputExpression(Object cellValue, String input);
	public Object setOutputExpression(Object cellValue, String output);
	public Object createCellValue();
	public void updateFonts();
	public void attachView();
	public void detachView();
}
