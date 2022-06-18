package com.anadolstudio.core.view

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.anadolstudio.core.dialogs.LoadingView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

open class BaseActivity : AppCompatActivity() {
    protected var loadingView: LoadingView? = null

    protected fun showLoadingDialog(isShow: Boolean) {
        if (isShow) {
            loadingView = LoadingView.Base.view(supportFragmentManager)
            loadingView?.showLoadingIndicator()
        }else{
            loadingView?.hideLoadingIndicator()
        }
    }

    fun replaceFragment(fragment: Fragment, containerId: Int) {
        replaceFragment(fragment, containerId, true)
    }

    fun replaceFragment(fragment: Fragment, containerId: Int, addToBackStack: Boolean) {

        val transaction = supportFragmentManager.beginTransaction()
            .replace(containerId, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)

        if (addToBackStack) transaction.addToBackStack(fragment.javaClass.name)

        transaction.commit()
    }

    private fun addFragment(fragment: Fragment, containerId: Int) {

        supportFragmentManager.beginTransaction()
            .add(containerId, fragment)
            .commit()
    }

    protected fun showToast(@StringRes id: Int, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, getString(id), length).show()
    }

    protected fun showToast(text: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, text, length).show()
    }

    open fun showSettingsSnackbar(
        rootView: View?,
        @StringRes textStringId: Int,
        @StringRes actionStringId: Int,
    ) {
        val snackbar = Snackbar.make(
            rootView!!,
            getText(textStringId),
            BaseTransientBottomBar.LENGTH_INDEFINITE
        )

        snackbar.setAction(actionStringId) { startAppSettingsActivity() }
        snackbar.show()
    }

    open fun startAppSettingsActivity() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}