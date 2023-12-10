package zhupf.gadget.theme

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View

open class ThemeFactory(
    private val factory: LayoutInflater.Factory2?,
    private val config: ThemeConfig?,
) : LayoutInflater.Factory2 {

    constructor() : this(null, null)

    constructor(factory: LayoutInflater.Factory2?) : this(factory, null)

    constructor(config: ThemeConfig?) : this(null, config)

    final override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
        val themeAttributes = parseThemeAttributes(name, context, attrs)
        val view = onCreateView(parent, name, context, attrs, themeAttributes)
        bindThemeObject(view, themeAttributes)
        return view
    }

    final override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        val themeAttributes = parseThemeAttributes(name, context, attrs)
        val view = onCreateView(name, context, attrs, themeAttributes)
        bindThemeObject(view, themeAttributes)
        return view
    }

    fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet, themeAttributes: List<ThemeAttribute>?): View? =
        if (factory is ThemeFactory) {
            factory.onCreateView(parent, name, context, attrs, themeAttributes)
        } else {
            factory?.onCreateView(parent, name, context, attrs)
        } ?: createView(parent, name, context, attrs, themeAttributes)

    fun onCreateView(name: String, context: Context, attrs: AttributeSet, themeAttributes: List<ThemeAttribute>?): View? =
        if (factory is ThemeFactory) {
            factory.onCreateView(name, context, attrs, themeAttributes)
        } else {
            factory?.onCreateView(name, context, attrs)
        } ?: createView(null, name, context, attrs, themeAttributes)

    protected open fun createView(parent: View?, name: String, context: Context, attrs: AttributeSet, themeAttributes: List<ThemeAttribute>?): View? {
        if (themeAttributes.isNullOrEmpty()) return null
        return try {
            val layoutInflater = LayoutInflater.from(context)
            if (!name.contains('.'))
                layoutInflater.createView(name, "android.widget.", attrs)
                ?: layoutInflater.createView(name, "android.view.", attrs)
                ?: layoutInflater.createView(name, "android.webkit.", attrs)
            else
                layoutInflater.createView(name, null, attrs)
        } catch (e: Exception) {
            null
        }
    }

    protected open fun parseThemeAttributes(name: String, context: Context, attrs: AttributeSet): MutableList<ThemeAttribute>? {
        var themeAttributes: ArrayList<ThemeAttribute>? = null
        if (config != null && config.prefix.isNotEmpty() && config.attributes.isNotEmpty()) {
            for (index in 0 until attrs.attributeCount) {
                val attributeName = attrs.getAttributeName(index)
                val attributeValue = attrs.getAttributeValue(index)
                if (!attributeValue.startsWith('@')) continue
                val resourceId = attributeValue.substring(1).toIntOrNull() ?: continue
                val resourceName = context.resources.getResourceEntryName(resourceId)
                if (!resourceName.startsWith(config.prefix)) continue
                val resourceType = context.resources.getResourceTypeName(resourceId)
                val attribute = config.obtainAttribute(attributeName, resourceId, resourceName, resourceType)
                if (attribute != null) {
                    if (themeAttributes == null) {
                        themeAttributes = ArrayList(2)
                    }
                    themeAttributes.add(attribute)
                }
            }
        }
        return themeAttributes
    }

    protected open fun bindThemeObject(view: View?, themeAttributes: MutableList<ThemeAttribute>?) {
        if (view != null && !themeAttributes.isNullOrEmpty()) {
            ThemeObject.bind(view).addThemeAttributes(themeAttributes)
        }
    }
}