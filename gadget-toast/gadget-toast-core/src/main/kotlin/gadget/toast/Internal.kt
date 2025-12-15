package gadget.toast

import android.content.Context

internal val applicationContext: Context by lazy {
    val classActivityThread = Class.forName("android.app.ActivityThread")
    val methodCurrentActivityThread = classActivityThread.getDeclaredMethod("currentActivityThread")
    val currentActivityThread = methodCurrentActivityThread.invoke(null)
    val methodGetApplication = currentActivityThread::class.java.getDeclaredMethod("getApplication")
    val context: Context = methodGetApplication.invoke(currentActivityThread) as Context
    context.applicationContext
}