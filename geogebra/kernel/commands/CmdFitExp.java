package geogebra.kernel.commands; 
/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
import geogebra.MyError;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;

/** 
 * FitExp[<List of points>]
 * 
 * @author Hans-Petter Ulven
 * @version 06.04.08
 */
public class CmdFitExp extends CommandProcessor{

    public CmdFitExp(Kernel kernel) {super(kernel);}
    
    public GeoElement[] process(Command c) throws MyError {
        int n=c.getArgumentNumber();
        GeoElement[] arg;
        switch(n) {
            case 1: arg=resArgs(c);
                    if( (arg[0].isGeoList()) ){ 
                        GeoElement[] ret={kernel.FitExp(c.getLabel(),(GeoList)arg[0])};
                        return ret;
                    }else{
                        throw argErr(app,c.getName(),arg[0]);
                    }//if arg[0] is GeoList 

           default: throw argNumErr(app,c.getName(),n);
        }//switch(number of arguments)
    }//process(Command) 
}// class CmdFitExp
