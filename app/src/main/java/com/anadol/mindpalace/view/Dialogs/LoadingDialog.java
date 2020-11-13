package com.anadol.mindpalace.view.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.anadol.rememberwords.R;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoadingDialog extends DialogFragment {
    public static final Handler HANDLER = new Handler(Looper.getMainLooper());

    public static LoadingView view(@NonNull FragmentManager fm) {
        return new LoadingView() {
            //TODO разобраться как это работает (Программирование Теория)
            private final AtomicBoolean mWaitForHide = new AtomicBoolean();

            @Override
            public void showLoadingIndicator() {
                if (mWaitForHide.compareAndSet(false, true)) {
                    if (fm.findFragmentByTag(LoadingDialog.class.getName()) == null) {
                        LoadingDialog dialog = new LoadingDialog();
                        dialog.show(fm, LoadingDialog.class.getName());
                    }
                }
            }

            @Override
            public void hideLoadingIndicator() {
                HANDLER.post(new HideTask(fm));
            }
        };
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, getTheme());
        setCancelable(false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setView(View.inflate(getActivity(), R.layout.dialog_loading, null))
                .create();
    }

    private static class HideTask implements Runnable {

        private final Reference<FragmentManager> mFmRef;

        private int mAttempts = 5;

        public HideTask(FragmentManager fm) {
            mFmRef = new WeakReference<>(fm);
        }

        @Override
        public void run() {
            HANDLER.removeCallbacks(this);
            final FragmentManager fm = mFmRef.get();

            if (fm != null) {
                LoadingDialog dialog = (LoadingDialog) fm.findFragmentByTag(LoadingDialog.class.getName());
                if (dialog != null) {
                    dialog.dismissAllowingStateLoss();
                } else {
                    HANDLER.postDelayed(this, 300);
                }
            }
        }
    }
}
