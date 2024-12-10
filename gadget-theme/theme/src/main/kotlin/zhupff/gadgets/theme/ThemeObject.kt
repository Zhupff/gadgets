package zhupff.gadgets.theme

import android.app.Activity
import android.content.ContextWrapper
import android.view.View
import androidx.collection.ArrayMap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import java.util.LinkedList

class ThemeObject private constructor(val view: View) : View.OnAttachStateChangeListener, Observer<Theme> {

    companion object {
        @JvmField
        val TAG_ID = R.id.gadgets_theme_object

        @JvmStatic
        fun get(view: View): ThemeObject? = view.getTag(TAG_ID) as? ThemeObject

        @JvmStatic
        fun bind(view: View): ThemeObject {
            val tag = view.getTag(TAG_ID)
            if (tag != null) {
                if (tag is ThemeObject) {
                    return tag
                } else {
                    throw IllegalStateException("$view already bind an object($tag)!")
                }
            }
            return ThemeObject(view)
        }

        @JvmStatic
        fun unbind(view: View) {
            get(view)?.release()
        }
    }

    private val themeAttributes = HashMap<String, ThemeAttribute>(2)

    private var theme: Theme? = null

    private var version: Int = Theme.START_VERSION

    private var themeDispatcher: ThemeDispatcher? = null

    init {
        view.getTag(TAG_ID)?.let {
            throw IllegalStateException("$view already bind an object($it)!")
        }
        view.setTag(TAG_ID, this)
        view.addOnAttachStateChangeListener(this)
        if (view.isAttachedToWindow) {
            attachThemeDispatcher()
        }
    }

    fun release() {
        view.setTag(TAG_ID, null)
        view.removeOnAttachStateChangeListener(this)
        detachThemeDispatcher()
    }

    fun clearThemeAttributes() = apply {
        themeAttributes.clear()
    }

    fun findThemeAttribute(attributeName: String): ThemeAttribute? {
        return themeAttributes[attributeName]
    }

    fun getAllThemeAttributes(): List<ThemeAttribute> = ArrayList(themeAttributes.values)

    fun addThemeAttribute(attribute: ThemeAttribute) = apply {
        themeAttributes[attribute.attributeName] = attribute
        theme?.let { attribute.apply(view, it) }
    }

    fun addThemeAttributes(attributes: Collection<ThemeAttribute>) = apply {
        attributes.forEach { addThemeAttribute(it) }
    }

    fun removeThemeAttribute(attributeName: String): ThemeAttribute? {
        return themeAttributes.remove(attributeName)
    }

    fun removeThemeAttribute(attribute: ThemeAttribute): Boolean {
        return themeAttributes.remove(attribute.attributeName, attribute)
    }

    private fun findThemeDispatcher(): ThemeDispatcher {
        if (view is ThemeDispatcher) return view
        var context = view.context
        while (context is ContextWrapper) {
            if (context is Activity) {
                break
            }
            context = context.baseContext
        }
        if (context is FragmentActivity) {
            val map = ArrayMap<View, Fragment>()
            val queue = LinkedList<Fragment>(context.supportFragmentManager.fragments)
            while (queue.isNotEmpty()) {
                val fragment = queue.pop()
                queue.addAll(fragment.childFragmentManager.fragments)
                map[fragment.view] = fragment
            }
            val root: View = context.findViewById(android.R.id.content)
            var fragment: Fragment? = null
            var current = view
            while (current !== root) {
                fragment = map[current]
                if (fragment != null) {
                    if (fragment is ThemeDispatcher) {
                        return fragment
                    }
                }
                current = current.parent as? View ?: break
                val dispatcher = current as? ThemeDispatcher ?: ThemeObject.get(current)?.themeDispatcher
                if (dispatcher != null) {
                    return dispatcher
                }
            }
        }
        if (context is ThemeDispatcher) {
            return context
        }
        return view.context.applicationContext as? ThemeDispatcher ?:
        throw IllegalStateException("Can't find theme dispatcher for $view!")
    }

    private fun attachThemeDispatcher() {
        if (themeDispatcher == null) {
            themeDispatcher = findThemeDispatcher().also {
                it.observableTheme().observeForever(this)
            }
        }
    }

    private fun detachThemeDispatcher() {
        if (themeDispatcher != null) {
            themeDispatcher?.observableTheme()?.removeObserver(this)
            themeDispatcher = null
        }
    }

    override fun onViewAttachedToWindow(v: View) {
        attachThemeDispatcher()
    }

    override fun onViewDetachedFromWindow(v: View) {
        detachThemeDispatcher()
    }

    override fun onChanged(theme: Theme?) {
        if (theme == null) return
        if (this.theme == theme && this.version >= theme.version) return
        if (this.theme == null && theme.isOrigin) {
            // Don't apply for origin theme.
            this.theme = theme
            this.version = theme.version
        } else {
            this.theme = theme
            this.version = theme.version
            themeAttributes.forEach { (_, attribute) -> attribute.apply(view, theme) }
        }
    }
}