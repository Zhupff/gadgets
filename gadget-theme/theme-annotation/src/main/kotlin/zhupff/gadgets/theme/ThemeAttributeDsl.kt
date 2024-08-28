package zhupff.gadgets.theme

@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class ThemeAttributeDsl(
    val alias: String,
)