package com.cleven.clchat.home.Bean;

public enum CLUploadStatus {
    /**
     上传中
     */
    UploadStatus_upload(0),

    /**
     上传失败
     */
    UploadStatus_fail(1),

    /**
     上传成功
     */
    UploadStatus_success(2);

    private int typeName;

    CLUploadStatus(int typeName){
        this.typeName = typeName;
    }

    /**
     * 根据类型的名称，返回类型的枚举实例。
     *
     * @param typeName 类型名称
     */
    public static CLUploadStatus fromTypeName(int typeName) {
        for (CLUploadStatus type : CLUploadStatus.values()) {
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