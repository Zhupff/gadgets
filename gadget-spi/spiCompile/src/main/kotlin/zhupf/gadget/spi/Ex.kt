package zhupf.gadget.spi

import com.google.devtools.ksp.symbol.KSDeclaration


internal val KSDeclaration.declarePackageName: String; get() = "${packageName.getQualifier()}.${packageName.getShortName()}"

internal val KSDeclaration.declareClassName: String; get() {
    var name = simpleName.getShortName()
    var declaration = parentDeclaration
    while (declaration != null) {
        name = "${declaration.simpleName.getShortName()}.$name"
        declaration = declaration.parentDeclaration
    }
    return name
}

internal val KSDeclaration.declareQualifiedName: String; get() = "$declarePackageName.$declareClassName"