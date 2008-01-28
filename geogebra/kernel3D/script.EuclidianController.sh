#! /bin/bash

for I in ../euclidian/EuclidianController.java; do
echo "Fichier : $I"
sed 's/final public class EuclidianController/public class EuclidianController/' $I > temp ; cp temp $I
sed 's/private/protected/' $I > temp ; cp temp $I
sed 's/Point startLoc, mouseLoc, lastMouseLoc;/protected Point startLoc, mouseLoc, lastMouseLoc;/' $I > temp ; cp temp $I
sed 's/final public void mousePressed(MouseEvent e)/public void mousePressed(MouseEvent e)/' $I > temp ; cp temp $I
sed 's/protected protected/protected/' $I > temp ; cp temp $I
done 

rm temp