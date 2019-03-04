package com.dianping.pipeline.widgets;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.dianping.pipeline.BaseDialog;
import com.dianping.pipeline.R;

import java.util.Calendar;

public class ProjectCreateDialog extends BaseDialog {

    private View mRoot;
    private EditText projectDate, projectName, projectOwner, projectCity;
    private Button btnOK, btnCancel;

    private View.OnClickListener mDateClickListener = new View.OnClickListener() {
        Calendar mCalendar = Calendar.getInstance();

        @Override
        public void onClick(View v) {
            new DatePickerDialog(context,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            // 此处得到选择的时间
                            if (projectDate != null) {
                                String days;
                                if (monthOfYear + 1 < 10) {
                                    if (dayOfMonth < 10) {
                                        days = new StringBuffer().append(year).append("0").
                                                append(monthOfYear + 1).append("0").append(dayOfMonth).toString();
                                    } else {
                                        days = new StringBuffer().append(year).append("0").
                                                append(monthOfYear + 1).append(dayOfMonth).toString();
                                    }

                                } else {
                                    if (dayOfMonth < 10) {
                                        days = new StringBuffer().append(year).
                                                append(monthOfYear + 1).append("0").append(dayOfMonth).toString();
                                    } else {
                                        days = new StringBuffer().append(year).
                                                append(monthOfYear + 1).append(dayOfMonth).toString();
                                    }
                                }
                                projectDate.setText(days);
                            }
                        }
                    }
                    // 设置初始日期
                    , mCalendar.get(Calendar.YEAR)
                    , mCalendar.get(Calendar.MONTH)
                    , mCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    };

    @Override
    protected int getDialogStyleId() {
        return 0;
    }

    @Override
    protected View getView(Context context) {
        mRoot = LayoutInflater.from(context).inflate(R.layout.pipeline_project_creat_dialog_layout, null);
        return mRoot;
    }

    public ProjectCreateDialog(Context context) {
        super(context);
        initDialogView();
    }

    public ProjectCreateDialog setOKClickListener(View.OnClickListener listener) {
        if (btnOK != null) {
            btnOK.setOnClickListener(listener);
        }
        return this;
    }

    public ProjectCreateDialog setCancelClickListener(View.OnClickListener listener) {
        if (btnCancel != null) {
            btnCancel.setOnClickListener(listener);
        }
        return this;
    }

    private void initDialogView() {
        if (mRoot == null) {
            return;
        }

        projectDate = mRoot.findViewById(R.id.edit_project_date);
        projectName = mRoot.findViewById(R.id.edit_project_name);
        projectOwner = mRoot.findViewById(R.id.edit_project_owner);
        projectCity = mRoot.findViewById(R.id.edit_project_city);

        btnOK = mRoot.findViewById(R.id.btn_ok);
        btnCancel = mRoot.findViewById(R.id.btn_cancel);
        projectDate.setOnClickListener(mDateClickListener);
    }

    public String getProjectName() {
        if (!TextUtils.isEmpty(projectName.getText().toString())) {
            return projectName.getText().toString();
        }
        return null;
    }

    public String getProjectDate() {
        if (!TextUtils.isEmpty(projectDate.getText().toString())) {
            return projectDate.getText().toString();
        }
        return null;
    }

    public String getProjectOwner() {
        if (!TextUtils.isEmpty(projectOwner.getText().toString())) {
            return projectOwner.getText().toString();
        }
        return null;
    }

    public String getProjectCity() {
        if (!TextUtils.isEmpty(projectCity.getText().toString())) {
            return projectCity.getText().toString();
        }
        return null;
    }
}
