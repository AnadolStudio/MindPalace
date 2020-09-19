package com.anadol.rememberwords.model;

import android.net.Uri;
import android.provider.BaseColumns;

public final class DataBaseSchema {
    public static final String AUTHORITIES = "ru.anadolstudio.mindplace";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITIES);

    public static final String PATH_GROUPS = "groups";
    public static final String PATH_WORDS = "words";

    public static final class Groups implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_GROUPS);
//        public static final Uri CONTENT_ITEM_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_GROUPS + "/#");

        public static final String TABLE_NAME = "groups";

        public static final String _ID = BaseColumns._ID;
        public static final String NAME_GROUP = "name_group";
        public static final String UUID = "uuid";
        // Всегда будет 3 цвета
        public static final String COLOR = "color";
    }


    public static final class Words implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_WORDS);
//        public static final Uri CONTENT_ITEM_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_WORDS + "/#");
        public static final String TABLE_NAME = "words";

        public static final String _ID = BaseColumns._ID;
        // TODO возможно вместо NAME_GROUP стоит использовать GROUP_UUID
        //  тогда именна групп смогут повторятся
        public static final String NAME_GROUP = "name_group";
        public static final String UUID = "uuid";

        public static final String ORIGINAL = "original";
        // Асоциаций может быть несколько, храниться будут одной строкой, String.split через "/"
        public static final String ASSOCIATION = "ASSOCIATION";
        public static final String TRANSLATE = "translate";
    }

}
