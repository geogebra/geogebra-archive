package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra3D.Matrix.Ggb3DMatrix4x4;



public class GeoQuadric extends GeoElement3D {
	
	Ggb3DMatrix4x4 matrix = new Ggb3DMatrix4x4();
	

	public GeoQuadric(Construction c) {
		super(c);
		
		//TODO specific alpha
		setAlphaValue(ConstructionDefaults3D.DEFAULT_QUADRIC_ALPHA);

	}
	
	
	
	
	
	////////////////////////////////
	// SPHERE
	
	
	public void setSphere(GeoPoint3D o, double r){
		
		//TODO in Ggb3DMatrix : setTranslation, setDilatation
		matrix.set(o.getInhomCoords(), 4);
		matrix.set(4,4,1);
		for (int i=1;i<=3;i++)
			matrix.set(i,i,r);
		
		setDrawingMatrix(matrix);
		
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	

	///////////////////////////////
	// GeoElement
	
	
    public GeoElement copy() {

        return null;

    }

    public int getGeoClassType() {

        return GEO_CLASS_QUADRIC;

    }

    protected String getTypeString() {

        return "Quadric";

    }

    public boolean isDefined() {

        return true;

    }

    public boolean isEqual(GeoElement Geo) {

        return false;

    }

    public void set(GeoElement geo) {

    }

    public void setUndefined() {

    }

    public boolean showInAlgebraView() {

        return true;

    }

    protected boolean showInEuclidianView() {

        return true;

    }

    public String toValueString() {

        return "todo";

    }

    protected String getClassName() {

        return "GeoQuadric";

    }


    
	/** to be able to fill it with an alpha value */
	public boolean isFillable() {
		return true;
	}


}
