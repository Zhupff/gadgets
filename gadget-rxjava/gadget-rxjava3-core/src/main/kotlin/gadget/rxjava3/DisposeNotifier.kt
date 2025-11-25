package gadget.rxjava3

import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableSource
import io.reactivex.rxjava3.core.CompletableTransformer
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableTransformer
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.MaybeSource
import io.reactivex.rxjava3.core.MaybeTransformer
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleSource
import io.reactivex.rxjava3.core.SingleTransformer
import io.reactivex.rxjava3.disposables.Disposable
import org.reactivestreams.Publisher
import kotlin.coroutines.cancellation.CancellationException

interface DisposeNotifier {

    fun dispose()

    fun <UPSTREAM: Any> autoDispose(): AutoDispose<UPSTREAM>

    fun register(disposable: Disposable)

    fun unregister(disposable: Disposable)

    class AutoDispose<UPSTREAM : Any> internal constructor(
        private val notifier: Observable<Boolean>,
    ) : SingleTransformer<UPSTREAM, UPSTREAM>,
        ObservableTransformer<UPSTREAM, UPSTREAM>,
        MaybeTransformer<UPSTREAM, UPSTREAM>,
        FlowableTransformer<UPSTREAM, UPSTREAM>,
        CompletableTransformer
    {
        private val cancellationException by lazy { CancellationException("AutoDispose") }

        override fun apply(upstream: Single<UPSTREAM>): SingleSource<UPSTREAM> =
            upstream.takeUntil(notifier.flatMapSingle { Single.error(cancellationException) }.firstOrError())

        override fun apply(upstream: Observable<UPSTREAM>): ObservableSource<UPSTREAM> =
            upstream.takeUntil(notifier.flatMap { Observable.error(cancellationException) })

        override fun apply(upstream: Maybe<UPSTREAM>): MaybeSource<UPSTREAM> =
            upstream.takeUntil(notifier.flatMapMaybe { Maybe.error(cancellationException) }.firstElement())

        override fun apply(upstream: Flowable<UPSTREAM>): Publisher<UPSTREAM> =
            upstream.takeUntil(notifier.flatMap { Observable.error(cancellationException) }.toFlowable(BackpressureStrategy.LATEST))

        override fun apply(upstream: Completable): CompletableSource =
            Completable.ambArray(upstream, notifier.flatMapCompletable { Completable.error(cancellationException) })
    }
}