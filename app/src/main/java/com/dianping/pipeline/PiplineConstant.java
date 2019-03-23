package com.dianping.pipeline;

/**
 * Created by songjunmin on 2019/1/13.
 */

public class PiplineConstant {
    public static final String[] pointColumnNames = {"pointID", "name","auxType", "code", "feature", "jpgNo", "manHole", "manHoleSize",
            "mark", "note", "type", "wellDepth", "x", "y", "h"};

    public static final String[] lineColumnNames = {"lineID", "startID", "endID", "pipeType","firstDepth", "endDepth",
            "section","amount","holeUse","matero","pipeNum","material","pressure","flow","embed","bTime","pipeUser",
            "road","species","note"};

    public enum POP_EVENT_TYPE {
        ADD_POINT,
        ADD_LINE,
        MOVE,
        SAVE,
        NONE;
    }

    public enum MARKER_EVENT_TYPE {
        CLICK_FINISH,
        POINT_DELETE,
        POINT_EDIT,
        POINT_MOVE,
        DEFAULT
    }

    public enum POLYLINE_EVENT_TYPE {
        LINE_DELETE,
        LINE_EDIT,
        DEFAULT
    }
}
