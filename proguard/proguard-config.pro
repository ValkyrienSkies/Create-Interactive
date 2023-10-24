-obfuscate-strings class !**mixin** { *; }
-obfuscate-control-flow class * { *; }
-obfuscate-constants class !**mixin** { *; }
-obfuscate-arithmetic,medium class * { *;}

-packageobfuscationdictionary windows.txt
-classobfuscationdictionary classnames.txt
-obfuscationdictionary keywords.txt

#noinspection ShrinkerUnresolvedReference
-ignorewarnings
#-dontobfuscate
#-dontoptimize
#-dontshrink

-overloadaggressively

# https://stackoverflow.com/questions/33189249/how-to-tell-proguard-to-keep-enum-constants-and-fields
-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep annotations
-keepattributes RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations

# Keep the main entrypoints
-keep,allowobfuscation class
org.valkyrienskies.create_interactive.forge.mixin.ValkyrienForgeMixinConfigPlugin,
org.valkyrienskies.create_interactive.forge.CreateInteractiveModForge
{ *; }

# Keep all mixins as entrypoints, but obfuscating is OK
-keep,allowobfuscation @org.spongepowered.asm.mixin.Mixin class * { *; }

# ok, don't obfuscate the class name though
-keep @org.spongepowered.asm.mixin.Mixin class *


# Keep shadowed field/methods names the same
-keepclassmembers class * {
    @org.spongepowered.asm.mixin.Shadow <fields>;
    @org.spongepowered.asm.mixin.Shadow <methods>;
}

# Obfuscate mixin class names in the mixins json file
-adaptresourcefilecontents **.mixins.json**


# Dont obfuscate library classes
-keep class !org.valkyrienskies.create_interactive.** { *; }

# repackage everything into one big class
-repackageclasses org.valkyrienskies.create_interactive.aux


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
