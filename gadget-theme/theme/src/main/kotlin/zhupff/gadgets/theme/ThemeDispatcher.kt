package zhupff.gadgets.theme

import androidx.lifecycle.LiveData

interface ThemeDispatcher {
    fun observableTheme(): LiveData<Theme>
}