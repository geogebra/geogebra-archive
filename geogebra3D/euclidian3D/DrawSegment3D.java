package geogebra3D.euclidian3D;




import geogebra.kernel.linalg.GgbMatrix;
import geogebra3D.kernel3D.GeoSegment3D;



public class DrawSegment3D extends Drawable3DSolid {

	
	double dashLength;
	
	public DrawSegment3D(EuclidianView3D a_view3D, GeoSegment3D a_segment3D){
		
		super(a_view3D,a_segment3D);
	}
	

	public void updateDrawingMatrix() {
		

		GeoSegment3D l_segment3D = (GeoSegment3D) getGeoElement();
		GgbMatrix l_matrix = l_segment3D.getSegmentMatrix(0,1); 
		setMatrix(l_matrix);
		
		
		
		dashLength = 0.12f/((float) l_segment3D.getLength()); //TODO use object property

       
	}
	
	public void drawPrimitive(EuclidianRenderer3D renderer) {
		renderer.drawCylinder(LINE3D_THICKNESS); 
	}
	public void drawPrimitivePicked(EuclidianRenderer3D renderer){
		renderer.drawCylinder(LINE3D_THICKNESS*PICKED_DILATATION); 
	}
		

	
	public void drawHidden(EuclidianRenderer3D renderer){
		
		if(!getGeoElement().isEuclidianVisible())
			return;
		
		
		double l2;
		GgbMatrix m; 
		GeoSegment3D l_segment3D = (GeoSegment3D) getGeoElement();
		
    	for(float l=0; l<1;l+=2*dashLength){
    		l2 = l+dashLength;
    		if (l2>1) l2=1;
    		m = l_segment3D.getSegmentMatrix(l,l2); 
    		getView3D().toScreenCoords3D(m);
    		renderer.setMaterial(getGeoElement().getObjectColor(),1.0f);//TODO geo.getAlphaValue());
    		renderer.setMatrix(m);
    		renderer.drawCylinder(LINE3D_THICKNESS); 
    		
    	}

	} 
	
	
	
	
	
	
	

	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_1D;
	}	
	

}
