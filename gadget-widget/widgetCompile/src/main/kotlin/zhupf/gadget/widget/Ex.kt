package zhupf.gadget.widget

import com.google.devtools.ksp.symbol.KSDeclaration


val KSDeclaration.declarePackageName: String; get() = "${packageName.getQualifier()}.${packageName.getShortName()}"

val KSDeclaration.declareClassName: String; get() {
    var name = simpleName.getShortName()
    var declaration = parentDeclaration
    while (declaration != null) {
        name = "${declaration.simpleName.getShortName()}.$name"
        declaration = declaration.parentDeclaration
    }
    return name
}

val KSDeclaration.declareQualifiedName: String; get() = "$declarePackageName.$declareClassName"

inline fun <T> T.alsoIf(condition: Boolean?, block: (T) -> Unit): T {
    if (condition == true) return this.also(block)
    return this
}