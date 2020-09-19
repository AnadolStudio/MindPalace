package com.anadol.rememberwords.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.anadol.rememberwords.R;

import com.anadol.rememberwords.myList.MyItemTranslate;
import com.anadol.rememberwords.myList.MyRecyclerAdapter;
import com.anadol.rememberwords.myList.MyViewHolder;
import com.anadol.rememberwords.model.Word;

import java.util.ArrayList;
import java.util.HashSet;

import static com.anadol.rememberwords.fragments.GroupDetailFragment.POSITION;

public class DialogMultiTranslate extends AppCompatDialogFragment implements InputFilter {

    public static final String  MULTI_TEXT = "multi_text";
    public static final String  USED = "used";
    private static final String TAG = "dialog_translate";

    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter mAdapter;
    private ImageButton mAddButton;
    private EditText commentText;
    private Word mWord;
    private ArrayList<MyItemTranslate> mList;
    private String[] res;
    private boolean[] isUsed;

    public static DialogMultiTranslate newInstance(Word word,int position) {

        Bundle args = new Bundle();
        args.putParcelable(MULTI_TEXT, word);
        args.putInt(POSITION, position);
        DialogMultiTranslate fragment = new DialogMultiTranslate();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(MULTI_TEXT,mWord);
        outState.putBooleanArray(USED,isUsed);
        super.onSaveInstanceState(outState);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_translate,null);
        res = getResources().getStringArray(R.array.words_type);
        if (savedInstanceState != null) {
            mWord = savedInstanceState.getParcelable(MULTI_TEXT);
            isUsed = savedInstanceState.getBooleanArray(USED);
        } else {
            mWord = getArguments().getParcelable(MULTI_TEXT);
            isUsed = new boolean[res.length];
            for (boolean b : isUsed){
                b = false;
            }
        }

        mList = new ArrayList<>();
        if (mWord.hasMultiTrans() == Word.TRUE) {
            String[][] allTranslates = mWord.getMultiTranslateFormat();
            for (String[] s: allTranslates) {
                mList.add(new MyItemTranslate(s[0]+":",s[1]));
                isUsed[getSelectedItemFromResArray(s[0]+":")] = true;//Это подразумевает то, что Диалогу передается уже правильно отформатированный текст
            }
        }else {
            mList.add(new MyItemTranslate(res[0],mWord.getTranslate()));
            isUsed[getSelectedItemFromResArray(res[0])] = true;
        }

        mAddButton = view.findViewById(R.id.add_words);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mList.size() < res.length) {
                    mList.add(new MyItemTranslate(res[newSelectedItemFromResArray()],""));
                    mAdapter.notifyItemChanged(mList.size()-1);
                }
                if (mList.size() >= res.length){
                    v.setEnabled(false);
                }else {
                    v.setEnabled(true);
                }
                if (mList.size() <= 1){
                    mWord.setHasMultiTrans(Word.FALSE);
                }else {
                    mWord.setHasMultiTrans(Word.TRUE);
                }
            }
        });

        mRecyclerView = view.findViewById(R.id.recycler_view);
        createAdapter();
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(lm);
        mRecyclerView.setAdapter(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SimpleItemHelperCallback(mAdapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),DividerItemDecoration.VERTICAL));

        commentText = view.findViewById(R.id.edit_comment);
        commentText.setText(mWord.getComment());
        commentText.setSelection(commentText.length());
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mWord.setComment(s.toString());
            }
        });
        return new AlertDialog.Builder(getActivity(),R.style.DialogKeyboard)
                .setView(view)
                .create();
    }

    private void createAdapter() {

        mAdapter = new MyRecyclerAdapter(mList, R.layout.item_dialog_translate);
        mAdapter.setCreatorAdapter(new MyRecyclerAdapter.CreatorAdapter() {// ДЛЯ БОЛЬШЕЙ ГИБКОСТИ ТУТ Я РЕАЛИЗУЮ СЛУШАТЕЛЯ И МЕТОДЫ АДАПТЕРА
            @Override
            public void createHolderItems(final MyViewHolder holder) {
                Spinner type = holder.itemView.findViewById(R.id.type_word);
                type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mList.get(holder.getAdapterPosition()).setTypeName(res[position]);
                        updateArrayIsUsed();
                        if (hasDuplicate()) {
                            parent.setSelection(newSelectedItemFromResArray());
                        }
                        /*for (MyItemTranslate item :mList){
                            System.out.println(item.toString());
                        }*/
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                EditText words = holder.itemView.findViewById(R.id.words_text);
                words.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        mList.get(holder.getAdapterPosition()).setWords(s.toString().trim());
                    }
                });
                words.setFilters(new InputFilter[]{DialogMultiTranslate.this});

                holder.setViews(new View[]{type, words});
            }

            @Override
            public void bindHolderItems(final MyViewHolder holder) {
                int position = holder.getAdapterPosition();

                View[] views = holder.getViews();
                Spinner type = (Spinner) views[0];
                type.setSelection(getSelectedItemFromResArray(mList.get(position).getTypeName())); // : В Word ":" является делителем

                EditText words = (EditText) views[1];
                words.setText(mList.get(position).getWords());
                words.setSelection(words.length());
            }

            @Override
            public void myOnItemDismiss(int position, int flag) {
                if (flag == ItemTouchHelper.START){
                    mList.remove(position);
                    updateArrayIsUsed();
                    mAdapter.notifyItemRemoved(position);

                    if (mList.size() <= 1){
                        mWord.setHasMultiTrans(Word.FALSE);
                    }
                    mAddButton.setEnabled(true);
                } else if (flag == ItemTouchHelper.END){
                    mAdapter.notifyItemChanged(position);
                    Toast.makeText(getContext(), "If you want to remove this word(s), swipe left", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWord.setTranslate(getAllWords());
    }

    private String getAllWords(){
        StringBuilder stringBuilder = new StringBuilder();
        if (mList.size() > 1) {
            for (MyItemTranslate item : mList){
                if (!item.getWords().replaceAll("\n","").replaceAll(";","").equals("")) {// Если EditText не пуст
                    if (stringBuilder.length() != 0){
                        stringBuilder.append("/");
                    }
                    stringBuilder.append(item.getTypeName());
                    String[] itemsWords = item.getWords().replaceAll("\n","").split(";");
                    for (String s : itemsWords){
//                        System.out.println(s);
                        stringBuilder.append(s.trim()).append(";").append("\n");
                    }
                }
            }
            stringBuilder.replace(stringBuilder.length()-1,stringBuilder.length(),"");// Удаляет последний символ абзаца
        }else {
            MyItemTranslate item = mList.get(0);
            String[] itemsWords = item.getWords().replaceAll("\n","").split(";");
            if (itemsWords.length <= 1) {
                mWord.setHasMultiTrans(Word.FALSE);
                stringBuilder.append(mList.get(0).getWords()
                        .replaceAll("\n", "")
                        .replaceAll(";", ""));
            }else {
                mWord.setHasMultiTrans(Word.TRUE);

                stringBuilder.append(item.getTypeName());
                for (String s : itemsWords){
                    stringBuilder.append(s.trim()).append(";").append("\n");
                }
                stringBuilder.replace(stringBuilder.length()-1,stringBuilder.length(),"");// Удаляет последний символ абзаца
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        mWord.setTranslate(getAllWords());
        // Я передаю только позицию которую нужно обновить
        // ссылка на тот Word менялась здесь
        sendResult(Activity.RESULT_OK,getArguments().getInt(POSITION));

    }

    private void sendResult(int resultCode, int position){
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
//        intent.putExtra(MULTI_TEXT,word);
        intent.putExtra(POSITION,position);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }

    private int getSelectedItemFromResArray(String s){
        int rtn = 0;
        for (int i = 0; i < res.length; i++) {
            if ((s).equals(res[i])){
                rtn = i;
            }
        }
        return rtn;
    }

    private void updateArrayIsUsed(){
        isUsed = new boolean[res.length];
        /*for (boolean b :isUsed){
            b = false;
        }*/
        String[] cloneRes = res.clone();
        for (int i = 0; i < mList.size(); i++){
            //foreach не меняет свои дочерние обьекты, скорее всего там используется object.clone
            for (int j = 0; j < cloneRes.length; j++) {
                if (mList.get(i).getTypeName().equals(cloneRes[j])) {
                    Log.d(TAG,"equals " +mList.get(i).getTypeName()+ " == "+ cloneRes[j]);
                    isUsed[i] = true;
                    cloneRes[j] = "";
                }
            }

        }
        int i = 0;
        for (boolean b :isUsed){
            Log.d(TAG,"updateArrayIsUSed " +i+ " "+ b);
            i++;
        }
    }

    private boolean hasDuplicate(){
        HashSet<String> hashSet = new HashSet<>();
        for (MyItemTranslate itemTranslate : mList){
            hashSet.add(itemTranslate.getTypeName());
        }
        System.out.println(
                "Size hashSet " + hashSet.size() + "\n" +
                "Size mList " + mList.size());
        return hashSet.size() != mList.size();
    }

    private int newSelectedItemFromResArray(){
        int rtn = 0;
        boolean b = false;
        while (!b){
            if (isUsed[rtn]){//если уже существует то попробовать следующий
                rtn++;
            }else {
                b = true;
                isUsed[rtn] = true;
//                System.out.println("is not used " + rtn);
            }
            if (rtn >= isUsed.length){
                Log.e(TAG, "infinite loop");
                break;
            }
        }

        return rtn;
    }



    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        //source - это новые символы, а dest - все остальные

        if (end-start > 1){
            String string = source.toString();

            if (string.contains("/")) {
                Toast.makeText(getContext(), " \"/\" is the service symbol", Toast.LENGTH_SHORT).show();
                return  string.replaceAll("/","");
            }
            return null;
        }else if (end-start == 1){
            if (source.charAt(start) == '\n'
                    && dest.length() >= 1
                    && (dest.charAt(dend-1) != ';' )) {
                return ";" + source;
            } else if (source.charAt(start) == '\n'
                    && dest.length() < 1) {
                return "";
            }
            if (source.charAt(start) == ';') {
                return source + "\n";
            }
            if (source.charAt(start) == '/') {
                Toast.makeText(getContext(), "it is the service symbol", Toast.LENGTH_SHORT).show();
                return "";
            }
        }

        return null;
    }

}
