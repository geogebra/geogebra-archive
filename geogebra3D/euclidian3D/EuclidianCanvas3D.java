package geogebra3D.euclidian3D;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;

import javax.media.j3d.Alpha;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;



public class EuclidianCanvas3D extends Canvas3D {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6882819678811705259L;

	
	
	
	public EuclidianCanvas3D(GraphicsConfiguration graphics) {
		super(graphics);
		
		//using immediate mode rendering
        this.stopRenderer();
                
        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        SimpleUniverse u = new SimpleUniverse(this);
        u.getViewingPlatform().setNominalViewingTransform();
                
        //creating a white background
    	Background bg = new Background(1f,1f,1f);
    	bg.setApplicationBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY));    
    	this.getGraphicsContext3D().setBackground(bg);
    	
    	//adding a light
    	Vector3f v = new Vector3f(0.5f, -0.5f, -1);
    	v.normalize();
    	this.getGraphicsContext3D().addLight(new DirectionalLight(new Color3f(1, 1, 1),  v));
		
	}
	
	public EuclidianCanvas3D(){

		//this(SimpleUniverse.getPreferredConfiguration());
		this(GraphicsEnvironment.getLocalGraphicsEnvironment().
                getDefaultScreenDevice().getBestConfiguration(new GraphicsConfigTemplate3D()));

		
				
	}
	
	
	
	


}
