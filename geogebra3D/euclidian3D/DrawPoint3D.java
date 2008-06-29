package geogebra3D.euclidian3D;



import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.GraphicsContext3D;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Sphere;

import geogebra.kernel.linalg.GgbVector;
import geogebra3D.kernel3D.GeoPoint3D;





public class DrawPoint3D extends Drawable3D{
	
	
	
	private GeoPoint3D P;    
	private GgbVector coords = new GgbVector(4);
	
	Geometry geomNormal;
	Appearance appNormal;
		
	
	public DrawPoint3D(EuclidianView3D view3D, GeoPoint3D P) {     
		
    	this.view3D = view3D;          
        this.P = P;
        setGeoElement(P);
        
        //creating 3D object	
        t3d = new Transform3D();
        geomNormal = (new Sphere(0.06f)).getShape().getGeometry(Sphere.BODY);
        appNormal = new Appearance();
		
        
        update();
		
	}
	
	public void update() {
		isVisible = geo.isEuclidianVisible();       				 
		if (!isVisible) return;
		labelVisible = geo.isLabelVisible();  //TODO label  	

		coords.set(P.getCoords()); 
		view3D.toScreenCoords3D(coords);
		
		//System.out.println("coords ="); coords.SystemPrint();
		
		
		t3d.set(new Vector3d(new double[] {coords.get(1), coords.get(2), coords.get(3)} ));
				
		appNormal.setMaterial(new Material(new Color3f(0,0,0), 
				new Color3f(0,0,0), 
				new Color3f(geo.getObjectColor()), 
				new Color3f(1, 1, 1), 15));

	}
	
	
	public void draw(GraphicsContext3D gc){
		//System.out.println("draw");
    	gc.setModelTransform(t3d);
    	gc.setAppearance(appNormal);
    	gc.draw(geomNormal);
		
	}
	
	public void drawHidden(GraphicsContext3D gc){
		//System.out.println("draw");
    	gc.setModelTransform(t3d);
    	gc.setAppearance(appNormal);
    	gc.draw(geomNormal);
		
	}	
	
	public void drawTransp(GraphicsContext3D gc){}
	public void drawHiding(GraphicsContext3D gc){}

}
