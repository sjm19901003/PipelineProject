package com.dianping.pipeline.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.dianping.pipeline.BaseDialog;
import com.dianping.pipeline.R;
import com.dianping.pipeline.tools.ProjectUtils;


public class MarkLineDialog extends BaseDialog {
    private View root;
    private TextView startId, endId;
    private EditText startDepth, endDepth, etSection, etAmount, etHoleUse;
    private EditText etMatero, etNum, etPressure, etBtime;
    private EditText etUser, etRoad, etSpecies, etNote;
    private Spinner mMaterialSpinner, etFlow, etEmbed;
    private Button btnOK, btnCancel;
    private int color;

    public MarkLineDialog(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        if (root == null) {
            return;
        }

        startId = root.findViewById(R.id.start_point_id);
        endId = root.findViewById(R.id.end_point_id);
        startDepth = root.findViewById(R.id.start_depth_value);
        endDepth = root.findViewById(R.id.end_depth_value);
        mMaterialSpinner = root.findViewById(R.id.pipe_material);
        etSection = root.findViewById(R.id.pipe_size_value);
        etAmount = root.findViewById(R.id.hole_count);
        etHoleUse = root.findViewById(R.id.hole_use_count);
        etMatero = root.findViewById(R.id.pipe_matero_value);
        etNum = root.findViewById(R.id.pipe_num);
        etPressure = root.findViewById(R.id.pipe_pressure);
        etFlow = root.findViewById(R.id.pipe_flow);
        etEmbed = root.findViewById(R.id.pipe_embed);
        etBtime = root.findViewById(R.id.pipe_btime);
        etUser = root.findViewById(R.id.pipe_user);
        etRoad = root.findViewById(R.id.pipe_road);
        etSpecies = root.findViewById(R.id.pipe_species);
        etNote = root.findViewById(R.id.pipe_note);
        btnOK = root.findViewById(R.id.btn_ok);
        btnCancel = root.findViewById(R.id.btn_cancel);
    }

    public int getLineColor() {
        return color == 0 ? 0xAAFF0000 : color;
    }

    public String getPipNote() {
        if (etNote != null && !TextUtils.isEmpty(etNote.getText().toString())) {
            return ProjectUtils.stringFilter(etNote.getText().toString());
        }
        return "";
    }

    public String getPipSpecies() {
        if (etSpecies != null && !TextUtils.isEmpty(etSpecies.getText().toString())) {
            return ProjectUtils.stringFilter(etSpecies.getText().toString());
        }
        return "";
    }

    public String getPipRoad() {
        if (etRoad != null && !TextUtils.isEmpty(etRoad.getText().toString())) {
            return ProjectUtils.stringFilter(etRoad.getText().toString());
        }
        return "";
    }

    public String getPipUser() {
        if (etUser != null && !TextUtils.isEmpty(etUser.getText().toString())) {
            return ProjectUtils.stringFilter(etUser.getText().toString());
        }
        return "";
    }

    public String getPipFlow() {
        if (etFlow != null && !TextUtils.isEmpty(etFlow.getSelectedItem().toString())) {
            return ProjectUtils.stringFilter(etFlow.getSelectedItem().toString());
        }
        return "";
    }

    public String getPipTime() {
        if (etBtime != null && !TextUtils.isEmpty(etBtime.getText().toString())) {
            return ProjectUtils.stringFilter(etBtime.getText().toString());
        }
        return "20190101";
    }

    public String getPipEmbed() {
        if (etEmbed != null && !TextUtils.isEmpty(etEmbed.getSelectedItem().toString())) {
            return ProjectUtils.stringFilter(etEmbed.getSelectedItem().toString());
        }

        return "直埋";
    }

    public String getPipPressure() {
        if (etPressure != null && !TextUtils.isEmpty(etPressure.getText().toString())) {
            return ProjectUtils.stringFilter(etPressure.getText().toString());
        }
        return "";
    }

    public String getPipNum() {
        if (etNum != null && !TextUtils.isEmpty(etNum.getText().toString())) {
            return ProjectUtils.stringFilter(etNum.getText().toString());
        }
        return "";
    }

    public String getPipMatero() {
        if (etMatero != null && !TextUtils.isEmpty(etMatero.getText().toString())) {
            return ProjectUtils.stringFilter(etMatero.getText().toString());
        }
        return "";
    }

    public int getPipHoleUse() {
        if (etHoleUse != null && !TextUtils.isEmpty(etHoleUse.getText().toString())) {
            return Integer.valueOf(etHoleUse.getText().toString());
        }
        return 0;
    }

    public int getPipHoleCount() {
        if (etAmount != null && !TextUtils.isEmpty(etAmount.getText().toString())) {
            return Integer.valueOf(etAmount.getText().toString());
        }
        return 0;
    }


    public String getPipSection() {
        if (etSection != null && !TextUtils.isEmpty(etSection.getText().toString())) {
            return ProjectUtils.stringFilter(etSection.getText().toString());
        }
        return "";
    }

    public String getPipMaterial() {
        if (mMaterialSpinner != null && !TextUtils.isEmpty(mMaterialSpinner.getSelectedItem().toString())) {
            return ProjectUtils.stringFilter(mMaterialSpinner.getSelectedItem().toString());
        }
        return "光纤";
    }

    public double getStartDepth() {
        if (startDepth != null && !TextUtils.isEmpty(startDepth.getText().toString())) {
            return Double.valueOf(startDepth.getText().toString());
        }
        return 0.0;
    }

    public double getEndDepth() {
        if (endDepth != null && !TextUtils.isEmpty(endDepth.getText().toString())) {
            return Double.valueOf(endDepth.getText().toString());
        }
        return 0.0;
    }

    public void setStartId(String id) {
        if (startId != null) {
            startId.setText("起点点号:" + id);
        }
    }

    public void setEndId(String id) {
        if (endId != null) {
            endId.setText("终点点号:" + id);
        }
    }

    @Override
    protected int getDialogStyleId() {
        return 0;
    }

    @Override
    protected View getView(Context context) {
        root = LayoutInflater.from(context).inflate(R.layout.pipeline_load_markline, null);
        return root;
    }

    @Override
    public BaseDialog setCancelable(boolean cancel) {
        return super.setCancelable(cancel);
    }

    public MarkLineDialog setOKClickListener(View.OnClickListener listener) {
        if (btnOK != null) {
            btnOK.setOnClickListener(listener);
        }
        return this;
    }

    public MarkLineDialog setCancelClickListener(View.OnClickListener listener) {
        if (btnCancel != null) {
            btnCancel.setOnClickListener(listener);
        }
        return this;
    }

}
