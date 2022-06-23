package com.anadol.mindpalace.view.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.anadolstudio.core.dialogs.LoadingView
import com.anadolstudio.core.dialogs.LoadingView.Base.Companion.view

open class BaseFragment(@LayoutRes val layout: Int) : Fragment() {

    private var mLoadingView: LoadingView? = null

    protected fun showLoading(isShow: Boolean) {
        if (isShow) {
            mLoadingView = view(parentFragmentManager)
                .apply { showLoadingIndicator() }
        } else {
            mLoadingView?.hideLoadingIndicator()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(layout, container, false)

}