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
	}

	private double weight(int k, double a, double b, double c) {
		double a2 = a * a, b2 = b * b, c2 = c * c, s = 0.5 * (a + b + c), Area = Math
				.sqrt(s * (s - a) * (s - b) * (s - c)),  
				cosA =  S(a,b,c) / b / c, cosB =  S(b,a,c) / a / c, 
				cosC =  S(c,a,b) / b / a ;
		switch (k) {
		case 1: // incenter
			return a; 
		case 2: // centroid
			return 1; 
		case 3: // circumcentre
			return a * cosA; 
		case 4: // orthocenter
			return a / cosA; 
		case 5: // nine-point center
			return a2 * (b2 + c2) - (b2 - c2) * (b2 - c2); 				
		case 6: // symmedian point
			return a2;
		case 7: // Gergonne point
			return 1 / (s - a);
		case 8:// Nagel point
			return s - a;
		case 9:// Mittenpunkt
			return a * (s - a);
		case 10:// Spieker center
			return b + c; 
		case 11:// Feuerbach point
			return (s - a) * (b - c) * (b - c);
		case 12:
			return (b + c) * (b + c) / (b + c - a);
		case 13: //Fermat point
			return a2 * a2 - 2 * (b2 - c2) * (b2 - c2) + a * a
					* (b2 + c2 + 4 * Math.sqrt(3) * Area);
		case 14:
			return a2 * a2 - 2 * (b2 - c2) * (b2 - c2) + a * a
					* (b2 + c2 + 4 * Math.sqrt(3) * Area);
		case 15: //1st isodynamic point
			return a * sinAplusX(cosA, Math.PI / 3); 
		case 16: //2nd isodynamic point
			return a * sinAplusX(cosA, -Math.PI / 3); 
		case 17: //1st Napoleon point
			return a / sinAplusX(cosA, Math.PI / 6); 
		case 18: // 2nd Napoleon point
			return a / sinAplusX(cosA, -Math.PI / 6); 
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
			return a2*(b2*cosDouble(cosB) + c2*cosDouble(cosC) 
					- a2*cosDouble(cosA));
		case 27:
			return a/cosA/(b + c) ;
		case 28:
			return a*a/cosA/(b + c);
		case 29:
			return a*cosA/(cosB+cosC);
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
			return a*(cosB + cosC - cosA);
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
		case 51:
			return a2*(a2*(b2 + c2) - (b2 - c2)*(b2-c2));
		case 52:
			return a/cosA* (1/cosDouble(cosB) + 1/cosDouble(cosC));
		case 53:
			return a2/cosA*cosBminusC(cosB,cosC);
		case 54:
			return a/cosBminusC(cosB,cosC);
		case 55:
			return a2*(s-a);	
		case 56:
			return a2/(s-a);
		case 57:
			return a/(s-a);
		case 58:
			return a2/(b+c);
		case 59:
			return a/(1-cosBminusC(cosB,cosC));
		case 60:
			return a/(1-cosBminusC(cosB,cosC));
		case 61:
			return Math.cos(Math.acos(cosA) - Math.PI/3);
		case 62:
			return a*sinAplusX(cosA,-Math.PI/6);
		case 63:
			return cosA;
		case 64:
			return a/(cosA - cosB*cosC);
		case 65:
			return a*(b + c)/(s - a);
		case 66:
			return 1/(b2*b2 + c2*c2 - a2*a2);
		case 67:
			return 1/(b2*b2 + c2*c2 - a2*a2 - b2*c2);
		case 68: //Prasolov point
			return a*cosA/cosDouble(cosA);
		case 69:
			return b2 + c2 - a2;
		case 70:
			return 1/(b2*cosDouble(cosB) + c2*cosDouble(cosC) - a2*cosDouble(cosA));
		case 71:
			 return (b + c)*a*cosA;
		case 72:
			return (b + c)*(b2 + c2 - a2);
		case 73:
			return (cosB+cosC)*a*cosA;
		case 74:
			return a/(cosA - 2*cosB*cosC);
		case 75:
			return b*c;
		case 76: 
			return 1/a2;
		case 77:
			return a/(1 + 1/cosA);
		case 78:
			return a/(1 - 1/cosA);
		case 79:
			return 1/(b2 + c2 - a2 + b*c);
		case 80:
			return 1/(b2 + c2 - a2 - b*c);
		case 81:
			return a/(b+c);
		case 82:
			return a/(b2+c2);
		case 83:
			return 1/(b2+c2);
		case 84:
			return 1/(cosB + cosC - cosA - 1);
		case 85:
			return b*c/(b + c - a);
		case 86:
			return 1/(b+c);
		case 87:	
			return a/(a*b + a*c - b*c);
		case 88:
			return a/(b + c - 2*a);
		case 89:
			return a/(2*b + 2*c - a);
		case 90:
			return a/(cosB + cosC - cosA);
		case 91:
			return a/cosDouble(cosA);
		case 92:
			return 1/cosA;
		case 93:
			{double cos2A=cosDouble(cosA);			
			return a/(cosA*cos2A-Math.sqrt(1-cosA*cosA)*Math.sin(Math.acos(cos2A)));}
		case 94:
		{double cos2A=cosDouble(cosA);			
		return a/(cosA*Math.sin(Math.acos(cos2A))+cos2A*Math.sqrt(1-cosA*cosA));}
		case 95: 
			return b*c/cosBminusC(cosB,cosC);
		case 96: 
			return a/cosDouble(cosA)/cosBminusC(cosB,cosC);
		case 97: 
			return cosA/cosBminusC(cosB,cosC);
		case 98: //Tarry point 
			return 1/(b2*b2 + c2*c2 - a2*b2 - a2*c2);
		case 99: //Steiner point 
			return 1/(b2 - c2);
		case 100: 
			return a/(b - c);
		case 101:
			return a2/(b - c);
		case 102:
			return a2/(2*a2*a2*a + (b + c)*a2*a2 - 2*(b2 + c2)*a*a2 - (b + c)*(b2 - c2)*(b2-c2));
		case 103:
			return a/(a2 - b2*cosC - c2* cosB);
		case 104:
			return a/(-1 + cosB + cosC);
		case 105:
			return a/(b2 + c2 - a*(b + c));
		case 106:
			return a2/(2*a - b - c);
		case 107:
			return 1/(b2 - c2)/(b2 + c2 - a2)/(b2 + c2 - a2);
		case 108:
			return a2/(1/cosB - 1/cosC);
		case 109:
			return a2/(cosB - cosC);
		case 110: return a2/(b2-c2);
		case 111: return a2/(2*a2 - b2 - c2);
		case 112: return a2/(b*cosB - c*cosC);
		case 113:return b2/(b2*S(b,a,c) - 2*S(a,b,c)*S(c,b,a)) + c2/(c2*S(c,a,b) - 2*S(a,b,c)*S(b,a,c
				
				));
		case 114: double omega = Math.atan(4*Area/(a2+b2+c2));
			return b /Math.cos(Math.acos(cosB) + omega) + c /Math.cos(Math.acos(cosC) + omega);
		case 115: return(b2-c2)*(b2-c2);
		case 116: return (b - c)*(b-c)*(b2 + b*c + c2 - a*b - a*c);
		case 117: return b/(c*(1/cosB - 1/cosC) + a*(1/cosB - 1/cosA))+
				c/(a*(1/cosC - 1/cosA) + b*(1/cosC - 1/cosB)); 
		case 118: return b2/((b - c)* cosA/a + (b - a)*cosC/c)+
				c2/((c - a)* cosB/b + (c - b)*cosA/a);
		//(-1 + cos B + cos C)[sin 2B + sin 2C + 2(-1 + cos A)(sin B + sin C)]
		case 119: return (-1 + cosB + cosC)*(2*b*cosB + 2*c*cosC + 2*(-1 + cosA)*(b + c));
		case 120:return (2*a*b*c - (b + c)*(a2 + (b - c)*(b-c)))*(b2 + c2 - a*b -a*c); 
		case 121: return (b + c - 2*a)*(b2*b + c2*c + a*(b2 + c2) - 2*b*c*(b + c));
		case 122: return a*(b2 - c2)*(b2-c2)*(cosA - cosB*cosC)*cosDouble(cosA)/a/cosA;
		case 123: return (1/cosB - 1/cosC)*((1/cosA)*(b*b - c*c) + c*c/cosC - b*b/cosB);
		case 124: return (b + c - a)*(b - c)*(b-c)*((b + c)*(b2 + c2 - a2 - b*c) + a*b*c) ;
		case 125: return (b2 + c2 - a2)*(b2 - c2)*(b2 - c2);
		case 126: return (2*a2 - b2 - c2)*(b2*b2 + c2*c2 + a2*(b2 + c2) - 4*b2*c2);
		case 127: return (b*cosB - c*cosC)*((b2 -c2)*a*cosA - b2*b*cosB + c2*c*cosC);
		case 128: return a/cosA*(cosDouble(cosB) + cosDouble(cosC))*(1 + 2*cosDouble(cosA
				))*(cosDouble(cosA) +2*cosDouble(cosB) * cosDouble(cosC)); 
		case 129:
			double sin2A = 2*cosA*Math.sqrt(1-cosA*cosA);
			double sin2B = 2*cosB*Math.sqrt(1-cosB*cosB);
			double sin2C = 2*cosC*Math.sqrt(1-cosC*cosC);
			double vABC = (sin2B * sin2C)*(sin2B - sin2C)*(sin2B - sin2C);
			double uABC = sin2B* sin2C - sin2B*sin2B - sin2C*sin2C;
			double tABC = sin2A*sin2A*sin2A*sin2A + sin2A*sin2A* uABC + vABC;
			double sABC = sin2B*sin2B*sin2B*sin2B + sin2C*sin2C*sin2C*sin2C - sin2A*sin2A*sin2B*sin2B - sin2A*sin2A* sin2C*sin2C;
			return a/cosA*sin2A*(sin2B + sin2C)*sABC*tABC;
		case 130:
			return a*(b*cosB+c*cosC)*(b*cosB-c*cosC)*(b*cosB-c*cosC)*(a*a*cosA*cosA + b*c*cosB*cosC); 
            
		default:
			return Double.NaN;
		}
	}
	
	private double S(double a, double b, double c) {
		return (b*b+c*c-a*a)/2;
	}

	private double cosBminusC(double cosB, double cosC) {
		return Math.cos(Math.acos(cosB)-Math.acos(cosC));
	}

	
	private double cosDouble(double cosA) {
		return 2*cosA*cosA-1;
	}

	private double sinAplusX(double cosA, double X) {
		return Math.cos(X) * Math.sqrt(1 - cosA * cosA)
				+ Math.sin(X) * cosA;
	}
	
	
}