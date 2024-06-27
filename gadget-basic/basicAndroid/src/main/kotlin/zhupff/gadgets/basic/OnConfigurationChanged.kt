package zhupff.gadgets.basic

import android.content.res.Configuration


interface OnConfigurationChangedDispatcher {
    fun addOnConfigurationChangedListener(listener: OnConfigurationChangedListener): Boolean
    fun removeOnConfigurationChangedListener(listener: OnConfigurationChangedListener): Boolean
    fun clearOnConfigurationChangedListeners()
}


interface OnConfigurationChangedListener {
    fun onConfigurationChanged(newConfig: Configuration)
}