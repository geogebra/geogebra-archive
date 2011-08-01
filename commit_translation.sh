#!/bin/sh
cd geogebra/properties
svn add *.properties --force
svn status |sed 's/.*_\(..\).*/\1/' | sort | uniq | tr '\n' ',' |sed 's/\(.*\),/svn ci -m "Translation update \(\1\)."/'|sh



