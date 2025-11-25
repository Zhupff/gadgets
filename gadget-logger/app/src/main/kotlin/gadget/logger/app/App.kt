package gadget.logger.app

import android.app.Activity
import android.app.Application
import android.content.res.Configuration
import android.os.Bundle
import gadget.logger.FilePrinter
import gadget.logger.LogcatPrinter
import gadget.logger.Logger
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Supplier

class App : Application() {

    companion object {

        lateinit var INSTANCE: App
            private set

        val logger = Logger(
            "Gadget#",
            listOf(LogcatPrinter(), object : FilePrinter("GadgetLogger"), Lifecycle.Observer {
                init {
                    Lifecycle.add(this)
                }
                override fun onAppBackground() {
                    super.onAppBackground()
                    flushFlag = true
                }
                override fun onAppDestroyed() {
                    super.onAppDestroyed()
                    flushFlag = true
                    release()
                }
            })
        )

    }

    init {
        INSTANCE = this
        registerActivityLifecycleCallbacks(Lifecycle)
    }

    override fun onCreate() {
        super.onCreate()
        logI { "onCreate" }
    }


    object Lifecycle : ActivityLifecycleCallbacks, MutableSet<Lifecycle.Observer> by CopyOnWriteArraySet() {

        /** 创建的Activity计数 */
        private val ACTIVITY_CREATED_COUNTER = AtomicInteger(0)
        /** 存活的Activity计数 */
        private val ACTIVITY_STARTED_COUNTER = AtomicInteger(0)

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            ACTIVITY_CREATED_COUNTER.incrementAndGet()
        }

        override fun onActivityStarted(activity: Activity) {
            val count = ACTIVITY_STARTED_COUNTER.incrementAndGet()
            if (count == 1) {
                logD { "onAppForeground" }
                Lifecycle.forEach { it.onAppForeground() }
            }
        }

        override fun onActivityResumed(activity: Activity) {
        }

        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityStopped(activity: Activity) {
            val count = ACTIVITY_STARTED_COUNTER.decrementAndGet()
            if (count <= 0) {
                logD { "onAppBackground" }
                Lifecycle.forEach { it.onAppBackground() }
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
            val count = ACTIVITY_CREATED_COUNTER.decrementAndGet()
            if (count <= 0) {
                logD { "onAppDestroyed" }
                Lifecycle.forEach { it.onAppDestroyed() }
            }
        }

        interface Observer {

            /**
             * 应用回前台
             */
            fun onAppForeground() {}

            /**
             * 应用退后台
             */
            fun onAppBackground() {}

            /**
             * 配置发生变化，比如亮暗模式切换
             */
            fun onConfigurationChanged(newConfig: Configuration) {}

            /**
             * 应用销毁
             */
            fun onAppDestroyed() {}
        }
    }
}

fun Any.logD(content: Supplier<String>) {
    App.logger.d(if (this is String) this else "${this::class.java.simpleName}(${this.hashCode()})", content)
}

fun Any.logI(content: Supplier<String>) {
    App.logger.i(if (this is String) this else "${this::class.java.simpleName}(${this.hashCode()})", content)
}

fun Any.logW(throwable: Throwable?, content: Supplier<String>) {
    App.logger.w(if (this is String) this else "${this::class.java.simpleName}(${this.hashCode()})", throwable, content)
}

fun Any.logE(throwable: Throwable, content: Supplier<String>) {
    App.logger.e(if (this is String) this else "${this::class.java.simpleName}(${this.hashCode()})", throwable, content)
}