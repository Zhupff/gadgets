package zhupff.gadgets.widget

@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class WidgetDsl(
    val alias: String,
)