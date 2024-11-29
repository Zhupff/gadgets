package zhupff.gadgets.theme

class ThemeInjectExtension : (Array<String>) -> Unit {
    var prefix: String = ""
    var variant: String = ""
    val variantPrefix: String; get() = variant + prefix

    override fun invoke(args: Array<String>) {
        prefix = args[0]
        variant = args[1]
    }
}