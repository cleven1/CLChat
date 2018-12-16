package com.cleven.clchat.home.Bean;

public enum CLSentStatus {
    /**
     发送中
     */
    SentStatus_SENDING(0),

    /**
     发送失败
     */
    SentStatus_FAILED(1),

    /**
     已发送成功
     */
    SentStatus_SENT(2),

    /**
     对方已接收
     */
    SentStatus_RECEIVED(3),

    /**
     对方已阅读
     */
    SentStatus_READ(4),

    /**
     对方已销毁
     */
    SentStatus_DESTROYED(5),

    /**
     发送已取消
     */
    SentStatus_CANCELED(6),

    /**
     无效类型
     */
    SentStatus_INVALID(10000);

    private int typeName;

    CLSentStatus(int typeName){
        this.typeName = typeName;
    }

    /**
     * 根据类型的名称，返回类型的枚举实例。
     *
     * @param typeName 类型名称
     */
    public static CLSentStatus fromTypeName(int typeName) {
        for (CLSentStatus type : CLSentStatus.values()) {
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
