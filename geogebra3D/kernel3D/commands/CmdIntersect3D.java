package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.commands.CmdIntersect;
import geogebra.main.Application;
import geogebra.main.MyError;
import geogebra3D.kernel3D.GeoCoordSys;
import geogebra3D.kernel3D.GeoCoordSys2D;
import geogebra3D.kernel3D.Kernel3D;


/*
 * Intersect[ <GeoLine3D>, <GeoLine3D> ] 
 */
public class CmdIntersect3D extends CmdIntersect {
	
	Kernel3D kernel3D;
	
	
	public CmdIntersect3D(Kernel3D kernel3D) {
		super( (Kernel) kernel3D);
		this.kernel3D = kernel3D;
		
		
	}	
	
	
public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
            arg = resArgs(c);
            // GeoCoordSys - GeoCoordSys
            if ((ok[0] = (arg[0] instanceof GeoCoordSys))
                && (ok[1] = (arg[1] instanceof GeoCoordSys))) {
            	if ((arg[0] instanceof GeoCoordSys2D) && (arg[1] instanceof GeoCoordSys2D)){
            		GeoElement[]ret =
                    {
                         kernel3D.Intersect(
                            c.getLabel(),
                            (GeoCoordSys2D) arg[0],
                            (GeoCoordSys2D) arg[1])};
            		return ret;
            	}

            	GeoElement[] ret =
                    {
                         kernel3D.Intersect(
                            c.getLabel(),
                            (GeoCoordSys) arg[0],
                            (GeoCoordSys) arg[1])};
                return ret;
            }
            
			else {
                if (!ok[0])
                    throw argErr(app, "Intersect", arg[0]);
                else
                    throw argErr(app, "Intersect", arg[1]);
            }



        default :
            throw argNumErr(app, "Intersect", n);
    }
}
}