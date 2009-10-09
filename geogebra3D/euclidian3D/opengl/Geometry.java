package geogebra3D.euclidian3D.opengl;

/**
 * Common class for openGL geometry objects
 * 
 * @author ggb3D
 *
 */

public abstract class Geometry {
	
	/** radius for drawing 3D points*/
	protected static final float POINT3D_RADIUS = 1.4f;	
	
	/** thickness for drawing 3D lines*/
	protected static final float LINE3D_THICKNESS = 0.17f;


	
	/**  geometryRenderer that draws the geometry */
	protected GeometryRenderer geometryRenderer;
	
	
	/**
	 * Create the geometry linked to the renderer
	 * @param geometryRenderer
	 */
	public Geometry(GeometryRenderer geometryRenderer){
		
		this.geometryRenderer = geometryRenderer;
		
		init();
	}
	
	
	/**
	 * initializes the geometry
	 * used by the goemetryRenderer for GL lists and VBOs
	 */
	abstract public void init();
	
	
	
	/**
	 * calls the goemetryRenderer drawing method
	 */
	public void draw(){
		
		geometryRenderer.draw(this);
	}
	
	
	
	

	/** creates a vertex at coordinates (x,y,z)
	 * @param x x coord
	 * @param y y coord
	 * @param z z coord
	 */
	protected void vertex(float x, float y, float z){
		geometryRenderer.vertex(x, y, z);
	}
	
	
	/** creates a normal at coordinates (x,y,z)
	 * @param x x coord
	 * @param y y coord
	 * @param z z coord
	 */
	protected void normal(float x, float y, float z){
		geometryRenderer.normal(x, y, z);
	}
	
	
	/** creates a texture at coordinates (x,y)
	 * @param x x coord
	 * @param y y coord
	 */
	protected void texture(float x, float y){
		geometryRenderer.texture(x, y);
	}

	
	
	
	
	
	
	
	
	
	
	
	
    /** create the vertices an normals for spheres
     * @param latitude number of latitudes
     * @param longitude number of longitudes
     * @param radius radius of the sphere
     */
    protected void sphere(int latitude, int longitude, float radius){
    	
 
    	float da = (float) (Math.PI / (2*latitude)) ; 
    	float db = (float) ( 2*Math.PI / longitude ); 
    	
    	
    	
    	float rXY1 = radius;
    	float z1 = 0;
    	
    	for( int i = 0; i < latitude  ; i++ ) { 
    		float rXY4 = radius * (float) Math.cos( (i+1) * da ); 
    		float z4   = radius * (float) Math.sin( (i+1) * da ); 
    		
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
    			texture(j/longitude,i/(2*latitude));
    			vertex(x1,y1,z1); 
    			
    			texture((j+1)/longitude,i/(2*latitude));
    			normal(x2,y2,z1); 
    			vertex(x2,y2,z1);
    			
    			texture((j+1)/longitude,(i+1)/(2*latitude));
    			normal(x3,y3,z4); 
    			vertex(x3,y3,z4); 
    			
    			texture(j/longitude,(i+1)/(2*latitude));
    			normal(x4,y4,z4); 
    			vertex(x4,y4,z4); 
    
    			
    			//bottom face
    			
    			normal(x2,y2,-z1); 
    			texture((j+1)/longitude,-i/(2*latitude));
    			vertex(x2,y2,-z1); 
    			
    			normal(x1,y1,-z1); 
    			texture(j/longitude,-i/(2*latitude));
    			vertex(x1,y1,-z1);
    			
    			normal(x4,y4,-z4); 
    			texture(j/longitude,-(i+1)/(2*latitude));
    			vertex(x4,y4,-z4); 
    			
    			normal(x3,y3,-z4); 
    			texture((j+1)/longitude,-(i+1)/(2*latitude));
    			vertex(x3,y3,-z4); 

    			
    			
    			x1=x2;y1=y2;
    			x4=x3;y4=y3;
    			

    		} 
    		
    		rXY1 = rXY4; z1 = z4;
    	} 
    	
    }
    
    
    
    
    
    
    
    
    /** create a segment geometry ((Ox)-cylinder with number of latitudes)
     * @param latitude number of latitudes
     * @param thickness thickness of the cylinder
     */
    protected void cylinder(int latitude, float thickness){

    	
    	float dt = (float) 1/latitude;
    	float da = (float) (2*Math.PI *dt) ; 

    	for( int i = 0; i < latitude + 1 ; i++ ) { 
    		float y0 = 2 * thickness * (float) Math.sin ( i * da ); 
    		float z0 = 2 * thickness * (float) Math.cos ( i * da ); 
    		float y1 = 2 * thickness * (float) Math.sin ( (i+1) * da ); 
    		float z1 = 2 * thickness * (float) Math.cos ( (i+1) * da ); 

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
    }
    
    
    
    


}








