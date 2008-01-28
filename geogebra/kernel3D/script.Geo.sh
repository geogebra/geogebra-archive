#! /bin/bash

for I in ../kernel/Geo*.java; do
echo "Fichier : $I"
sed 's/String getClassName()/protected String getClassName()/' $I > temp ; cp temp $I
sed 's/public protected/public/' $I > temp ; cp temp $I
sed 's/protected protected/protected/' $I > temp ; cp temp $I
done 

rm temp