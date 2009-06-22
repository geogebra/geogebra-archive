package geogebra3D.Matrix;

import geogebra.kernel.Kernel;

/**
 * @author ggb3D
 *
 * Class for algebra utilities
 */
public final class Ggb3DMatrixUtil {

	
	/** Return points p1 from line1 and p2 from line2 that are the nearest possible.
	 * Return infinite points if the two lines are parallel.
	 * @param line1 matrix [v1 o1] describing first line
	 * @param line2 matrix [v2 o2] describing second line
	 * @return {p1,p2,{p1 coord on l1,p2 coord on l2}}
	 */
	static public Ggb3DVector[] nearestPointsFromTwoLines(Ggb3DMatrix line1, Ggb3DMatrix line2){
		
		Ggb3DVector v1 = line1.getColumn(1);
		Ggb3DVector v2 = line2.getColumn(1);
		
		// if v1 and v2 are parallel, return infinite points v1 and v2
		Ggb3DVector vn = v1.crossProduct(v2);
		if (vn.equalsForKernel(0, Kernel.STANDARD_PRECISION)){
			//Application.debug("v1="+v1.toString()+"\nv2="+v2.toString());
			return  new Ggb3DVector[] {v1,v2, new Ggb3DVector(new double[] {Double.NaN,Double.NaN})};
		}
		//return null;
		
		//vn.normalize();
		
		Ggb3DVector o1 = line1.getColumn(2);
		Ggb3DVector o2 = line2.getColumn(2);
		
		// plane containing o1, v1, vn, with v2 direction
		Ggb3DMatrix plane = new Ggb3DMatrix(4,4);
		plane.set(v1, 1);
		plane.set(vn, 2);
		plane.set(v2, 3);
		plane.set(o1, 4);		
		//projection of o2 on this plane
		Ggb3DVector[] project2 = o2.projectPlane(plane);
		
		// plane containing o2, v2, vn, with v1 direction
		plane.set(v2, 1);
		plane.set(vn, 2);
		plane.set(v1, 3);
		plane.set(o2, 4);		
		//projection of o2 on this plane
		Ggb3DVector[] project1 = o1.projectPlane(plane);
		
		//points in lines coords
		Ggb3DVector lineCoords = new Ggb3DVector(new double[] {-project1[1].get(3),-project2[1].get(3)});
		
		return new Ggb3DVector[] {project1[0],project2[0], lineCoords};
	}
	
	
	/** Return the point p intersection of the line and the plane. Return null if the line is parallel to plane.
	 * @param line the line
	 * @param plane the plane
	 * @return the point p intersection of the line and the plane. 
	 */
	static public Ggb3DVector intersectLinePlane(Ggb3DMatrix line, Ggb3DMatrix plane){
		
		Ggb3DVector v = line.getColumn(1);
		Ggb3DVector v1 = plane.getColumn(1);
		Ggb3DVector v2 = plane.getColumn(2);
		Ggb3DVector vn = v1.crossProduct(v2);
		
		// if v is orthogonal to vn, v is parallel to the plane and so the line is
		if (Kernel.isEqual(vn.dotproduct(v),0,Kernel.STANDARD_PRECISION))
				return null;
		
		// project the origin of the line on the plane (along v direction)
		Ggb3DVector o = line.getColumn(2);
		return o.projectPlaneThruV(plane, v)[0];
	}
	
}
