package gadget.theme

import androidx.lifecycle.LiveData

/**
 * 主题源，作用是分发主题方案。
 */
interface ThemeObservable {
    fun subscribe(): LiveData<Theme>
}