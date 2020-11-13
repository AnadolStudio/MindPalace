package com.anadol.mindpalace.presenter;

import android.os.Parcel;
import android.os.Parcelable;

public class GroupStatisticItem implements Parcelable {

    public static final Creator<GroupStatisticItem> CREATOR = new Creator<GroupStatisticItem>() {
        @Override
        public GroupStatisticItem createFromParcel(Parcel in) {
            return new GroupStatisticItem(in);
        }

        @Override
        public GroupStatisticItem[] newArray(int size) {
            return new GroupStatisticItem[size];
        }
    };
    String name;
    int needToLearn;
    int learning;
    int learned;

    public GroupStatisticItem(String name, int needToLearn, int learning, int learned) {
        this.name = name;
        this.needToLearn = needToLearn;
        this.learning = learning;
        this.learned = learned;
    }

    protected GroupStatisticItem(Parcel in) {
        name = in.readString();
        needToLearn = in.readInt();
        learning = in.readInt();
        learned = in.readInt();
    }

    public String getName() {
        return name;
    }

    public int getNeedToLearn() {
        return needToLearn;
    }

    public int getLearning() {
        return learning;
    }

    public int getLearned() {
        return learned;
    }

    public float[] getValues() {
        return new float[]{needToLearn, learning, learned};
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(needToLearn);
        dest.writeInt(learning);
        dest.writeInt(learned);
    }

    @Override
    public String toString() {
        return "GroupStatisticItem{" +
                "name='" + name + '\'' +
                ", needToLearn=" + needToLearn +
                ", learning=" + learning +
                ", learned=" + learned +
                '}';
    }
}
