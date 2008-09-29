#
# Proguard config file for GeoGebra
#
# Sept 28th 2008
#
-injars ../build/geogebra.jar
-injars ../build/geogebra_gui.jar
-injars ../build/geogebra_export.jar
-injars ../build/geogebra_cas.jar

-outjars ../build/temp

-libraryjars ../java142-rt.jar
-libraryjars netscape_javascript.jar
-libraryjars AppleJavaExtensions.jar

-dontoptimize
-allowaccessmodification
-overloadaggressively

# Keep - Applications. Keep all application classes that have a main method.
-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}

-keep class geogebra.GeoGebraApplet {
    public <methods>;
}

-keep class geogebra.GeoGebraAppletBase {
    public <methods>;
}

# Jasymca uses reflection to create functions like LambaSIN
-keep class jasymca.Lambda* {}

# see META-INF/services
-keep class org.freehep.graphicsio.raw.RawImageWriterSpi { <methods>; }

# needed so that SymbolLoader can find Des12.gif etc
-keep class geogebra.gui.hoteqn.SymbolLoader { <methods>; }

# needed so that MenuBarImpl can find _license.txt
-keep class geogebra.gui.menubar.MenubarImpl { <methods>; }


#####
# Plugin part
####

-keep class geogebra.plugin.GgbAPI { <methods>; }

# -keep public class * {
#    public protected *;
# }

#-keep class geogebra.gui.util.BrowserLauncher { <methods>; }
#-keep class geogebra.plugin.PlugLetIF { <methods>; }

#-keep class geogebra.MyFileFilter { <methods>; }

#-keep class geogebra.Application { <methods>; }
#-keep class geogebra.kernel.Construction { <methods>; }
#-keep class geogebra.kernel.ConstructionElement { <methods>; }
#-keep class geogebra.kernel.AlgoElement { <methods>; }
#-keep class geogebra.kernel.arithmetic.Equation { <methods>; }
#-keep class geogebra.kernel.arithmetic.ExpressionNode { <methods>; }
#-keep class geogebra.kernel.arithmetic.ExpressionValue { <methods>; }
#-keep class geogebra.kernel.arithmetic.Function { <methods>; }
#-keep class geogebra.kernel.arithmetic.NumberValue { <methods>; }
#-keep class geogebra.kernel.Dilateable { <methods>; }
#-keep class geogebra.kernel.GeoBoolean { <methods>; }
#-keep class geogebra.kernel.GeoConic { <methods>; }
#-keep class geogebra.kernel.GeoCurveCartesian { <methods>; }
#-keep class geogebra.kernel.GeoDeriveable { <methods>; }
#-keep class geogebra.kernel.GeoElement { <methods>; }
#-keep class geogebra.kernel.GeoFunction { <methods>; }
#-keep class geogebra.kernel.GeoImage { <methods>; }
#-keep class geogebra.kernel.GeoLine { <methods>; }
#-keep class geogebra.kernel.GeoList { <methods>; }
#-keep class geogebra.kernel.GeoNumeric { <methods>; }
#-keep class geogebra.kernel.GeoPoint { <methods>; }
#-keep class geogebra.kernel.GeoPolygon { <methods>; }
#-keep class geogebra.kernel.GeoSegment { <methods>; }
#-keep class geogebra.kernel.GeoText { <methods>; }
#-keep class geogebra.kernel.GeoVec3D { <methods>; }
#-keep class geogebra.kernel.GeoVector { <methods>; }
#-keep class geogebra.kernel.Kernel { <methods>; }
#-keep class geogebra.kernel.Macro { <methods>; }
#-keep class geogebra.kernel.Mirrorable { <methods>; }
#-keep class geogebra.kernel.Path { <methods>; }
#-keep class geogebra.kernel.PointRotateable { <methods>; }
#-keep class geogebra.kernel.Rotateable { <methods>; }
#-keep class geogebra.kernel.Translateable { <methods>; }
#-keep class org.freehep.graphics2d.TagString { <methods>; }
