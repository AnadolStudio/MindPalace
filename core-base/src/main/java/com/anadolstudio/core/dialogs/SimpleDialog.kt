package com.anadolstudio.core.dialogs

import android.os.Bundle
import androidx.annotation.StringRes

class ApplyDialog(
    positiveListener: Listener?, negativeListener: Listener?
) : AbstractDialog.ChoiceDialog(positiveListener, negativeListener) {

    // TODO делать более общим решением

    companion object {
        fun newInstance(
            @StringRes id: Int,
            positiveListener: Listener? = null,
            negativeListener: Listener? = null
        ): ApplyDialog {
            val args = Bundle().apply { putInt(DIALOG_MESSAGE_ID, id) }
            return ApplyDialog(positiveListener, negativeListener).apply { arguments = args }
        }
    }
}

class CancelDialog(
    positiveListener: Listener?, negativeListener: Listener?
) : AbstractDialog.ChoiceDialog(positiveListener, negativeListener) {

    companion object {
        fun newInstance(
            @StringRes id: Int,
            positiveListener: Listener? = null,
            negativeListener: Listener? = null
        ): CancelDialog {
            val args = Bundle().apply { putInt(DIALOG_MESSAGE_ID, id) }
            return CancelDialog(positiveListener, negativeListener).apply { arguments = args }
        }
    }
}

class InfoDialog(
    positiveListener: Listener?
) : AbstractDialog.InformationDialog(positiveListener) {

    companion object {
        fun newInstance(
            @StringRes id: Int, positiveListener: Listener? = null,
        ): InfoDialog {
            val args = Bundle().apply { putInt(DIALOG_MESSAGE_ID, id) }
            return InfoDialog(positiveListener).apply { arguments = args }
        }
    }

    override fun onStop() {
        dismissAllowingStateLoss()
        super.onStop()
    }
}