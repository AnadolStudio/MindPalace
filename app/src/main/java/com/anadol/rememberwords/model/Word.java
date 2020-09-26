package com.anadol.rememberwords.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.UUID;

public class Word extends SimpleParent implements Parcelable, Comparable<Word> {

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
    //TODO удалить, когда версия DB будет равна 7 (Сейчас 5 (21.09.2020))
    private String nameGroup;

    public Word(Parcel in) {
        String[] dataStrings = new String[6];
        in.readStringArray(dataStrings);

        setOriginal(dataStrings[0]);
        setAssociation(dataStrings[1]);
        setTranslate(dataStrings[2]);
        this.uuid = UUID.fromString(dataStrings[3]);
        this.groupUUID = UUID.fromString(dataStrings[4]);
        setComment(dataStrings[5]);

        int[] dataInts = new int[1];
        in.readIntArray(dataInts);

        this.tableId = dataInts[0];
    }

    public Word(int tableId,
                @NonNull UUID uuid,
                @NonNull String original,
                @NonNull String translate,
                @NonNull String association,
                String groupUUID,
                @NonNull String comment) {

        this.tableId = tableId;
        this.uuid = uuid;
        this.original = original;
        translate = deleteWordTypes(translate);
        setTranslate(translate);
        this.association = association;

        //TODO удалить, когда версия DB будет равна 7 (Сейчас 5 (21.09.2020))
        if (groupUUID != null && !groupUUID.equals("")) {
            this.groupUUID = UUID.fromString(groupUUID);
        } else {
            this.groupUUID = null;
        }
        this.comment = comment;
    }

    public Word(int tableId,
                @NonNull UUID uuid,
                @NonNull String original,
                @NonNull String translate,
                @NonNull String association,
                @NonNull String nameGroup,
                String groupUUID,
                @NonNull String comment) {

        //TODO удалить, когда версия DB будет равна 7 (Сейчас 5 (21.09.2020))
        this(tableId, uuid, original, translate, association, groupUUID, comment);
        this.nameGroup = nameGroup;
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

    public String getNameGroup() {
        return nameGroup;
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
        translate = deleteSpace(translate);
        return translate;
    }

    public void setTranslate(String translate) {
        // Удаляю \n и последний ";", если он есть
        translate = translate.toLowerCase().trim().replaceAll("\n", "");
        int lastIndex = translate.length() - 1;

        if (lastIndex < 0) lastIndex = 0;

        if (translate.lastIndexOf(';') == lastIndex) {
            translate = translate.substring(0, lastIndex);
        }
        this.translate = translate;
    }

    private String deleteSpace(String s) {
        if (!s.contains(";")) return s;

        StringBuilder builder = new StringBuilder();
        String[] strings = s.split(";");

        for (int i = 0; i < strings.length; i++) {

            if (i != 0) builder.append(";");
            builder.append(strings[i].trim());
        }
        return builder.toString();
    }

    private String deleteWordTypes(String string) {
        String[] wordsTypes = new String[]{"n:", "prn:", "v:", "adj:", "adv:", "prep:", "conj:", "inter:"};
        for (String s : wordsTypes) {
            if (string.contains("/" + s)) {
                string = string.replace("/" + s, "");
            }
            if (string.contains(s)) {
                string = string.replace(s, "");
            }
        }
        return string;
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
        return original + " " + association + " " + translate;
    }
}