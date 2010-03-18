package geogebra3D.euclidian3D;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import geogebra.Matrix.GgbMatrix4x4;
import geogebra.Matrix.GgbVector;
import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoAxis3D;
import geogebra3D.kernel3D.GeoCoordSys1D;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.GeoElement3DInterface;

public class DrawAxis3D extends DrawLine3D {
	
	public DrawAxis3D(EuclidianView3D a_view3D, GeoAxis3D axis3D){
		
		super(a_view3D, axis3D);
		
	}	
	
	public void drawGeometry(Renderer renderer) {
		
		renderer.setArrowType(Renderer.ARROW_TYPE_SIMPLE);
		renderer.setArrowLength(20);
		renderer.setArrowWidth(10);
		

		super.drawGeometry(renderer);
		
		renderer.setArrowType(Renderer.ARROW_TYPE_NONE);
	}
	
	
	
	
	/**
	 * drawLabel is used here for ticks
	 */
    public void drawLabel(Renderer renderer){


    	
    	
		if(!getGeoElement().isEuclidianVisible())
			return;
		
    	if (!getGeoElement().isLabelVisible())
    		return;
    	
    	
    	renderer.setTextColor(getGeoElement().getObjectColor());
  		renderer.setColor(getGeoElement().getObjectColor(), 1);
  		renderer.setDash(Renderer.DASH_NONE);  	
    	
    	double ticksSize = 5;
    	

    	//gets the direction vector of the axis as it is drawn on screen
    	//TODO do this when updated
    	GgbVector v = ((GeoCoordSys1D) getGeoElement()).getVx().copyVector();
    	getView3D().toScreenCoords3D(v);
    	v.set(3, 0); //set z-coord to 0
    	double vScale = v.norm(); //axis scale, used for ticks distance
    	//v.normalize();
    	
    	//calc orthogonal offsets
    	int vx = (int) (v.get(1)*3*ticksSize/vScale);
    	int vy = (int) (v.get(2)*3*ticksSize/vScale);
    	int xOffset = -vy;
    	int yOffset = vx;
    	
    	if (yOffset>0){
    		xOffset = -xOffset;
    		yOffset = -yOffset;
    	}
    	
    	
    	//interval between two ticks
    	//TODO merge with EuclidianView.setAxesIntervals(double scale, int axis)
    	//Application.debug("vscale : "+vScale);
    	double maxPix = 100; // only one tick is allowed per maxPix pixels
		double units = maxPix / vScale;
		int exp = (int) Math.floor(Math.log(units) / Math.log(10));
		int maxFractionDigtis = Math.max(-exp, getView3D().getKernel().getPrintDecimals());
		NumberFormat numberFormat = new DecimalFormat();
		((DecimalFormat) numberFormat).applyPattern("###0.##");	
		numberFormat.setMaximumFractionDigits(maxFractionDigtis);
		double pot = Math.pow(10, exp);
		double n = units / pot;
		double distance;

		if (n > 5) {
			distance = 5 * pot;
		} else if (n > 2) {
			distance = 2 * pot;
		} else {
			distance = pot;
		}
    	
    	
    	//matrix for each number 
    	GgbMatrix4x4 numbersMatrix = GgbMatrix4x4.Identity();
    	
    	//matrix for each ticks
    	GgbMatrix4x4 ticksMatrix = new GgbMatrix4x4();
    	GgbMatrix4x4 drawingMatrix = ((GeoElement3D) getGeoElement()).getDrawingMatrix();
    	double ticksThickness = 1/getView3D().getScale();
    	ticksMatrix.setVx(drawingMatrix.getVx().normalized());
    	ticksMatrix.setVy((GgbVector) drawingMatrix.getVy().mul(ticksSize));
    	ticksMatrix.setVz((GgbVector) drawingMatrix.getVz().mul(ticksSize));
  	
    	
    	//for(int i=(int) getDrawMin();i<=getDrawMax();i++){
    	for(int i=(int) (getDrawMin()/distance);i<=getDrawMax()/distance;i++){
    		double val = i*distance;
    		GgbVector origin = ((GeoCoordSys1D) getGeoElement()).getPoint(val);
    		
    		//draw numbers
    		String strNum = getView3D().getKernel().formatPiE(val,numberFormat);
    		numbersMatrix.setOrigin(origin);
    		renderer.setMatrix(numbersMatrix);
       		renderer.drawText(xOffset-4,yOffset-6, strNum,true); //TODO values 4 and 6 depend to label size 
    	
       		//draw ticks
       		ticksMatrix.setOrigin(origin);
       		renderer.setMatrix(ticksMatrix);
       		renderer.drawSegment(-ticksThickness, ticksThickness);
       		
    	}
    	
		numbersMatrix.setOrigin(((GeoCoordSys1D) getGeoElement()).getPoint(getDrawMax()));
		renderer.setMatrix(numbersMatrix);
   		renderer.drawText(-vx-xOffset-4,-vy-yOffset-6, ((GeoAxis3D) getGeoElement()).getAxisLabel(),true); //TODO values 4 and 6 depend to label size 
    	

    	
    }
	

}
