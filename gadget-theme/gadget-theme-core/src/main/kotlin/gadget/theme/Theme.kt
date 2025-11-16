package gadget.theme

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.res.ResourcesCompat

/**
 * 主题基类，提供对应的主题资源。
 */
abstract class Theme @JvmOverloads constructor(
    /** 主题ID，期望是唯一值。 */
    val id: String,
    /** 父主题，当前主题拿不到资源时，可以尝试用父主题的资源。 */
    protected val parent: Theme? = null,
) {

    open fun getColorInt(r: Resource): Int {
        return parent?.getColorInt(r) ?: throw IllegalStateException("Not implement yet!")
    }

    open fun getColorState(r: Resource): ColorStateList? {
        return parent?.getColorState(r) ?: throw IllegalStateException("Not implement yet!")
    }

    open fun getDrawable(r: Resource): Drawable? {
        return parent?.getDrawable(r) ?: throw IllegalStateException("Not implement yet!")
    }

    open fun getString(r: Resource, vararg args: Any): String? {
        return parent?.getString(r, *args) ?: throw IllegalStateException("Not implement yet!")
    }


    /**
     * 主题属性，原型是按照View体系的属性设计的，可按照实际的使用场景赋予其不同的含义。
     */
    abstract class Attribute(
        /** 属性名[android.util.AttributeSet.getAttributeName] */
        val name: String,
    ) {
        companion object NotSupport : Attribute("NotSupport!") {
            override fun apply(view: View, theme: Theme, resource: Resource) {
                throw IllegalStateException("NotSupport!")
            }
        }

        /**
         * 对视图[view]的当前属性使用主题[theme]里的资源[resource]进行改变。
         */
        abstract fun apply(view: View, theme: Theme, resource: Resource)

        override fun hashCode(): Int = name.hashCode()

        override fun equals(other: Any?): Boolean = other is Attribute && this.name == other.name
    }



    /**
     * 主题资源三元组，原型是按照View体系的资源设计的，可按照实际的使用场景赋予其不同的含义。
     */
    open class Resource(
        /** 资源id。 */
        val id: Int,
        /** 资源名[android.content.res.Resources.getResourceEntryName]。 */
        val name: String,
        /** 资源名[android.content.res.Resources.getResourceTypeName]。 */
        val type: String,
    ) {
        companion object NotFound : Resource(ResourcesCompat.ID_NULL, "NotFound!", "NotFound!")

        override fun hashCode(): Int = id

        override fun equals(other: Any?): Boolean = other is Resource && this.id == other.id
    }
}