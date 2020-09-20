package com.anadol.rememberwords.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Comparator;
import java.util.UUID;

public class Word extends SimpleParent implements Parcelable, Comparable<Word> {
    public static final int TRUE = 1;
    public static final int FALSE = 0;
    public static final Parcelable.Creator<Word> CREATOR = new Parcelable.Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel source) {
            return new Word(source);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };
    private static final String TAG = "word";
    private int tableId;
    private UUID uuid;
    private UUID groupUUID;
    private String original;
    private String association;
    private String translate;
    private String comment;

    public Word(Parcel in) {
        String[] dataStrings = new String[6];
        in.readStringArray(dataStrings);

        setOriginal(dataStrings[0]);
        setAssociation(dataStrings[1]);
        setTranslate(dataStrings[2]);
        this.uuid = UUID.fromString(dataStrings[3]);
        this.groupUUID = UUID.fromString(dataStrings[4]);
        setComment(dataStrings[5]);

        int[] dataInts = new int[2];
        in.readIntArray(dataInts);

        this.tableId = dataInts[0];
    }

    public Word(int tableId,
                @NonNull UUID uuid,
                @NonNull String original,
                @NonNull String translate,
                @NonNull String association,
                @NonNull UUID groupUUID,
                @NonNull String comment) {

        this.tableId = tableId;
        this.uuid = uuid;
        this.original = original;
        this.translate = translate;
        this.association = association;
        this.groupUUID = groupUUID;
        this.comment = comment;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                original,
                association,
                translate,
                uuid.toString(),
                groupUUID.toString(),
                comment});

        dest.writeIntArray(new int[]{
                tableId});
    }

    public boolean isMultiTranslate() {
        return translate.contains(";"); // Возможно будет другой знак
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getUUIDString() {
        return uuid.toString();
    }

    @Override
    public int getTableId() {
        return tableId;
    }

    @NonNull
    public String getOriginal() {
        original = isNull(original);
        return original;
    }

    public void setOriginal(String original) {
        this.original = original.toLowerCase().trim();
    }

    @NonNull
    public String getAssociation() {
        association = isNull(association);
        return association;
    }

    public void setAssociation(String association) {
        this.association = association.toLowerCase().trim();
    }

    @NonNull
    public String getTranslate() {
        translate = isNull(translate);
        return translate;
    }

    public void setTranslate(String translate) {
        // Удаляю \n и последний ";", если он есть
        translate = translate.toLowerCase().trim().replaceAll("\n", "");
        int lastIndex = translate.indexOf(translate.length() - 1);

        if (translate.lastIndexOf(';') == lastIndex) {
            translate = translate.substring(0, lastIndex);
        }
        this.translate = translate;
    }

    public String getMultiTranslateFormat() {
        // Формат для EditText
        return translate.replaceAll(";", ";\n");
    }

    //Возвращает нужное слово из списка всех слов
    public String getOneOfMultiTranslates(int position) {
        String[] strings = translate.split(";");
        if (position >= strings.length) {
            position = strings.length - 1;
        }

        return strings[position];
    }

    public int getCountTranslates() {
        String[] strings = translate.split(";");
        return strings.length;
    }

    public boolean isExistTranslate(String s) {
        return translate.contains(s);
    }

    private String isNull(String s) {
        if (s == null) s = "";
        return s;
    }

    public UUID getGroupUUID() {
        return groupUUID;
    }

    public String getGroupUUIDString() {
        return groupUUID.toString();
    }

    public String getComment() {
        comment = isNull(comment);
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment.toLowerCase().trim();
    }

    @Override
    public int compareTo(@NonNull Word word) {
        return original.compareTo(word.getOriginal());
    }

    @NonNull
    @Override
    public String toString() {
        return original;
    }

    public static class OriginalCompare implements Comparator<Word> {
        @Override
        public int compare(Word o1, Word o2) {
            return o1.getOriginal().compareTo(o2.getOriginal());
        }
    }

    public static class TranslateCompare implements Comparator<Word> {
        @Override
        public int compare(Word o1, Word o2) {
            return o1.getTranslate().compareTo(o2.getTranslate());
        }
    }
}