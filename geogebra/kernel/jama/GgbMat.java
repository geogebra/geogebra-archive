package geogebra.kernel.jama;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.main.Application;


public class GgbMat extends Matrix{

	private boolean isUndefined = false;

	public GgbMat (GeoList inputList) {
		int rows = inputList.size();
		if (!inputList.isDefined() || rows == 0) {
			setIsUndefined(true);
			return;
		} 

		GeoElement geo = inputList.get(0);

		if (!geo.isGeoList()) {
			setIsUndefined(true);
			return;   		
		}


		int cols = ((GeoList)geo).size();

		if (cols == 0) {
			setIsUndefined(true);
			return;   		
		}

		A = new double[rows][cols];
		m = rows;
		n = cols;

		GeoList rowList;

		for (int r = 0 ; r < rows ; r++) {
			geo = inputList.get(r);
			if (!geo.isGeoList()) {
				setIsUndefined(true);
				return;   		
			}
			rowList = (GeoList)geo;
			if (rowList.size() != cols) {
				setIsUndefined(true);
				return;   		
			}
			for (int c = 0 ; c < cols ; c++) {
				geo = rowList.get(c);
				if (!geo.isGeoNumeric()) {
					setIsUndefined(true);
					return;   		
				}

				set(r, c, ((GeoNumeric)geo).getValue());
			}
		}
	}

	public void inverseImmediate() {

		try {
			Matrix ret = inverse();
			A = ret.A;
			m = ret.m;
			n = ret.n;
		}
		catch (Exception e) { // can't invert
			setIsUndefined(true);
		}

	}



	public void transposeImmediate() {

		double[][] C = new double[n][m];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[j][i] = A[i][j];
			}
		}
		A = C;
		int temp = n;
		n = m;
		m = temp;
		//Application.debug(""+A[0][0]);
	}

	/*
	 * returns GgbMatrix as a GeoList eg { {1,2}, {3,4} }
	 */
	public GeoList getGeoList(GeoList outputList, Construction cons) {

		if (isUndefined) {
			outputList.setDefined(false);
			return outputList;
		}

		outputList.clear();
		outputList.setDefined(true);

		for (int r = 0 ; r < m ; r++) {  	   			
			GeoList columnList = new GeoList(cons);
			for (int c = 0 ; c < n ; c++) {
				//Application.debug(get(r, c)+"");
				columnList.add(new GeoNumeric(cons, get(r, c)));  	   			
			}
			outputList.add(columnList);
		}

		return outputList;

	}

	/*
	 * returns true if the matrix is undefined
	 * eg after being inverted 
	 */
	public boolean isUndefined() {
		return isUndefined;
	}
	public void setIsUndefined(boolean undefined) {
		isUndefined = undefined;
	}

	public boolean isSquare() {
		return (n == m);
	}


}
