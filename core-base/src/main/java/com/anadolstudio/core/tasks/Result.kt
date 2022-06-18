package com.anadolstudio.core.tasks

sealed class Result<T> {

    class Success<T>(val data: T) : Result<T>()

    class Error<T>(val error: Throwable) : Result<T>()

    class Loading<T> : Result<T>()

    class Empty<T> : Result<T>()

}
