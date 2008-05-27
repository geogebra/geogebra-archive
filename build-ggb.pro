#
# Proguard config file for GeoGebra
#
-injars ../build/geogebra.jar
-injars ../build/geogebra_export.jar
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


#####
# Plugin part
####

 -keep public class * {
    public protected *;
 }

# -keepclassmembernames class * {
#    java.lang.Class class$(java.lang.String);
#    java.lang.Class class$(java.lang.String, boolean);
# }

# -keepclasseswithmembernames class * {
#    native <methods>;
# }