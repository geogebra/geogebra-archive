package geogebra3D.euclidian3D.opengl;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;

import com.sun.opengl.util.BufferUtil;



/**
 * Class for primitives drawn in 3D renderer, using VBOs.
 * 
 * @author ggb3D
 *
 */
public class RendererPrimitivesVBO extends RendererPrimitives {

	//////////////
	// opengl
	
	private FloatBuffer vertices;
	private FloatBuffer normals;
	private FloatBuffer texture;
	
	//////////////
	// points
	
	/** index of vertices of 3D points*/
	private int[] pointVertices = new int[1];  // Vertex VBO Name 
	/** index of normals of 3D points*/
	private int[] pointNormals = new int[1];  // Normal VBO Name 
	/** number of vertices of 3D points*/
	private int[] pointVerticesNumber = new int[1];
	
	
	//////////////
	// segment
	
	/** index of vertices of 3D segments */
	private int[] segmentVertices = new int[1];  // Vertex VBO Name 
	/** index of normals of 3D segments*/
	private int[] segmentNormals = new int[1];  // Normal VBO Name 
	/** index of texture of 3D segments*/
	private int[] segmentTexture = new int[1];  // Normal VBO Name 
	/** number of vertices of 3D segments*/
	private int[] segmentVerticesNumber = new int[1];
	
	
	/**
	 * default constructor
	 * @param gl opengl drawing parameter
	 */
	public RendererPrimitivesVBO(GL gl) {
		
		super(gl); //TODO remove
		
		
		
		//points		
		// Generate And Bind The Vertex Buffer
        gl.glGenBuffersARB(1, pointVertices, 0);  // Get A Valid Name
        gl.glGenBuffersARB(1, pointNormals, 0);  // Get A Valid Name
        pointVBO(gl, 1);
        
        //segments		
		// Generate And Bind The Vertex Buffer
        gl.glGenBuffersARB(1, segmentVertices, 0);  // Get A Valid Name
        gl.glGenBuffersARB(1, segmentNormals, 0);  // Get A Valid Name
        gl.glGenBuffersARB(1, segmentTexture, 0);  // Get A Valid Name
        segmentVBO(gl, 1);
        
	}
	
	
	
	
	
	
	///////////////////////////////
	// GEOMETRY
	///////////////////////////////
	
	/** creates a vertex at coordinates (x,y,z)
	 * @param x x coord
	 * @param y y coord
	 * @param z z coord
	 */
	protected void vertex(float x, float y, float z){
		if (vertices!=null){
			vertices.put(x);vertices.put(y);vertices.put(z);
		}else
			super.vertex(x, y, z);
	}
	
	
	/** creates a vertex at coordinates (x,y,z)
	 * @param x x coord
	 * @param y y coord
	 * @param z z coord
	 */
	protected void normal(float x, float y, float z){
		if (normals!=null){
			normals.put(x);normals.put(y);normals.put(z);
		}else
			super.normal(x, y, z);
	}
	
	
	
	/** creates a texture at coordinates (x,y)
	 * @param x x coord
	 * @param y y coord
	 */
	protected void texture(float x, float y){
		if (texture!=null){
			texture.put(x);texture.put(y);
		}else
			super.texture(x, y);
	}
	
	
	
	
	
	
	///////////////////////////////
	// POINTS
	///////////////////////////////
	

    
	
    /**
     * draws a point
     * 
     * @param gl opengl drawing parameter
     * @param size size of the point
     */
    public void point(GL gl, int size){
    
    	gl.glEnableClientState(GL.GL_VERTEX_ARRAY);  // Enable Vertex Arrays
    	gl.glEnableClientState(GL.GL_NORMAL_ARRAY);  // Enable Normal Arrays
    	
    	gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, pointNormals[0]);
        // Set The Vertex Pointer To The Vertex Buffer
        gl.glNormalPointer(GL.GL_FLOAT, 0, 0);   
        
    	gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, pointVertices[0]);
        // Set The Vertex Pointer To The Vertex Buffer
        gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);   
        
        
        //gl.glDrawArrays(GL.GL_TRIANGLES, 0, vertexCount); 
        gl.glDrawArrays(GL.GL_QUADS, 0, pointVerticesNumber[0]); 
        

        
        // Disable Pointers
        // Disable Vertex Arrays
        gl.glDisableClientState(GL.GL_VERTEX_ARRAY);  
        gl.glDisableClientState(GL.GL_NORMAL_ARRAY);  

    }
    
    
    
    private void pointVBO(GL gl, int size){
    	
    	
    	int latitude = pointLatitudes[size-1];
    	int longitude = pointLongitudes[size-1];
 
  

    	
    	
    	pointVerticesNumber[size-1] = getPointGeometryNumber(size);
    	
    	vertices = BufferUtil.newFloatBuffer(pointVerticesNumber[size-1] * 3);
    	normals = BufferUtil.newFloatBuffer(pointVerticesNumber[size-1] * 3);
    	
    	pointGeometry(3,latitude,longitude);
    	
    	
    	vertices.flip();
    	normals.flip();
    	
    	
       	gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, pointVertices[size-1]);  // Bind The Buffer
        
        // Load The Data
        gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, pointVerticesNumber[size-1] * 3 * 
                BufferUtil.SIZEOF_FLOAT, vertices, GL.GL_STATIC_DRAW_ARB);
        
        
       	gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, pointNormals[size-1]);  // Bind The Buffer
        
        // Load The Data
        gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, pointVerticesNumber[size-1] * 3 * 
                BufferUtil.SIZEOF_FLOAT, normals, GL.GL_STATIC_DRAW_ARB);
       
        
        vertices = null;
        normals = null;
    }

	
	
    
    
	///////////////////////////////
	// POINTS
	///////////////////////////////
    
	
    /**
     * draws a segment
     * 
     * @param gl opengl drawing parameter
     * @param thickness thickness of the segment
     */
    public void segment(GL gl, int thickness){
    
    	gl.glEnableClientState(GL.GL_VERTEX_ARRAY);  // Enable Vertex Arrays
    	gl.glEnableClientState(GL.GL_NORMAL_ARRAY);  // Enable Normal Arrays
    	gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);  // Enable texture Arrays
    	
    	gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, segmentNormals[0]);
        gl.glNormalPointer(GL.GL_FLOAT, 0, 0);   
        
    	gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, segmentVertices[0]);
        gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);   
        
    	gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, segmentTexture[0]);
    	gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, 0);
      
        //gl.glDrawArrays(GL.GL_TRIANGLES, 0, vertexCount); 
        gl.glDrawArrays(GL.GL_QUADS, 0, segmentVerticesNumber[0]); 
        

        
        // Disable Pointers
        // Disable Vertex Arrays
        gl.glDisableClientState(GL.GL_VERTEX_ARRAY);  
        gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
        gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);

    }
    
    
    
    private void segmentVBO(GL gl, int thickness){
    	
    	
    	int latitude = 8;
     	
    	segmentVerticesNumber[thickness-1] = 4 * (latitude+1);
    	
    	vertices = BufferUtil.newFloatBuffer(segmentVerticesNumber[thickness-1] * 3);
    	normals = BufferUtil.newFloatBuffer(segmentVerticesNumber[thickness-1] * 3);
    	texture = BufferUtil.newFloatBuffer(segmentVerticesNumber[thickness-1] * 2);
    	
    	segmentGeometry(2);
    	
    	
    	vertices.flip();
    	normals.flip();
    	texture.flip();
    	
    	
       	gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, segmentVertices[thickness-1]);  
        gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, segmentVerticesNumber[thickness-1] * 3 * 
                BufferUtil.SIZEOF_FLOAT, vertices, GL.GL_STATIC_DRAW_ARB);
        
        
       	gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, segmentNormals[thickness-1]); 
        gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, segmentVerticesNumber[thickness-1] * 3 * 
                BufferUtil.SIZEOF_FLOAT, normals, GL.GL_STATIC_DRAW_ARB);
        
        
        gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, segmentTexture[thickness-1]);	
        gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, segmentVerticesNumber[thickness-1] * 2 * 
        		BufferUtil.SIZEOF_FLOAT, texture, GL.GL_STATIC_DRAW_ARB);

       
        
        vertices = null;
        normals = null;
    }

	
	
    
    
	

}
