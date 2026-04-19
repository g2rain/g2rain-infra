package com.g2rain.infra.enums;


import com.g2rain.common.exception.ErrorCode;

/**
 * @author alpha
 * @since 2025/12/28
 */
public enum InfraErrorCode implements ErrorCode {

    SNOWFLAKE_WORKER_ID_FAIL("infra.40001", "雪花 workerId 获取失败"),
    WORKER_ID_INVALID("infra.40002", "当前 雪花 workerId 无效, 请等待重新抢号"),
    CLOCK_BACKWARD("infra.40003", "时钟回拨超过允许范围"),
    BIZ_TAG_NOT_FOUND("infra.40004", "业务号段不存在或未初始化"),
    SEGMENT_NOT_READY("infra.40005", "号段资源未准备好"),
    SEGMENT_UPDATE_FAILED("infra.40006", "数据库号段更新失败"),
    SEGMENT_NOT_FOUND("infra.40007", "数据库号段记录不存在");

    private final String code;

    private final String messageTemplate;

    /**
     * 构造系统错误码
     *
     * @param code            错误码（遵循4xxx客户端错误，5xxx服务器错误）
     * @param messageTemplate 消息模板（支持{0:param}顺序占位符或{key}键值对占位符）
     */
    InfraErrorCode(String code, String messageTemplate) {
        this.code = code;
        this.messageTemplate = messageTemplate;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String messageTemplate() {
        return messageTemplate;
    }
}
