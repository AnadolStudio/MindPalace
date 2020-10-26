package com.anadol.rememberwords.presenter;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.anadol.rememberwords.model.Word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
    private Word mWord;
    private String question;
    private String trueAnswer;
    private String[] falseAnswers; // минимум 3
    private String userAnswer;
    private String[] allAnswersRandomOrder;

    public Question(String question, String trueAnswer, String[] falseAnswers, Word word) {
        this.question = question;
        this.trueAnswer = trueAnswer;
        this.falseAnswers = falseAnswers;
        mWord = word;
    }

    private Question(Parcel in) {
        question = in.readString();
        trueAnswer = in.readString();
        falseAnswers = in.createStringArray();
        userAnswer = in.readString();
        mWord = in.readParcelable(mWord.getClass().getClassLoader());
    }

    public String getUUID() {
        return mWord.getUUIDString();
    }

    public int getCountLearn() {
        return mWord.getCountLearn();
    }

    public long getTime() {
        return mWord.getTime();
    }

    public boolean isExam() {
        return mWord.isExam();
    }

    public Word getWord() {
        return mWord;
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

    public void createAllAnswersRandomOrder() {
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
        if (allAnswersRandomOrder == null || allAnswersRandomOrder.length == 0) {
            createAllAnswersRandomOrder();
        }
        return allAnswersRandomOrder;
    }

    public boolean isUserAnswerCorrect() {
        // TODO: остановился на следующем:
        //  3) реализовать более точную проверку ответов (для Answer);
        //  7) comment(вместо него будет допинфа о кол-ве поторений,последней сдаче и т.п.) в Detail;
        //  8) иной способо select words в Detail;
        //  9) Выполнить оставшиеся "TO DO".

        boolean isCorrect = false;
        String s1 = trueAnswer.toLowerCase();
        String s2 = userAnswer.toLowerCase();

        if (s1.equals(s2)) {
            isCorrect = true;
        } else {
            // TODO:
            //  проверка типа Связь при MultiTranslate
            //  проверка типа Даты

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
        dest.writeParcelable(mWord, flags);
    }

    public String[] toPuzzle() {
        ArrayList<String> puzzle = new ArrayList<>();
        // будет использоваться 1-3 ложных ассоциации (зависит от сложности)
        ArrayList<String> allAnswers = getAllAnswers(getFalseAnswers().length / 2);

        String[] split;

        int countSpace = 0;
        for (int i = 0; i < allAnswers.size(); i++) {
            split = allAnswers.get(i).split(" ");
            countSpace += split.length - 1;
            Log.i(TAG, "split: " + Arrays.toString(split) + " countSpace " + countSpace);
            Collections.addAll(puzzle, split);
        }
        allAnswers = new ArrayList<>(puzzle);
        puzzle.clear();

        int index;
        int increment;
        int length;
        String string;

        for (int i = 0; i < allAnswers.size(); i++) {
            string = allAnswers.get(i);
            index = 0;
            length = getLengthPuzzle(string.length());

            for (int j = 0; j < length; j++) {
                increment = (j + 1) * (string.length() / length);
                Log.i(TAG, "increment: " + increment);
                if (j == length - 1) {// если последний круг в цикле, то берем остаток
                    Log.i(TAG, "toPuzzle: substring(index) " + string.substring(index));
                    puzzle.add(string.substring(index));
                } else {
                    Log.i(TAG, "toPuzzle: substring(index, increment) " + string.substring(index, increment));
                    puzzle.add(string.substring(index, increment));
                }
                index = increment;
            }
        }
        for (int i = 0; i < countSpace; i++) {
            puzzle.add(" ");
        }
        Log.i(TAG, "toPuzzle: " + puzzle);
        puzzle = MyRandom.getRandomArrayList(puzzle, puzzle.size());
        return puzzle.toArray(new String[0]);
    }

    private ArrayList<String> getAllAnswers(int countFalseAnswers) {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(getTrueAnswer());
        for (int i = 0; i < countFalseAnswers; i++) {
            arrayList.add(getFalseAnswers()[i]);
        }
        return arrayList;
    }

    private int getLengthPuzzle(int length) {
        int i;
        if (length < 6) {
            i = 2;
        } else {
            i = 3;
        }
        return i;
    }
}
