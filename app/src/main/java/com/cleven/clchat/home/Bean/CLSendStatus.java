package com.cleven.clchat.home.Bean;

public enum CLSendStatus {
    /**
     发送中
     */
    SendStatus_SENDING(0),

    /**
     发送失败
     */
    SendStatus_FAILED(1),

    /**
     已发送成功
     */
    SendStatus_SEND(2),

    /**
     对方已接收
     */
    SendStatus_RECEIVED(3),

    /**
     对方已阅读
     */
    SendStatus_READ(4),

    /**
     对方已销毁
     */
    SendStatus_DESTROYED(5),

    /**
     发送已取消
     */
    SendStatus_CANCELED(6),

    /**
     无效类型
     */
    SendStatus_INVALID(10000);

    private int typeName;

    CLSendStatus(int typeName){
        this.typeName = typeName;
    }

    /**
     * 根据类型的名称，返回类型的枚举实例。
     *
     * @param typeName 类型名称
     */
    public static CLSendStatus fromTypeName(int typeName) {
        for (CLSendStatus type : CLSendStatus.values()) {
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
