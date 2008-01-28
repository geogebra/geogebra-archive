#! /bin/bash

for I in ../euclidian/Drawable.java; do
echo "Fichier : $I"
sed 's/EuclidianView view;/protected EuclidianView view;/' $I > temp ; cp temp $I
sed 's/GeoElement geo;/protected GeoElement geo;/' $I > temp ; cp temp $I
sed 's/boolean isTracing = false;/protected boolean isTracing = false;/' $I > temp ; cp temp $I
sed 's/String labelDesc;/protected String labelDesc;/' $I > temp ; cp temp $I
sed 's/final boolean addLabelOffset()/final protected boolean addLabelOffset()/' $I > temp ; cp temp $I
sed 's/final void drawLabel(Graphics2D g2)/final protected void drawLabel(Graphics2D g2)/' $I > temp ; cp temp $I
sed 's/protected protected/protected/' $I > temp ; cp temp $I
done 

rm temp