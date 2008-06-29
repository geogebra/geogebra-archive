package geogebra3D.euclidian3D;


import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import java.awt.*;


public class TestCanvas3d 
extends Canvas3D
{
	/**
	 * Default constructor.  Here we create the universe.
	 */
	public TestCanvas3d()
	{		
		super(SimpleUniverse.getPreferredConfiguration());		
		SimpleUniverse u = new SimpleUniverse(this);
		BranchGroup scene = createContent();
		u.getViewingPlatform().setNominalViewingTransform();  // back away from object a little
		OrbitBehavior orbit = new OrbitBehavior(this);
		orbit.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY));
		u.getViewingPlatform().setViewPlatformBehavior(orbit);
		scene.compile();
		u.addBranchGraph(scene);
	}

	/**
	 * Create a canvas to draw the 3D world on.
	 */
	/*
	private Canvas3D createCanvas()
	{
		GraphicsConfigTemplate3D graphicsTemplate = new GraphicsConfigTemplate3D();
		GraphicsConfiguration gc1 =
			GraphicsEnvironment.getLocalGraphicsEnvironment()
			.getDefaultScreenDevice().getBestConfiguration(graphicsTemplate);
		return new Canvas3D(gc1);
	}
	*/

	/**
	 * Fill your 3D world with content
	 */
	private BranchGroup createContent()
	{
		BranchGroup objRoot = new BranchGroup();

		// Create a triangle with each point a different color.  Remember to
		// draw the points in counter-clockwise order.  That is the default
		// way of determining which is the front of a polygon.
		//        o (1)
		//       / \
		//      /   \
		// (2) o-----o (0)
		Shape3D shape = new Shape3D();
		TriangleArray tri = new TriangleArray(3, TriangleArray.COORDINATES | TriangleArray.COLOR_3);
		tri.setCoordinate(0, new Point3f(0.5f, 0.0f, 0.0f));
		tri.setCoordinate(1, new Point3f(0.0f, 0.5f, 0.0f));
		tri.setCoordinate(2, new Point3f(-0.5f, 0.0f, 0.0f));
		tri.setColor(0, new Color3f(1.0f, 0.0f, 0.0f));
		tri.setColor(1, new Color3f(0.0f, 1.0f, 0.0f));
		tri.setColor(2, new Color3f(0.0f, 0.0f, 1.0f));

		// Because we're about to spin this triangle, be sure to draw
		// backfaces.  If we don't, the back side of the triangle is invisible.
		Appearance ap = new Appearance();
		PolygonAttributes pa = new PolygonAttributes();
		pa.setCullFace(PolygonAttributes.CULL_NONE);
		ap.setPolygonAttributes(pa);
		shape.setAppearance(ap);

		// Set up a simple RotationInterpolator
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 5.0);
		TransformGroup tg = new TransformGroup();
		Transform3D yAxis = new Transform3D();
		Alpha rotationAlpha = new Alpha(-1, 4000);
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		RotationInterpolator rotator =
			new RotationInterpolator(rotationAlpha, tg, yAxis,
					0.0f, (float) Math.PI * 2.0f);
		rotator.setSchedulingBounds(bounds);

		shape.setGeometry(tri);
		tg.addChild(rotator);
		tg.addChild(shape);
		objRoot.addChild(tg);

		return objRoot;
	}





	/**
	 * This is our entrypoint to the application.  This code is not called when the program runs as an applet.
	 *
	 * @param args - command line arguments (unused)
	 */
	public static void main(String args[])
	{
		
		Frame myApp = new Frame();
		TestCanvas3d tc3d = new TestCanvas3d();
		myApp.add("Center", tc3d);
		myApp.setSize(600,600);
		myApp.setVisible(true);
	}

}
