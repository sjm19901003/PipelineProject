package com.dianping.pipeline;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.dianping.pipeline.tools.PipelineMap;

import java.util.ArrayList;
import java.util.List;


public class BaseActivity extends AppCompatActivity {
    private RelativeLayout mTitleContainer;
    private TextView content;
    private Spinner lineTypeSp;
    private TextView delete;
    private TextView back;
    private static SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.pipeline_base_layout);
        initBaseView();
        prefs = preferences(getApplicationContext());
        getResources().getDisplayMetrics();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static SharedPreferences preferences(Context context) {
        if (context == null) {
            context = PipelineProjectApplication.instance();
        }

        return context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
    }

    public static SharedPreferences preferences() {
        if (prefs == null) {
            prefs = preferences(PipelineProjectApplication.instance());

        }
        return prefs;
    }

    public void setContentView(int layoutResID) {
        View contentView = getLayoutInflater().inflate(layoutResID, null);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.BELOW, R.id.base_titlebar);
        if (mTitleContainer != null) {
            mTitleContainer.addView(contentView, layoutParams);
        }
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            findViewById(R.id.title).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.title)).setText(title);
        }
    }

    public void setTitleVisible(boolean visible) {
        TextView title = findViewById(R.id.title);
        if (title != null) {
            title.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    private void initBaseView() {
        mTitleContainer = (RelativeLayout) findViewById(R.id.base_title_container);
        content = findViewById(R.id.content_tv);
        delete = findViewById(R.id.delete_db);
        back = findViewById(R.id.titlebar_back);
        lineTypeSp = findViewById(R.id.sp_line_type);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void setLineTypeSp(String[] types) {
        if (lineTypeSp != null && types != null && types.length > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, types);
            lineTypeSp.setAdapter(adapter);
            lineTypeSp.setVisibility(View.VISIBLE);
        }
    }

    protected String getLineType() {
        if (lineTypeSp != null && lineTypeSp.getVisibility() == View.VISIBLE) {
            return lineTypeSp.getSelectedItem().toString();
        }
        return "DL";
    }

    protected int getLintTypeIndex() {
        String categroy = PipelineMap.getPipelineCategory(getLineType());
        return PipelineMap.getPipelineTypeIndex(categroy);
    }

    protected void setContentTitle(String title) {
        if (!TextUtils.isEmpty(title) && content != null) {
            content.setVisibility(View.VISIBLE);
            content.setText(title);
        }
    }

    protected void setContentClickListener(View.OnClickListener listener) {
        if (listener != null && content != null) {
            content.setOnClickListener(listener);
        }
    }

    protected View getTitleContentView() {
        return content == null ? null : content;
    }

    public View getTitleBarBackView() {
        return back == null ? null : back;
    }

    protected void setDeleteClickListener(View.OnClickListener listener) {
        if (listener != null && delete != null) {
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(listener);
        }
    }
}
