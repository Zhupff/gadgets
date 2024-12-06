package zhupff.gadgets.theme

import android.content.res.ColorStateList
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.auto.service.AutoService
import zhupff.gadgets.theme.widget.CornerClipView
import java.util.ServiceLoader

object ThemeConfiguration : ThemeConfig(
    prefix = "theme__",
    ServiceLoader.load(ThemeAttribute::class.java).toList(),
) {

    @AutoService(ThemeAttribute::class)
    class Background : ThemeAttribute("background") {
        override fun apply(view: View, theme: Theme) {
            if (resourceType == TYPE_COLOR) {
                val color = theme.getColor(resourceId) ?: return
                if (color is ColorStateList) {
                    view.setBackgroundColor(color.defaultColor)
                } else if (color is Int) {
                    view.setBackgroundColor(color)
                }
            } else if (resourceType == TYPE_DRAWABLE) {
                val drawable = theme.getDrawable(resourceId)
                view.background = drawable
            }
        }
    }

    @AutoService(ThemeAttribute::class)
    class BorderColor : ThemeAttribute("BorderColor") {
        override fun apply(view: View, theme: Theme) {
            assert(resourceType == TYPE_COLOR)
            when (view) {
                is CornerClipView -> {
                    view.borderColor = theme.getColorInt(resourceId)
                }
            }
        }
    }

    @AutoService(ThemeAttribute::class)
    class TextColor : ThemeAttribute("textColor") {
        override fun apply(view: View, theme: Theme) {
            val color = theme.getColor(resourceId) ?: return
            when (view) {
                is TextView -> {
                    if (color is ColorStateList) {
                        view.setTextColor(color)
                    } else if (color is Int) {
                        view.setTextColor(color)
                    }
                }
            }
        }
    }

    @AutoService(ThemeAttribute::class)
    class TextColorHint : ThemeAttribute("textColorHint") {
        override fun apply(view: View, theme: Theme) {
            val color = theme.getColor(resourceId) ?: return
            when (view) {
                is EditText -> {
                    if (color is ColorStateList) {
                        view.setHintTextColor(color)
                    } else if (color is Int) {
                        view.setHintTextColor(color)
                    }
                }
            }
        }
    }

    @AutoService(ThemeAttribute::class)
    class Tint : ThemeAttribute("tint") {
        override fun apply(view: View, theme: Theme) {
            when (view) {
                is ImageView -> {
                    val color = theme.getColor(resourceId) ?: return
                    if (color is ColorStateList) {
                        view.imageTintList = color
                    } else if (color is Int) {
                        view.imageTintList = ColorStateList(emptyArray(), intArrayOf(color))
                    }
                }
            }
        }
    }
}