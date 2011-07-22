package geogebra.gui.util;


import geogebra.main.Application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PopupMenuButtonMultiple extends PopupMenuButton{

	public PopupMenuButtonMultiple(Application app, Object[] data, Integer rows, Integer columns, Dimension iconSize, Integer mode){
		super( app, data, rows, columns, iconSize, mode,  true,  false);
		myTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}
	
	public int getSelectedIndex() {
		return myTable.getSelectedIndex();
	}

	public Object getSelectedValue() {
		return myTable.getSelectedValue();
	}
	
	
	public void setSelectedIndex(Integer selectedIndex) {
		
		if(selectedIndex == null)
			selectedIndex = -1;

		myTable.addSelectedIndex(selectedIndex);
		updateGUI();
	}

	public void removeRowSelectionInterval(int index0, int index1){
		myTable.removeRowSelectionInterval(index0, index1);
	}

}		

