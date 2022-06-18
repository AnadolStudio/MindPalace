package com.anadolstudio.core.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

interface Communication {

    interface Observe<T> {
        fun observe(owner: LifecycleOwner, observer: Observer<T>)
    }

    interface Update<T> : Mapper.Unit<T>

    interface ImMutable<T> : Observe<T>

    interface Mutable<T> : ImMutable<T>, Update<T>

    abstract class Abstract<T : Any>(
        protected val mutableLiveData: MutableLiveData<T>
    ) : Mutable<T> {

        override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
            mutableLiveData.observe(owner, observer)
        }

        open fun getValue() = mutableLiveData.value
    }

    open class UiUpdate<T : Any>(
        liveData: MutableLiveData<T> = MutableLiveData()
    ) : Abstract<T>(liveData) {

        override fun map(data: T) {
            mutableLiveData.value = data
        }
    }

    open class PostUpdate<T : Any>(
        liveData: MutableLiveData<T> = MutableLiveData()
    ) : Abstract<T>(liveData) {

        override fun map(data: T) = mutableLiveData.postValue(data)
    }
}