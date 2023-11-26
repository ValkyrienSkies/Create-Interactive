package org.valkyrienskies.create_interactive.services

/**
 * Use this annotation to tell proguard pretty please do not delete my function!
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class NoOptimize
