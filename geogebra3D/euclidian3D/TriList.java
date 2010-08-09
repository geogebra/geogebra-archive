package geogebra3D.euclidian3D;

import java.nio.FloatBuffer;

/**
 * A list of triangles representing the current mesh.
 * @author André Eriksson
 */
public class TriList {
	/** the total amount of triangles available for allocation*/
	private int capacity;
	
	/** current triangle amt */
	private int count = 0;
	
	private int marigin;
	
//	private double totalError = 0;
	
	/** A buffer containing data for all the triangles. Each triangle is stored
	 *  as 9 consecutive floats (representing x/y/z values for three points).
	 *  The triangles are packed tightly.
	 */
	private FloatBuffer vertexBuf;
	/** A counterpart to tribuf containing normals */
	private FloatBuffer normalBuf;
	
	/** Pointers to the front and back of the queue */
	private TriListElem front, back;

	/**
	 * Empty constuctor. Allocates memory for vertexBuf.
	 * @param capacity the maximum number of triangles
	 * @param marigin free triangle amount before considered full
	 */
	TriList(int capacity, int marigin){
		this.capacity=capacity;
		vertexBuf=FloatBuffer.allocate(capacity*9);
		normalBuf = FloatBuffer.allocate(capacity*9);
	}
	
	/** 
	 * @return the current amount of triangles
	 */
	public int getCount() { return count; }

//	/**
//	 * @return the total error for all visible triangles
//	 */
//	public double getError() { return totalError; }
	
	/**
	 * @return a reference to vertexBuf
	 */
	public FloatBuffer getTriangleBuffer() { return vertexBuf; }
	
	/**
	 * @return a reference to normalBuf
	 */
	public FloatBuffer getNormalBuffer() { return normalBuf; }

	/**
	 * @return true if count>=maxCount - otherwise false.
	 */
	public boolean isFull() { return count>=capacity-marigin; }
	
	/** sets elements in the float buffers to the provided values
	 * @param vertices 9 floats representing 3 vertices
	 * @param normals 9 floats representing 3 normals
	 * @param index the index of the first float to be changed
	 */
	protected void setFloats(float[] vertices, float[] normals, int index) {
		vertexBuf.position(index);
		vertexBuf.put(vertices);
		normalBuf.position(index);
		normalBuf.put(normals);
	}
	
	/**
	 * @param index
	 * @return float array of vertices (9 floats)
	 */
	protected float[] getVertices(int index) {
		float[] vertices = new float[9];
		vertexBuf.position(index);
		vertexBuf.get(vertices);
		return vertices;
	}
	
	/**
	 * @param index
	 * @return float array of normals (9 floats)
	 */
	protected float[] getNormals(int index) {
		float[] normals = new float[9];
		normalBuf.position(index);
		normalBuf.get(normals);
		return normals;
	}
	
	/**
	 * Adds a triangle to the list.
	 * @param vertices the tree vertices in the triangle stored as 9 floats
	 * @param normals the normals of the vertices stored as 9 floats
	 * @return a reference to the created triangle element
	 */
	public TriListElem add(float[] vertices, float[] normals) {
		
		TriListElem t = new TriListElem(back);
		if(front==null)
			front=t;		
		if(back!=null)
			back.setNext(t);
		back=t;
		
		setFloats(vertices,normals,9*count);
		
		t.setIndex(9*count);

		count++;
		
		return t;
	}
	
	/**
	 * transfers nine consecutive floats from one place in the buffers to another
	 * @param oldIndex the old index of the first float
	 * @param newIndex the new index of the first float
	 */
	protected void transferFloats(int oldIndex, int newIndex) {
		float[] f = new float[9];
		float[] g = new float[9];
		
		vertexBuf.position(oldIndex);
		vertexBuf.get(f);
		vertexBuf.position(newIndex);
		
		normalBuf.position(oldIndex);
		normalBuf.get(g);
		normalBuf.position(newIndex);
		
		for(int i=0;i<9;i++){
			vertexBuf.put(f[i]);
			normalBuf.put(g[i]);
		}
	}
	
	/**
	 * Tests the list for consistency.
	 */
	protected void consistencyCheck(){
		int i;
		TriListElem o = front;
		for(i = 0; o!=back; i++){
			try{
				if(!o.getNext().getPrev().equals(o))
					System.err.println("Error in TriangleList: invalid order");
				o=o.getNext();
			}catch(NullPointerException e){
				System.err.println(e);
			}
		}
		if(i!=(count-1<0?0:count-1))
			System.err.println("Error in TriangleList: invalid count");
	}
	
	/**
	 * Removes a triangle from the queue.
	 * @param t
	 */
	public void remove(TriListElem t) {
		hide(t);
	}

	/**
	 * removes a triangle from the list, but does not erase it
	 * @param t any triangle in the list
	 */
	public boolean hide(TriListElem t) {
		if(t==null || t.getIndex()==-1)
			return false;
		
		t.pushVertices(getVertices(t.getIndex()));
		t.pushNormals(getNormals(t.getIndex()));
		
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
			
			TriListElem prev = t.getPrev();
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
			TriListElem prevBack = back;
			
			back=prevBack.getPrev();
			back.setNext(null);
			
			TriListElem next = t.getNext();
			TriListElem prev = t.getPrev();
			
			prevBack.setNext(next);
			prevBack.setPrev(prev);
			
			if(prev!=null)
				prev.setNext(prevBack);
			next.setPrev(prevBack);
			
			if(front==t)
				front=prevBack;
		}
		
		t.setIndex(-1);
		t.setNext(null);
		t.setPrev(null);
		
		count--;
		return true;
	}

	/**
	 * shows a triangle that has been hidden
	 * @param t any hidden triangle in the list
	 */
	public boolean show(TriListElem t) {
		
		if(t==null || t.getIndex()!=-1)
			return false;
		
		if(front==null)
			front=t;		
		if(back!=null){
			back.setNext(t);
			t.setPrev(back);
		}
		back=t;
		
		setFloats(t.popVertices(),t.popNormals(),9*count);
		
		t.setIndex(9*count);
		
		count++;
		
		return true;
	}
}

/**
 * A class representing a triangle in {@link DrawList}.
 * @author André Eriksson
 */
class TriListElem{
	private int index;
	private TriListElem next, prev;
	
	private float[] vertices;
	private float[] normals;

	/** 
	 * @param prev the previous element in the queue
	 */
	TriListElem(TriListElem prev) {
        this.prev=prev;
	}
	
	public void pushVertices(float[] vertices){ this.vertices=vertices; }
	public void pushNormals(float[] normals)  { this.normals =normals;  }
	
	public float[] popVertices(){ 
		float[] temp = vertices;
		vertices = null;
		return temp;
	}
	public float[] popNormals(){ 
		float[] temp = normals;
		normals = null;
		return temp;
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
	public TriListElem getNext() { return next; }

	/**
	 * @param next a reference to the next triangle in the queue.
	 */
	public void setNext(TriListElem next) { this.next = next; }

	/**
	 * @return a reference to the previous triangle in the queue.
	 */
	public TriListElem getPrev() { return prev; }
	
	/**
	 * @param prev a reference to the previous triangle in the queue.
	 */
	public void setPrev(TriListElem prev) { this.prev = prev; }
}