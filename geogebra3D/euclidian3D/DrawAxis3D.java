package geogebra3D.euclidian3D;

import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DMatrix4x4;
import geogebra3D.Matrix.Ggb3DVector;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoAxis3D;
import geogebra3D.kernel3D.GeoCoordSys1D;
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
    public void drawLabel(Renderer renderer, boolean colored, boolean forPicking){


    	/*
    	
    	if (forPicking)
			return;
    	
		if(!getGeoElement().isEuclidianVisible())
			return;
		
    	if (!getGeoElement().isLabelVisible())
    		return;
    	
    	
    	if (colored)
    		renderer.setTextColor(getGeoElement().getObjectColor());
    	
    	
    	//gets the direction vector of the axis as it is drawn
    	//TODO do this when updated
    	Ggb3DVector v = ((GeoCoordSys1D) getGeoElement()).getVx().copyVector();
    	getView3D().toScreenCoords3D(v);
    	
    	//Application.debug(getGeoElement().getLabel()+":v=\n"+v);

    	//matrix for each tick
    	Ggb3DMatrix4x4 matrix = Ggb3DMatrix4x4.Identity();
    	
    	
    	for(int i=(int) getDrawMin();i<getDrawMax();i++){
    		matrix.setOrigin(((GeoCoordSys1D) getGeoElement()).getPoint(i));
    		renderer.setMatrix(matrix);
    		//renderer.drawText(getGeoElement().labelOffsetX,-getGeoElement().labelOffsetY,getGeoElement().getLabelDescription(),colored); 
    		renderer.drawText(-20,-20, ""+i,colored); 
    	}
    		
    	/*
    	matrix = Ggb3DMatrix4x4.Identity();		
    	renderer.setMatrix(matrix);
    	for(int i=(int) getDrawMin();i<getDrawMax();i++){
    		//renderer.drawText(getGeoElement().labelOffsetX,-getGeoElement().labelOffsetY,getGeoElement().getLabelDescription(),colored); 
    		renderer.drawText((int) (-20+v.get(1)*i),(int) (-20+v.get(2)*i), ""+i,colored); 
    	}
    	*/
    	
    }
	

}
