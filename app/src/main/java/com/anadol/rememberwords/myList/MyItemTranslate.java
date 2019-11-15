package com.anadol.rememberwords.myList;

import android.support.annotation.NonNull;

public class MyItemTranslate {
    private String typeName;
    private String words;

    public MyItemTranslate(String typeName, String words) {
        this.typeName = typeName;
        this.words = words;
//            System.out.println("type " + typeName +" words " + words);
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    @NonNull
    @Override
    public String toString() {
        return typeName + words;
    }


}
