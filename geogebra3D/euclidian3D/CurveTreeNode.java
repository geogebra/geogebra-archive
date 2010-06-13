package geogebra3D.euclidian3D;

import geogebra.Matrix.GgbVector;
import geogebra3D.kernel3D.GeoCurveCartesian3D;


/**a node in CurveTree
 * @author André Eriksson
 * @see CurveTree
 */
public class CurveTreeNode{
	private GgbVector pos;
	private GgbVector tangent;
	private double param;
	
	private final int level;
	private final double diff;
	
	private final double deltaParam = 1e-8;
	
	private GgbVector boundingBoxMin, boundingBoxMax;
	private double boundingRadius;
	private CurveTreeNode[] children;
	private CurveTreeNode parent;
	private GeoCurveCartesian3D curve;
	
	/**
	 * @return the spatial position of the node
	 */
	public GgbVector getPos(){return pos;}
	
	/**
	 * @return the parameter value at the point
	 */
	public double getParam(){return param;}
	
	/**
	 * @return the node tangent
	 */
	public GgbVector getTangent(){return tangent;}
	
	@Override
	public String toString() {
		return "CurveTreeNode [param=" + param + ", pos=" + pos + "]";
	}
	
	/**
	 * @param pos	spatial position
	 * @param param parameter value
	 * @param diff  the difference between the minimum and maximum parameter values
	 * @param level	the level of the tree
	 * @param curve a reference to the curve
	 * @param parent 
	 */
	CurveTreeNode(GgbVector pos, double param, double diff, int level, 
					GeoCurveCartesian3D curve, CurveTreeNode parent){
		this.pos = pos.copyVector();
		boundingBoxMax = pos.copyVector();
		boundingBoxMin = pos.copyVector();
		this.param = param;
		this.children = new CurveTreeNode[2];
		this.level = level;
		this.diff = diff;
		this.curve=curve;
		this.parent=parent;
		
		approxTangent();
	}

	/**
	 * @return the node's left child. If the child does not exist, it is created  
	 */
	public CurveTreeNode getLeftChild(){
		if(children[0]==null){
			if(this.level>40)
				System.out.print("");
			double childParam = param-diff/Math.pow(2,level+1);
			GgbVector childPos = curve.evaluateCurve(childParam);
			children[0] = new CurveTreeNode(childPos,childParam, diff, level+1, curve, this);
			updateBoundingBoxesUpwards(childPos);
		}
		return children[0];
	}
	
	/**
	 * @return the node's right child. If the child does not exist, it is created  
	 */
	public CurveTreeNode getRightChild(){
		if(children[1]==null){
			double childParam = param+diff/Math.pow(2,level+1);
			GgbVector childPos = curve.evaluateCurve(childParam);
			children[1] = new CurveTreeNode(childPos,childParam, diff, level+1, curve, this);
			updateBoundingBoxesUpwards(childPos);
		}
		return children[1];
	}
	
	private void updateBoundingBoxesUpwards(GgbVector pos){
		updateBoundingRadius(pos);
		if(parent!=null)
			parent.updateBoundingBoxesUpwards(pos);
	}
	
	/** Checks if the bounding box of this leaf intersects with the one defined by bbMin and bbMax
	 * 
	 * @param bbMin minimum corner of bounding box
	 * @param bbMax maximum corner of bounding box
	 * @return true if the boxes intersect, false otherwise
	 */
	public boolean boundingBoxIntersect(GgbVector bbMin, GgbVector bbMax){	
		if(boundingBoxMin.getX() > bbMax.getX())
			return false;
		if(bbMin.getX() > boundingBoxMax.getX())
			return false;
		if(boundingBoxMin.getY() > bbMax.getY())
			return false;
		if(bbMin.getY() > boundingBoxMax.getY())
			return false;
		if(boundingBoxMin.getZ() > bbMax.getZ())
			return false;
		if(bbMin.getZ() > boundingBoxMax.getZ())
			return false;
		return true;
	}
	
	/** checks if the subtree bounding box intersects a sphere centered at the origin
	 * @param radius radius of the sphere
	 * @return true if they intersect, otherwise false
	 */
	public boolean boundingBoxIntersect(double radius){	
		/*double rSq = radius*radius;
		double x1 = boundingBoxMin.getX();
		double x2 = boundingBoxMax.getX();
		double y1 = boundingBoxMin.getY();
		double y2 = boundingBoxMax.getY();
		double z1 = boundingBoxMin.getZ();
		double z2 = boundingBoxMax.getZ();
		if(x1*x1+y1*y1+z1*z1 < rSq) return true;
		if(x1*x1+y2*y2+z1*z1 < rSq) return true;
		if(x1*x1+y2*y2+z2*z2 < rSq) return true;
		if(x1*x1+y1*y1+z2*z2 < rSq) return true;
		if(x2*x2+y1*y1+z1*z1 < rSq) return true;
		if(x2*x2+y2*y2+z1*z1 < rSq) return true;
		if(x2*x2+y1*y1+z2*z2 < rSq) return true;
		if(x2*x2+y2*y2+z2*z2 < rSq) return true;
		return false;*/
		double p = pos.norm();
		if(p-boundingRadius < radius)
			return true;
		if(Double.isInfinite(p) || Double. isNaN(p))
			return true;
		return false;
	}
	
	/** Recursive function that inserts a node into the tree
	 * @param pos 	the node position
	 * @param param the node parameter value
	 */
	public void insert(GgbVector pos, double param){
		int i = 0;
		
		if(param>this.param)
			i=1;
		
		if(children[i]==null)
			children[i] = new CurveTreeNode(pos, param, diff, this.level+1, curve, this);
		else 
			children[i].insert(pos,param);
		updateBoundingRadius(pos);
		//updateBoundingBox(pos);
	}
	
	private void updateBoundingRadius(GgbVector pos){
		double radius = pos.sub(this.pos).norm();
		if(radius>boundingRadius)
			boundingRadius=radius;
	}
	
	/** Approximates the tangent by a simple forward difference quotient.
	 * 	Should only be called in the constructor.
	 */
	private void approxTangent(){
		GgbVector d = curve.evaluateCurve(param+deltaParam);
		tangent = d.sub(pos).normalized();
	}

//	/** Updates the bounding box to contain the given point
//	 * @param point
//	 */
//	private void updateBoundingBox(GgbVector point){
//		double x = point.getX();
//		double y = point.getY();
//		double z = point.getZ();
//		
//		if(boundingBoxMin.getX() > x)
//			boundingBoxMin.setX(x);	
//		if(boundingBoxMin.getY() > y)
//			boundingBoxMin.setY(y);
//		if(boundingBoxMin.getZ() > z)
//			boundingBoxMin.setZ(z);
//
//		if(boundingBoxMax.getX() < x)
//			boundingBoxMax.setX(x);	
//		if(boundingBoxMax.getY() < y)
//			boundingBoxMax.setY(y);
//		if(boundingBoxMax.getZ() < z)
//			boundingBoxMax.setZ(z);
//	}
}