#
# Proguard config file for GeoGebra
# Special version for building plugins
#

### you want to keep your plugin's methods
-keep class JMathTeX_Plugin { <methods>; }

### choose one of these, depending upon which version of Java your plugin requires
#-libraryjars ../java142-rt.jar
-libraryjars ../java160-rt.jar

### the name of your plugin
-injars ../plugin.jar

### any library JARs your plugin requires
-libraryjars ../JMathTeX-0.7pre.jar
-libraryjars ../jdom-1.1.jar

-injars ../build/geogebra.jar
-injars ../build/geogebra_export.jar
-outjars ../build/temp

-libraryjars netscape_javascript.jar
-libraryjars AppleJavaExtensions.jar

-dontoptimize
-allowaccessmodification
-overloadaggressively

-applymapping geogebra3180.map

# Keep - Applications. Keep all application classes that have a main method.
-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}

-keep class geogebra.GeoGebraApplet {
    <methods>;
}

-keep class geogebra.GeoGebraAppletBase {
    <methods>;
}

# Jasymca uses reflection to create functions like LambaSIN
-keep class jasymca.Lambda* {}

# Export classes called using reflection
-keep class geogebra.export.ConstructionProtocolExportDialog { <methods>; }
-keep class geogebra.export.WorksheetExportDialog { <methods>; }
-keep class geogebra.export.PrintPreview { <methods>; }
-keep class geogebra.export.GraphicExportDialog { <methods>; }
-keep class geogebra.export.pstricks.GeoGebraToPstricks { <methods>; }

# see META-INF/services
-keep class org.freehep.graphicsio.raw.RawImageWriterSpi { <methods>; }

#####
# Plugin part
####

# -keep public class * {
#    public protected *;
# }

-keep class geogebra.plugin.PlugLetIF { <methods>; }
-keep class geogebra.plugin.GgbAPI { <methods>; }

-keep class geogebra.Application { <methods>; }
-keep class geogebra.kernel.Construction { <methods>; }
-keep class geogebra.kernel.ConstructionElement { <methods>; }
-keep class geogebra.kernel.AlgoElement { <methods>; }
-keep class geogebra.kernel.arithmetic.Equation { <methods>; }
-keep class geogebra.kernel.arithmetic.ExpressionNode { <methods>; }
-keep class geogebra.kernel.arithmetic.ExpressionValue { <methods>; }
-keep class geogebra.kernel.arithmetic.Function { <methods>; }
-keep class geogebra.kernel.arithmetic.NumberValue { <methods>; }
-keep class geogebra.kernel.Dilateable { <methods>; }
-keep class geogebra.kernel.GeoBoolean { <methods>; }
-keep class geogebra.kernel.GeoConic { <methods>; }
-keep class geogebra.kernel.GeoCurveCartesian { <methods>; }
-keep class geogebra.kernel.GeoDeriveable { <methods>; }
-keep class geogebra.kernel.GeoElement { <methods>; }
-keep class geogebra.kernel.GeoFunction { <methods>; }
-keep class geogebra.kernel.GeoImage { <methods>; }
-keep class geogebra.kernel.GeoLine { <methods>; }
-keep class geogebra.kernel.GeoList { <methods>; }
-keep class geogebra.kernel.GeoNumeric { <methods>; }
-keep class geogebra.kernel.GeoPoint { <methods>; }
-keep class geogebra.kernel.GeoPolygon { <methods>; }
-keep class geogebra.kernel.GeoSegment { <methods>; }
-keep class geogebra.kernel.GeoText { <methods>; }
-keep class geogebra.kernel.GeoVec3D { <methods>; }
-keep class geogebra.kernel.GeoVector { <methods>; }
-keep class geogebra.kernel.Kernel { <methods>; }
-keep class geogebra.kernel.Macro { <methods>; }
-keep class geogebra.kernel.Mirrorable { <methods>; }
-keep class geogebra.kernel.Path { <methods>; }
-keep class geogebra.kernel.PointRotateable { <methods>; }
-keep class geogebra.kernel.Rotateable { <methods>; }
-keep class geogebra.kernel.Translateable { <methods>; }
-keep class org.freehep.graphics2d.TagString { <methods>; }
