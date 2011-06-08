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

	
	private  Color[] getColorArray(int colorSetType) {
		
		Color[] colors = new Color[24];
		
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
		
		Color[]	lightColors = new Color[] {		
				new Color(255,228,225),	//Misty Rose
				new Color(255, 204, 153), // Tan
				new Color(255, 255, 153), // Light Yellow
				new Color(204, 255, 204), // Light Green
				new Color(204, 255, 255), // Light Turquoise
				new Color(153, 204, 255), // Pale Blue
				new Color(204, 153, 255), // Lavender
				new Color(255,153,204),	//Rose		 	
		};
		
		//TODO assign better dark colors
		Color[]	darkColors = new Color[] {		
				primaryColors[0].darker(),	
				new Color(153, 51, 0), // orange-brown default fill color	
				primaryColors[2].darker(),	
				primaryColors[3].darker(),	
				primaryColors[4].darker(),	
				primaryColors[5].darker(),	
				primaryColors[6].darker(),	
				primaryColors[7].darker(),	
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
