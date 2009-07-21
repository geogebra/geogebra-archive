package geogebra3D.euclidian3D.opengl;

import javax.media.opengl.GL;

/**
 * Class for primitives drawn in 3D renderer.
 * 
 * @author ggb3D
 *
 */
public class RendererPrimitives {
	
	/** radius for drawing 3D points*/
	private static final float POINT3D_RADIUS = 1.4f;
	
	/** start index for point primitives list */
	private int pointIndex;
	/** TODO point primitives number of latitudes */
	private int[] pointLatitudes  = {1,2,2, 2,2,2, 2,2,2};
	/** TODO point primitives number of longitudes */
	private int[] pointLongitudes = {2,4,8, 8,8,8, 8,8,8};
	
	/**
	 * default constructor
	 * @param gl opengl drawing parameter
	 */
	public RendererPrimitives(GL gl){
		
		
		//points
		pointIndex = gl.glGenLists(9);
		for (int i=0;i<9;i++){
			gl.glNewList(pointIndex+i, GL.GL_COMPILE);
			pointList(gl, 1+i);
			gl.glEndList();
		}
		
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
    public void pointList(GL gl, int size){
    	
    	int latitude = pointLatitudes[size-1];
    	int longitude = pointLongitudes[size-1];
    	
    	//gl.glShadeModel(GL.GL_FLAT);
    	

    	float da = (float) (Math.PI / (2*latitude)) ; 
    	float db = (float) ( 2*Math.PI / longitude ); 
    	
    	gl.glBegin(GL.GL_QUADS); 
    	
    	
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
    			//gl.glNormal3f((x1+x2+x3+x4)/4,(y1+y2+y3+y4)/4,(z1+z4)/2); 
    			
    			gl.glNormal3f(x1,y1,z1); 
    			gl.glVertex3f(x1,y1,z1); 
    			
    			gl.glNormal3f(x2,y2,z1); 
    			gl.glVertex3f(x2,y2,z1);
    			
    			gl.glNormal3f(x3,y3,z4); 
    			gl.glVertex3f(x3,y3,z4); 
    			
    			gl.glNormal3f(x4,y4,z4); 
    			gl.glVertex3f(x4,y4,z4); 
    
    			
    			//bottom face
    			//gl.glNormal3f((x1+x2+x3+x4)/4,(y1+y2+y3+y4)/4,-(z1+z4)/2); 
    			
    			gl.glNormal3f(x2,y2,-z1); 
    			gl.glVertex3f(x2,y2,-z1); 
    			
    			gl.glNormal3f(x1,y1,-z1); 
    			gl.glVertex3f(x1,y1,-z1);
    			
    			gl.glNormal3f(x4,y4,-z4); 
    			gl.glVertex3f(x4,y4,-z4); 
    			
    			gl.glNormal3f(x3,y3,-z4); 
    			gl.glVertex3f(x3,y3,-z4); 


    			
    			
    			
    			x1=x2;y1=y2;
    			x4=x3;y4=y3;
    			

    		} 
    		
    		rXY1 = rXY4; z1 = z4;
    	} 
    	gl.glEnd();  

    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //////////////////////////////
    // TODO remove
    
    
    
    
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
