package geogebra3D.euclidian3D;

import java.util.LinkedList;

import geogebra.Matrix.GgbVector;
import geogebra3D.euclidian3D.opengl.Renderer;
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

	static final private double cosThreshold = 0.99995;
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
		root = new CurveTreeNode(curve.evaluateCurve(t), t, diff, 1, curve);
		
		//create start and end points
		start = new CurveTreeNode(curve.evaluateCurve(minParam), minParam, diff, 0, curve);
		end	  = new CurveTreeNode(curve.evaluateCurve(maxParam), maxParam, diff, 0, curve);
		
		addPoints(minParam,t,5-1);
		addPoints(t,maxParam,5-1);
		updateRadius();
	}
	
	private void addPoints(double min, double max, int lev) {
		double t = (max+min)*0.5;
		if(lev==0)
			return;
		root.insert(curve.evaluateCurve(t), t);
		addPoints(min,t,lev-1);
		addPoints(t,max,lev-1);
	}
	
	/** updates the viewing radius based on the viewing frustum 
	 */
	private void updateRadius() {
		Renderer temp = view.getRenderer();
		double x1 = temp.getLeft();
		double x2 = temp.getRight();
		double y1 = temp.getTop();
		double y2 = temp.getBottom();
		double z1 = temp.getFront();
		double z2 = temp.getBack();
		GgbVector [] v = new GgbVector[8];
		v[0] = new GgbVector(x1,y1,z1,1);
		v[1] = new GgbVector(x1,y2,z1,1);
		v[2] = new GgbVector(x1,y1,z2,1);
		v[3] = new GgbVector(x1,y2,z2,1);
		v[4] = new GgbVector(x2,y1,z1,1);
		v[5] = new GgbVector(x2,y2,z1,1);
		v[6] = new GgbVector(x2,y1,z2,1);
		v[7] = new GgbVector(x2,y2,z2,1);
		
		for(int i = 0; i < 8; i++){
			view.toSceneCoords3D(v[i]);
			if(v[i].norm()>radius)
				radius=v[i].norm();
		}
	}

	/**
	 * @return a linked list of points to be rendered
	 */
	public LinkedList<GgbVector> getPoints(){
		LinkedList<GgbVector> temp = new LinkedList<GgbVector>();
		updateRadius();
		
		
		if(subtreeVisible(start))
			temp.add(start.getPos());
		
		refine(start,root,end,temp);
		
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
										 CurveTreeNode n3, LinkedList<GgbVector> points){
		if(!subtreeVisible(n2))
			return;
		if(angleTest(n1,n2,n3)){	//only refine if we have to
			
			//if the left subtree is big enough (in screen coordinates), refine it
			if(distanceTest(n1,n2))
				refine(n1,n2.getLeftChild(),n2, points);
			
			//add middle point
			if(pointVisible(n2))
				points.add(n2.getPos());
			
			//refine right subtree
			if(distanceTest(n2,n3))
				refine(n2,n2.getRightChild(),n3, points);
		}
	}
	
	private boolean angleTest(CurveTreeNode n1, CurveTreeNode n2, CurveTreeNode n3){
		GgbVector p1 = n1.getPos();
		GgbVector p2 = n2.getPos();
		GgbVector p3 = n3.getPos();
		
		GgbVector v1 = p3.sub(p1);
		GgbVector v2 = p2.sub(p3);
		double cosAng = v1.dotproduct(v2)/(v1.norm()*v2.norm());
		if( cosAng < cosThreshold )
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

/** An abstract class representing a node in CurveTree
 * 
 * @author André Eriksson
 * @see CurveTree
 */
class CurveTreeNode{
	private GgbVector pos;
	private double param;
	
	private final int level;
	private final double diff;
	
	private GgbVector boundingBoxMin, boundingBoxMax;
	private double boundingRadius;
	private CurveTreeNode[] children;
	private GeoCurveCartesian3D curve;
	
	/**
	 * @return the spatial position of the node
	 */
	public GgbVector getPos(){return pos;}
	
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
	 */
	CurveTreeNode(GgbVector pos, double param, double diff, int level, GeoCurveCartesian3D curve){
		this.pos = pos.copyVector();
		boundingBoxMax = pos.copyVector();
		boundingBoxMin = pos.copyVector();
		this.param = param;
		this.children = new CurveTreeNode[2];
		this.level = level;
		this.diff = diff;
		this.curve=curve;
	}

	/**
	 * @return the node's left child. If the child does not exist, it is created  
	 */
	public CurveTreeNode getLeftChild(){
		if(children[0]==null){
			double childParam = param-diff/Math.pow(2,level+1);
			children[0] = new CurveTreeNode(curve.evaluateCurve(childParam),childParam, diff, level+1, curve);
		}
		return children[0];
	}
	
	/**
	 * @return the node's right child. If the child does not exist, it is created  
	 */
	public CurveTreeNode getRightChild(){
		if(children[1]==null){
			double childParam = param+diff/Math.pow(2,level+1);
			children[1] = new CurveTreeNode(curve.evaluateCurve(childParam),childParam, diff, level+1, curve);
		}
		return children[1];
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
		
		return pos.norm()-boundingRadius < radius;
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
			children[i] = new CurveTreeNode(pos, param, diff, this.level+1, curve);
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