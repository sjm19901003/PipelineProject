package com.dianping.pipeline.projectlist;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.dianping.pipeline.BaseActivity;
import com.dianping.pipeline.R;
import com.dianping.pipeline.model.ProjectListItemModel;
import com.dianping.pipeline.tools.PipelineProjectListDBHelper;
import com.dianping.pipeline.widgets.ProjectCreateDialog;

import java.util.ArrayList;
import java.util.List;


public class ProjectListActivity extends BaseActivity {

    private static final String PIPELINE_PROJ_DB_NAME = "pipeprojectlist.db";
    private PipelineProjectListDBHelper mProjectDBHelper;

    private RecyclerView mRecyclerView;
    private ProjectListAdapter mListAdapter;

    private List<ProjectListItemModel> mListItemModels = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pipeline_project_list_layout);
        //初始化数据库
        mProjectDBHelper = new PipelineProjectListDBHelper(this,
                PIPELINE_PROJ_DB_NAME, null, 1);
        if (mProjectDBHelper != null) {
            mProjectDBHelper.getWritableDatabase();
        }

        initProjectList();
        mListAdapter = new ProjectListAdapter(this, mListItemModels, mProjectDBHelper);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        setContentTitle("工程新建");
        setContentClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createProject();
            }
        });

        //删除所有工程
        setDeleteClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空列表内容
                mListItemModels.clear();
                if (mListAdapter != null) {
                    mListAdapter.notifyDataSetChanged();
                }

                //清空projectlist数据库
                if (mProjectDBHelper != null) {
                    mProjectDBHelper.deleteDatabase(getApplicationContext());
                    mProjectDBHelper = null;
                }
            }
        });

        //设置recyclerview
        mRecyclerView = findViewById(R.id.project_list_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mListAdapter);
    }

    //在进入到该页面之后，从sqlite当中加载已有的project信息
    private void initProjectList() {
        //从数据库中查询已有的project list数据

        if (mProjectDBHelper != null) {
            Cursor cursor = mProjectDBHelper.query();
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String city = cursor.getString(cursor.getColumnIndex("city"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String owner = cursor.getString(cursor.getColumnIndex("owner"));
                ProjectListItemModel model = new ProjectListItemModel(name, city, date, owner);
                mListItemModels.add(model);
            }
        }
    }

    //创建一个Project，创建完成之后会展示在列表中
    private void createProject() {
        final ProjectCreateDialog dialog = new ProjectCreateDialog(this);

        View.OnClickListener mOKListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProjectListItemModel itemModel = new ProjectListItemModel();
                itemModel.projectName = dialog.getProjectName();
                itemModel.projectOwner = dialog.getProjectOwner();
                itemModel.projectCity = dialog.getProjectCity();
                itemModel.projectDate = dialog.getProjectDate();

                if (!TextUtils.isEmpty(itemModel.projectName)) {
                    if (mListItemModels != null) {
                        mListItemModels.add(itemModel);
                    }
                    //异步写入数据库逻辑
                    insertToDatabase(itemModel);
                    //刷新RecyclerView
                    refreshProjList();
                    dialog.dismiss();
                }
            }
        };
        View.OnClickListener mCancelListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        };
        dialog.setCancelable(true)
                .setdismissListeren(null)
                .show();

        dialog.setOKClickListener(mOKListener)
                .setCancelClickListener(mCancelListener);
    }

    //刷新列表数据
    private void refreshProjList() {
        if (mListAdapter != null) {
            mListAdapter.notifyDataSetChanged();
        }
    }

    private void insertToDatabase(ProjectListItemModel itemModel) {
        //如果此前点击过"删除工程"，则会删除数据库，所以此时新建需要重新创建数据库
        if(mListItemModels.size() == 0 && mProjectDBHelper == null) {
            mProjectDBHelper = new PipelineProjectListDBHelper(this, PIPELINE_PROJ_DB_NAME, null, 1);
            mProjectDBHelper.getWritableDatabase();
        }

        if (mProjectDBHelper != null) {
            ContentValues values = new ContentValues();
            values.put("name", itemModel.projectName);
            values.put("city", itemModel.projectCity);
            values.put("date", itemModel.projectDate);
            values.put("owner", itemModel.projectOwner);

            mProjectDBHelper.insert(values);
        }
    }
}
