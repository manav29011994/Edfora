package com.manav.edfora;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manav on 16/12/17.
 */

public class UserHistory {

    private static final String TAG = UserHistory.class.getSimpleName();
    private static UserHistory historyDB = null;

    //db for song history
    public static final String SONGS_HISTORY = "songsHistory";

    //db for playlist
    public static final String PLAYLIST = "playList";



    private SQLiteDatabase db;

    //columnsName

    public static final String SONG_NAME = "song";
    private static final String SONG_URL = "url";
    private static final String ARTISTS = "artists";
    private static final String TIMESTAMP="timeStamp";
    private static final String ACTION="Action";
    private static final String IMAGEURL = "imageUrl";

    public UserHistory() {
        db = OlaPlayDB.getDB();
    }

    public static UserHistory getInstance() {
        if (historyDB == null) {
            historyDB = new UserHistory();
        }
        return historyDB;
    }

    public static void createTables(SQLiteDatabase sql)
    {
        String songHistory = " CREATE TABLE IF NOT EXISTS " + SONGS_HISTORY + " ( " + SONG_NAME + " varchar ,"
                + TIMESTAMP + " varchar,"
                + ACTION+ " varchar,"
                + SONG_URL + " varchar,"
                + IMAGEURL+ " varchar,"
                + ARTISTS + " varchar )";
        sql.execSQL(songHistory);

        String playlist = " CREATE TABLE IF NOT EXISTS " + PLAYLIST + " ( " + SONG_NAME + " varchar ,"
                + SONG_URL + " varchar,"
                + IMAGEURL+ " varchar,"
                + ARTISTS + " varchar )";
        sql.execSQL(playlist);
    }

    public void saveSongsHistory(Model data,String timestamp, String action) {
        if (data == null) {
            return;
        }
        db.beginTransaction();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SONG_NAME, data.getSong());
        contentValues.put(SONG_URL, data.getUrl());
        contentValues.put(ARTISTS, data.getArtists());
        contentValues.put(IMAGEURL, data.getCover_image());
        contentValues.put(TIMESTAMP,timestamp);
        contentValues.put(ACTION,action);
        db.insert(SONGS_HISTORY,null,contentValues);
      //  db.insertWithOnConflict(SONGS_HISTORY, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void savePlayList(Model data) {
        if (data == null) {
            return;
        }
        db.beginTransaction();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SONG_NAME, data.getSong());
        contentValues.put(SONG_URL, data.getUrl());
        contentValues.put(ARTISTS, data.getArtists());
        contentValues.put(IMAGEURL, data.getCover_image());
        //db.insert(SONGS_HISTORY,null,contentValues);
        db.insertWithOnConflict(PLAYLIST, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<Model> getSongHistory() {
        List<Model> songList = new ArrayList<>();
        Cursor c = db.query(SONGS_HISTORY, null, null,
                null, null, null, null);
        try {
            while (c.moveToNext()) {
                Model song = new Model();
                song.setSong(c.getString(c.getColumnIndex(SONG_NAME)));
                song.setArtists(c.getString(c.getColumnIndex(ARTISTS)));
                song.setUrl(c.getString(c.getColumnIndex(SONG_URL)));
                song.setCover_image(c.getString(c.getColumnIndex(IMAGEURL)));
                song.setTimeStamp(c.getString(c.getColumnIndex(TIMESTAMP)));
                song.setAction(c.getString(c.getColumnIndex(ACTION)));

                songList.add(song);
            }
        } catch (Throwable V) {
            V.printStackTrace();
        } finally {
            c.close();
        }
        return songList;
    }

    public List<Model> getPlayList() {
        List<Model> songList = new ArrayList<>();
        Cursor c = db.query(PLAYLIST, null, null,
                null, null, null, null);
        try {
            while (c.moveToNext()) {
                Model song = new Model();
                song.setSong(c.getString(c.getColumnIndex(SONG_NAME)));
                song.setArtists(c.getString(c.getColumnIndex(ARTISTS)));
                song.setUrl(c.getString(c.getColumnIndex(SONG_URL)));
                song.setCover_image(c.getString(c.getColumnIndex(IMAGEURL)));
                songList.add(song);
            }
        } catch (Throwable V) {
            V.printStackTrace();
        } finally {
            c.close();
        }
        return songList;
    }

    public boolean songPresentInDb(String url)
    {
        String Query = "Select * from " + PLAYLIST + " where " + SONG_URL + " like '%"  + url + "%'";
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public void removeFromPlayList(String url)
    {
        db.delete(PLAYLIST, SONG_URL + "=?", new String[]{url});
    }
}
