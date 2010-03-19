package geogebra.Matrix;

import geogebra.kernel.Kernel;

/**
 * @author ggb3D
 *
 * Class for algebra utilities
 */
public final class GgbMatrixUtil {

	
	/** Return points p1 from line1 and p2 from line2 that are the nearest possible.
	 * Return infinite points if the two lines are parallel.
	 * @param line1 matrix [v1 o1] describing first line
	 * @param line2 matrix [v2 o2] describing second line
	 * @return {p1,p2,{p1 coord on l1,p2 coord on l2}}
	 */
	static final public GgbVector[] nearestPointsFromTwoLines(GgbMatrix line1, GgbMatrix line2){
		
		GgbVector v1 = line1.getColumn(1);
		GgbVector v2 = line2.getColumn(1);
		
		// if v1 and v2 are parallel, return infinite points v1 and v2
		GgbVector vn = v1.crossProduct(v2);
		if (vn.equalsForKernel(0, Kernel.STANDARD_PRECISION)){
			//Application.debug("v1="+v1.toString()+"\nv2="+v2.toString());
			return  new GgbVector[] {v1,v2, new GgbVector(new double[] {Double.NaN,Double.NaN})};
		}
		//return null;
		
		//vn.normalize();
		
		GgbVector o1 = line1.getColumn(2);
		GgbVector o2 = line2.getColumn(2);
		
		// plane containing o1, v1, vn, with v2 direction
		GgbMatrix plane = new GgbMatrix(4,4);
		plane.set(v1, 1);
		plane.set(vn, 2);
		plane.set(v2, 3);
		plane.set(o1, 4);		
		//projection of o2 on this plane
		GgbVector[] project2 = o2.projectPlane(plane);
		
		// plane containing o2, v2, vn, with v1 direction
		plane.set(v2, 1);
		plane.set(vn, 2);
		plane.set(v1, 3);
		plane.set(o2, 4);		
		//projection of o2 on this plane
		GgbVector[] project1 = o1.projectPlane(plane);
		
		//points in lines coords
		GgbVector lineCoords = new GgbVector(new double[] {-project1[1].get(3),-project2[1].get(3)});
		
		return new GgbVector[] {project1[0],project2[0], lineCoords};
	}
	
	
	/** Return the point p intersection of the line and the plane. <br>
	 * Return infinite point (direction of the line) if the line is parallel to plane.
	 * @param line the line
	 * @param plane the plane
	 * @return the point p intersection of the line and the plane. 
	 */
	static final public GgbVector intersectLinePlane(GgbMatrix line, GgbMatrix plane){
		
		GgbVector v = line.getColumn(1);
		GgbVector v1 = plane.getColumn(1);
		GgbVector v2 = plane.getColumn(2);
		GgbVector vn = v1.crossProduct(v2);
		
		// if v is orthogonal to vn, v is parallel to the plane and so the line is
		//if (Kernel.isEqual(vn.dotproduct(v),0,Kernel.STANDARD_PRECISION))
		//		return null;
		
		// project the origin of the line on the plane (along v direction)
		GgbVector o = line.getColumn(2);
		return o.projectPlaneThruV(plane, v)[0];
	}
	
	
	/** return the spherical coords of v
	 * @param v 3D vector in cartesian coords
	 * @return the spherical coords of v
	 */
	static final public GgbVector sphericalCoords(GgbVector v){
		
		double x = v.get(1);
		double y = v.get(2);
		double z = v.get(3);
		
		//norms
		double n2 = x*x+y*y;
		double n1 = Math.sqrt(n2);
		double norm = Math.sqrt(n2+z*z);
		
		//angles
		double a;
		if (n1==0)
			a = 0;
		else{
			a = Math.acos(x/n1);
			if (y<0)
				a*=-1;
		}
		
		double b;
		if (norm==0)
			b = 0;
		else{
			b = Math.acos(n1/norm);
			if (z<0)
				b*=-1;
		}
		
		
		return new GgbVector(new double[] {norm, a, b});

	}
	
	
	/** return the cartesian coords of v
	 * @param v 3D vector in spherical coords
	 * @return the cartesian coords of v
	 */
	static final public GgbVector cartesianCoords(GgbVector v){

		return cartesianCoords(v.get(1), v.get(2), v.get(3));

		
	}
	
	
	/** return the cartesian coords of (r,theta,phi)
	 * @param r spherical radius
	 * @param theta (Oz) angle
	 * @param phi (xOy) angle
	 * @return the cartesian coords of (r,theta,phi)
	 */
	static final public GgbVector cartesianCoords(double r, double theta, double phi){

		double z = r*Math.sin(phi);
		double n2 = r*Math.cos(phi);
		double x = n2*Math.cos(theta);
		double y = n2*Math.sin(theta);

		return new GgbVector(new double[] {x, y, z, 0});

		
	}
}
