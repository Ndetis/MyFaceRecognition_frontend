package com.example.myfacerecognition.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mycampusface";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "header";
    public static final String CODE_UE = "Code_ue";
    public static final String FILIERE = "Filière";
    public static final String INTITULE = "Intitulé";
    public static final String SEMESTRE = "Semestre";
    public static final String GRADE = "Grade";
    public static final String ANNEE = "Annee";
    private static final String ID_COL = "id";


    public DataHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CODE_UE + " TEXT,"
                + FILIERE + " TEXT,"
                + INTITULE + " TEXT,"
                + SEMESTRE + " TEXT,"
                + GRADE + " TEXT,"
                + ANNEE + " TEXT)";
        db.execSQL(query);
    }

    public void addNewline(String code_ue, String filiere, String intitule, String semestre, String grade, String annee) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CODE_UE, code_ue);
        values.put(FILIERE, filiere);
        values.put(INTITULE, intitule);
        values.put(SEMESTRE, semestre);
        values.put(GRADE, grade);
        values.put(ANNEE, annee);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

