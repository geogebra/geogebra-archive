package geogebra.gui.color;

import geogebra.gui.util.GeoGebraIcon;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.color.ColorSpace;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;



public class GeoGebraColorConstants {

	public static final int COLORSET_STANDARD = 0;
	public static final int COLORSET_BGCOLOR = 1;


	private static final HashMap<String, Color> geogebraColor = new HashMap<String, Color>();
	static
	{
		// primary
		geogebraColor.put("red", new Color(255,0,0));
		geogebraColor.put("orange", new Color(255,127,0));
		geogebraColor.put("yellow", new Color(255,255,0));		
		geogebraColor.put("green", new Color(0,255,0));
		geogebraColor.put("cyan", new Color(0,255,255));
		geogebraColor.put("blue", new Color(0,0,255));
		geogebraColor.put("violet", new Color(127,0,255));
		geogebraColor.put("magenta", new Color(255,0,255));

		// light primary
		geogebraColor.put("pink", new Color(255,192,203));
		geogebraColor.put("lightorange", new Color(255, 239, 213)); // wikipedia "papaya whip"
		geogebraColor.put("lightyellow", new Color(255, 250, 205)); // wikipedia "lemon chiffon"
		geogebraColor.put("lime", new Color(191,255,0));
		geogebraColor.put("aqua", new Color(188, 212, 230));  // wikipedia "pale aqua" 
		geogebraColor.put("lightpurple", new Color(204, 204, 255));  // wikipedia "periwinkle"
		geogebraColor.put("lightviolet", new Color(224, 176, 255));  // wikipedia "mauve"
		geogebraColor.put("turquoise", new Color(175, 238, 238)); // wikiopedia "pale turquoise"


		// dark primary
		geogebraColor.put("maroon", new Color(128, 0, 0)); 
		geogebraColor.put("brown", new Color(150, 75, 0));
		geogebraColor.put("gold",  new Color(255, 215, 0));   
		geogebraColor.put("darkgreen",  new Color(0, 100, 0));   
		geogebraColor.put("darkblue", new Color(28, 57, 187));  // wikipedia "persian blue"
		geogebraColor.put("indigo", new Color(75,0,130));
		geogebraColor.put("purple", new Color(128,0,128));
		geogebraColor.put("crimson", new Color(220,20,60));


		// white/gray/black
		geogebraColor.put("white", htmlColorMap().get("WHITE"));
		geogebraColor.put("black", htmlColorMap().get("BLACK"));
		geogebraColor.put("gray7", grayN(7));
		geogebraColor.put("gray6", grayN(6)); // silver
		geogebraColor.put("gray5", grayN(5));
		geogebraColor.put("gray4", grayN(4));
		geogebraColor.put("gray3", grayN(3));
		geogebraColor.put("gray2", grayN(2));
		geogebraColor.put("gray1", grayN(1));
		geogebraColor.put("darkgray", htmlColorMap().get("DARKGRAY"));
		geogebraColor.put("lightgray", htmlColorMap().get("LIGHTGRAY"));
		geogebraColor.put("silver", htmlColorMap().get("SILVER"));

	}


	private static final HashMap<Color, String> geogebraColorReverse = new HashMap<Color, String>();
	static
	{	for (Entry<String, Color> entry : geogebraColor.entrySet())	
		geogebraColorReverse.put(entry.getValue(), entry.getKey());
	}

	public static Color getGeogebraColor(Application app, String colorName){
		
		Color ret = geogebraColor.get(colorName);
		
		if (ret == null)
			ret = geogebraColor.get(app.reverseGetColor(colorName));
		
		return ret;
	}

	public static String getGeogebraColorName(Application app, Color color){
		return app.getColor(geogebraColorReverse.get(color));
	}


	private static Color grayN(int n){
		int grayN = 256 - 32 * n;
		return new Color(grayN, grayN, grayN);
	}



	public static Color[] primaryColors = new Color[9];
	static{
		primaryColors[0] = geogebraColor.get("red");
		primaryColors[1] = geogebraColor.get("orange");
		primaryColors[2] = geogebraColor.get("yellow");
		primaryColors[3] = geogebraColor.get("green");
		primaryColors[4] = geogebraColor.get("cyan");
		primaryColors[5] = geogebraColor.get("blue");
		primaryColors[6] = geogebraColor.get("violet");
		primaryColors[7] = geogebraColor.get("magenta");
		primaryColors[8] = null;
	}


	public static Color[] lightPrimaryColors = new Color[9];
	static{
		lightPrimaryColors[0] = geogebraColor.get("pink");
		lightPrimaryColors[1] = geogebraColor.get("lightorange");
		lightPrimaryColors[2] = geogebraColor.get("lightyellow");
		lightPrimaryColors[3] = geogebraColor.get("lime");
		lightPrimaryColors[5] = geogebraColor.get("turquoise");
		lightPrimaryColors[4] = geogebraColor.get("aqua");
		lightPrimaryColors[6] = geogebraColor.get("lightpurple");
		lightPrimaryColors[7] = geogebraColor.get("lightviolet");
		lightPrimaryColors[8] = null;
	}


	public static Color[] darkPrimaryColors = new Color[9];
	static{
		darkPrimaryColors[0] = geogebraColor.get("maroon");
		darkPrimaryColors[1] = geogebraColor.get("brown");
		darkPrimaryColors[2] = geogebraColor.get("gold");
		darkPrimaryColors[3] = geogebraColor.get("darkgreen");
		darkPrimaryColors[4] = geogebraColor.get("darkblue");
		darkPrimaryColors[5] = geogebraColor.get("purple");
		darkPrimaryColors[6] = geogebraColor.get("indigo");
		darkPrimaryColors[7] = geogebraColor.get("crimson");
		darkPrimaryColors[8] = null;
	}


	public static Color[] grayColors = new Color[9];
	static{
		grayColors[0] = geogebraColor.get("white");
		grayColors[1] = grayN(1);
		grayColors[2] = grayN(2);
		grayColors[3] = grayN(3);
		grayColors[4] = grayN(4);
		grayColors[5] = grayN(5);
		grayColors[6] = grayN(6);
		grayColors[7] = grayN(7);
		grayColors[8] = geogebraColor.get("black");;
	}



	/**
	 * Returns array of colors for color popup menus
	 * @param colorSetType
	 * @return
	 */
	public static Color[] getPopupArray(int colorSetType) {

		Color[] colors = new Color[27];
		HashMap<String, Color> hm = htmlColorMap();

		for(int i = 0; i< 9; i++){
			// first row
			colors[i] = primaryColors[i];

			// second row
			if(colorSetType == COLORSET_STANDARD)
				colors[i+9] = darkPrimaryColors[i];
			else
				colors[i+9] = lightPrimaryColors[i];

			// third row
			colors[i+18] =grayColors[i];
		}	

		return colors;	
	}

	/**
	 * Returns array of localized color names 
	 * @param app
	 * @param color
	 * @return
	 */
	public static String[] getColorNames(Application app, Color[] color){
		String[] s = new String[color.length];
		for(int i=0; i<s.length; i++){
			s[i] =  getGeogebraColorName(app, color[i]);
		}
		return s;
	}



	public static final HashMap<String, Color> htmlColorMap() {

		HashMap<String, Color> colors = new HashMap<String, Color>();

		// HTML 3.2
		colors.put("AQUA", new Color(0x00FFFF));
		colors.put("BLACK", new Color(0x000000));
		colors.put("BLUE", new Color(0x0000FF));
		colors.put("FUCHSIA", new Color(0xFF00FF));
		colors.put("GRAY", new Color(0x808080));
		colors.put("GREEN", new Color(0x008000));
		colors.put("LIME", new Color(0x00FF00));
		colors.put("MAROON", new Color(0x800000));
		colors.put("NAVY", new Color(0x000080));
		colors.put("OLIVE", new Color(0x808000));
		colors.put("PURPLE", new Color(0x800080));
		colors.put("RED", new Color(0xFF0000));
		colors.put("SILVER", new Color(0xC0C0C0));
		colors.put("TEAL", new Color(0x008080));
		colors.put("WHITE", new Color(0xFFFFFF));
		colors.put("YELLOW", new Color(0xFFFF00));

		colors.put("ALICEBLUE", new Color(0xEFF7FF));
		colors.put("ANTIQUEWHITE", new Color(0xF9E8D2));
		colors.put("AQUAMARINE", new Color(0x43B7BA));
		colors.put("AZURE", new Color(0xEFFFFF));
		colors.put("BEIGE", new Color(0xF5F3D7));
		colors.put("BISQUE", new Color(0xFDE0BC));
		colors.put("BLANCHEDALMOND", new Color(0xFEE8C6));
		colors.put("BLUEVIOLET", new Color(0x7931DF));
		colors.put("BROWN", new Color(0x980516));
		colors.put("BURLYWOOD", new Color(0xEABE83));
		colors.put("CADETBLUE", new Color(0x578693));
		colors.put("CHARTREUSE", new Color(0x8AFB17));
		colors.put("CHOCOLATE", new Color(0xC85A17));
		colors.put("CORAL", new Color(0xF76541));
		colors.put("CORNFLOWERBLUE", new Color(0x151B8D));
		colors.put("CORNSILK", new Color(0xFFF7D7));
		colors.put("CRIMSON", new Color(0xE41B17));
		colors.put("CYAN", new Color(0x00FFFF));
		colors.put("DARKBLUE", new Color(0x2F2F4F));
		colors.put("DARKCYAN", new Color(0x57FEFF));
		colors.put("DARKGOLDENROD", new Color(0xAF7817));
		colors.put("DARKGRAY", new Color(0x7A7777));
		colors.put("DARKGREEN", new Color(0x254117));
		colors.put("DARKKHAKI", new Color(0xB7AD59));
		colors.put("DARKMAGENTA", new Color(0xF43EFF));
		colors.put("DARKOLIVEGREEN", new Color(0xCCFB5D));
		colors.put("DARKORANGE", new Color(0xF88017));
		colors.put("DARKORCHID", new Color(0x7D1B7E));
		colors.put("DARKRED", new Color(0xE41B17));
		colors.put("DARKSALMON", new Color(0xE18B6B));
		colors.put("DARKSEAGREEN", new Color(0x8BB381));
		colors.put("DARKSLATEBLUE", new Color(0x2B3856));
		colors.put("DARKSLATEGRAY", new Color(0x253856));
		colors.put("DARKTURQUOISE", new Color(0x3B9C9C));
		colors.put("DARKVIOLET", new Color(0x842DCE));
		colors.put("DEEPPINK", new Color(0xF52887));
		colors.put("DEEPSKYBLUE", new Color(0x3BB9FF));
		colors.put("DIMGRAY", new Color(0x463E41));
		colors.put("DODGERBLUE", new Color(0x1589FF));
		colors.put("FIREBRICK", new Color(0x800517));
		colors.put("FLORALWHITE", new Color(0xFFF9EE));
		colors.put("FORESTGREEN", new Color(0x4E9258));
		colors.put("GAINSBORO", new Color(0xD8D9D7));
		colors.put("GHOSTWHITE", new Color(0xF7F7FF));
		colors.put("GOLD", new Color(0xD4A017));
		colors.put("GOLDENROD", new Color(0xEDDA74));
		colors.put("GREENYELLOW", new Color(0xB1FB17));
		colors.put("HONEYDEW", new Color(0xF0FEEE));
		colors.put("INDIANRED", new Color(0x5E2217));
		colors.put("INDIGO", new Color(0x307D7E));
		colors.put("IVORY", new Color(0xFFFFEE));
		colors.put("KHAKI", new Color(0xADA96E));
		colors.put("LAVENDER", new Color(0xE3E4FA));
		colors.put("LAVENDERBLUSH", new Color(0xFDEEF4));
		colors.put("LAWNGREEN", new Color(0x87F717));
		colors.put("LEMONCHIFFON", new Color(0xFFF8C6));
		colors.put("LIGHTBLUE", new Color(0xADDFFF));
		colors.put("LIGHTCORAL", new Color(0xE77471));
		colors.put("LIGHTCYAN", new Color(0xE0FFFF));
		colors.put("LIGHTGOLDENRODYELLOW", new Color(0xFAF8CC));
		colors.put("LIGHTGREEN", new Color(0xCCFFCC));
		colors.put("LIGHTGRAY", Color.LIGHT_GRAY);
		colors.put("LIGHTPINK", new Color(0xFAAFBA));
		colors.put("LIGHTSALMON", new Color(0xF9966B));
		colors.put("LIGHTSEAGREEN", new Color(0x3EA99F));
		colors.put("LIGHTSKYBLUE", new Color(0x82CAFA));
		colors.put("LIGHTSLATEGRAY", new Color(0x6D7B8D));
		colors.put("LIGHTSTEELBLUE", new Color(0x728FCE));
		colors.put("LIGHTYELLOW", new Color(0xFFFEDC));
		colors.put("LIMEGREEN", new Color(0x41A317));
		colors.put("LINEN", new Color(0xF9EEE2));
		colors.put("MAGENTA", new Color(0xFF00FF));
		colors.put("MEDIUMAQUAMARINE", new Color(0x348781));
		colors.put("MEDIUMBLUE", new Color(0x152DC6));
		colors.put("MEDIUMORCHID", new Color(0xB048B5));
		colors.put("MEDIUMPURPLE", new Color(0x8467D7));
		colors.put("MEDIUMSEAGREEN", new Color(0x306754));
		colors.put("MEDIUMSLATEBLUE", new Color(0x5E5A80));
		colors.put("MEDIUMSPRINGGREEN", new Color(0x348017));
		colors.put("MEDIUMTURQUOISE", new Color(0x48CCCD));
		colors.put("MEDIUMVIOLETRED", new Color(0xCA226B));
		colors.put("MIDNIGHTBLUE", new Color(0x151B54));
		colors.put("MINTCREAM", new Color(0xF5FFF9));
		colors.put("MISTYROSE", new Color(0xFDE1DD));
		colors.put("MOCCASIN", new Color(0xFDE0AC));
		colors.put("NAVAJOWHITE", new Color(0xFDDAA3));
		colors.put("OLDLACE", new Color(0xFCF3E2));
		colors.put("OLIVEDRAB", new Color(0x658017));
		colors.put("ORANGE", new Color(0xF87A17));
		colors.put("ORANGERED", new Color(0xF63817));
		colors.put("ORCHID", new Color(0xE57DED));
		colors.put("PALEGOLDENROD", new Color(0xEDE49E));
		colors.put("PALETURQUOISE", new Color(0xAEEBEC));
		colors.put("PALEVIOLETRED", new Color(0xD16587));
		colors.put("PAPAYAWHIP", new Color(0xFEECCF));
		colors.put("PEACHPUFF", new Color(0xFCD5B0));
		colors.put("PERU", new Color(0xC57726));
		colors.put("PINK", new Color(0xFAAFBE));
		colors.put("PLUM", new Color(0xB93B8F));
		colors.put("POWDERBLUE", new Color(0xADDCE3));
		colors.put("ROSYBROWN", new Color(0xB38481));
		colors.put("ROYALBLUE", new Color(0x2B60DE));
		colors.put("SADDLEBROWN", new Color(0xF63526));
		colors.put("SALMON", new Color(0xF88158));
		colors.put("SANDYBROWN", new Color(0xEE9A4D));
		colors.put("SEAGREEN", new Color(0x4E8975));
		colors.put("SEASHELL", new Color(0xFEF3EB));
		colors.put("SIENNA", new Color(0x8A4117));
		colors.put("SKYBLUE", new Color(0x6698FF));
		colors.put("SLATEBLUE", new Color(0x737CA1));
		colors.put("SLATEGRAY", new Color(0x657383));
		colors.put("SNOW", new Color(0xFFF9FA));
		colors.put("SPRINGGREEN", new Color(0x4AA02C));
		colors.put("STEELBLUE", new Color(0x4863A0));
		colors.put("TAN", new Color(0xD8AF79));
		colors.put("THISTLE", new Color(0xD2B9D3));
		colors.put("TOMATO", new Color(0xF75431));
		colors.put("TURQUOISE", new Color(0x43C6DB));
		colors.put("VIOLET", new Color(0x8D38C9));
		colors.put("WHEAT", new Color(0xF3DAA9));
		colors.put("WHITESMOKE", new Color(0xFFFFFF));
		colors.put("YELLOWGREEN", new Color(0x52D017));

		return colors;
	}






	private static void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("FrameDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(600, 600));

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBackground(Color.white);
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));


		ImageIcon ic;
		JLabel lbl;

		p.add(new JLabel(" ")); 
		p.add(new JLabel("============== Primary Colors ========== ")); 

		for (int i = 0; i < primaryColors.length; i++){	
			String text  = geogebraColorReverse.get(primaryColors[i]);
			ic = GeoGebraIcon.createColorSwatchIcon( 1.0f,  new Dimension(64,24), primaryColors[i] , null);
			lbl = new JLabel(text);
			lbl.setIcon(ic);
			p.add(lbl);
		}

		p.add(new JLabel(" ")); 
		p.add(new JLabel("============== Light Primary Colors ========== ")); 

		for (int i = 0; i < lightPrimaryColors.length; i++){	
			String text  = geogebraColorReverse.get(lightPrimaryColors[i]);
			ic = GeoGebraIcon.createColorSwatchIcon( 1.0f,  new Dimension(64,24), lightPrimaryColors[i] , null);
			lbl = new JLabel(text);
			lbl.setIcon(ic);
			p.add(lbl);
		}

		p.add(new JLabel(" ")); 
		p.add(new JLabel("============== Dark Primary Colors ========== ")); 

		for (int i = 0; i < darkPrimaryColors.length; i++){	
			String text  = geogebraColorReverse.get(darkPrimaryColors[i]);
			ic = GeoGebraIcon.createColorSwatchIcon( 1.0f,  new Dimension(64,24), darkPrimaryColors[i] , null);
			lbl = new JLabel(text);
			lbl.setIcon(ic);
			p.add(lbl);
		}

		p.add(new JLabel(" ")); 
		p.add(new JLabel("============== Gray Colors ========== ")); 

		for (int i = 0; i < grayColors.length; i++){	
			String text  = geogebraColorReverse.get(grayColors[i]);
			ic = GeoGebraIcon.createColorSwatchIcon( 1.0f,  new Dimension(64,24), grayColors[i] , null);
			lbl = new JLabel(text);
			lbl.setIcon(ic);
			p.add(lbl);
		}


		p.add(new JLabel(" ")); 
		p.add(new JLabel("============== GeoGebraColor ========== ")); 

		for (Entry<String, Color> entry : geogebraColor.entrySet()){	
			String text  = entry.getKey();
			ic = GeoGebraIcon.createColorSwatchIcon( 1.0f,  new Dimension(64,24), entry.getValue() , null);
			lbl = new JLabel(text);
			lbl.setIcon(ic);
			p.add(lbl);
		}


		p.add(new JLabel(" ")); 
		p.add(new JLabel("============== HTML Map Sorted by Color String ========== ")); 

		TreeMap<String, Color> sortedStringMap = new TreeMap<String, Color>(htmlColorMap());
		for (Entry<String, Color> entry : sortedStringMap.entrySet()){	
			String text  = entry.getKey();
			ic = GeoGebraIcon.createColorSwatchIcon( 1.0f,  new Dimension(64,24), entry.getValue() , null);
			lbl = new JLabel(text);
			lbl.setIcon(ic);
			p.add(lbl);
		}

		
		frame.getContentPane().add(new JScrollPane(p), BorderLayout.CENTER);

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}



}
