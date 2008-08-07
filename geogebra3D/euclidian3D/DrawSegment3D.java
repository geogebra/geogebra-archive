package geogebra3D.euclidian3D;


import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.GraphicsContext3D;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;




import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;
import geogebra3D.kernel3D.GeoSegment3D;



public class DrawSegment3D extends Drawable3D {

	GeoSegment3D S;
	Geometry geomNormal;
	Appearance appNormal;
	
	double dashLength;
	
	public DrawSegment3D(EuclidianView3D view, GeoSegment3D s){
		this.S=s;
		this.view3D=view;
		setGeoElement(s);
		
		
        //creating 3D object	
        t3d = new Transform3D();
        geomNormal = Drawable3D.createCylinder(0.02f, 1f, 10, 1f); //TODO beware of euclidianView3D scale 
        appNormal = new Appearance();
        
        
		
		
		update();
	}
	

	public void update() {
		
        isVisible = geo.isEuclidianVisible();       				 
        if (!isVisible) return;
		labelVisible = geo.isLabelVisible();    	
		
		
		
		GgbMatrix mc = S.getSegmentMatrix(0,1); 
		view3D.toScreenCoords3D(mc);
		mc.getTransform3D(t3d);
		
		
		
		
		appNormal.setMaterial(new Material(new Color3f(0,0,0), 
				new Color3f(0,0,0), 
				new Color3f(geo.getObjectColor()), 
				new Color3f(1, 1, 1), 15));
		
		
		dashLength = 0.08f/((float) S.getLength()); //TODO use object property

       
	}
	
	

	
	public void draw(GraphicsContext3D gc){
		//Application.debug("draw");
    	gc.setModelTransform(t3d);
    	gc.setAppearance(appNormal);
    	gc.draw(geomNormal);
		
	}
	
	public void drawHidden(GraphicsContext3D gc){
    	
    	gc.setAppearance(appNormal);
    	Transform3D t = new Transform3D();
    	GgbMatrix m; 
		double l2;
		
    	for(float l=0; l<1;l+=2*dashLength){
    		l2 = l+dashLength;
    		if (l2>1) l2=1;
    		m = S.getSegmentMatrix(l,l2); 
    		view3D.toScreenCoords3D(m);
    		m.getTransform3D(t);
    		gc.setModelTransform(t);
    		gc.draw(geomNormal);
    	}
    	
		
	}
	
	public void drawTransp(GraphicsContext3D gc){}
	public void drawHiding(GraphicsContext3D gc){}
	public void drawPicked(GraphicsContext3D gc){};
	
	
	public boolean isPicked(GgbVector pickLine){ return false; };
	

}
