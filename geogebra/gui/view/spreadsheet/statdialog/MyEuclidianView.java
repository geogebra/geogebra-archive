package geogebra.gui.view.spreadsheet.statdialog;

import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;

import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.EuclidianViewInterface;

public class MyEuclidianView extends EuclidianView {


	public MyEuclidianView(EuclidianController ec, boolean[] showAxes, boolean showGrid) {
		super(ec, showAxes, showGrid);
		
	}

		
	/**
	 * Override UpdateSize() so that our plots stay centered and scaled in a
	 * resized window.
	 */
	@Override
	public void updateSize(){
		
		// record the old coord system
		double xminTemp = getXmin();
		double xmaxTemp = getXmax();
		double yminTemp = getYmin();
		double ymaxTemp = getYmax();	
		
		// standard update: change the coord system to match new window dimensions
		// with the upper left corner fixed and the other bounds adjusted.  
		super.updateSize();		
		
		// now reset the coord system so that our view dimensions are restored 
		// using the new scaling factors. 
		setRealWorldCoordSystem(xminTemp, xmaxTemp, yminTemp, ymaxTemp);
	}	

	
	public void setMode(int mode) {
		// .... do nothing

	}
	
	
	
	
/*	
	public void getYAxisBuffer(Graphics2D g2) {
		
		FontRenderContext frc = g2.getFontRenderContext();
		g2.setFont(fontAxes);
		
		double rw = ymax - (ymax % axesNumberingDistances[1]);
		double pix = yZero - rw * yscale;
		double axesStep = yscale * axesNumberingDistances[1]; // pixelstep
		double tickStep = axesStep / 2;
		
		for (; pix <= yAxisHeight; rw -= axesNumberingDistances[1], pix += axesStep) {
			if (pix <= maxY) {
				if (showAxesNumbers[1]) {
					String strNum = kernel.formatPiE(rw,
							axesNumberFormat[1]);
					boolean zero = strNum.equals("0");

					sb.setLength(0);
					sb.append(strNum);
					if (axesUnitLabels[1] != null && !piAxisUnit[1])
						sb.append(axesUnitLabels[1]);

					TextLayout layout = new TextLayout(sb.toString(),
							fontAxes, frc);
					int x = (int) (xZero + xoffset - layout.getAdvance());
					int y;
					if (zero && showAxes[0]) {
						y = (int) (yZero - 2);
					} else {
						y = (int) (pix + yoffset);
					}
					g2.drawString(sb.toString(), x, y);
				}
			}
			
			
	
		
		
		
	}
	
	
	*/

}
