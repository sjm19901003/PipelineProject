package com.dianping.pipeline.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


public class PipelineDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "PipelineDBHelper";
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private String DB_NAME;
    private String POINT_TABLE_NAME = DB_NAME + "_points_table";
    private String LINE_TABLE_NAME = DB_NAME + "_line_table";

    private String CREATE_POINT_TABLE = "create table " + DB_NAME + "_points_table ("
            + "_id integer primary key autoincrement, "
            + "name text,"
            + "code text, "
            + "type text, "
            + "feature text, "
            + "x double, "
            + "y double, "
            + "h double, "
            + "welldepth text, "
            + "manhole text, "
            + "manholesize text, "
            + "auxtype text, "
            + "note text, "
            + "jpgno integer, "
            + "mark integer, "
            + "res text)";

    private String CREATE_LINE_TABLE = "create table " + DB_NAME + "_line_table ("
            + "_id integer primary key autoincrement, "
            + "name text,"
            + "startid text, "
            + "endid text, "
            + "pipetype text, "
            + "startdepth double, "
            + "enddepth double, "
            + "pipesize text, "
            + "amount integer, "
            + "holeuse integer, "
            + "matero text, "
            + "pipenum text, "
            + "material text, pressure text, flow text, enbed text, btime text, pipeuser text, "
            + "road text, species text, "
            + "note text)";

    public PipelineDBHelper(Context context,
                            String name,
                            SQLiteDatabase.CursorFactory factory,
                            int version) {
        super(context, name, factory, version);
        mContext = context;
        DB_NAME = name;
    }

    @Override
    public String getDatabaseName() {
        return super.getDatabaseName();
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mDatabase = db;
        db.execSQL(CREATE_POINT_TABLE);
        db.execSQL(CREATE_LINE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public synchronized void close() {
        super.close();
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    //删除整个数据库
    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(DB_NAME);
    }

    //往数据库的点表中插入一条数据
    public void insertPoint(ContentValues values) {
        if (values == null) {
            Log.e(TAG, "ContentValues object is null");
            return;
        }
        if (mDatabase == null) {
            mDatabase = getWritableDatabase();
        }
        mDatabase.insert(POINT_TABLE_NAME, null, values);
        Toast.makeText(mContext, "成功插入点", Toast.LENGTH_SHORT).show();
    }

    //从数据库的点表中删除一条数据
    public void deletePoint(int id) throws Exception {
        if (id < 0) {
            throw new Exception("delete id must not be zero(0).");
        }

        if (mDatabase == null) {
            mDatabase = getWritableDatabase();
        }
        mDatabase.delete(POINT_TABLE_NAME, "_id=?", new String[]{String.valueOf(id)});
    }

    public void deletePoint(String ptName) throws Exception{
        if (TextUtils.isEmpty(ptName)) {
            throw new Exception("name can not be null or empty.");
        }

        if (mDatabase == null) {
            mDatabase = getWritableDatabase();
        }
        mDatabase.delete(POINT_TABLE_NAME, "name=?", new String[]{ptName});
    }

    public void updatePoint(ContentValues values, int id) throws Exception {
        if (values == null) {
            throw new Exception("ContentValues object is null");
        }
        if (id < 0) {
            throw new Exception("id must not be zero(0).");
        }

        if (mDatabase == null) {
            mDatabase = getWritableDatabase();
        }
        mDatabase.update(POINT_TABLE_NAME, values, "_id=?", new String[]{String.valueOf(id)});
    }

    public void updatePoint(ContentValues values, String name) throws Exception {
        if (values == null) {
            throw new Exception("ContentValues object is null");
        }
        if (TextUtils.isEmpty(name)) {
            throw new Exception("name must not be empty.");
        }

        if (mDatabase == null) {
            mDatabase = getWritableDatabase();
        }
        mDatabase.update(POINT_TABLE_NAME, values, "name=?", new String[]{name});
    }

    //查询数据点表
    public Cursor queryPoint() {
        if (mDatabase == null) {
            mDatabase = getWritableDatabase();
        }
        Cursor cursor = mDatabase.query(POINT_TABLE_NAME, null, null, null, null, null, null);
        return cursor;
    }

    //往数据库的线表中插入一条数据
    public void insertLine(ContentValues values) {
        if (values == null) {
            Log.e(TAG, "ContentValues object is null");
            return;
        }
        if (mDatabase == null) {
            mDatabase = getWritableDatabase();
        }
        mDatabase.insert(LINE_TABLE_NAME, null, values);
        Toast.makeText(mContext, "成功插入线", Toast.LENGTH_SHORT).show();
    }

    //从数据库的线表中删除一条数据
    public void deleteLine(String name) throws Exception {
        if (TextUtils.isEmpty(name)) {
            throw new Exception("line name must not be empty.");
        }

        if (mDatabase == null) {
            mDatabase = getWritableDatabase();
        }
        mDatabase.delete(LINE_TABLE_NAME, "name=?", new String[]{name});

    }

    public void updateLine(ContentValues values, int id) throws Exception{
        if (values == null) {
            throw new Exception("ContentValues object is null");
        }
        if (id < 0) {
            throw new Exception("id must not be zero(0).");
        }

        if(mDatabase == null) {
            mDatabase = getWritableDatabase();
        }

        mDatabase.update(LINE_TABLE_NAME, values, "_id=?",new String[]{String.valueOf(id)});
    }

    //查询数据线表
    public Cursor queryLine() {
        if (mDatabase == null) {
            mDatabase = getWritableDatabase();
        }
        Cursor cursor = mDatabase.query(LINE_TABLE_NAME, null, null, null, null, null, null);
        return cursor;
    }
}
