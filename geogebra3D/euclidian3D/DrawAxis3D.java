package geogebra3D.euclidian3D;

import geogebra.Matrix.GgbVector;
import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoAxis3D;
import geogebra3D.kernel3D.GeoCoordSys1D;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.TreeMap;

/**
 * Class for drawing axis (Ox), (Oy), ...
 * 
 * @author matthieu
 *
 */
public class DrawAxis3D extends DrawLine3D {
	
	private TreeMap<String, DrawLabel3D>  labels;
	
	/**
	 * common constructor
	 * @param view3D
	 * @param axis3D
	 */
	public DrawAxis3D(EuclidianView3D view3D, GeoAxis3D axis3D){
		
		super(view3D, axis3D);
		
		labels = new TreeMap<String, DrawLabel3D>();
	}	
	
	
	/**
	 * drawLabel is used here for ticks
	 */
    public void drawLabel(Renderer renderer){


    	//if (!getView3D().isStarted()) return;
    	
		if(!getGeoElement().isEuclidianVisible())
			return;
		
    	if (!getGeoElement().isLabelVisible())
    		return;

    	
   

    	
    	for(DrawLabel3D label : labels.values())
    		label.draw(renderer);
    		
    	
    	super.drawLabel(renderer);
    	
    	
    }
    	
    
	

    	
    protected void updateLabel(){
  
  		//draw numbers
  		GeoAxis3D axis = (GeoAxis3D) getGeoElement();
  		
		NumberFormat numberFormat = axis.getNumberFormat();
		double distance = axis.getNumbersDistance();
    	
    	int iMin = (int) (getDrawMin()/distance);
    	int iMax = (int) (getDrawMax()/distance);
    	int nb = iMax-iMin+1;
    	
    	
    	if (nb<1){
    		Application.debug("nb="+nb);
    		//labels = null;
    		return;
    	}
    	
    	
    	//sets all already existing labels not visible
    	for(DrawLabel3D label : labels.values())
    		label.setIsVisible(false);
    	
    	
    	for(int i=iMin;i<=iMax;i++){
    		double val = i*distance;
    		GgbVector origin = ((GeoCoordSys1D) getGeoElement()).getPoint(val);
    		
    		//draw numbers
    		String strNum = getView3D().getKernel().formatPiE(val,numberFormat);

    		//check if the label already exists
    		DrawLabel3D label = labels.get(strNum);
    		if (label!=null){
    			//sets the label visible
    			label.setIsVisible(true);
    			label.update(strNum, 10, 
    					getGeoElement().getObjectColor(),
    					origin.copyVector(),
    					axis.getNumbersXOffset()-4,axis.getNumbersYOffset()-6);
    			//TODO optimize this
    		}else{
    			//creates new label
    			label = new DrawLabel3D(getView3D());
    			label.update(strNum, 10, 
    					getGeoElement().getObjectColor(),
    					origin.copyVector(),
    					axis.getNumbersXOffset()-4,axis.getNumbersYOffset()-6);
    			labels.put(strNum, label);
    		}
    		//TODO 4 and 6 depends to police size -> anchor
       		
    	}
    	
		
		// update end of axis label
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
