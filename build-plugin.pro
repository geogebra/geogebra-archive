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

-applymapping geogebra3130.map

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
-keep class geogebra.export.* { <methods>; }
-keep class org.freehep.* { <methods>; }


-keep class geogebra.plugin.PlugLetIF { <methods>; }
-keep class geogebra.plugin.GgbAPI { <methods>; }

#####
# Plugin part
####

# -keep public class * {
#    public protected *;
# }
