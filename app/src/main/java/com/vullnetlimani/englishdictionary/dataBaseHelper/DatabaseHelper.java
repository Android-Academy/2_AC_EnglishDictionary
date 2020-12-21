package com.vullnetlimani.englishdictionary.dataBaseHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.vullnetlimani.englishdictionary.util.Constants;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String EN_WORD = "en_word";
    public static final String EN_DEFINITION = "en_definition";
    public static final String EXAMPLE = "example";
    public static final String SYNONYMS = "synonyms";
    public static final String ANTONYMS = "antonyms";
    private static final String DB_NAME = "eng_dictionary.db";
    private static final String TABLE_NAME = "words";
    private final Context context;
    private String DB_PATH = null;
    private SQLiteDatabase myDatabase;
    private String perqindja;
    private ProgressInterface mProgressInterface;

    public DatabaseHelper(@Nullable Context context, ProgressInterface mProgressInterface) {
        super(context, DB_NAME, null, 1);
        this.context = context;
        this.DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";

        if (mProgressInterface != null)
            this.mProgressInterface = mProgressInterface;

        Log.i("DB_PATH", DB_PATH);
    }


    public void createDatabase() throws IOException {

        boolean DBExist = checkDatabase();

        if (!DBExist) {

            this.getReadableDatabase();
            try {
                copyDatabase();
            } catch (IOException e) {
                throw new Error("Error Copying Database");
            }

        }

    }

    private void copyDatabase() throws IOException {
        InputStream myInput = context.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        long total = 0;

        int fileSize = myInput.available();

        Log.d(Constants.TAG, "fileSize - " + fileSize);

        while ((length = myInput.read(buffer)) > 0) {


            total = total + length;

            perqindja = String.valueOf(((total * 100) / fileSize));

            if (mProgressInterface != null)
                mProgressInterface.onProgressUpdate(perqindja);


            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public boolean checkDatabase() {

        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLException e) {
            Log.d(Constants.TAG, "Databaza nuk egziston - " + e.getMessage());
        }

        if (checkDB != null)
            checkDB.close();

        return checkDB != null;

    }

    public void openDatabase() throws SQLException {
        String path = DB_PATH + DB_NAME;
        myDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {

        if (myDatabase != null)
            myDatabase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
            this.getReadableDatabase();
            context.deleteDatabase(DB_NAME);
            copyDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Cursor getMeaning(String text) {

        return myDatabase.rawQuery("SELECT " + EN_DEFINITION + "," + EXAMPLE + "," + SYNONYMS + "," + ANTONYMS + " FROM " + TABLE_NAME + " WHERE " + EN_WORD + "==UPPER('" + text + "')", null);

    }

    public Cursor getSuggestions(String newText) {
        return myDatabase.rawQuery("SELECT _id" + ", " + EN_WORD + " FROM " + TABLE_NAME + " WHERE " + EN_WORD + " LIKE '" + newText + "%' LIMIT 40", null);
    }

    public Cursor getHistory() {
        return myDatabase.rawQuery("select distinct word, en_definition from history h join words w on h.word==w.en_word order by h._id desc", null);
    }

    public void insertHistory(String enWord) {

        myDatabase.execSQL("INSERT INTO history(word) VALUES (UPPER ('" + enWord + "'))");
    }

    public void deleteHistory() {
        myDatabase.execSQL("DELETE FROM history");
    }
}
