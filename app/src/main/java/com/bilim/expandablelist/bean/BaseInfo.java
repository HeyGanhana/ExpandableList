package com.bilim.expandablelist.bean;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by zhangdi on 12/4/18.
 */
public class BaseInfo {
    public enum SelectStaus {
        NO_ONE_SELECTED,
        ALL_SELECTED,
        SOME_SELECTED
    }

    private static ArrayList groupTypes = new ArrayList(Arrays.asList(GroupType.values()));

    public enum GroupType {
        IMAGE,
        MUSIC,
        VIDEO,
        APP_DATA,
        BIG_FILE,
        OTHER;

        public static int getValue(GroupType type) {
            return groupTypes.indexOf(type);
        }

    }

}
