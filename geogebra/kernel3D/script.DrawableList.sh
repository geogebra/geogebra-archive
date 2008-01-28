#! /bin/bash

for I in ../euclidian/DrawableList.java; do
echo "Fichier : $I"
sed 's/final void add(Drawable d)/public final void add(Drawable d)/' $I > temp ; cp temp $I
sed 's/final void drawAll(Graphics2D g2)/public final void drawAll(Graphics2D g2)/' $I > temp ; cp temp $I
#sed 's/protected protected/protected/' $I > temp ; cp temp $I
done 

rm temp