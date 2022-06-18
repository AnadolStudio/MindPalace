package com.anadol.mindpalace.data.group;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.anadol.mindpalace.R;
import com.anadol.mindpalace.data.group.DataBaseSchema.Groups;

import java.util.UUID;

public class CreatorExampleGroup {
    // TODO: 08.07.2021
    //  1) Это можно будет скачать с сервера
    //  2) Тут используется AsyncTack, что явно плохо, т.к. он устаревший

    public static class AlphaNumericCode extends GroupExample {
        @Override
        void set() {
            setName("Буквенно-Цифровой код");
            setUuid("83a2b19f-f129-4047-83a5-7bae2164ac54");
            setDrawableString(Group.CreatorDrawable.getColorsStringFromInts(new int[]{0xFF6D004E, 0xFFAB006D, 0xFFC7004E}));
            setType(Group.TYPE_NUMBERS);
            setOriginals(new String[]{
                    "00", "01", "02", "03", "04", "05", "06", "07", "08", "09",
                    "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
                    "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
                    "30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
                    "40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
                    "50", "51", "52", "53", "54", "55", "56", "57", "58", "59",
                    "60", "61", "62", "63", "64", "65", "66", "67", "68", "69",
                    "70", "71", "72", "73", "74", "75", "76", "77", "78", "79",
                    "80", "81", "82", "83", "84", "85", "86", "87", "88", "89",
                    "90", "91", "92", "93", "94", "95", "96", "97", "98", "99"});
            setAssociations(new String[]{
                    "олень", "лук", "лоб", "нить", "нора", "лапа", "нож", "лом", "лев", "лед",
                    "конь", "кок", "куб", "кот", "гиря", "капа", "кожа", "гусь", "кофе", "кеды",
                    "белье", "бык", "боец", "бита", "царь", "цепь", "бош", "бусы", "буфф", "будда",
                    "тень", "утюг", "зубы", "туз", "туча", "утопия", "тушь", "змея", "язва", "тайд",
                    "руль", "рог", "рыба", "роза", "ручей", "чип", "ружье", "часы", "арфа", "радио",
                    "пила", "паук", "пицца", "путы", "печь", "поп", "пежо", "пояс", "пиво", "айпад",
                    "шина", "жук", "жаба", "щит", "шар", "шип", "жижа", "шум", "шеф", "ж/д",
                    "соль", "маяк", "мох", "сито", "меч", "суп", "мышь", "мим", "сова", "мед",
                    "вино", "веко", "овца", "ваза", "веер", "vip", "вши", "весы", "fifa", "вода",
                    "дыня", "дуга", "дуб", "доза", "дыра", "депо", "душа", "дама", "даф", "дeд"});
        }
    }

    public static class PlayingCard extends GroupExample {
        @Override
        void set() {
            setName("Игральные карты");
            setUuid("55c27522-7dd6-4ca2-a719-21ca702a8446");
            setDrawableString(Group.CreatorDrawable.getColorsStringFromInts(new int[]{0xFF550000, 0xFFAA0000, 0xFF550000}));
            setType(Group.TYPE_NUMBERS);
            setOriginals(new String[]{
                    "10 б", "10 к", "10 п", "10 ч", "2 б", "2 к", "2 п", "2 ч", "3 б", "3 к", "3 п", "3 ч", "4 б",
                    "4 к", "4 п", "4 ч", "5 б", "5 к", "5 п", "5 ч", "6 б", "6 к", "6 п", "6 ч", "7 б", "7 к", "7 п",
                    "7 ч", "8 б", "8 к", "8 п", "8 ч", "9 б", "9 к", "9 п", "9 ч", "валет б", "валет к", "валет п",
                    "валет ч", "дама б", "дама к", "дама п", "дама ч", "король к", "король ч", "король б", "король п",
                    "туз б", "туз к", "туз п", "туз ч", "джокер черный", "джокер красный"});
            setAssociations(new String[]{
                    "белье", "тень", "руль", "конь", "куб", "боец", "зубы", "лоб", "кот", "бита", "туз",
                    "нить", "гиря", "царь", "туча", "нора", "капа", "цепь", "утопия", "лапа", "кожа", "бош",
                    "тушь", "нож", "гусь", "бусы", "змея", "лом", "кофе", "буфф", "язва", "лев", "кеды", "будда",
                    "тайд", "лед", "паук", "жук", "маяк", "рог", "пицца", "жаба", "мох", "рыба", "щит", "роза",
                    "путы", "сито", "печь", "шар", "меч", "ручей", "дауд", "олень"});
        }
    }

    public static class Month extends GroupExample {
        @Override
        void set() {
            setName("Месяцы");
            setUuid("66c27522-7dd6-4ca2-a719-21ca702a8446");
            setDrawableString(Group.CreatorDrawable.getColorsStringFromInts(new int[]{0xFF005500, 0xFF00AA00, 0xFF005500}));
            setType(Group.TYPE_TEXTS);
            setOriginals(new String[]{
                    "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"});
            setAssociations(new String[] {
                "Дед Мороз", "Солдат", "Розы", "Клоун", "Танк", "Врач", "Пальма", "Парашют", "Рюкзак", "Ленин", "Микки Маус", "Снеговик"
            });
        }
    }

    public static class Creator extends AsyncTask<GroupExample, Void, Boolean> {
        private Context mContext;

        public void setContext(Context context) {
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(GroupExample... examples) {
            ContentResolver contentResolver = mContext.getContentResolver();
            boolean b = true;// Группа уже создана

            for (int j = 0; j < examples.length; j++) {

                GroupExample group = examples[j];

                GroupCursorWrapper cursor = new GroupCursorWrapper(
                        contentResolver.query(
                                Groups.CONTENT_URI,
                                null, Groups.UUID + " = ?", new String[]{group.getUuid().toString()},  null));

                if (cursor.getCount() != 0) {
                    continue;
                }

                b = false;

                ContentValues values = CreatorValues.createGroupValues(
                        group.getUuid(),
                        group.getName(),
                        group.getDrawableString(),
                        group.getType());
                contentResolver.insert(Groups.CONTENT_URI, values);

                String[] originals = group.getOriginals();
                String[] associations = group.getAssociations();

                for (int i = 0; i < originals.length; i++) {
                    values = CreatorValues.createWordsValues(
                            UUID.randomUUID(),
                            group.getUuid().toString(),
                            originals[i],
                            associations[i],
                            "", "", 0, 0, false);

                    contentResolver.insert(DataBaseSchema.Words.CONTENT_URI, values);
                }

            }
            return b;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            String s = !b ?
                    mContext.getString(R.string.was_upload) : mContext.getString(R.string.is_already_upload);
            Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
        }
    }
}
