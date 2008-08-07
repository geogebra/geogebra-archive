package geogebra3D.euclidian3D;



import geogebra.kernel.GeoElement;
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;



import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Geometry;
import javax.media.j3d.GraphicsContext3D;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.QuadArray;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * 
 * @author ggb3D
 *
 * 3D representation of a GeoElement3D
 *
 */
public abstract class Drawable3D {
	
	EuclidianView3D view3D; 
	BranchGroup bg; //root BranchGroup of the 3D object
	TransformGroup tg;
	Transform3D t3d;
	
	
	//picking
	boolean isPicked = false;
	

	
	
	
	
	GeoElement geo; //GeoElement linked to this
	
	boolean isVisible;
	boolean labelVisible;
	
	
	/** returns appearance for transparent parts with (or without) back faces */
	protected Appearance getAppTransp(boolean drawBackFaces){
		
		Appearance appTransp = new Appearance();
		
		if (drawBackFaces){
			//no culling for back faces
			PolygonAttributes pa = new PolygonAttributes();
			pa.setCullFace(PolygonAttributes.CULL_NONE );
			pa.setBackFaceNormalFlip(true);		
			appTransp.setPolygonAttributes(pa);	
		}
		return appTransp;
		
	}
	
	
	
	/** appearance for hiding parts 
	static final Appearance hidingApp(){
		Appearance ret = new Appearance();
		
	    RenderingAttributes ra = new RenderingAttributes();
	    ra.setRasterOpEnable(true);
	    ra.setRasterOp(RenderingAttributes.ROP_NOOP); //don't draw it
	    ret.setRenderingAttributes(ra);		
	    
	    //no culling for back faces
	    PolygonAttributes pa = new PolygonAttributes();
		pa.setCullFace(PolygonAttributes.CULL_NONE );
		//pa.setBackFaceNormalFlip(true);		
		ret.setPolygonAttributes(pa);		
	    
		
		return ret;
	}
	*/
	
	
	
	/** update the 3D object */
	abstract public void update(); 
	
	/** draw the 3D object */
	abstract public void draw(GraphicsContext3D gc); 
	abstract public void drawHidden(GraphicsContext3D gc); 
	abstract public void drawTransp(GraphicsContext3D gc); 
	abstract public void drawHiding(GraphicsContext3D gc); 
	abstract public void drawPicked(GraphicsContext3D gc); 
	
	
	
	
	/** picking */
	abstract public boolean isPicked(GgbVector pickLine);
	
	
	
	
	
	
	
	
    public GeoElement getGeoElement() {
        return geo;
    }    
    
    public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    } 
	

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    static final public Geometry createCylinder(int nbFaces){
    	return createCylinder(0.03f, 1f, 10, 1f); //TODO radius = 1
    }
    
    
	
	static final public Geometry createCylinder(float radius, float length, int nbFaces, float pointLength){
		
		int nbPointilles = (int) (length/pointLength)/2 + ((int) (length/pointLength) %2);
		QuadArray ret = new QuadArray(nbFaces*nbPointilles*4,QuadArray.COORDINATES|QuadArray.NORMALS);
		
		int index = 0;
		
		float[] c = new float[nbFaces+1]; 
		float[] s = new float[nbFaces+1];
		Vector3f[] v = new Vector3f[nbFaces]; //normal vectors
		
		int i;
		for (i=0; i<=nbFaces; i++){
			c[i] = (float) (Math.cos(i*2*Math.PI/nbFaces));
			s[i] = (float) (Math.sin(i*2*Math.PI/nbFaces));
		}
		
		for (i=0; i<nbFaces; i++){
			v[i] = new Vector3f(0f,c[i],s[i]);
		}
		
		for (i=0; i<=nbFaces; i++){
			c[i] *= radius;
			s[i] *= radius;
		}
		
		
		
		index = 0;
		float x = 0;
		float x2;
		for (int j=0; j<nbPointilles; j++){
			//Application.debug("y= "+y);
			for (i=0; i<nbFaces; i++){
				x2=x+pointLength;
				if (x2>length) x2=length;
				ret.setCoordinate(index,new Point3f(x2,c[i],s[i])); ret.setNormal(index,v[i]); index++;
				ret.setCoordinate(index,new Point3f(x,c[i],s[i])); ret.setNormal(index,v[i]); index++;
				ret.setCoordinate(index,new Point3f(x,c[i+1],s[i+1])); ret.setNormal(index,v[(i+1)%nbFaces]); index++;
				ret.setCoordinate(index,new Point3f(x2,c[i+1],s[i+1])); ret.setNormal(index,v[(i+1)%nbFaces]); index++;
			}
			x+=2*pointLength;
		}
		
		

		
		
		
		
		return ret;
		
		
	}
	
	
	static final public Geometry createQuad(){
		
		QuadArray ret = new QuadArray(4,QuadArray.COORDINATES|QuadArray.NORMALS);
		
		Vector3f v = new Vector3f(0f,0f,1f);
		
		ret.setCoordinate(0,new Point3f(0,0,0)); ret.setNormal(0,v);
		ret.setCoordinate(1,new Point3f(1f,0,0)); ret.setNormal(1,v);
		ret.setCoordinate(2,new Point3f(1f,1f,0)); ret.setNormal(2,v);
		ret.setCoordinate(3,new Point3f(0,1f,0)); ret.setNormal(3,v);
		
	
		return ret;
	}
	
	
	
	
	static final public Geometry createSphere(int nbMeridians, int nbParallels){
		
		TriangleArray ret = new TriangleArray(nbMeridians*nbParallels*2*3,TriangleArray.COORDINATES|TriangleArray.NORMALS);
		
		Point3f[] p = new Point3f[(nbMeridians+1)*(nbParallels+1)];
		Vector3f[] v = new Vector3f[(nbMeridians+1)*(nbParallels+1)];
		
		int i,j;
				
		float cM, sM, cP, sP;
		
		
		for (i=0; i<=nbMeridians; i++){			
			cM = (float) (Math.cos(i*2*Math.PI/nbMeridians));
			sM = (float) (Math.sin(i*2*Math.PI/nbMeridians));	
			
			for (j=0; j<=nbParallels; j++){				
				cP = (float) (Math.cos(j*Math.PI/(2*nbParallels)));
				sP = (float) (Math.sin(j*Math.PI/(2*nbParallels)));
				
				p[i*(nbParallels+1)+j] = new Point3f(cM*cP,sM*cP,sP);
				v[i*(nbParallels+1)+j] = new Vector3f(cM*cP,sM*cP,sP);
				
			}		
		}

		int index=0;
		for (i=0; i<nbMeridians; i++){						
			for (j=0; j<nbParallels; j++){				
				ret.setCoordinate(index,p[i*(nbParallels+1)+j]); ret.setNormal(index,v[i*(nbParallels+1)+j]); index++;
				ret.setCoordinate(index,p[(i+1)*(nbParallels+1)+j+1]); ret.setNormal(index,v[(i+1)*(nbParallels+1)+j+1]); index++;
				ret.setCoordinate(index,p[i*(nbParallels+1)+j+1]); ret.setNormal(index,v[i*(nbParallels+1)+j+1]); index++;

				ret.setCoordinate(index,p[i*(nbParallels+1)+j]); ret.setNormal(index,v[i*(nbParallels+1)+j]); index++;
				ret.setCoordinate(index,p[(i+1)*(nbParallels+1)+j]); ret.setNormal(index,v[(i+1)*(nbParallels+1)+j]); index++;
				ret.setCoordinate(index,p[(i+1)*(nbParallels+1)+j+1]); ret.setNormal(index,v[(i+1)*(nbParallels+1)+j+1]); index++;
			}		
		}
		
		
		
		return ret;
	}
	

    
 	

}
