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
import com.anadol.mindpalace.domain.sortusecase.ComparatorMaker;
import com.anadol.mindpalace.presenter.GroupStatisticItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BackgroundSingleton {

    private static final String TAG = BackgroundSingleton.class.getName();
    private static BackgroundSingleton sBackground;
    private static Context mContext;
    private ArrayMap<String, Observable> stackActions = new ArrayMap<>();
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

    public ArrayMap<String, Observable> getStackActions() {
        return stackActions;
    }

    public int getLastItem() {
        return lastItem;
    }

    private void removeFromStack(DatabaseApiKeys key) {
        Log.i(TAG, "removeFromStack: " + key.name());
        stackActions.remove(key.name());
    }

    private void addToStack(DatabaseApiKeys key, Observable observable) {
        stackActions.put(key.name(), observable);
    }

    public Observable<ArrayList<Group>> getGroups() {
        Observable<ArrayList<Group>> getGroups = (Observable<ArrayList<Group>>) getStackActions().get(DatabaseApiKeys.GET_GROUPS.name());

        if (getGroups == null || !stackActions.containsKey(DatabaseApiKeys.GET_GROUPS.name())) {


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

                        // Делается сортировка, изходя из последних настроек
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
                        removeFromStack(DatabaseApiKeys.GET_GROUPS);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache();

            addToStack(DatabaseApiKeys.GET_GROUPS, getGroups);
        }
        return getGroups;
    }

    public Observable<Group> getGroupItem(int itemId) {
        Observable<Group> getGroupItem = (Observable<Group>) getStackActions().get(DatabaseApiKeys.GET_GROUP_ITEM.name());

        if (getGroupItem == null || !stackActions.containsKey(DatabaseApiKeys.GET_GROUP_ITEM.name())) {
            lastItem = itemId;

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
                        removeFromStack(DatabaseApiKeys.GET_GROUP_ITEM);
                        lastItem = -1;
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache();

            addToStack(DatabaseApiKeys.GET_GROUP_ITEM, getGroupItem);
        }

        return getGroupItem;
    }

    public Observable<Group> insertGroup() {
        Observable<Group> insertGroupItem = (Observable<Group>) getStackActions().get(DatabaseApiKeys.INSERT_GROUP.name());
        // TODO: 11.07.2021
        //  Возможно следует добавить проверку на правильное приведение типов.

        if (insertGroupItem == null || !stackActions.containsKey(DatabaseApiKeys.INSERT_GROUP.name())) {

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
                        removeFromStack(DatabaseApiKeys.INSERT_GROUP);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache();
            addToStack(DatabaseApiKeys.INSERT_GROUP, insertGroupItem);
        }

        return insertGroupItem;
    }

    public Observable<ArrayList<Group>> deleteGroups(ArrayList<Group> groups) {
        Observable<ArrayList<Group>> deleteGroups = (Observable<ArrayList<Group>>) getStackActions().get(DatabaseApiKeys.DELETE_GROUPS.name());

        if (deleteGroups == null || !stackActions.containsKey(DatabaseApiKeys.DELETE_GROUPS.name())) {

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
                        removeFromStack(DatabaseApiKeys.DELETE_GROUPS);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache();

            addToStack(DatabaseApiKeys.DELETE_GROUPS, deleteGroups);
        }

        return deleteGroups;
    }

    public Observable<ArrayList<Word>> getWords(String groupUUIDString) {
        Observable<ArrayList<Word>> getWords = (Observable<ArrayList<Word>>) getStackActions().get(DatabaseApiKeys.GET_WORDS.name());

        if (getWords == null || !stackActions.containsKey(DatabaseApiKeys.GET_WORDS.name())) {

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
                        removeFromStack(DatabaseApiKeys.GET_WORDS);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache();

            addToStack(DatabaseApiKeys.GET_WORDS, getWords);
        }

        return getWords;
    }

    public Observable<Integer> saveGroupAndWords(Group group, ArrayList<Word> words) {
        Observable<Integer> saveGroupAndWords = (Observable<Integer>) getStackActions().get(DatabaseApiKeys.SAVE_GROUP_AND_WORDS.name());

        if (saveGroupAndWords == null || !stackActions.containsKey(DatabaseApiKeys.SAVE_GROUP_AND_WORDS.name())) {

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
                        removeFromStack(DatabaseApiKeys.SAVE_GROUP_AND_WORDS);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache();

            addToStack(DatabaseApiKeys.SAVE_GROUP_AND_WORDS, saveGroupAndWords);
        }

        return saveGroupAndWords;
    }

    public Observable<Word> insertWord(String groupUUIDString) {
        Observable<Word> insertWordItem = (Observable<Word>) getStackActions().get(DatabaseApiKeys.INSERT_WORD.name());

        if (insertWordItem == null || !stackActions.containsKey(DatabaseApiKeys.INSERT_WORD.name())) {

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
                        removeFromStack(DatabaseApiKeys.INSERT_WORD);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache();

            addToStack(DatabaseApiKeys.INSERT_WORD, insertWordItem);
        }

        return insertWordItem;
    }

    public Observable<ArrayList<Word>> deleteWords(ArrayList<Word> words) {
        Observable<ArrayList<Word>> deleteWords = (Observable<ArrayList<Word>>) getStackActions().get(DatabaseApiKeys.DELETE_WORDS.name());

        if (deleteWords == null || !stackActions.containsKey(DatabaseApiKeys.DELETE_WORDS.name())) {

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
                        removeFromStack(DatabaseApiKeys.DELETE_WORDS);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache();

            addToStack(DatabaseApiKeys.DELETE_WORDS, deleteWords);
        }

        return deleteWords;
    }

    public Observable<ArrayList<GroupStatisticItem>> getGroupStatistic() {

        Observable<ArrayList<GroupStatisticItem>> getGroupStatistic = (Observable<ArrayList<GroupStatisticItem>>) getStackActions().get(DatabaseApiKeys.GET_GROUP_STATISTIC.name());

        if (getGroupStatistic == null || !stackActions.containsKey(DatabaseApiKeys.GET_GROUP_STATISTIC.name())) {

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
                        removeFromStack(DatabaseApiKeys.GET_GROUP_STATISTIC);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache();

            addToStack(DatabaseApiKeys.GET_GROUP_STATISTIC, getGroupStatistic);
        }

        return getGroupStatistic;
    }

    public Observable<ArrayList<Word>> updateWordsExam(ArrayList<Word> mWords) {

        Observable<ArrayList<Word>> updateWordExam = (Observable<ArrayList<Word>>) getStackActions().get(DatabaseApiKeys.UPDATE_WORD_EXAM.name());

        if (updateWordExam == null || !stackActions.containsKey(DatabaseApiKeys.UPDATE_WORD_EXAM.name())) {

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
                        removeFromStack(DatabaseApiKeys.UPDATE_WORD_EXAM);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache();

            addToStack(DatabaseApiKeys.UPDATE_WORD_EXAM, updateWordExam);
        }

        return updateWordExam;
    }

    // Для симмулирования сложной операции
    private void delay(int sec) throws InterruptedException {
        for (int i = 0; i < sec; i++) {
            Thread.sleep(i * 1000);
            Log.i(TAG, "" + i);
        }
    }

    public enum DatabaseApiKeys {
        GET_GROUPS,
        GET_GROUP_ITEM,
        DELETE_GROUPS,
        INSERT_GROUP,
        GET_WORDS,
        SAVE_GROUP_AND_WORDS,
        DELETE_WORDS,
        INSERT_WORD,
        GET_GROUP_STATISTIC,
        UPDATE_WORD_EXAM
    }

}
