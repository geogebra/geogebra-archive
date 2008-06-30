package geogebra3D.euclidian3D;



import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.GraphicsContext3D;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Sphere;

import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;
import geogebra3D.kernel3D.GeoPoint3D;





public class DrawPoint3D extends Drawable3D{
	
	
	
	private GeoPoint3D P;    
	private GgbVector coords = new GgbVector(4);
	
	Geometry geomNormal;
	Appearance appNormal;
	Geometry geomPicked;
	
	Transform3D t3dPicked = new Transform3D();
	
	float radius = 0.06f; //TODO use object property
		
	
	public DrawPoint3D(EuclidianView3D view3D, GeoPoint3D P) {     
		
    	this.view3D = view3D;          
        this.P = P;
        setGeoElement(P);
        
        //creating 3D object	
        t3d = new Transform3D();       
        //geomNormal = (new Sphere(radius)).getShape().getGeometry(Sphere.BODY);
        geomNormal = Drawable3D.createSphere(20, 10);
        appNormal = new Appearance();
		
        //geomPicked = (new Sphere(radius*1.1f)).getShape().getGeometry(Sphere.BODY);
        geomPicked = Drawable3D.createSphere(20, 10);
        
        update();
		
	}
	
	public void update() {
		isVisible = geo.isEuclidianVisible();       				 
		if (!isVisible) return;
		labelVisible = geo.isLabelVisible();  //TODO label  	

		coords.set(P.getCoords()); 
		view3D.toScreenCoords3D(coords);
		//coords.SystemPrint();
		
		//System.out.println("coords ="); coords.SystemPrint();
		
		
		t3d.set(new Vector3d(new double[] {coords.get(1), coords.get(2), coords.get(3)} ));
		Transform3D tscale = new Transform3D();
		tscale.setScale(radius);
		t3d.mul(tscale);
		
		tscale.setScale(radius*1.2f);
		t3dPicked.set(new Vector3d(new double[] {coords.get(1), coords.get(2), coords.get(3)} ));
		t3dPicked.mul(tscale);
		
		
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
	
	public void drawPicked(GraphicsContext3D gc){
		if (isPicked){
			gc.setModelTransform(t3dPicked);
			gc.draw(geomPicked);
		}
	};
	
	public void drawTransp(GraphicsContext3D gc){}
	public void drawHiding(GraphicsContext3D gc){}
	
	
	
	public void isPicked(GgbVector pickPoint){
		if (coords.subVector(1,3).distLine(view3D.eye,pickPoint.subVector(1,3).sub(view3D.eye))<=radius){
			//System.out.println("picked = "+P.getLabel());
			isPicked = true;
		}else
			isPicked = false;
	};

}
