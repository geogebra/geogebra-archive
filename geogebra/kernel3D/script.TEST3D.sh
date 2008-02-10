#! /bin/bash

#################################################################################
# run this script to test the 3D packages
# it will modify Application.java
# to undo just replace Application.java by the one provided by the cvs repository
# Mathieu Blossier
#################################################################################


for I in ../Application.java; do
echo "Fichier : $I"
sed 's,//Mathieu Blossier - place for code to test 3D packages,new geogebra.kernel3D.Kernel3D().test(kernel);//Mathieu Blossier - place for code to test 3D packages,' $I > temp ; cp temp $I
sed 's/private EuclidianView euclidianView/private geogebra.euclidian3D.EuclidianView3D euclidianView/' $I > temp ; cp temp $I
sed 's/euclidianView = new EuclidianView(euclidianController, showAxes, showGrid);/euclidianView = new geogebra.euclidian3D.EuclidianView3D(euclidianController, showAxes, showGrid);/' $I > temp ; cp temp $I
sed 's/private EuclidianController euclidianController/private geogebra.euclidian3D.EuclidianController3D euclidianController;/' $I > temp ; cp temp $I
sed 's/euclidianController = new EuclidianController(kernel);/euclidianController = new geogebra.euclidian3D.EuclidianController3D(kernel);/' $I > temp ; cp temp $I        
done 

rm temp