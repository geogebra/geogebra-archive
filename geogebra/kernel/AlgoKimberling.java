package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;

/**
 * @author Victor Franco Espino
 * @version 11-02-2007
 * 
 *          This class calculate affine ratio of 3 points: (A,B,C) = (t(C)-t(A))
 *          : (t(C)-t(B))
 */

public class AlgoKimberling extends AlgoElement {

	public static final long serialVersionUID = 1L;
	private GeoPoint A, B, C; // input
	private GeoPoint M; // output
	private NumberValue n;

	AlgoKimberling(Construction cons, String label, GeoPoint A, GeoPoint B,
			GeoPoint C, NumberValue n) {
		super(cons);
		this.A = A;
		this.B = B;
		this.C = C;
		this.n = n;
		// create new GeoNumeric Object to return the result
		M = new GeoPoint(cons);
		setInputOutput();
		compute();
		M.setLabel(label);
	}

	public String getClassName() {
		return "AlgoKimberling";
	}

	// for AlgoElement
	protected void setInputOutput() {
		input = new GeoElement[4];
		input[0] = A;
		input[1] = B;
		input[2] = C;
		input[3] = n.toGeoElement();

		setOutputLength(1);
		setOutput(0, M);
		setDependencies(); // done by AlgoElement
	}

	GeoPoint getResult() {
		return M;
	}

	protected final void compute() {
		// Check if the points are aligned
		double c = A.distance(B);
		double b = C.distance(A);
		double a = B.distance(C);

		int k = (int) n.getDouble();
		double wA = weight(k, a, b, c);
		double wB = weight(k, b, c, a);
		double wC = weight(k, c, a, b);
		double w = wA + wB + wC;
		M.setCoords((A.x / A.z * wA + B.x / B.z * wB + C.x / C.z * wC) / w,
				(A.y / A.z * wA + B.y / B.z * wB + C.y / C.z * wC) / w, 1);
		M.setCaption("$X_{" + k + "}$");
		M.setLabelMode(GeoElement.LABEL_CAPTION);

	}

	private double weight(int k, double a, double b, double c) {
		double a2 = a * a, b2 = b * b, c2 = c * c, s = 0.5 * (a + b + c), Area = Math
				.sqrt(s * (s - a) * (s - b) * (s - c)), cosA = 0.5
				* (b2 + c2 - a2) / b / c;
		switch (k) {
		case 1:
			return a; // incenter
		case 2:
			return 1; // centroid
		case 3:
			return a * cosA; // circumcentre
		case 4:
			return a / cosA; // orthocenter
		case 5:
			return a2 * (b2 + c2) - (b2 - c2) * (b2 - c2); // nine-point
															// center
		case 6:
			return a2;// symmedian point
		case 7:
			return 1 / (b + c - a);// Gergonne point
		case 8:
			return b + c - a;// Nagel point
		case 9:
			return a * (b + c - a);// Mittenpunkt
		case 10:
			return b + c; // Spieker center
		case 11:
			return (b + c - a) * (b - c) * (b - c);// Feuerbach point
		case 12:
			return (b + c) * (b + c) / (b + c - a);
		case 13:
			return a2 * a2 - 2 * (b2 - c2) * (b2 - c2) + a * a
					* (b2 + c2 + 4 * Math.sqrt(3) * Area);// Fermat
															// point
		case 14:
			return a2 * a2 - 2 * (b2 - c2) * (b2 - c2) + a * a
					* (b2 + c2 + 4 * Math.sqrt(3) * Area);
		case 15:
			return a * sinAlphaPlusB(cosA, Math.PI / 3); // 1st isodynamic
																// point
		case 16:
			return a * sinAlphaPlusB(cosA, -Math.PI / 3); // 2nd isodynamic
																// point
		case 17:
			return a / sinAlphaPlusB(cosA, Math.PI / 6); // 1st Napoleon
																// point
		case 18:
			return a / sinAlphaPlusB(cosA, -Math.PI / 6); // 2nd Napoleon
																// point
		case 19:
			return a2 / cosA;
		case 20:
			return -3 * a2 * a2 + 2 * a2 * (b2 + c2) + (b2 - c2) * (b2 - c2);
		case 21:
			return a / (b * (a2 + c2 - b2) + c * (a2 + b2 - c2));
		case 22:
			return a2 * (b2 * b2 + c2 * c2 - a2 * a2);
		case 23: 
			return a2*(b2*b2 + c2*c2 - a2*a2 - b2*c2);
		case 24:
			return a/cosA*cosDouble(cosA);
		case 25:
			return a2/(b2 + c2 - a2);
		case 26:
			return a2*(b2*cosDouble(cosFst(b,c,a)) + c2*cosDouble(cosFst(c,a,b)) 
					- a2*cosDouble(cosA));
		case 27:
			return a/cosA/(b + c) ;
		case 28:
			return a*a/cosA/(b + c);
		case 29:
			return a*cosA/(cosFst(b,c,a)+cosFst(c,a,b));
		case 30:
			return 2*a2*a2 - (b2 - c2)*(b2 - c2) - a2*(b2 + c2);
		case 31:
			return a2*a;
		case 32:
			return a2*a2;
		case 33:
			return (1 + cosA)/2*a/cosA;
		case 34:
			return (1 - cosA)/2*a/cosA;
		case 35:
			return a2*(b2 + c2 - a2 + b*c);
		case 36:
			return a2*(b2 + c2 - a2 - b*c);
		case 37:
			return a*(b+c);
		case 38:
			return a*(b2+c2);
		case 39:
			return a2*(b2+c2);
		case 40:
			return a*(b/(s - b) + c/(s - c) - a/(s - a));
		case 41:
			return a2*a*(b + c - a);
		case 42:
			return a2*(b+c);
		case 43:
			return a2*(b+c)-a*b*c;
		case 44:
			return a*(b + c - 2*a);
		case 45:
			return a*(2*b + 2*c - a);
		case 46:
			return a*(cosFst(b,c,a) + cosFst(c,a,b) - cosA);
		case 47:
			return a*cosDouble(cosA);
		case 48:
			return a*cosA*Math.sqrt(1-cosA*cosA);
		case 49:{
			double cos2A=cosDouble(cosA);			
			return a*(cosA*cos2A-Math.sqrt(1-cosA*cosA)*Math.sin(Math.acos(cos2A)));}
		case 50:{
			double cos2A=cosDouble(cosA);
			return a*(cosA*Math.sin(Math.acos(cos2A))+cos2A*Math.sqrt(1-cosA*cosA));}	
		default:
			return Double.NaN;
		}
	}
	
	private double cosFst(double a,double b,double c){
		return 0.5*(b*b+c*c-a*a)/b/c;
	}
	private double cosDouble(double cosA) {
		return 2*cosA*cosA-1;
	}

	private double sinAlphaPlusB(double cosA, double B) {
		return Math.cos(B) * Math.sqrt(1 - cosA * cosA)
				+ Math.sin(B) * cosA;

	}

}