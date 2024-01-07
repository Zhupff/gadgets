package zhupf.gadget.widget

@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class WidgetX(
    val alias: String,
    val qualifiedName: String = "",
    val cornerClip: Boolean = false,
    val windowFit: Boolean = false,
)