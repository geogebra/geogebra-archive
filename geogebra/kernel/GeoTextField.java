package geogebra.kernel;

import geogebra.util.Util;

public class GeoTextField extends GeoButton {

	private GeoElement linkedGeo = null;
	
	
	public GeoTextField(Construction c) {
		
		super(c);
	}
	protected String getClassName() {
		return "GeoTextField";
	}
	
    protected String getTypeString() {
		return "TextField";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_TEXTFIELD;
    }
    
	public boolean isTextField() {
		return true;
	}
	
	public void setLinkedGeo(GeoElement geo) {
		linkedGeo = geo;
	}
	
	public GeoElement getLinkedGeo() {
		return linkedGeo;
	}
	
	protected void getXMLtags(StringBuilder sb) {

		super.getXMLtags(sb);
		if (linkedGeo != null) {
   	
			sb.append("\t<linkedGeo exp=\"");
			sb.append(Util.encodeXML(linkedGeo.getLabel()));
				sb.append("\"");			    	
	    	
			sb.append("/>\n");
		}

	}
	

}
