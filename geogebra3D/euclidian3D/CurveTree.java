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
	private double minParam, maxParam;
	private GgbVector minPoint, maxPoint;
	private GeoCurveCartesian3D curve;
	
	/**
	 * @return the curve object for the tree
	 */
	public GeoCurveCartesian3D getCurve() { return curve; }
	
	/**
	 * @param curve			the curve
	 * @param initialLevels number of initial levels of refinement
	 */
	public CurveTree(GeoCurveCartesian3D curve, int initialLevels) {
		maxParam = curve.getMaxParameter();
		minParam = curve.getMinParameter();
		this.curve = curve;
		double t = (minParam+maxParam)/2;
		root = new CurveTreeNode(curve.evaluateCurve(t), t, this);
		
		addPoints(minParam,t,initialLevels-1);
		addPoints(t,maxParam,initialLevels-1);
	}
	
	private void addPoints(double min, double max, int lev) {
		double t = (max+min)*0.5;
		if(lev==0)
			return;
		root.insert(new CurveTreeNode(curve.evaluateCurve(t), t, this));
		addPoints(min,t,lev-1);
		addPoints(t,max,lev-1);
	}

	/**
	 * retrieves a curve based on 
	 * @param bbMin
	 * @param bbMax
	 * @param viewpoint
	 */
	public LinkedList<GgbVector> getPoints(GgbVector bbMin, GgbVector bbMax, GgbVector viewpoint){
	
		LinkedList<CurveTreeNode> temp = new LinkedList<CurveTreeNode>();
		
		//if the starting point is in the bounding box, draw it
		//TODO:add start/end points
		
		root.getPoints(bbMin, bbMax, viewpoint, temp);
		
		//add the end point if it's in the viewing volume
		
		
		//TODO:replace the following with a better solution
		LinkedList<GgbVector> ret = new LinkedList<GgbVector>();
		for(CurveTreeNode n: temp)
			ret.add(n.getPos());
		return ret;
	}
	
	/** inserts the specified node into the tree
	 * @param n
	 */
	public void insert(CurveTreeNode n) {root.insert(n);}
}

/** An abstract class representing a node in CurveTree
 * 
 * @author André Eriksson
 * @see CurveTree
 */
class CurveTreeNode{
	private GgbVector pos;
	private double param;
	private CurveTree tree;
	private GgbVector boundingBoxMin, boundingBoxMax;
	private CurveTreeNode[] children;
	
	static final private double angleThreshold = 0.9995;
	static final private double dtMin = 0.01;
	
	public GgbVector getPos(){return pos;}
	
	@Override
	public String toString() {
		return "CurveTreeNode [param=" + param + ", pos=" + pos + "]";
	}

	/** Copy constructor
	 * @param node any well defined CurveTreeNode
	 */
	CurveTreeNode(CurveTreeNode node){
		pos = node.pos.copyVector();
		boundingBoxMax = node.pos.copyVector();
		boundingBoxMin = node.pos.copyVector();
		param = node.param;
		children = new CurveTreeNode[2];
		tree = node.tree;
	}
	
	/**
	 * @param pos	spatial position
	 * @param param parameter value
	 * @param tree a reference to the tree that contains the node
	 */
	CurveTreeNode(GgbVector pos, double param, CurveTree tree){
		this.pos = pos.copyVector();
		boundingBoxMax = pos.copyVector();
		boundingBoxMin = pos.copyVector();
		this.param = param;
		this.children = new CurveTreeNode[2];
		this.tree = tree;
	}
	
	/**
	 * Returns a collection of points depending on their visibility and distance to viewpoint
	 * @param bbMin		minimum corner of the bounding box
	 * @param bbMax		maximum corner of the bounding box
	 * @param viewpoint	a point representing the location of the camera
	 * @param points	a reference to the collection of points being built
	 */
	public void getPoints(GgbVector bbMin, GgbVector bbMax, GgbVector viewpoint, LinkedList<CurveTreeNode> points){
		if(boundingBoxIntersect(bbMin, bbMax)){
			//draw left subtree first
			if(children[0]!=null)
				children[0].getPoints(bbMin,bbMax,viewpoint,points);
			
			//always draw the first two points
			if(points.size()<2)
				points.add(this);
			else if(shouldDraw(viewpoint, points)){ //see if we want to draw this point
				//check if we should refine before drawing this
				
				//TODO:uncomment the following line, fix!
				//attemptRefine(viewpoint, points);
				
				//then this
				points.add(this);
			}
				
			//then right subtree
			if(children[1]!=null)
				children[1].getPoints(bbMin,bbMax,viewpoint,points);
		}
	}
	
	private void attemptRefine(GgbVector viewpoint, LinkedList<CurveTreeNode> points){
		CurveTreeNode n2 = points.getLast();
		points.removeLast();
		CurveTreeNode n1 = points.getLast();
		CurveTreeNode n3 = this;
		
		GgbVector v1 = n2.pos.sub(n1.pos);
		GgbVector v2 = n3.pos.sub(n2.pos);
		
		double cosAng = v1.dotproduct(v2)/(v1.norm()*v2.norm());
		
		if(cosAng<angleThreshold){
			//add points on each side of curr
			points.add(n1);
			points.addAll(refine(n1,n2));
			points.add(n2);
			points.addAll(refine(n2,n3));
		}
	}
	
	/**
	 * Recursive helper method for findPoints. Subdivides a segment until smooth.
	 * @param p1 The first point of the segment
	 * @param p2 The second point of the segment
	 * @param t1 Parameter value for the first point
	 * @param t2 Parameter value for the second point
	 * @param curve
	 * @return
	 */
	private LinkedList<CurveTreeNode> refine(CurveTreeNode n1, CurveTreeNode n2){
		LinkedList<CurveTreeNode> tempList = new LinkedList<CurveTreeNode>();
		double t1 = n1.param;
		double t2 = n2.param;
		GgbVector p1 = n1.pos;
		GgbVector p2 = n2.pos;
		
		//stop the recursion if the distance is too small
		if(t2-t1<dtMin)
			return tempList;
		
		//set values for the new point
		double t3 = (t1+t2)/2;
		GgbVector p3=tree.getCurve().evaluateCurve(t3);
		CurveTreeNode n3 = new CurveTreeNode(p3, t3, tree);
		
		GgbVector v1 = p3.sub(p1);
		GgbVector v2 = p2.sub(p3);
		double cosAng = v1.dotproduct(v2)/(v1.norm()*v2.norm());
		if( cosAng < angleThreshold ){
			//recurse if the angle is still too big
			tempList.addAll(refine(n1,n3));
			tempList.add(n3);
			tempList.addAll(refine(n3,n1));
		} else {
			tempList.add(n3);
		}
		tree.insert(n3);
		
		return tempList;
	}
	
	private boolean shouldDraw(GgbVector viewpoint, LinkedList<CurveTreeNode> points){
		double viewDist = viewpoint.distance(pos);
		double prevDist = points.getLast().pos.distance(pos);
		
		//for now just a linear measure
		if(viewDist/prevDist>10)
			return false;
		
		return true;
	}
	
	/** Checks if the bounding box of this leaf intersects with the one defined by bbMin and bbMax
	 * 
	 * @param bbMin minimum corner of bounding box
	 * @param bbMax maximum corner of bounding box
	 * @return true if the boxes intersect, false otherwise
	 */
	private boolean boundingBoxIntersect(GgbVector bbMin, GgbVector bbMax){	
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
	
	/** Recursive function that inserts a node into the tree
	 * @param node
	 */
	public void insert(CurveTreeNode node){
		assert node.param!=param;
		
		int i = 0;

		if(node.param>this.param)
			i=1;
		
		if(children[i]==null)
			children[i] = node;
		else 
			children[i].insert(node);
		
		updateBoundingBox(node.pos);
	}

	/** Updates the bounding box to contain the given point
	 * @param point
	 */
	private void updateBoundingBox(GgbVector point){
		double x = point.getX();
		double y = point.getY();
		double z = point.getZ();
		
		if(boundingBoxMin.getX() > x)
			boundingBoxMin.setX(x);	
		if(boundingBoxMin.getY() > y)
			boundingBoxMin.setY(y);
		if(boundingBoxMin.getZ() > z)
			boundingBoxMin.setZ(z);

		if(boundingBoxMax.getX() < x)
			boundingBoxMax.setX(x);	
		if(boundingBoxMax.getY() < y)
			boundingBoxMax.setY(y);
		if(boundingBoxMax.getZ() < z)
			boundingBoxMax.setZ(z);
	}
}