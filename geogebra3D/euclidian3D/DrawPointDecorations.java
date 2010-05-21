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
import geogebra3D.kernel3D.GeoSegment3D;

public class DrawPointDecorations extends DrawCoordSys1D {
	
	//private GgbMatrix4x4 segmentMatrix;
	private GgbMatrix4x4 planeMatrix;
	
	private GgbVector p1, p2;
	
	/** gl index of the plane */
	private int planeIndex;
	
	// altitude of the point
	//private double altitude;
	

	public DrawPointDecorations(EuclidianView3D aView3d) {
		super(aView3d);
		
		setDrawMinMax(0, 1);
		
		
		p1 = new GgbVector(4);
		p1.setW(1);
		
		p2 = p1.copyVector();
		
		planeMatrix = GgbMatrix4x4.Identity();
		planeMatrix.setVx((GgbVector) EuclidianView3D.vx.mul(0.2)); 
		planeMatrix.setVy((GgbVector) EuclidianView3D.vy.mul(0.2));
		

		setWaitForUpdate();
	}
	
	

	
	
	
	/** set the point for which decorations are made
	 * @param point
	 */
	public void setPoint(GeoPoint3D point){
		
		p1 = point.getCoords();
				
		//set origin to projection of the point on xOy plane
		p2 = new GgbVector(4);
		p2.set(p1);
		p2.set(3, 0);

		planeMatrix.setOrigin(p2);
		
		setWaitForUpdate();
		
	}
	
	
	public void drawHiding(Renderer renderer) { 
		
		drawTransp(renderer);
	}



	/*
	public void drawTransp(Renderer renderer) {
		
		renderer.setMatrix(planeMatrix);
		renderer.initMatrix();
		renderer.getGeometryManager().draw(planeIndex);
		renderer.resetMatrix();
	}

	*/
	
	
	public void drawHidden(Renderer renderer){
		
		renderer.getTextures().setDashFromLineType(EuclidianView.LINE_TYPE_DASHED_LONG);
		draw(renderer);		

	} 
	
	public void draw(Renderer renderer) {
		
		renderer.setColor(Color.BLACK,1.0f);
		drawGeometry(renderer);

	}
	
	


	
	protected void updateForItSelf() {
		
		Renderer renderer = getView3D().getRenderer();
		
		//renderer.getGeometryManager().remove(planeIndex);
		
		//planeIndex = renderer.getGeometryManager().newPlane(Color.GRAY,0.25f,1f);
		
		
		updateForItSelf(p1, p2);
	}


	protected int getLineThickness(){
		return 1;
	}

	///////////////////////////////////////////
	// UNUSED METHODS
	///////////////////////////////////////////
	
	public void drawGeometryPicked(Renderer renderer) { }
	public int getPickOrder() {return 0;}
	public int getType() {return 0;}
	public boolean isTransparent() {return false;}



}
