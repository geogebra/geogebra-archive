package geogebra3D.euclidian3D;

import geogebra.Matrix.GgbVector;
import geogebra3D.euclidian3D.opengl.PlotterSurface;
import geogebra3D.kernel3D.GeoFunction2Var;

public class SurfaceTree {
	private SurfaceTreeNode root1, root2;
	private GeoFunction2Var function;
	private double radSq;
	
	private final double normalDotThreshold = 0.95;
	private final double areaFactor = 10.0;
	private final double maxArea = 1.0;
	private final double minArea = 0.005;
	private final int	 minLevels = 5; 
	private final EuclidianView3D view;
	
	SurfaceTree(GeoFunction2Var function, EuclidianView3D view){
		this.function=function;
		this.view=view;
		GgbVector p1 = function.evaluatePoint(-1,-1);
		GgbVector p2 = function.evaluatePoint(1,-1);
		GgbVector p3 = function.evaluatePoint(1,1);
		GgbVector p4 = function.evaluatePoint(-1,1);

		root1 = new SurfaceTreeNode(p1,p3,p4);
		root2 = new SurfaceTreeNode(p3,p1,p2);
	}
	
	/**
	 * Tests if a segment is partly or wholly within the viewing volume
	 * @param n1 start of segment
	 * @param n2 end of segment
	 */
	private boolean segmentVisible(GgbVector n1, GgbVector n2) {
		if(n1.squareNorm() < radSq)
			return true;
		if(n2.squareNorm() < radSq)
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
		if((x1+u*dx)*(x1+u*dx)+(y1+u*dy)*(y1+u*dy)+(z1+u*dz)*(z1+u*dz)<radSq)
			return true;
		return false;
	}
	
	public void setRadius(double r){
		radSq=r*r;
		while(radSq>root1.p3.getX()*root1.p3.getX()+root1.p3.getY()*root1.p3.getY())
			expand();
	}
	
	public void beginRefine(PlotterSurface surf){
		refine(surf,root1,root2,null,null,1);
		refine(surf,root2,root1,null,null,1);
	}
	
	boolean oddExpansion = false;

	private void expand(){
		SurfaceTreeNode newRoot1,newRoot2;
		GgbVector p1,p2,p3,p4;
		double dX = root2.p3.getX()-root1.p3.getX();
		double dY = root1.p3.getY()-root2.p3.getY();
		
		double xb,yb;
		
		if(oddExpansion){
			xb = root2.p3.getX();
			yb = root2.p3.getY();
		} else {
			xb = root1.p3.getX();
			yb = root1.p3.getY();
		}
		p1 = function.evaluatePoint(xb-2*dX,yb-2*dY);	//lower left corner
		p2 = function.evaluatePoint(xb+2*dX,yb-2*dY); 	//lower right corner
		p3 = function.evaluatePoint(xb+2*dY, yb+2*dY);	//upper right corner
		p4 = function.evaluatePoint(xb-2*dX,yb+2*dY);	//upper left corner

		newRoot1 = new SurfaceTreeNode(p1,p3,p4);
		newRoot2 = new SurfaceTreeNode(p3,p1,p2);
		
		if(oddExpansion){
			newRoot1.subdivide(function);
			SurfaceTreeNode child = newRoot1.getChild(0);
			child.subdivide(function);
			child.setChild(0,root1);
			child.setChild(3,root2);
		} else {
			newRoot2.subdivide(function);
			SurfaceTreeNode child = newRoot2.getChild(0);
			child.subdivide(function);
			child.setChild(0,root2);
			child.setChild(3,root1);
		}
		root1 = newRoot1;
		root2 = newRoot2;
		
		oddExpansion=!oddExpansion;
	}
	
	private boolean nodeVisible(SurfaceTreeNode n) {
		if(segmentVisible(n.p1,n.p2)||segmentVisible(n.p2,n.p3)||segmentVisible(n.p3,n.p1))
			return true;
		return false;
	}
	
	private boolean normalDifferenceTooLarge(SurfaceTreeNode n0, SurfaceTreeNode n1) {
		if(n1==null)
			return false;
		double d = n0.normal.dotproduct(n1.normal);
		return d<normalDotThreshold;
	}
	
//	private void trianglePointDist(GgbVector p, GgbVector t1, GgbVector t2, GgbVector t3){
//		double a,b,c,d,e,f,delta,det,s,t,invDet, numer, denom;
//		GgbVector E0 = t2.sub(t1);
//		GgbVector E1 = t3.sub(t1);
//		GgbVector B = t1;
//		GgbVector D = B.sub(p);
//		a = E0.dotproduct(E0);
//		b = E0.dotproduct(E1);
//		c = E1.dotproduct(E1);
//		d = E0.dotproduct(D);
//		e = E1.dotproduct(D);
//		f = D.dotproduct(D);
//		delta = Math.abs(a*c-b*b);
//		
//		det = a*c-b*b; s = b*e-c*d; t = b*d-a*e;
//		if ( s+t <= det ) {
//			if ( s < 0 ) {
//				if ( t < 0 ) { 
//					//region 4 
//				} else {
//					s = 0;
//					t = ( e >= 0 ? 0 : ( -e >= c ? 1 : -e/c ) );
//				} 
//			} else if ( t < 0 ) {
//				//region 5 
//			} else {
//				invDet = 1/det;
//				s *= invDet;
//				t *= invDet;
//			}
//		} else {
//			if ( s < 0 ) { 
//				double tmp0 = b+d;
//				double tmp1 = c+e;
//				if ( tmp1 > tmp0 ) { // minimum on edge s+t=1
//					numer = tmp1 - tmp0;
//					denom = a-2*b+c;
//					s = ( numer >= denom ? 1 : numer/denom );
//					t = 1-s;
//				}
//				else { // minimum on edge s=0
//					s = 0;
//					t = ( tmp1 <= 0 ? 1 : ( e >= 0 ? 0 : -e/c ) );
//				} 
//			} else if ( t < 0 ) {
//				//region 6 
//			} else { 
//				if ( numer <= 0 )
//					s = 0;
//				else {
//					denom = a-2*b+c; // positive quantity
//					s = ( numer >= denom ? 1 : numer/denom );
//				}
//				t = 1-s;
//			}
//		}
//	}
	
	private void refine(PlotterSurface surf, SurfaceTreeNode n0, SurfaceTreeNode n1,
						SurfaceTreeNode n2, SurfaceTreeNode n3, int level){
		//culling
		if(!nodeVisible(n0) && level > minLevels || n0.area < minArea){
			surf.drawTriangle(n0.p1,n0.p2,n0.p3);
			return;
		}

		//refine depending on normal differences
		if(level < minLevels || n0.area>maxArea || normalDifferenceTooLarge(n0,n1) 
				|| normalDifferenceTooLarge(n0,n2) || normalDifferenceTooLarge(n0,n3)){
			n0.subdivide(function);
			if(n1!=null)
				n1.subdivide(function);
			if(n2!=null)
				n2.subdivide(function);
			if(n3!=null)
				n3.subdivide(function);

			SurfaceTreeNode n1Child1 = n1!=null?n1.getChild(1):null;
			SurfaceTreeNode n1Child2 = n1!=null?n1.getChild(2):null;
			SurfaceTreeNode n2Child2 = n2!=null?n2.getChild(2):null;
			SurfaceTreeNode n2Child3 = n2!=null?n2.getChild(3):null;
			SurfaceTreeNode n3Child1 = n3!=null?n3.getChild(1):null;
			SurfaceTreeNode n3Child3 = n3!=null?n3.getChild(3):null;
			if(nodeVisible(n0.getChild(0)))
				refine(surf,n0.getChild(0),n0.getChild(3),n0.getChild(1),n0.getChild(2),level+1);
			if(nodeVisible(n0.getChild(1)))
				refine(surf,n0.getChild(1),n1Child2,n0.getChild(0),n3Child3,level+1);
			if(nodeVisible(n0.getChild(2)))
				refine(surf,n0.getChild(2),n1Child1,n2Child3,n0.getChild(0),level+1);
			if(nodeVisible(n0.getChild(3)))
				refine(surf,n0.getChild(3),n0.getChild(0),n2Child2,n3Child1,level+1);
			
			/*refine(surf,n0.getFirstChild(function),n3,n1.getSecondChild(function),n0.getSecondChild(function));
			refine(surf,n0.getSecondChild(function),n2,n0.getFirstChild(function),n1.getFirstChild(function));*/
		} else {
			surf.drawTriangle(n0.p1,n0.p2,n0.p3);
		}
	}
	
	private boolean triangleLargeEnough(SurfaceTreeNode v0){
		return v0.area<areaFactor/view.getScale();
	}
}

class SurfaceTreeNode {
	public final GgbVector p1,p2,p3;
	public final GgbVector normal;
	public final double area;
	public boolean childrenDefined = false;
	
	
	//private SurfaceTreeNode child1, child2;
	private SurfaceTreeNode[] children = new SurfaceTreeNode[4];
	
	public SurfaceTreeNode getChild(int i){return children[i];}
	public void setChild(int i, SurfaceTreeNode child){ children[i]=child; }
	
	public void subdivide(GeoFunction2Var func) {
		if(childrenDefined)
			return;
		
		double x1 = (p2.getX()-p1.getX())*0.5+p1.getX();
		double y1 = (p2.getY()-p1.getY())*0.5+p1.getY();
		double x2 = (p3.getX()-p2.getX())*0.5+p2.getX();
		double y2 = (p3.getY()-p2.getY())*0.5+p2.getY();
		double x3 = (p1.getX()-p3.getX())*0.5+p3.getX();
		double y3 = (p1.getY()-p3.getY())*0.5+p3.getY();
		
		GgbVector m1 = func.evaluatePoint(x1,y1);
		GgbVector m2 = func.evaluatePoint(x2,y2);
		GgbVector m3 = func.evaluatePoint(x3,y3);
		
		children[0] = new SurfaceTreeNode(m2,m3,m1);
		children[1] = new SurfaceTreeNode(p1,m1,m3);
		children[2] = new SurfaceTreeNode(m1,p2,m2);
		children[3] = new SurfaceTreeNode(m3,m2,p3);
		
		childrenDefined=true;
	}
	
	SurfaceTreeNode(GgbVector p1, GgbVector p2, GgbVector p3){
		this.p1=p1;
		this.p2=p2;
		this.p3=p3;
		normal = p2.sub(p1).crossProduct(p3.sub(p1)).normalized();
		area = p2.sub(p1).crossProduct(p3.sub(p1)).norm()*0.5;
	}
}