package com.cleven.clchat.home.Bean;

public enum CLReceivedStatus {

    /**
     * 未读
     */
    ReceivedStatus_UNREAD(0),

    /**
     * 已读
     */
    ReceivedStatus_READ(1),

    /**
     已听
     仅用于语音消息
     */
    ReceivedStatus_LISTENED(2),

    /**
     已下载
     */
    ReceivedStatus_DOWNLOADED(3);

    private int typeName;

    CLReceivedStatus(int typeName){
        this.typeName = typeName;
    }

    /**
     * 根据类型的名称，返回类型的枚举实例。
     *
     * @param typeName 类型名称
     */
    public static CLReceivedStatus fromTypeName(int typeName) {
        for (CLReceivedStatus type : CLReceivedStatus.values()) {
            if (type.getTypeName() == typeName) {
                return type;
            }
        }
        return null;
    }

    public int getTypeName() {
        return this.typeName;
    }

}
