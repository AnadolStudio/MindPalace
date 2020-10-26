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
        public static final String TABLE_NAME = "groups";

        public static final String _ID = BaseColumns._ID;
        public static final String NAME_GROUP = "name_group";
        public static final String UUID = "uuid";
        public static final String DRAWABLE = "drawable";
        public static final String TYPE = "type";
    }


    public static final class Words implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_WORDS);
        public static final String TABLE_NAME = "words";

        public static final String _ID = BaseColumns._ID;
        public static final String UUID = "uuid";
        public static final String UUID_GROUP = "uuid_group";
        public static final String ORIGINAL = "original";
        public static final String ASSOCIATION = "association";
        public static final String TRANSLATE = "translate";
        public static final String COMMENT = "comment";
        public static final String COUNT_LEARN = "count_learn";
        public static final String EXAM = "exam";
        public static final String TIME = "time";
    }

}
