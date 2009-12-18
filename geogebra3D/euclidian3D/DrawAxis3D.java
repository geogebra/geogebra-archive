package geogebra3D.euclidian3D;

import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DMatrix4x4;
import geogebra3D.Matrix.Ggb3DVector;
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

    	//gets the direction vector of the axis as it is drawn
    	//TODO do this when updated
    	Ggb3DVector v = ((GeoCoordSys1D) getGeoElement()).getVx().copyVector();
    	getView3D().toScreenCoords3D(v);
    	v.set(3, 0); //set z-coord to 0
    	v.normalize();
    	
    	//calc orthogonal offsets
    	int vx = (int) (v.get(1)*3*ticksSize);
    	int vy = (int) (v.get(2)*3*ticksSize);
    	int xOffset = -vy;
    	int yOffset = vx;
    	
    	if (yOffset>0){
    		xOffset = -xOffset;
    		yOffset = -yOffset;
    	}
    	//Application.debug(getGeoElement().getLabel()+":v=\n"+v);

    	//matrix for each number 
    	Ggb3DMatrix4x4 numbersMatrix = Ggb3DMatrix4x4.Identity();
    	
    	//matrix for each ticks
    	Ggb3DMatrix4x4 ticksMatrix = new Ggb3DMatrix4x4();
    	Ggb3DMatrix4x4 drawingMatrix = ((GeoElement3D) getGeoElement()).getDrawingMatrix();
    	double ticksThickness = 1/getView3D().getScale();
    	ticksMatrix.setVx(drawingMatrix.getVx().normalized());
    	ticksMatrix.setVy((Ggb3DVector) drawingMatrix.getVy().mul(ticksSize));
    	ticksMatrix.setVz((Ggb3DVector) drawingMatrix.getVz().mul(ticksSize));
  	
    	
    	for(int i=(int) getDrawMin();i<=getDrawMax();i++){
    		Ggb3DVector origin = ((GeoCoordSys1D) getGeoElement()).getPoint(i);
    		
    		//draw numbers
    		numbersMatrix.setOrigin(origin);
    		renderer.setMatrix(numbersMatrix);
       		renderer.drawText(xOffset-4,yOffset-6, ""+i,true); //TODO values 4 and 6 depend to label size 
    	
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
