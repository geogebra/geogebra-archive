package geogebra3D.euclidian3D.opengl;

import geogebra.main.Application;

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
	
	
	/** index of vbo vertices */
	private int[] vboVertices = new int[1];  // Vertex VBO Name 
	/** index of vbo normals */
	private int[] vboNormals = new int[1];  // Normal VBO Name 
	/** index of vbo texture */
	private int[] vboTexture = new int[1];  // Normal VBO Name 
	
	
	//////////////
	// points

	/** offsets for points */
	private int[] pointOffsets = new int[POINT_SIZE_NUMBER];
	
	
	
	
	//////////////
	// segment
	
	/** offsets for segments */
	private int[] segmentsOffsets = new int[THICKNESS_NUMBER];

	
	/**
	 * default constructor
	 * @param gl opengl drawing parameter
	 */
	public RendererPrimitivesVBO(GL gl) {
		
		super(gl); //TODO remove
		
		////////////////////////////////////
		//count number of geometries needed
		//and store the offsets
		
		int geometriesNumber = 0;
		
		//points
		int size = 3;
		
		pointOffsets[size-1] = geometriesNumber;
		geometriesNumber += getPointGeometryNumber(size);


		Application.debug("point : "+geometriesNumber);
		
		//segments
		int thickness = 2;
		
		segmentsOffsets[thickness-1] = geometriesNumber;
		geometriesNumber += getSegmentGeometryNumber(thickness);
     	
		Application.debug("segment : "+geometriesNumber);
		
		////////////////////////////////////
		//creates geometries 

		
    	vertices = BufferUtil.newFloatBuffer(geometriesNumber * 3);
    	normals = BufferUtil.newFloatBuffer(geometriesNumber * 3);
    	texture = BufferUtil.newFloatBuffer(geometriesNumber * 3);
    	
        gl.glGenBuffersARB(1, vboVertices, 0);  // Generate  The Vertex Buffer
        gl.glGenBuffersARB(1, vboNormals, 0);  // Generate  The Normal Buffer
        gl.glGenBuffersARB(1, vboTexture, 0);  // Generate  The Normal Buffer

		
		//points		
    	
        size=3;
    	pointGeometry(size,pointLatitudes[size-1],pointLongitudes[size-1]);

    	
    	//segments		
        
        thickness = 2;
    	segmentGeometry(thickness);
    	
    	
    	
    	
    	
    	
    	
    	//bind the buffers
    	
    	vertices.flip();
    	normals.flip();
    	texture.flip();
    	
       	gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, vboVertices[0]);  
        gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, 
        		geometriesNumber * 3 * BufferUtil.SIZEOF_FLOAT, 
        		vertices, GL.GL_STATIC_DRAW_ARB);
        
        gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, vboNormals[0]);  
        gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, 
        		geometriesNumber * 3 * BufferUtil.SIZEOF_FLOAT, 
        		normals, GL.GL_STATIC_DRAW_ARB);
        
        
        gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, vboTexture[0]);		// Bind The Buffer
        gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, 
        		geometriesNumber * 2 * BufferUtil.SIZEOF_FLOAT, 
        		texture, GL.GL_STATIC_DRAW_ARB);

        
      
    	
       
        
        vertices = null;
        normals = null;
        texture = null;
        
        
        
        
        
        
        
        
        
        bindBuffersAndSetPointers(gl);
	}
	
	
	
	
	
	private void bindBuffersAndSetPointers(GL gl){
		
    	gl.glEnableClientState(GL.GL_VERTEX_ARRAY);  // Enable Vertex Arrays
    	gl.glEnableClientState(GL.GL_NORMAL_ARRAY);  // Enable Normal Arrays
    	gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);  // Enable texture Arrays
    	
		
		
    	gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, vboNormals[0]);
        // Set The normal Pointer To The normal Buffer
        gl.glNormalPointer(GL.GL_FLOAT, 0, 0);   
        
    	gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, vboVertices[0]);
        // Set The Vertex Pointer To The Vertex Buffer
        gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);   
        
        
    	gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, vboTexture[0]);
    	gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, 0);
    	
    	
        gl.glDisableClientState(GL.GL_VERTEX_ARRAY);  
        gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
        gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
        
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
    
    	gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
    	gl.glEnableClientState(GL.GL_VERTEX_ARRAY);  // Enable Vertex Arrays
    	gl.glEnableClientState(GL.GL_NORMAL_ARRAY);  // Enable Normal Arrays
    	

        gl.glDrawArrays(GL.GL_QUADS, 0, getPointGeometryNumber(size)); 
        

        
        // Disable Pointers
        // Disable Vertex Arrays
        gl.glDisableClientState(GL.GL_VERTEX_ARRAY);  
        gl.glDisableClientState(GL.GL_NORMAL_ARRAY);  


        //gl.glFlush();
    }
    
  

	
	
    
    
	///////////////////////////////
	// SEGMENTS
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
    	

        

      

    	
        gl.glDrawArrays(GL.GL_QUADS, segmentsOffsets[thickness-1], getSegmentGeometryNumber(thickness)); 
        
        

        
        // Disable Pointers
        // Disable Vertex Arrays
        gl.glDisableClientState(GL.GL_VERTEX_ARRAY);  
        gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
        gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);

        //gl.glFlush();
   	 
    	 
    }
    
    
    


	
	
    
    
	

}
