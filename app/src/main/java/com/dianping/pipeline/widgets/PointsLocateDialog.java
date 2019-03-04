package com.dianping.pipeline.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dianping.pipeline.BaseDialog;
import com.dianping.pipeline.R;


public class PointsLocateDialog extends BaseDialog {

    private View root;
    private Context mContext;
    private EditText etPtName;
    private Button locateOK, locateCancel;

    public PointsLocateDialog(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    private void initView() {
        if (root == null) {
            return;
        }

        etPtName = root.findViewById(R.id.et_point_name);
        locateOK = root.findViewById(R.id.locate_ok);
        locateCancel = root.findViewById(R.id.locate_cancel);
    }

    public String getPointName(){
        if(etPtName != null && !TextUtils.isEmpty(etPtName.getText().toString())){
            return etPtName.getText().toString();
        }
        return null;
    }

    public PointsLocateDialog setLocateOKListener(View.OnClickListener listener){
        if(listener != null && locateOK != null){
            locateOK.setOnClickListener(listener);
        }
        return this;
    }

    public PointsLocateDialog setLocateCancelListener(View.OnClickListener listener){
        if(listener != null && locateCancel != null){
            locateCancel.setOnClickListener(listener);
        }
        return this;
    }

    @Override
    protected int getDialogStyleId() {
        return 0;
    }

    @Override
    protected View getView(Context context) {
        root = LayoutInflater.from(context).inflate(R.layout.pipeline_points_locate_dialog_layout, null);
        return root;
    }

    @Override
    public BaseDialog setCancelable(boolean cancel) {
        return super.setCancelable(cancel);
    }
}
