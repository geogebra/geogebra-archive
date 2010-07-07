package geogebra3D.euclidian3D.opengl;

import java.nio.FloatBuffer;

import geogebra.Matrix.GgbVector;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.kernel3D.GeoFunction2Var;

//TODO: add frustum culling
//TODO: use a more efficient priority queue implementation
//TODO: replace periodic arrangement with null pointers
//TODO: use simpler base mesh and implement expand()

/**
 * @author André Eriksson
 */
public class SurfaceTree2 {
	private GeoFunction2Var function;
	@SuppressWarnings("unused")
	private final EuclidianView3D view;
	@SuppressWarnings("unused")
	private double radSq;
	private SplitQueue splitQueue = new SplitQueue();
	private MergeQueue mergeQueue = new MergeQueue();
	
	private SurfaceDiamond root;
	
	private DrawList drawList;

//	private double totalerror;
//	private final double errorgoal = 1.0;
//	private final double minimumerror = 1.0;
	/** base diamonds - level 0 */
	private SurfaceDiamond[][] base0 = new SurfaceDiamond[4][4];
	/** base diamonds - level -1 or lower */
	private SurfaceDiamond[][] base1 = new SurfaceDiamond[4][4];

	/**
	 * @param r the bounding radius of the viewing volume
	 */
	public void setRadius(double r){radSq=r*r;}
	
	/**
	 * @param function
	 * @param view
	 */
	public SurfaceTree2(GeoFunction2Var function, EuclidianView3D view) {
		this.function = function;
		this.view = view;
		drawList = new DrawList();
		initMesh(function.getMinParameter(0), function.getMaxParameter(0),
				 function.getMinParameter(1), function.getMaxParameter(1));
		splitQueue.add(base0[1][1]);
		optimize();
	}

	/**
	 * 
	 * @param xMin
	 * @param xMax
	 * @param yMin
	 * @param yMax
	 */
	@SuppressWarnings("unused")
	private void initSmallMesh(double xMin, double xMax, double yMin, double yMax) {
		//TODO: implement this function properly, have it replace initMesh()
		SurfaceDiamond p1 = new SurfaceDiamond(function,xMin,yMin,-1);
		SurfaceDiamond a0 = new SurfaceDiamond(function,xMin,yMax,-2);
		SurfaceDiamond p0 = new SurfaceDiamond(function,xMax,yMax,-1);
		SurfaceDiamond a1 = new SurfaceDiamond(function,xMax,yMin,-3);

		p1.toggleSplit();p0.toggleSplit();a0.toggleSplit();a1.toggleSplit();
		
		int index0 = 0;
		int index1 = 1;
		
		root = new SurfaceDiamond(function,p0,index0,p1,index1,a0,a1, 0);
		
		p0.setChild(index0, root);
		p1.setChild(index1, root);
	}
	
	/**
	 * Bootstraps a fairly complex mesh. Sets base0 and base1.
	 * @param xMin the minimum x coordinate
	 * @param xMax the maximum x coordinate
	 * @param yMin the minimum y coordinate
	 * @param yMax the maximum y coordinate
	 */
	private void initMesh(double xMin, double xMax, double yMin, double yMax) {
		int di, dj, ix, jx;
		double x, y;
		SurfaceDiamond dm;
		
		double dx = (xMax-xMin)/3.0;
		double dy = (yMax-yMin)/3.0;
		
		for(int i=0; i < 4; i++)
			for(int j=0; j<4; j++) {
				x=xMin+(0.5+i)*dx;
				y=yMin+(0.5+j)*dy;
	            base0[j][i]=new SurfaceDiamond(function,x,y,0); 
	            
	            x=xMin+i*dx;
				y=yMin+j*dy;
	            base1[j][i]=dm=new SurfaceDiamond(function,x,y,((i^j)&1)!=0?-1:-2);
	            dm.toggleSplit();
			}

		for(int i=0; i < 4; i++)
			for(int j=0; j<4; j++) {
				dm=base0[j][i];
	            di=(((i^j)&1)!=0?1:-1); dj=1;
	            ix=((2*i+1-di)>>1)%4; jx=((2*j+1-dj)>>1)%4;
	            dm.parents[0]=base1[jx][ix];
	            ix=((2*i+1+di)>>1)%4; jx=((2*j+1+dj)>>1)%4;
	            dm.parents[1]=base1[jx][ix];
	            ix=((2*i+1-dj)>>1)%4; jx=((2*j+1+di)>>1)%4;
	            dm.a[0]=base1[jx][ix];
	            ix=((2*i+1+dj)>>1)%4; jx=((2*j+1-di)>>1)%4;
	            dm.a[1]=base1[jx][ix];
	            ix=(di<0?0:3); dm.parents[0].setChild(ix, dm); dm.indices[0]=ix;
	            ix=(di<0?2:1); dm.parents[1].setChild(ix, dm); dm.indices[1]=ix;
	        }
		for(int i=0; i < 4; i++)
			for(int j=0; j<4; j++) {
				dm=base1[j][i];
	            dm.a[1]=base1[(j+3)%4][i];
	            dm.a[0]=base1[(j+1)%4][i];
	            dm.parents[0]=base1[j][(i+3)%4];
	            dm.parents[1]=base1[j][(i+1)%4];
	        }
	}
	
	private void updateCullingInfo() {
		
	}
	
	/**
	 * Expands the mesh to accomodate for a larger bounding sphere
	 */
	@SuppressWarnings("unused")
	private void expand() {
		
	}

	/** used in optimize() */
	private enum Side {MERGE,SPLIT,NONE};
	
	/**
	 * Splits/merges diamonds until target accuracy is reached
	 */
	public void optimize() {
		//TODO: update culling info for all active diamonds
		
//		int maxCount = 200;
//		int count = 0;
//		double overlap, overlap0;
//		overlap=overlap0=splitQueue.peek().error-mergeQueue.peek().error;
//		Side side = tooCoarse()?Side.SPLIT:Side.MERGE;
//		while((side!=Side.NONE || overlap0>1) && count < maxCount) {
//			if(side==Side.MERGE) {
//				merge(mergeQueue.poll());
//				if(tooCoarse())
//					side=Side.NONE;
//			} else {
//				if(iqs>0) {
//					split(splitQueue.poll());
//					if(!tooCoarse())
//						side=Side.MERGE;
//				} else {
//					side = Side.NONE;
//				}
//			}
//			
//			overlap=...;
//			if (overlap<overlap0) 
//				overlap0=overlap;
//			count++;
//		}
		int maxCount = 1000;
		int count = 0;
		
		Side side = tooCoarse()?Side.SPLIT:Side.MERGE;
		while(side!=Side.NONE && count < maxCount) {
			if(side==Side.MERGE) {
				merge(mergeQueue.peek());
				if(tooCoarse())
					side=Side.NONE;
			} else {
				split(splitQueue.peek());
				if(!tooCoarse())
					side=Side.NONE;
			}
			count++;
		}
	}
	
	/**
	 * @return true if the mesh should be refined, otherwise false
	 */
	private boolean tooCoarse(){
		if(!drawList.isFull())
				return true;
		return false;
	}

	/**
	 * Recursively splits the target diamond
	 * @param t the target diamond
	 */
	private void split(SurfaceDiamond t){
		if(t.v.getX()==-1.5 && t.v.getY()==-2.5)
			System.out.println("");
		
		//dont split a diamond that has already been split
		if(t.isSplit())
			return;
		
		SurfaceDiamond temp;
		
		t.toggleSplit();	//mark as split
		
		//recursively split parents
		if(!t.isBoundaryPoint)	
			for(int i = 0; i < 2; i++){
				temp=t.parents[i];
				if(temp==null)
					continue;
				split(temp);
				if(temp.inMergeQueue){
					mergeQueue.remove(temp);
				 	temp.inMergeQueue=false;
				}
			}
		
		//get kids, insert into priority queue
		for(int i=0;i<4;i++){		
			if(t.v.getX()==0 && t.v.getY()==-3)
				System.out.println("");
			
			temp = t.getChild(i);
			//TODO: update culling info for child
			
			if(t.isBoundaryPoint){
				//TODO: come up with a better solution for this
				double x1 = temp.v.getX()-t.v.getX();
				double y1 = temp.v.getY()-t.v.getY();
				double d1 = x1*x1+y1*y1;
				double x2 = t.v.getX()-t.a[0].v.getX();
				double y2 = t.v.getY()-t.a[0].v.getY();
				double d2 = x2*x2+y2*y2;
				if(d1>d2)
					continue;
			}
			
			if(!temp.isSplit() && !temp.isBoundaryPoint)
				splitQueue.add(temp);
			
			//add child to drawing list
			drawList.add(temp,(temp.parents[1]==t?1:0));
		}
		
		//remove from drawing list
		drawList.remove(t, 0);
		drawList.remove(t, 1);

		splitQueue.remove(t);  //remove from priority queue
		 
		mergeQueue.add(t);	//add to merge queue
	}
	
	/**
	 * Merges the triangles in the target diamond
	 * @param t the target diamond
	 */
	private void merge(SurfaceDiamond t){
		SurfaceDiamond temp;
		
		//if already merged, skip
		if(!t.isSplit())
			return;
		
		//remove kids from split queue if their other parent isn't split
		for(int i=0;i<4;i++) {
			temp=t.getChild(i);
			if(!temp.getOtherParent(t).isSplit())
				splitQueue.remove(temp);
			
			//remove children from draw list
			drawList.remove(temp,(temp.parents[1]==t?1:0));
		}
		t.toggleSplit();	//mark as not split
		splitQueue.add(t);	//add to split queue

		//TODO: update parents as needed
		
		//add parent triangles to draw list
		drawList.add(t, 0);
		drawList.add(t, 1);
	}

	/**
	 * @return 	Returns a FloatBuffer containing the current mesh as a triangle list. 
	 * 			Each triangle is represented as 9 consecutive floats. The FloatBuffer
	 * 			will probably contain extra floats - use getTriangleCount() to find out
	 * 			how many floats are valid.
	 */
	public FloatBuffer getTriangles() {
		return drawList.getTriangleBuffer();
	}
	
	/**
	 * @return the amount of triangles in the current mesh.
	 */
	public int getTriangleCount() {
		return drawList.getCount();
	}
}

abstract class SurfaceQueue {
	//TODO: speed this up - at the moment add() is O(n)
	protected SurfaceDiamond front;

	/**
	 * @return the diamond at the front of the queue.
	 */
	public SurfaceDiamond peek() { return front; }
	
	/**
	 * Inserts a diamond into the queue.
	 * @param d
	 */
	public void add(SurfaceDiamond d) {
		if(front==null) {
			front=d;
			return;
		}
		SurfaceDiamond t = front;
		while(t.error>=d.error){
			if(t==d)
				return;
			if(t.nextInQueue==null){
				t.nextInQueue=d;
				d.prevInQueue=t;
				return;
			}
			t=t.nextInQueue;
		}
		d.nextInQueue=t;
		d.prevInQueue=t.prevInQueue;
		if(t.prevInQueue!=null)
			t.prevInQueue.nextInQueue=d;
		t.prevInQueue=d;
		if(t==front)
			front=d;
	}
	
	abstract protected boolean compare(SurfaceDiamond a, SurfaceDiamond b);
	
	/**
	 * Removes a diamond from the queue.
	 * @param d
	 */
	public void remove(SurfaceDiamond d) {
		SurfaceDiamond next = d.nextInQueue;
		SurfaceDiamond prev = d.prevInQueue;
		if(next!=null)
			next.prevInQueue=prev;
		if(prev!=null)
			prev.nextInQueue=next;
		if(front==d)
			front=next;
		d.nextInQueue=d.prevInQueue=null;
	}
	
	public String toString() {
		SurfaceDiamond t = front;
		StringBuilder b = new StringBuilder("[ ");
		while(t!=null) {
			b.append("["+t.toString()+"] ");
			t=t.nextInQueue;
		}
		b.append("]");
		return b.toString();
	}
}

/**
 * A priority queue used for split operations. Sorts based on SurfaceDiamond.error.
 * @author André Eriksson
 */
class SplitQueue extends SurfaceQueue {

	@Override
	protected boolean compare(SurfaceDiamond a, SurfaceDiamond b) {
		return a.error>=b.error;
	}
}

/**
 * A priority queue used for merge operations. Sorts based on SurfaceDiamond.error.
 * @author André Eriksson
 */
class MergeQueue extends SurfaceQueue {

	@Override
	protected boolean compare(SurfaceDiamond a, SurfaceDiamond b) {
		return a.error<=b.error;
	}
}

/**
 * A list of triangles representing the current mesh.
 * @author André
 */
class DrawList {
	/** the maximum amount of triangles to allocate */
	private static final int maxCount = 40000;
	/** the current amount of triangles */
	private int count = 0;
	
	/** A buffer containing data for all the triangles. Each triangle is stored
	 *  as 9 consecutive floats (representing x/y/z values for three points).
	 *  The triangles are packed tightly.
	 */
	private FloatBuffer triBuf;
	
	/** Pointers to the front and back of the queue */
	private SurfaceTriangle front, back;

	/**
	 * Empty constuctor. Allocates memory for triBuf.
	 */
	DrawList(){
		triBuf=FloatBuffer.allocate(maxCount*9);
	}
	
	/** 
	 * @return the current amount of triangles
	 */
	public int getCount() { return count; }

	/**
	 * @return a reference to triBuf
	 */
	public FloatBuffer getTriangleBuffer() { return triBuf; }

	/**
	 * @return true if count>=maxCount - otherwise false.
	 */
	public boolean isFull() { return count>=maxCount; }
	
	private float[] getFloats(SurfaceDiamond d, int j) {
		float[] v = new float[9];
		
        SurfaceDiamond dmtab[] = new SurfaceDiamond[3];
        dmtab[1]=d.parents[j];
        if (j!=0) 	{ dmtab[0]=d.a[1]; dmtab[2]=d.a[0]; }
        else   		{ dmtab[0]=d.a[0]; dmtab[2]=d.a[1]; }
        for (int vi=0, c=0;vi<3;vi++,c+=3) {
            v[c]   = (float) dmtab[vi].v.getX();
            v[c+1] = (float) dmtab[vi].v.getY();
            v[c+2] = (float) dmtab[vi].v.getZ();
        }
        return v;
	}
	
	/**
	 * Adds a triangle to the queue.
	 * @param d The parent diamond of the triangle
	 * @param j The index of the triangle within the diamond
	 */
	public void add(SurfaceDiamond d, int j) {
		//TODO: add culling handling
		
		SurfaceTriangle t = new SurfaceTriangle(back);
		if(front==null)
			front=t;		
		if(back!=null)
			back.setNext(t);
		back=t;
		
		d.setTriangle(j,t);
		
		triBuf.position(9*count);
		triBuf.put(getFloats(d,j));
		
		t.setIndex(9*count);
		
		count++;
	}
	
	/**
	 * Removes a triangle from the queue.
	 * @param d The parent diamond of the triangle
	 * @param j The index of the triangle within the diamond
	 */
	public void remove(SurfaceDiamond d, int j) {
		//TODO: add culling handling
			
		SurfaceTriangle t = d.getTriangle(j);
		
		if(t==null)
			return;
		
		//swap back for current position
		int n = t.getIndex();
		if(count==1){
			count=0;
			back=front=null;
		} else {
			SurfaceTriangle prevBack = back;
			
			//transfer prevBack's floats to new position
			float[] f = new float[9];
			triBuf.position(prevBack.getIndex());
			triBuf.get(f);
			triBuf.position(n);
			for(float u: f)
				triBuf.put(u);
			
			if(prevBack.getPrev()!=t)
				back=prevBack.getPrev();
			
			// update pointers
			SurfaceTriangle next = t.getNext();
			SurfaceTriangle prev = t.getPrev();
			if(prev!=null)
				prevBack.setPrev(t.getPrev());
			else
				prevBack.setPrev(null);
			if(next!=null)
				prevBack.setNext(t.getNext());
			else
				prevBack.setNext(null);
			prevBack.setIndex(n);
			if(front==t)
				front=prevBack;
			count--;
		}
		
		//free triangle
		d.freeTriangle(j);
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder("[ ");
		triBuf.rewind();
		float[] t = new float[9];
		for(int i=0;i<count;i++) {
			triBuf.get(t);
			b.append("[("+t[0]+","+t[1]+") ("+t[3]+","+t[4]+") ("+t[6]+","+t[7]+")] ");
		}
		b.append("]");
		return b.toString();
	}
}

/**
 * A class representing a triangle in {@link DrawList}.
 * @author André Eriksson
 */
class SurfaceTriangle{
	private int index;
	private SurfaceTriangle next, prev;

	/** 
	 * @param prev the previous element in the queue
	 */
	SurfaceTriangle(SurfaceTriangle prev) {
        this.prev=prev;
	}
	
	/**
	 * Sets the triangle's index in the float buffer.
	 * @param i 
	 */
	public void setIndex(int i) { index = i; }
	
	/**
	 * @return the triangle's index in the float buffer.
	 */
	public int getIndex() { return index; }
	
	/** 
	 * @return a reference to the next triangle in the queue.
	 */
	public SurfaceTriangle getNext() { return next; }

	/**
	 * @param next a reference to the next triangle in the queue.
	 */
	public void setNext(SurfaceTriangle next) { this.next = next; }

	/**
	 * @return a reference to the previous triangle in the queue.
	 */
	public SurfaceTriangle getPrev() { return prev; }
	
	/**
	 * @param prev a reference to the previous triangle in the queue.
	 */
	public void setPrev(SurfaceTriangle prev) { this.prev = prev; }
}

/**
 * A class representing a diamond.
 * @author André Eriksson
 */
class SurfaceDiamond {
	/** vertex position */
	GgbVector v;
	/** radius of sphere bound squared */
	double boundingRad;
	/** error measure */
	double error;
	/** diamond's parents (two of its corners) */
	SurfaceDiamond[] parents = new SurfaceDiamond[2];
	/** the other two corners */
	SurfaceDiamond[] a = new SurfaceDiamond[2];
	/** children */
	private SurfaceDiamond[] children = new SurfaceDiamond[4];
	/** the index of this diamond within each of its parents */
	int[] indices = new int[2];
	/** level of resolution */
	final int level;
	/** a reference to the function used */
	GeoFunction2Var func;
	/** flag indicating if the diamond has been split */
	private boolean split = false;
	/** flag indicating if the diamond is in the merge queue */
	boolean inMergeQueue = false;
	
	/** indicates whether or not the point is at the boundary of or outside
	 *  the drawing area */
	boolean isBoundaryPoint = false;
	
	/** indicates whether or not the point is outside the viewing area */
	boolean isOutside = false;
	
	enum CullInfo { IN, OUT }
	

	/** pointer to the next element in a priority queue */
	SurfaceDiamond nextInQueue;
	/** pointer to the previous element in a priority queue */
	SurfaceDiamond prevInQueue;
	
	private SurfaceTriangle[] triangles = new SurfaceTriangle[2];

	/**
	 * @return true if the diamond is split, otherwise false
	 */
	public boolean isSplit(){return split;}
	
	/**
	 * toggles the split flag for the diamond
	 */
	public void toggleSplit() { split = !split; }
	
	/**
	 * @param j either 0 or 1.
	 * @return the triangle with index j or null if that triangle does not exist.
	 */
	public SurfaceTriangle getTriangle(int j) { return triangles[j]; }
	/**
	 * sets triangles[j] to t.
	 * @param j either 0 or 1.
	 * @param t
	 */
	public void setTriangle(int j, SurfaceTriangle t) { triangles[j]=t; }
	/**
	 * releases the triangle with index j
	 * @param j either 0 or 1.
	 */
	public void freeTriangle(int j) { triangles[j]=null; }
	
	/**
	 * A simple constructor. Use only when bootstrapping a mesh.
	 * @param func
	 * @param x
	 * @param y
	 * @param level
	 */
	SurfaceDiamond(GeoFunction2Var func, double x, double y, int level) {
		this.level = level;
		this.func = func;
		v = func.evaluatePoint(x, y);
		if(x>=3.0||x<=-3.0||y<=-3.0||y>=3.0)
			isBoundaryPoint=true;
	}

	/**
	 * @param func
	 * @param parent0
	 * @param index0
	 * @param parent1
	 * @param index1
	 * @param a0
	 * @param a1
	 * @param level
	 */
	SurfaceDiamond(GeoFunction2Var func, SurfaceDiamond parent0, int index0,
			SurfaceDiamond parent1, int index1, SurfaceDiamond a0,
			SurfaceDiamond a1, int level) {
		this.level = level;
		this.func = func;
		parents[0] = parent0;
		parents[1] = parent1;
		indices[0] = index0;
		indices[1] = index1;
		a[0] = a0;
		a[1] = a1;
		this.v = func.evaluatePoint((a[0].v.getX() + a[1].v.getX()) * 0.5,
				(a[0].v.getY() + a[1].v.getY()) * 0.5);
		setBoundingRadius();
		setError();
		
		double xMin = func.getMinParameter(0); double xMax = func.getMaxParameter(0);
		double yMin = func.getMinParameter(1); double yMax = func.getMaxParameter(1);

		if(v.getX()>=xMax||v.getX()<=xMin||v.getY()<=yMin||v.getY()>=yMax)
			isBoundaryPoint=true;
		if((parent0.isBoundaryPoint && parent1.isBoundaryPoint) ||
			(a0.isBoundaryPoint && a1.isBoundaryPoint))
			isOutside = true;
	}

	/**
	 * Computes the error for the diamond.
	 */
	private void setError() {
		//TODO: implement a better error measure
		error = pointLineDistance(v, a[0].v, a[1].v);
	}

	/**
	 * Computes the minimum distance from point p to a line defined by l1 and l2.
	 * @param p
	 * @param l1
	 * @param l2
	 * @return 
	 */
	private double pointLineDistance(GgbVector p, GgbVector l1, GgbVector l2) {
		GgbVector d1 = l1.sub(p);
		GgbVector d2 = l2.sub(p);
		GgbVector d3 = l2.sub(l1);
		return d1.crossProduct(d2).norm() / d3.norm();
	}

	/**
	 * Sets the (squared) bounding radius of the triangle based on
	 * the distances from its midpoint to its corner vertices.
	 */
	private void setBoundingRadius() {
		boundingRad = a[0].v.sub(v).squareNorm();
		double r = a[1].v.sub(v).squareNorm();
		
		if (r > boundingRad)
			boundingRad = r;
		
		if(parents[0]!=null) {
			r = parents[0].v.sub(v).squareNorm();
			if (r > boundingRad)
				boundingRad = r;
		}
		if(parents[1]!=null) {
			r = parents[1].v.sub(v).squareNorm();
			if (r > boundingRad)
				boundingRad = r;	
		}
	}

	/**
	 * @param ix index of child
	 * @param dm reference to child
	 */
	public void setChild(int ix, SurfaceDiamond dm) { children[ix]=dm; }
	
	/**
	 * note: this assumes that p isn't a copy of a parent, or some other diamond
	 * @param p a reference to a parent
	 * @return the other parent
	 */
	public SurfaceDiamond getOtherParent(SurfaceDiamond p){
		if(p==parents[0])
			return parents[1];
		return parents[0];
	}
	
	/**
	 * Retrieves a child of the diamond. If the child doesn't exist, it is created 
	 * before being returned.
	 * @param i index of child
	 * @return reference to child
	 */
	public SurfaceDiamond getChild(int i) {
		if(children[i] == null) {
			
			SurfaceDiamond parent;
			// get other parent
			SurfaceDiamond otherParent = null;
			if (i < 2) {
				parent = parents[0];
				if(parent!=null)
					otherParent = parent.getChild((indices[0] + (i == 0 ? 1 : -1)) & 3);
			} else {
				parent = parents[1];
				if(parent!=null)
					otherParent = parent.getChild((indices[1] + (i == 2 ? 1 : -1)) & 3);
			}
			SurfaceDiamond a0 = parents[i >> 1];
			SurfaceDiamond a1 = a[((i + 1) & 2) >> 1];

			int ix = (i & 1) ^ 1;
			if (otherParent!=null && otherParent.parents[1] == parent)
				ix |= 2;
			if (i == 1 || i == 3)
				children[i] = new SurfaceDiamond(func, otherParent, ix, this, i, a0, a1, level + 1);
			else
				children[i] = new SurfaceDiamond(func, this, i, otherParent, ix, a0, a1, level + 1);

			if(otherParent!=null)
				otherParent.children[ix] = children[i];
		}
		return children[i];
	}

	@Override
	public String toString() {
		return "[l="+level+", e="+error+" ("+v.getX()+","+v.getY()+")]";
	}
};