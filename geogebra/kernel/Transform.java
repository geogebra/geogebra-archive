package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;

/**
 * Container for transforms
 * 
 * @author kondr
 * 
 */
public abstract class Transform {

	public static String transformedGeoLabel(GeoElement geo) {
			                if (geo.isLabelSet() && !geo.hasIndexLabel() && !geo.label.endsWith("'''")) {
			                        return geo.label + "'";
		} else {
			                        return null;
		}
		}
	/**
	 * Apply the transform to given element and set label for result 
	 * 
	 * @param geo
	 * @return transformed geo
	 */
	protected abstract GeoElement doTransform(GeoElement geo);

	/** construction */
	protected Construction cons;

	/**
	 * @param label
	 * @param poly
	 * @return transformed polygon
	 */
	final public GeoElement[] transformPoly(String label, GeoPolygon poly) {
		return transformPoly(label, poly, transformPoints(poly.getPoints()));
	}

	/**
	 * Apply the transform to given element and set label for result 
	 * @param label
	 * @param geo
	 * @return transformed geo
	 */
	public GeoElement[] transform(GeoElement geo, String label) {
		if (geo.isGeoPolygon()) {
			GeoPolygon poly = (GeoPolygon) geo;
			return transformPoly(label, poly, transformPoints(poly.getPoints()));
		}
		if (label == null)
			label = transformedGeoLabel(geo);

		if (geo.isLimitedPath()) {
			// handle segments, rays and arcs separately
			GeoElement[] geos = ((LimitedPath) geo)
					.createTransformedObject(this);

			// if (geos[0] instanceof Orientable && geoMir instanceof
			// Orientable)
			// ((Orientable)geos[0]).setOppositeOrientation(
			// (Orientable)geoMir);

			return geos;
		}
		// standard case
		GeoElement ret = doTransform(geo);
		ret.setLabel(label);
		ret.setVisualStyleForTransformations(geo);
		GeoElement[] geos = { ret };
		return geos;

	}

	private GeoElement[] transformPoly(String label, GeoPolygon oldPoly,
			GeoPoint[] transformedPoints) {
		// get label for polygon
		String[] polyLabel = null;
		if (label == null) {
			if (oldPoly.isLabelSet()) {
				polyLabel = new String[1];
				polyLabel[0] = transformedGeoLabel(oldPoly);
			}
		} else {
			polyLabel = new String[1];
			polyLabel[0] = label;
		}

		// use visibility of points for transformed points
		GeoPoint[] oldPoints = oldPoly.getPoints();
		for (int i = 0; i < oldPoints.length; i++) {
			transformedPoints[i].setEuclidianVisible(oldPoints[i]
					.isSetEuclidianVisible());
			transformedPoints[i].setVisualStyleForTransformations(oldPoints[i]);
			cons.getKernel().notifyUpdate(transformedPoints[i]);
		}

		// build the polygon from the transformed points
		return cons.getKernel().Polygon(polyLabel, transformedPoints);
	}

	public GeoPoint[] transformPoints(GeoPoint[] points) {
		// dilate all points
		GeoPoint[] newPoints = new GeoPoint[points.length];
		for (int i = 0; i < points.length; i++) {
			String pointLabel = transformedGeoLabel(points[i]);
			newPoints[i] = (GeoPoint) transform(points[i], pointLabel)[0];
			newPoints[i].setVisualStyleForTransformations(points[i]);
		}
		return newPoints;
	}

	public GeoConic getTransformedConic(GeoConic conic) {
		GeoConic ret = (GeoConic) doTransform(conic);
		ret.setVisualStyleForTransformations(conic);
		return ret;
	}

	public GeoLine getTransformedLine(GeoLine line) {
		GeoLine ret = (GeoLine) doTransform(line);
		ret.setVisualStyleForTransformations(line);
		return ret;
	}
}

/**
 * Rotation
 * 
 * @author kondr
 * 
 */
class TransformRotate extends Transform {

	private GeoPoint center;
	private NumberValue angle;

	/**
	 * @param angle
	 */
	public TransformRotate(NumberValue angle) {
		this.angle = angle;
		this.cons = angle.toGeoElement().getConstruction();
	}
	
	/**
	 * @param angle
	 * @param center
	 */
	public TransformRotate(NumberValue angle,GeoPoint center) {
		this.angle = angle;
		this.center = center;
		this.cons = center.getConstruction();
	}

	@Override
	protected GeoElement doTransform(GeoElement geo) {
		AlgoTransformation algo = null;
		if (center == null) {
			algo = new AlgoRotate(cons,geo,angle);
		}
		else algo = new AlgoRotatePoint(cons,geo,angle,center);
		return algo.getResult();
	}

}

/**
 * Translation
 * 
 * @author kondr
 * 
 */
class TransformTranslate extends Transform {

	private GeoVec3D transVec;

	/**
	 * @param transVec
	 */
	public TransformTranslate(GeoVec3D transVec) {
		this.transVec = transVec;
		this.cons = transVec.getConstruction();
	}

	@Override
	protected GeoElement doTransform(GeoElement geo) {
		AlgoTranslate algo = new AlgoTranslate(cons, (Translateable) geo,
				transVec);
		return algo.getResult();
	}

}

/**
 * Dilation
 * 
 * @author kondr
 * 
 */
class TransformDilate extends Transform {

	private NumberValue ratio;
	private GeoPoint center;

	/**
	 * @param ratio
	 */
	public TransformDilate(NumberValue ratio) {
		this.ratio = ratio;
		this.cons = ratio.toGeoElement().getConstruction();
	}

	/**
	 * @param ratio
	 * @param center
	 */
	public TransformDilate(NumberValue ratio, GeoPoint center) {
		this.ratio = ratio;
		this.center = center;
		this.cons = center.getConstruction();
	}

	@Override
	protected GeoElement doTransform(GeoElement geo) {
		AlgoDilate algo = new AlgoDilate(cons, (Dilateable) geo, ratio, center);
		return algo.getResult();
	}

}

/**
 * Mirror
 * 
 * @author kondr
 * 
 */
class TransformMirror extends Transform {

	private GeoElement mirror;

	/**
	 * @param mirrorPoint
	 */
	public TransformMirror(GeoPoint mirrorPoint) {
		mirror = mirrorPoint;
		cons = mirror.getConstruction();
	}

	/**
	 * @param mirrorCircle
	 */
	public TransformMirror(GeoConic mirrorCircle) {
		mirror = mirrorCircle;
		cons = mirror.getConstruction();
	}

	/**
	 * @param mirrorLine
	 */
	public TransformMirror(GeoLine mirrorLine) {
		mirror = mirrorLine;
		cons = mirror.getConstruction();
	}

	@Override
	protected GeoElement doTransform(GeoElement geo) {
		AlgoMirror algo = null;
		if (mirror.isGeoLine()) {
			algo = new AlgoMirror(cons, geo, (GeoLine) mirror, null, null);
		} else if (mirror.isGeoPoint()) {
			algo = new AlgoMirror(cons, geo, null, (GeoPoint) mirror, null);
		} else {
			algo = new AlgoMirror(cons, geo, null, null, (GeoConic) mirror);
		}
		return algo.getResult();
	}

}

/**
 * Shear or stretch
 * 
 * @author kondr
 * 
 */
class TransformShearOrStretch extends Transform {

	private boolean shear;
	private GeoVec3D line;
	private NumberValue num;

	/**
	 * @param line
	 * @param num
	 * @param shear
	 */
	public TransformShearOrStretch(GeoVec3D line, GeoNumeric num, boolean shear) {
		this.shear = shear;
		this.line = line;
		this.num = num;
		this.cons = line.getConstruction();
	}

	@Override
	protected GeoElement doTransform(GeoElement geo) {
		AlgoShearOrStretch algo = new AlgoShearOrStretch(cons, geo, line, num,
				shear);
		return algo.getResult();
	}

}

/**
 * Generic affine transform
 * 
 * @author kondr
 * 
 */
class TransformApplyMatrix extends Transform {

	private GeoList matrix;

	
	/**
	 * @param matrix
	 */
	public TransformApplyMatrix(GeoList matrix) {
		this.matrix = matrix;
		this.cons = matrix.getConstruction();
	}

	@Override
	protected GeoElement doTransform(GeoElement geo) {
		AlgoApplyMatrix algo = new AlgoApplyMatrix(cons, null, geo, matrix);
		return algo.getResult();
	}

}
