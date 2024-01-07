package zhupf.gadget.widget

@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class LayoutManagerDsl(
    val alias: String,
    val qualifiedName: String = "",
)