package com.julian.mapapplication;

import android.provider.BaseColumns;

public class ItemsContract {
    public static abstract class ItemsEntry implements BaseColumns {
        public static final String TABLE_NAME ="items";

        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String CORDILLERA = "cordillera";
        public static final String COUNTRY = "country";
        public static final String HEIGHT = "height";
        public static final String LAT = "lat";
        public static final String LNG = "lng";
        }
}
