package geogebra3D.kernel3D.commands;


import geogebra.kernel.commands.CommandDispatcher;
import geogebra3D.kernel3D.Kernel3D;

public class CommandDispatcher3D extends CommandDispatcher {
	
	Kernel3D kernel3D;

	public CommandDispatcher3D(Kernel3D kernel3D) {
		super(kernel3D);
		this.kernel3D = kernel3D;
		//Application.debug("CommandDispatcher3D");
	}

	protected void initCmdTable() {  
		super.initCmdTable();
		//Application.debug("CommandDispatcher3D.initCmdTable()");
		cmdTable.put("Segment", new CmdSegment3D(kernel3D));
		cmdTable.put("Line", new CmdLine3D(kernel3D));
		cmdTable.put("Ray", new CmdRay3D(kernel3D));
		cmdTable.put("Vector", new CmdVector3D(kernel3D));
		cmdTable.put("Polygon", new CmdPolygon3D(kernel3D));
		cmdTable.put("Point", new CmdPoint3D(kernel3D));
		cmdTable.put("Circle", new CmdCircle3D(kernel3D));
		cmdTable.put("Plane", new CmdPlane(kernel3D));
		cmdTable.put("Pyramid", new CmdPyramid(kernel3D));
		cmdTable.put("Polyhedron", new CmdPolyhedron(kernel3D));
		
		cmdTable.put("PointIn", new CmdPointIn3D(kernel3D));   
		  
    	cmdTable.put("Intersect", new CmdIntersect3D(kernel3D));	
    	
	}
	
	
	
}
