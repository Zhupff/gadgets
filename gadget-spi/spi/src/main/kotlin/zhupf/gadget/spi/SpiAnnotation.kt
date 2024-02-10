package zhupf.gadget.spi

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class SpiAnnotation(
    vararg val cls: KClass<*>,
)