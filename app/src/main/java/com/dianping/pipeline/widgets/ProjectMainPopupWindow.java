package com.dianping.pipeline.widgets;

import android.content.DialogInterface;
import android.os.HandlerThread;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.dianping.pipeline.BaseActivity;
import com.dianping.pipeline.BasePopWindow;
import com.dianping.pipeline.PiplineConstant;
import com.dianping.pipeline.R;
import com.dianping.pipeline.main.ProjectMainActivity;
import com.dianping.pipeline.tools.ViewUtils;

import java.lang.ref.WeakReference;

/**
 * Created by songjunmin on 2019/1/12.
 */

public class ProjectMainPopupWindow extends BasePopWindow {
    RelativeLayout rlPointView, rlLineView, rlExportView, rlDeleteView, rlPtStatistic;
    RelativeLayout rlPtDelete, rlLineDelte, rlPtSearch, rlMvPoint;
    private WeakReference<ProjectMainActivity> mActivity;
    private BasePopWindow ptStatisticPopWindow;
    private BaiduMap mBaiduMap;

    public ProjectMainPopupWindow(BaseActivity activity, BaiduMap baiduMap) {
        super(activity, R.layout.pipeline_project_main_pop_menu, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mActivity = new WeakReference<>((ProjectMainActivity) activity);
        mBaiduMap = baiduMap;
    }

    @Override
    protected void initView() {
        rlLineView = getContentView().findViewById(R.id.add_line);
        rlPointView = getContentView().findViewById(R.id.add_point);
        rlExportView = getContentView().findViewById(R.id.export);
        rlDeleteView = getContentView().findViewById(R.id.delete);
        rlPtStatistic = getContentView().findViewById(R.id.statistic);
        rlPtDelete = getContentView().findViewById(R.id.delete_point);
        rlPtSearch = getContentView().findViewById(R.id.search_point);
        rlLineDelte = getContentView().findViewById(R.id.delete_line);
        rlMvPoint = getContentView().findViewById(R.id.move_point);
    }

    public void setStatisticViewName(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (rlPtStatistic != null) {
                ((TextView) rlPtStatistic.findViewById(R.id.menu_statistic)).setText(text);
            }
        }
    }

    @Override
    protected void initEvent() {
        //删除点
        rlPtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.get().marker_event_type = PiplineConstant.MARKER_EVENT_TYPE.POINT_DELETE;
                dismiss();
            }
        });
        //移动点
        rlMvPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.get().marker_event_type = PiplineConstant.MARKER_EVENT_TYPE.POINT_MOVE;
                dismiss();
            }
        });
        //删除线
        rlLineDelte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.get().polyline_event_type = PiplineConstant.POLYLINE_EVENT_TYPE.LINE_DELETE;
                dismiss();
            }
        });
        //搜索点
        rlPtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.get().searchMarker();
                dismiss();
            }
        });
        //添加点
        rlPointView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.get().clickType = PiplineConstant.POP_EVENT_TYPE.ADD_POINT;
                mActivity.get().drawLine = false;
                dismiss();
            }
        });
        //添加线
        rlLineView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.get().clickType = PiplineConstant.POP_EVENT_TYPE.ADD_LINE;
                mActivity.get().drawLine = true;
                if (mActivity.get().lintStartAndEndPoints != null) {
                    mActivity.get().lintStartAndEndPoints.clear();
                }
                dismiss();
            }
        });
        //导出点和线
        rlExportView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.get().clickType = PiplineConstant.POP_EVENT_TYPE.NONE;
                mActivity.get().drawLine = false;
                dismiss();
                AlertDialog alertDialog = new AlertDialog.Builder(mActivity.get())
                        .setTitle("导 出")
                        .setMessage("确认导出数据表到手机？")
                        .setIcon(R.drawable.ic_launcher_foreground)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String pointsExcelName = mActivity.get().getDatabaseName() + "_results.xls";
                                        mActivity.get().ouputDataToExcel(pointsExcelName);
                                        mActivity.get().copyDatabase();
                                    }
                                }).start();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                alertDialog.show();
            }
        });
        //删除数据
        rlDeleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.get().clickType = PiplineConstant.POP_EVENT_TYPE.NONE;
                mActivity.get().drawLine = false;
                dismiss();

                AlertDialog alertDialog = new AlertDialog.Builder(mActivity.get())
                        .setTitle("一键删除")
                        .setMessage("确认删除所有的点、线数据？此操作会清空所有数据表的内容。")
                        .setIcon(R.drawable.ic_launcher_foreground)
                        .setPositiveButton("清除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mActivity.get().mPipelineDBHelper != null) {
                                            mActivity.get().mPipelineDBHelper.deleteDatabase(mActivity.get().getApplicationContext());
                                            mActivity.get().removeAllMarkers(mActivity.get().mMarkers);
                                            mActivity.get().removeAllPolylines(mActivity.get().mPolylines);
                                            mActivity.get().mPipelineDBHelper = null;
                                        }
                                    }
                                }).start();
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                alertDialog.show();

            }
        });
        //收点统计
        rlPtStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.get().drawLine = false;
                if (!mActivity.get().isStatisting) {
                    mActivity.get().clickType = PiplineConstant.POP_EVENT_TYPE.SAVE;
                    //同时在返回键的下方展示收点相关的popwindow
                    showStatisticPopWindow();
                    mActivity.get().isStatisting = true;
                } else {
                    mActivity.get().clickType = PiplineConstant.POP_EVENT_TYPE.NONE;
                    mActivity.get().isStatisting = false;
                    ptStatisticPopWindow.setDisapperOutside(true);
                    ptStatisticPopWindow.dismiss();
                    ptStatisticPopWindow = null;
                    mActivity.get().marker_event_type = PiplineConstant.MARKER_EVENT_TYPE.DEFAULT;
                }
                dismiss();
            }
        });
    }

    //收点相关操作的弹窗
    private void showStatisticPopWindow() {
        if (ptStatisticPopWindow == null) {
            ptStatisticPopWindow = new BasePopWindow(mActivity.get(),
                    R.layout.pipeline_points_statistic_bottom_menu,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) {
                RelativeLayout statistic, locate_unused, action;

                @Override
                protected void initView() {
                    statistic = getContentView().findViewById(R.id.statistic);
                    locate_unused = getContentView().findViewById(R.id.locate_unused);
                    action = getContentView().findViewById(R.id.action);
                }

                @Override
                protected void initEvent() {
                    statistic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final PointsStatisticDialog dialog = new PointsStatisticDialog(mActivity.get());

                            mActivity.get().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!mActivity.get().hasReStatistic) {
                                        mActivity.get().getMapPointsFromDB();
                                    }
                                    int unfinished = 0;
                                    StringBuilder stringBuilder = new StringBuilder();
                                    if (mActivity.get().mPoints.size() > 0) {
                                        for (int i = 0; i < mActivity.get().mPoints.size(); i++) {
                                            if (!(mActivity.get().mPoints.get(i).mark == 1)) {
                                                unfinished++;
                                                stringBuilder.append(mActivity.get().mPoints.get(i).name + " ");
                                            }
                                        }
                                    }

                                    mActivity.get().renderPointsFromDB(mBaiduMap, mActivity.get().mPoints);
                                    dialog.setTotalPoints(mActivity.get().mPoints.size());
                                    dialog.setUnFininshedPoints(unfinished);
                                    dialog.setUnFinishedDesc(stringBuilder.toString());
                                }
                            });
                            mActivity.get().clickType = PiplineConstant.POP_EVENT_TYPE.NONE;
                            dialog.show();
                        }
                    });

                    //定位某个未检测点
                    locate_unused.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mActivity.get().searchMarker();
                        }
                    });

                    action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mActivity.get().marker_event_type = PiplineConstant.MARKER_EVENT_TYPE.CLICK_FINISH;//进行收点
                        }
                    });
                }
            };
            ptStatisticPopWindow.setDisapperOutside(false);
            BasePopWindow.LayoutGravity layoutGravity = new BasePopWindow.LayoutGravity(BasePopWindow.LayoutGravity.CENTER_HORI | BasePopWindow.LayoutGravity.TO_BOTTOM);
            if (mActivity.get().getTitleBarBackView() != null) {
                ptStatisticPopWindow.showBashOfAnchor(mActivity.get().getTitleBarBackView(), layoutGravity,
                        -ViewUtils.dip2px(mActivity.get().getApplicationContext(), 50),
                        -ViewUtils.dip2px(mActivity.get().getApplicationContext(), 10));
            }
        }
    }

}
