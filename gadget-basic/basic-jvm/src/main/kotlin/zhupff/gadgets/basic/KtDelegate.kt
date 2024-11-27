package zhupff.gadgets.basic

import java.lang.reflect.Proxy

inline fun <reified T : Any> noDelegate(): T {
    val jc = T::class.java
    return Proxy.newProxyInstance(jc.classLoader, arrayOf(jc)) { _, _, _ -> } as T
}