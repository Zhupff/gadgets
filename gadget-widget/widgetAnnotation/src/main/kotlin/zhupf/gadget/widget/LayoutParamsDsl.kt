package zhupf.gadget.widget

@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.SOURCE)
annotation class LayoutParamsDsl(
    val alias: String,
)