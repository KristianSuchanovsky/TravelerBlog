package com.kristian.travelerblog;

import android.provider.BaseColumns;

public class DataTable {

    public DataTable() {

    }

    public static abstract class TableInfo implements BaseColumns{

        public static final String DATABASE_NAME = "Coordinates_info";
        public static final String TABLE_NAME = "CoordinatesTAB";
        public static final String TABLE_ID = "_id";
        public static final String LATITUDE = "Latitude";
        public static final String LONGITUDE = "Longitude";
        public static final String TIMESTAMP = "TimeStamp";
        public static final String TEXT = "Text";
    }
}
