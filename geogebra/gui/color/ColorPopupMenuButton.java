package geogebra.gui.color;

import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.util.SelectionTable;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.ImageIcon;


public class ColorPopupMenuButton extends PopupMenuButton implements ActionListener{

	private Application app;
	
	public static final int COLORSET_DEFAULT = 0;
	public static final int COLORSET_BGCOLOR = 1;
	private int colorSetType;
	private Color[]  colorSet; 
	private HashMap<Color,Integer> lookupMap; 
	
	private boolean hasSlider;
	private Dimension iconSize;
	
	public ColorPopupMenuButton(Application app, Dimension iconSize, int colorSetType, boolean hasSlider) {
		
		super(app, createDummyIcons(iconSize), -1, 8, iconSize, SelectionTable.MODE_ICON);
		this.app = app;
		this.iconSize = iconSize;
		this.colorSetType = colorSetType;
		this.hasSlider = hasSlider;
		colorSet = getColorArray(colorSetType);
		
		lookupMap = new HashMap<Color,Integer>();
		for(int i = 0; i < colorSet.length; i++)
			lookupMap.put(colorSet[i], i);
		
		setToolTipArray(getToolTipArray());
		
		getMyTable().setUseColorSwatchBorder(true);
		getMySlider().setMinimum(0);
		getMySlider().setMaximum(100);
		getMySlider().setMajorTickSpacing(25);
		getMySlider().setMinorTickSpacing(5);
		setSliderValue(100);
		getMySlider().setVisible(hasSlider);
		
		updateColorTable();	
		addActionListener(this);
		
	}
	
	public void updateColorTable(){
		getMyTable().populateModel(getColorSwatchIcons(colorSet, getSliderValue()/100f, iconSize, colorSetType));
	}
	
	public void actionPerformed(ActionEvent e){
    	if(this.hasSlider) {
    		Integer si = getSelectedIndex();
    		updateColorTable();
    		setSelectedIndex(si);
    	}
    }
	
	
	
	public int getColorIndex(Color color){
		int index = -1;
		
		if(color == null && colorSetType == this.COLORSET_BGCOLOR){
			index = colorSet.length - 1;
			return index;
		}
		
		if(lookupMap.containsKey(color)){
			index = lookupMap.get(color);
		}
		
		return index;
	}
	
	
	public Color getSelectedColor(){
		
		int index = getSelectedIndex();
		if(index == -1 || (colorSetType == COLORSET_BGCOLOR && index == colorSet.length-1))
			return null;
		else
			return colorSet[index];
	}

	
	private  Color[] getColorArray(int colorSetType){
		return GeoGebraColorConstants.getPopupArray(colorSetType);
	}
	
	
	private  Color[] getColorArray2(int colorSetType) {
		
		Color[] colors = new Color[24];
		HashMap<String, Color> hm = app.getColorsHashMap();
		
		
		/*
		gray=Gray
		darkgray=Dark Gray
		lightgray=Light Gray
		silver=Silver
		
		aqua=Aqua	
		maroon=Maroon
		white=White
		black=Black
		brown=Brown
		crimson=Crimson
		indigo=Indigo
		violet=Violet
		turquoise=Turquoise
*/
		
		Color[]	primaryColors = new Color[] {		
				hm.get("RED"), //red=Red
				hm.get("DARKORANGE"), //orange=Orange? 
				hm.get("YELLOW"),  // yellow=Yellow
				hm.get("LIME"), // green=Green
				hm.get("CYAN"), // cyan=Cyan
				hm.get("BLUE"), // blue=Blue
				hm.get("BLUEVIOLET"), // ?purple=Purple
				hm.get("MAGENTA"), // magenta=Magenta
		};
		
		Color[]	lightColors = new Color[] {		
				hm.get("MISTYROSE"),   
				hm.get("NAVAJOWHITE"),  // 
				hm.get("PALEGOLDENROD"),  // gold=Gold
				hm.get("LIGHTGREEN"),  
				hm.get("LIGHTCYAN"),
				hm.get("LIGHTSKYBLUE"),
				hm.get("ORCHID"),
				hm.get("PINK"),		// pink=Pink 	
		};
		
		//TODO assign better dark colors
		Color[]	darkColors = new Color[] {		
				hm.get("BROWN"),
				hm.get("SIENNA"),
				hm.get("GOLD"),
				hm.get("GREEN"),  
				hm.get("LIGHTSEAGREEN"),  // lime=Lime
				hm.get("NAVY"),   // darkblue=Dark Blue
				hm.get("DARKORCHID"),
				hm.get("PLUM"),
		};		
		
		for(int i = 0; i< 8; i++){
			
			// first row: primary colors
			colors[i] = primaryColors[i];
			
			
			if(colorSetType == COLORSET_DEFAULT){
				
				// second row: darker primary colors
				colors[i+8] = darkColors[i];	
				// third row: 8 gray scales (white ==> black)
				float p = 1.0f - i/7f;
				colors[i+16] = new Color(p,p,p);
				
			}	
			
			if(colorSetType == COLORSET_BGCOLOR){	
				// second row: pale colors for backgrounds
				colors[i+8] = lightColors[i];
				// third row: 7 gray scales (white ==> black)
				if(i<7){
					float p = 1.0f - i/6f;
					colors[i+16] = new Color(p,p,p);
				}else{
					colors[i+16] = Color.white; // this will hold an empty set icon
				}
			}
			
		}

		return colors;
		
	}
	
	private String[] getToolTipArray(){
		String[] toolTipArray = new String[colorSet.length];
		for(int i=0; i<toolTipArray.length; i++){			
			toolTipArray[i] = GeoGebraColorConstants.getGeogebraColorName(app, colorSet[i]);
		}
		return toolTipArray;
	}
	
	
	
	private ImageIcon[] getColorSwatchIcons(Color[] colorArray, float alpha, Dimension iconSize, int colorSetType){
		
		ImageIcon[] a = new ImageIcon[colorArray.length];
		for(int i = 0; i < colorArray.length; i++)
			a[i] = GeoGebraIcon.createColorSwatchIcon( alpha,  iconSize, colorArray[i] , null);
		
		if(colorSetType == COLORSET_BGCOLOR){
			a[colorArray.length-1] = GeoGebraIcon.createNullSymbolIcon(iconSize.width, iconSize.height);
		}
		return a;
	}
	
	private static  ImageIcon[] createDummyIcons( Dimension iconSize){
		
		ImageIcon[] a = new ImageIcon[24];
		for(int i = 0; i < 24; i++)
			a[i] = GeoGebraIcon.createEmptyIcon(iconSize.width, iconSize.height);

		return a;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
