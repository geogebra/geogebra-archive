package geogebra3D.euclidian3D.opengl;

import javax.media.opengl.GL;

/**
 * Class for primitives drawn in 3D renderer.
 * 
 * @author ggb3D
 *
 */
public class RendererPrimitives {
	
	
	//////////////
	// opengl
	
	private GL gl;
	
	//////////////
	// points
	
	/** radius for drawing 3D points*/
	private static final float POINT3D_RADIUS = 1.4f;	
	/** start index for point primitives list */
	private int pointIndex;
	/** number of different sizes of points */
	protected static final int POINT_SIZE_NUMBER = 9;
	/** TODO point primitives number of latitudes */
	protected int[] pointLatitudes  = {2,2,2, 2,2,2, 2,2,2};
	/** TODO point primitives number of longitudes */
	protected int[] pointLongitudes = {8,8,8, 8,8,8, 8,8,8};
	
	
	
	//////////////
	// lines
	
	/** radius for drawing 3D points*/
	private static final float LINE3D_THICKNESS = 0.5f;
	/** start index for segment primitives list */
	private int segmentIndex;
	
	
	
	
	
	
	/**
	 * empty constructor
	 */
	protected RendererPrimitives(){
		
	}
	
	
	
	
	/**
	 * default constructor
	 * @param gl opengl drawing parameter
	 */
	public RendererPrimitives(GL gl){
		
		this();
		
		//points
		pointIndex = gl.glGenLists(9);
		for (int i=0;i<9;i++){
			gl.glNewList(pointIndex+i, GL.GL_COMPILE);
			pointList(gl, 1+i);
			gl.glEndList();
		}
		
		//segments
		segmentIndex = gl.glGenLists(1);
		for (int i=0;i<1;i++){
			gl.glNewList(segmentIndex+i, GL.GL_COMPILE);
			segmentList(gl, 1+i);
			gl.glEndList();
		}
		
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
		gl.glVertex3f(x,y,z); 
	}
	
	
	/** creates a vertex at coordinates (x,y,z)
	 * @param x x coord
	 * @param y y coord
	 * @param z z coord
	 */
	protected void normal(float x, float y, float z){
		gl.glNormal3f(x,y,z); 
	}
	
	
	/** creates a texture at coordinates (x,y)
	 * @param x x coord
	 * @param y y coord
	 */
	protected void texture(float x, float y){
		gl.glTexCoord2f(x,y);
	}
	
	
	
	///////////////////////////////
	// POINTS
	///////////////////////////////
	
	/**
	 * return the number of geometries for drawing a point
	 * @param size size of the point
	 * @return the number of geometries for drawing a point
	 */
	protected int getPointGeometryNumber(int size){
		return pointLatitudes[size-1]*pointLongitudes[size-1]*8;
	}
    
	
    /**
     * draws a point
     * 
     * @param gl opengl drawing parameter
     * @param size size of the point
     */
    public void point(GL gl, int size){
    
    	gl.glCallList(pointIndex+size-1);
    	//pointList(gl, size);
    }
    	
    	
    
    /** create a gl list for points
     * @param gl gl opengl drawing parameter
     * @param size size of the point
     */
    private void pointList(GL gl, int size){
    	
    	gl.glBegin(GL.GL_QUADS); 
    	
    	this.gl = gl;    	
    	pointGeometry(size, pointLatitudes[size-1],pointLongitudes[size-1]);
    	
    	gl.glEnd();  

    }
    
    /** create the vertices an normals for points
     * @param size size of the point
     * @param latitude number of latitudes
     * @param longitude number of longitudes
     */
    protected void pointGeometry(int size, int latitude, int longitude){
    	
 
    	float da = (float) (Math.PI / (2*latitude)) ; 
    	float db = (float) ( 2*Math.PI / longitude ); 
    	
    	
    	
    	float rXY1 = POINT3D_RADIUS*size;
    	float z1 = 0;
    	
    	for( int i = 0; i < latitude  ; i++ ) { 
    		float rXY4 = POINT3D_RADIUS * size * (float) Math.cos( (i+1) * da ); 
    		float z4   = POINT3D_RADIUS * size * (float) Math.sin( (i+1) * da ); 
    		
    		float x1 = rXY1;
    		float y1 = 0;
    		float x4 = rXY4;
    		float y4 = 0;

    		for( int j = 0; j < longitude  ; j++ ) { 
    			float x2 = rXY1 * (float) Math.cos( (j+1) * db ); 
    			float y2 = rXY1 * (float) Math.sin( (j+1) * db ); 

    			float x3 = rXY4 * (float) Math.cos( (j+1) * db ); 
    			float y3 = rXY4 * (float) Math.sin( (j+1) * db ); 
    			
    			
    			//up face
    			
    			normal(x1,y1,z1); 
    			vertex(x1,y1,z1); 
    			
    			normal(x2,y2,z1); 
    			vertex(x2,y2,z1);
    			
    			normal(x3,y3,z4); 
    			vertex(x3,y3,z4); 
    			
    			normal(x4,y4,z4); 
    			vertex(x4,y4,z4); 
    
    			
    			//bottom face
    			
    			normal(x2,y2,-z1); 
    			vertex(x2,y2,-z1); 
    			
    			normal(x1,y1,-z1); 
    			vertex(x1,y1,-z1);
    			
    			normal(x4,y4,-z4); 
    			vertex(x4,y4,-z4); 
    			
    			normal(x3,y3,-z4); 
    			vertex(x3,y3,-z4); 


    			
    			
    			
    			x1=x2;y1=y2;
    			x4=x3;y4=y3;
    			

    		} 
    		
    		rXY1 = rXY4; z1 = z4;
    	} 
    	
    }

    
    
	///////////////////////////////
	// SEGMENTS
	///////////////////////////////
    
    
	/**
	 * return the number of geometries for drawing a segment
	 * @param thickness thickness of the segment
	 * @return the number of geometries for drawing a segment
	 */
	protected int getSegmentGeometryNumber(int thickness){
		int latitude = 8; //TODO list
		return (latitude+1)*4;
	}
	

    public void segment(GL gl, int thickness){
    	gl.glCallList(segmentIndex);
    	//segmentList(gl, thickness);
    }

    private void segmentList(GL gl, int thickness){
    	
    	gl.glBegin(GL.GL_QUADS); 
    	
    	this.gl = gl;    	
    	segmentGeometry(thickness);
    	
    	gl.glEnd();  
    }

    	
    	
    protected void segmentGeometry(int thickness){

    	int latitude = 8;
    	
    	float dt = (float) 1/latitude;
    	float da = (float) (2*Math.PI *dt) ; 
    	gl.glBegin(GL.GL_QUADS); 

    	for( int i = 0; i < latitude + 1 ; i++ ) { 
    		float y0 = 2 * LINE3D_THICKNESS * (float) Math.sin ( i * da ); 
    		float z0 = 2 * LINE3D_THICKNESS * (float) Math.cos ( i * da ); 
    		float y1 = 2 * LINE3D_THICKNESS * (float) Math.sin ( (i+1) * da ); 
    		float z1 = 2 * LINE3D_THICKNESS * (float) Math.cos ( (i+1) * da ); 

    		texture(0,i*dt);
    		normal(0,y0,z0); 
    		vertex(0,y0,z0); 


    		texture(1,i*dt);
    		normal(1,y0,z0); 
    		vertex(1,y0,z0); 

    		texture(1,(i+1)*dt);
    		normal(1,y1,z1); 
    		vertex(1,y1,z1); 

    		texture(0,(i+1)*dt);
    		normal(0,y1,z1); 
    		vertex(0,y1,z1); 


    	} 
    	gl.glEnd();  
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //////////////////////////////
    // TODO remove below 
    
    
    
    
    /**
     * draws a sphere
     * 
     * @param gl opengl drawing parameter
     * @param radius radius of the sphere
     * @param latitude number of latitudes
     * @param longitude number of longitudes
     */
    public void drawSphere(GL gl, double radius, int latitude, int longitude){
    	
    	gl.glScaled(radius, radius, radius);
    	
    	

    	float da = (float) (Math.PI / latitude) ; 
    	float db = (float) ( 2.0f * Math.PI / longitude ); 
    	gl.glBegin(GL.GL_QUADS); 
    	
    	for( int i = 0; i < latitude + 1 ; i++ ) { 
    		float r0 = (float) Math.sin ( i * da ); 
    		float y0 = (float) Math.cos ( i * da ); 
    		float r1 = (float) Math.sin ( (i+1) * da ); 
    		float y1 = (float) Math.cos ( (i+1) * da ); 

    		for( int j = 0; j < longitude + 1 ; j++ ) { 
    			float x0 = r0 * (float) Math.sin( j * db ); 
    			float z0 = r0 * (float) Math.cos( j * db ); 
    			float x1 = r0 * (float) Math.sin( (j+1) * db ); 
    			float z1 = r0 * (float) Math.cos( (j+1) * db ); 

    			float x2 = r1 * (float) Math.sin( j * db ); 
    			float z2 = r1 * (float) Math.cos( j * db ); 
    			float x3 = r1 * (float) Math.sin( (j+1) * db ); 
    			float z3 = r1 * (float) Math.cos( (j+1) * db ); 

    			gl.glNormal3f(x0,y0,z0); 
    			gl.glVertex3f(x0,y0,z0); 


    			gl.glNormal3f(x2,y1,z2); 
    			gl.glVertex3f(x2,y1,z2); 

    			gl.glNormal3f(x3,y1,z3); 
    			gl.glVertex3f(x3,y1,z3); 

    			gl.glNormal3f(x1,y0,z1); 
    			gl.glVertex3f(x1,y0,z1); 

    		} 
    	} 
    	gl.glEnd();  

    }
	

}
