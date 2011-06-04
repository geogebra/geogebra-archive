package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.commands.CmdIntersect;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra.kernel.kernelND.GeoCoordSys;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPlaneND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoQuadricND;
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

        	if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D() ){
        		
        		//POINTS
        		
        		//intersection outlined surface/line : points on outline
        		/*
        		if (arg[0] instanceof GeoLineND && arg[1] instanceof GeoPolygon){
        			return kernel.getManager3D().IntersectOutline(
        					c.getLabels(),
        					(GeoLineND) arg[0],
        					(GeoPolygon) arg[1]);
        		}else if (arg[0] instanceof GeoPolygon && arg[1] instanceof GeoLineND){
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
        		
        		

        		//intersection line/surface : only if surface has no outline
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

        			
        		//LINES

        		//intersection plane/plane
        		}else if (arg[0] instanceof GeoPlaneND && arg[1] instanceof GeoPlaneND){

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

            	

        	/*
        case 3 :
        	arg = resArgs(c);
        	if ((arg[0] instanceof GeoQuadricND) && (arg[1] instanceof GeoPointND) && (arg[2] instanceof GeoPointND)){
    			GeoElement[] ret =
    			{
    					kernel.getManager3D().Intersect(
    							c.getLabel(),
    							(GeoQuadricND) arg[0],
    							(GeoPointND) arg[1],
    							(GeoPointND) arg[2])};
    			return ret;
    		} 
        	
        	return super.process(c);
        	*/

        default :
            return super.process(c);
        	//throw argNumErr(app, "Intersect", n);
    }
}
}