package com.dianping.pipeline.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dianping.pipeline.BaseDialog;
import com.dianping.pipeline.R;


public class PointsStatisticDialog extends BaseDialog {

    private View root;
    private Context mContext;
    private TextView totalPoints, unFininshedPoints;
    private TextView desc;

    public PointsStatisticDialog(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    private void initView() {
        if (root == null) {
            return;
        }

        totalPoints = root.findViewById(R.id.total_count);
        unFininshedPoints = root.findViewById(R.id.unfinished_points);
        desc = root.findViewById(R.id.unfinished_desc);
    }

    public void setTotalPoints(int totalCount) {
        if (totalPoints != null) {
            totalPoints.setText("总点数: " + totalCount);
        }
    }

    public void setUnFininshedPoints(int unFinishedCount) {
        if (unFininshedPoints != null) {
            unFininshedPoints.setText("未测点数: " + unFinishedCount);
        }
    }

    public void setUnFinishedDesc(String descStr){
        if(!TextUtils.isEmpty(descStr) && desc != null) {
            desc.setText(descStr);
        }
    }

    @Override
    protected int getDialogStyleId() {
        return 0;
    }

    @Override
    protected View getView(Context context) {
        root = LayoutInflater.from(context).inflate(R.layout.pipeline_points_statistic_dialog_layout, null);
        return root;
    }

    @Override
    public BaseDialog setCancelable(boolean cancel) {
        return super.setCancelable(cancel);
    }
}
