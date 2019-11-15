package com.anadol.rememberwords.fragments;



import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;


import com.anadol.rememberwords.R;

import static com.anadol.rememberwords.fragments.GroupDetailFragment.IS_CREATED;

public class DialogTranscript extends DialogFragment  implements KeyboardView.OnKeyboardActionListener{
    public static final String EXTRA_WORDS = "words";
    public static final String TRANSCRIPT = "trans";

    private static final String TAG = "keyboard";
    private static final int BACKSPACE = -5;
    private KeyboardView mKeyboardView;
    private Keyboard mKeyboard;
    private EditText mEditText;
    private FrameLayout container;
//    private UUID id;
//    private int positionSelected;

    private class MyKeyboard extends KeyboardView{
        public MyKeyboard(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
        }
    }


    public static DialogTranscript newInstance(String text) {

        Bundle args = new Bundle();
        args.putString(TRANSCRIPT,text);
        DialogTranscript fragment = new DialogTranscript();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(TRANSCRIPT,mEditText.getText().toString());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().getWindow().setGravity(Gravity.BOTTOM);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_transcription,null);

        mKeyboardView = v.findViewById(R.id.keyboard);
        mKeyboard = new Keyboard(getContext(),R.xml.keys_definition_transcript);
        mKeyboard.setShifted(false);
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView.setOnKeyboardActionListener(this);
//        mKeyboardView.setPreviewEnabled(false);

        container = v.findViewById(R.id.container_keyboard);

        mEditText = v.findViewById(R.id.editText);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mEditText.setShowSoftInputOnFocus(false);
        } else {
            mEditText.setTextIsSelectable(true);
            //N.B. Accepting the case when non editable text will be selectable
        }
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); // Убирает клавиатуру
        }catch (NullPointerException ex){
            ex.printStackTrace();
        }
        String text;
        text = getArguments().getString(TRANSCRIPT);

        if (savedInstanceState != null){
            text = savedInstanceState.getString(TRANSCRIPT);
        }
        mEditText.setText(text);
        mEditText.setSelection(mEditText.length());

        AlertDialog.Builder adb = new AlertDialog.Builder(getContext(),R.style.DialogStyle)
                .setView(v);
        Dialog dialog = adb.create();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);
        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        sendResult(Activity.RESULT_OK);
    }
   /* @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.context_word_list, menu);
        menu.setGroupVisible(0, false);
        menu.findItem(R.id.action_context_replace).setVisible(false);
        menu.findItem(R.id.action_context_remove).setVisible(true);
//        System.out.println("onCreateContextMenu");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //        System.out.println("position "+ info);

        switch (item.getItemId()) {

            case R.id.action_context_remove:
                removeWord(positionSelected);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }*/

    private void sendResult(int resultCode){
        if (getTargetFragment() == null){
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(TRANSCRIPT,mEditText.getText().toString());
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }


    @Override
    public void onPress(int primaryCode) {
        Log.d(TAG, "onPress " + primaryCode);
    }

    @Override
    public void onRelease(int primaryCode) {
        Log.d(TAG, "onRelease " + primaryCode);
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        Log.d(TAG,"KeyCod " + primaryCode);
        InputConnection ic = mEditText.onCreateInputConnection(new EditorInfo());

        switch (primaryCode){
            case BACKSPACE:
                ic.deleteSurroundingText(1,0);
                break;

            default:
                char c = (char) primaryCode;
//                mEditText.append(String.valueOf(c));
                ic.commitText(String.valueOf(c),1);
        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

}
