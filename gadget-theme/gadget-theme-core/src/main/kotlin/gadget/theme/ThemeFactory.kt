package gadget.theme

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View

/**
 * 主题解析工厂，拦截[LayoutInflater]的接口，解析主题属性和资源。
 */
abstract class ThemeFactory(
    protected val factory: LayoutInflater.Factory?,
) : LayoutInflater.Factory2 {

    protected val factory2: LayoutInflater.Factory2? = factory as? LayoutInflater.Factory2
    /** 对曾经解析过的主题资源进行缓存。 */
    protected val resourceCaches: MutableMap<Int, Theme.Resource> = HashMap()
    /** 对曾经解析过的主题属性进行缓存。 */
    protected val attributeCaches: MutableMap<String, Theme.Attribute> = HashMap()

    /**
     * 将该Factory通过反射的方式注入到目标[LayoutInflater]中。
     */
    open fun inject(target: Any): Boolean {
        val layoutInflater: LayoutInflater = when (target) {
            is Context -> target.getSystemService(LayoutInflater::class.java)
            is LayoutInflater -> target
            else -> throw IllegalStateException("Not support $target!")
        }
        try {
            val mFactory = LayoutInflater::class.java.getDeclaredField("mFactory")
            mFactory.isAccessible = true
            mFactory.set(layoutInflater, this)
            val mFactory2 = LayoutInflater::class.java.getDeclaredField("mFactory2")
            mFactory2.isAccessible = true
            mFactory2.set(layoutInflater, this)
            return true
        } catch (throwable: Throwable) {
            return false
        }
    }

    /**
     * 解析xml中使用到的主题资源和属性
     */
    open fun parse(context: Context, attrs: AttributeSet): MutableMap<Theme.Attribute, Theme.Resource>? {
        var result: MutableMap<Theme.Attribute, Theme.Resource>? = null
        for (index in 0 until attrs.attributeCount) {
            val attributeName = attrs.getAttributeName(index) ?: continue
            if (attrs.getAttributeNameResource(index) == gadget.theme.core.R.attr.gadget_theme_flags) {
                val flags = attrs.getAttributeValue(index).substring(2).toIntOrNull(16) ?: continue
                if (result == null) {
                    result = HashMap()
                }
                result[Theme.Attribute.Flags] = Theme.Resource(flags, "", "")
            } else {
                val attribute = attributeCaches.getOrPut(attributeName) {
                    provide(attributeName) ?: Theme.Attribute.NotSupport
                }
                if (attribute === Theme.Attribute.NotSupport) {
                    continue
                }
                val attributeValue = attrs.getAttributeValue(index) ?: continue
                val resourceId = if (attributeValue.startsWith('@')) {
                    attributeValue.substring(1).toIntOrNull()
                } else { null } ?: continue
                val resource = resourceCaches.getOrPut(resourceId) {
                    try {
                        val resourceName = context.resources.getResourceEntryName(resourceId)
                        val resourceType = context.resources.getResourceTypeName(resourceId)
                        Theme.Resource(resourceId, resourceName, resourceType).let {
                            if (filter(it)) it else Theme.Resource.NotFound
                        }
                    } catch (throwable: Throwable) {
                        Theme.Resource.NotFound
                    }
                }
                if (resource === Theme.Resource.NotFound) {
                    continue
                }
                if (result == null) {
                    result = HashMap()
                }
                result[attribute] = resource
            }
        }
        return result;
    }

    /**
     * 对解析到的疑似和主题相关的资源进行判断，返回true表示该资源有效，返回false会跳过该资源的处理。
     */
    abstract fun filter(resource: Theme.Resource): Boolean

    /**
     * 根据属性名提供主题属性。
     */
    abstract fun provide(attributeName: String): Theme.Attribute?

    /**
     * 手动创建一个View。
     */
    open fun create(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
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

    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
        var view = factory2?.onCreateView(parent, name, context, attrs)
            ?: factory?.onCreateView(name, context, attrs)
        val attribute2resource = parse(context, attrs) ?: return view
        if (view == null) {
            view = create(parent, name, context, attrs)
        }
        if (view != null) {
            val flags = attribute2resource.remove(Theme.Attribute.Flags)?.id ?: 0
            view = ThemeObserver(view, attribute2resource, flags).get()
        }
        return view
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        var view = factory?.onCreateView(name, context, attrs)
        val attribute2resource = parse(context, attrs) ?: return view
        if (view == null) {
            view = create(null, name, context, attrs)
        }
        if (view != null) {
            val flags = attribute2resource.remove(Theme.Attribute.Flags)?.id ?: 0
            view = ThemeObserver(view, attribute2resource, flags).get()
        }
        return view
    }
}