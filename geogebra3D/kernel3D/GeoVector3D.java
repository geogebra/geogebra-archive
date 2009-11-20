package geogebra3D.kernel3D;

import geogebra.kernel.CircularDefinitionException;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPointInterface;
import geogebra.kernel.GeoVectorInterface;
import geogebra.kernel.Kernel;
import geogebra.kernel.Locateable;
import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.Matrix.Ggb3DMatrix4x4;


/**
 * 3D vector class
 * @author ggb3D
 *
 */
public class GeoVector3D extends GeoVec4D
implements GeoVectorInterface, Locateable{

	/** simple constructor
	 * @param c
	 */
	public GeoVector3D(Construction c) {
		super(c);
	}

	/** simple constructor with (x,y,z) coords
	 * @param c
	 * @param x
	 * @param y
	 * @param z
	 */
	public GeoVector3D(Construction c, double x, double y, double z) {
		super(c,x,y,z,0);
	}
	
	
	public void setCoords(double[] vals){
		super.setCoords(vals);
		
		//sets the drawing matrix 
		Ggb3DMatrix matrix = new Ggb3DMatrix(4,2);
		matrix.set(getCoords(), 1);
		
		//TODO use start point
		matrix.set(4, 2, 1.0);
		
		setDrawingMatrix(new Ggb3DMatrix4x4(matrix));
		
	}




	public GeoElement copy() {
		// TODO Auto-generated method stub
		return null;
	}


	public int getGeoClassType() {
		return GEO_CLASS_VECTOR3D;		
	}


	protected String getTypeString() {
		return "Vector3D";
	}


	public boolean isDefined() {
		return true;
	}


	public boolean isEqual(GeoElement Geo) {
		// TODO Auto-generated method stub
		return false;
	}


	public void set(GeoElement geo) {
		// TODO Auto-generated method stub

	}


	public void setUndefined() {
		// TODO Auto-generated method stub

	}


	public boolean showInAlgebraView() {
		// TODO Auto-generated method stub
		return true;
	}


	protected boolean showInEuclidianView() {
		// TODO Auto-generated method stub
		return true;
	}



	
	protected String getClassName() {
		return "GeoVector3D";
	}

	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}

	
	
	// for properties panel
	public boolean isPath(){
		return true;
	}
	
	
	

	///////////////////////////////////////////////
	// TO STRING
	///////////////////////////////////////////////


	final public String toString() {            
		sbToString.setLength(0);
		sbToString.append(label);

		switch (kernel.getCoordStyle()) {
		case Kernel.COORD_STYLE_FRENCH:
			// no equal sign
			sbToString.append(": ");

		case Kernel.COORD_STYLE_AUSTRIAN:
			// no equal sign
			break;

		default: 
			sbToString.append(" = ");
		}

		sbToString.append(buildValueString());
		return sbToString.toString();
	}
	
	private StringBuilder sbToString = new StringBuilder(50); 

	final public String toValueString() {
		return buildValueString().toString();
	}

	private StringBuilder buildValueString() {
		sbBuildValueString.setLength(0);
		
		/*
		switch (toStringMode) {

		
		case Kernel.COORD_POLAR:                	
			sbBuildValueString.append("(");		
		sbBuildValueString.append(kernel.format(GeoVec2D.length(x, y)));
		sbBuildValueString.append("; ");
		sbBuildValueString.append(kernel.formatAngle(Math.atan2(y, x)));
		sbBuildValueString.append(")");
			break;

		case Kernel.COORD_COMPLEX:              	
			sbBuildValueString.append(kernel.format(x));
			sbBuildValueString.append(" ");
			sbBuildValueString.append(kernel.formatSigned(y));
			sbBuildValueString.append("i");
            break;                                

			default: // CARTESIAN
				sbBuildValueString.append("(");		
			sbBuildValueString.append(kernel.format(x));
			switch (kernel.getCoordStyle()) {
				case Kernel.COORD_STYLE_AUSTRIAN:
					sbBuildValueString.append(" | ");
					break;

				default:
					sbBuildValueString.append(", ");												
			}
			sbBuildValueString.append(kernel.format(y));
			sbBuildValueString.append(")");
				break;       
		}
		 */

		sbBuildValueString.append("(");		
		sbBuildValueString.append(kernel.format(getX()));
		setCoordSep();
		sbBuildValueString.append(kernel.format(getY()));
		setCoordSep();
		sbBuildValueString.append(kernel.format(getZ()));
		sbBuildValueString.append(")");


		return sbBuildValueString;
		}


		private void setCoordSep(){
			switch (kernel.getCoordStyle()) {
			case Kernel.COORD_STYLE_AUSTRIAN:
				sbBuildValueString.append(" | ");
				break;

			default:
				sbBuildValueString.append(", ");	
			}
		}

		private StringBuilder sbBuildValueString = new StringBuilder(50);
		
		
		
		
		
		///////////////////////////////////////////////
		// LOCATEABLE INTERFACE
		///////////////////////////////////////////////

		

		public GeoPointInterface getStartPoint() {
			// TODO Auto-generated method stub
			return null;
		}

		public void setStartPoint(GeoPointInterface p)
				throws CircularDefinitionException {
			// TODO Auto-generated method stub
			
		}

		public GeoPointInterface[] getStartPoints() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean hasAbsoluteLocation() {
			// TODO Auto-generated method stub
			return false;
		}

		public void initStartPoint(GeoPointInterface p, int number) {
			// TODO Auto-generated method stub
			
		}

		public boolean isAlwaysFixed() {
			// TODO Auto-generated method stub
			return false;
		}

		public void removeStartPoint(GeoPointInterface p) {
			// TODO Auto-generated method stub
			
		}

		public void setStartPoint(GeoPointInterface p, int number)
				throws CircularDefinitionException {
			// TODO Auto-generated method stub
			
		}

		public void setWaitForStartPoint() {
			// TODO Auto-generated method stub
			
		} 


		
		
		



}
