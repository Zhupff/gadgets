package gadget.theme.app

import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.auto.service.AutoService
import gadget.theme.Theme
import gadget.theme.ThemeFactory
import gadget.widget.GadgetLayout
import java.util.ServiceLoader
import androidx.core.graphics.drawable.toDrawable

object CustomThemeFactory : ThemeFactory(null), List<Int> by listOf(
    R.color.themePrimary,
    R.color.themeOnPrimary,
    R.color.themePrimaryContainer,
    R.color.themeOnPrimaryContainer,
    R.color.themePrimaryFixed,
    R.color.themePrimaryFixedDim,
    R.color.themeOnPrimaryFixed,
    R.color.themeOnPrimaryFixedVariant,
    R.color.themeSecondary,
    R.color.themeOnSecondary,
    R.color.themeSecondaryContainer,
    R.color.themeOnSecondaryContainer,
    R.color.themeSecondaryFixed,
    R.color.themeSecondaryFixedDim,
    R.color.themeOnSecondaryFixed,
    R.color.themeOnSecondaryFixedVariant,
    R.color.themeTertiary,
    R.color.themeOnTertiary,
    R.color.themeTertiaryContainer,
    R.color.themeOnTertiaryContainer,
    R.color.themeTertiaryFixed,
    R.color.themeTertiaryFixedDim,
    R.color.themeOnTertiaryFixed,
    R.color.themeOnTertiaryFixedVariant,
    R.color.themeError,
    R.color.themeOnError,
    R.color.themeErrorContainer,
    R.color.themeOnErrorContainer,
    R.color.themeSurfaceDim,
    R.color.themeSurface,
    R.color.themeSurfaceBright,
    R.color.themeSurfaceContainerLowest,
    R.color.themeSurfaceContainerLow,
    R.color.themeSurfaceContainer,
    R.color.themeSurfaceContainerHigh,
    R.color.themeSurfaceContainerHighest,
    R.color.themeOnSurface,
    R.color.themeOnSurfaceVariant,
    R.color.themeOutline,
    R.color.themeOutlineVariant,
    R.color.themeScrim,
    R.color.themeShadow,
    R.color.themeInverseSurface,
    R.color.themeInverseOnSurface,
    R.color.themeInversePrimary,
) {

    private val THEME_ATTRIBUTES = ServiceLoader.load(Theme.Attribute::class.java).associateBy { it.name }

    override fun filter(resource: Theme.Resource): Boolean = contains(resource.id)

    override fun provide(attributeName: String): Theme.Attribute? = THEME_ATTRIBUTES[attributeName]

    @AutoService(Theme.Attribute::class)
    class ThemeBackground : Theme.Attribute("background") {
        override fun apply(view: View, theme: Theme, resource: Theme.Resource) {
            if (resource.type == "color") {
                val color = theme.getColorInt(resource)
                view.setBackgroundColor(color)
            } else if (resource.type == "drawable") {
                val drawable = theme.getDrawable(resource)
                view.background = drawable
            }
        }
    }

    @AutoService(Theme.Attribute::class)
    class ThemeTextColor : Theme.Attribute("textColor") {
        override fun apply(view: View, theme: Theme, resource: Theme.Resource) {
            if (view is TextView) {
                view.setTextColor(theme.getColorInt(resource))
            }
        }
    }

    @AutoService(Theme.Attribute::class)
    class ThemeTextColorHint : Theme.Attribute("textColorHint") {
        override fun apply(view: View, theme: Theme, resource: Theme.Resource) {
            if (view is EditText) {
                view.setHintTextColor(theme.getColorInt(resource))
            }
        }
    }

    @AutoService(Theme.Attribute::class)
    class ThemeTint : Theme.Attribute("tint") {
        override fun apply(view: View, theme: Theme, resource: Theme.Resource) {
            if (view is ImageView) {
                view.imageTintList = theme.getColorState(resource)
            }
        }
    }

    @AutoService(Theme.Attribute::class)
    class ThemeBorderShader : Theme.Attribute("gadget_border_shader") {
        override fun apply(view: View, theme: Theme, resource: Theme.Resource) {
            if (view is GadgetLayout) {
                view.borderShader = theme.getColorInt(resource).toDrawable()
            }
        }
    }
}