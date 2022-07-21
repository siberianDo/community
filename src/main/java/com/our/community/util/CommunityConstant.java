package com.our.community.util;

/**
 * 常量
 */
public interface CommunityConstant {
    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 默认状态登录凭证过期时间（不勾选记住我）
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 记住状态下的登录凭证过期时间（勾选记住我）
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    /**
     * 实体类型帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型评论
     */
    int ENTITY_TYPE_COMMENT = 2;
}
