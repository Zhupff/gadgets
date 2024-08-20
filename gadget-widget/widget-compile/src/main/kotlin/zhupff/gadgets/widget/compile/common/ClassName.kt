package zhupff.gadgets.widget.compile.common

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import zhupff.gadgets.widget.DslScope

internal val cn_View: ClassName = ClassName("android.view", "View")

internal val cn_ViewGroup: ClassName = ClassName("android.view", "ViewGroup")

internal val cn_LayoutParams: ClassName = ClassName("", "android.view.ViewGroup.LayoutParams")

internal val cn_Context: ClassName = ClassName("android.content", "Context")

internal val cn_IdRes: ClassName = ClassName("androidx.annotation", "IdRes")

internal val cn_DslScope: ClassName = DslScope::class.asClassName()