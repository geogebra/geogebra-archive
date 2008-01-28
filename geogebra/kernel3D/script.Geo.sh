#! /bin/bash

for I in ../kernel/Geo*.java; do
echo "Fichier : $I"
sed 's/String getClassName()/protected String getClassName()/' $I > temp ; cp temp $I
sed 's/String getTypeString()/protected String getTypeString()/' $I > temp ; cp temp $I
sed 's/void doRemove()/protected void doRemove()/' $I > temp ; cp temp $I
sed 's/boolean showInAlgebraView()/protected boolean showInAlgebraView()/' $I > temp ; cp temp $I
sed 's/boolean showInEuclidianView()/protected boolean showInEuclidianView()/' $I > temp ; cp temp $I
sed 's/String getXMLtags()/protected String getXMLtags()/' $I > temp ; cp temp $I
sed 's/public protected/public/' $I > temp ; cp temp $I
sed 's/protected protected/protected/' $I > temp ; cp temp $I
done 

rm temp