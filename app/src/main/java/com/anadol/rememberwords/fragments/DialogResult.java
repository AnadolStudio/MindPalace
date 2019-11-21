package com.anadol.rememberwords.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.myList.MyRecyclerAdapter;
import com.anadol.rememberwords.myList.MyViewHolder;

import java.util.ArrayList;

import static com.anadol.rememberwords.activities.LearnDetailActivity.ANSWER_LIST;
import static com.anadol.rememberwords.activities.LearnDetailActivity.QUESTION_LIST;

public class DialogResult extends DialogFragment {
    public static final String RESULT = "result";
    public static final String CORRECT = "correct";
    public static final String CORRECT_ARRAY = "correctArray";
    public static final String WRONG = "wrong";

    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter adapter;
    private ArrayList<RecyclerItem> mList;


    public static DialogResult newInstance(String[] question,String[] answers,boolean[] correctWrong) {

        Bundle args = new Bundle();

        int correct = 0;
        int wrong = 0;

        for (boolean b :correctWrong){
           if (b){
               correct++;
           }else {
               wrong++;
           }
        }

        args.putInt(CORRECT,correct);
        args.putInt(WRONG,wrong);
        args.putBooleanArray(CORRECT_ARRAY,correctWrong);
        args.putStringArray(QUESTION_LIST,question);
        args.putStringArray(ANSWER_LIST,answers);
        DialogResult fragment = new DialogResult();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_learn_result,null);

        int c = getArguments().getInt(CORRECT);
        int w = getArguments().getInt(WRONG);
        /*String[] question = getArguments().getStringArray(QUESTION_LIST);
        String[] answers = getArguments().getStringArray(ANSWER_LIST);*/

        TextView correct = v.findViewById(R.id.text_correct);
        correct.setText(getResources().getQuantityString(R.plurals.correct_items, c,c));

        TextView wrong = v.findViewById(R.id.text_wrong);
        wrong.setText(getResources().getQuantityString(R.plurals.wrong_items, w,w));

        mList = new ArrayList<>();
        inflateList(mList);

        mRecyclerView = v.findViewById(R.id.recycler_view);
        createAdapter();
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayout);
        mRecyclerView.setAdapter(adapter);



        return new AlertDialog.Builder(getContext())
                .setView(v)
                .create();
    }

    private void inflateList(ArrayList<RecyclerItem> list){
        String[] question = getArguments().getStringArray(QUESTION_LIST);
        System.out.println("QUESTION" + question);
        String[] answers = getArguments().getStringArray(ANSWER_LIST);
        boolean[] b = getArguments().getBooleanArray(CORRECT_ARRAY);


        for (int i = 0; i<question.length;i++) {
            list.add(new RecyclerItem(question[i],answers[i],b[i]));
//            System.out.println(question[i]+" "+answers[i]+" "+b[i]);
        }
    }



    private void createAdapter() {

        adapter = new MyRecyclerAdapter(mList,R.layout.item_result_list);
        adapter.setCreatorAdapter(new MyRecyclerAdapter.CreatorAdapter() {// ДЛЯ БОЛЬШЕЙ ГИБКОСТИ ТУТ Я РЕАЛИЗУЮ СЛУШАТЕЛЯ И МЕТОДЫ АДАПТЕРА
            @Override
            public void createHolderItems(MyViewHolder holder) {
                TextView question = holder.itemView.findViewById(R.id.text_question);
                TextView answer = holder.itemView.findViewById(R.id.text_answer);
                holder.setViews(new View[]{question, answer});
            }

            @Override
            public void bindHolderItems(MyViewHolder holder) {
                View[] views = holder.getViews();
                final int position = holder.getAdapterPosition();
                String question = mList.get(position).getQ();
                String answer = mList.get(position).getA();
                boolean b = mList.get(position).isB();

                TextView textQ = (TextView) views[0];
                textQ.setText(question);

                TextView textA = (TextView) views[1];
                textA.setText(answer);


                if (b) {
                    holder.itemView.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                } else {
                    holder.itemView.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                }


            }
            @Override
            public void myOnItemDismiss(int position, int flag) {
            }
        });
        adapter.setListener(new MyRecyclerAdapter.Listeners() {
            @Override
            public void onClick(View view, int position) {
            }

            @Override
            public boolean onLongClick(View view, int position) {
                return false;
            }
        });

    }


    private void sendResult(int resultCode){
        if (getTargetFragment() == null){
            return;
        }

        Intent intent = new Intent();
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        sendResult(Activity.RESULT_OK);
    }



    private class RecyclerItem {
        String q;
        String a;
        boolean b;

        public RecyclerItem(String q, String a, boolean b) {
            this.q = q;
            this.a = a;
            this.b = b;
        }

        public String getQ() {
            return q;
        }

        public void setQ(String q) {
            this.q = q;
        }

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public boolean isB() {
            return b;
        }

        public void setB(boolean b) {
            this.b = b;
        }
    }
}
