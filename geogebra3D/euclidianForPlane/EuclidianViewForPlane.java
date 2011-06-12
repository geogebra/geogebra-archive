package geogebra3D.euclidianForPlane;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import geogebra.Matrix.CoordMatrix;
import geogebra.Matrix.CoordMatrix4x4;
import geogebra.Matrix.CoordSys;
import geogebra.Matrix.Coords;
import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.Previewable;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.kernel.kernelND.GeoDirectionND;
import geogebra.kernel.kernelND.GeoPlaneND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;
import geogebra3D.kernel3D.GeoPlane3D;


/**
 * 2D view for plane.
 * 
 * @author matthieu
 *
 */
public class EuclidianViewForPlane extends EuclidianView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private GeoCoordSys2D plane;

	/**
	 * 
	 * @param ec
	 * @param plane 
	 */
	public EuclidianViewForPlane(EuclidianController ec, GeoCoordSys2D plane) {
		super(ec, new boolean[]{ true, true }, true);
		
		this.plane = plane;
		updateMatrix();
		
		
		
		//TODO
		setShowAxes(false, true);
	}
	

	public boolean isDefault2D(){
		return false;
	}
	
	
	public boolean isVisibleInThisView(GeoElement geo){

		// prevent not implemented type to be displayed (TODO remove)
		switch (geo.getGeoClassType()){
		case GeoElement.GEO_CLASS_POINT:
		case GeoElement.GEO_CLASS_POINT3D:
		case GeoElement.GEO_CLASS_SEGMENT:
		case GeoElement.GEO_CLASS_SEGMENT3D:
		case GeoElement.GEO_CLASS_LINE:
		case GeoElement.GEO_CLASS_LINE3D:
		case GeoElement.GEO_CLASS_RAY:
		case GeoElement.GEO_CLASS_RAY3D:
		case GeoElement.GEO_CLASS_VECTOR:
		case GeoElement.GEO_CLASS_VECTOR3D:
		case GeoElement.GEO_CLASS_POLYGON:
		case GeoElement.GEO_CLASS_POLYGON3D:
		case GeoElement.GEO_CLASS_CONIC:
		case GeoElement.GEO_CLASS_CONIC3D:
			break;
		default:
			return false;
		}
		
		return geo.isVisibleInView3D();
	}
	
	public void attachView() {
		kernel.attach(this);
	}


	/**
	 * add all existing geos to this view
	 */
	public void addExistingGeos(){

		kernel.notifyAddAll(this);
	}
	
	
	
	
	
	
	
	public Coords getCoordsForView(Coords coords){
		return coords.projectPlane(getMatrix())[1];
	}
	
	public Coords getCoordsFromView(Coords coords){
		return getMatrix().mul(coords);
	}
	
	public CoordMatrix getMatrix(){
		
		return transformedMatrix;
		
		/*
		if (reverse==1)
			return planeMatrix;
		else
			return reverseMatrix;
			*/
		
		
		//return plane.getCoordSys().getMatrixOrthonormal();
		//return plane.getCoordSys().getDrawingMatrix();
	}
	
	private CoordMatrix4x4 planeMatrix, reverseMatrix, transformedMatrix;
	private CoordMatrix4x4 transform = CoordMatrix4x4.IDENTITY;
	private int reverse = 1;
	
	/*
	final static private int TRANSFORM_INIT = 0;
	final static private int TRANSFORM_IDENTITY = 1;
	final static private int TRANSFORM_MIRROR_Y = 2;
	final static private int TRANSFORM_MIRROR_X = 3;
	final static private int TRANSFORM_MIRROR_O = 4;
	final static private int TRANSFORM_ROT_M90 = 5;
	final static private int TRANSFORM_ROT_90 = 6;
	private int oldTransform = TRANSFORM_INIT;
	private int transform = TRANSFORM_IDENTITY;

	 */
	
	public void updateMatrix(){
		planeMatrix = plane.getCoordSys().getMatrixOrthonormal();	
		
		transformedMatrix = planeMatrix.mul(transform);//transform.mul(planeMatrix);
		
		
		
		//Application.debug("t="+transform+"\nold="+oldTransform);
		/*
		if (oldTransform!=transform){
			switch(transform){
			case TRANSFORM_MIRROR_Y:
				transformedMatrix = planeMatrix.mirrorY();
				break;
			case TRANSFORM_MIRROR_X:
				transformedMatrix = planeMatrix.mirrorX();
				break;
			case TRANSFORM_MIRROR_O:
				transformedMatrix = planeMatrix.mirrorO();
				break;
			case TRANSFORM_ROT_M90:
				transformedMatrix = planeMatrix.rotateM90();
				break;
			case TRANSFORM_ROT_90:
				transformedMatrix = planeMatrix.rotate90();
				break;
				
				
			case TRANSFORM_IDENTITY:
			default:
				transformedMatrix = planeMatrix;
				break;
			}
			
			Application.debug("planeMatrix=\n"+planeMatrix+"\ntransf.=\n"+transformedMatrix);
			
			oldTransform=transform;
			
		}
		*/
	}
	
	public void setTransform(Coords directionView3D, CoordMatrix toScreenMatrix){
		

		//front or back view
		double p = plane.getCoordSys().getNormal().dotproduct(directionView3D);
		if (p>0)
			transform = CoordMatrix4x4.IDENTITY;
		else if (p<0)
			transform = CoordMatrix4x4.MIRROR_Y;		

		//Application.debug("transform=\n"+transform);
		
		Coords vx = toScreenMatrix.mul(planeMatrix.getVx());
		Coords vy = toScreenMatrix.mul(planeMatrix.getVy());
		
		//Application.debug("vx=\n"+vx+"\nvy=\n"+vy);
		
		/*
		if (vx.getX()>=0){
			if (vy.getY()>=0){
				if (vx.getX()<vx.getY())
					transform = TRANSFORM_ROT_M90;
				else if (vx.getX()<-vx.getY())
					transform = TRANSFORM_ROT_90;
				else
					transform = TRANSFORM_IDENTITY;
			}else{
				//if (vx.getX()>-vx.getY())
					transform = TRANSFORM_MIRROR_X;
				//else
				//	transform = TRANSFORM_ROT_90;
			}
		}else{
			if (vy.getY()>=0)
				transform = TRANSFORM_MIRROR_Y;
			else{
				if (-vx.getX()>vx.getY())
					transform = TRANSFORM_MIRROR_O;
				else
					transform = TRANSFORM_ROT_M90;
			}
		}
		*/
		
		updateMatrix();
		
		
		//TODO only if new matrix != old matrix
		updateAllDrawables(true);
		
	}
	
	

	public AffineTransform getTransform(GeoConicND conic, Coords M, Coords[] ev){

		//use already computed for this view middlepoint M and eigen vecs ev
		AffineTransform transform = new AffineTransform();			
		transform.setTransform(
				ev[0].getX(),
				ev[0].getY(),
				ev[1].getX(),
				ev[1].getY(),
				M.getX(),
				M.getY());

		return transform;
	}
	
	public String getFromPlaneString(){
		return ((GeoElement) plane).getLabel();
	}

	public String getTranslatedFromPlaneString(){
		if (plane instanceof GeoPlaneND)
			return app.getPlain("PlaneA",((GeoElement) plane).getLabel());
		else
			return app.getPlain("PlaneFromA",((GeoElement) plane).getLabel());
	}
	
	public GeoCoordSys2D getGeoElement(){
		return plane;
	}
	
	public GeoPlaneND getPlaneContaining(){
		if (plane instanceof GeoPlaneND)
			return (GeoPlaneND) plane;
		else
			return kernel.getManager3D().Plane3D(plane);
	}
	
	public GeoDirectionND getDirection(){
		return plane;
	}
	
	
	
	
	
}
