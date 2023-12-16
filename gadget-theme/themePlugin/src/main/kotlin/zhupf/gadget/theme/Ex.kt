package zhupf.gadget.theme

val com.android.build.gradle.api.BaseVariant.variantName: String; get() = name

fun String.toCamelCase(): String = replaceFirstChar { it.uppercaseChar() }