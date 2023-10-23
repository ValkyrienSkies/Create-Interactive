#noinspection ShrinkerUnresolvedReference
-ignorewarnings
-dontoptimize
-dontshrink


-keepattributes
InnerClasses,
Signature,
RuntimeVisibleAnnotations,
RuntimeVisibleParameterAnnotations,
RuntimeVisibleTypeAnnotations,
EnclosingMethod,
AnnotationDefault,
MethodParameters

-keepparameternames


-dontnote
org.joml.**,
org.apache.**,
io.netty.**,
kotlin.reflect.**,
kotlin.coroutines.**,
kotlinx.coroutines.**,
com.google.common.**,
com.fasterxml.jackson.**,
com.github.victools.**

-dontwarn
org.joml.**,
org.apache.**,
io.netty.**,
kotlin.reflect.**,
kotlin.coroutines.**,
kotlinx.coroutines.**,
com.google.common.**,
com.fasterxml.jackson.**,
com.github.victools.**

# https://stackoverflow.com/questions/33189249/how-to-tell-proguard-to-keep-enum-constants-and-fields
-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep annotations
-keepattributes RuntimeInvisibleAnnotations

# Keep the forge mixin plugin names the same
-keep,allowshrinking,allowoptimization class org.valkyrienskies.create_interactive.forge.mixin.ValkyrienForgeMixinConfigPlugin { *; }

# Keep mixin class names the same
-keepnames class org.valkyrienskies.create_interactive.mixin.*
-keepnames class org.valkyrienskies.create_interactive.mixin.client.*
-keepnames class org.valkyrienskies.create_interactive.mixin.client.actor.*
-keepnames class org.valkyrienskies.create_interactive.fabric.mixin.*
-keepnames class org.valkyrienskies.create_interactive.forge.mixin.*

# Keep mixin field names the same
-keepclassmembers class org.valkyrienskies.create_interactive.mixin.* { <fields>; }
-keepclassmembers class org.valkyrienskies.create_interactive.mixin.client.* { <fields>; }
-keepclassmembers class org.valkyrienskies.create_interactive.mixin.client.actor.* { <fields>; }
-keepclassmembers class org.valkyrienskies.create_interactive.fabric.mixin.* { <fields>; }
-keepclassmembers class org.valkyrienskies.create_interactive.forge.mixin.* { <fields>; }

# Keep some mixin method names the same (Anything that shadows a method)
-keepclassmembers class org.valkyrienskies.create_interactive.fabric.mixin.MixinCombinedInvWrapper { *; }
-keepclassmembers class org.valkyrienskies.create_interactive.fabric.mixin.MixinCombinedTankWrapper { *; }
-keepclassmembers class org.valkyrienskies.create_interactive.mixin.client.MixinMinecraft { *; }
-keepclassmembers class org.valkyrienskies.create_interactive.mixin.MixinContraption { *; }

-keep class !org.vlakyrienksies.createinteractive.** { *; }

