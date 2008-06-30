package geogebra3D.euclidian3D;


import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.GraphicsContext3D;
import javax.media.j3d.Material;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix4d;



import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;
import geogebra3D.kernel3D.GeoPlane3D;




public class DrawPlane3D extends Drawable3D {

	GeoPlane3D P;
	GgbMatrix m; //representative matrix in physical coordinates

	
	Geometry geomTransp;
	Appearance appTransp;
	
	
	public DrawPlane3D(EuclidianView3D view, GeoPlane3D p){
		this.P=p;
		this.view3D=view;
		setGeoElement(p);
		
		
        //creating 3D object	
        t3d = new Transform3D();
        geomTransp = Drawable3D.createQuad(); //TODO beware of euclidianView3D scale 
        appTransp = getAppTransp();
		
		update();
	}
	

	public void update() {
		
        isVisible = geo.isEuclidianVisible();       				 
        if (!isVisible) return;
		labelVisible = geo.isLabelVisible();    	
		
		

		
		GgbMatrix mc = P.getMatrixCompleted(); 
		view3D.toScreenCoords3D(mc);
		
		Matrix4d m4d = new Matrix4d();
		for(int j=1;j<=4;j++)
			m4d.setColumn(j-1, mc.get(1, j), mc.get(2, j), mc.get(3, j), mc.get(4, j));
		t3d.set(m4d);
		
		
		
		
		appTransp.setMaterial(new Material(new Color3f(0,0,0), 
				new Color3f(0,0,0), 
				new Color3f(geo.getObjectColor()), 
				new Color3f(1, 1, 1), 15));
		
		//TODO use object transparency
		float transp = 0.75f;
		if (transp>0){
			RenderingAttributes ra = new RenderingAttributes();	    	   
		    ra.setDepthBufferWriteEnable(false); //don't write zbuffer	    
		    appTransp.setRenderingAttributes(ra);
		}			
		appTransp.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.FASTEST,transp));
		
		

       
	}
	
	

	
	public void draw(GraphicsContext3D gc){}
	public void drawHidden(GraphicsContext3D gc){}
	public void drawPicked(GraphicsContext3D gc){};
	
	public void drawTransp(GraphicsContext3D gc){
		//System.out.println("draw");
    	gc.setModelTransform(t3d);
    	gc.setAppearance(appTransp);
    	gc.draw(geomTransp);
		
	}
	
	public void drawHiding(GraphicsContext3D gc){
		//System.out.println("draw");
    	gc.setModelTransform(t3d);    	
    	gc.draw(geomTransp);
		
	}	
	
	
	
	
	
	
	
	
	public boolean isPicked(GgbVector pickLine){ return false; };
	

	

}
