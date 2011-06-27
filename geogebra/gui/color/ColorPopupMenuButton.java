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

		super(app, createDummyIcons(iconSize), -1, 9, iconSize, SelectionTable.MODE_ICON);
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
			if(colorArray[i] != null)
				a[i] = GeoGebraIcon.createColorSwatchIcon( alpha,  iconSize, colorArray[i] , null);
			else
				a[i] = GeoGebraIcon.createNullSymbolIcon(iconSize.width, iconSize.height);

		return a;
	}

	private static  ImageIcon[] createDummyIcons( Dimension iconSize){

		ImageIcon[] a = new ImageIcon[27];
		for(int i = 0; i < 27; i++)
			a[i] = GeoGebraIcon.createEmptyIcon(iconSize.width, iconSize.height);

		return a;
	}
























}
