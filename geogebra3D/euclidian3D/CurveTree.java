package geogebra3D.euclidian3D;

import java.util.LinkedList;

import geogebra.Matrix.GgbVector;
import geogebra3D.kernel3D.GeoCurveCartesian3D;

/**
 * @author André Eriksson
 * Tree representing a parametric curve
 */
public class CurveTree {
	private CurveTreeNode root;
	private CurveTreeNode start, end;
	private GeoCurveCartesian3D curve;
	private EuclidianView3D view;
	private double radius =0;
	
	/** amount of levels intially calculated*/
	private final int initialLevels = 10;
	/**minimum amount of levels that is drawn*/
	private final int forcedLevels = 5;

	static final private double pCosThreshold = 0.9995;
	static final private double tCosThreshold = 0.9995;
	static final private double distanceFactor = 10.0;
	
	
	/**
	 * @return the curve object for the tree
	 */
	public GeoCurveCartesian3D getCurve() { return curve; }
	
	/**
	 * @param curve	the curve
	 * @param view
	 */
	public CurveTree(GeoCurveCartesian3D curve, EuclidianView3D view) {
		double maxParam = curve.getMaxParameter();
		double minParam = curve.getMinParameter();
		double diff = maxParam-minParam;
		this.view=view;
		
		//create root
		this.curve = curve;
		double t = (minParam+maxParam)/2;
		root = new CurveTreeNode(curve.evaluateCurve(t), t, diff, 1, curve, null);
		
		//create start and end points
		start = new CurveTreeNode(curve.evaluateCurve(minParam), minParam, diff, 0, curve, null);
		end	  = new CurveTreeNode(curve.evaluateCurve(maxParam), maxParam, diff, 0, curve, null);
		
		addPoints(minParam,t,initialLevels-1);
		addPoints(t,maxParam,initialLevels-1);
	}
	
	private void addPoints(double min, double max, int lev) {
		double t = (max+min)*0.5;
		if(lev==0)
			return;
		insert(curve.evaluateCurve(t), t);
		addPoints(min,t,lev-1);
		addPoints(t,max,lev-1);
	}

	/**
	 * @param radius the radius of a sphere bounding the viewing volume
	 * @return a linked list of points to be rendered
	 */
	public LinkedList<GgbVector> getPoints(double radius){
		this.radius=radius;
		LinkedList<GgbVector> temp = new LinkedList<GgbVector>();
		
		
		if(subtreeVisible(start))
			temp.add(start.getPos());
		
		refine(start,root,end,temp,1);
		
		if(subtreeVisible(end))
			temp.add(end.getPos());
		
		return temp;
	}
	
	private boolean subtreeVisible(CurveTreeNode n){
		return n.boundingBoxIntersect(radius);
		//return n.boundingBoxIntersect(bbMin,bbMax);
	}
	
	private boolean pointVisible(CurveTreeNode n){
		/*GgbVector temp = n.getPos();
		double x = temp.getX();
		double y = temp.getY();
		double z = temp.getZ();
		if(x>bbMin.getX())
			if(x<bbMax.getX())
				if(y>bbMin.getY())
					if(y<bbMax.getY())
						if(z>bbMin.getZ())
							if(z<bbMax.getZ())
								return true;
		return false;*/
		return n.getPos().norm()<radius;
	}
	
	private void refine(CurveTreeNode n1, CurveTreeNode n2, 
						 CurveTreeNode n3, LinkedList<GgbVector> points, int level){
		
		if(!subtreeVisible(n2))
			return;
		if(angleTest(n1,n2,n3) || level<= forcedLevels){ //only refine if we have to
			
			//if the left subtree is big enough (in screen coordinates), refine it
			if(distanceTest(n1,n2))
				refine(n1,n2.getLeftChild(),n2, points,level+1);
			
			//add middle point
			if(pointVisible(n2))
				points.add(n2.getPos());
			
			//refine right subtree
			if(distanceTest(n2,n3))
				refine(n2,n2.getRightChild(),n3, points, level+1);
		}
	}
	
	/** Tests if the segments defined by n1,n2,n3 are nearly in C1.
	 * @param n1 "leftmost" node
	 * @param n2 "middle" node
	 * @param n3 "rightmost" node
	 * @return false if the values are nearly continuous, otherwise true
	 */
	private boolean angleTest(CurveTreeNode n1, CurveTreeNode n2, CurveTreeNode n3){
		GgbVector p1 = n1.getPos();
		GgbVector p2 = n2.getPos();
		GgbVector p3 = n3.getPos();
		GgbVector t1 = n1.getTangent();
		GgbVector t2 = n2.getTangent();
		GgbVector t3 = n3.getTangent();
		
		GgbVector dp1 = p2.sub(p1);
		GgbVector dp2 = p3.sub(p2);
		
		GgbVector dt1 = t2.sub(t1);
		GgbVector dt2 = t3.sub(t2);
		
		double pCosAng = dp1.normalized().dotproduct(dp2.normalized());
		double tCosAng = dt1.normalized().dotproduct(dt2.normalized());
		
		if( pCosAng < pCosThreshold || tCosAng < tCosThreshold || 
				Double.isNaN(pCosAng) || Double.isNaN(tCosAng) )
			return true;
		return false;
	}
	
	private boolean distanceTest(CurveTreeNode n1, CurveTreeNode n2){
		GgbVector p1 = n1.getPos();
		GgbVector p2 = n2.getPos();
		double scale = view.getScale();
		if(p1.sub(p2).norm()>distanceFactor/scale)
			return true;
		return false;
	}
	
	/** inserts a node into the tree
	 * @param pos 	node position
	 * @param param node parameter value
	 */
	public void insert(GgbVector pos, double param) {root.insert(pos,param);}
}

/**a node in CurveTree
 * @author André Eriksson
 * @see CurveTree
 */
class CurveTreeNode{
	private GgbVector pos;
	private GgbVector tangent;
	private double param;
	
	private final int level;
	private final double diff;
	
	private final double deltaParam = 1e-10;
	
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
		
		return (pos.norm()-boundingRadius < radius || Double.isInfinite(pos.norm()));
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