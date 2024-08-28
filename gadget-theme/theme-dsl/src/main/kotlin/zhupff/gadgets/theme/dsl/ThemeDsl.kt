package zhupff.gadgets.theme.dsl

import android.view.View
import androidx.annotation.AnyRes
import zhupff.gadgets.theme.DslScope
import zhupff.gadgets.theme.ThemeAttribute
import zhupff.gadgets.theme.ThemeObject


val Int.resourceEntryName: String; get() = RESOURCES.getResourceEntryName(this)
val Int.resourceTypeName: String; get() = RESOURCES.getResourceTypeName(this)

inline fun <V : View> V.theme(
    block: (@DslScope ThemeObject).() -> Unit = {},
): ThemeObject {
    val themeObject = ThemeObject.bind(this)
    themeObject.block()
    return themeObject
}

fun <A : ThemeAttribute> A.initialize(
    @AnyRes id: Int,
): A = apply {
    this.set(id, id.resourceEntryName, id.resourceTypeName)
}