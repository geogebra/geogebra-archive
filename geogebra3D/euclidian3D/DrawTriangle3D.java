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



import geogebra.Application;
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.GeoTriangle3D;




public class DrawTriangle3D extends Drawable3D {

	GeoTriangle3D T;
	GgbMatrix m; //representative matrix in physical coordinates

	
	Geometry geomTransp;
	Appearance appTransp;
	
	Geometry gridGeom;
	Appearance gridApp;
	
	public DrawTriangle3D(EuclidianView3D view, GeoTriangle3D t){
		this.T=t;
		this.view3D=view;
		setGeoElement(t);
		
		
        //creating 3D object	
        t3d = new Transform3D();
        geomTransp = Drawable3D.createTriangle(); //TODO beware of euclidianView3D scale 
        appTransp = getAppTransp(true);
        
        /*
        gridGeom = Drawable3D.createCylinder(0.01f, 1f, 10, 1f); //TODO use object property
        gridApp = getAppTransp(false);
        */
        
		update();
	}
	

	public void update() {
		
        isVisible = geo.isEuclidianVisible();       				 
        if (!isVisible){
        	
        	return;
        }
		labelVisible = geo.isLabelVisible();    	
		
		

		
		//GgbMatrix mc = P.getMatrixCompleted(); 
		GgbMatrix mc = T.getMatrixCompleted(); 
		//mc.SystemPrint();
		view3D.toScreenCoords3D(mc);
		
		Matrix4d m4d = new Matrix4d();
		for(int j=1;j<=4;j++)
			m4d.setColumn(j-1, mc.get(1, j), mc.get(2, j), mc.get(3, j), mc.get(4, j));
		t3d.set(m4d);
		
		
		
		
		appTransp.setMaterial(new Material(
				new Color3f(0,0,0), //ambient
				new Color3f(geo.getObjectColor()), //emmisive
				//new Color3f(0,0,0), 
				new Color3f(geo.getObjectColor()), //diffuse
				//new Color3f(1, 1, 1),
				new Color3f(0, 0, 0), //specular
				//new Color3f(geo.getObjectColor()),
				15));  //shininess
		
		//TODO use object transparency
		float transp = 0.75f;
		if (transp>0){
			RenderingAttributes ra = new RenderingAttributes();	    	   
		    ra.setDepthBufferWriteEnable(false); //don't write zbuffer	    
		    appTransp.setRenderingAttributes(ra);
		}			
		appTransp.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.FASTEST,transp));
		
		/*
		gridApp.setMaterial(new Material(
				new Color3f(0,0,0), //ambient
				new Color3f(geo.getObjectColor()), //emmisive
				new Color3f(geo.getObjectColor()), //diffuse
				new Color3f(0, 0, 0), //specular
				15));  //shininess
		gridApp.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.FASTEST,0.5f));
        */
	}
	
	

	
	public void draw(GraphicsContext3D gc){}
	public void drawHidden(GraphicsContext3D gc){}
	public void drawPicked(GraphicsContext3D gc){};
	
	public void drawTransp(GraphicsContext3D gc){
		//Application.debug("drawTransp");
		if(isVisible){
			//Application.debug("isVisible");
			gc.setAppearance(appTransp);
			
			gc.setModelTransform(t3d);			
			gc.draw(geomTransp);
			
			//grid
			/*
			GgbMatrix mc;
			Transform3D t;			
			gc.setAppearance(gridApp);
			
			for(double x=P.getGridXmin();x<=P.getGridXmax();x+=P.getGridXd()){
				mc = P.getDrawingXMatrix(x); 
				view3D.toScreenCoords3D(mc);
				t = new Transform3D();
				mc.getTransform3D(t);
				gc.setModelTransform(t);			
				gc.draw(gridGeom);
			}
			
			for(double y=P.getGridYmin();y<=P.getGridYmax();y+=P.getGridYd()){
				mc = P.getDrawingYMatrix(y); 
				view3D.toScreenCoords3D(mc);
				t = new Transform3D();
				mc.getTransform3D(t);
				gc.setModelTransform(t);			
				gc.draw(gridGeom);
			}
			*/
		}
		
	}
	
	public void drawHiding(GraphicsContext3D gc){
		if(isVisible){
			gc.setModelTransform(t3d);    	
			gc.draw(geomTransp);
		}
		
	}	
	
	
	
	
	
	


	
	
	public boolean isPicked(GgbVector pickLine){ return false; };
	

	

}
