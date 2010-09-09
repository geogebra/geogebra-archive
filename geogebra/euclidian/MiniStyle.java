package geogebra.euclidian;

import java.awt.Color;

/**
 * Class to hold style settings for the Euclidian stylebar
 * @author G. Sturr
 *
 */
public class MiniStyle{
	
	final public static int MODE_PEN = 0;
	final public static int MODE_STANDARD = 1;
	
	public int lineStyle;
	public int lineSize;
	public int pointSize;
	public Color color;
	public int colorIndex;
	public float alpha;

	public MiniStyle(int mode){			
		if(mode == MODE_PEN)
			setPenDefaults();

		else if(mode == MODE_STANDARD)
			setStandardDefaults();			
	}

	public void setPenDefaults(){	
		lineStyle = EuclidianView.LINE_TYPE_FULL;
		pointSize = 3;
		lineSize = 3;
		color = Color.black;
		colorIndex = 23;  // index for black
		alpha = 1.0f;
	}
	
	public void setStandardDefaults(){	
		lineStyle = EuclidianView.LINE_TYPE_FULL;
		pointSize = 3;
		lineSize = 3;
		color = Color.red;
		colorIndex = 3;  // index for green
		alpha = 0.25f;
	}
	
	

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
