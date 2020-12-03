package com.anadol.mindpalace.model;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.anadol.mindpalace.R;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class Group extends SimpleParent implements Parcelable, Comparable<Group> {
    public static final int TYPE_NUMBERS = R.string.numbers;
    public static final int TYPE_DATES = R.string.dates;
    public static final int TYPE_TEXTS = R.string.texts;
    public static final int TYPE_LINK = R.string.link;
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
    private static final String TAG = Group.class.getName();
    private int tableId;
    private UUID mId;
    private String drawable;
    private String mName;
    private int type;

    private Group(Parcel in) {
        String[] data = new String[3];
        int[] ints = new int[2];
        in.readStringArray(data);
        in.readIntArray(ints);

        mName = data[0];
        mId = UUID.fromString(data[1]);
        drawable = data[2];
        tableId = ints[0];
        type = ints[1];
    }

    public Group(int tableId, UUID id, String drawable, String name, int type) {
        this.tableId = tableId;
        mId = id;
        this.drawable = drawable;
        mName = name;
        setType(type);
    }

    public Group(Group group) {
        this(group.getTableId(), group.getUUID(), group.getStringDrawable(), group.getName(), group.getType());
    }

    public static String getColorsStringFromInts(int[] colors) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < colors.length; i++) {

            if (i != 0) stringBuilder.append(";");

            stringBuilder.append(colors[i]);
        }
        return stringBuilder.toString();
    }

    public static int[] getDefaultColors() {
        return new int[]{0xFF000000, 0xFF004064, 0xFF0080E1};
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{mName, mId.toString(), drawable});
        dest.writeIntArray(new int[]{tableId, type});
    }

    private Drawable createDrawable(int[] colors) {
        return new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{colors[0], colors[1], colors[2]});
    }

    @Override
    public int compareTo(@NonNull Group o) {
        return mName.compareTo(o.getName());
    }

    @Override
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
        if (!drawable.contains("content")) {
            return getColorsFromString(drawable);
        } else {
            return getDefaultColors();
        }
    }

    public void setColors(int[] colors) {
        drawable = getColorsStringFromInts(colors);
    }

    public static int[] getColorsFromString(@Nullable String colors) {
        if (colors == null || colors.contains("content")) {
            return getDefaultColors();
        }

        String[] strings = colors.split(";");
        int[] ints = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            ints[i] = Integer.parseInt(strings[i]);
        }
        return ints;
    }

    public String getStringDrawable() {
        return drawable;
    }

    public void getImage(ImageView imageView) {
//        Log.i(TAG, "getImage: " + drawable);
        if (drawable.contains("content")) {
            Drawable placeholder = createDrawable(getDefaultColors());

            Picasso.get()
                    .load(drawable)
                    .error(placeholder)
                    .placeholder(placeholder)
                    .fit()
                    .centerCrop()
                    .into(imageView);
        } else {
            int[] colors = getColorsFromString(drawable);
            imageView.setImageDrawable(createDrawable(colors));
        }
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        switch (type) {
            case TYPE_NUMBERS:
            case TYPE_DATES:
            case TYPE_TEXTS:
            case TYPE_LINK:
                this.type = type;
                break;

            default:
                this.type = TYPE_NUMBERS;
        }
    }

    public void setPathPhoto(Uri uriPhoto) {
        drawable = uriPhoto.toString();
    }

    public boolean isPhotoDrawable() {
        return drawable.contains("content");
    }

    @Override
    public String toString() {
        return getName();
    }
}
