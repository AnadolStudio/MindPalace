package com.anadol.rememberwords.myList;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class Word implements Parcelable,Comparable{
    private static final String TAG = "word";
    public static final int TRUE = 1;
    public static final int FALSE = 0;

    private String original;
    private String translate;
    private String multiTranslate;
    private String transcript;
    private String group;
    private String comment;
    private UUID id;
    private int isMultiTrans;

    public static class OriginalCompare implements Comparator<Word> {
        @Override
        public int compare(Word o1, Word o2) {
            return o1.getOriginal().compareTo(o2.getOriginal());
        }
    }
    public static class  TranslateCompare implements Comparator<Word>{
        @Override
        public int compare(Word o1, Word o2) {
            return o1.getTranslate().compareTo(o2.getTranslate());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                original,
                translate,
                transcript,
                id.toString(),
                group,
                comment,});
        dest.writeInt(isMultiTrans);
    }

    public static final Parcelable.Creator<Word> CREATOR = new Parcelable.Creator<Word>(){
        @Override
        public Word createFromParcel(Parcel source) {
            return new Word(source);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };

    public Word(Parcel in) {
        String[] data = new String[6];
        in.readStringArray(data);
        this.original = data[0];
        this.translate = data[1];
        this.transcript = data[2];
        this.id = UUID.fromString(data[3]);
        this.group = data[4];
        this.comment = data[5];
        this.isMultiTrans = in.readInt();
    }



    public Word(@NonNull UUID id,
                @NonNull String original,
                @NonNull String translate,
                @NonNull String transcript,
                @NonNull String group,
                @NonNull String comment,
                int isMultiTrans) {
        this.id = id;
        this.original = original;
        this.translate = translate;
        this.transcript = transcript;
        this.group = group;
        this.comment = comment;
        this.isMultiTrans = isMultiTrans;
    }


    public int hasMultiTrans() {
        return isMultiTrans;
    }

    public void setHasMultiTrans(int isMultiTrans) {
        this.isMultiTrans = isMultiTrans;
    }

    public UUID getId() {
        return id;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
        multiTranslate = null;
        System.out.println("setTranslate");
    }

    public String getMultiTranslate() {

        if (multiTranslate == null) {
            String[][] allWords = getMultiTranslateFormat();

            StringBuilder stringBuilder = new StringBuilder();
            for (String[] s: allWords){
                if (stringBuilder.length() != 0){
                    stringBuilder.append("/");
                }
                stringBuilder.append(s[0]).append(":").append(s[1]);

            }
            multiTranslate = stringBuilder.toString();
        }

        return multiTranslate;
    }


    public String[][] getMultiTranslateFormat() {
        String[] partOfSpeech = translate.split("/");//Делю по группам: сущ., гл., и т.д.
//        List<String> allWords = new ArrayList<>();
        String[][] allWords = new String[partOfSpeech.length][2];
        for (int i = 0; i < partOfSpeech.length; i++){
            //Тут происходит деление названия типа слов "существительное" и самих слов "слово один",
            //их разделительный знак - ":"
            //И создается массив массива String{часть речи, все слова одним String}
            allWords[i] = partOfSpeech[i].split(":",2);
//            System.out.println(allWords[i][0] +":"+ allWords[i][1]);
        }
        return allWords;
    }

    //Возвращает нужное слово из списка всех слов
    public String getOneTranslate(int word) {
        ArrayList<String> arrayList = addAllTranslates();

        if (word >= arrayList.size()) {
            word = arrayList.size()-1;
        }

        return arrayList.get(word);
    }

    public int getCountTranslates(){
        ArrayList<String> arrayList = addAllTranslates();
        return arrayList.size();
    }

    private ArrayList<String> addAllTranslates(){
        String[][] allWords = getMultiTranslateFormat();
        ArrayList<String> arrayList = new ArrayList<>();
        for (String[] s : allWords) {
            String[] tmp = s[1].replaceAll("\n", "").split(";");
            Collections.addAll(arrayList, tmp);
        }
        return arrayList;
    }

    public boolean isExistTranslate(String s){
        ArrayList<String> arrayList = addAllTranslates();
        return arrayList.contains(s.toLowerCase());
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getComment() {
        if (comment == null){//это было сделанно из-за ошибки при добавлении нового column. Temp
            comment = "";
        }
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return original.compareTo(((Word)o).getOriginal());
    }


    @NonNull
    @Override
    public String toString() {
        return original;
    }
}