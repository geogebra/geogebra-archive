package geogebra3D.old.euclidian3D;

import java.awt.Graphics2D;
import java.awt.Shape;

import geogebra.euclidian.DrawableList;
import geogebra.kernel.Kernel;
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;
import geogebra3D.kernel3D.Kernel3D;


public class DrawableList3D extends DrawableList {
	
	Shape totalClip; //total clipping part = view
	
	//public void drawAll(Graphics2D g2, int layer) { drawHiddenParts(g2); }
	
	public void drawHiddenParts(Graphics2D g2){
		
		
		
		totalClip = g2.getClip() ; //total clipping part = view
		
		Drawable3D d1, d2;
		Link cur1, cur2;
		
		//initing hidden parts
		/*
		cur1 = head;
		while (cur1 != null) {
			d1 = (Drawable3D) cur1.d;
			d1.initHiddenPart();
			cur1 = cur1.next;
		}
		*/
		
		
		//creating hidden parts	
		
		cur1 = head;
		while (cur1 != null) {
			d1 = (Drawable3D) cur1.d;
			cur2 = cur1.next;
			while (cur2 != null) {
				d2 = (Drawable3D) cur2.d;
				
				//if (d1.hidable(d2)){
					switch(d1.getType()){
					case Drawable3D.POINT3D:
						switch(d2.getType()){
						case Drawable3D.POINT3D:
							creatHiddenPartsPoint3DPoint3D((DrawPoint3D) d1, (DrawPoint3D) d2);
							break;
						case Drawable3D.SEGMENT3D:
							creatHiddenPartsPoint3DSegment3D((DrawPoint3D) d1, (DrawSegment3D) d2);
							break;
						default :
							break;
						}
						break;
					case Drawable3D.SEGMENT3D:
						switch(d2.getType()){
						case Drawable3D.POINT3D:
							creatHiddenPartsPoint3DSegment3D((DrawPoint3D) d2, (DrawSegment3D) d1);
							break;
						case Drawable3D.SEGMENT3D:
							creatHiddenPartsSegment3DSegment3D((DrawSegment3D) d1, (DrawSegment3D) d2);
							break;
						default :
							break;
						}
						break;
					default :
						break;				
					}
				//}
				
				cur2 = cur2.next;
			}
			cur1 = cur1.next;
		}
		
		
		//drawing not hidden parts
		cur1 = head;
		while (cur1 != null) {
			d1 = (Drawable3D) cur1.d;
			d1.drawNotHidden(g2,totalClip);
			//d1.draw(g2);
			cur1 = cur1.next;
		}
		
		g2.setClip(totalClip);
		
	}
	
	
	protected void creatHiddenPartsPoint3DPoint3D(DrawPoint3D d1, DrawPoint3D d2){
		
		if (d1.intersectsPoint3D(d2)){ // check if d1 intersects d2
			if (d1.getZ()>d2.getZ()){
				d1.addHiddenPart(d2.getHidingPart());
			}else{
				d2.addHiddenPart(d1.getHidingPart());
			}
		}
	}

	protected void creatHiddenPartsPoint3DSegment3D(DrawPoint3D d1, DrawSegment3D d2){
		if (d1.intersectsSegment3D(d2)){ // check if d1 intersects d2
			double dist = d1.getZ()-d2.getZ(d1) - (d1.thickness + d2.thickness);
			if (dist > 0)
				d1.addHiddenPart(d2.getHidingPart());
			if (dist < 0)
				d2.addHiddenPart(d1.getHidingPart());
			//TODO case dist = 0
		}
	}

	
	
	protected void creatHiddenPartsSegment3DSegment3D(DrawSegment3D d1, DrawSegment3D d2){
		
		GgbVector O1 = d1.getOrigin(3); GgbVector V1 = d1.getVector(3);
		GgbVector O2 = d2.getOrigin(3); GgbVector V2 = d2.getVector(3);
		
		GgbMatrix m = new GgbMatrix(2,2);
		m.set(1,1,V1.dotproduct(V1));  m.set(1,2,-V1.dotproduct(V2));
		m.set(2,1,-V1.dotproduct(V2)); m.set(2,2,V2.dotproduct(V2));
		
		GgbMatrix M1;
		GgbMatrix M2;
		GgbMatrix M3;
		GgbMatrix M4;
		
		if (Math.abs(m.det())< Kernel.EPSILON){
			M1 = O1.copy();
			M2 = O2.copy();
			M3 = O1.add(V1);
			M4 = O2.add(V2);
		}else{
			GgbVector V = new GgbVector(new double[] {O2.sub(O1).dotproduct(V1),O1.sub(O2).dotproduct(V2)});
			
			
			GgbVector Lambda = m.inverse().mul(V);
			//Lambda.SystemPrint();
			
			M1 = O1.add(V1.mul(Lambda.get(1)));
			M2 = O2.add(V2.mul(Lambda.get(2)));
			M3 = O1.add(V1.mul(1.0-Lambda.get(1)));
			M4 = O2.add(V2.mul(1.0-Lambda.get(2)));
		}
		
		double dZ = M1.get(3, 1) - M2.get(3, 1);
		if (dZ > Kernel.EPSILON)
			d1.addHiddenPart(d2.getHidingPart());
		else if (dZ < -Kernel.EPSILON)
			d2.addHiddenPart(d1.getHidingPart());
		else{
			dZ = M3.get(3, 1) - M4.get(3, 1);
			if (dZ > Kernel.EPSILON)
				d1.addHiddenPart(d2.getHidingPart());
			else if (dZ < -Kernel.EPSILON)
				d2.addHiddenPart(d1.getHidingPart());			
		}
		
	}
	
	
}
