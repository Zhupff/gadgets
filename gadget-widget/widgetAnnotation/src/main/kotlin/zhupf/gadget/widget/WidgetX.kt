package zhupf.gadget.widget

@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class WidgetX(
    val name: String,
    val cornerClip: Boolean,
    val windowFit: Boolean,
)