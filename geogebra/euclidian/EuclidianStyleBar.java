package geogebra.euclidian;

import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.PopupMenuButton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JToggleButton;
import javax.swing.JToolBar;

public class EuclidianStyleBar extends JToolBar implements ActionListener {
	

	private JToggleButton btnShowGrid, btnShowAxes, btnVisualMode;
	private PopupMenuButton btnColor, btnLineStyle, btnSize;
	
	private EuclidianController ec;
	private EuclidianView ev;
	private Integer[] lineStyleArray = {
			EuclidianView.LINE_TYPE_FULL,
			EuclidianView.LINE_TYPE_DASHED_DOTTED,
			EuclidianView.LINE_TYPE_DASHED_LONG, 
			EuclidianView.LINE_TYPE_DASHED_SHORT,
			EuclidianView.LINE_TYPE_DOTTED};
	
	//do we need this??
	private int maxIconHeight = 16;
	
	private Color defaultBackground;
	
	
	public EuclidianStyleBar(EuclidianView ev) {
		
		this.ev = ev;
		ec = ev.getEuclidianController(); 
			
		setFloatable(false);
		defaultBackground = this.getBackground();
		
		initGUI();

	}
		
	
	private void initGUI() {
	
		btnVisualMode = new JToggleButton(ev.getApplication().getImageIcon("magnet.gif"));
		btnVisualMode.addActionListener(this);
		btnVisualMode.setSelected(ev.getMode()==EuclidianView.MODE_VISUAL_STYLE);
		add(btnVisualMode);
		
		btnShowAxes = new JToggleButton(ev.getApplication().getImageIcon("axes.gif"));
		btnShowAxes.addActionListener(this);
		btnShowAxes.setSelected(ev.getShowXaxis());
		add(btnShowAxes);
		
		btnShowGrid = new JToggleButton(ev.getApplication().getImageIcon("grid.gif"));
		btnShowGrid.addActionListener(this);
		btnShowGrid.setSelected(ev.getShowGrid());
		add(btnShowGrid);
		
		
		this.addSeparator();
		
		// color menu
		btnColor = new PopupMenuButton(ev.getApplication(), getStyleBarColors(), -1,8,new Dimension(20,maxIconHeight), GeoGebraIcon.MODE_COLOR_SWATCH);		
		btnColor.getMySlider().setMinimum(0);
		btnColor.getMySlider().setMaximum(100);
		btnColor.getMySlider().setMajorTickSpacing(25);
		btnColor.getMySlider().setMinorTickSpacing(5);
		btnColor.setSliderValue(50);
		btnColor.setSelectedIndex(0);
		btnColor.addActionListener(this);
		add(btnColor);
		
		// line style 
		btnLineStyle = new PopupMenuButton(ev.getApplication(), lineStyleArray, -1,1,new Dimension(60,maxIconHeight), GeoGebraIcon.MODE_LINE);
		btnLineStyle.setSelectedIndex(0);
		btnLineStyle.addActionListener(this);
		add(btnLineStyle);
		
		// size (lines and points ???)
		btnSize = new PopupMenuButton(ev.getApplication(), null,null,null,new Dimension(20,maxIconHeight), GeoGebraIcon.MODE_SLIDER);
		//btnSize.getMySlider().setOrientation(JSlider.VERTICAL);
		btnSize.getMySlider().setMinimum(1);
		btnSize.getMySlider().setMaximum(13);
		btnSize.getMySlider().setMajorTickSpacing(2);
		btnSize.getMySlider().setMinorTickSpacing(1);
		//btnSize.getMySlider().setPaintLabels(true);
		btnSize.getMySlider().setPaintTicks(true);
		btnSize.getMySlider().setValue(1);	
		btnSize.addActionListener(this);
		add(btnSize);
		
		
	}


	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		if (source.equals(btnVisualMode)) {		
			if(btnVisualMode.isSelected())
				ev.setMode(EuclidianView.MODE_VISUAL_STYLE);
			else
				ev.getApplication().setMoveMode();
		}
		
		
		if (source.equals(btnShowAxes)) {		
			ev.setShowAxes(!ev.getShowXaxis(), true);
			ev.repaint();
		}
		
		else if (source.equals(btnShowGrid)) {
			ev.showGrid(!ev.getShowGrid());
			ev.repaint();
		}

		else if (source == btnColor) {
			if(btnColor.getSelectedValue() != null){
				ec.setColor((Color) btnColor.getSelectedValue());
				ec.setAlpha(btnColor.getSliderValue() / 100.0f);
			}
		}
		
		else if (source == btnLineStyle) {
			if(btnLineStyle.getSelectedValue() != null){
				ec.setLineStyle((Integer) btnLineStyle.getSelectedValue());
			//	ec.setSize(btnLineStyle.getSliderValue());
			}
		}
		
		
		else if (source == btnSize) {
				ec.setSize(btnSize.getSliderValue());
		}
		
		updateGUI();
	}

	private void updateGUI(){
		
		if(btnVisualMode.isSelected())
			this.setBackground(defaultBackground.brighter());
			//this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		else
			this.setBackground(defaultBackground);
			//this.setBorder(BorderFactory.createEmptyBorder());
		
		
		btnShowAxes.removeActionListener(this);
		btnShowAxes.setSelected(ev.getShowXaxis());
		btnShowAxes.addActionListener(this);
		
		btnShowGrid.removeActionListener(this);
		btnShowGrid.setSelected(ev.getShowGrid());
		btnShowGrid.addActionListener(this);
		
	}
	

	
	
	private Color[] getStyleBarColors() {
		
		Color[]	primaryColors = new Color[] {		
				new Color(255, 0, 0), // Red
				new Color(255, 153, 0), // Orange
				new Color(255, 255, 0), // Yellow
				new Color(0, 255, 0), // Green 
				new Color(0, 255, 255), // Cyan 
				new Color(0, 0, 255), // Blue
				new Color(153, 0, 255), // Purple
				new Color(255, 0, 255) // Magenta 
		};
		
		Color[] c = new Color[24];
		for(int i = 0; i< 8; i++){
			
			// first row: primary colors
			c[i] = primaryColors[i];
			
			// second row: modified primary colors
			float[] hsb = Color.RGBtoHSB(c[i].getRed(), c[i].getGreen(), c[i].getBlue(), null); 
			int rgb = Color.HSBtoRGB((float) (.9*hsb[0]), (float) (.5*hsb[1]), (float) (1*hsb[2]));
			c[i+8] = new Color(rgb);
			
			// third row: gray scales (white ==> black)
			float p = 1.0f - i/7f;
			c[i+16] = new Color(p,p,p);
		}
			
		return c;
	
	}
	
	
		

}
