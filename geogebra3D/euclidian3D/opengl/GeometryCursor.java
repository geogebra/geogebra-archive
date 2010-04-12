package geogebra3D.euclidian3D.opengl;

import javax.media.opengl.GL;





/**
 * Class that describes the geometry of the 3D cursor
 * 
 * @author ggb3D
 *
 */
public class GeometryCursor extends Geometry {
	
	
	static public int TYPE_CROSS2D = 0;
	static public int TYPE_DIAMOND = 1;
	static public int TYPE_CYLINDER = 2;
	static public int TYPE_CROSS3D = 3;
	
	
	
	static private float size = 12f;
	static private float thickness = 1.25f;
	static private float thickness2 = 1.25f;
	static private float depth = 1f;
	

	/*
	static private float size = 120f;
	static private float thickness = 12.5f;
	static private float thickness2 = 12.5f;
	static private float depth = 10f;
	*/

	/** common constructor
	 * @param geometryRenderer
	 */
	public GeometryCursor(Manager manager) {
		super(manager,NORMAL_OFF,TEXTURE_OFF,COLOR_ON);
	}

	public void init() {
		
		manager.preInit(this);
		
		
		manager.startListAndGeometry(this, TYPE_CROSS2D);
		cursorCross2D();
		manager.endListAndGeometry();
		
		manager.startListAndGeometry(this, TYPE_DIAMOND);
		cursorDiamond();
		manager.endListAndGeometry();
		
		
		manager.startListAndGeometry(this, TYPE_CYLINDER);
		cursorCylinder();
		manager.endListAndGeometry();

		
		manager.startListAndGeometry(this, TYPE_CROSS3D);
		cursorCross3D();
		manager.endListAndGeometry();
		
		
	}
	
	
	public int getType(){
		return GL.GL_QUADS;
	}
	
	public int getNb(){
		return 4;
	}
	
	
	//////////////////////////////////
	// GEOMETRIES
	//////////////////////////////////
	
	
	
	
	
	private void cursorCross2D(){
		


		
		//white parts
		color(1,1,1);

		//up
		vertex(thickness, size, depth);
		vertex(-thickness, size, depth);
		vertex(-thickness, -size, depth);
		vertex(thickness, -size, depth);
				
		vertex(size, thickness, depth);
		vertex(thickness, thickness, depth);
		vertex(thickness, -thickness, depth);
		vertex(size, -thickness, depth);
		
		vertex(-size, thickness, depth);
		vertex(-size, -thickness, depth);
		vertex(-thickness, -thickness, depth);
		vertex(-thickness, thickness, depth);
		
		//down
		vertex(thickness, size, -depth);
		vertex(thickness, -size, -depth);
		vertex(-thickness, -size, -depth);
		vertex(-thickness, size, -depth);
				
		vertex(size, thickness, -depth);
		vertex(size, -thickness, -depth);
		vertex(thickness, -thickness, -depth);
		vertex(thickness, thickness, -depth);
		
		vertex(-size, thickness, -depth);
		vertex(-thickness, thickness, -depth);
		vertex(-thickness, -thickness, -depth);
		vertex(-size, -thickness, -depth);
		
		
		
		//black parts
		color(0,0,0);
		

		//up and down
		quadSymxOyRotOz90SymOz(
				thickness, thickness, depth,
				thickness+thickness2, thickness+thickness2, depth,
				thickness+thickness2, size+thickness2, depth,
				thickness, size, depth
		);
		
		quadSymxOyRotOz90SymOz(
				thickness, -thickness, depth,
				thickness, -size, depth,
				thickness+thickness2, -size-thickness2, depth,
				thickness+thickness2, -thickness-thickness2, depth
		);
		
		quadSymxOyRotOz90SymOz(
				size, thickness,depth,
				size, -thickness,depth,
				size+thickness2, -thickness-thickness2,  depth,
				size+thickness2, thickness+thickness2,  depth
		);
		
		
		//edges
		quadSymxOyRotOz90SymOz(
				thickness+thickness2, thickness+thickness2, -depth,
				thickness+thickness2, size+thickness2, -depth,
				thickness+thickness2, size+thickness2, depth,
				thickness+thickness2, thickness+thickness2, depth
		);
		
		quadSymxOyRotOz90SymOz(
				thickness+thickness2, -thickness-thickness2, -depth,
				thickness+thickness2, -thickness-thickness2, depth,
				thickness+thickness2, -size-thickness2, depth,
				thickness+thickness2, -size-thickness2, -depth
		);
		
		quadRotOz90SymOz(
				size+thickness2, thickness+thickness2,  -depth,
				size+thickness2, thickness+thickness2,  depth,
				size+thickness2, -thickness-thickness2,  depth,
				size+thickness2, -thickness-thickness2,  -depth
		);	
		
		
		
	}
	
	
	
	private void cursorCross3D(){
		
		
		float t = (float) (thickness / Math.tan(Math.PI/8));
		
		float size2 = size+thickness2;
		
		//white parts
		color(1,1,1);
		
		quadSymxOyRotOz90SymOz(
				thickness, t, t, 
				-thickness, t, t,
				-thickness, t, size2,
				thickness, t, size2
				);
		
		quadSymxOyRotOz90SymOz(
				thickness, t, t, 
				thickness, size2, t,
				-thickness, size2, t, 
				-thickness, t, t
				);
		
		quadRotOz90SymOz(
				t, t, thickness, 
				t, t, -thickness,
				t, size2, -thickness, 
				t, size2, thickness
				);
				
		quadRotOz90SymOz(
				-t, t, thickness, 
				-t, size2, thickness,
				-t, size2, -thickness, 
				-t, t, -thickness
				);
		
		
		quadRotOz90SymOz(
				thickness, size2+t-thickness, -thickness,
				-thickness, size2+t-thickness, -thickness,
				-thickness, size2+t-thickness, thickness,
				thickness, size2+t-thickness, thickness
				);	

		quadSymxOyRotOz90SymOz(
				thickness, -thickness, size2+t-thickness, 
				thickness, thickness, size2+t-thickness,
				-thickness, thickness, size2+t-thickness,
				-thickness, -thickness, size2+t-thickness
				);	
		
		
		//black parts
		color(0,0,0);
		
		quadSymxOyRotOz90SymOz(
				t, t, t, 
				t, t, size2,
				t, thickness, size2,
				t, thickness, t
				);		
		
		quadSymxOyRotOz90SymOz(
				thickness, t, t, 
				thickness, t, size2,
				t, t, size2,
				t, t, t
		);

		
		quadSymxOyRotOz90SymOz(
				t, t, t, 
				t, t, thickness,
				t, size2, thickness, 
				t, size2, t
				);

		quadSymxOyRotOz90SymOz(
				thickness, t, t, 
				t, t, t,
				t, size2, t, 
				thickness, size2, t
				);
		

		
		quadSymxOyRotOz90SymOz(
				-t, t, t, 
				-t, size2, t,
				-t, size2, thickness, 
				-t, t, thickness
				);

		quadSymxOyRotOz90SymOz(
				-thickness, t, t, 
				-thickness, size2, t,
				-t, size2, t, 
				-t, t, t
				);

	
		
		quadSymxOyRotOz90SymOz(
				t, size2, t, 
				t, size2+t-thickness, t,
				-t, size2+t-thickness, t,
				-t, size2, t
				);	
		
		quadSymxOyRotOz90SymOz(
				t, size2+t-thickness, t, 
				t, size2+t-thickness, thickness,
				-t, size2+t-thickness, thickness,
				-t, size2+t-thickness, t
				);	
		

	
		
		quadRotOz90SymOz(
				t, size2, t, 
				t, size2, -t,
				t, size2+t-thickness, -t,
				t, size2+t-thickness, t
				);	

		quadRotOz90SymOz(
				t, size2+t-thickness, thickness, 
				t, size2+t-thickness, -thickness,
				thickness, size2+t-thickness, -thickness,
				thickness, size2+t-thickness, thickness
				);
		


		
		quadRotOz90SymOz(
				-t, size2, t, 
				-t, size2+t-thickness, t,
				-t, size2+t-thickness, -t,
				-t, size2, -t
				);	

		quadRotOz90SymOz(
				-t, size2+t-thickness, thickness, 
				-thickness, size2+t-thickness, thickness,
				-thickness, size2+t-thickness, -thickness,
				-t, size2+t-thickness, -thickness
				);	
		


		
		quadSymxOyRotOz90SymOz(
				t, t, size2,
				t, t, size2+t-thickness,
				t, -t, size2+t-thickness,
				t, -t, size2
				);	

		quadSymxOyRotOz90SymOz(
				t, t, size2+t-thickness,
				thickness, thickness, size2+t-thickness,
				thickness, -thickness, size2+t-thickness,
				t, -t, size2+t-thickness
				);	
		
	
	}
	
	

	
	
	private void cursorDiamond(){
		
    	float t1 = 0.15f;
    	float t2 = 1f-2*t1;
    	
    	//black parts
		color(0,0,0);
    	
    	quadSymxOyRotOz90SymOz(1f, 0f, 0f,	        
    			t2, t1, t1,	        
    			t1, t1, t2,
    			0f, 0f, 1f);
    	
    	
    	quadSymxOyRotOz90SymOz(0f, 0f, 1f,
    			t1, t1, t2,
    			t1, t2, t1,	
    			0f, 1f, 0f);	

    	quadSymxOyRotOz90SymOz(0f, 1f, 0f,	
    			t1, t2, t1,	
    			t2, t1, t1,	        
    			1f, 0f, 0f);
    	
		//white parts
		color(1,1,1);
		
		quadSymxOyRotOz90SymOz(
				t2, t1, t1,
				t2, t1, t1,	
				t1, t2, t1,	
				t1, t1, t2);

		
	}

	
	
	
	private void cursorCylinder(){
		
		int latitude = 8;
		float x1 = 4f;
		float r1 = LINE3D_THICKNESS;
		float r2 = (float) (r1*Math.sqrt(2));
		float x2 = x1/3;
		
    	float da = (float) (Math.PI/latitude) ; 

    	float y1;
    	float z1;
    	float y0,z0;

    	
    	//white parts
		color(1,1,1);
		  	
		//ring
    	y1 = 2 * r2 * (float) Math.sin ( da ); 
		z1 = 2 * r2 * (float) Math.cos ( da );
		
    	for( int i = 1; i <= latitude  ; i++ ) { 
    		y0 = y1; 
    		z0 = z1; 
    		y1 = 2 * r2 * (float) Math.sin ( (2*i+1) * da ); 
    		z1 = 2 * r2 * (float) Math.cos ( (2*i+1) * da ); 

    		vertex(-x2,y0,z0); 
    		vertex(x2,y0,z0); 
    		vertex(x2,y1,z1); 
    		vertex(-x2,y1,z1); 


    	} 
    	
    	//caps
    	y1 = 2 * r1 * (float) Math.sin ( da ); 
		z1 = 2 * r1 * (float) Math.cos ( da );
		
    	for( int i = 1; i < latitude/2  ; i++ ) { 
    		y0 = y1; 
    		z0 = z1; 
    		y1 = 2 * r1 * (float) Math.sin ( (2*i+1) * da ); 
    		z1 = 2 * r1 * (float) Math.cos ( (2*i+1) * da ); 

    		quadSymOz(
    				x1,y0,z0, 
    				x1,-y0,z0, 
    				x1,-y1,z1, 
    				x1,y1,z1); 

    	} 


    	//black parts
		color(0,0,0);
		
		//ring
    	y1 = 2 * (float) Math.sin ( da ); 
		z1 = 2 * (float) Math.cos ( da );
		
    	for( int i = 1; i <= latitude  ; i++ ) { 
    		y0 = y1; 
    		z0 = z1; 
    		y1 = 2 * (float) Math.sin ( (2*i+1) * da ); 
    		z1 = 2 * (float) Math.cos ( (2*i+1) * da ); 

    		quadSymOz(x2,y0*r2,z0*r2,
    				x1,y0*r1,z0*r1, 
    				x1,y1*r1,z1*r1, 
    				x2,y1*r2,z1*r2); 


    	} 
    	
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void quadSymxOyRotOz90SymOz(float x1, float y1, float z1, 
			float x2, float y2, float z2,
			float x3, float y3, float z3,
			float x4, float y4, float z4){	
		
		quadRotOz90SymOz(
				x1, y1, z1, 
				x2, y2, z2, 
				x3, y3, z3, 
				x4, y4, z4
				);
		
		quadRotOz90SymOz(
				x1, y1, -z1, 
				x4, y4, -z4, 
				x3, y3, -z3, 
				x2, y2, -z2
				);
		
	}
	
	private void quadSymxOzSymxOyRotOz90SymOz(float x1, float y1, float z1, 
			float x2, float y2, float z2,
			float x3, float y3, float z3,
			float x4, float y4, float z4){	
		
		quadSymxOyRotOz90SymOz(
				x1, y1, z1, 
				x2, y2, z2, 
				x3, y3, z3, 
				x4, y4, z4
				);
		
		quadSymxOyRotOz90SymOz(
				x1, -y1, z1, 
				x4, -y4, z4, 
				x3, -y3, z3, 
				x2, -y2, z2
				);
		
	}
	private void quadRotOz90SymOz(float x1, float y1, float z1, 
			float x2, float y2, float z2,
			float x3, float y3, float z3,
			float x4, float y4, float z4){	
		
		quadSymOz(
				x1, y1, z1, 
				x2, y2, z2, 
				x3, y3, z3, 
				x4, y4, z4
				);
		
		quadSymOz(
				-y1, x1, z1, 
				-y2, x2, z2,
				-y3, x3, z3, 
				-y4, x4, z4
		);
		
		
		
	}
	
	private void quadSymOz(float x1, float y1, float z1, 
			float x2, float y2, float z2,
			float x3, float y3, float z3,
			float x4, float y4, float z4){
		
		vertex(x1,y1,z1);
		vertex(x2,y2,z2);
		vertex(x3,y3,z3);
		vertex(x4,y4,z4);
		
		vertex(-x1,-y1,z1);
		vertex(-x2,-y2,z2);
		vertex(-x3,-y3,z3);
		vertex(-x4,-y4,z4);
		
		/*
		vertex(-x1,y1,z1);
		vertex(-x4,y4,z4);
		vertex(-x3,y3,z3);
		vertex(-x2,y2,z2);
		*/
		
	}


	
}
