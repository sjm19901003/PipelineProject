package com.dianping.pipeline.login;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dianping.pipeline.BaseActivity;
import com.dianping.pipeline.R;
import com.dianping.pipeline.tools.ProjectUtils;

public class LoginMainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pipeline_login_main_layout);

        final Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.pipeline_project_login_dialog_layout);

        TextView textView = dialog.findViewById(R.id.tv_award_dialog_content);
        TextView titleView = dialog.findViewById(R.id.tv_award_dialog_title);
        final EditText etPasswords = dialog.findViewById(R.id.passward);
        titleView.setText("登录验证");
        textView.setText("请输入相关信息来验证信息，验证通过方可使用该软件");
        dialog.findViewById(R.id.iv_award_dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.findViewById(R.id.btn_award_dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.findViewById(R.id.btn_award_dialog_ok).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("pipeline://projectlist")));
                dialog.dismiss();
                finish();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }
}
