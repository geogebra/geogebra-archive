package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.commands.CmdIntersect;
import geogebra.kernel.kernelND.GeoCoordSys;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.main.Application;
import geogebra.main.MyError;
import geogebra3D.kernel3D.Kernel3D;


/*
 * Intersect[ <GeoLine3D>, <GeoLine3D> ] 
 */
public class CmdIntersect3D extends CmdIntersect {
	
	
	
	public CmdIntersect3D(Kernel kernel) {
		super(kernel);
		
		
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
            				kernel.getManager3D().Intersect(
                            c.getLabel(),
                            (GeoElement) arg[0],
                            (GeoElement) arg[1])};
            		return ret;
            	}

            	GeoElement[] ret =
                    {
            			kernel.getManager3D().Intersect(
                            c.getLabel(),
                            (GeoElement) arg[0],
                            (GeoElement) arg[1])};
                return ret;
            }

            else {

            	return super.process(c);

            	/*
                if (!ok[0])
                    throw argErr(app, "Intersect", arg[0]);
                else
                    throw argErr(app, "Intersect", arg[1]);
            	 */
            }



        default :
            return super.process(c);
        	//throw argNumErr(app, "Intersect", n);
    }
}
}