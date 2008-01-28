#! /bin/bash

for I in ../kernel/ConstructionElement.java; do
echo "Fichier : $I"
sed 's/String getClassName()/protected String getClassName()/' $I > temp ; cp temp $I
sed 's/transient Construction cons/protected transient Construction cons/' $I > temp ; cp temp $I
sed 's/transient Kernel kernel/protected transient Kernel kernel/' $I > temp ; cp temp $I
sed 's/transient Application app/protected transient Application app/' $I > temp ; cp temp $I
sed 's/protected protected/protected/' $I > temp ; cp temp $I
done 

rm temp