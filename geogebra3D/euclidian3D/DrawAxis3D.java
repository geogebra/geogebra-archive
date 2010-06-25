package geogebra3D.euclidian3D;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.TreeMap;

import geogebra.Matrix.GgbMatrix4x4;
import geogebra.Matrix.GgbVector;
import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.PlotterTextLabel;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoAxis3D;
import geogebra3D.kernel3D.GeoCoordSys1D;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.GeoElement3DInterface;
import geogebra3D.kernel3D.GeoSegment3D;

public class DrawAxis3D extends DrawLine3D {
	
	private TreeMap<String, PlotterTextLabel>  labels;
	
	public DrawAxis3D(EuclidianView3D a_view3D, GeoAxis3D axis3D){
		
		super(a_view3D, axis3D);
		
		labels = new TreeMap<String, PlotterTextLabel>();
	}	
	
	/*
	public void drawGeometry(Renderer renderer) {
		
		renderer.setArrowType(Renderer.ARROW_TYPE_SIMPLE);
		renderer.setArrowLength(20);
		renderer.setArrowWidth(10);
		

		super.drawGeometry(renderer);
		
		renderer.setArrowType(Renderer.ARROW_TYPE_NONE);
	}
	
	*/
	
	
	/**
	 * drawLabel is used here for ticks
	 */
    public void drawLabel(Renderer renderer){


    	//if (!getView3D().isStarted()) return;
    	
		if(!getGeoElement().isEuclidianVisible())
			return;
		
    	if (!getGeoElement().isLabelVisible())
    		return;

    	
   

    	for(PlotterTextLabel label : labels.values())
    		renderer.drawText(label);
    	
    	super.drawLabel(renderer);
    	
    	
    }
    	
    
	

    	
    protected void updateLabel(){
    	
    	//if (!getView3D().isStarted()) return;

 
  		//draw ticks and numbers
  		GeoAxis3D axis = (GeoAxis3D) getGeoElement();
  		
		NumberFormat numberFormat = axis.getNumberFormat();
		double distance = axis.getNumbersDistance();

    	
    	//matrix for each number 
    	GgbMatrix4x4 numbersMatrix = GgbMatrix4x4.Identity();
    	
    	//matrix for each ticks
    	GgbMatrix4x4 ticksMatrix = new GgbMatrix4x4();
    	GgbMatrix4x4 drawingMatrix = ((GeoElement3D) getGeoElement()).getDrawingMatrix();
    	ticksMatrix.setVx(drawingMatrix.getVx().normalized());
    	ticksMatrix.setVy((GgbVector) drawingMatrix.getVy().mul(axis.getTickSize()));
    	ticksMatrix.setVz((GgbVector) drawingMatrix.getVz().mul(axis.getTickSize()));
  	
    	
    	
    	
    	int iMin = (int) (getDrawMin()/distance);
    	int iMax = (int) (getDrawMax()/distance);
    	int nb = iMax-iMin+1;
    	
    	
    	if (nb<1){
    		Application.debug("nb="+nb);
    		//labels = null;
    		return;
    	}
    	
    	
    	//sets all already existing labels not visible
    	for(PlotterTextLabel label : labels.values())
    		label.setIsVisible(false);
    	
    	
    	for(int i=iMin;i<=iMax;i++){
    		double val = i*distance;
    		GgbVector origin = ((GeoCoordSys1D) getGeoElement()).getPoint(val);
    		
    		//draw numbers
    		String strNum = getView3D().getKernel().formatPiE(val,numberFormat);

    		//check if the label already exists
    		PlotterTextLabel label = labels.get(strNum);
    		if (label!=null){
    			//sets the label visible
    			label.setIsVisible(true);
    			label.updateTexture(); //TODO remove this
    		}else{
    			//creates new label
    			label = new PlotterTextLabel(getView3D());
    			label.update(strNum, 10, 
    					getGeoElement().getObjectColor(),
    					origin.copyVector(),
    					axis.getNumbersXOffset()-4,axis.getNumbersYOffset()-6);
    			labels.put(strNum, label);
    		}
    		//TODO 4 and 6 depends to police size
    		//TODO anchor
       		
    	}
    	
    	
    	

		
		
		label.update(((GeoAxis3D) getGeoElement()).getAxisLabel(), 10, 
				getGeoElement().getObjectColor(),
				((GeoCoordSys1D) getGeoElement()).getPoint(getDrawMax()),
				axis.labelOffsetX-4,axis.labelOffsetY-6
		);

		
    	
    }
    


    
    protected void updateForItSelf(){
    	
    	updateDrawMinMax();
    	updateDecorations();
    	updateLabel();
    	
    	PlotterBrush brush = getView3D().getRenderer().getGeometryManager().getBrush();
       	brush.setArrowType(PlotterBrush.ARROW_TYPE_SIMPLE);
       	brush.setTicks(PlotterBrush.TICKS_ON);
       	brush.setTicksDistance( (float) ((GeoAxis3D) getGeoElement()).getNumbersDistance());
       	brush.setTicksOffset((float) (-getDrawMin()/(getDrawMax()-getDrawMin())));
       	super.updateForItSelf(false);
       	brush.setArrowType(PlotterBrush.ARROW_TYPE_NONE);
       	brush.setTicks(PlotterBrush.TICKS_OFF);
    }
    
    
    
    private void updateDecorations(){
    	

		
		//update decorations
		GeoAxis3D axis = (GeoAxis3D) getGeoElement();
		

    	//gets the direction vector of the axis as it is drawn on screen
    	GgbVector v = axis.getCoordSys().getVx().copyVector();
    	getView3D().toScreenCoords3D(v);
    	v.set(3, 0); //set z-coord to 0
    	double vScale = v.norm(); //axis scale, used for ticks distance
    	
    	//calc orthogonal offsets
    	int vx = (int) (v.get(1)*3*axis.getTickSize()/vScale);
    	int vy = (int) (v.get(2)*3*axis.getTickSize()/vScale);
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
		
		DecimalFormat numberFormat = new DecimalFormat();
		double distance = getView3D().getKernel().axisNumberDistance(units, numberFormat);

		axis.updateDecorations(distance, numberFormat, 
				xOffset, yOffset,
				-vx-xOffset,-vy-yOffset);
		
    	
    }
    
	protected void updateForView(){
		

		
		
		updateForItSelf();
		
	}
	

}
