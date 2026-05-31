package com.daniellu.lawyer.common.constant;

/**
 * 数据状态
 */
public class DataStatus {

    private DataStatus() {
        // 防止实例化
    }

    /**
     * 已删除，不可恢复到其他状态
     */
    public static final Integer DELETED = 0;

    /**
     * 正常
     */
    public static final Integer NORMAL = 1;


    /**
     * 已禁用，可以恢复到正常状态
     */
    public static final Integer DISABLED = 2;

}
