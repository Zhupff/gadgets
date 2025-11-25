package gadget.rxjava3

import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Predicate
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.atomic.AtomicBoolean

class SingleDisposeNotifier : DisposeNotifier, Predicate<Boolean> {

    private val notifier = BehaviorSubject.createDefault(false)

    private val disposed = AtomicBoolean(false)

    private val disposables = HashSet<Disposable>()

    override fun dispose() {
        if (disposed.compareAndSet(false, true)) {
            notifier.onNext(true)
            synchronized(disposables) {
                val iterator = disposables.iterator()
                while (iterator.hasNext()) {
                    val disposable = iterator.next()
                    if (!disposable.isDisposed) {
                        disposable.dispose()
                    }
                    iterator.remove()
                }
            }
        }
    }

    override fun <UPSTREAM : Any> autoDispose(): DisposeNotifier.AutoDispose<UPSTREAM> =
        DisposeNotifier.AutoDispose(notifier.filter(this))

    override fun register(disposable: Disposable) {
        synchronized(disposables) {
            if (disposables.add(disposable) && disposed.get()) {
                disposables.remove(disposable)
                if (!disposable.isDisposed) {
                    disposable.dispose()
                }
            }
        }
    }

    override fun unregister(disposable: Disposable) {
        synchronized(disposables) {
            disposables.remove(disposable)
        }
    }

    override fun test(bool: Boolean): Boolean = bool
}