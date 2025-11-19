package gadget.theme.app

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import gadget.theme.Theme
import gadget.theme.ThemeObservable
import gadget.widget.GadgetLayout

class CustomThemeLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : GadgetLayout(context, attrs), ThemeObservable, Observer<Theme> {

    private val themeObservable = MutableLiveData<Theme>()

    var theme: Theme?
        get() = themeObservable.value
        set(value) {
            if (themeObservable.value != value) {
                themeObservable.postValue(value)
            }
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        App.INSTANCE.subscribe().observeForever(this)
        themeObservable.observeForever(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        App.INSTANCE.subscribe().removeObserver(this)
        themeObservable.removeObserver(this)
    }

    override fun subscribe(): LiveData<Theme> = themeObservable

    override fun onChanged(value: Theme) {
        if (App.INSTANCE.subscribe().value === this.theme) {
            findViewById<View>(R.id.Check).isVisible = true
        } else {
            findViewById<View>(R.id.Check).isInvisible = true
        }
    }
}