package gadget.theme

import android.app.Activity
import android.content.ContextWrapper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import java.lang.ref.WeakReference
import java.util.LinkedList

/**
 * 主题观察者，通过弱引用+Lifecycle的方式监听主题的变化。
 */
class ThemeObserver(
    view: View,
    private val attribute2resource: Map<Theme.Attribute, Theme.Resource>,
) : WeakReference<View>(view), View.OnAttachStateChangeListener, LifecycleOwner, Observer<Theme> {

    private var current: String = ""

    override val lifecycle: LifecycleRegistry = LifecycleRegistry(this)

    init {
        view.addOnAttachStateChangeListener(this)
        lifecycle.currentState = Lifecycle.State.CREATED
    }

    /**
     * 当View attached的时候溯源并将生命周期置为RESUMED以获取主题资源。
     */
    override fun onViewAttachedToWindow(v: View) {
        val target = get()
        if (target == null || target != v) {
            return
        }
        trace(target).subscribe().observe(this, this)
        lifecycle.currentState = Lifecycle.State.RESUMED
    }

    /**
     * 当View detached的时候将生命周期置为DESTROYED以停止监听主题的变化。
     * 之所以是DESTROYED而不是PAUSED，是因为View可能在不同的ViewParent之间、不同的Fragment之间甚至不同的Context之间移动。
     */
    override fun onViewDetachedFromWindow(v: View) {
        val target = get()
        if (target == null || target != v) {
            return
        }
        lifecycle.currentState = Lifecycle.State.DESTROYED
    }

    override fun onChanged(value: Theme) {
        val target = get()
        if (target == null || current == value.id) {
            return
        }
        current = value.id
        attribute2resource.forEach { (attribute, resource) ->
            attribute.apply(target, value, resource)
        }
    }

    /**
     * 每次涉及到弱应用的View的操作时，都会判断一下该View是否被回收了，如果被回收了则将生命周期置为DESTROYED。
     */
    override fun get(): View? {
        val target = super.get()
        if (target == null) {
            lifecycle.currentState = Lifecycle.State.DESTROYED
        }
        return target
    }

    /**
     * 按照【view->fragment->activity->application】的优先级，逐层往上溯源最近的一个[ThemeObservable]。
     */
    private fun trace(target: View): ThemeObservable {
        if (target is ThemeObservable) {
            return target
        }
        var context = target.context
        while (context is ContextWrapper) {
            if (context is Activity) {
                break
            }
            context = context.baseContext
        }
        if (context is FragmentActivity) {
            val map = HashMap<View, Fragment>()
            val queue = LinkedList<Fragment>(context.supportFragmentManager.fragments)
            while (queue.isNotEmpty()) {
                val fragment = queue.pop() ?: continue
                val view = fragment.view ?: continue
                map[view] = fragment
                queue.addAll(fragment.childFragmentManager.fragments)
            }
            val root = context.findViewById<View>(android.R.id.content)
            var view = target
            while (view !== root) {
                val fragment = map[view]
                if (fragment != null && fragment is ThemeObservable) {
                    return fragment
                }
                val parent = view.parent
                if (parent is ThemeObservable) {
                    return parent
                }
                if (parent == null || parent === view || parent !is View) {
                    break
                }
                view = parent
            }
        }
        if (context is ThemeObservable) {
            return context
        }
        return context.applicationContext as? ThemeObservable ?: throw IllegalStateException("ThemeObservable not found!")
    }
}