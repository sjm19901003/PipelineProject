package com.dianping.pipeline.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.dianping.pipeline.BaseDialog;
import com.dianping.pipeline.R;
import com.dianping.pipeline.model.PipePoint;
import com.dianping.pipeline.tools.PipelineMap;


public class MarkPointDialog extends BaseDialog {

    private View root;
    private Context mContext;
    private EditText tvPointID;
    private Button btnOK, btnCancel;
    private EditText etLng, etLati, etHigh, etDesc, etDepth, manholeValue, manholeSize, etAuxType, etNote;
    private Spinner mFeatureSpinner;
    private CheckBox jpgnoCheckBox, markCheckBox;

    public MarkPointDialog(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    private void initView() {
        if (root == null) {
            return;
        }

        btnOK = root.findViewById(R.id.btn_ok);
        btnCancel = root.findViewById(R.id.btn_cancel);
        mFeatureSpinner = root.findViewById(R.id.feature_spinner);
        etLng = root.findViewById(R.id.et_lng);
        etLati = root.findViewById(R.id.et_lat);
        etHigh = root.findViewById(R.id.et_high);
        etDesc = root.findViewById(R.id.point_desc);
        etDepth = root.findViewById(R.id.well_depth_value);
        manholeValue = root.findViewById(R.id.manhole_value);
        manholeSize = root.findViewById(R.id.manholesize);
        etAuxType = root.findViewById(R.id.auxtype_value);
        etNote = root.findViewById(R.id.note);
        tvPointID = root.findViewById(R.id.point_id);
        jpgnoCheckBox = root.findViewById(R.id.jpgno);
        markCheckBox = root.findViewById(R.id.mark);
    }

    public void setContentValue(PipePoint pt, int categoryIndex) {
        if (pt != null) {
            setFeatureSpinner(categoryIndex);
            etLng.setText(pt.x + "");
            etLati.setText(pt.y + "");
            etHigh.setText(pt.h + "");
            etDepth.setText(pt.wellDepth);
            etDesc.setText(pt.code);
            manholeValue.setText(pt.manHole);
            manholeSize.setText(pt.manHoleSize);
            etAuxType.setText(pt.auxType);
            etNote.setText(pt.note);
            jpgnoCheckBox.setChecked(pt.jpgNo == 1);
            markCheckBox.setChecked(pt.mark == 1);
        }
    }

    public String getPointID() {
        if (tvPointID != null) {
            return tvPointID.getText().toString();
        }
        return null;
    }

    public void setPointID(String id) {
        if (tvPointID != null) {
            tvPointID.setText(id);
        }
    }

    public String getWellDepth() {
        if (etDepth != null) {
            return etDepth.getText().toString();
        }
        return null;
    }

    public String getManholeValue() {
        if (manholeValue != null) {
            return manholeValue.getText().toString();
        }
        return null;
    }

    public String getManholeSize() {
        if (manholeSize != null) {
            return manholeSize.getText().toString();
        }
        return null;
    }

    public String getAuxType() {
        if (etAuxType != null) {
            return etAuxType.getText().toString();
        }
        return null;
    }

    public String getNote() {
        if (etNote != null) {
            return etNote.getText().toString();
        }
        return "default";
    }

    public boolean isJPGNO() {
        if (jpgnoCheckBox != null) {
            return jpgnoCheckBox.isChecked();
        }
        return false;
    }

    public boolean isPointMarked() {
        if (markCheckBox != null) {
            return markCheckBox.isChecked();
        }
        return false;
    }

    @Override
    protected int getDialogStyleId() {
        return 0;
    }

    @Override
    protected View getView(Context context) {
        root = LayoutInflater.from(context).inflate(R.layout.pipeline_load_markpoint, null);
        return root;
    }

    @Override
    public BaseDialog setCancelable(boolean cancel) {
        return super.setCancelable(cancel);
    }


    //设置对应Category的Feature
    public void setFeatureSpinner(int index) {
        if (index < 0 || mFeatureSpinner == null) {
            return;
        }

        // 建立数据源
        String[] mItems = PipelineMap.getPointsWithCategory(index);
        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> _Adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, mItems);
        //绑定 Adapter到控件
        mFeatureSpinner.setAdapter(_Adapter);
    }

    //获取管种Feature
    public String getMarkPointName() {
        if (mFeatureSpinner != null) {
            return mFeatureSpinner.getSelectedItem().toString();
        }
        return "ZJD";
    }

    public MarkPointDialog setOKClickListener(View.OnClickListener listener) {
        if (btnOK != null && listener != null) {
            btnOK.setOnClickListener(listener);
        }
        return this;
    }

    public MarkPointDialog setCancelClickListener(View.OnClickListener listener) {
        if (btnCancel != null && listener != null) {
            btnCancel.setOnClickListener(listener);
        }
        return this;
    }

    //获取经度坐标
    public Double getLatitude() {
        if (etLati != null && !TextUtils.isEmpty(etLati.getText().toString())) {
            return Double.valueOf(etLati.getText().toString());
        }
        return 0.0;
    }

    public void setLatitude(Double latitude) {
        if (etLati != null) {
            etLati.setText(String.valueOf(latitude));
        }
    }

    public void setLongitude(Double longitude) {
        if (etLng != null) {
            etLng.setText(String.valueOf(longitude));
        }
    }

    //获取纬度坐标
    public Double getLongitude() {
        if (etLng != null && !TextUtils.isEmpty(etLng.getText().toString())) {
            return Double.valueOf(etLng.getText().toString());
        }
        return 0.0;
    }

    //获取高程
    public Double getPointHeight() {
        if (etHigh != null && !TextUtils.isEmpty(etHigh.getText().toString())) {
            return Double.valueOf(etHigh.getText().toString());
        }
        return 0.0;
    }

    //获取点位描述
    public String getPointDesc() {
        if (etDesc != null && !TextUtils.isEmpty(etDesc.getText().toString())) {
            return etDesc.getText().toString();
        }
        return "default";
    }
}
