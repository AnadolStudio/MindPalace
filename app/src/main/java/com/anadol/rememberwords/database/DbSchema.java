package com.anadol.rememberwords.database;

public class DbSchema {

    public static final class Tables{
        public static final String GROUPS = "groups";
        public static final String WORDS = "words";

        public static final class Cols{
            public static final String NAME_GROUP = "name_group";
            public static final String ORIGINAL = "original";
            public static final String TRANSLATE = "translate";
            public static final String TRANSCRIPTION = "transcription";
            public static final String UUID = "uuid";
            public static final String COLOR_ONE = "color_one";
            public static final String COLOR_TWO = "color_two";
            public static final String COLOR_THREE = "color_three";
            public static final String COMMENT = "comment";
            public static final String IS_MULTI_TRANS = "is_multi_trans";

            public static final String OLD_COLOR_START = "color_start";
            public static final String OLD_COLOR_END = "color_end";
        }
    }

}
