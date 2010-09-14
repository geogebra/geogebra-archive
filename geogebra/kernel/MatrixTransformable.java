package geogebra.kernel;


public interface MatrixTransformable {
	
	public void matrixTransform(double a00,double a01,double a10,double a11);
	public GeoElement toGeoElement();

}
