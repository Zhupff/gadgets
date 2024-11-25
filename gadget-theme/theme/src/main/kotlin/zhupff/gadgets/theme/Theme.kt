package zhupff.gadgets.theme

import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import java.util.concurrent.atomic.AtomicReference

abstract class Theme @MainThread constructor(
    val name: String,
    val parent: Theme?,
) {
    internal companion object : AtomicReference<Theme>() {
        const val START_VERSION = -1
    }

    val isOrigin: Boolean = parent == null

    var version: Int = START_VERSION + 1; protected set

    protected val children = ArrayList<Theme>()


    init {
        if (parent == null) {
            val origin = Theme.getAndSet(this)
            if (origin != null) {
                throw IllegalStateException("Can only have 1 original theme! Otherwise it should have a parent theme.")
            }
        } else {
            parent.children.add(this)
        }
    }


    @MainThread
    fun upgrade() {
        ++version
        children.forEach { child -> child.upgrade() }
    }

    /**
     * @return Int or ColorStateList or Null
     */
    abstract fun getColor(@ColorRes id: Int): Any?

    abstract fun getDrawable(@DrawableRes id: Int): Drawable?

    abstract fun getString(@StringRes id: Int, vararg formatArgs: Any): String?
}