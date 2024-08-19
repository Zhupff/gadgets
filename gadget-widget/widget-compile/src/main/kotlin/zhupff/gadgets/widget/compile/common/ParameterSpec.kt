package zhupff.gadgets.widget.compile.common

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.asClassName

internal val ps_Context = ParameterSpec("context", cn_Context)

internal val ps_IdRes = ParameterSpec.builder("id", INT)
    .addAnnotation(cn_IdRes)
    .defaultValue("View.NO_ID")
    .build()

internal val ps_LayoutParams: ParameterSpec = ParameterSpec.builder("layoutParams", cn_LayoutParams).build()

internal val ps_size = ParameterSpec.builder("size", Pair::class.asClassName().parameterizedBy(INT, INT))
    .build()

internal val ps_index: ParameterSpec = ParameterSpec.builder("index", INT)
    .defaultValue("-1")
    .build()

internal fun ps_block(cnItem: ClassName): ParameterSpec = ParameterSpec.builder("block",
    LambdaTypeName.get(
        receiver = cnItem
            .copy(annotations = listOf(
                AnnotationSpec.builder(cn_WidgetDslScope).build()
            )),
        parameters = listOf(
            ParameterSpec.unnamed(cnItem)
        ),
        returnType = UNIT
    )
).build()