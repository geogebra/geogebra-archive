package geogebra.gui.hoteqn;

import java.awt.Color;
import java.awt.image.RGBImageFilter;

//*************************************************************
//** Filter-Class, die als Rückgabewert das Pixel veraendert **
//** mit mask wird der RGB-Farbwert rrggbb vorgegeben, der   **
//** den Farbwert schwarz ersetzt.                           **
class ColorMaskFilter extends RGBImageFilter {
Color color;
boolean maskORinvert = false;

//Filter for normal Image
ColorMaskFilter (Color mask) {
  color = mask;
  maskORinvert = false;
  canFilterIndexColorModel = true;
}

//Filter for highlight
ColorMaskFilter (Color mask, boolean maskB) {
  color = mask;
  maskORinvert = maskB;
  canFilterIndexColorModel = true;
}

public int filterRGB(int x, int y, int pixel) {
  if (maskORinvert)  return 0x1fff0000;  // rot transparent
  int p = pixel & 0xffffff;
  if (p == 0xffffff) {return p;} else {return 255 << 24 | color.getRGB();} 
}

} // end ColorMaskFilter