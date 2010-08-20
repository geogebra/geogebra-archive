package geogebra.euclidian;

import geogebra.gui.color.ColorChooserButton;
import geogebra.gui.view.spreadsheet.CellFormat;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class EuclidianStyleBar extends JToolBar implements ActionListener {
	

	JToggleButton btnShowGrid, btnShowAxes;
	private ColorChooserButton btnBgColor;
	
	EuclidianController ec;
	EuclidianView ev;

	
	public EuclidianStyleBar(EuclidianView ev) {
		this.ev = ev;
		ec = ev.getEuclidianController(); 
		
		
		setFloatable(false);
		initGUI();

	}
		
	
	private void initGUI() {
	
		btnShowAxes = new JToggleButton(ev.getApplication().getImageIcon("axes.gif"));
		btnShowAxes.addActionListener(this);
		btnShowAxes.setSelected(ev.getShowXaxis());
		add(btnShowAxes);
		
		btnShowGrid = new JToggleButton(ev.getApplication().getImageIcon("grid.gif"));
		btnShowGrid.addActionListener(this);
		btnShowGrid.setSelected(ev.getShowGrid());
		add(btnShowGrid);
		
		
		this.addSeparator();
		
		btnBgColor = new ColorChooserButton(ColorChooserButton.MODE_GENERIC);
		btnBgColor.addActionListener(this);
		add(btnBgColor);
		

	}


	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		if (source.equals(btnShowAxes)) {		
			ev.setShowAxes(!ev.getShowXaxis(), true);
			ev.repaint();
		}
		
		else if (source.equals(btnShowGrid)) {
			ev.showGrid(!ev.getShowGrid());
			ev.repaint();
		}
		
		else if (source == btnBgColor) {
			if(btnBgColor.getSelectedColor() != null)
				ec.setColor(btnBgColor.getSelectedColor());
		}
		
	}



}
