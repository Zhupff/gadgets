package zhupff.gadgets.widget.compile.common

import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.asClassName

internal val ps_Context = ParameterSpec("context", cn_Context)

internal val ps_IdRes = ParameterSpec.builder("id", INT)
    .addAnnotation(cn_IdRes)
    .defaultValue("View.NO_ID")
    .build()

internal val ps_IdString = ParameterSpec("id", STRING)

internal val ps_Size = ParameterSpec.builder("size", Pair::class.asClassName().parameterizedBy(
    INT, INT
))
    .defaultValue("ViewGroup.LayoutParams.MATCH_PARENT to ViewGroup.LayoutParams.MATCH_PARENT")
    .build()

internal val ps_LayoutParams: ParameterSpec = ParameterSpec.builder("layoutParams", cn_LayoutParams)
    .defaultValue("ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)")
    .build()

internal val ps_Index: ParameterSpec = ParameterSpec.builder("index", INT)
    .defaultValue("-1")
    .build()