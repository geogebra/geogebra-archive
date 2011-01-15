package geogebra3D.euclidian3D.plots;

import geogebra3D.euclidian3D.BucketAssigner;
import geogebra3D.euclidian3D.BucketPQ;

/**
 * A bucket priority queue designed specifically for dynamic meshes.
 * Inserts culled diamonds into the zeroth bucket.
 * 
 * @author André Eriksson
 */
public class DynamicMeshBucketPQ extends BucketPQ<AbstractDynamicMeshElement> {

	/**
	 * @param ba
	 *            the bucket assigner to use
	 */
	DynamicMeshBucketPQ(BucketAssigner<AbstractDynamicMeshElement> ba) {
		super(ba);
	}

	public boolean add(AbstractDynamicMeshElement object) {
		if (findLink(object) != null) // already in queue
			return false;

		// put invisible diamonds in first bucket
		if (object.cullInfo == CullInfo.OUT)
			return addToZeroBucket(object);

		return super.add(object);
	}

	private boolean addToZeroBucket(AbstractDynamicMeshElement object) {
		if (null == object)
			throw new NullPointerException();

		Link<AbstractDynamicMeshElement> elem = findLink(object);

		// ignore element if already in queue
		if (elem != null)
			return false;

		int bucketIndex = 0;

		elem = new Link<AbstractDynamicMeshElement>(object);

		// update pointers
		elem.prev = backs[bucketIndex];
		if (backs[bucketIndex] != null)
			backs[bucketIndex].next = elem;
		backs[bucketIndex] = elem;
		if (buckets[bucketIndex] == null)
			buckets[bucketIndex] = elem;

		// update max bucket index if needed
		if (bucketIndex > maxBucket)
			maxBucket = bucketIndex;

		elem.bucketIndex = bucketIndex;

		count++;

		linkAssociations.put(object, elem);

		return true;
	}

}