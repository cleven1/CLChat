package com.cleven.clchat.home.Bean;

public enum CLMessageDirection {
    /**
     * 发送
     */
    MessageDirection_SEND(0),
    /**
     * 接受
     */
    MessageDirection_RECEIVE(1);

    private int typeName;

    CLMessageDirection(int typeName){
        this.typeName = typeName;
    }

    /**
     * 根据类型的名称，返回类型的枚举实例。
     *
     * @param typeName 类型名称
     */
    public static CLMessageDirection fromTypeName(int typeName) {
        for (CLMessageDirection type : CLMessageDirection.values()) {
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
