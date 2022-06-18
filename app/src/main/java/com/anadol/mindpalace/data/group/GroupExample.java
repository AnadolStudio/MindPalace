package com.anadol.mindpalace.data.group;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import com.anadol.mindpalace.data.group.Group;

import java.util.UUID;

public abstract class GroupExample {
    private String name;
    private UUID uuid;
    private int type;
    private String drawable;// Градиет
    private String[] originals;
    private String[] associations;

    public GroupExample() {
        set();
    }

    abstract void set();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDrawableString() {
        return drawable;
    }

    public void setDrawableString(String drawable) {
        this.drawable = drawable;
    }

    public Drawable getDrawable() {
        return new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, Group.CreatorDrawable.getColorsFromString(drawable));
    }

    public String[] getOriginals() {
        return originals;
    }

    public void setOriginals(String[] originals) {
        this.originals = originals;
    }

    public String[] getAssociations() {
        return associations;
    }

    public void setAssociations(String[] associations) {
        this.associations = associations;
    }
}
