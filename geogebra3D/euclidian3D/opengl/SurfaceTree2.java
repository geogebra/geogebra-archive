package geogebra3D.euclidian3D.opengl;

import java.nio.FloatBuffer;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.GeoFunctionNVar;
import geogebra3D.euclidian3D.EuclidianView3D;

//TODO: there's something wrong with the culling. fix it.
//TODO: use bucket queues for merges - make sure everything works.
//TODO: replace periodic arrangement with null pointers
//TODO: use simpler base mesh and implement expand()


/**
 * @author André Eriksson
 */
public class SurfaceTree2 {
	private GeoFunctionNVar function;
	@SuppressWarnings("unused")
	private final EuclidianView3D view;
	private double radSq;
	private SplitQueue splitQueue = new SplitQueue();
	private MergeQueue mergeQueue = new MergeQueue();
	
	private SurfaceDiamond root;
	
	private DrawList drawList;

	private double totalError;
//	private final double errorgoal = 1.0;
//	private final double minimumerror = 1.0;
	/** base diamonds - level 0 */
	private SurfaceDiamond[][] base0 = new SurfaceDiamond[4][4];
	/** base diamonds - level -1 or lower */
	private SurfaceDiamond[][] base1 = new SurfaceDiamond[4][4];
	
	private final int initialRefinement = 1000;
	private final int stepRefinement = 30;
	

	/**
	 * @param r the bounding radius of the viewing volume
	 */
	public void setRadius(double r){radSq=r*r;}
	
	/**
	 * @param function
	 * @param view
	 */
	public SurfaceTree2(GeoFunctionNVar function, EuclidianView3D view) {
		this.function = function;
		this.view = view;
		drawList = new DrawList();
		initMesh(function.getMinParameter(0), function.getMaxParameter(0),
				 function.getMinParameter(1), function.getMaxParameter(1));
		totalError=base0[1][1].error;
		splitQueue.add(base0[1][1]);
		optimizeSub(initialRefinement);
	}

	/**
	 * Bootstraps a simple square mesh. Sets base0 and base1.
	 * @param xMin the minimum x coordinate
	 * @param xMax the maximum x coordinate
	 * @param yMin the minimum y coordinate
	 * @param yMax the maximum y coordinate
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
		
		double dx = (xMax-xMin);
		double dy = (yMax-yMin);
		
		for(int i=0; i < 4; i++)
			for(int j=0; j<4; j++) {
				x=xMin+(i-0.5)*dx;
				y=yMin+(j-0.5)*dy;
	            base0[j][i]=new SurfaceDiamond(function,x,y,0);
	            if(!(i==1 && j==1))
	            	base0[j][i].isClipped=true;
	            
	            x=xMin+(i-1)*dx;
				y=yMin+(j-1)*dy;
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
	    SurfaceDiamond d = base0[1][1];
	    d.updateCullInfo(radSq, drawList);
	    for (int i=0;i<4;i++) { 
	    	if (d.childCreated(i)) 
	    		d.getChild(i).updateCullInfo(radSq, drawList);
	    	}
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
	 * Performs a set number (stepRefinement) of splits/merges
	 */
	public void optimize() {
		optimizeSub(stepRefinement);
	}
	
	private void optimizeSub(int maxCount) {
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
		int count = 0;
		
		updateCullingInfo();
		
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
			
			temp = t.getChild(i);
			if(!temp.isClipped){
				temp.updateCullInfo(radSq, drawList);
				
				splitQueue.add(temp);
				
				//add child to drawing list
				drawList.add(temp,(temp.parents[1]==t?1:0));
			}
		}
		
		//remove from drawing list
		drawList.remove(t, 0);
		drawList.remove(t, 1);

		splitQueue.remove(t);  //remove from priority queue
		 
		mergeQueue.add(t);	//add to merge queue
		
		//update total error
		totalError+= t.getChild(0).error+t.getChild(1).error+
					 t.getChild(2).error+t.getChild(3).error-t.error;
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
		
		//update total error
		totalError-= t.getChild(0).error+t.getChild(1).error+
					 t.getChild(2).error+t.getChild(3).error-t.error;
	}

	/**
	 * @return 	Returns a FloatBuffer containing the current mesh as a triangle list. 
	 * 			Each triangle is represented as 9 consecutive floats. The FloatBuffer
	 * 			will probably contain extra floats - use getTriangleCount() to find out
	 * 			how many floats are valid.
	 */
	public FloatBuffer getVertices() {
		return drawList.getTriangleBuffer();
	}
	
	/**
	 * @return Returns a FloatBuffer containing the current mesh as a triangle list.
	 */
	public FloatBuffer getNormals() {
		return drawList.getNormalBuffer();
	}
	
	/**
	 * @return the amount of triangles in the current mesh.
	 */
	public int getTriangleCount() {
		return drawList.getCount();
	}
}

enum CullInfo { ALLIN, SOMEIN, OUT };

/**
 * An approximate priority queue using buckets and linked lists. 
 * Insertion and deletion is O(1).
 * @author André Eriksson
 */
abstract class SurfaceBucketPQ{
	protected final int BUCKETAMT = 1024;
	SurfaceDiamond[] buckets = new SurfaceDiamond[BUCKETAMT];
	
	private int maxBucket = 0;
	
	abstract protected int clamp(double d);
	
	/** Adds an element to the queue.
	 * @param d the element to be added.
	 */
	public void add(SurfaceDiamond d) {
		if(d.v.getX()==0.5 && d.v.getY()==1.0)
			System.out.print("");
		
		//ignore element if already in queue
		if(d.bucketIndex!=-1)
			return;
		
		int n = clamp(d.error);
		
		//update pointers
		d.nextInQueue=buckets[n];
		if(buckets[n]!=null)
			buckets[n].prevInQueue=d;
		buckets[n]=d;
		
		//update max bucket index if needed
		if(n>maxBucket)
			maxBucket=n;
		
		d.bucketIndex=n;
	}
	
	/** Removes an element from the queue. If the specified
	 *  element is not part of the queue, nothing is done.
	 * @param d the element to remove. 
	 */
	public void remove(SurfaceDiamond d) {
		//ignore element if not in queue
		if(d.bucketIndex==-1)
			return;
			
		//update pointers of elements before/after in queue
		if(d.nextInQueue!=null)
			d.nextInQueue.prevInQueue=d.prevInQueue;
		if(d.prevInQueue!=null)
			d.prevInQueue.nextInQueue=d.nextInQueue;
		
		//update bucket list and max bucket index as needed
		if(buckets[d.bucketIndex]==d){
			buckets[d.bucketIndex]=d.nextInQueue;
			while(buckets[maxBucket]==null && maxBucket>=0)
				maxBucket--;
		}
		
		d.nextInQueue=d.prevInQueue=null;
		
		d.bucketIndex=-1;
	}
	
	/** 
	 * @return the first element in the top bucket
	 */
	public SurfaceDiamond peek() {	return buckets[maxBucket]; }
}

/**
 * A priority queue for SurfaceDiamonds.
 * @author André Eriksson
 */
abstract class SurfaceQueue {
	//TODO: speed this up - at the moment add() is O(n)
	protected SurfaceDiamond front;

	/**
	 * @return the diamond at the front of the queue.
	 */
	public SurfaceDiamond peek() {
		SurfaceDiamond d = front;
//		while(d.cullInfo==CullInfo.OUT)
//			d=d.nextInQueue;
		return d;
	}
	
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
		while(compare(t,d)==true){
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
class SplitQueue extends SurfaceBucketPQ {
	
	@Override
	protected int clamp(double d){
		int f = (int) (d*10000);
		return f>BUCKETAMT-1?BUCKETAMT-1:f;
	}
	
//	@Override
//	protected boolean compare(SurfaceDiamond a, SurfaceDiamond b) {
//		return a.error>=b.error;
//	}
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
 * @author André Eriksson
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
	/** A counterpart to tribuf containing normals */
	private FloatBuffer normalBuf;
	
	/** Pointers to the front and back of the queue */
	private SurfaceTriangle front, back;

	/**
	 * Empty constuctor. Allocates memory for triBuf.
	 */
	DrawList(){
		triBuf=FloatBuffer.allocate(maxCount*9);
		normalBuf = FloatBuffer.allocate(maxCount*9);
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
	 * @return a reference to normalBuf
	 */
	public FloatBuffer getNormalBuffer() { return normalBuf; }

	/**
	 * @return true if count>=maxCount - otherwise false.
	 */
	public boolean isFull() { return count>=maxCount-20; }
	
	private void setFloats(SurfaceDiamond d, int j, int index) {
		float[] p = new float[9];
		float[] n = new float[9];
		
        SurfaceDiamond dmtab[] = new SurfaceDiamond[3];
        dmtab[1]=d.parents[j];
        if (j!=0) 	{ dmtab[0]=d.a[1]; dmtab[2]=d.a[0]; }
        else   		{ dmtab[0]=d.a[0]; dmtab[2]=d.a[1]; }
        for (int vi=0, c=0;vi<3;vi++,c+=3) {
        	p[c]   = (float) dmtab[vi].v.getX();
            p[c+1] = (float) dmtab[vi].v.getY();
            p[c+2] = (float) dmtab[vi].v.getZ();
            n[c]   = (float) dmtab[vi].normal.getX();
            n[c+1] = (float) dmtab[vi].normal.getY();
            n[c+2] = (float) dmtab[vi].normal.getZ();
        }
		triBuf.position(index);
		triBuf.put(p);
		normalBuf.position(index);
		normalBuf.put(n);
	}
	
	/**
	 * Adds a triangle to the queue.
	 * @param d The parent diamond of the triangle
	 * @param j The index of the triangle within the diamond
	 */
	public void add(SurfaceDiamond d, int j) {		
		//handle clipping
		if(d.isClipped || d.parents[j].isClipped)
			return;
		
		//handle culling
		//if(d.cullInfo==CullInfo.OUT)
		//	return;
		
		SurfaceTriangle t = new SurfaceTriangle(back);
		if(front==null)
			front=t;		
		if(back!=null)
			back.setNext(t);
		back=t;
		
		d.setTriangle(j,t);
		
		setFloats(d,j,9*count);
		
		t.setIndex(9*count);
		
		count++;
	}
	
	private void transferFloats(int oldIndex, int newIndex) {
		float[] f = new float[9];
		float[] g = new float[9];
		
		triBuf.position(oldIndex);
		triBuf.get(f);
		triBuf.position(newIndex);
		
		normalBuf.position(oldIndex);
		normalBuf.get(g);
		normalBuf.position(newIndex);
		
		for(int i=0;i<9;i++){
			triBuf.put(f[i]);
			normalBuf.put(g[i]);
		}
	}
	
	/**
	 * Removes a triangle from the queue.
	 * @param d The parent diamond of the triangle
	 * @param j The index of the triangle within the diamond
	 */
	public void remove(SurfaceDiamond d, int j) {
		//handle clipping
		if(d.isClipped || d.parents[j].isClipped)
			return;
			
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
			transferFloats(prevBack.getIndex(),n);
			
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
	/** vertex normal */
	GgbVector normal;
	/** radius of sphere bound squared */
	double boundingRadSq;
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
	GeoFunctionNVar func;
	/** flag indicating if the diamond has been split */
	private boolean split = false;
	/** flag indicating if the diamond is in the merge queue */
	boolean inMergeQueue = false;
	
	/** x/y difference used when estimating normal */
	private final double normalDelta = 1e-8;
	
	/** indicates whether or not the point is at the boundary of or outside
	 *  the drawing area */
	boolean isBoundaryPoint = false;
	
	/** indicates whether or not the point is outside the viewing area */
	boolean isOutside = false;
	
	/** an flag used to discern triangles outside domain of defininition */
	boolean isClipped = false;
	
	/** culling info for the diamond */
	CullInfo cullInfo;

	/** pointer to the next element in a priority queue */
	SurfaceDiamond nextInQueue;
	/** pointer to the previous element in a priority queue */
	SurfaceDiamond prevInQueue;
	/** index of bucket in priority queue, set to -1 if not a member of a queue*/
	int bucketIndex = -1;
	
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
	SurfaceDiamond(GeoFunctionNVar func, double x, double y, int level) {
		this.level = level;
		this.func = func;
		v = func.evaluatePoint(x, y);
		estimateNormal(func);
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
	SurfaceDiamond(GeoFunctionNVar func, SurfaceDiamond parent0, int index0,
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
		estimateNormal(func);
		setBoundingRadius();
		setError();
		
		//set clipping flag
		if(a0.isClipped || (parent0.isClipped && parent1.isClipped))
			isClipped=true;
	}
	
	private void estimateNormal(GeoFunctionNVar func){
		double x = v.getX(); double y = v.getY();
		GgbVector dx = func.evaluatePoint(x+normalDelta, y);
		GgbVector dy = func.evaluatePoint(x, y+normalDelta);
		normal = dx.sub(v).crossProduct(dy.sub(v)).normalized();
	}

	/**
	 * Computes the error for the diamond.
	 */
	private void setError() {
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
		boundingRadSq = a[0].v.sub(v).squareNorm();
		double r = a[1].v.sub(v).squareNorm();
		
		if (r > boundingRadSq)
			boundingRadSq = r;
		
		if(parents[0]!=null) {
			r = parents[0].v.sub(v).squareNorm();
			if (r > boundingRadSq)
				boundingRadSq = r;
		}
		if(parents[1]!=null) {
			r = parents[1].v.sub(v).squareNorm();
			if (r > boundingRadSq)
				boundingRadSq = r;	
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

	/** Checks if a child has been created.
	 * @param i the index of the child
	 * @return false if the child is null, otherwise true.
	 */
	public boolean childCreated(int i) { return children[i]!=null; }
	
	/**
	 * Recursively updates the culling info.
	 * @param radSq the squared radius of the viewing volume.
	 * @param drawList a reference to the DrawList used.
	 */
	public void updateCullInfo(double radSq, DrawList drawList)
	{
		//ignore clipped diamonds
		if(isClipped)
			return;
		
		CullInfo oldCullInfo = cullInfo;
		
		//update cull flags
	    updateCullFlags(radSq, drawList);

	    if (oldCullInfo==cullInfo && cullInfo!=CullInfo.SOMEIN) 
	    	return;
	    
	    /* if diamond is split, recurse on four quadtree kids (if they exist) */
	    if (split) {
	        for (int i=0;i<4;i+=2) {
	        	if(childCreated(i)){
	        		SurfaceDiamond c = children[i];
	                if (c.parents[0]==this) {
	                    if (c.childCreated(0)) 
	                    	c.children[0].updateCullInfo(radSq, drawList);
	                    if (c.childCreated(1)) 
	                    	c.children[1].updateCullInfo(radSq, drawList);
	                }else{
	                    if (c.childCreated(2)) 
	                    	c.children[2].updateCullInfo(radSq, drawList);
	                    if (c.childCreated(3)) 
	                    	c.children[3].updateCullInfo(radSq, drawList);
	                }
	        	}
	        }
	    }
	}
	
	
	private void updateCullFlags(double radSq, DrawList drawList)
	{
	    // get quadtree parent's cull flag
	    CullInfo parentCull=a[0].cullInfo;
	    CullInfo oldCull=cullInfo;

	    //update culling info if needed
	    if (parentCull!=CullInfo.ALLIN && parentCull!=CullInfo.OUT) {
	    	double sqn = v.squareNorm();
	        if(sqn>radSq+boundingRadSq)
	        	cullInfo=CullInfo.OUT;
	        else if(sqn<radSq-boundingRadSq)
	        	cullInfo=CullInfo.ALLIN;
	        else
	        	cullInfo=CullInfo.SOMEIN;
	    }

	    // if OUT state changes, update in/out listing on any draw tris
	    if ((oldCull==CullInfo.OUT && cullInfo!=CullInfo.OUT) ||
    		(oldCull!=CullInfo.OUT && cullInfo==CullInfo.OUT)) {
	        for (int i=0;i<2;i++) {
	            if (triangles[i]!=null) {
	                if (cullInfo==CullInfo.OUT)
	                	drawList.remove(this, i);
	                else 
	                	drawList.add(this, i);
	            }
	        }
	    }
	}
	
	@Override
	public String toString() {
		return "[l="+level+", e="+error+" ("+v.getX()+","+v.getY()+")]";
	}
};