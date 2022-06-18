package com.anadol.mindpalace.data.group;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.anadol.mindpalace.data.group.DataBaseSchema.Groups;
import com.anadol.mindpalace.data.group.DataBaseSchema.Words;

public class MyContentProvider extends ContentProvider {
    private static final int GROUPS = 1000;
    private static final int GROUPS_ID = 1001;

    private static final int WORDS = 2000;
    private static final int WORDS_ID = 2001;

    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(DataBaseSchema.AUTHORITIES, DataBaseSchema.PATH_GROUPS, GROUPS);
        sUriMatcher.addURI(DataBaseSchema.AUTHORITIES, DataBaseSchema.PATH_GROUPS + "/#", GROUPS_ID);
        sUriMatcher.addURI(DataBaseSchema.AUTHORITIES, DataBaseSchema.PATH_WORDS, WORDS);
        sUriMatcher.addURI(DataBaseSchema.AUTHORITIES, DataBaseSchema.PATH_WORDS + "/#", WORDS_ID);
    }

    private DatabaseHelper mDatabaseHelper;

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public GroupCursorWrapper query(@NonNull Uri uri, @Nullable String[] columns, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);

        switch (match){
            // Должен только возвращать курсор
            case GROUPS:
                cursor = database.query(
                        Groups.TABLE_NAME,
                        columns,
                        selection,
                        selectionArgs,
                        null,null, sortOrder);
                break;

            case WORDS:
                cursor = database.query(
                        Words.TABLE_NAME,
                        columns,
                        selection,
                        selectionArgs,
                        null,null, sortOrder);
                break;
            case GROUPS_ID:
                selection = Groups._ID +" = ?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(
                        Groups.TABLE_NAME,
                        columns,
                        selection,
                        selectionArgs,
                        null, null,null);
                break;
            case WORDS_ID:
                selection = Words._ID +" = ?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(
                        Words.TABLE_NAME,
                        columns,
                        selection,
                        selectionArgs,
                        null, null,null);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }
        return new GroupCursorWrapper(cursor);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        // Не понимаю зачем мне type
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // Проверку на null в значениях values я не делая потому, что они создаются через CreatorValues
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        long id;

        switch (match){
            // Должен только возвращать курсор
            case GROUPS:
                id = database.insert(Groups.TABLE_NAME, null,values);
                break;

            case WORDS:
                id = database.insert(Words.TABLE_NAME, null,values);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }

        if (id == -1){
            return null;
        }

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int id;
        switch (match) {
            case GROUPS:
                id =  database.delete(
                        Groups.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case WORDS:
                id =  database.delete(
                        Words.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case GROUPS_ID:
                selection = Groups._ID +" = ?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                id = database.delete(Groups.TABLE_NAME,selection,selectionArgs);
                break;
            case WORDS_ID:
                selection = Words._ID +" = ?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                id = database.delete(Words.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }
        return  id;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int id;

        switch (match){
            case GROUPS:
                id = database.update(Groups.TABLE_NAME, values, selection, selectionArgs);
                break;
            case WORDS:
                id =  database.update(Words.TABLE_NAME, values, selection, selectionArgs);
                break;
            case GROUPS_ID:
                selection = Groups._ID +" = ?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                id = database.update(Groups.TABLE_NAME, values,selection,selectionArgs);
                break;
            case WORDS_ID:
                selection = Words._ID +" = ?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                id = database.update(Words.TABLE_NAME, values, selection,selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }

        if (id == -1){
            return 0;
        }
        return id;
    }
}
