package com.anadolstudio.core.tasks

typealias RxDomain<T> = () -> T

typealias RxDoProgress<T, ProgressData> = (ProgressListener<ProgressData>?) -> T

typealias RxCallback<T> = (T) -> Unit