package geogebra3D.euclidian3D.plots;

import geogebra3D.euclidian3D.BucketAssigner;


/**
 * An approximate priority queue using buckets and linked lists. Insertion and
 * deletion are fast.
 * 
 */
public class FastBucketPQ{
	/** total amount of buckets */
	private static final int DEFAULT_BUCKET_AMT = 2048;
	/** array of front of buckets */
	protected DynamicMeshElement2[] buckets;
	/** array of back of buckets */
	protected DynamicMeshElement2[] backs;

	/** used for figuring out which buckets to insert elements into */
	protected BucketAssigner<DynamicMeshElement2> bucketAssigner;

	/** the amount of buckets used */
	protected final int bucketAmt;

	/** the amount of elements in the queue */
	protected int count;

	/** the current highest bucket */
	protected int maxBucket = 0;

	/** indicates the direction of the PQ */
	protected final boolean reverse;

	/**
	 * @param ba
	 *            the bucket assigner to use
	 * @param reverse
	 */
	protected FastBucketPQ(BucketAssigner<DynamicMeshElement2> ba, boolean reverse) {
		this(DEFAULT_BUCKET_AMT, ba, reverse);
	}

	/**
	 * @param bucketAmt
	 *            the number of buckets to use
	 * @param ba
	 *            the bucket assigner to use
	 * @param reverse
	 */
	@SuppressWarnings("unchecked")
	public FastBucketPQ(int bucketAmt, BucketAssigner<DynamicMeshElement2> ba, boolean reverse) {
		this.bucketAmt = bucketAmt;
		buckets = new DynamicMeshElement2[bucketAmt];
		backs = new DynamicMeshElement2[bucketAmt];
		this.bucketAssigner = ba;
		this.reverse = reverse;
	}

	private int getIndex(DynamicMeshElement2 el) {
		int i = bucketAssigner.getBucketIndex(el, bucketAmt);
		return reverse ? bucketAmt - 1 - i : i;
	}
	
	private boolean addToZeroBucket(DynamicMeshElement2 obj) {
		if (null == obj)
			throw new NullPointerException();

		if (obj.owner!=null)
			return false;

		int bucketIndex = 0;

		// update pointers
		obj.prev = backs[bucketIndex];
		if (backs[bucketIndex] != null)
			backs[bucketIndex].next = obj;
		backs[bucketIndex] = obj;
		if (buckets[bucketIndex] == null)
			buckets[bucketIndex] = obj;

		// update max bucket index if needed
		if (bucketIndex > maxBucket)
			maxBucket = bucketIndex;

		obj.bucketIndex = bucketIndex;

		count++;

		obj.owner=this;

		return true;
	}

	/**
	 * Adds an element to the queue.
	 * 
	 * @param ob
	 *            the object to be added.
	 * @return false if the element is already in the queue. Otherwise true.
	 */
	public boolean add(DynamicMeshElement2 obj) {

		if (null == obj)
			throw new NullPointerException();

		if (obj.owner!=null)
			return false;

		int bucketIndex = getIndex(obj);
		
		// update pointers
		obj.prev = backs[bucketIndex];
		if (backs[bucketIndex] != null)
			backs[bucketIndex].next = obj;
		backs[bucketIndex] = obj;
		if (buckets[bucketIndex] == null)
			buckets[bucketIndex] = obj;

		// update max bucket index if needed
		if (bucketIndex > maxBucket)
			maxBucket = bucketIndex;

		obj.bucketIndex = bucketIndex;

		count++;

		obj.owner=this;

		return true;
	}

	/**
	 * @param elem
	 *            the element to remove
	 * @return true if the object was in the queue - otherwise false.
	 */
	public boolean remove(DynamicMeshElement2 elem) {
		// ignore element if not in queue
		if (elem == null || elem.owner!=this)
			return false;

		int bi = elem.bucketIndex;

		// update pointers of elements before/after in queue
		if (elem.next != null)
			elem.next.prev = elem.prev;
		if (elem.prev != null)
			elem.prev.next = elem.next;

		// update bucket list and max bucket index as needed
		if (buckets[bi] == elem)
			buckets[bi] = elem.next;

		if (backs[bi] == elem)
			backs[bi] = elem.prev;

		while (maxBucket > 0 && buckets[maxBucket] == null)
			maxBucket--;

		elem.next = elem.prev = null;

		elem.owner=null;

		count--;

		return true;
	}

	/**
	 * @return the first element in the top bucket
	 */
	public DynamicMeshElement2 peek() {
		return buckets[maxBucket];
	}

	public DynamicMeshElement2 poll() {
		if (maxBucket == 0)
			return null;
		DynamicMeshElement2 elem = buckets[maxBucket];
		remove(elem);
		return elem;
	}
	
	public DynamicMeshElement2 forcePoll() {
		DynamicMeshElement2 elem = buckets[maxBucket];
		remove(elem);
		return elem;
	}

	public int size() {
		return count;
	}
}