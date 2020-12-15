package com.anadol.mindpalace.model;

import androidx.annotation.NonNull;

public abstract class SimpleParent {
    public abstract String getName();

    @NonNull
    public abstract String toString();

    public abstract int getTableId();

    public abstract String getUUIDString();
}
