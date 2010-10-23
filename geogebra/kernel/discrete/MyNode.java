package geogebra.kernel.discrete;

import geogebra.kernel.kernelND.GeoPointND;

public class MyNode {
	private GeoPointND id; 
	public MyNode(GeoPointND id) {
		this.id = id;
	}
	public String toString() { 
		return "Vertex:"+id; 
	}
}