package geogebra3D.euclidian3D;

import java.awt.Color;

import geogebra.Matrix.GgbMatrix4x4;
import geogebra.Matrix.GgbVector;
import geogebra.euclidian.EuclidianView;
import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.GeoPoint3D;

public class DrawPointDecorations extends Drawable3D {
	
	private GgbMatrix4x4 segmentMatrix;
	private GgbMatrix4x4 planeMatrix;
	
	/** gl index of the plane */
	private int planeIndex;
	
	// altitude of the point
	private double altitude;
	

	public DrawPointDecorations(EuclidianView3D aView3d) {
		super(aView3d);
		
		segmentMatrix = GgbMatrix4x4.Identity();
		segmentMatrix.setVx(EuclidianView3D.vz);
		segmentMatrix.setVy((GgbVector) EuclidianView3D.vx.mul(1)); //TODO remove mul
		segmentMatrix.setVz((GgbVector) EuclidianView3D.vy.mul(1));
		
		planeMatrix = GgbMatrix4x4.Identity();
		planeMatrix.setVx((GgbVector) EuclidianView3D.vx.mul(0.2)); 
		planeMatrix.setVy((GgbVector) EuclidianView3D.vy.mul(0.2));
		

		setWaitForUpdate();
	}

	
	
	
	public void draw(Renderer renderer) {
		
		renderer.setColor(Color.BLACK, 1);
		renderer.setMatrix(segmentMatrix);
		
		if (altitude>0)
			renderer.drawSegment(0,altitude);
		else
			renderer.drawSegment(altitude,0);

	}
	
	
	public void drawHidden(Renderer renderer) {
		
		renderer.setDash(EuclidianView.LINE_TYPE_DASHED_SHORT);
		draw(renderer);

	}
	
	
	
	/** set the point for which decorations are made
	 * @param point
	 */
	public void setPoint(GeoPoint3D point){
		
		// get point altitude
		altitude = point.getDrawingMatrix().getOrigin().get(3);
				
		//set origin to projection of the point on xOy plane
		GgbVector origin = new GgbVector(4);
		origin.set(point.getDrawingMatrix().getOrigin());
		origin.set(3, 0);
		segmentMatrix.setOrigin(origin);
		planeMatrix.setOrigin(origin);
		
	}
	
	
	public void drawHiding(Renderer renderer) { 
		
		drawTransp(renderer);
	}



	public void drawTransp(Renderer renderer) {
		
		renderer.setMatrix(planeMatrix);
		renderer.initMatrix();
		renderer.getGeometryManager().draw(planeIndex);
		renderer.resetMatrix();
	}

	
	
	

	protected void updateForView() {

	}
	
	protected void updateForItSelf() {
		
		Renderer renderer = getView3D().getRenderer();
		planeIndex = renderer.getGeometryManager().newPlane(Color.GRAY,0.25f,1f);
	}



	///////////////////////////////////////////
	// UNUSED METHODS
	///////////////////////////////////////////
	
	public void drawGeometry(Renderer renderer) { }
	public void drawGeometryHidden(Renderer renderer) { }
	public void drawGeometryPicked(Renderer renderer) { }
	public void drawHighlighting(Renderer renderer) { }
	public int getPickOrder() {return 0;}
	public int getType() {return 0;}
	public boolean isTransparent() {return false;}



}
