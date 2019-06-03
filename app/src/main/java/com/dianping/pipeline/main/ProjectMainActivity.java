package com.dianping.pipeline.main;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.dianping.pipeline.BaseActivity;
import com.dianping.pipeline.BasePopWindow;
import com.dianping.pipeline.PiplineConstant;
import com.dianping.pipeline.R;
import com.dianping.pipeline.model.PipeLine;
import com.dianping.pipeline.model.PipePoint;
import com.dianping.pipeline.tools.PermissionUtils;
import com.dianping.pipeline.tools.PipelineDBHelper;
import com.dianping.pipeline.tools.PipelineMap;
import com.dianping.pipeline.tools.ViewUtils;
import com.dianping.pipeline.widgets.MarkLineDialog;
import com.dianping.pipeline.widgets.MarkPointDialog;
import com.dianping.pipeline.widgets.PointsLocateDialog;
import com.dianping.pipeline.widgets.ProjectMainPopupWindow;
import com.liyu.sqlitetoexcel.ExcelToSQLite;
import com.liyu.sqlitetoexcel.SQLiteToExcel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class ProjectMainActivity extends BaseActivity {
    private static final String TAG = "ProjectMainActivity";


    class MapLine {
        public PipePoint startPoint;
        public PipePoint endPoint;
        public int color;
        public String name;

        public MapLine(PipePoint pt1, PipePoint pt2, int c, String _name) {
            startPoint = pt1;
            endPoint = pt2;
            color = c;
            name = _name;
        }
    }

    public boolean isStatisting;
    public boolean drawLine = false;
    public boolean hasReStatistic = false;
    public PipelineDBHelper mPipelineDBHelper;
    public PiplineConstant.POP_EVENT_TYPE clickType;
    public PiplineConstant.MARKER_EVENT_TYPE marker_event_type;
    public PiplineConstant.POLYLINE_EVENT_TYPE polyline_event_type;
    public List<PipeLine> mLines = new ArrayList<>();
    public List<Marker> mMarkers = new ArrayList<>();
    public List<PipePoint> mPoints = new ArrayList<>();
    public List<Polyline> mPolylines = new ArrayList<>();
    public List<LatLng> lintStartAndEndPoints = new ArrayList<>();

    private Context mContext;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private String mDBName;
    private String currentPoindID;
    private String startPt, endPt;
    private boolean isFirstLoc = true; // 是否首次定位
    private LocationClient mLocationClient;
    private ProjectMainPopupWindow mMainPopupWindow;
    private BDLocationListener mLocationListener = new MyLocationListener();

    private BaiduMap.OnPolylineClickListener mPolylineClickListener = new BaiduMap.OnPolylineClickListener() {
        @Override
        public boolean onPolylineClick(Polyline polyline) {
            Bundle bundle = polyline.getExtraInfo();
            String polylineName = bundle.getString("polylineID");
            PiplineConstant.POLYLINE_EVENT_TYPE type = polyline_event_type;
            switch (type) {
                case LINE_DELETE:
                    try {
                        polyline.remove();
                        mPipelineDBHelper.deleteLine(polylineName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    polyline_event_type = PiplineConstant.POLYLINE_EVENT_TYPE.DEFAULT;
                    break;
                case LINE_EDIT:
                case DEFAULT:
                    break;
            }
            return false;
        }
    };

    private boolean isMoving = false;
    private Marker tempMarker = null;
    private BaiduMap.OnMarkerClickListener mMarkerClickListener = new BaiduMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(final Marker marker) {
            //添加marker的点击效果，提高UI体验
            marker.setAlpha(0.5f);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    marker.setAlpha(1.0f);
                }
            }, 300);

            final Bundle bundle = marker.getExtraInfo();
            if (bundle == null) {
                return false;
            }
            final String id = bundle.getString("mID");
            final String markerName = bundle.getString("mName");
            LatLng p = marker.getPosition();
            final PipePoint pipePoint = getPipePointWithMarkername(markerName);

            if (drawLine) {
                //添加第一个点击点
                if (p != null && lintStartAndEndPoints.size() == 0) {
                    lintStartAndEndPoints.add(p);
                    startPt = markerName;
                } else if (p != null && lintStartAndEndPoints.size() == 1) {
                    if (!p.equals(lintStartAndEndPoints.get(0))) {
                        lintStartAndEndPoints.add(p);
                        endPt = markerName;
                    }
                }
                if (lintStartAndEndPoints.size() == 2) {
                    drawLineOnBitmap(lintStartAndEndPoints, startPt, endPt);
                }
            } else {
                PiplineConstant.MARKER_EVENT_TYPE type = marker_event_type;
                switch (type) {
                    case CLICK_FINISH:
                        if (!hasReStatistic) {
                            getMapPointsFromDB();
                        }
                        if (pipePoint != null) {
                            pipePoint.mark = 1;
                            ContentValues values = createContentValueWithPipePoint(pipePoint);
                            try {
                                mPipelineDBHelper.updatePoint(values, markerName);
                                refreshSimpleMarkerOnMap(marker);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        marker_event_type = PiplineConstant.MARKER_EVENT_TYPE.DEFAULT;
                        break;
                    case POINT_MOVE:
                        isMoving = true;
                        tempMarker = marker;
                        clickType = PiplineConstant.POP_EVENT_TYPE.MOVE;
                        marker_event_type = PiplineConstant.MARKER_EVENT_TYPE.DEFAULT;
                        break;
                    case POINT_DELETE:
                        if (!hasReStatistic) {
                            getMapPointsFromDB();
                        }
                        try {
                            if (pipePoint != null) {
                                mPoints.remove(pipePoint);
                                marker.remove();
                                mPipelineDBHelper.deletePoint(markerName);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        marker_event_type = PiplineConstant.MARKER_EVENT_TYPE.DEFAULT;
                        break;
                    case POINT_EDIT:
                    case DEFAULT:
                        final int categoryIndex = getLintTypeIndex();
                        //show dialog
                        final MarkPointDialog markPointDialog = new MarkPointDialog(mContext);
                        markPointDialog.setCancelable(true)
                                .setdismissListeren(null)
                                .show();
                        markPointDialog.setPointID(id);
                        markPointDialog.setContentValue(pipePoint, categoryIndex);
                        markPointDialog.setCancelClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                markPointDialog.dismiss();
                            }
                        }).setOKClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    pipePoint.name = getLineType() + markPointDialog.getPointID();
                                    pipePoint.code = markPointDialog.getPointDesc();
                                    pipePoint.type = getLineType();
                                    pipePoint.feature = markPointDialog.getMarkPointName();
                                    pipePoint.x = markPointDialog.getLongitude();
                                    pipePoint.y = markPointDialog.getLatitude();
                                    pipePoint.h = markPointDialog.getPointHeight();
                                    pipePoint.wellDepth = markPointDialog.getWellDepth();
                                    pipePoint.manHole = markPointDialog.getManholeValue();
                                    pipePoint.manHoleSize = markPointDialog.getManholeSize();
                                    pipePoint.auxType = markPointDialog.getAuxType();
                                    pipePoint.note = markPointDialog.getNote();
                                    pipePoint.jpgNo = markPointDialog.isJPGNO() ? 1 : 0;
                                    pipePoint.mark = markPointDialog.isPointMarked() ? 1 : 0;
                                    mPipelineDBHelper.updatePoint(createContentValueWithPipePoint(pipePoint), markerName);
                                    markPointDialog.dismiss();

                                    //更新marker的bundle
                                    Bundle bundle1 = new Bundle();
                                    bundle1.putString("mName", pipePoint.name);
                                    bundle1.putString("mID", markPointDialog.getPointID());
                                    marker.setExtraInfo(bundle1);

                                    //跟新marker的icon
                                    updateMarkerIcon(marker, pipePoint.feature, pipePoint.name);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        break;
                }
            }
            return false;
        }
    };

    private void updateMarkerIcon(Marker marker, String pointName, String markerName) {
        if (marker == null || TextUtils.isEmpty(pointName) || TextUtils.isEmpty(markerName)) {
            return;
        }
        int categoryIndex = getLintTypeIndex();
        String categoryPrefix = PipelineMap.getPointPrefex(categoryIndex);
        String pointResName = "pipeline_" + (categoryPrefix + "_" + pointName).toLowerCase();
        int markPointRes = getResIdByName(pointResName);
        Bitmap bitmap = getPointMarkerBitmapFromLayout(R.layout.pipeline_marker_layout, markPointRes, markerName, categoryIndex);
        if (bitmap != null) {
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
            if (descriptor != null) {
                marker.setIcon(descriptor);
            }
        }
    }

    private void refresh() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //初始化当前点集数据库中的点
                getMapPointsFromDB();
                if (mPoints != null && mPoints.size() > 0) {
                    renderPointsFromDB(mBaiduMap, mPoints);
                }

                //初始化当前线表中的线
                List<MapLine> mapLines = getMapLinesFromDB(mPoints);
                if (mapLines != null && mapLines.size() > 0) {
                    renderLinesFromDB(mBaiduMap, mapLines);
                }
            }
        });
    }

    private BaiduMap.OnMapClickListener mMapClickListener = new BaiduMap.OnMapClickListener() {
        @Override
        public void onMapClick(final LatLng lng) {
            PiplineConstant.POP_EVENT_TYPE type = clickType;
            switch (type) {
                case ADD_POINT:
                    drawPointOnBitmap(lng);
                    clickType = PiplineConstant.POP_EVENT_TYPE.NONE;
                    break;
                case SAVE:
                    break;
                case MOVE:
                    Bundle bundle = tempMarker.getExtraInfo();
                    final String markName = bundle.getString("mName");
                    if (isMoving && tempMarker != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (!TextUtils.isEmpty(markName)) {
                                    try {
                                        PipePoint pt = getPipePointWithMarkername(markName);
                                        pt.x = lng.longitude;
                                        pt.y = lng.latitude;
                                        mPipelineDBHelper.updatePoint(createContentValueWithPipePoint(pt), markName);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        tempMarker.setPosition(lng);
                        //预先删除polyline
                        Polyline polyline = getRelatedPolylineByMarker(tempMarker, markName);
                        if (polyline != null) {
                            polyline.remove();
                        }
                        //重绘polyline
                        refresh();

                        isMoving = false;
                        tempMarker = null;
                    }
                default:
                    break;
            }
        }

        @Override
        public boolean onMapPoiClick(MapPoi poi) {
            return false;
        }
    };

    private Polyline getRelatedPolylineByMarker(Marker marker, String markerName) {
        if (marker == null || mPolylines == null || mPolylines.size() == 0) {
            return null;
        }

        for (Polyline polyline : mPolylines) {
            Bundle bundle = polyline.getExtraInfo();
            String polylineName = bundle.getString("polylineID");
            if (polylineName.contains(markerName)) {
                return polyline;
            }
        }
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pipeline_project_main_activity_layout);
        Intent intent = getIntent();
        mDBName = intent.getStringExtra("projectName");
        clickType = PiplineConstant.POP_EVENT_TYPE.NONE;
        //初始化数据库
        if (!TextUtils.isEmpty(mDBName)) {
            mPipelineDBHelper = new PipelineDBHelper(this, mDBName, null, 1);
            if (mPipelineDBHelper != null) {
                mPipelineDBHelper.getWritableDatabase();
            }
        }
        mContext = this;
        marker_event_type = PiplineConstant.MARKER_EVENT_TYPE.DEFAULT;
        polyline_event_type = PiplineConstant.POLYLINE_EVENT_TYPE.DEFAULT;
        initView();
        new PermissionUtils(this).checkPermisson();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
            mLocationClient.requestLocation();
        }
        mBaiduMap.setOnMapClickListener(mMapClickListener);
        mBaiduMap.setOnPolylineClickListener(mPolylineClickListener);
        mBaiduMap.setOnMarkerClickListener(mMarkerClickListener);
        //通过子线程操作数据库IO来刷新UI
        refresh();
    }

    private void initView() {
        setContentTitle("功能列表");
        // 建立数据源
        List<String> totalItems = new ArrayList<>();
        int count = PipelineMap.PIPELINE_FLAGS.length;
        for (int i = 0; i < count; i++) {
            String[] items = PipelineMap.getPipelineFlag(i);
            if (items != null && items.length > 0) {
                for (int j = 0; j < items.length; j++) {
                    totalItems.add(items[j]);
                }
            }
        }
        String[] types = totalItems.toArray(new String[totalItems.size()]);
        setLineTypeSp(types);
        setContentClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMainPopupWindow == null) {
                    mMainPopupWindow = new ProjectMainPopupWindow(ProjectMainActivity.this, mBaiduMap);
                }
                BasePopWindow.LayoutGravity layoutGravity = new BasePopWindow.LayoutGravity(BasePopWindow.LayoutGravity.CENTER_HORI | BasePopWindow.LayoutGravity.TO_BOTTOM);
                if (getTitleContentView() != null) {
                    mMainPopupWindow.showBashOfAnchor(getTitleContentView(), layoutGravity,
                            -ViewUtils.dip2px(getApplicationContext(), 50),
                            -ViewUtils.dip2px(getApplicationContext(), 10));
                }
                if (isStatisting) {
                    mMainPopupWindow.setStatisticViewName("取消收点");
                } else {
                    mMainPopupWindow.setStatisticViewName("收点统计");
                }
            }
        });
        setTitle("主工程");
        mMapView = findViewById(R.id.map);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(getApplicationContext());//监听授权
        mLocationClient.registerLocationListener(mLocationListener);
        initLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        if (mBaiduMap != null) {
            mBaiduMap.setMyLocationEnabled(false);
        }
        if (mMapView != null) {
            mMapView.onDestroy();
            mMapView = null;
        }
        if (mMarkers != null && mMarkers.size() > 0) {
            mMarkers.clear();
            mMarkers = null;
        }
        if (mPolylines != null && mPolylines.size() > 0) {
            mPolylines.clear();
            mPolylines = null;
        }
        super.onDestroy();
    }

    public void searchMarker() {
        final PointsLocateDialog dialog = new PointsLocateDialog(mContext);
        dialog.setLocateCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        }).setLocateOKListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!hasReStatistic) {
                            getMapPointsFromDB();
                        }
                        if (TextUtils.isEmpty(dialog.getPointName())) {
                            Toast.makeText(mContext, "点名不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String ptName = dialog.getPointName().toUpperCase();
                        renderPointsFromDB(mBaiduMap, mPoints);
                        Marker target = getMarkerWithName(ptName);
                        if (target != null) {
                            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(target.getPosition()));
                        } else {
                            Toast.makeText(mContext, "无法找到对应点，请确认名称", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.dismiss();
            }
        }).show();
    }

    private Marker getMarkerWithName(String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        if (mMarkers != null && mMarkers.size() > 0) {
            int lenght = mMarkers.size();
            for (int i = 0; i < lenght; i++) {
                Marker marker = mMarkers.get(i);
                Bundle bundle = marker.getExtraInfo();
                String mName = bundle.getString("mName");
                if (!TextUtils.isEmpty(mName) && mName.equals(name)) {
                    return marker;
                }
            }
        }
        return null;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case EXPORT_TO_EXCEL_SUCCESS:
                    Toast.makeText(mContext, "导出成功", Toast.LENGTH_SHORT).show();
                    break;
                case EXPORT_TO_EXCEL_FAILED:
                    Toast.makeText(mContext, "导出成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private static final int START_EXPORT_TO_EXCEL = 0;
    private static final int EXPORT_TO_EXCEL_SUCCESS = 1;
    private static final int EXPORT_TO_EXCEL_FAILED = -1;

    public void OuputDataToExcel(String xlsName) {
        if (TextUtils.isEmpty(xlsName)) {
            return;
        }

        File file = new File("/sdcard/pipleline/");
        if (!file.exists()) {
            file.mkdirs();
        }

        new SQLiteToExcel.Builder(this)
                .setDataBase(getDatabasePath(mDBName).getAbsolutePath())
                .setOutputPath(file.getAbsolutePath())
                .setOutputFileName(xlsName)
                .start(new SQLiteToExcel.ExportListener() {
                    @Override
                    public void onStart() {
                        Message msg = Message.obtain();
                        msg.what = START_EXPORT_TO_EXCEL;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onCompleted(String filePath) {
                        Message msg = Message.obtain();
                        msg.what = EXPORT_TO_EXCEL_SUCCESS;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onError(Exception e) {
                        Message msg = Message.obtain();
                        msg.what = EXPORT_TO_EXCEL_FAILED;
                        mHandler.sendMessage(msg);
                    }
                });
    }

    public void removeAllMarkers(List<Marker> markers) {
        if (markers != null && markers.size() > 0) {
            for (int i = 0; i < markers.size(); i++) {
                markers.get(i).remove();
            }
        }
    }

    public void removeAllPolylines(List<Polyline> polylines) {
        if (polylines != null && polylines.size() > 0) {
            for (int j = 0; j < polylines.size(); j++) {
                polylines.get(j).remove();
            }
        }
    }

    private ContentValues createContentValueWithPipePoint(PipePoint pipePoint) {
        ContentValues values = new ContentValues();
        values.put("name", pipePoint.name);
        values.put("code", pipePoint.code);
        values.put("type", pipePoint.type);
        values.put("feature", pipePoint.feature);
        values.put("x", pipePoint.x);
        values.put("y", pipePoint.y);
        values.put("h", pipePoint.h);
        values.put("welldepth", pipePoint.wellDepth);
        values.put("manhole", pipePoint.manHole);
        values.put("manholesize", pipePoint.manHoleSize);
        values.put("auxtype", pipePoint.auxType);
        values.put("note", pipePoint.note);
        values.put("jpgno", pipePoint.jpgNo);
        values.put("mark", pipePoint.mark);
        values.put("res", pipePoint.res);
        return values;
    }

    private PipePoint getPipePointWithMarkername(String name) {
        if (!TextUtils.isEmpty(name)) {
            for (PipePoint p : mPoints) {
                if (name.equals(p.name)) {
                    return p;
                }
            }
        }
        return null;
    }

    private void refreshSimpleMarkerOnMap(Marker marker) {
        String markName = "";
        int mID = 0;
        LatLng position = null;
        if (marker != null) {
            Bundle bundle = marker.getExtraInfo();
            markName = bundle.getString("mName");
            mID = bundle.getInt("mID");
            position = marker.getPosition();
            mMarkers.remove(marker);
            marker.remove();
        }

        int markPointRes = getResIdByName("pipeline_point_ismarked");
        Marker newMarker = null;
        Bitmap bitmap = getPointMarkerBitmapFromLayout(R.layout.pipeline_marker_layout,
                markPointRes,
                markName, 0);
        if (bitmap != null) {
            BitmapDescriptor descriptor = BitmapDescriptorFactory
                    .fromBitmap(bitmap);
            OverlayOptions option = new MarkerOptions()
                    .position(position)
                    .icon(descriptor)
                    .draggable(true);
            newMarker = (Marker) mBaiduMap.addOverlay(option);
        }
        if (newMarker != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("mID", mID);
            bundle.putString("mName", markName);
            newMarker.setExtraInfo(bundle);
            mMarkers.add(newMarker);
        }
    }

    public List<MapLine> getMapLinesFromDB(List<PipePoint> points) {
        if (points.size() == 0) {
            Toast.makeText(this, "未发现任何有效管点", Toast.LENGTH_SHORT).show();
            return null;
        }

        List<MapLine> lines = new ArrayList<>();
        if (mPipelineDBHelper != null) {
            Cursor cursor = mPipelineDBHelper.queryLine();
            if (cursor.getCount() == 0) {
                return null;
            }
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String start_id = cursor.getString(cursor.getColumnIndex("startid"));
                String end_id = cursor.getString(cursor.getColumnIndex("endid"));
                String type = cursor.getString(cursor.getColumnIndex("pipetype"));
                int color = PipelineMap.getPipelineColor(PipelineMap.getPipelineTypeIndex(PipelineMap.getPipelineCategory(type)));
                PipePoint pt1 = getPipePointWithMarkername(start_id);
                PipePoint pt2 = getPipePointWithMarkername(end_id);
                MapLine l = new MapLine(pt1, pt2, color, name);
                lines.add(l);
            }
            cursor.close();
        }
        return lines;
    }

    public List<PipePoint> getMapPointsFromDB() {
        if (mPoints != null) {
            mPoints.clear();
        }

        if (mPipelineDBHelper != null) {
            Cursor pointCursor = mPipelineDBHelper.queryPoint();
            int dataCount = pointCursor.getCount();
            if (dataCount == 0) {
                currentPoindID = "1";
            } else {
                while (pointCursor.moveToNext()) {
                    PipePoint p = new PipePoint();

                    p.x = pointCursor.getDouble(pointCursor.getColumnIndex("x"));
                    p.y = pointCursor.getDouble(pointCursor.getColumnIndex("y"));
                    p.h = pointCursor.getDouble(pointCursor.getColumnIndex("h"));
                    p.code = pointCursor.getString(pointCursor.getColumnIndex("code"));
                    p.feature = pointCursor.getString(pointCursor.getColumnIndex("feature"));
                    p.type = pointCursor.getString(pointCursor.getColumnIndex("type"));
                    p.wellDepth = pointCursor.getString(pointCursor.getColumnIndex("welldepth"));
                    p.manHole = pointCursor.getString(pointCursor.getColumnIndex("manhole"));
                    p.manHoleSize = pointCursor.getString(pointCursor.getColumnIndex("manholesize"));
                    p.auxType = pointCursor.getString(pointCursor.getColumnIndex("auxtype"));
                    p.note = pointCursor.getString(pointCursor.getColumnIndex("note"));
                    p.res = pointCursor.getString(pointCursor.getColumnIndex("res"));
                    p.name = pointCursor.getString(pointCursor.getColumnIndex("name"));
                    p.mark = pointCursor.getInt(pointCursor.getColumnIndex("mark"));
                    p.pointID = p.name.substring(getLineType().length());
                    p.jpgNo = pointCursor.getInt(pointCursor.getColumnIndex("jpgno"));
                    p.mLatLng = new LatLng(p.y, p.x);
                    currentPoindID = p.pointID;

                    mPoints.add(p);
                }
            }
            pointCursor.close();
        }
        hasReStatistic = true;
        return mPoints;
    }

    public void renderPointsFromDB(BaiduMap baiduMap, List<PipePoint> points) {
        if (baiduMap == null || points.size() == 0) {
            return;
        }

        for (PipePoint p : points) {
            int markPointRes = getResIdByName(p.mark == 1 ? "pipeline_point_ismarked" : p.res);
            Marker marker = null;
            String markName = p.name;
            String type = p.type;
            int categoryIndex = PipelineMap.getPipelineTypeIndex(PipelineMap.getPipelineCategory(type));
            Bitmap bitmap = getPointMarkerBitmapFromLayout(R.layout.pipeline_marker_layout,
                    markPointRes,
                    markName, categoryIndex);
            if (bitmap != null) {
                BitmapDescriptor descriptor = BitmapDescriptorFactory
                        .fromBitmap(bitmap);
                OverlayOptions option = new MarkerOptions()
                        .position(p.mLatLng)
                        .icon(descriptor)
                        .draggable(true);
                marker = (Marker) baiduMap.addOverlay(option);
            }
            if (marker != null) {
                Bundle bundle = new Bundle();
                bundle.putString("mID", p.pointID);
                bundle.putString("mName", p.name);
                marker.setExtraInfo(bundle);
                mMarkers.add(marker);
            }
        }
    }

    private void renderLinesFromDB(BaiduMap baiduMap, List<MapLine> lines) {
        if (baiduMap == null || lines.size() == 0) {
            return;
        }
        for (MapLine line : lines) {
            if (line == null || line.startPoint == null || line.endPoint == null) {
                continue;
            }
            int color = line.color;
            LatLng ptA = line.startPoint.mLatLng;
            LatLng ptB = line.endPoint.mLatLng;
            List<LatLng> points = new ArrayList<>();
            points.add(ptA);
            points.add(ptB);

            OverlayOptions options = new PolylineOptions()
                    .width(3)
                    .color(color)
                    .points(points);
            Polyline polyline = (Polyline) mBaiduMap.addOverlay(options);
            Bundle bundle = new Bundle();
            bundle.putString("polylineID", line.name);
            polyline.setExtraInfo(bundle);
            if (polyline != null) {
                mPolylines.add(polyline);
            }
        }
    }

    private Bitmap getPointMarkerBitmapFromLayout(int layout, int markerRes, String name, int categoryIndex) {
        View view = LayoutInflater.from(ProjectMainActivity.this).inflate(layout, null);
        if (view != null) {
            TextView tvName = view.findViewById(R.id.marker_id);
            tvName.setText(name);
            tvName.setTextColor(PipelineMap.getPipelineColor(categoryIndex));
            ImageView res = view.findViewById(R.id.marker_res);
            res.setImageResource(markerRes);
            int me = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            view.measure(me, me);
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            view.buildDrawingCache();
            return view.getDrawingCache();
        }
        return null;
    }

    private void drawPointOnBitmap(final LatLng point) {
        final int categoryIndex = getLintTypeIndex();
        final String categoryPrefix = PipelineMap.getPointPrefex(categoryIndex);
        //show dialog
        final MarkPointDialog markPointDialog = new MarkPointDialog(this);
        markPointDialog.setCancelable(true)
                .setdismissListeren(null)
                .show();
        markPointDialog.setPointID(currentPoindID);
        markPointDialog.setCurrentGZ(getLineType());
        markPointDialog.setLongitude(point.longitude);
        markPointDialog.setLatitude(point.latitude);
        markPointDialog.setFeatureSpinner(categoryIndex);
        markPointDialog.setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markPointDialog.dismiss();
            }
        }).setOKClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (markPointDialog == null) {
                    return;
                }

                String pointName = markPointDialog.getMarkPointName();
                String pointResName = "pipeline_" + (categoryPrefix + "_" + pointName).toLowerCase();
                int markPointRes = getResIdByName(pointResName);
                //从layout解析出bitmap作为BitmapDescriptor
                Marker marker = null;
                String markName = getLineType() + markPointDialog.getPointID() + "";
                Bitmap bitmap = getPointMarkerBitmapFromLayout(R.layout.pipeline_marker_layout, markPointRes, markName, categoryIndex);
                if (bitmap != null) {
                    BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
                    if (descriptor != null) {
                        OverlayOptions option = new MarkerOptions()
                                .position(point)
                                .icon(descriptor)
                                .draggable(true);
                        marker = (Marker) mBaiduMap.addOverlay(option);
                    }
                }

                if (!isSamePoint(markName)) {
                    //向点表中插入点
                    PipePoint pipePoint = new PipePoint();
                    pipePoint.code = markPointDialog.getPointDesc();
                    pipePoint.type = getLineType();
                    pipePoint.feature = markPointDialog.getMarkPointName();
                    pipePoint.name = markName;
                    pipePoint.x = point.longitude;
                    pipePoint.y = point.latitude;
                    pipePoint.h = markPointDialog.getPointHeight();
                    pipePoint.wellDepth = markPointDialog.getWellDepth();
                    pipePoint.manHole = markPointDialog.getManholeValue();
                    pipePoint.manHoleSize = markPointDialog.getManholeSize();
                    pipePoint.auxType = markPointDialog.getAuxType();
                    pipePoint.note = markPointDialog.getNote();
                    pipePoint.jpgNo = markPointDialog.isJPGNO() ? 1 : 0;
                    pipePoint.mark = markPointDialog.isPointMarked() ? 1 : 0;
                    pipePoint.res = pointResName;

                    if (insertPointIntoDatabase(pipePoint)) {
                        mPoints.add(pipePoint);//在内存的点集中添加一个point
                        currentPoindID = String.valueOf(Integer.valueOf(markName.substring(pipePoint.type.length())) + 1);
                        if (marker != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString("mID", currentPoindID);
                            bundle.putString("mName", markName);
                            marker.setExtraInfo(bundle);
                            mMarkers.add(marker);
                        }
                        hasReStatistic = false;
                        markPointDialog.dismiss();
                    } else {
                        Toast.makeText(ProjectMainActivity.this, "插入失败", Toast.LENGTH_SHORT).show();
                        markPointDialog.dismiss();
                    }
                }


            }
        });
    }

    private boolean isSamePoint(String ptName) {
        if (mPoints == null || mPoints.size() == 0) {
            return false;
        }

        if (TextUtils.isEmpty(ptName)) {
            Log.e(TAG, "Check same point need ptName must not null");
            return true;
        }

        for (PipePoint pt : mPoints) {
            if (!TextUtils.isEmpty(pt.name) && ptName.equals(pt.name)) {
                Toast.makeText(ProjectMainActivity.this, "插入点不允许有相同的ID，请检查", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

    private void drawLineOnBitmap(final List<LatLng> points, final String startId, final String endId) {
        final int categoryIndex = getLintTypeIndex();
        if (points == null || points.size() == 0) {
            return;
        }

        final MarkLineDialog lineDialog = new MarkLineDialog(this);
        lineDialog.setCancelable(true)
                .setdismissListeren(null)
                .show();
        lineDialog.setStartId(startId);
        lineDialog.setEndId(endId);

        lineDialog.setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lineDialog != null) {
                    lineDialog.dismiss();
                }
            }
        }).setOKClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int _color = PipelineMap.getPipelineColor(categoryIndex);
                OverlayOptions options = new PolylineOptions()
                        .width(3)
                        .color(_color)
                        .points(points);
                Polyline polyline = (Polyline) mBaiduMap.addOverlay(options);
                String polylineName = getLineType() + "_" + startId + "_" + endId;
                Bundle bundle = new Bundle();
                bundle.putString("polylineID", polylineName);
                polyline.setExtraInfo(bundle);
                if (polyline != null) {
                    mPolylines.add(polyline);
                }
                clickType = PiplineConstant.POP_EVENT_TYPE.NONE;
                lintStartAndEndPoints.clear();
                drawLine = false;

                //向线表中插入线
                PipeLine pipeLine = new PipeLine();
                pipeLine.name = polylineName;
                pipeLine.startID = startId;
                pipeLine.endID = endId;
                pipeLine.pipeType = getLineType();
                pipeLine.firstDepth = lineDialog.getStartDepth();
                pipeLine.endDepth = lineDialog.getEndDepth();
                pipeLine.section = lineDialog.getPipSection();
                pipeLine.amount = lineDialog.getPipHoleCount();
                pipeLine.holeUse = lineDialog.getPipHoleUse();
                pipeLine.pipeUser = lineDialog.getPipUser();
                pipeLine.matero = lineDialog.getPipMatero();
                pipeLine.pipeNum = lineDialog.getPipNum();
                pipeLine.material = lineDialog.getPipMaterial();
                pipeLine.pressure = lineDialog.getPipPressure();
                pipeLine.flow = lineDialog.getPipFlow();
                pipeLine.embed = lineDialog.getPipEmbed();
                pipeLine.bTime = lineDialog.getPipTime();
                pipeLine.road = lineDialog.getPipRoad();
                pipeLine.species = lineDialog.getPipSpecies();
                pipeLine.note = lineDialog.getPipNote();
                insertLineIntoDatabase(pipeLine);
                mLines.add(pipeLine);
                if (lineDialog != null) {
                    lineDialog.dismiss();
                }
            }

        });
    }

    //将一个点数据插入到数据库的点表中
    private boolean insertPointIntoDatabase(PipePoint pipePoint) {
        if (mPipelineDBHelper == null) {
            mPipelineDBHelper = new PipelineDBHelper(this, mDBName, null, 1);
            mPipelineDBHelper.getWritableDatabase();
        }

        ContentValues values = new ContentValues();
        values.put("name", pipePoint.name);
        values.put("code", pipePoint.code);
        values.put("type", pipePoint.type);
        values.put("feature", pipePoint.feature);
        values.put("x", pipePoint.x);
        values.put("y", pipePoint.y);
        values.put("h", pipePoint.h);
        values.put("welldepth", pipePoint.wellDepth);
        values.put("manhole", pipePoint.manHole);
        values.put("manholesize", pipePoint.manHoleSize);
        values.put("auxtype", pipePoint.auxType);
        values.put("note", pipePoint.note);
        values.put("jpgno", pipePoint.jpgNo);
        values.put("mark", pipePoint.mark);
        values.put("res", pipePoint.res);

        if (mPipelineDBHelper.insertPoint(values)) {
            return true;
        }
        return false;
    }

    //将一个线数据插入到数据库的线表中
    private void insertLineIntoDatabase(PipeLine line) {
        if (mPipelineDBHelper == null) {
            mPipelineDBHelper = new PipelineDBHelper(this, mDBName, null, 1);
            mPipelineDBHelper.getWritableDatabase();
        }

        ContentValues values = new ContentValues();
        values.put("name", line.name);
        values.put("startid", line.startID);
        values.put("endid", line.endID);
        values.put("pipetype", line.pipeType);
        values.put("startdepth", line.firstDepth);
        values.put("enddepth", line.endDepth);
        values.put("pipesize", line.section);
        values.put("amount", line.amount);
        values.put("holeuse", line.holeUse);
        values.put("matero", line.matero);
        values.put("pipenum", line.pipeNum);
        values.put("material", line.material);
        values.put("pressure", line.pressure);
        values.put("flow", line.flow);
        values.put("enbed", line.embed);
        values.put("btime", line.bTime);
        values.put("pipeuser", line.pipeUser);
        values.put("road", line.road);
        values.put("species", line.species);
        values.put("note", line.note);

        mPipelineDBHelper.insertLine(values);
    }

    public String getDatabaseName() {
        return mDBName;
    }

    //通过反射获取一个资源的id
    private int getResIdByName(String resName) {
        ApplicationInfo applicationInfo = getApplicationInfo();
        int resId = getResources().getIdentifier(resName, "drawable", applicationInfo.packageName);
        return resId == 0 ? R.drawable.pipeline_marker : resId;
    }

    //百度地图的初始化操作
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(1000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation

        // .getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);
        option.setOpenGps(true); // 打开gps

        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    //实现BDLocationListener接口,BDLocationListener为结果监听接口，异步获取定位结果
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || mMapView == null) {
                return;
            }

            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            // 设置定位数据
            mBaiduMap.setMyLocationData(locData);
            // 当不需要定位图层时关闭定位图层
            //mBaiduMap.setMyLocationEnabled(false);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(21.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

                if (location.getLocType() == BDLocation.TypeGpsLocation) {
                    // GPS定位结果
                    Toast.makeText(ProjectMainActivity.this, location.getAddrStr(), Toast.LENGTH_SHORT).show();
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                    // 网络定位结果
                    Toast.makeText(ProjectMainActivity.this, location.getAddrStr(), Toast.LENGTH_SHORT).show();

                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                    // 离线定位结果
                    Toast.makeText(ProjectMainActivity.this, location.getAddrStr(), Toast.LENGTH_SHORT).show();

                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    Toast.makeText(ProjectMainActivity.this, "服务器错误，请检查", Toast.LENGTH_SHORT).show();
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    Toast.makeText(ProjectMainActivity.this, "网络错误，请检查", Toast.LENGTH_SHORT).show();
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    Toast.makeText(ProjectMainActivity.this, "手机模式错误，请检查是否飞行", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}