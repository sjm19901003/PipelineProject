package com.dianping.pipeline.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;


public class PipelineProjectListDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "PipelineProjectDBHelper";
    private String PIPELINE_PROJECT_DB_NAME;
    private static final String PIPELINE_PROJECT_LIST_TABLE_NAME = "project_list";
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private static final String CREATE_PIPELINE_PROJ_LIST_TABLE = "create table project_list ("
            + "_id integer primary key autoincrement, "
            + "name text, "
            + "city text, "
            + "date text, "
            + "owner text)";

    public PipelineProjectListDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
        PIPELINE_PROJECT_DB_NAME = name;
    }

    @Override
    public String getDatabaseName() {
        return super.getDatabaseName();
    }

    @Override
    public void setWriteAheadLoggingEnabled(boolean enabled) {
        super.setWriteAheadLoggingEnabled(enabled);
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

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表结构
        db.execSQL(CREATE_PIPELINE_PROJ_LIST_TABLE);
        Toast.makeText(mContext, "创建数据库:" + getDatabaseName(), Toast.LENGTH_SHORT).show();

        mDatabase = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //删除整个数据库
    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(PIPELINE_PROJECT_DB_NAME);
    }

    //往数据库的project_list表中插入一条数据
    public void insert(ContentValues values) {
        if (values == null) {
            Log.e(TAG, "ContentValues object is null");
            return;
        }
        if (mDatabase == null) {
            mDatabase = getWritableDatabase();
        }
        mDatabase.insert(PIPELINE_PROJECT_LIST_TABLE_NAME, null, values);
        Toast.makeText(mContext, "插入成功", Toast.LENGTH_SHORT).show();
    }

    //从数据库的project_list表中删除一条数据
    public void delete(int id) throws Exception {
        if (id < 0) {
            throw new Exception("delete id must not be zero(0).");
        }

        if (mDatabase == null) {
            mDatabase = getWritableDatabase();
        }
        mDatabase.delete(PIPELINE_PROJECT_LIST_TABLE_NAME, "_id=?", new String[]{String.valueOf(id)});

    }

    //查询数据表project_list
    public Cursor query() {
        if (mDatabase == null) {
            mDatabase = getWritableDatabase();
        }
        Cursor cursor = mDatabase.query(PIPELINE_PROJECT_LIST_TABLE_NAME, null, null, null, null, null, null);
        return cursor;
    }
}
