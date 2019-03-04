package com.dianping.pipeline.model;


import com.baidu.mapapi.model.LatLng;
import com.dianping.pipeline.BaseModel;

public class PipePoint extends BaseModel {
    public String pointID;
    public String name;
    public String code;
    public String type;
    public String feature;
    public Double x;
    public Double y;
    public Double h;
    public String wellDepth;
    public String manHole;
    public String manHoleSize;
    public String auxType;
    public String note;
    public int jpgNo;
    public int mark;
    public String res;
    public LatLng mLatLng;
}
