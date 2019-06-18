package com.dianping.pipeline.model;

import com.dianping.pipeline.BaseModel;

public class PipeLine extends BaseModel {
    public String name;
    public String startID;
    public String endID;
    public String pipeType;
    public Double firstDepth, endDepth;
    public String section;//管径
    public int amount, holeUse;//孔数，孔
    public String matero;//套管
    public String pipeNum;//电缆数
    public String material;//材质
    public String pressure;//压力
    public String flow;//流向
    public String embed;//埋设方式
    public String bTime;//日期
    public String pipeUser;//权属单位
    public String road;//所在道路
    public String species, note;//辅助类型和备注

    public PipePoint startPoint;
    public PipePoint endPoint;
    public int color;
}
