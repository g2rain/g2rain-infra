package com.g2rain.infra.enums;


import lombok.Getter;

/**
 * @author alpha
 * @since 2026/5/4
 */
@Getter
public enum I18nMsgUsage {
    DICTIONARY("字典"),
    ERROR_CODE("错误码"),
    UI_MESSAGE("页面文案");

    private final String desc;

    I18nMsgUsage(String desc) {
        this.desc = desc;
    }

    public static I18nMsgUsage fromName(String name) {
        for (I18nMsgUsage item : I18nMsgUsage.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }

        return null;
    }
}
