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
	private int[] pointOffsets = new int[pointGeometryNb.length];
	
	
	
	
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
		int lod = 0;
		
		pointOffsets[lod] = geometriesNumber;
		geometriesNumber += getPointGeometryNumber(lod);


		//Application.debug("point : "+geometriesNumber);
		
		//segments
		lod = 0;
		
		segmentsOffsets[lod] = geometriesNumber;
		geometriesNumber += getSegmentGeometryNumber(lod);
     	
		//Application.debug("segment : "+geometriesNumber);
		
		////////////////////////////////////
		//creates geometries 

		
    	vertices = BufferUtil.newFloatBuffer(geometriesNumber * 3);
    	normals = BufferUtil.newFloatBuffer(geometriesNumber * 3);
    	texture = BufferUtil.newFloatBuffer(geometriesNumber * 3);
    	
        gl.glGenBuffersARB(1, vboVertices, 0);  // Generate  The Vertex Buffer
        gl.glGenBuffersARB(1, vboNormals, 0);  // Generate  The Normal Buffer
        gl.glGenBuffersARB(1, vboTexture, 0);  // Generate  The Normal Buffer

		
		//points		
    	
        lod=0;
    	pointGeometry(
    			pointGeometryNb[lod][0],
    			pointGeometryNb[lod][1]);
    	
    	//segments		
        
        lod = 0;
    	segmentGeometry(segmentGeometryNb[lod]);
    	
    	
    	
    	
    	
    	
    	
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
    

    	//enableVBO(gl);
    	
        gl.glDrawArrays(GL.GL_QUADS, 
        		pointOffsets[pointLOD[size-1]], 
        		getPointGeometryNumber(pointLOD[size-1])); 
        

       

        //disableVBO(gl);


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
    
    	//enableVBO(gl);
       
        gl.glDrawArrays(GL.GL_QUADS, 
        		segmentsOffsets[segmentLOD[thickness-1]], 
        		getSegmentGeometryNumber(segmentLOD[thickness-1])); 
        
        
        //disableVBO(gl);

    
   	 
    	 
    }
    
    
    


	///////////////////////////////
	// FOR VBOs
	///////////////////////////////
    
    /**
     * enable use of VBOs
     * @param gl opengl context
     */
    public void enableVBO(GL gl){
    	gl.glEnableClientState(GL.GL_VERTEX_ARRAY);  // Enable Vertex Arrays
    	gl.glEnableClientState(GL.GL_NORMAL_ARRAY);  // Enable Normal Arrays
    	gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);  // Enable texture Arrays

    }
    
    /**
     * disable use of VBOs
     * @param gl opengl context
     */
    public void disableVBO(GL gl){
        gl.glDisableClientState(GL.GL_VERTEX_ARRAY);  
        gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
        gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
    }
	
    
    
	

}
