package com.anadolstudio.core.tasks

sealed class RxException(message: String) : Exception(message) {

    class StartException : RxException("Еhe task has already been started")

}
