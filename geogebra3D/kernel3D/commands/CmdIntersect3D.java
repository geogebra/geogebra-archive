package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.GeoSurfaceFinite;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.commands.CmdIntersect;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPlaneND;
import geogebra.kernel.kernelND.GeoQuadricND;
import geogebra.main.MyError;
import geogebra3D.kernel3D.GeoPlane3D;


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

        	if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D() ){
        		
        		//POINTS
        		
        		//intersection outlined surface/line : points on outline (boundary)
        		/*
        		if (arg[0] instanceof GeoLineND && arg[1] instanceof GeoSurfaceFinite && ((GeoPolygon) arg[1]).asBoundary()){
        			return kernel.getManager3D().IntersectOutline(
        					c.getLabels(),
        					(GeoLineND) arg[0],
        					(GeoPolygon) arg[1]);
        			// else: will be done in line/surface
        		}else if (arg[0] instanceof GeoSurfaceFinite && arg[1] instanceof GeoLineND && ((GeoPolygon) arg[0]).asBoundary()){
        			return kernel.getManager3D().IntersectOutline(
        					c.getLabels(),
        					(GeoLineND) arg[1],
        					(GeoPolygon) arg[0]);
        		}else 
        			*/
        		
        		//intersection line/conic
        		if (
        				(arg[0] instanceof GeoLineND)
        				&& (arg[1] instanceof GeoConicND))
        			return (GeoElement[]) kernel.getManager3D().IntersectLineConic(
        					c.getLabels(),
        					(GeoLineND) arg[0],
        					(GeoConicND) arg[1]);
        		else if (
        				(arg[0] instanceof GeoConicND)
        				&& (arg[1] instanceof GeoLineND))
        			return (GeoElement[]) kernel.getManager3D().IntersectLineConic(
        					c.getLabels(),
        					(GeoLineND) arg[1],
        					(GeoConicND) arg[0]);

        		//intersection conic/conic
        		else if (
        				(arg[0] instanceof GeoConicND)
        				&& (arg[1] instanceof GeoConicND))
        			return (GeoElement[]) kernel.getManager3D().IntersectConics(
        					c.getLabels(),
        					(GeoConicND) arg[0],
        					(GeoConicND) arg[1]);
        		

        		//intersection line/polygon
        		
        		else if ((arg[0] instanceof GeoLineND && arg[1] instanceof GeoPolygon)
        				||(arg[1] instanceof GeoLineND && arg[0] instanceof GeoPolygon))
        			
        			return kernel.getManager3D().IntersectionPoint(
        							c.getLabels(),
        							(GeoLineND) arg[0],
        							(GeoPolygon) arg[1]);
        		
        		 
        		//intersection line/planar objects : only if surface is treated as its outline
        		else if ((arg[0] instanceof GeoLineND && arg[1] instanceof GeoCoordSys2D)
        				||(arg[1] instanceof GeoLineND && arg[0] instanceof GeoCoordSys2D)){
        			
        			GeoElement[] ret =
        			{
        					kernel.getManager3D().Intersect(
        							c.getLabel(),
        							(GeoElement) arg[0],
        							(GeoElement) arg[1])};
        			return ret;


        		//intersection line/line
        		}else if (arg[0] instanceof GeoLineND && arg[1] instanceof GeoLineND){

        			GeoElement[] ret =
        			{
        					kernel.getManager3D().Intersect(
        							c.getLabel(),
        							(GeoElement) arg[0],
        							(GeoElement) arg[1])};
        			return ret;

        		}
        		
        		//PLANES
        		
        		//intersection plane/polygon
   
        		else if (arg[0] instanceof GeoPlane3D && arg[1] instanceof GeoSurfaceFinite ||
        				arg[1] instanceof GeoPlane3D && arg[0] instanceof GeoSurfaceFinite ){

        			GeoElement[] ret =
        					kernel.getManager3D().IntersectionSegment(
        							c.getLabels(),
        							(GeoPlane3D) arg[0],
        							(GeoSurfaceFinite) arg[1]);
        			return ret;
        		}
        		
        		
        		//intersection plane/plane
        		else if (arg[0] instanceof GeoPlaneND && arg[1] instanceof GeoPlaneND){

        			GeoElement[] ret =
        			{
        					kernel.getManager3D().Intersect(
        							c.getLabel(),
        							(GeoElement) arg[0],
        							(GeoElement) arg[1])};
        			return ret;
        			
        		//intersection plane/quadric
        		}else if ((arg[0] instanceof GeoPlaneND) && (arg[1] instanceof GeoQuadricND)){
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

        	}
        	
        	


        	return super.process(c);

     	 case 3 :
     		arg = resArgs(c);
     		if ((arg[0].isGeoElement3D())||(arg[1].isGeoElement3D())||(arg[2].isGeoElement3D())){

                // Line - Conic
                if ((arg[0] .isGeoLine())
                    && arg[1] .isGeoConic()
                    && arg[2] .isNumberValue()) {
                    GeoElement[] ret =
                        {
                             kernel.getManager3D().IntersectLineConicSingle(
                                c.getLabel(),
                                (GeoLineND) arg[0],
                                (GeoConicND) arg[1],
                                (NumberValue) arg[2])};
                    return ret;
                } else if ((arg[1] .isGeoLine())
                        && arg[0] .isGeoConic()
                        && arg[2] .isNumberValue()) {
                        GeoElement[] ret =
                            {
                                 kernel.getManager3D().IntersectLineConicSingle(
                                    c.getLabel(),
                                    (GeoLineND) arg[1],
                                    (GeoConicND) arg[0],
                                    (NumberValue) arg[2])};
                        return ret;
                }
                	/*
                	else if ((arg[0] instanceof GeoQuadricND) && (arg[1] instanceof GeoPointND) && (arg[2] instanceof GeoPointND)){
            			GeoElement[] ret =
            			{
            					kernel.getManager3D().Intersect(
            							c.getLabel(),
            							(GeoQuadricND) arg[0],
            							(GeoPointND) arg[1],
            							(GeoPointND) arg[2])};
            			return ret;
            		} 
                	
                	*/

     		}
        	

        default :
            return super.process(c);
        	//throw argNumErr(app, "Intersect", n);
    }
}
}