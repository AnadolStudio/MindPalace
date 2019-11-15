package com.anadol.rememberwords.myList;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.anadol.rememberwords.fragments.MyFragment;
import java.util.ArrayList;
import java.util.UUID;

public class Group implements Parcelable, Comparable<Group>{
    public static final int NON_COLOR = 0;

    private UUID mId;
    private  int[] mColors; //всегда содержит 3 обькта, если цвет отсутствует ставиться "0"
    private  String mName;
    private Drawable mDrawable;


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
                Color.RED,Color.GREEN,Color.BLUE
            };
        }
        mDrawable = createDrawable();

    }

    private Drawable createDrawable(){
        Drawable drawable = null;
        int count = 0;
        for (int i :
                mColors) {
//            System.out.println(i +"\n" + NON_COLOR);
            if (i != NON_COLOR) {
                count++;
            }
        }

        switch (count){
            case 1:
                drawable = new ColorDrawable(mColors[0]);
                break;
            case 2:
                drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{mColors[0],mColors[1]});
                break;
            case 3:
                drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{mColors[0],mColors[1],mColors[2]});
                break;
        }
        mDrawable = drawable;
        return drawable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{mName,mId.toString()});
        dest.writeIntArray(mColors);
    }

    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>(){
        @Override
        public Group createFromParcel(Parcel source) {
            return new Group(source);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    @Override
    public int compareTo(@NonNull Group o) {
        return mName.compareTo(o.getName());
    }


    public Group(UUID id, int[]colors, String name) {
        mId = id;
        mColors = colors;
        mName = name;
        mDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, mColors);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public UUID getId() {
        return mId;
    }
    public String getIdString() {
        return mId.toString();
    }

    public int[] getColors() {
        return mColors;
    }

    public void setColors(int[] colors) {
        mColors = colors;
    }

    public Drawable getGroupDrawable() {
        return createDrawable();
    }


    @Override
    public String toString() {
        return getName();
    }

}
