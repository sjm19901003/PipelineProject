package com.dianping.pipeline;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


public abstract class BaseDialog{

    protected Context context;
    private Display display;//这个设置显示属性用的
    private Dialog dialog;//自定义Dialog对象

    //对话框布局的样式ID (通过这个抽象方法，我们可以给不同的对话框设置不同样式主题)
    protected abstract int getDialogStyleId();

    //设置布局
    protected abstract View getView(Context context);

    //构造方法 来实现 最基本的对话框
    public BaseDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        //在这里初始化 基础对话框style
        if (getDialogStyleId() == 0){
            dialog = new Dialog(context);
        }else {
            dialog = new Dialog(context, getDialogStyleId());
        }
        // 调整dialog背景大小
        getView(context).setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.3),
                LinearLayout.LayoutParams.WRAP_CONTENT));
        dialog.setContentView(getView(context));
        //隐藏系统输入盘
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    //设置对话框属性的方法，通过链式设置
    public BaseDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public void show() {
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
    }

    public boolean isShowing(){
        return dialog.isShowing();
    }

    public BaseDialog setdismissListeren(DialogInterface.OnDismissListener dismissListener){
        dialog.setOnDismissListener(dismissListener);
        return this;
    }
}