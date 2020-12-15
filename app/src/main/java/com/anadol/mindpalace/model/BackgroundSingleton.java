package com.anadol.mindpalace.model;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.ArrayMap;
import android.util.Log;

import com.anadol.mindpalace.R;
import com.anadol.mindpalace.presenter.ComparatorMaker;
import com.anadol.mindpalace.presenter.GroupStatisticItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BackgroundSingleton {

    private static final String TAG = BackgroundSingleton.class.getName();
    public static final String GET_GROUPS = "get_groups";
    public static final String GET_GROUP_ITEM = "get_group_item";
    public static final String DELETE_GROUPS = "remove_group";
    public static final String INSERT_GROUP = "add_group";

    public static final String GET_WORDS = "words";
    public static final String SAVE_GROUP_AND_WORDS = "update_group_and_words";
    public static final String DELETE_WORDS = "delete_words";
    public static final String INSERT_WORD = "add_words";

    public static final String GET_GROUP_STATISTIC = "get_group_statistic";
    public static final String UPDATE_WORD_EXAM = "update_word_exam";

    private static BackgroundSingleton sBackground;

    private static Context mContext;

    private Observable<ArrayList<Group>> getGroups;
    private Observable<Group> getGroupItem;
    private Observable<Group> insertGroupItem;
    private Observable<ArrayList<Group>> deleteGroups;

    private Observable<ArrayList<Word>> getWords;
    private Observable<Integer> saveGroupAndWords;
    private Observable<Word> insertWordItem;
    private Observable<ArrayList<Word>> deleteWords;

    private Observable<ArrayList<GroupStatisticItem>> getGroupStatistic;
    private Observable<ArrayList<Word>> updateWordExam;


    private ArrayMap<String, Boolean> stackActions = new ArrayMap<>();
    private int lastItem = -1;

    private BackgroundSingleton(Context context) {
        mContext = context.getApplicationContext();
    }

    public static BackgroundSingleton get(Context context) {
        if (sBackground == null) {
            sBackground = new BackgroundSingleton(context);
        }
        return sBackground;
    }

    public ArrayMap<String, Boolean> getStackActions() {
        return stackActions;
    }

    public int getLastItem() {
        return lastItem;
    }

    private void removeFromStack(String getGroups) {
        stackActions.remove(getGroups);
    }

    private void addToStack(String insertGroup) {
        stackActions.put(insertGroup, true);
    }


    public Observable<ArrayList<Group>> getGroups() {

        if (getGroups == null || !stackActions.containsKey(GET_GROUPS)) {
            addToStack(GET_GROUPS);

            Log.i(TAG, "Observable GET_GROUPS: create");

            getGroups = Observable.create(emitter -> {

                try {

                    ArrayList<Group> mGroupsList = new ArrayList<>();

                    ContentResolver contentResolver = mContext.getContentResolver();
                    MyCursorWrapper cursor = new MyCursorWrapper(
                            contentResolver.query(
                                    DataBaseSchema.Groups.CONTENT_URI,
                                    null, null, null, null));

                    if (cursor.getCount() != 0) {

                        cursor.moveToFirst();

                        while (!cursor.isAfterLast()) {
                            mGroupsList.add(cursor.getGroup());
                            Log.i(TAG, "Id :" + cursor.getGroup().getTableId());
                            cursor.moveToNext();
                        }
                        Collections.sort(mGroupsList, ComparatorMaker.getComparator(
                                SettingsPreference.getGroupTypeSort(mContext),
                                SettingsPreference.getGroupOrderSort(mContext)));
                    } else {
                        Log.i(TAG, "getGroups: нет объектов");
                    }

                    cursor.close();

                    emitter.onNext(mGroupsList);
                    emitter.onComplete();

                } catch (Exception e) {
                    emitter.onError(e);
                }

            });
            getGroups = getGroups
                    .doOnComplete(() -> {
                        removeFromStack(GET_GROUPS);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache();
        }

        return getGroups;
    }

    public Observable<Group> getGroupItem(int itemId) {

        if (getGroupItem == null || !stackActions.containsKey(GET_GROUP_ITEM)) {
            lastItem = itemId;
            addToStack(GET_GROUP_ITEM);

            Log.i(TAG, "Observable GET_GROUP_ITEM: create");

            getGroupItem = Observable.create(emitter -> {
                try {

                    ContentResolver contentResolver = mContext.getContentResolver();
                    Group group = null;

                    MyCursorWrapper cursor = new MyCursorWrapper(
                            contentResolver.query(
                                    ContentUris.withAppendedId(
                                            DataBaseSchema.Groups.CONTENT_URI,
                                            itemId),
                                    null, null, null, null));
                    if (cursor.getCount() != 0) {
                        cursor.moveToFirst();
                        group = cursor.getGroup();
                    } else {
                        Log.i(TAG, "getGroup: группа не найдена");
                    }

                    cursor.close();

                    emitter.onNext(group);
                    emitter.onComplete();

                } catch (Exception e) {
                    emitter.onError(e);
                }
            });


            getGroupItem = getGroupItem
                    .subscribeOn(Schedulers.io())
                    .doOnComplete(() -> {
                        removeFromStack(GET_GROUP_ITEM);
                        lastItem = -1;
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache();
        }

        return getGroupItem;
    }

    public Observable<Group> insertGroup() {

        if (insertGroupItem == null || !stackActions.containsKey(INSERT_GROUP)) {
            addToStack(INSERT_GROUP);

            Log.i(TAG, "Observable INSERT_GROUP: create");

            insertGroupItem = Observable.create(emitter -> {
                try {

                    ContentResolver contentResolver = mContext.getContentResolver();
                    Group group;

                    UUID uuid = UUID.randomUUID();
                    String name = mContext.getString(R.string.new_group);

                    String colors = Group.CreatorDrawable.getColorsStringFromInts(Group.DEFAULT_COLORS);
                    ContentValues values = CreatorValues.createGroupValues(
                            uuid,
                            name,
                            colors,
                            Group.TYPE_NUMBERS);
                    Uri uri = contentResolver.insert(DataBaseSchema.Groups.CONTENT_URI, values);

                    // Это более правильный метод конвертации long в int
                    Long l = (ContentUris.parseId(uri));
                    int idNewGroup = l.intValue();

                    Log.i(TAG, "_ID new group : " + idNewGroup);
                    group = new Group(idNewGroup, uuid, colors, name, Group.TYPE_NUMBERS);

                    emitter.onNext(group);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            });


            insertGroupItem = insertGroupItem
                    .subscribeOn(Schedulers.io())
                    .doOnComplete(() -> {
                        removeFromStack(INSERT_GROUP);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache();
        }

        return insertGroupItem;
    }

    public Observable<ArrayList<Group>> deleteGroups(ArrayList<Group> groups) {

        if (deleteGroups == null || !stackActions.containsKey(DELETE_GROUPS)) {
            addToStack(DELETE_GROUPS);

            Log.i(TAG, "Observable DELETE_GROUPS : create");

            deleteGroups = Observable.create(emitter -> {

                try {

                    ContentResolver contentResolver = mContext.getContentResolver();

                    String uuidString;
                    for (Group g : groups) {
                        uuidString = g.getUUIDString();
                        // Удаляю Группу и слова этой группы
                        contentResolver.delete(
                                DataBaseSchema.Groups.CONTENT_URI,
                                DataBaseSchema.Groups.UUID + " = ?",
                                new String[]{uuidString});
                        contentResolver.delete(
                                DataBaseSchema.Words.CONTENT_URI,
                                DataBaseSchema.Words.UUID_GROUP + " = ?",
                                new String[]{uuidString});
                    }

                    emitter.onNext(groups);
                    emitter.onComplete();

                } catch (Exception e) {
                    emitter.onError(e);
                }

            });
            deleteGroups = deleteGroups
                    .doOnComplete(() -> {
                        removeFromStack(DELETE_GROUPS);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache();
        }

        return deleteGroups;
    }


    public Observable<ArrayList<Word>> getWords(String groupUUIDString) {

        if (getWords == null || !stackActions.containsKey(GET_WORDS)) {
            addToStack(GET_WORDS);

            Log.i(TAG, "Observable GET_WORDS: create");

            getWords = Observable.create(emitter -> {

                try {

                    ArrayList<Word> words = new ArrayList<>();

                    ContentResolver contentResolver = mContext.getContentResolver();
                    MyCursorWrapper cursor = new MyCursorWrapper(contentResolver.query(
                            DataBaseSchema.Words.CONTENT_URI,
                            null,
                            DataBaseSchema.Words.UUID_GROUP + " = ?",
                            new String[]{groupUUIDString}, null));

                    if (cursor.getCount() != 0) {

                        cursor.moveToFirst();

                        //TODO: реализовать порционную (ленивую) загрузку
                        while (!cursor.isAfterLast()) {
                            words.add(cursor.getWord());
                            cursor.moveToNext();
                        }
                        Collections.sort(words,
                                ComparatorMaker.getComparator(
                                        SettingsPreference.getWordTypeSort(mContext),
                                        SettingsPreference.getWordOrderSort(mContext)));
                    } else {
                        Log.i(TAG, "getWords: нет объектов");
                    }

                    cursor.close();

                    emitter.onNext(words);
                    emitter.onComplete();

                } catch (Exception e) {
                    emitter.onError(e);
                }

            });
            getWords = getWords
                    .doOnComplete(() -> {
                        removeFromStack(GET_WORDS);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache();
        }

        return getWords;
    }

    public Observable<Integer> saveGroupAndWords(Group group, ArrayList<Word> words) {

        if (saveGroupAndWords == null || !stackActions.containsKey(SAVE_GROUP_AND_WORDS)) {

            addToStack(SAVE_GROUP_AND_WORDS);

            Log.i(TAG, "Observable SAVE_GROUP_AND_WORDS: create");

            saveGroupAndWords = Observable.create(emitter -> {
                try {

                    ContentResolver contentResolver = mContext.getContentResolver();
                    ContentValues values = CreatorValues.createGroupValues(
                            group.getUUID(),
                            group.getName(),
                            group.getStringDrawable(),
                            group.getType());

                    contentResolver.update(
                            ContentUris.withAppendedId(DataBaseSchema.Groups.CONTENT_URI, group.getTableId()),
                            values,
                            null, null);
                    for (Word word : words) {
                        contentResolver.update(DataBaseSchema.Words.CONTENT_URI,
                                CreatorValues.createWordsValues(word),
                                DataBaseSchema.Words.UUID + " = ?",
                                new String[]{word.getUUIDString()});
                    }

                    emitter.onNext(words.size());
                    emitter.onComplete();

                } catch (Exception e) {
                    emitter.onError(e);
                }
            });


            saveGroupAndWords = saveGroupAndWords
                    .subscribeOn(Schedulers.io())
                    .doOnComplete(() -> {
                        removeFromStack(SAVE_GROUP_AND_WORDS);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache();
        }

        return saveGroupAndWords;
    }

    public Observable<Word> insertWord(String groupUUIDString) {

        if (insertWordItem == null || !stackActions.containsKey(INSERT_WORD)) {
            addToStack(INSERT_WORD);

            Log.i(TAG, "Observable INSERT_WORD: create");

            insertWordItem = Observable.create(emitter -> {
                try {
                    ContentResolver contentResolver = mContext.getContentResolver();
                    UUID uuid = UUID.randomUUID();
                    String original = "";
                    String association = "";
                    String translate = "";
                    String comment = "";
                    int countLearn = 0;
                    long time = 0;


                    Uri uri = contentResolver.insert(
                            DataBaseSchema.Words.CONTENT_URI,
                            CreatorValues.createWordsValues(
                                    uuid,
                                    groupUUIDString,
                                    original,
                                    translate,
                                    association,
                                    comment,
                                    countLearn,
                                    time,
                                    false));

                    Long l = (ContentUris.parseId(uri));
                    int idNewWord = Integer.valueOf(l.intValue());
                    Log.i(TAG, "_ID new word : " + idNewWord);

                    Word word = new Word(
                            idNewWord,
                            uuid,
                            UUID.fromString(groupUUIDString),
                            original,
                            association,
                            translate,
                            comment,
                            countLearn,
                            time,
                            false);

                    emitter.onNext(word);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            });


            insertWordItem = insertWordItem
                    .subscribeOn(Schedulers.io())
                    .doOnComplete(() -> {
                        removeFromStack(INSERT_WORD);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache();
        }

        return insertWordItem;
    }

    public Observable<ArrayList<Word>> deleteWords(ArrayList<Word> words) {

        if (deleteWords == null || !stackActions.containsKey(DELETE_WORDS)) {
            addToStack(DELETE_WORDS);
            Log.i(TAG, "Observable DELETE_GROUPS : create");

            deleteWords = Observable.create(emitter -> {

                try {
                    ContentResolver contentResolver = mContext.getContentResolver();

                    String uuidString;
                    for (Word w : words) {
                        uuidString = w.getUUIDString();

                        contentResolver.delete(DataBaseSchema.Words.CONTENT_URI,
                                DataBaseSchema.Groups.UUID + " = ?",
                                new String[]{uuidString});
                    }

                    emitter.onNext(words);
                    emitter.onComplete();

                } catch (Exception e) {
                    emitter.onError(e);
                }

            });
            deleteWords = deleteWords
                    .doOnComplete(() -> {
                        removeFromStack(DELETE_WORDS);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache();
        }

        return deleteWords;
    }


    public Observable<ArrayList<GroupStatisticItem>> getGroupStatistic() {

        if (getGroupStatistic == null || !stackActions.containsKey(GET_GROUP_STATISTIC)) {
            addToStack(GET_GROUP_STATISTIC);

            Log.i(TAG, "Observable GET_GROUP_STATISTIC: create");

            getGroupStatistic = Observable.create(emitter -> {

                try {
                    ArrayList<GroupStatisticItem> statisticItems = new ArrayList<>();

                    ContentResolver contentResolver = mContext.getContentResolver();

                    MyCursorWrapper groupCursor = new MyCursorWrapper(contentResolver.query(
                            DataBaseSchema.Groups.CONTENT_URI,
                            null, null, null, null));
                    Cursor wordCursor = null;

                    if (groupCursor.getCount() != 0) {
                        groupCursor.moveToFirst();

                        GroupStatisticItem item;
                        String name;
                        String uuidGroup;
                        int needToLearn = 0;
                        int learning = 0;
                        int learned = 0;


                        while (!groupCursor.isAfterLast()) {
                            name = groupCursor.getString(groupCursor.getColumnIndex(DataBaseSchema.Groups.NAME_GROUP));
                            uuidGroup = groupCursor.getString(groupCursor.getColumnIndex(DataBaseSchema.Groups.UUID));
                            // Need to learn
                            wordCursor = contentResolver.query(
                                    DataBaseSchema.Words.CONTENT_URI,
                                    new String[]{"COUNT(" + DataBaseSchema.Words._ID + ") AS count"},
                                    DataBaseSchema.Words.UUID_GROUP + " = ? AND (" + DataBaseSchema.Words.TIME + " = ? OR " + DataBaseSchema.Words.TIME + " IS NULL)",
                                    new String[]{uuidGroup, "0"}, null);

                            if (wordCursor != null) {
                                wordCursor.moveToFirst();
                                needToLearn = wordCursor.getInt(0);
                            }
                            // Learning
                            wordCursor = contentResolver.query(
                                    DataBaseSchema.Words.CONTENT_URI,
                                    new String[]{"COUNT(" + DataBaseSchema.Words._ID + ") AS count"},
                                    DataBaseSchema.Words.UUID_GROUP + " = ? AND " + DataBaseSchema.Words.TIME + " != ? AND " + DataBaseSchema.Words.EXAM + " = ?",
                                    new String[]{uuidGroup, "0", "0"}, null);

                            if (wordCursor != null) {
                                wordCursor.moveToFirst();
                                learning = wordCursor.getInt(0);
                            }
                            // Learned
                            wordCursor = contentResolver.query(
                                    DataBaseSchema.Words.CONTENT_URI,
                                    new String[]{"COUNT(" + DataBaseSchema.Words._ID + ") AS count"},
                                    DataBaseSchema.Words.UUID_GROUP + " = ? AND " + DataBaseSchema.Words.EXAM + " = ?",
                                    new String[]{uuidGroup, "1"}, null);

                            if (wordCursor != null) {
                                wordCursor.moveToFirst();
                                learned = wordCursor.getInt(0);
                            }
                            Log.i(TAG, "doInBackground: " + needToLearn + " " + learning + " " + learned);
                            item = new GroupStatisticItem(name, needToLearn, learning, learned);
                            statisticItems.add(item);

                            groupCursor.moveToNext();
                        }
                    }

                    Log.i(TAG, "doInBackground: " + statisticItems.toString());

                    groupCursor.close();
                    if (wordCursor != null) {
                        wordCursor.close();
                    }

                    emitter.onNext(statisticItems);
                    emitter.onComplete();

                } catch (Exception e) {
                    emitter.onError(e);
                }

            });
            getGroupStatistic = getGroupStatistic
                    .doOnComplete(() -> {
                        removeFromStack(GET_GROUP_STATISTIC);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache();
        }

        return getGroupStatistic;
    }

    public Observable<ArrayList<Word>> updateWordsExam(ArrayList<Word> mWords) {

        if (updateWordExam == null || !stackActions.containsKey(UPDATE_WORD_EXAM)) {
            addToStack(UPDATE_WORD_EXAM);

            Log.i(TAG, "Observable UPDATE_WORD_EXAM: create");

            updateWordExam = Observable.create(emitter -> {

                try {
                    ArrayList<Word> words = new ArrayList<>(mWords);

                    ContentResolver contentResolver = mContext.getContentResolver();
                    MyCursorWrapper cursor;

                    for (Word word : words) {

                        cursor = new MyCursorWrapper(contentResolver.query(
                                DataBaseSchema.Words.CONTENT_URI,
                                null,
                                DataBaseSchema.Words.UUID + " = ?",
                                new String[]{word.getUUIDString()}, null));

                        if (cursor.getCount() != 0) {
                            cursor.moveToFirst();
                            cursor.getWordExam(word);
                        }
                    }

                    emitter.onNext(words);
                    emitter.onComplete();

                } catch (Exception e) {
                    emitter.onError(e);
                }

            });
            updateWordExam = updateWordExam
                    .doOnComplete(() -> {
                        removeFromStack(UPDATE_WORD_EXAM);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache();
        }

        return updateWordExam;
    }

    // Для симмулирования сложной операции
    private void delay() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            Thread.sleep(i * 500);
            Log.i(TAG, "" + i);
        }
    }

}
