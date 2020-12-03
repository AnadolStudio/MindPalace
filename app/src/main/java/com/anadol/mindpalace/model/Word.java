package com.anadol.mindpalace.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.anadol.mindpalace.presenter.MyRandom;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.anadol.mindpalace.presenter.MyRandom.getRandomInts;

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
    // 7,5с > 30c > 2м > 8м > |30м| > 2ч > 32ч > 5д > 20д > 80д > 320д
    public static final long MIN_REPEAT_UNIT = TimeUnit.MINUTES.toMillis(30);
    //    public static final int MIN_REPEAT_UNIT = 1000 * 7;
    private static final String TAG = Word.class.getName();
    private int tableId;
    private UUID uuid;
    private UUID groupUUID;
    private String original;
    private String association;
    private String translate;
    private String comment;
    private int countLearn;
    private long time;
    private boolean isExam;

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
        this.countLearn = dataInts[1];

        time = in.readLong();
        boolean[] booleans = new boolean[1];
        in.readBooleanArray(booleans);
        isExam = booleans[0];
    }

    public Word(int tableId,
                @NonNull UUID uuid,
                @NonNull UUID groupUUID,
                @NonNull String original,
                @NonNull String association,
                @NonNull String translate,
                @NonNull String comment,
                int countLearn,
                long time,
                boolean isExam) {

        this.tableId = tableId;
        this.uuid = uuid;
        this.original = original;
        setTranslate(translate);
        setAssociation(association);
        this.groupUUID = groupUUID;
        this.comment = comment;
        setCountLearn(countLearn);
        this.time = time;
        this.isExam = isExam;
//        Log.i(TAG, dataToString());
    }

    public static boolean isRepeatable(long lastRepeat, long currentTime, int countLearn) {
        long time = currentTime - lastRepeat;
        return time >= (MIN_REPEAT_UNIT * Math.pow(4, countLearn));
    }

    public static long repeatTime(int countLearn) {

        return (long) (MIN_REPEAT_UNIT * Math.pow(4, countLearn));
    }

    @Override
    public String getName() {
        return original;
    }

    public boolean isRepeatable() {
        long currentTime = System.currentTimeMillis();
        return isRepeatable(time, currentTime, countLearn - 1);
    }
    public boolean isRepeatable(long currentTime) {
        return isRepeatable(time, currentTime, countLearn - 1);
    }

    public long getNextRepeatTime() {
        long rtn = (time + repeatTime(Math.max(0, countLearn - 1)));
        return time == 0 ? 0 : rtn;
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
                tableId, countLearn});

        dest.writeLong(time);
        dest.writeBooleanArray(new boolean[]{isExam});
    }

    public boolean isMultiTranslate() {
        return translate.contains(";"); // Возможно будет другой знак
    }

    public boolean isMultiAssociation() {
        return association.contains(";"); // Возможно будет другой знак
    }

//    public boolean isExistTranslate(String s) {
//        return translate.contains(s);

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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isExam() {

        return isExam;
    }

    public boolean readyToExam() {
        return countLearn >= 4;
    }

    public void setExam(boolean exam) {
        isExam = exam;
    }

    private String isNull(String s) {
        if (s == null) s = "";
        return s;
    }

    public String getMultiTranslateFormat() {
        // Формат для EditText
        return translate.replaceAll(";", ";\n");
    }

    public String getMultiAssociationFormat() {
        // Формат для EditText
        return association.replaceAll(";", ";\n");
    }

    public String getMultiAssociationFormatSpace() {
        // Формат для EditText
        return association.replaceAll(";", " ");
    }

    //Возвращает нужное слово из списка всех слов
    public String getOneOfMultiTranslates(int position) {
        String[] strings = translate.split(";");
        if (position < 0) {
            position = 0;
        }
        if (position >= strings.length) {
            position = strings.length - 1;
        }

        return strings[position];
    }

    public int getCountTranslates() {
        String[] strings = translate.split(";");
        return strings.length;
    }

    public String getOneOfMultiAssociation(int position) {
        String[] strings = association.split(";");
        if (position < 0) {
            position = 0;
        }
        if (position >= strings.length) {
            position = strings.length - 1;
        }

        return strings[position];
    }

    public int getCountAssociation() {
        String[] strings = association.split(";");
        return strings.length;
    }

    @NonNull
    public String getTranslate() {
        translate = isNull(translate);
        translate = deleteSpace(translate);
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = clearString(translate);
    }

    private String clearString(String string) {
        // Удаляю \n и последний ";", если он есть
        string = string.toLowerCase().trim().replaceAll("\n", "");
        int lastIndex = string.length() - 1;

        if (lastIndex < 0) lastIndex = 0;

        if (string.lastIndexOf(';') == lastIndex) {
            string = string.substring(0, lastIndex);
        }
        return string;
    }

    @NonNull
    public String getAssociation() {
        association = isNull(association);
        return association;
    }

    public void setAssociation(String association) {
        this.association = clearString(association);
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

    public String dataToString() {
        return "Word{" +
                "tableId=" + tableId +
                ", uuid=" + uuid +
                ", groupUUID=" + groupUUID +
                ", original='" + original + '\'' +
                ", association='" + association + '\'' +
                ", translate='" + translate + '\'' +
                ", countLearn=" + countLearn +
                ", isRepeatable=" + isRepeatable(time, System.currentTimeMillis(), countLearn) +
                ", isExam=" + isExam +
                '}';
    }

    @NonNull
    @Override
    public String toString() {
        return original + " " + association + " " + translate;
    }

    public Difficult getDifficult() {
        Difficult difficult = Difficult.EASY;

        if (countLearn == 2) {
            difficult = Difficult.MEDIUM;
        } else if (countLearn >= 3) {
            difficult = Difficult.HARD;
        }

        return difficult;
    }

    public int getCountLearn() {
        return countLearn;
    }

    public void setCountLearn(int countLearn) {
        if (countLearn < 0) {
            countLearn = 0;
        }
        this.countLearn = countLearn;
    }

    public String getRandomTranslate() {
        int size = getCountTranslates();
        int r = getRandom(size);
        return getOneOfMultiTranslates(r);
    }

    public String getRandomAssociation() {
        int size = getCountAssociation();
        int r = getRandom(size);
        return getOneOfMultiAssociation(r);
    }

    private int getRandom(int size) {
        return MyRandom.getRandomInts(1, size).get(0);
    }

    public enum Difficult {EASY, MEDIUM, HARD}
}