package com.dianping.pipeline.projectlist;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dianping.pipeline.R;
import com.dianping.pipeline.model.ProjectListItemModel;
import com.dianping.pipeline.tools.PipelineProjectListDBHelper;

import java.util.List;


public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ViewHolder> {
    private Context mContext;
    private List<ProjectListItemModel> mList;
    private PipelineProjectListDBHelper mDBHelper;

    public ProjectListAdapter(Context context, List<ProjectListItemModel> datas) {
        this(context, datas, null);
    }

    public ProjectListAdapter(Context context, List<ProjectListItemModel> datas, PipelineProjectListDBHelper dbHelper) {
        mContext = context;
        mList = datas;
        mDBHelper = dbHelper;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pipeline_project_list_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ProjectListItemModel model = mList.get(position);
        holder.name.setText("工程名称：" + model.projectName);
        holder.city.setText("所属城市：" + model.projectCity);
        holder.owner.setText("负责人" + model.projectOwner);
        holder.date.setText("日期：" + model.projectDate);

        holder.open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("pipeline://projectmain"));
                intent.putExtra("projectName", model.projectName);
                mContext.startActivity(intent);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除当前子工程
                if (mDBHelper != null) {
                    try {
                        mDBHelper.delete(position);
                        mList.remove(position);
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, city, owner, date;
        private TextView open, delete;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            city = itemView.findViewById(R.id.city);
            owner = itemView.findViewById(R.id.owner);
            date = itemView.findViewById(R.id.date);

            open = itemView.findViewById(R.id.open);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
