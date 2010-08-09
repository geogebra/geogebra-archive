package geogebra3D.euclidian3D;
//
///**
// * An approximate priority queue using buckets and linked lists. 
// * Insertion and deletion is O(1).
// * @author André Eriksson
// */
//abstract class BucketPriorityQueue{
//	/**	total amount of buckets */
//	protected final int BUCKETAMT = 1024;
//	/** array of front of buckets */
//	BucketPQElem[] buckets = new BucketPQElem[BUCKETAMT];
//	/** array of back of buckets */
//	BucketPQElem[] backs = new BucketPQElem[BUCKETAMT];
//	
//	/** amount of triangles in list */
//	int count = 0;
//
//	
//	/** the current highest bucket */
//	private int maxBucket = 0;
//	
//	/**
//	 * Assigns a bucket number to a value.
//	 * @param d any positive value
//	 * @return a number between 0 and BUCKETAMT
//	 */
//	abstract protected int clamp(double d);
//	
//	/** Adds an element to the queue.
//	 * @param d the element to be added.
//	 * @return false if the element is already in the queue. Otherwise true.
//	 */
//	public BucketPQElem add(float f) {
//		int n = clamp(f);
//		
//		
//		//ignore element if already in queue
//		if(d.bucketIndex!=-1)
//			return false;
//		
//		BucketPQElem d = new BucketPQElem(backs[n],null,n);
//		
//		int n = clamp(d.errors[0]+d.errors[1]);
//		
////		//put invisible diamonds in first bucket
////		if(d.cullInfo==CullInfo.OUT)
////			n=0;
//		
//		//update pointers
//		if(backs[n]!=null)
//			backs[n].next=d;
//		backs[n]=d;
//		if(buckets[n]==null)
//			buckets[n]=d;
//		
//		//update max bucket index if needed
//		if(n>maxBucket)
//			maxBucket=n;
//		
//		count++;
//		
//		return true;
//	}
//	
//	/** Removes an element from the queue. If the specified
//	 *  element is not part of the queue, nothing is done.
//	 * @param d the element to remove. 
//	 * @return false if the element is not in the queue. Otherwise true.
//	 */
//	public boolean remove(BucketPQElem d) {
//		
//		//ignore element if not in queue
//		if(d.bucketIndex==-1)
//			return false;
//			
//		//update pointers of elements before/after in queue
//		if(d.next!=null)
//			d.next.prev=d.prev;
//		if(d.prev!=null)
//			d.prev.next=d.next;
//		
//		//update bucket list and max bucket index as needed
//		if(buckets[d.bucketIndex]==d)
//			buckets[d.bucketIndex]=d.next;
//
//		if(backs[d.bucketIndex]==d)
//			backs[d.bucketIndex]=d.prev;
//		
//		while(maxBucket>0 && buckets[maxBucket]==null)
//			maxBucket--;
//		
//		d.next=d.prev=null;
//		
//		d.bucketIndex=-1;
//
//		count--;
//		
//		return true;
//	}
//	
////	@SuppressWarnings("unused")
////	private void sanityCheck(){
////		boolean merge = this instanceof MergeQueue;
////		BucketPQElem temp;
////		
////		int counter = 0;
////		
////		for(int i=0;i<=maxBucket;i++){
////			temp=buckets[i];
////			if(temp==null)
////				return;
////			while(temp.next!=null){
////				if(temp.next.prev!=temp)
////					System.out.print("");
////				if(merge){
////					if(!temp.inMergeQueue)
////						System.out.print("");
////					if(temp.inSplitQueue)
////						System.out.print("");
////					if(!temp.isSplit())
////						System.out.print("");
////				} else {
////					if(!temp.inSplitQueue)
////						System.out.print("");
////					if(temp.inMergeQueue)
////						System.out.print("");
////					if(temp.isSplit())
////						System.out.print("");
////				}
////				if(temp.bucketIndex!=i)
////					System.out.print("");
////				counter++;
////				if(counter>count)
////					System.out.print("");
////				temp=temp.next;
////			}
////		}
////	}
//	
//	/** 
//	 * @return the first element in the top bucket
//	 */
//	public BucketPQElem peek() {	return buckets[maxBucket]; }
//	
//	/** 
//	 * @return the first element in the top bucket
//	 */
//	public BucketPQElem pop() { 
//		BucketPQElem d = buckets[maxBucket];
//		remove(d);
//		return d;
//	}
//}
//
//class BucketPQElem {
//	public int bucketIndex;
//	public BucketPQElem prev, next;
//	
//	public BucketPQElem(BucketPQElem prev, BucketPQElem next, int bucketIndex) {
//		this.prev=prev;
//		this.next=next;
//		this.bucketIndex=bucketIndex;
//	}
//}

/**
 * An approximate priority queue using buckets and linked lists. Insertion and
 * deletion is O(1).
 * 
 * @author André Eriksson
 */
abstract public class BucketPriorityQueue {
	/** total amount of buckets */
	protected final int BUCKETAMT = 1024;
	/** array of front of buckets */
	SurfaceMeshDiamond[] buckets = new SurfaceMeshDiamond[BUCKETAMT];
	/** array of back of buckets */
	SurfaceMeshDiamond[] backs = new SurfaceMeshDiamond[BUCKETAMT];

	/** amount of triangles in list */
	int count = 0;

	/** the current highest bucket */
	private int maxBucket = 0;

	/**
	 * Assigns a bucket number to a value.
	 * 
	 * @param d
	 *            any positive value
	 * @return a number between 0 and BUCKETAMT
	 */
	abstract protected int clamp(double d);

	/**
	 * Adds an element to the queue.
	 * 
	 * @param d
	 *            the element to be added.
	 * @return false if the element is already in the queue. Otherwise true.
	 */
	public boolean add(SurfaceMeshDiamond d) {

		// ignore element if already in queue
		if (d.bucketIndex != -1)
			return false;

		int n = clamp(d.errors[0] + d.errors[1]);

		// put invisible diamonds in first bucket
		if (d.cullInfo == SurfaceMesh.CullInfo.OUT)
			n = 0;

		// update pointers
		d.prevInQueue = backs[n];
		if (backs[n] != null)
			backs[n].nextInQueue = d;
		backs[n] = d;
		if (buckets[n] == null)
			buckets[n] = d;

		// update max bucket index if needed
		if (n > maxBucket)
			maxBucket = n;

		d.bucketIndex = n;

		count++;

		return true;
	}

	/**
	 * Removes an element from the queue. If the specified element is not part
	 * of the queue, nothing is done.
	 * 
	 * @param d
	 *            the element to remove.
	 * @return false if the element is not in the queue. Otherwise true.
	 */
	public boolean remove(SurfaceMeshDiamond d) {

		// ignore element if not in queue
		if (d.bucketIndex == -1)
			return false;

		// update pointers of elements before/after in queue
		if (d.nextInQueue != null)
			d.nextInQueue.prevInQueue = d.prevInQueue;
		if (d.prevInQueue != null)
			d.prevInQueue.nextInQueue = d.nextInQueue;

		// update bucket list and max bucket index as needed
		if (buckets[d.bucketIndex] == d)
			buckets[d.bucketIndex] = d.nextInQueue;

		if (backs[d.bucketIndex] == d)
			backs[d.bucketIndex] = d.prevInQueue;

		while (maxBucket > 0 && buckets[maxBucket] == null)
			maxBucket--;

		d.nextInQueue = d.prevInQueue = null;

		d.bucketIndex = -1;

		count--;

		return true;
	}

	@SuppressWarnings("unused")
	private void sanityCheck() {
		boolean merge = this instanceof MergeQueue;
		SurfaceMeshDiamond temp;

		int counter = 0;

		for (int i = 0; i <= maxBucket; i++) {
			temp = buckets[i];
			if (temp == null)
				return;
			while (temp.nextInQueue != null) {
				if (temp.nextInQueue.prevInQueue != temp)
					System.out.print("");
				if (merge) {
					if (!temp.inMergeQueue)
						System.out.print("");
					if (temp.inSplitQueue)
						System.out.print("");
					if (!temp.isSplit())
						System.out.print("");
				} else {
					if (!temp.inSplitQueue)
						System.out.print("");
					if (temp.inMergeQueue)
						System.out.print("");
					if (temp.isSplit())
						System.out.print("");
				}
				if (temp.bucketIndex != i)
					System.out.print("");
				counter++;
				if (counter > count)
					System.out.print("");
				temp = temp.nextInQueue;
			}
		}
	}

	/**
	 * @return the first element in the top bucket
	 */
	public SurfaceMeshDiamond peek() {
		return buckets[maxBucket];
	}

	/**
	 * @return the first element in the top bucket
	 */
	public SurfaceMeshDiamond pop() {
		if (maxBucket == 0)
			return null;
		SurfaceMeshDiamond d = buckets[maxBucket];
		remove(d);
		return d;
	}
}