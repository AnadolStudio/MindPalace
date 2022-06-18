package com.anadolstudio.core.view

import android.view.View

class ViewState(val view: View) {
    private val translationX = view.translationX
    private val translationY = view.translationX
    private val scaleX = view.scaleX
    private val scaleY = view.scaleY
    private val rotation = view.rotation

    fun rebootToDefault() {
        view.translationX = translationX
        view.translationY = translationY
        view.scaleX = scaleX
        view.scaleY = scaleY
        view.rotation = rotation
    }

    fun rebootToDefaultWithAnim() {
        view.animate()
            .translationX(translationX)
            .translationY(translationY)
            .scaleX(scaleX)
            .scaleY(scaleY)
            .rotation(rotation)

        view.clearAnimation()
    }

    fun rebootToDefaultTranslation() {
        view.translationX = translationX
        view.translationY = translationY
    }
}