package com.cleven.clchat.clchat.home.Bean;

/**
 * Created by cleven on 2018/12/14.
 */

import java.util.List;

/**
 消息中的@提醒信息
 */
public class CLMentionedInfo {

    /**
     @提醒的类型
     */
    private CLMentionedType type;
    /**
     @的用户ID列表
     @discussion 如果type是@所有人，则可以传nil
     */
    private List<String> userIdList;
    /**
     是否@了我
     */
    private boolean isMentionedMe;
}
