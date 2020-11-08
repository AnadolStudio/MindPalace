package com.anadol.rememberwords.view.Services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.model.DataBaseSchema;
import com.anadol.rememberwords.model.Group;
import com.anadol.rememberwords.model.MyCursorWrapper;
import com.anadol.rememberwords.model.Word;
import com.anadol.rememberwords.presenter.MyRandom;
import com.anadol.rememberwords.view.Activities.GroupDetailActivity;
import com.anadol.rememberwords.view.Activities.LearnActivity;
import com.anadol.rememberwords.view.Dialogs.LearnStartBottomSheet;

import java.util.ArrayList;
import java.util.Arrays;

public class NotificationService extends IntentService {

    private static final String TAG = NotificationService.class.getName();
    private static final String WORDS_ID = "words_id";
    private static final int REPEAT = 1;
    private static final String CHANEL_ID = "my_chanel";

    public NotificationService() {
        super(TAG);
    }

    public static Intent newIntent(Context context, String[] ids) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.putExtra(WORDS_ID, ids);
        return intent;
    }

    public static void setServiceAlarm(Context context, ArrayList<Word> words) {
        String[] ids = new String[words.size()];
        int minCount = -1;
        Word word;

        for (int i = 0; i < words.size(); i++) {
            word = words.get(i);
            ids[i] = word.getUUIDString();
            // Поиск самого малаго промежутка для повторения
            if (minCount == -1 || minCount > word.getCountLearn()) {
                minCount = word.getCountLearn() - 1;// Данный countLearn уже новый, а счет должен идти по старому
            }
        }
        Log.i(TAG, "setServiceAlarm: ids " + words + "\n min count: " + minCount);

        Intent intent = NotificationService.newIntent(context, ids);
//        long longTime = System.currentTimeMillis();
//        int time = (int) longTime;
//        Log.i(TAG, "setServiceAlarm: intTime " + time + " longTime " + longTime);

        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        long alarm = Word.repeatTime(minCount);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + alarm, pi);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String[] ids = intent.getStringArrayExtra(WORDS_ID);
            Log.i(TAG, "onHandleIntent: ids " + Arrays.toString(ids));
            new Background(this).execute(ids);
        }
    }

    class Background extends AsyncTask<String, Void, ArrayList<Word>> {
        private Group mGroup;
        private int readyToRepeat = 0;
        private Context mContext;

        public Background(Context context) {
            mContext = context;
        }

        @Override
        protected ArrayList<Word> doInBackground(String... strings) {
            ContentResolver contentResolver = getContentResolver();

            ArrayList<Word> words = new ArrayList<>();
            MyCursorWrapper cursor;
            Word word;
            for (String id : strings) {

                cursor = new MyCursorWrapper(contentResolver.query(
                        DataBaseSchema.Words.CONTENT_URI,
                        null,
                        DataBaseSchema.Words.UUID + " = ?",
                        new String[]{id}, null));

                if (cursor.getCount() != 0) {
                    cursor.moveToFirst();
                    word = cursor.getWord();
                    if (word.isRepeatable()) readyToRepeat++;
                    words.add(word);
                }
            }
            if (!words.isEmpty()) {
                cursor = new MyCursorWrapper(contentResolver.query(
                        DataBaseSchema.Groups.CONTENT_URI,
                        null,
                        DataBaseSchema.Groups.UUID + " = ?",
                        new String[]{words.get(0).getGroupUUIDString()}, null));

                if (cursor.getCount() != 0) {
                    cursor.moveToFirst();
                    mGroup = cursor.getGroup();
                }
            }

            return words;
        }

        @Override
        protected void onPostExecute(ArrayList<Word> words) {
            super.onPostExecute(words);

            StringBuilder builder = new StringBuilder();
            for (Word w : words) {
                builder.append(w.getTableId());
            }
            int nId = builder.toString().hashCode();

            ArrayList<Word> mWords = MyRandom.getRandomArrayList(words, words.size());
            Intent intent;
            if (readyToRepeat >= LearnStartBottomSheet.MIN_COUNT_WORDS) {
                intent = LearnActivity.newIntent(
                        mContext,
                        mWords,
                        mGroup.getType(),
                        LearnStartBottomSheet.getTypeTest(mWords),
                        LearnStartBottomSheet.getRouteTest(mWords));
            } else {
                intent = GroupDetailActivity.newIntent(mContext, mGroup);
            }

            PendingIntent pi = PendingIntent.getActivity(mContext, nId, intent, 0);

            Resources resources = mContext.getResources();
            String contentText = resources.getQuantityString(R.plurals.association_repeat, readyToRepeat, readyToRepeat);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

            createChanel(notificationManager);

            Notification notification = new NotificationCompat.Builder(mContext, CHANEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(pi)
                    .setContentTitle(mGroup.getName())
                    .setContentText(contentText)// TODO в GDF будут помечаться готовые к повторению ассоциации
                    .setAutoCancel(true)
                    .addAction(android.R.drawable.ic_media_play, getString(R.string.repeat), pi)
                    .build();

            notificationManager.notify(nId, notification);
        }
    }

    private void createChanel(NotificationManagerCompat manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =
                    new NotificationChannel(CHANEL_ID, "Mind Palace Chanel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
    }

}
