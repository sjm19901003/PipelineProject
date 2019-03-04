package com.dianping.pipeline.tools;


import android.text.TextUtils;

public class PipelineMap {
    /******************************************************************************************************/

    //以下顺序必须一致
    //特征线相关
    private static final int[] PIPELINE_COLORS = {0xAAFF0000, 0xAA7FFF00, 0xAA0000EE, 0xAACD8500,
            0xAAB22222, 0xAAFF00FF, 0xAAFF7F00, 0xAA00FFFF, 0xAA0A0A0A};

    private static final String[] ELECTRIC_FLAG = {"GD", "LD", "DC", "XH", "DG", "DY", "QD", "CD"};
    private static final String[] COMMUNICATE_FLAG = {"DX", "DS", "XX", "JK", "JY", "DT", "QT", "CT"};
    private static final String[] WATER_SUPPLY_FLAG = {"SS", "QS", "ZS", "XF", "LS", "QJ", "CJ"};
    private static final String[] DRAINAGE_FLAG = {"YS", "WS", "HL", "QP"};
    private static final String[] GAS_FLAG = {"MQ", "TR", "YH", "QR", "CR"};
    private static final String[] HEAT_FLAG = {"RZ", "RS", "QL"};
    private static final String[] INDUSTRY_FLAG = {"GY", "QQ", "YU", "YQ", "YY", "CY", "HY", "PZ", "YX", "QG", "CS"};
    private static final String[] OTHER_FLAG = {"ZH", "TS", "CQ"};

    public static final String[][] PIPELINE_FLAGS = {ELECTRIC_FLAG, COMMUNICATE_FLAG, WATER_SUPPLY_FLAG,
            DRAINAGE_FLAG, GAS_FLAG, HEAT_FLAG, INDUSTRY_FLAG, OTHER_FLAG};
    private static final String[] PIPELINE_TYPE_NAMES = {"电力", "通信", "给水", "排水", "燃气", "热力", "工业", "其他"};
    private static final String[] POINT_PREFIX = {"DL", "TX", "JS", "PS", "RQ", "RL", "GY", "QT"};

    /******************************************************************************************************/

    //电力
    private static final String[] ELECTRIC_POWER = {
            "BYX", "DD", "FPD", "GD", "GJ", "JBD",
            "KZG", "LD", "RK", "SG", "SK", "TCD",
            "TFJ", "XHD", "YJDGD", "YLK", "ZJD"};
    //通信
    private static final String[] COMMUNICATE = {
            "ZJD", "YLK", "YJDGD", "TCD", "SXT", "SK",
            "SG", "RK", "JXX", "JH", "JBD", "GJ", "GD",
            "FXX", "FPD", "DHT","RK"};
    //热力
    private static final String[] HEAT = {
            "ZSQ", "TYX", "YLK", "YJBH", "PXD", "GJ",
            "JH", "NSG", "TCD", "CD", "ZJD","WSFPXD","FPD","BJ","BC","GD","FMJ","XFM","WG"};
    //燃气
    private static final String[] GAS = {
            "NSG", "CD", "PXD", "WSFPXD", "FPD",
            "BJ", "BC", "GD", "ZSQ", "GJ",
            "FM", "YJBH", "WG", "JH", "XFM","YLK","FMJ","ZJD","TCD","TYX"};
    //排水
    private static final String[] DRAINAGE = {
            "ZJD", "YSB", "YLK", "YLJ", "YJ",
            "TCD", "PXD", "JH", "HFC", "GJ",
            "FPD", "CSK", "AJ"};
    //工业
    private static final String[] INDUSTRY = {
            "BC", "GD", "FMJ", "XFM", "NSG",
            "TYF", "YJBH", "ZSQ", "JH", "BJ",
            "FPD", "WSFPXD", "PXD", "CD", "WG",
            "TCD", "ZJD", "GJ", "YLK"};
    //给水
    private static final String[] WATER_SUPPLY = {
            "SB", "GD", "XFS", "PQF", "WSFPXD",
            "PNF", "XFM", "FMJ", "SZGGQSQ", "BC",
            "BJ", "FPD", "CD", "ZJD", "PXD",
            "TCD", "WSF", "JH", "YLK", "DXXFS", "GJ",
            "WG", "FM"};
    //其他
    private static final String[] OTHER = {"ZJD", "TCD", "JH", "GD", "FPD", "CD"};

    //建立Spinner的数据源
    private static final String[][] mPOINTSCOLL = {
            ELECTRIC_POWER,
            COMMUNICATE,
            WATER_SUPPLY,
            DRAINAGE,
            GAS,
            HEAT,
            INDUSTRY,
            OTHER
    };

    public static String[] getPointsWithCategory(int categoryIndex) {
        return mPOINTSCOLL[categoryIndex];
    }

    //获取管种的英文名
    public static String getPointPrefex(int categoryIndex) {
        return POINT_PREFIX[categoryIndex];
    }

    //获取对应管种大类下的各个子类
    public static String[] getPipelineFlag(int index) {
        return PIPELINE_FLAGS[index];
    }

    //获取管种对应的颜色
    public static int getPipelineColor(int index) {
        return PIPELINE_COLORS[index];
    }

    //获取管种对应的索引号
    public static int getPipelineTypeIndex(String typeName) {
        int index = 0;
        for (int i = 0; i < PIPELINE_TYPE_NAMES.length; i++) {
            if (!TextUtils.isEmpty(typeName) && typeName.equals(PIPELINE_TYPE_NAMES[i])) {
                index = i;
                break;
            }
        }
        return index;
    }

    //根据一个管种的typeName获取对应的大类：如DH 对应的是电力, 返回结果是中文名
    public static String getPipelineCategory(String typeName) {
        for (int i = 0; i < PIPELINE_FLAGS.length; i++) {
            String[] flags = PIPELINE_FLAGS[i];
            for (int j = 0; j < flags.length; j++) {
                if (!TextUtils.isEmpty(typeName) && typeName.equals(flags[j])) {
                    return PIPELINE_TYPE_NAMES[i];
                }
            }
        }
        return "电力";
    }
}
