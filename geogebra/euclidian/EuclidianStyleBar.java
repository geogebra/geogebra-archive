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
	

	private JToggleButton btnShowGrid, btnShowAxes, btnMode;
	private PopupMenuButton btnColor, btnLineStyle, btnSize;
	
	private EuclidianController ec;
	private EuclidianView ev;
	private Integer[] lineStyleArray = {
			EuclidianView.LINE_TYPE_FULL,
			EuclidianView.LINE_TYPE_DASHED_DOTTED,
			EuclidianView.LINE_TYPE_DASHED_LONG, 
			EuclidianView.LINE_TYPE_DASHED_SHORT,
			EuclidianView.LINE_TYPE_DOTTED};
	
	
	private int maxIconHeight = 16;	//do we need this??
	private Color defaultBackground;
	
	
	private int mode;
	private MiniStyle penStyle, standardStyle, style;
	
	private boolean isIniting;
	

	public EuclidianStyleBar(EuclidianView ev) {
		
		isIniting = true;
		this.ev = ev;
		ec = ev.getEuclidianController(); 
		
		
		setFloatable(false);
		defaultBackground = this.getBackground();
		
		penStyle = ec.getPenStyle();
		standardStyle = ec.getStandardStyle();
		
		style = ec.getStandardStyle();
		
		initGUI();
		isIniting = false;
		
		setMode(ev.getMode());

	}
		
	

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;	
		

		switch (mode){
		case EuclidianView.MODE_VISUAL_STYLE:
			btnMode.setIcon(ev.getApplication().getImageIcon("magnet.gif"));
			this.btnMode.setVisible(true);
			this.btnShowAxes.setVisible(true);
			this.btnShowGrid.setVisible(true);
			this.setBackground(defaultBackground.brighter());
			//this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			style = ec.getStandardStyle();
			break;
			
		case EuclidianView.MODE_PEN:
			btnMode.setIcon(ev.getApplication().getImageIcon("applications-graphics.png"));
			this.btnMode.setVisible(true);
			this.btnShowAxes.setVisible(false);
			this.btnShowGrid.setVisible(false);
			btnColor.getMySlider().setVisible(false);
			this.setBackground(defaultBackground.brighter());
			style = ec.getPenStyle();
			break;
			
		default:
			this.btnMode.setVisible(false);
			this.btnShowAxes.setVisible(true);
			this.btnShowGrid.setVisible(true);
			this.setBackground(defaultBackground);
			btnColor.getMySlider().setVisible(true);
			style = ec.getStandardStyle();
			//this.setBorder(BorderFactory.createEmptyBorder());
		}
		
		updateStyle();
		updateGUI();
	}
	
	
	
	//=====================================================
	//                   GUI
	//=====================================================
	
	private void initGUI() {
	
		btnMode = new JToggleButton();
		btnMode.addActionListener(this);
		btnMode.setSelected(ev.getMode()==EuclidianView.MODE_VISUAL_STYLE);
		add(btnMode);
		
		btnShowAxes = new JToggleButton(ev.getApplication().getImageIcon("axes.gif"));
		btnShowAxes.addActionListener(this);
		//btnShowAxes.setSelected(ev.getShowXaxis());
		add(btnShowAxes);
		
		btnShowGrid = new JToggleButton(ev.getApplication().getImageIcon("grid.gif"));
		btnShowGrid.addActionListener(this);
		//btnShowGrid.setSelected(ev.getShowGrid());
		add(btnShowGrid);
		
		
		this.addSeparator();
		
		// color menu
		btnColor = new PopupMenuButton(ev.getApplication(), getStyleBarColors(), -1,8,new Dimension(20,maxIconHeight), GeoGebraIcon.MODE_COLOR_SWATCH);		
		btnColor.getMySlider().setMinimum(0);
		btnColor.getMySlider().setMaximum(100);
		btnColor.getMySlider().setMajorTickSpacing(25);
		btnColor.getMySlider().setMinorTickSpacing(5);
		btnColor.setSliderValue(50);
		//btnColor.setSelectedIndex(0);
		btnColor.addActionListener(this);
		add(btnColor);
		
		// line style 
		btnLineStyle = new PopupMenuButton(ev.getApplication(), lineStyleArray, -1,1,new Dimension(60,maxIconHeight), GeoGebraIcon.MODE_LINE);
		//btnLineStyle.setSelectedIndex(0);
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
		//btnSize.getMySlider().setValue(1);	
		btnSize.addActionListener(this);
		add(btnSize);
		
	}

	
	private void updateStyle(){
		
		if(isIniting) return;
		
		btnColor.setSelectedIndex(style.colorIndex);
		btnColor.setSliderValue((int) (style.alpha*100));
		btnLineStyle.setSelectedIndex(style.lineStyle);
		btnSize.getMySlider().setValue(style.lineSize);
		
		
	}
	
	
	private void updateGUI(){

		if(isIniting) return;
		
		btnMode.removeActionListener(this);
		btnMode.setSelected(mode == EuclidianView.MODE_VISUAL_STYLE);
		btnMode.addActionListener(this);
		

		btnShowAxes.removeActionListener(this);
		btnShowAxes.setSelected(ev.getShowXaxis());
		btnShowAxes.addActionListener(this);

		btnShowGrid.removeActionListener(this);
		btnShowGrid.setSelected(ev.getShowGrid());
		btnShowGrid.addActionListener(this);
			
		
	}


	
	
	
	//=====================================================
	//                 Event Handlers
	//=====================================================
	

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		if (source.equals(btnMode)) {		
		//	if(btnVisualMode.isSelected())
		//		ev.getApplication().setMode(EuclidianView.MODE_VISUAL_STYLE);
		//	else
		//		ev.getApplication().setMoveMode();
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
				/*
				style.colorIndex = btnColor.getSelectedIndex();
				style.color = (Color) btnColor.getSelectedValue();;
				style.alpha = btnColor.getSliderValue() / 100.0f;
				ec.setStandardStyle(style);
				ec.applyColor(style);
				ec.applyAlpha(style);
				*/
				
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

	
	
	//TODO: move this to MiniStyle class

	public Color[] getStyleBarColors() {
		
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
