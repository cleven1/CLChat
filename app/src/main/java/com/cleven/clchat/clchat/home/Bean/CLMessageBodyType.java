package com.cleven.clchat.clchat.home.Bean;

/**
 * Created by cleven on 2018/12/14.
 */

public enum CLMessageBodyType {
    /// 文本
    MessageBodyType_Text(0),
    /// 图片
    MessageBodyType_Image(1),
    /// 语音
    MessageBodyType_Voice(2),
    /// 时间
    MessageBodyType_Time(3),
    /// 视频
    MessageBodyType_Video(4),
    /// 消息撤回
    MessageBodyType_ecall(5),
    /// 阅后即焚
    MessageBodyType_Immolation(6),
    /// 文件
    MessageBodyType_File(7),
    /// 位置
    MessageBodyType_Location(8),
    /// 红包
    MessageBodyType_RedPacket(9),
    /// 系统消息
    MessageBodyType_System(10);

    private int typeName;

    CLMessageBodyType(int typeName) {
        this.typeName = typeName;
    }

    /**
     * 根据类型的名称，返回类型的枚举实例。
     *
     * @param typeName 类型名称
     */
    public static CLMessageBodyType fromTypeName(int typeName) {
        for (CLMessageBodyType type : CLMessageBodyType.values()) {
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

enum CLReceivedStatus {

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

enum CLSentStatus {
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

/**
 @提醒的类型
 */
enum CLMentionedType {
    /**
     @所有人
     */
    CL_Mentioned_All(0),

    /**
     @部分指定用户
     */
    CL_Mentioned_Users(1);

    private int typeName;

    CLMentionedType(int typeName){
        this.typeName = typeName;
    }

    /**
     * 根据类型的名称，返回类型的枚举实例。
     *
     * @param typeName 类型名称
     */
    public static CLMentionedType fromTypeName(int typeName) {
        for (CLMentionedType type : CLMentionedType.values()) {
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