package zhupff.gadgets.theme.compile.common

import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.ParameterSpec

internal val ps_AnyRes = ParameterSpec.builder("id", INT)
    .addAnnotation(cn_AnyRes)
    .build()