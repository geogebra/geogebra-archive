/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

 */

package geogebra.kernel.statistics;

import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.arithmetic.NumberValue;

import org.apache.commons.math.distribution.CauchyDistribution;
import org.apache.commons.math.distribution.CauchyDistributionImpl;
import org.apache.commons.math.distribution.ChiSquaredDistribution;
import org.apache.commons.math.distribution.ChiSquaredDistributionImpl;
import org.apache.commons.math.distribution.ExponentialDistribution;
import org.apache.commons.math.distribution.ExponentialDistributionImpl;
import org.apache.commons.math.distribution.FDistribution;
import org.apache.commons.math.distribution.FDistributionImpl;
import org.apache.commons.math.distribution.GammaDistribution;
import org.apache.commons.math.distribution.GammaDistributionImpl;
import org.apache.commons.math.distribution.HypergeometricDistribution;
import org.apache.commons.math.distribution.HypergeometricDistributionImpl;
import org.apache.commons.math.distribution.PascalDistribution;
import org.apache.commons.math.distribution.PascalDistributionImpl;
import org.apache.commons.math.distribution.TDistribution;
import org.apache.commons.math.distribution.TDistributionImpl;
import org.apache.commons.math.distribution.WeibullDistribution;
import org.apache.commons.math.distribution.WeibullDistributionImpl;

/**
 * 
 * @author Michael Borcherds
 * @version 20090730
 */
@SuppressWarnings("deprecation")
public abstract class AlgoDistribution extends AlgoElement {

	private static final long serialVersionUID = 1L;
	protected NumberValue a,b,c,d; //input
	protected GeoNumeric num; //output	
	private TDistribution t = null;
	private ChiSquaredDistribution chisquared = null;
	private FDistribution f = null;
	private GammaDistribution gamma = null;
	private CauchyDistribution cauchy = null;
	private ExponentialDistribution exponential = null;
	private HypergeometricDistribution hypergeometric = null;
	private PascalDistribution pascal = null;
	private WeibullDistribution weibull = null;

	public AlgoDistribution(Construction cons, String label, NumberValue a, NumberValue b, NumberValue c, NumberValue d) {
		super(cons);
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;

		num = new GeoNumeric(cons);

		setInputOutput();
		compute();
		num.setLabel(label);
	}

	abstract protected String getClassName();

	protected void setInputOutput(){
		
		if (d != null) {
			input = new GeoElement[4];
			input[2] = c.toGeoElement();
			input[3] = d.toGeoElement();		
		} else if (c != null) {
			input = new GeoElement[3];
			input[2] = c.toGeoElement();
		} else {
			input = new GeoElement[2];
		} 
		input[0] = a.toGeoElement();
		input[1] = b.toGeoElement();

		output = new GeoElement[1];
		output[0] = num;
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getResult() {
		return num;
	}

	abstract protected void compute();     



	TDistribution getTDistribution(double param) {
		if (t == null) 
			t = new TDistributionImpl(param);
		else if (t.getDegreesOfFreedom() != param)
			t.setDegreesOfFreedom(param);
		return t;
	}

	FDistribution getFDistribution(double param, double param2) {
		if (f == null) 
			f = new FDistributionImpl(param, param2);
		else {
			if (f.getNumeratorDegreesOfFreedom() != param)
				f.setNumeratorDegreesOfFreedom(param);
			if (f.getDenominatorDegreesOfFreedom() != param2)
				f.setDenominatorDegreesOfFreedom(param2);
		}
		return f;
	}

	GammaDistribution getGammaDistribution(double param, double param2) {
		if (gamma == null) 
			gamma = new GammaDistributionImpl(param, param2);
		else {
			if (gamma.getAlpha() != param)
				gamma.setAlpha(param);
			if (gamma.getBeta() != param2)
				gamma.setBeta(param2);
		}
		return gamma;
	}

	CauchyDistribution getCauchyDistribution(double param, double param2) {
		if (cauchy == null) 
			cauchy = new CauchyDistributionImpl(param, param2);
		else {
			if (cauchy.getMedian() != param)
				cauchy.setMedian(param);
			if (cauchy.getScale() != param2)
				cauchy.setScale(param2);
		}
		return cauchy;
	}

	ChiSquaredDistribution getChiSquaredDistribution(double param) {
		if (chisquared == null) 
			chisquared = new ChiSquaredDistributionImpl(param);
		else {
			if (chisquared.getDegreesOfFreedom() != param)
				chisquared.setDegreesOfFreedom(param);
		}
		return chisquared;
	}

	ExponentialDistribution getExponentialDistribution(double param) {
		if (exponential == null) 
			exponential = new ExponentialDistributionImpl(param);
		else {
			if (exponential.getMean() != param)
				exponential.setMean(param);
		}
		return exponential;
	}

	
	HypergeometricDistribution getHypergeometricDistribution(int param, int param2, int param3) {
		if (hypergeometric == null) 
			hypergeometric = new HypergeometricDistributionImpl(param, param2, param3);
		else {
			if (hypergeometric.getPopulationSize() != param)
				hypergeometric.setPopulationSize(param);
			if (hypergeometric.getNumberOfSuccesses() != param2)
				hypergeometric.setNumberOfSuccesses(param2);
			if (hypergeometric.getSampleSize() != param3)
				hypergeometric.setSampleSize(param3);
		}
		return hypergeometric;
	}

	PascalDistribution getPascalDistribution(int param, double param2) {
		if (pascal == null) 
			pascal = new PascalDistributionImpl(param, param2);
		else {
			if (pascal.getNumberOfSuccesses() != param)
				pascal.setNumberOfSuccesses(param);
			if (pascal.getProbabilityOfSuccess() != param2)
				pascal.setProbabilityOfSuccess(param2);
		}
		return pascal;
	}

	WeibullDistribution getWeibullDistribution(double param, double param2) {
		if (weibull == null) 
			weibull = new WeibullDistributionImpl(param, param2);
		else {
			if (weibull.getShape() != param)
				weibull.setShape(param);
			if (weibull.getScale() != param2)
				weibull.setScale(param2);
		}
		return weibull;
	}

}


