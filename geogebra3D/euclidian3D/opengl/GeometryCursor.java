package geogebra3D.euclidian3D.opengl;

import geogebra.main.Application;

import javax.media.opengl.GL;





/**
 * Class that describes the geometry of the 3D cursor
 * 
 * @author ggb3D
 *
 */
public class GeometryCursor extends Geometry {
	

	static private float SIZE = 0.12f;
	static private float THICKNESS = 0.0125f;
	static private float THICKNESS2 = 0.0125f;
	static private float DEPTH = 0.01f;

	/** common constructor
	 * @param geometryRenderer
	 */
	public GeometryCursor(GeometryRenderer geometryRenderer) {
		super(geometryRenderer,NORMAL_OFF,TEXTURE_OFF,COLOR_ON);
	}

	public void init() {
		
		geometryRenderer.preInit(this);
		
		int index = 0;
		
		geometryRenderer.startGeometry(this, index);
		cursorCross2D();
		geometryRenderer.endGeometry(this);
		
		index++;
		geometryRenderer.startGeometry(this, index);
		cursorDiamond();
		geometryRenderer.endGeometry(this);

	}
	
	
	public int getType(){
		return GL.GL_QUADS;
	}
	
	public int getNb(){
		return 2;
	}
	
	
	//////////////////////////////////
	// GEOMETRIES
	//////////////////////////////////
	
	private void cursorCross2D(){
		
		//white parts
		color(1,1,1);

		//up
		vertex(THICKNESS, SIZE, DEPTH);
		vertex(-THICKNESS, SIZE, DEPTH);
		vertex(-THICKNESS, -SIZE, DEPTH);
		vertex(THICKNESS, -SIZE, DEPTH);
				
		vertex(SIZE, THICKNESS, DEPTH);
		vertex(THICKNESS, THICKNESS, DEPTH);
		vertex(THICKNESS, -THICKNESS, DEPTH);
		vertex(SIZE, -THICKNESS, DEPTH);
		
		vertex(-SIZE, THICKNESS, DEPTH);
		vertex(-SIZE, -THICKNESS, DEPTH);
		vertex(-THICKNESS, -THICKNESS, DEPTH);
		vertex(-THICKNESS, THICKNESS, DEPTH);
		
		//down
		vertex(THICKNESS, SIZE, -DEPTH);
		vertex(THICKNESS, -SIZE, -DEPTH);
		vertex(-THICKNESS, -SIZE, -DEPTH);
		vertex(-THICKNESS, SIZE, -DEPTH);
				
		vertex(SIZE, THICKNESS, -DEPTH);
		vertex(SIZE, -THICKNESS, -DEPTH);
		vertex(THICKNESS, -THICKNESS, -DEPTH);
		vertex(THICKNESS, THICKNESS, -DEPTH);
		
		vertex(-SIZE, THICKNESS, -DEPTH);
		vertex(-THICKNESS, THICKNESS, -DEPTH);
		vertex(-THICKNESS, -THICKNESS, -DEPTH);
		vertex(-SIZE, -THICKNESS, -DEPTH);
		
		
		//black parts
		color(0,0,0);

		//up and down
		quadSymxOyRotOz90SymOz(
				THICKNESS, THICKNESS, DEPTH,
				THICKNESS+THICKNESS2, THICKNESS+THICKNESS2, DEPTH,
				THICKNESS+THICKNESS2, SIZE+THICKNESS2, DEPTH,
				THICKNESS, SIZE, DEPTH
		);
		
		quadSymxOyRotOz90SymOz(
				THICKNESS, -THICKNESS, DEPTH,
				THICKNESS, -SIZE, DEPTH,
				THICKNESS+THICKNESS2, -SIZE-THICKNESS2, DEPTH,
				THICKNESS+THICKNESS2, -THICKNESS-THICKNESS2, DEPTH
		);
		
		quadSymxOyRotOz90SymOz(
				SIZE, THICKNESS,DEPTH,
				SIZE, -THICKNESS,DEPTH,
				SIZE+THICKNESS2, -THICKNESS-THICKNESS2,  DEPTH,
				SIZE+THICKNESS2, THICKNESS+THICKNESS2,  DEPTH
		);
		
		
		//edges
		quadSymxOyRotOz90SymOz(
				THICKNESS+THICKNESS2, THICKNESS+THICKNESS2, -DEPTH,
				THICKNESS+THICKNESS2, SIZE+THICKNESS2, -DEPTH,
				THICKNESS+THICKNESS2, SIZE+THICKNESS2, DEPTH,
				THICKNESS+THICKNESS2, THICKNESS+THICKNESS2, DEPTH
		);
		
		quadSymxOyRotOz90SymOz(
				THICKNESS+THICKNESS2, -THICKNESS-THICKNESS2, -DEPTH,
				THICKNESS+THICKNESS2, -THICKNESS-THICKNESS2, DEPTH,
				THICKNESS+THICKNESS2, -SIZE-THICKNESS2, DEPTH,
				THICKNESS+THICKNESS2, -SIZE-THICKNESS2, -DEPTH
		);
		
		quadRotOz90SymOz(
				SIZE+THICKNESS2, THICKNESS+THICKNESS2,  -DEPTH,
				SIZE+THICKNESS2, THICKNESS+THICKNESS2,  DEPTH,
				SIZE+THICKNESS2, -THICKNESS-THICKNESS2,  DEPTH,
				SIZE+THICKNESS2, -THICKNESS-THICKNESS2,  -DEPTH
		);	
		
		
	}
	
	
	
	
	
	
	private void cursorDiamond(){
		
		
		
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
				y1, x1, z1, 
				y4, x4, z4,
				y3, x3, z3, 
				y2, x2, z2 
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
