package com.anadol.rememberwords.presenter;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;

import static com.anadol.rememberwords.presenter.MyRandom.getRandomInts;

public class Question implements Parcelable {
    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
    private static final String TAG = Question.class.getName();
    private String UUID;
    private String question;
    private String trueAnswer;
    private String[] falseAnswers; // минимум 3
    private String userAnswer;
    private String[] allAnswersRandomOrder; // минимум 3

    public Question(String question, String trueAnswer, String[] falseAnswers, String uuid) {
        this.question = question;
        this.trueAnswer = trueAnswer;
        this.falseAnswers = falseAnswers;
        this.UUID = uuid;
    }

    private Question(Parcel in) {
        question = in.readString();
        trueAnswer = in.readString();
        falseAnswers = in.createStringArray();
        userAnswer = in.readString();
        UUID = in.readString();
    }

    public String getQuestion() {
        return question;
    }

    public String getTrueAnswer() {
        return trueAnswer;
    }

    public String[] getFalseAnswers() {
        return falseAnswers;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer.toLowerCase().trim();
    }

    public void createAllAnswersRandomOrder(){
        int length = falseAnswers.length + 1;// + 1 true Answer
        ArrayList<Integer> integers = getRandomInts(length, length);

        String[] strings = new String[length];

        for (int i = 0; i < length; i++) {
            if (i == falseAnswers.length) {
                strings[integers.get(i)] = trueAnswer;
            } else {
                strings[integers.get(i)] = falseAnswers[i];
            }
        }
//        Log.i(TAG, "getAllAnswersRandomOrder: " + Arrays.toString(strings));
        allAnswersRandomOrder = strings;
    }

    public String[] getAllAnswersRandomOrder() {
        if (allAnswersRandomOrder == null || allAnswersRandomOrder.length == 0){
            createAllAnswersRandomOrder();
        }
        return allAnswersRandomOrder;
    }

    public boolean isUserAnswerCorrect() {
        // TODO: остановился на следующем:
        //  Решено! 1) необходимо сначала довести до ума дизайн LearnQuiz;
        //  Решено! 2) обновить DialogResult, он будеь в виде BottomSheet;
        //  !Отложено 3) реализовать более точную проверку ответов;
        //  Решено! 4) реализовать MultiAssociation;
        //  Решено! 5) протестировать Learn с MultiAssociation;
        //  6) занятся LearnPuzzle;
        //  7) comment в Detail;
        //  8) иной способо select words в Detail;
        //  9) Выполнить оставшиеся "TO DO".

        boolean isCorrect = false;
        String s1 = trueAnswer.toLowerCase();
        String s2 = userAnswer.toLowerCase();

        if (s1.equals(s2)) {
            isCorrect = true;
        } else {
            // TODO тут необходимо реализовать более точную проверку
        }
        return isCorrect;
    }

    @Override
    public String toString() {
        return "Question{" +
                "question='" + question + '\'' +
                ", trueAnswer='" + trueAnswer + '\'' +
                ", falseAnswers=" + Arrays.toString(falseAnswers) +
                ", userAnswer='" + userAnswer + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        dest.writeString(trueAnswer);
        dest.writeStringArray(falseAnswers);
        dest.writeString(userAnswer);
        dest.writeString(UUID);
    }
}
