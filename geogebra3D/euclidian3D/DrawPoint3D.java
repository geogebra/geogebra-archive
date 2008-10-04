package geogebra3D.euclidian3D;



import java.awt.Color;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.GraphicsContext3D;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Sphere;

import geogebra.Application;
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
	
	float radius = 100f; //TODO use object property
		
	
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
		
		matrix.set(coords, 4);
		//TODO use point "thickness"
		for(int i=1;i<=3;i++){
			matrix.set(i,i,0.1);
		}
		//coords.SystemPrint();
		
		//Application.debug("coords ="); coords.SystemPrint();
		
		
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
	

	public void draw(EuclidianRenderer3D renderer){
		if(!geo.isEuclidianVisible())
			return;
		
		renderer.setMaterial(geo.getObjectColor(),1f);//TODO geo.getAlphaValue());
		renderer.setMatrix(getMatrixGL());
		renderer.drawSphere(radius);
		renderer.resetMatrix();

	}
	
	/*
	public void draw(GraphicsContext3D gc){
		if(!geo.isEuclidianVisible())
			return;
 
    	gc.setModelTransform(t3d);
    	gc.setAppearance(appNormal);
    	gc.draw(geomNormal);
		
	}
	*/
	
	public void drawHidden(EuclidianRenderer3D renderer){
		draw(renderer);
		
	}	
	
	
	public void drawPicked(EuclidianRenderer3D renderer){
		if(!geo.isEuclidianVisible())
			return;
		
		if (geo.doHighlighting()){
			renderer.setMaterial(new Color(0f,0f,0f),0.75f);
			renderer.setMatrix(getMatrixGL());
			renderer.drawSphere(radius*1.3f);
			renderer.resetMatrix();
		}

	}
	
	/*
	public void drawPicked(GraphicsContext3D gc){
		if(!geo.isEuclidianVisible())
			return;
 		
		if (geo.doHighlighting()){
			gc.setModelTransform(t3dPicked);
			gc.draw(geomPicked);
		}
	};
	*/
	
	public void drawTransp(EuclidianRenderer3D renderer){}
	public void drawHiding(EuclidianRenderer3D renderer){}
	
	
	
	public boolean isPicked(GgbVector pickPoint){
		//if (coords.subVector(1,3).distLine(view3D.eye,pickPoint.subVector(1,3).sub(view3D.eye))<=radius){
		//TODO use euclidianview3D scale factor
		if (coords.subVector(1,3).distLine(pickPoint.subVector(1,3),new GgbVector(new double[] {0,0,1}))<=radius/10f){
			//Application.debug("picked = "+P.getLabel());
			geo.setHighlighted(true);
			return true;
		}else{
			geo.setHighlighted(false);
			return false;
		}
	};

}
