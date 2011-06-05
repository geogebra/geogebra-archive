package geogebra.gui;

import geogebra.kernel.View;

import java.awt.print.Printable;
import java.util.LinkedList;

import javax.swing.JComponent;

public interface CasManager extends View, Printable {

	public void getSessionXML(StringBuilder sb);
	public JComponent getCASViewComponent();
	public Object createRow();
	public void updateFonts();
	public void attachView();
	public void detachView();
	public void setLabels();
}
