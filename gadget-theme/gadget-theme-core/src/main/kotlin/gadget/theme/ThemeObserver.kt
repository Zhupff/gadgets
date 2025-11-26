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
    private var flags: Int = 0,
) : WeakReference<View>(view), View.OnAttachStateChangeListener, LifecycleOwner, Observer<Theme> {

    companion object {
        /** 是否是可变的，即View在Detach之后又重新Attach是否可能改变它所在的视图树（比如某些在不同视图间移动的悬浮View）。 */
        const val FLAG_MUTABLE: Int = 1 shl 1
    }

    private var current: String = ""

    private var observable: ThemeObservable? = null

    override val lifecycle: LifecycleRegistry = LifecycleRegistry(this)

    init {
        if (view.getTag(gadget.theme.core.R.id.gadget_theme_observer) != null) {
            throw IllegalStateException("Already bind ThemeObserver!")
        } else {
            view.setTag(gadget.theme.core.R.id.gadget_theme_observer, this)
        }
        lifecycle.currentState = Lifecycle.State.CREATED
        view.addOnAttachStateChangeListener(this)
        if (view.isAttachedToWindow) {
            // 对于那些手动添加的View，需要检查一下是否已经attach了，如果是，那么需要主动触发一次attach的逻辑。
            onViewAttachedToWindow(view)
        }
    }

    /**
     * 当View attached的时候溯源并将生命周期置为RESUMED以获取主题资源。
     */
    override fun onViewAttachedToWindow(v: View) {
        val target = get()
        if (target == null || target != v) {
            return
        }
        lifecycle.currentState = Lifecycle.State.RESUMED
        if (flags and 1 == 0) { // 首次attach。
            trace(target).also {
                flags = flags or 1
                observable = it
            }.subscribe().observe(this, this)
        } else {
            if (flags and FLAG_MUTABLE != 0) {
                // 如果是可变的，那么每次attach的时候都需要重新trace一下对应的主题源。
                trace(target).also {
                    observable = it
                }.subscribe().observe(this, this)
            }
        }
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
        if (flags and FLAG_MUTABLE != 0) {
            // 如果是可变的，每次detach的时候将生命周期设为Destroyed，这样Observable就会自动清理该Observer；
            // 在下次attach的时候再重新trace对应的Observable并observe。
            lifecycle.currentState = Lifecycle.State.DESTROYED
            observable = null
        } else {
            // 如果不是可变的，那只需要将生命周期设为CREATED（等价于paused）。
            lifecycle.currentState = Lifecycle.State.CREATED
        }
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
                if (parent is View) {
                    val observer = parent.getTag(gadget.theme.core.R.id.gadget_theme_observer)
                    if (observer is ThemeObserver) {
                        // 首次attach且最近的observable是View的时候，需要处理些flags相关的内容。
                        if (flags and 1 == 0) {
                            // 如果父View是可变的，那意味着当前View也是可变的。
                            this.flags = this.flags or (observer.flags and FLAG_MUTABLE)
                        }
                        return observer.observable!!
                    }
                } else if (parent is ThemeObservable) {
                    return parent
                }
                view = parent as? View ?: break
            }
        }
        if (context is ThemeObservable) {
            return context
        }
        return context.applicationContext as? ThemeObservable ?: throw IllegalStateException("ThemeObservable not found!")
    }
}