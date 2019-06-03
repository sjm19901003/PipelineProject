package com.dianping.pipeline.tools;

import android.support.v7.util.DiffUtil;

/**
 * Created by songjunmin on 2019/4/3.
 */

public class MyDiffUtilsCallback extends DiffUtil.Callback {

    @Override
    public int getOldListSize() {
        return 0;
    }

    @Override
    public int getNewListSize() {
        return 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return false;
    }
}
