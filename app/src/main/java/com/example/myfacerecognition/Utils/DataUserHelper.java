package com.example.myfacerecognition.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataUserHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "campusface";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "student";
    public static final String ID = "id";
    public static final String NOM = "nom";
    public static final String PRENOM = "prenom";
    public static String FILIERE = "filiere";
    public static String MATRICULE = "matricule";
    public static final String TRANCHE = "tranche";
    public static final String SOMME = "somme";
    public static final String TRANSACTION = "transactions";
    public static final String NIVEAU = "niveau";
    public static String ANNEE = "annee";

    public DataUserHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID + " TEXT, "
                + NOM + " TEXT,"
                + PRENOM + " TEXT,"
                + MATRICULE + " TEXT,"
                + FILIERE + " TEXT,"
                + NIVEAU + " TEXT,"
                + TRANSACTION + " TEXT,"
                + TRANCHE + " TEXT,"
                + SOMME + " TEXT,"
                + ANNEE + " TEXT)";
        db.execSQL(query);
    }

    public void addNewline(String id, String nom, String prenom, String matricule, String filiere, String niveau, String transaction, String tranche, String somme, String annee) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, id);
        values.put(NOM, nom);
        values.put(PRENOM, prenom);
        values.put(MATRICULE, matricule);
        values.put(FILIERE, filiere);
        values.put(NIVEAU, niveau);
        values.put(TRANSACTION, transaction);
        values.put(TRANCHE, tranche);
        values.put(SOMME, somme);
        values.put(ANNEE, annee);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void ondelete() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase dbs, int oldVersion, int newVersion) {
        dbs.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(dbs);
    }
}
