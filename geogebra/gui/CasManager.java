package geogebra.gui;

import geogebra.kernel.View;

import java.util.LinkedList;

import javax.swing.JComponent;

public interface CasManager extends View {

	public String getSessionXML();
	public JComponent getCASViewComponent();
	public Object createRow();
	public void updateFonts();
	public void attachView();
	public void detachView();
}
