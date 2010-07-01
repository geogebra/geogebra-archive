package geogebra.euclidian;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.commands.AlgebraProcessor;

import org.scilab.forge.jlatexmath.dynamic.ExternalConverter;

public class LatexConvertor implements ExternalConverter {

	   AlgebraProcessor env;
	   Construction cons;

	   public LatexConvertor(AlgebraProcessor env, Construction cons) {
	       this.env = env;
	       this.cons = cons;
	   }

	   public String getLaTeXString(String externalCode) {
		   cons.setSuppressLabelCreation(true);
		   GeoElement[] geos;
		   try {
			   geos = env.processAlgebraCommandNoExceptionHandling(externalCode, false);
		   }
		   catch (Exception e) {
			   e.printStackTrace();
			   geos = null;
		   }
		   cons.setSuppressLabelCreation(false);
		   if (geos != null) {
			   GeoElement geo = geos[0];
		       return geo.getLaTeXdescription();
		   }
		   return null;
	   }
	}

