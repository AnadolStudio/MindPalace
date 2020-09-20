package com.anadol.rememberwords.model;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.UUID;

public class Group extends SimpleParent implements Parcelable, Comparable<Group> {
    public static final int NON_COLOR = 0;
    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel source) {
            return new Group(source);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };
    private int tableId;
    private UUID mId;
    private int[] mColors; //всегда содержит 3 обькта, если цвет отсутствует ставиться "0"
    private String mName;

    private Group(Parcel in) {
        String[] data = new String[2];
        in.readStringArray(data);
        mName = data[0];
        mId = UUID.fromString(data[1]);
        mColors = new int[3];
        try {
            in.readIntArray(mColors);
        } catch (Exception e) {
            e.printStackTrace();
            mColors = new int[]{
                    Color.BLUE, Color.LTGRAY, Color.BLACK
            };
        }
        tableId = in.readInt();
    }

    public Group(int tableId, UUID id, String drawable, String name) {
        this.tableId = tableId;
        mId = id;
        mColors = getColorsFromString(drawable);
        mName = name;
    }

    public static String getColorsStringFromInts(int[] colors) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < colors.length; i++) {

            if (i != 0) stringBuilder.append(";");

            stringBuilder.append(colors[i]);
        }

        return stringBuilder.toString();
    }

    private Drawable createDrawable() {
        Drawable drawable = null;
        int count = 0;
        for (int i :
                mColors) {
//            System.out.println(i +"\n" + NON_COLOR);
            if (i != NON_COLOR) {
                count++;
            }
        }

        switch (count) {
            case 1:
                drawable = new ColorDrawable(mColors[0]);
                break;
            case 2:
                drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{mColors[0], mColors[1]});
                break;
            case 3:
                drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{mColors[0], mColors[1], mColors[2]});
                break;
        }
        return drawable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{mName, mId.toString()});
        dest.writeIntArray(mColors);
        dest.writeInt(tableId);
    }

    @Override
    public int compareTo(@NonNull Group o) {
        return mName.compareTo(o.getName());
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name.trim();
    }

    public UUID getUUID() {
        return mId;
    }

    public int getTableId() {
        return tableId;
    }

    public String getUUIDString() {
        return mId.toString();
    }

    public int[] getColors() {
        return mColors;
    }

    public void setColors(int[] colors) {
        mColors = colors;
    }

    private int[] getColorsFromString(String colors) {
        String[] strings = colors.split(";");
        int[] ints = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            ints[i] = Integer.parseInt(strings[i]);
        }
        return ints;
    }

    public Drawable getGroupDrawable() {
        return createDrawable();
    }

    @Override
    public String toString() {
        return getName();
    }

}
