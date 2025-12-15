package gadget.basic

import android.app.Application
import android.content.pm.ApplicationInfo

val Application.debuggable: Boolean
    get() = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

val Application.version: Pair<String, Long>
    get() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val versionCode = packageInfo.longVersionCode
        val versionName = packageInfo.versionName ?: versionCode.toString()
        return versionName to versionCode
    }
