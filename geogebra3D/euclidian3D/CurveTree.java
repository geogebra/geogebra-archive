package geogebra3D.euclidian3D;

import java.awt.Point;

import geogebra.Matrix.GgbVector;
import geogebra3D.kernel3D.GeoCurveCartesian3D;

/**
 * @author Andr� Eriksson
 * Tree representing a parametric curve
 */
public class CurveTree {
	public CurveTreeNode root;
	public CurveTreeNode start, end;
	public GeoCurveCartesian3D curve;
	public EuclidianView3D view;
	public double radius =0;
	
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
	
	private void addPoints(double min, double max, int lev) {
		double t = (max+min)*0.5;
		if(lev==0)
			return;
		insert(curve.evaluateCurve(t), t);
		addPoints(min,t,lev-1);
		addPoints(t,max,lev-1);
	}
	
	public boolean subtreeVisible(CurveTreeNode n){
		return n.boundingBoxIntersect(radius);
		//return n.boundingBoxIntersect(bbMin,bbMax);
	}
	
	public boolean segmentVisible(GgbVector n1, GgbVector n2) {
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
	
	public boolean pointVisible(CurveTreeNode n){
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
	

	
	/** Tests if the segments defined by n1,n2,n3 are nearly in C1.
	 * @param n1 "leftmost" node
	 * @param n2 "middle" node
	 * @param n3 "rightmost" node
	 * @return false if the values are nearly continuous, otherwise true
	 */
	public boolean angleTest(CurveTreeNode n1, CurveTreeNode n2, CurveTreeNode n3){
		GgbVector p1 = n1.getPos();
		GgbVector p2 = n2.getPos();
		GgbVector p3 = n3.getPos();
		
		GgbVector dp1 = p2.sub(p1);
		GgbVector dp2 = p3.sub(p2);
		
		double pCosAng = dp1.normalized().dotproduct(dp2.normalized());
		
		if( pCosAng < pCosThreshold || Double.isNaN(pCosAng))
			return true;
		return false;
	}
	
	public boolean tangentTest(CurveTreeNode n1, CurveTreeNode n2) {
		double tCosAng = n2.getTangent().dotproduct(n1.getTangent());
		if(tCosAng < tCosThreshold)
			return true;
		return false;
	}
	
	public boolean distanceTest(CurveTreeNode n1, CurveTreeNode n2){
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
	public void insert(GgbVector pos, double param) {root.insert(pos,param);}
}