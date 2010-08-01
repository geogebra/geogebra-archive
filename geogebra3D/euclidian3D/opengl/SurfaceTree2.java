package geogebra3D.euclidian3D.opengl;

import java.nio.FloatBuffer;
import java.util.Date;

import geogebra.Matrix.GgbVector;
import geogebra.Matrix.GgbVector3D;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.kernel.GeoFunctionNVar;

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
	
	private final double errorConst = .04; //desired error per area unit
	private final int triGoal = 10000;
	private final int minTriCount = 4000;
	/** base diamonds - level 0 */
	private SurfaceDiamond[][] base0 = new SurfaceDiamond[4][4];
	/** base diamonds - level -1 or lower */
	private SurfaceDiamond[][] base1 = new SurfaceDiamond[4][4];
	
	private final int initialRefinement = 30;
	private final int stepRefinement = 1000;
	
	private double baseArea;
	
	/** the maximum level of refinement */
	private static final int maxLevel = 20;
	
	private static final boolean printInfo=true;
	

	/**
	 * @param r the bounding radius of the viewing volume
	 */
	public void setRadius(double r){radSq=r*r;}
	
	/**
	 * @param function
	 * @param view
	 * @param radSq 
	 */
	public SurfaceTree2(GeoFunctionNVar function, EuclidianView3D view, double radSq) {
		this.function = function;
		this.view = view;
		this.radSq=radSq;
		drawList = new DrawList();
		initMesh(function.getMinParameter(0), function.getMaxParameter(0),
				 function.getMinParameter(1), function.getMaxParameter(1));
		base0[1][1].addToSplitQueue();
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
		SurfaceDiamond p1 = new SurfaceDiamond(function,xMin,yMin,-1, splitQueue, mergeQueue);
		SurfaceDiamond a0 = new SurfaceDiamond(function,xMin,yMax,-2, splitQueue, mergeQueue);
		SurfaceDiamond p0 = new SurfaceDiamond(function,xMax,yMax,-1, splitQueue, mergeQueue);
		SurfaceDiamond a1 = new SurfaceDiamond(function,xMax,yMin,-3, splitQueue, mergeQueue);

		p1.setSplit(true);p0.setSplit(true);a0.setSplit(true);a1.setSplit(true);
		
		int index0 = 0;
		int index1 = 1;
		
		root = new SurfaceDiamond(function,p0,index0,p1,index1,a0,a1, 0, splitQueue, mergeQueue);
		
		p0.setChild(index0, root);
		p1.setChild(index1, root);
	}
	
	/**
	 * Bootstraps a fairly complex mesh. Sets base0 and base1.
	 * @param xMin the minimum x coordinate
	 * @param xMax the maximum x coordinate
	 * @param yMin the minimum y caoordinate
	 * @param yMax the maximum y coordinate
	 */
	private void initMesh(double xMin, double xMax, double yMin, double yMax) {
		int di, dj, ix, jx;
		double x, y;
		SurfaceDiamond dm;

		baseArea=(xMax-xMin)*(yMax-yMin);
		
		double dx = (xMax-xMin);
		double dy = (yMax-yMin);
		
		for(int i=0; i < 4; i++)
			for(int j=0; j<4; j++) {
				x=xMin+(i-0.5)*dx;
				y=yMin+(j-0.5)*dy;
	            base0[j][i]=new SurfaceDiamond(function,x,y,0, splitQueue, mergeQueue);
	            if(!(i==1 && j==1))
	            	base0[j][i].isClipped=true;
	            
	            x=xMin+(i-1)*dx;
				y=yMin+(j-1)*dy;
	            base1[j][i]=dm=new SurfaceDiamond(function,x,y,((i^j)&1)!=0?-1:-2, splitQueue, mergeQueue);
	            dm.setSplit(true);
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
		base0[1][1].setBoundingRadius();
	}
	
	private void updateCullingInfo() {
	    SurfaceDiamond d = base0[1][1];
	    d.updateCullInfo2(radSq, drawList);
	    
    	if (d.childCreated(0)) d.getChild(0).updateCullInfo2(radSq, drawList);
    	if (d.childCreated(1)) d.getChild(1).updateCullInfo2(radSq, drawList);
    	if (d.childCreated(2)) d.getChild(2).updateCullInfo2(radSq, drawList);
    	if (d.childCreated(3)) d.getChild(3).updateCullInfo2(radSq, drawList);
	}
	
	/**
	 * Expands the mesh to accomodate for a larger bounding sphere
	 */
	@SuppressWarnings("unused")
	private void expand() {
		
	}

	/** used in optimizeSub() */
	private enum Side {MERGE,SPLIT,NONE};
	
	/**
	 * Performs a set number (stepRefinement) of splits/merges
	 */
	public void optimize() {
		optimizeSub(stepRefinement);
	}
	
	private void optimizeSub(int maxCount) {
		int count = 0;
		
		updateCullingInfo();
		
		long t1 = new Date().getTime();
		
		Side side = tooCoarse()?Side.SPLIT:Side.MERGE;
		while(side!=Side.NONE && count < maxCount) {
			if(side==Side.MERGE) {
				merge(mergeQueue.pop());
				if(tooCoarse())
					side=Side.NONE;
			} else {
				split(splitQueue.pop());
				if(!tooCoarse())
					side=Side.NONE;
			}
			count++;
		}
		if(printInfo)
			System.out.println(this.function+":\tupdate time: "+(new Date().getTime()-t1) + "ms\ttriangles: "+drawList.getCount()+"\terror: "+(float)drawList.getError());
	}
	
	/**
	 * @return true if the mesh should be refined, otherwise false
	 */
	private boolean tooCoarse(){
		double minError=0.1;
		if(drawList.getError()<minError*baseArea)
			if(drawList.getCount()>triGoal)
				return false;
		if(drawList.getCount()<minTriCount)
			return true;
		if(!drawList.isFull())
			if((drawList.getError()>errorConst*baseArea) || drawList.getCount()<minTriCount)
				return true;
		return false;
	}

	/**
	 * Recursively splits the target diamond
	 * @param t the target diamond
	 */
	private void split(SurfaceDiamond t){
		
		//dont split a diamond that has already been split
		if(t.isSplit()){
			return;
		}
		
		SurfaceDiamond temp;
		
		t.removeFromSplitQueue();

		t.addToMergeQueue();	//add to merge queue
		
		t.setSplit(true);		//mark as split
		
		//recursively split parents
		for(int i = 0; i < 2; i++){
			temp=t.parents[i];
			if(temp==null)
				continue;
			split(temp);
			temp.removeFromMergeQueue();
		}
		
		//get kids, insert into priority queue
		for(int i=0;i<4;i++){
			temp = t.getChild(i);
			if(!temp.isClipped){
				
				temp.updateCullInfo2(radSq, drawList);
				
				if(t.level<=maxLevel && !temp.isSplit())
					temp.addToSplitQueue();
				
				//add child to drawing list
				drawList.add(temp,(temp.parents[1]==t?1:0));
			}
		}
		
		//remove from drawing list
		drawList.remove(t, 0);
		drawList.remove(t, 1);
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
		t.setSplit(false);		//mark as not split
		
		//handle kids
		for(int i=0;i<4;i++) {
			temp=t.getChild(i);
			if(!temp.getOtherParent(t).isSplit()){
				temp.removeFromSplitQueue(); //remove from split queue
				if(temp.isSplit())
					temp.addToMergeQueue();
			}
			
			//remove children from draw list
			drawList.remove(temp,(temp.parents[1]==t?1:0));
		}
		t.removeFromMergeQueue();
		
	    for (int i=0;i<2;i++) {
	        SurfaceDiamond p=t.parents[i];
	        if(!p.childrenSplit()){
	            p.updateCullInfo2(radSq, drawList);
	            p.addToMergeQueue();
	        }
	    }

		t.addToSplitQueue();	//add to split queue

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

/**
 * An enum for describing the culling status of a diamond
 * @author André Eriksson
 */
enum CullInfo {
	/** the entire diamond is in the viewing sphere */
	ALLIN, 
	/** part of the diamond is in the viewing sphere */
	SOMEIN, 
	/** the entire diamond is outside the viewing sphere */
	OUT;
	};

/**
 * An approximate priority queue using buckets and linked lists. 
 * Insertion and deletion is O(1).
 * @author André Eriksson
 */
abstract class SurfaceBucketPQ{
	/**	total amount of buckets */
	protected final int BUCKETAMT = 1024;
	/** array of front of buckets */
	SurfaceDiamond[] buckets = new SurfaceDiamond[BUCKETAMT];
	/** array of back of buckets */
	SurfaceDiamond[] backs = new SurfaceDiamond[BUCKETAMT];
	
	/** amount of triangles in list */
	int count = 0;
	
	/** the current highest bucket */
	private int maxBucket = 0;
	
	/**
	 * Assigns a bucket number to a value.
	 * @param d any positive value
	 * @return a number between 0 and BUCKETAMT
	 */
	abstract protected int clamp(double d);
	
	/** Adds an element to the queue.
	 * @param d the element to be added.
	 * @return false if the element is already in the queue. Otherwise true.
	 */
	public boolean add(SurfaceDiamond d) {
		
		//ignore element if already in queue
		if(d.bucketIndex!=-1)
			return false;
		
		int n = clamp(d.errors[0]+d.errors[1]);
		
		//put invisible diamonds in first bucket
		if(d.cullInfo==CullInfo.OUT)
			n=0;
		
		//update pointers
		d.prevInQueue=backs[n];
		if(backs[n]!=null)
			backs[n].nextInQueue=d;
		backs[n]=d;
		if(buckets[n]==null)
			buckets[n]=d;
		
		//update max bucket index if needed
		if(n>maxBucket)
			maxBucket=n;
		
		d.bucketIndex=n;
		
		count++;
		
		return true;
	}
	
	/** Removes an element from the queue. If the specified
	 *  element is not part of the queue, nothing is done.
	 * @param d the element to remove. 
	 * @return false if the element is not in the queue. Otherwise true.
	 */
	public boolean remove(SurfaceDiamond d) {
		
		//ignore element if not in queue
		if(d.bucketIndex==-1)
			return false;
			
		//update pointers of elements before/after in queue
		if(d.nextInQueue!=null)
			d.nextInQueue.prevInQueue=d.prevInQueue;
		if(d.prevInQueue!=null)
			d.prevInQueue.nextInQueue=d.nextInQueue;
		
		//update bucket list and max bucket index as needed
		if(buckets[d.bucketIndex]==d)
			buckets[d.bucketIndex]=d.nextInQueue;

		if(backs[d.bucketIndex]==d)
			backs[d.bucketIndex]=d.prevInQueue;
		
		while(maxBucket>0 && buckets[maxBucket]==null)
			maxBucket--;
		
		d.nextInQueue=d.prevInQueue=null;
		
		d.bucketIndex=-1;

		count--;
		
		return true;
	}
	
	@SuppressWarnings("unused")
	private void sanityCheck(){
		boolean merge = this instanceof MergeQueue;
		SurfaceDiamond temp;
		
		int counter = 0;
		
		for(int i=0;i<=maxBucket;i++){
			temp=buckets[i];
			if(temp==null)
				return;
			while(temp.nextInQueue!=null){
				if(temp.nextInQueue.prevInQueue!=temp)
					System.out.print("");
				if(merge){
					if(!temp.inMergeQueue)
						System.out.print("");
					if(temp.inSplitQueue)
						System.out.print("");
					if(!temp.isSplit())
						System.out.print("");
				} else {
					if(!temp.inSplitQueue)
						System.out.print("");
					if(temp.inMergeQueue)
						System.out.print("");
					if(temp.isSplit())
						System.out.print("");
				}
				if(temp.bucketIndex!=i)
					System.out.print("");
				counter++;
				if(counter>count)
					System.out.print("");
				temp=temp.nextInQueue;
			}
		}
	}
	
	/** 
	 * @return the first element in the top bucket
	 */
	public SurfaceDiamond peek() {	return buckets[maxBucket]; }
	
	/** 
	 * @return the first element in the top bucket
	 */
	public SurfaceDiamond pop() { 
		SurfaceDiamond d = buckets[maxBucket];
		remove(d);
		return d;
	}
}

///**
// * A priority queue for SurfaceDiamonds.
// * @author André Eriksson
// */
//abstract class SurfaceQueue {
//	protected SurfaceDiamond front;
//
//	/**
//	 * @return the diamond at the front of the queue.
//	 */
//	public SurfaceDiamond peek() {
//		SurfaceDiamond d = front;
////		while(d.cullInfo==CullInfo.OUT)
////			d=d.nextInQueue;
//		return d;
//	}
//	
//	/**
//	 * Inserts a diamond into the queue.
//	 * @param d
//	 */
//	public void add(SurfaceDiamond d) {
//		if(front==null) {
//			front=d;
//			return;
//		}
//		SurfaceDiamond t = front;
//		while(compare(t,d)==true){
//			if(t==d)
//				return;
//			if(t.nextInQueue==null){
//				t.nextInQueue=d;
//				d.prevInQueue=t;
//				return;
//			}
//			t=t.nextInQueue;
//		}
//		d.nextInQueue=t;
//		d.prevInQueue=t.prevInQueue;
//		if(t.prevInQueue!=null)
//			t.prevInQueue.nextInQueue=d;
//		t.prevInQueue=d;
//		if(t==front)
//			front=d;
//	}
//	
//	abstract protected boolean compare(SurfaceDiamond a, SurfaceDiamond b);
//	
//	/**
//	 * Removes a diamond from the queue.
//	 * @param d
//	 */
//	public void remove(SurfaceDiamond d) {
//		SurfaceDiamond next = d.nextInQueue;
//		SurfaceDiamond prev = d.prevInQueue;
//		if(next!=null)
//			next.prevInQueue=prev;
//		if(prev!=null)
//			prev.nextInQueue=next;
//		if(front==d)
//			front=next;
//		d.nextInQueue=d.prevInQueue=null;
//	}
//	
//	public String toString() {
//		SurfaceDiamond t = front;
//		StringBuilder b = new StringBuilder("[ ");
//		while(t!=null) {
//			b.append("["+t.toString()+"] ");
//			t=t.nextInQueue;
//		}
//		b.append("]");
//		return b.toString();
//	}
//}

/**
 * A priority queue used for split operations. Sorts based on SurfaceDiamond.error.
 * @author André Eriksson
 */
class SplitQueue extends SurfaceBucketPQ {
	
	@Override
	protected int clamp(double d){
		int f = (int) (Math.exp(d+1)*200)+2;
		return f>BUCKETAMT-1||f<0?BUCKETAMT-1:f;
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
class MergeQueue extends SurfaceBucketPQ {

	@Override
	protected int clamp(double d){
		int f = (int) (Math.exp(1-d)*200);
		int ret=f>BUCKETAMT-1?BUCKETAMT-1:f;
		if(ret<0) ret=0;
		return ret;
	}

}

/**
 * A list of triangles representing the current mesh.
 * @author André Eriksson
 */
class DrawList {
	/** the maximum amount of triangles to allocate */
	private static final int maxCount = 200000;
	
	private static final int marigin = 1000;
	/** the current amount of triangles */
	private int count = 0;
	
	private double totalError = 0;
	
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
		triBuf=FloatBuffer.allocate((maxCount+marigin)*9);
		normalBuf = FloatBuffer.allocate((maxCount+marigin)*9);
	}
	
	/** 
	 * @return the current amount of triangles
	 */
	public int getCount() { return count; }

	/**
	 * @return the total error for all visible triangles
	 */
	public double getError() { return totalError; }
	
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
	 * Adds a triangle to the list.
	 * @param d The parent diamond of the triangle
	 * @param j The index of the triangle within the diamond
	 */
	public void add(SurfaceDiamond d, int j) {

		//handle clipping
		if(d.isClipped || d.parents[j].isClipped)
			return;
		
		SurfaceTriangle t = new SurfaceTriangle(back);
		if(front==null)
			front=t;		
		if(back!=null)
			back.setNext(t);
		back=t;
		
		d.setTriangle(j,t);
		
		totalError+=d.errors[j];
		
		setFloats(d,j,9*count);
		
		t.setIndex(9*count);

		count++;
		
		if(d.cullInfo==CullInfo.OUT)
			hide(d,j);

	}
	
	/**
	 * transfers nine consecutive floats from one place in the buffers to another
	 * @param oldIndex the old index of the first float
	 * @param newIndex the new index of the first float
	 */
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
	
	@SuppressWarnings("unused")
	private void sanityCheck(){
		int i;
		SurfaceTriangle o = front;
		for(i = 0; o!=back; i++){
			try{
				if(!o.getNext().getPrev().equals(o))
					System.out.println("invalid order");
				o=o.getNext();
			}catch(NullPointerException e){
				System.out.println(e);
			}
		}
		if(i!=(count-1<0?0:count-1))
			System.out.println("invalid count");
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
		
		hide(d,j);
		
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

	/**
	 * removes a triangle from the list, but does not erase it
	 * @param d the diamond
	 * @param j the triangle index
	 */
	public void hide(SurfaceDiamond d, int j) {
		
		SurfaceTriangle t = d.getTriangle(j);
		
		if(t==null || t.getIndex()==-1)
			return;
		
		//swap back for current position
		int n = t.getIndex();
		if(count==1){
			back=front=null;
		} else if(t==back) {
			//update pointers
			back=t.getPrev();
			back.setNext(null);
		} else if(t==back.getPrev()){
			//transfer prevBack's floats to new position
			transferFloats(back.getIndex(),n);
			back.setIndex(n);
			
			SurfaceTriangle prev = t.getPrev();
			//update pointers
			back.setPrev(prev);
			if(prev!=null)
				prev.setNext(back);

			if(front==t)
				front=back;
		} else {
			//transfer prevBack's floats to new position
			transferFloats(back.getIndex(),n);
			back.setIndex(n);
			
			//update pointers
			SurfaceTriangle prevBack = back;
			
			back=prevBack.getPrev();
			back.setNext(null);
			
			SurfaceTriangle next = t.getNext();
			SurfaceTriangle prev = t.getPrev();
			
			prevBack.setNext(next);
			prevBack.setPrev(prev);
			
			if(prev!=null)
				prev.setNext(prevBack);
			next.setPrev(prevBack);
			
			if(front==t)
				front=prevBack;
		}
		
		totalError-=d.errors[j];
		
		t.setIndex(-1);
		t.setNext(null);
		t.setPrev(null);
		
		count--;
	}

	/**
	 * shows a triangle that has been hidden
	 * @param d the diamond
	 * @param j the index of the triangle
	 */
	public void show(SurfaceDiamond d, int j) {
		SurfaceTriangle t = d.getTriangle(j);
		
		if(t==null || t.getIndex()!=-1)
			return;
		
		if(front==null)
			front=t;		
		if(back!=null){
			back.setNext(t);
			t.setPrev(back);
		}
		back=t;
		
		setFloats(d,j,9*count);
		
		t.setIndex(9*count);
		
		totalError+=d.errors[j];
		
		count++;
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
	double[] errors = new double[2];
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
	/** flag indicating if the diamond is in the split queue */
	boolean inSplitQueue = false;
	
	private double infConst = 0.1;
	
	/** (approximately) the minimum distance from the origin to the diamond */
	double minRadSq;
	/** (approximately) the maximum distance from the origin to the diamond */
	double maxRadSq;
	
	/** a reference to the merge queue */
	MergeQueue mergeQueue;
	/** a reference to the split queue */
	SplitQueue splitQueue;
	
	/** x/y difference used when estimating normal */
	private final double normalDelta = 1e-8;
	
	/** flag used to discern triangles outside domain of defininition */
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
	 * @return true iff at least one child is split
	 */
	public boolean childrenSplit() {
		return (children[0]!=null?children[0].split:false)
			|| (children[1]!=null?children[1].split:false)
			|| (children[2]!=null?children[2].split:false)
			|| (children[3]!=null?children[3].split:false);
	}

	/**
	 * sets the split flag to the specified value
	 * @param val 
	 */
	public void setSplit(boolean val) { split = val; }
	
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
	 * @param spQ 
	 * @param merQ 
	 */
	SurfaceDiamond(GeoFunctionNVar func, double x, double y, int level, 
					SplitQueue spQ, MergeQueue merQ) {
		this.level = level;
		this.func = func;
		v = func.evaluatePoint(x, y);
		estimateNormal(func);
		splitQueue=spQ;
		mergeQueue=merQ;
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
	 * @param spQ 
	 * @param merQ 
	 */
	SurfaceDiamond(GeoFunctionNVar func, SurfaceDiamond parent0, int index0,
			SurfaceDiamond parent1, int index1, SurfaceDiamond a0,
			SurfaceDiamond a1, int level, SplitQueue spQ, MergeQueue merQ) {
		splitQueue=spQ;
		mergeQueue=merQ;
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
		GgbVector v0 = a[1].v.sub(parents[0].v);
		GgbVector v1 = a[0].v.sub(parents[0].v);
		GgbVector v2 = a[0].v.sub(parents[1].v);
		GgbVector v3 = a[1].v.sub(parents[1].v);
		
		GgbVector n0 = v0.crossProduct(v1);
		GgbVector n1 = v2.crossProduct(v3);

		double a0 = n0.norm(); //proportional to area
		double a1 = n1.norm();

		n0.normalize();
		n1.normalize();
		
		GgbVector o0 = v.sub(parents[0].v);
		GgbVector o1 = v.sub(parents[1].v);
		
		double d0 = Math.abs(n0.dotproduct(o0));
		double d1 = Math.abs(n1.dotproduct(o1));
		
		//vol is proportional to actual volume
		double vol1 = d0*a0;
		double vol2 = d1*a1;
		
		if(Double.isNaN(vol1)){
			//use a different error measure for infinite points
			//namely the base area times some constant
			double ar = Math.abs((a[0].v.getX()-v.getX())*(parents[0].v.getY()-v.getY()));
			errors[0]=ar*ar*infConst;
		}
		else
			errors[0]=vol1;
		if(Double.isNaN(vol2)){
			double ar = Math.abs((a[1].v.getX()-v.getX())*(parents[1].v.getY()-v.getY()));
			errors[1]=ar*ar*infConst;
		}
		else
			errors[1]=vol2;
	}

	/**
	 * Sets the (squared) bounding radius of the triangle based on
	 * the distances from its midpoint to its corner vertices.
	 */
	public void setBoundingRadius() {
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
		
		minRadSq=v.norm()-Math.sqrt(boundingRadSq);
		maxRadSq=v.norm()+Math.sqrt(boundingRadSq);
		if(minRadSq<0) minRadSq=0;
		if(Double.isNaN(minRadSq))
			minRadSq=0;
		if(Double.isNaN(maxRadSq))
			maxRadSq=Double.POSITIVE_INFINITY;
		minRadSq*=minRadSq;
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
				children[i] = new SurfaceDiamond(func, otherParent, ix, this, i, a0, a1, level + 1,
													splitQueue, mergeQueue);
			else
				children[i] = new SurfaceDiamond(func, this, i, otherParent, ix, a0, a1, level + 1,
													splitQueue, mergeQueue);

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
	 * @param radSq
	 * @param drawList
	 */
	public void updateCullInfo2(double radSq, DrawList drawList)
	{
		//ignore clipped diamonds
		if(isClipped)
			return;
		CullInfo parentCull = a[1].cullInfo;
		CullInfo oldCull = cullInfo;
		
		if(parentCull==CullInfo.ALLIN||parentCull==CullInfo.OUT)
			cullInfo=parentCull;
		else {
			if(maxRadSq<radSq)
				cullInfo=CullInfo.ALLIN;
			else if(minRadSq<radSq)
				cullInfo=CullInfo.SOMEIN;
			else
				cullInfo=CullInfo.OUT;
		}
	    
		
		if(oldCull!=cullInfo || cullInfo==CullInfo.SOMEIN){
		    if(cullInfo==CullInfo.OUT){
		    	if(triangles[0]!=null && triangles[0].getIndex()!=-1)
		    		drawList.hide(this, 0);
		    	if(triangles[1]!=null && triangles[1].getIndex()!=-1)
		    		drawList.hide(this, 1);
		    } else if(cullInfo==CullInfo.ALLIN || cullInfo==CullInfo.SOMEIN){
		    	if(triangles[0]!=null && triangles[0].getIndex()==-1)
		    		drawList.show(this, 0);
		    	if(triangles[1]!=null && triangles[1].getIndex()==-1)
		    		drawList.show(this, 1);
		    }
		    //reinsert into priority queue
		    if(oldCull==CullInfo.OUT && cullInfo!=CullInfo.OUT ||
	    	   oldCull!=CullInfo.OUT && cullInfo==CullInfo.OUT){
			    if(inMergeQueue){
			    	mergeQueue.remove(this);
			    	mergeQueue.add(this);
			    } else if(inSplitQueue){
			    	splitQueue.remove(this);
			    	splitQueue.add(this);
			    }
		    }
		}
		
		if (split) {
	        for (int i=0;i<4;i+=2) {
	        	if(childCreated(i)){
	        		SurfaceDiamond c = children[i];
	                if (c.parents[0]==this) {
	                    if (c.childCreated(0)) 
	                    	c.children[0].updateCullInfo2(radSq, drawList);
	                    if (c.childCreated(1)) 
	                    	c.children[1].updateCullInfo2(radSq, drawList);
	                }else{
	                    if (c.childCreated(2)) 
	                    	c.children[2].updateCullInfo2(radSq, drawList);
	                    if (c.childCreated(3)) 
	                    	c.children[3].updateCullInfo2(radSq, drawList);
	                }
	        	}
	        }
	    }
	}
	
	/** inserts the diamond into the split queue */
	public void addToSplitQueue(){
		if(!inSplitQueue){
			splitQueue.add(this);
			inSplitQueue=true;
		}
	}
	
	/** removes the diamond from the split queue */
	public void removeFromSplitQueue(){
		splitQueue.remove(this);
		inSplitQueue=false;
	}
	
	/** adds the diamond to the merge queue */
	public void addToMergeQueue(){
		if(!inMergeQueue){
			mergeQueue.add(this);
			inMergeQueue=true;
		}
	}
	
	/** removes the diamond from the merge queue */
	public void removeFromMergeQueue(){
		if(inMergeQueue){
			mergeQueue.remove(this);
			inMergeQueue=false;
		}
	}
	
	@Override
	public String toString() {
		return "[l="+level+", e=("+errors[0]+", "+ errors[1]+") ("+v.getX()+","+v.getY()+")]";
	}
};