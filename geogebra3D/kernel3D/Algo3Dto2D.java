package geogebra3D.kernel3D;

import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;

public class Algo3Dto2D extends AlgoElement3D {

	
	GeoElement3D geo3D; 
	GeoCoordSys2DAbstract cs;
	
	GeoElement geo;
	
	
	public Algo3Dto2D(Construction c, String label, GeoElement3D geo3D, GeoCoordSys2DAbstract cs) {
		this(c,geo3D,cs);
		geo.setLabel(label);
		
	}
	
	public Algo3Dto2D(Construction c, GeoElement3D geo3D, GeoCoordSys2DAbstract cs) {
		super(c);
		
		this.geo3D = geo3D;
		this.cs =  cs;

		switch(geo3D.getGeoClassType()){
		case GeoElement3D.GEO_CLASS_POINT3D:
			geo = new GeoPoint(c);
			//geo = new GeoPoint(c,"to2D",0,0,1);
			break;
		default:
			break;
		}
		
    	setInputOutput(); 
    	compute();
		
		
	}

	protected void compute() {
		
		switch(geo3D.getGeoClassType()){
		case GeoElement3D.GEO_CLASS_POINT3D:
			if (!geo3D.isDefined()){
				geo.setUndefined();
				return;
			}
			
			GgbVector p = ((GeoPoint3D) geo3D).getCoords();
			GgbVector[] project=p.projectPlane(cs.getMatrix4x4());
			((GeoPoint) geo).setCoords(project[1].get(1), project[1].get(2), 1);
			break;
		default:
			break;
		}


	}

	protected void setInputOutput() {
		
    	input = new GeoElement3D[2];
    	input[0] = geo3D;
    	input[1] = cs;
   	
        output = new GeoElement[1];
        output[0] = geo;
          
        setDependencies();
       
	}

	protected String getClassName() {
		return "Algo3Dto2D";
	}
	
	public GeoElement getGeo(){
		return geo;
	}

}
