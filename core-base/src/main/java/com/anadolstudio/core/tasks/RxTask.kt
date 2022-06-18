package com.anadolstudio.core.tasks

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

interface RxTask<T> {

    fun onSuccess(callback: RxCallback<T>): RxTask<T>

    fun onError(callback: RxCallback<Throwable>): RxTask<T>

    fun onFinal(callback: RxCallback<Boolean>): RxTask<T>

    fun cancel()

    abstract class Abstract<TaskData : Any>() : RxTask<TaskData> {

        private var isStart = false
        protected var result: Result<TaskData> = Result.Loading()
        protected var disposable: Disposable? = null

        fun start(): RxTask<TaskData> {
            if (isStart) throw RxException.StartException()
            isStart = true
            subscribe(createObservable())

            return this
        }

        abstract fun createObservable(): Observable<TaskData>

        protected open fun subscribe(observable: Observable<TaskData>) {
            disposable = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result = Result.Success(it) },
                    {
                        result = Result.Error(it)
                        notifyAllListeners()
                    },
                    { notifyAllListeners() }
                )
        }

        protected var valueCallbacks: MutableList<RxCallback<TaskData>> = mutableListOf()
        protected var errorCallbacks: MutableList<RxCallback<Throwable>> = mutableListOf()
        protected var finalCallbacks: MutableList<RxCallback<Boolean>> = mutableListOf()

        override fun onSuccess(callback: RxCallback<TaskData>): RxTask<TaskData> {
            valueCallbacks.add(callback)

            val result = this.result

            if (result is Result.Success) callback.invoke(result.data)

            return this
        }

        override fun onError(callback: RxCallback<Throwable>): RxTask<TaskData> {
            errorCallbacks.add(callback)
            val result = this.result

            if (result is Result.Error) callback.invoke(result.error)

            return this
        }

        override fun onFinal(callback: RxCallback<Boolean>): RxTask<TaskData> {
            finalCallbacks.add(callback)

            when (this.result) {
                is Result.Success -> callback.invoke(true)
                is Result.Error -> callback.invoke(false)
                else -> {}
            }

            return this
        }

        override fun cancel() {
            disposable?.apply {
                if (!isDisposed) dispose()
            }
        }

        protected fun notifyAllListeners() {
            val result = this.result
            val valueCallbacks = this.valueCallbacks
            val errorCallbacks = this.errorCallbacks

            if (result is Result.Success && valueCallbacks.isNotEmpty()) {
                valueCallbacks.forEach { it.invoke(result.data) }
                finalCallbacks.forEach { it.invoke(true) }
            } else if (result is Result.Error && errorCallbacks.isNotEmpty()) {
                errorCallbacks.forEach { it.invoke(result.error) }
                finalCallbacks.forEach { it.invoke(false) }
            }
        }
    }

    open class Base<TaskData : Any>(
        protected val domain: RxDomain<TaskData>
    ) : Abstract<TaskData>() {

        override fun createObservable(): Observable<TaskData> = Observable
            .create { emitter ->
                try {
                    emitter.onNext(domain.invoke())
                    emitter.onComplete()
                } catch (ex: Exception) {
                    emitter.onError(ex)
                }
            }


        class Quick<TaskData : Any>(domain: RxDomain<TaskData>) : Base<TaskData>(domain) {

            init {
                start()
            }
        }
    }

    open class Progress<TaskData : Any, ProgressData>(
        protected val progressListener: ProgressListener<ProgressData>?,
        protected val domain: RxDoProgress<TaskData, ProgressData>
    ) : Abstract<TaskData>() {

        override fun createObservable(): Observable<TaskData> = Observable.create { emitter ->
            try {
                emitter.onNext(domain.invoke(progressListener))
                emitter.onComplete()
            } catch (ex: Exception) {
                emitter.onError(ex)
            }
        }


        class Quick<TaskData : Any, ProgressData>(
            progressListener: ProgressListener<ProgressData>?,
            callback: RxDoProgress<TaskData, ProgressData>
        ) : Progress<TaskData, ProgressData>(progressListener, callback) {

            init {
                start()
            }
        }
    }

    open class Delay<TaskData : Any>(
        protected val delay: Long,
        protected val unit: TimeUnit,
        domain: RxDomain<TaskData>
    ) : Base<TaskData>(domain) {

        override fun createObservable(): Observable<TaskData> = super.createObservable().delay(delay, unit)


        class Quick<TaskData : Any>(
            delay: Long,
            unit: TimeUnit,
            callback: RxDomain<TaskData>
        ) : Delay<TaskData>(delay, unit, callback) {

            init {
                start()
            }
        }
    }

}
