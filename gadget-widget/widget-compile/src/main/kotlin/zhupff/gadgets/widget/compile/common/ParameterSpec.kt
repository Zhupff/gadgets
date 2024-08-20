package zhupff.gadgets.widget.compile.common

import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.ParameterSpec

internal val ps_Context = ParameterSpec("context", cn_Context)

internal val ps_IdRes = ParameterSpec.builder("id", INT)
    .addAnnotation(cn_IdRes)
    .defaultValue("View.NO_ID")
    .build()

internal val ps_LayoutParams: ParameterSpec = ParameterSpec.builder("layoutParams", cn_LayoutParams).build()