package com.autumnbytes.porukakaoslika;

import android.provider.BaseColumns;

/**
 * Created by Simona Tošić on 14-May-17.
 */

public class UrlContractClass implements BaseColumns {

        //COLUMNS
        public static final String COLUMN_URL = "url";

        //DATABASE AND TABLE PROPERTIES
        public static final String TABLE_NAME = "primljene_poruke";
        public static final String DATABASE_NAME = "PRIMLJENEPORUKE.DB";
        public static final int DATABASE_VERSION = 1;

        //CREATE TABLE
        public static final String CREATE_TABLE = "create table " + TABLE_NAME +
                "(" +COLUMN_URL + " text not null unique );";

        //UPGRADE TABLE
        static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
