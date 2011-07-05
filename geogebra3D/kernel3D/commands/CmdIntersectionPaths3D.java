package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;

import geogebra.kernel.GeoPolygon;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;

import geogebra.kernel.commands.CmdIntersectionPaths;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPlaneND;
import geogebra.kernel.kernelND.GeoQuadricND;
import geogebra.main.MyError;
import geogebra3D.kernel3D.GeoPlane3D;

public class CmdIntersectionPaths3D extends CmdIntersectionPaths {
	
	public CmdIntersectionPaths3D(Kernel kernel) {
		super(kernel);
	}
	
public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
            arg = resArgs(c);
            
            if (!arg[0].isGeoElement3D() && !arg[1].isGeoElement3D())
            	return super.process(c);
            else {
            	 // Line - Polygon(as region) in 2D/3D
                if ((ok[0] = (arg[0] .isGeoLine()))
                		&& (ok[1] = (arg[1] .isGeoPolygon()))) {
                    GeoElement[] ret =
                    		kernel.getManager3D().IntersectionSegment(
                                c.getLabels(),
                                (GeoLineND) arg[0],
                                (GeoPolygon) arg[1]);
                    return ret;
                } else if ((ok[0] = (arg[0] .isGeoPolygon()))
                		&& (ok[1] = (arg[1] .isGeoLine()))) {
                    GeoElement[] ret =
                		kernel.getManager3D().IntersectionSegment(
                            c.getLabels(),
                            (GeoLineND) arg[1],
                            (GeoPolygon) arg[0]);
                return ret;
                }
            	// Plane - Polygon(as region)
                if (
                        (ok[0] = (arg[0] .isGeoPlane()))
                            && (ok[1] = (arg[1] .isGeoPolygon())))
        				return kernel.getManager3D().IntersectionSegment(
                            c.getLabels(),
                            (GeoPlane3D) arg[0],
                            (GeoPolygon) arg[1]);
        			else if (
                            (ok[0] = (arg[1] .isGeoPlane()))
                            && (ok[1] = (arg[0] .isGeoPolygon())))
        				return kernel.getManager3D().IntersectionSegment(
                            c.getLabels(),
                            (GeoPlane3D) arg[1],
                            (GeoPolygon) arg[0]);
        		//intersection plane/plane
            		else if (arg[0] instanceof GeoPlaneND && arg[1] instanceof GeoPlaneND){

            			GeoElement[] ret =
            			{
            					kernel.getManager3D().Intersect(
            							c.getLabel(),
            							(GeoElement) arg[0],
            							(GeoElement) arg[1])};
            			return ret;

            		}
                
        		//intersection plane/quadric
            		else if ((arg[0] instanceof GeoPlaneND) && (arg[1] instanceof GeoQuadricND)){
            			GeoElement[] ret =
            			{
            					kernel.getManager3D().Intersect(
            							c.getLabel(),
            							(GeoPlaneND) arg[0],
            							(GeoQuadricND) arg[1])};
            			return ret;
            		}else if ((arg[0] instanceof GeoQuadricND) && (arg[1] instanceof GeoPlaneND)){
            			GeoElement[] ret =
            			{
            					kernel.getManager3D().Intersect(
            							c.getLabel(),
            							(GeoPlaneND) arg[1],
            							(GeoQuadricND) arg[0])};
            			return ret;
            		}
                return super.process(c);
            }

        default :
        	return super.process(c);
    }
}
}