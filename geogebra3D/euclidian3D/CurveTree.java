package geogebra3D.euclidian3D;

import geogebra.Matrix.GgbVector;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
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
	private final int initialLevels = 8;
	/**minimum amount of levels that is drawn*/
	public final int forcedLevels = 3;

	static final private double pCosThreshold = 0.995;
	static final private double tCosThreshold = 0.95;
	static final private double minParamDist = 1e-5;
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
	
	/**
	 * Sets the viewing volume radius
	 * @param r
	 */
	public void setRadius(double r){
		this.radius = r;
	}
	
	/** Starts refining the tree
	 * @param brush a reference to the calling brush
	 */
	public void beginRefine(PlotterBrush brush){
		refine(brush,start,root,end,1);
	}
	
	
	/**
	 * If the start point is well-defined and visible, it is drawn using
	 * brush.addPointToCurve().
	 * @param brush a reference to the calling brush
	 */
	public void drawStartPointIfVisible(PlotterBrush brush){
		GgbVector pos = start.getPos();
		if(pos.isDefined() && pos.isFinite() 
				&& pointVisible(pos))
			brush.addPointToCurve(pos,start.getTangent());
	}
	
	/**
	 * If the end point is well-defined and visible, it is drawn using
	 * brush.addPointToCurve().
	 * @param brush a reference to the calling brush
	 */
	public void drawEndPointIfVisible(PlotterBrush brush){
		GgbVector pos = end.getPos();
		if(pos.isDefined() && pos.isFinite() 
				&& pointVisible(pos))
			brush.addPointToCurve(pos,end.getTangent());
	}
	
	/**
	 * Function that recursively draws a curve segment depending on curvature,
	 * zoom level, and tangent information. Draws using brush.addPointToCurve().
	 * @param brush a reference to the calling brush
	 * @param n1 the left point of the segment
	 * @param n2 the center point of the segment
	 * @param n3 the end point of the segment
	 * @param level the current level of recursion
	 */
	public void refine(PlotterBrush brush, CurveTreeNode n1, CurveTreeNode n2, 
						 CurveTreeNode n3, int level){
		GgbVector p1 = n1.getPos();
		GgbVector p2 = n2.getPos();
		GgbVector p3 = n3.getPos();
		if(level <= forcedLevels || angleTooSharp(p1, p2, p3)){
			//if the left segment is visible and passes the distance test, refine it
			if(segmentVisible(p1,p2))
				if(distanceLargeEnough(n1, n2))
					refine(brush,n1,n2.getLeftChild(),n2,level+1);
			
			//draw the center point if it is defined
			if(p2.isDefined() && p2.isFinite())
				brush.addPointToCurve(p2,n2.getTangent());
			
			//if the right segment is visible and passes the distance test, refine it
			if(segmentVisible(p2,p3))
				if(distanceLargeEnough(n2, n3))
					refine(brush,n2,n2.getRightChild(),n3,level+1);
			
		} else {
			//if the left segment is visible, and the tangent and distance tests are passed
			//for the same segment, refine it 
			if(segmentVisible(p1,p2))
				if(tangentTooDifferent(n1,n2))
					if(distanceLargeEnough(n1, n2))
						refine(brush,n1,n2.getLeftChild(),n2,level+1);
			
			//draw the center point if it is defined
			if(p2.isDefined() && p2.isFinite())
				brush.addPointToCurve(p2,n2.getTangent());
			
			//if the right segment is visible, and the tangent and distance tests are passed
			//for the same segment, refine it 
			if(segmentVisible(p2,p3))
				if(tangentTooDifferent(n2,n3))
					if(distanceLargeEnough(n2, n3))
						refine(brush,n2,n2.getRightChild(),n3,level+1);
		}
	}
	/**
	 * Function that recursively inserts points between min and max.
	 * Only to be used in the constructor.
	 * @param min minimum parameter value
	 * @param max maximum parameter value
	 * @param lev the depth to which should continue
	 */
	private void addPoints(double min, double max, int lev) {
		double t = (max+min)*0.5;
		if(lev==0)
			return;
		insert(curve.evaluateCurve(t), t);
		addPoints(min,t,lev-1);
		addPoints(t,max,lev-1);
	}
	
	/**
	 * Tests if a segment is partly or wholly within the viewing volume
	 * @param n1 start of segment
	 * @param n2 end of segment
	 */
	private boolean segmentVisible(GgbVector n1, GgbVector n2) {
		if(n1.norm() < radius)
			return true;
		if(n2.norm() < radius)
			return true;
		if(!n2.isDefined() || !n2.isDefined())
			return true;
		double x1,x2,y1,y2,z1,z2,dx,dy,dz,u;
		
		x1=n1.getX();
		x2=n2.getX();
		y1=n1.getY();
		y2=n2.getY();
		z1=n1.getZ();
		z2=n2.getZ();
		dx=x2-x1;
		dy=y2-y1;
		dz=z2-z1;
		u=-(x1*dx+y1*dy+z1*dz)/(dx*dx+dy*dy+dz*dz);
		if((x1+u*dx)*(x1+u*dx)+(y1+u*dy)*(y1+u*dy)+(z1+u*dz)*(z1+u*dz)<radius*radius)
			return true;
		return false;
	}
	
	/**
	 * Tests if the given point is visible.
	 * Currently just tests if the point is within the viewing radius.
	 * @param pos
	 * @return
	 */
	private boolean pointVisible(GgbVector pos){
		return pos.norm()<radius;
	}
	

	
	/** Tests if the segments defined by n1,n2,n3 are nearly in C1.
	 * @param n1 "leftmost" node
	 * @param n2 "middle" node
	 * @param n3 "rightmost" node
	 * @return false if the values are nearly continuous, otherwise true
	 */
	private boolean angleTooSharp(GgbVector p1, GgbVector p2, GgbVector p3){
		GgbVector dp1 = p2.sub(p1);
		GgbVector dp2 = p3.sub(p2);
		
		double pCosAng = dp1.normalized().dotproduct(dp2.normalized());
		
		if( pCosAng < pCosThreshold || Double.isNaN(pCosAng))
			return true;
		return false;
	}
	
	/**
	 * Tests if a segment between two points should be refined based on how close to
	 * continuous their tangents are.
	 * @param n1
	 * @param n2
	 * @return true if the cosine of the angle between the tangents is < than tCosThreshold
	 */
	private boolean tangentTooDifferent(CurveTreeNode n1, CurveTreeNode n2) {
		double tCosAng = n2.getTangent().dotproduct(n1.getTangent());
		if(tCosAng < tCosThreshold)
			return true;
		return false;
	}
	
	/**
	 * Tests if two nodes are far enough, both in their parameter values and their
	 * spatial values, to be refined.
	 * @param n1
	 * @param n2
	 * @return
	 */
	private boolean distanceLargeEnough(CurveTreeNode n1, CurveTreeNode n2){
		//test parameter distance
		if(Math.abs(n1.getParam()-n2.getParam())<minParamDist)
			return false;
		
		GgbVector p1 = n1.getPos();
		GgbVector p2 = n2.getPos();
		double scale = view.getScale();
		double diff = p1.sub(p2).norm();
		if(diff>distanceFactor/scale)
			return true;
		if(Double.isNaN(diff) || Double.isInfinite(diff))
			return true;
		return false;
	}
	
	/** inserts a node into the tree
	 * @param pos 	node position
	 * @param param node parameter value
	 */
	private void insert(GgbVector pos, double param) {root.insert(pos,param);}
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
	
	/** the difference in the paramater value used for tangent estimations*/
	private final double deltaParam = 1e-8;
	
	private CurveTreeNode[] children;
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
		this.param = param;
		this.children = new CurveTreeNode[2];
		this.level = level;
		this.diff = diff;
		this.curve=curve;
		
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
		}
		return children[1];
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
	}
	
	/** Approximates the tangent by a simple forward difference quotient.
	 * 	Should only be called in the constructor.
	 */
	private void approxTangent(){
		GgbVector d = curve.evaluateCurve(param+deltaParam);
		tangent = d.sub(pos).normalized();
	}
}