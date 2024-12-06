package zhupff.gadgets.basic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

fun <T> LiveData<T>.mutable(): MutableLiveData<T> =
    if (this is MutableLiveData) this else throw IllegalStateException("It's not a mutable object.")