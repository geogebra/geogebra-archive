#! /bin/bash

for I in ../euclidian/EuclidianView.java; do
echo "Fichier : $I"
sed 's/public final class EuclidianView/public class EuclidianView/' $I > temp ; cp temp $I
sed 's/final public void add(GeoElement geo)/public void add(GeoElement geo)/' $I > temp ; cp temp $I
sed 's/final Drawable createDrawable(GeoElement geo)/protected Drawable createDrawable(GeoElement geo)/' $I > temp ; cp temp $I
sed 's/final private void addToDrawableLists(Drawable d)/protected void addToDrawableLists(Drawable d)/' $I > temp ; cp temp $I
sed 's/final Graphics2D getBackgroundGraphics()/final public Graphics2D getBackgroundGraphics()/' $I > temp ; cp temp $I 
sed 's/private/protected/' $I > temp ; cp temp $I
sed 's/void setMoveCursor()/public void setMoveCursor()/' $I > temp ; cp temp $I
sed 's/final void updateBackgroundImage()/final protected void updateBackgroundImage()/' $I > temp ; cp temp $I 
sed 's/protected protected/protected/' $I > temp ; cp temp $I
done 

rm temp