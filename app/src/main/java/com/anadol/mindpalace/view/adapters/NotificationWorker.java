package com.anadol.mindpalace.view.adapters;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.anadol.mindpalace.domain.utils.RandomUtil;
import com.anadol.mindpalace.data.group.Group;
import com.anadol.mindpalace.data.group.GroupCursorWrapper;
import com.anadol.mindpalace.R;
import com.anadol.mindpalace.data.group.DataBaseSchema;
import com.anadol.mindpalace.data.group.Word;
import com.anadol.mindpalace.view.screens.learn.LearnActivity;
import com.anadol.mindpalace.view.screens.groupdetail.LearnStartBottomSheet;
import com.anadol.mindpalace.view.screens.groupdetail.GroupDetailActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class NotificationWorker extends Worker {
    public static final String WORDS_ID = "word_id";
    private static final String TAG = NotificationWorker.class.getName();
    private static final String CHANEL_ID = "chanel_id";
    private int readyToRepeat = 0;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public static int createNotificationId(ArrayList<Word> words) {
        StringBuilder builder = new StringBuilder();
        for (Word w : words) {
            if (w.isRepeatable()) {
                builder.append(w.getTableId());
            }
        }
        return builder.toString().hashCode();
        // Можно было бы прсото использовать Integer.parse(), но что если число будет слишком большим для Integer?
        // Поэтому использую hashCode()
    }

    @NonNull
    @Override
    public Result doWork() {
        String[] ids = getInputData().getStringArray(WORDS_ID);

        Context context = getApplicationContext();
        ContentResolver contentResolver = context.getContentResolver();
        ArrayList<Word> words = getWords(ids, contentResolver);
        Group mGroup = getGroup(words, contentResolver);

        if (mGroup == null) {
            return Result.failure();
        }

        int nId = createNotificationId(words);
        createNotification(words, mGroup, nId);

        return Result.success();
    }

    private void createNotification(ArrayList<Word> words, Group mGroup, int nId) {
        Context context = getApplicationContext();

        ArrayList<Word> mWords = RandomUtil.getRandomArrayList(words, words.size());
        Intent i;

        if (readyToRepeat >= LearnStartBottomSheet.MIN_COUNT_WORDS) {
            i = LearnActivity.newIntent(
                    context,
                    mWords,
                    mGroup.getType(),
                    LearnStartBottomSheet.getTypeTest(mWords),
                    LearnStartBottomSheet.getRouteTest(mWords));
        } else {
            i = GroupDetailActivity.newIntent(context, mGroup);
        }

        PendingIntent pi = PendingIntent.getActivity(context, nId, i, 0);

        Resources resources = context.getResources();
        String contentText = resources.getQuantityString(R.plurals.association_repeat, readyToRepeat, readyToRepeat);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        createChanel(notificationManager);

        Log.i(TAG, "createNotification");
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pi)
                .setContentTitle(mGroup.getName())
                .setContentText(contentText)
                .setAutoCancel(true)
//                .addAction(android.R.drawable.ic_media_play, resources.getString(R.string.repeat), pi)
                .build();

        notificationManager.notify(nId, notification);
    }

    private Group getGroup(ArrayList<Word> words, ContentResolver contentResolver) {
        Group mGroup = null;

        if (!words.isEmpty()) {
            GroupCursorWrapper cursor = new GroupCursorWrapper(contentResolver.query(
                    DataBaseSchema.Groups.CONTENT_URI,
                    null,
                    DataBaseSchema.Groups.UUID + " = ?",
                    new String[]{words.get(0).getGroupUUIDString()}, null));

            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                mGroup = cursor.getGroup();
            }
        }

        return mGroup;
    }

    private ArrayList<Word> getWords(String[] ids, ContentResolver contentResolver) {
        ArrayList<Word> words = new ArrayList<>();
        GroupCursorWrapper cursor;


        for (String id : ids) {
            cursor = new GroupCursorWrapper(contentResolver.query(
                    DataBaseSchema.Words.CONTENT_URI,
                    null,
                    DataBaseSchema.Words.UUID + " = ?",
                    new String[]{id}, null));

            if (cursor.getCount() != 0) {
                cursor.moveToFirst();

                Word word = cursor.getWord();
                if (word.isRepeatable()) readyToRepeat++;
                words.add(word);
            }
        }

        return words;
    }

    private void createChanel(NotificationManagerCompat manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANEL_ID, "Mind Palace Chanel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
    }
}
