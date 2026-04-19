-- =============================================
-- g2rain_infra 数据库表结构
-- MySQL 8.0 版本
-- =============================================

-- 创建数据库
CREATE
DATABASE IF NOT EXISTS `g2rain_infra` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE
`g2rain_infra`;

-- =============================================
-- 1. 字典用途表 (dictionary_usage)
-- =============================================
DROP TABLE IF EXISTS `dictionary_usage`;

CREATE TABLE `dictionary_usage`
(
    `id`          BIGINT      NOT NULL COMMENT                                                          '主键标识',
    `usage_code`  VARCHAR(64) NOT NULL COMMENT                                                          '字典用途代码',
    `usage_name`  VARCHAR(64) NOT NULL COMMENT                                                          '字典用途名称',
    `description` VARCHAR(512)         DEFAULT NULL COMMENT                                             '业务描述',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version`     INT         NOT NULL DEFAULT 0 COMMENT                                                '记录版本',
    `delete_flag` TINYINT     NOT NULL DEFAULT 0 COMMENT                                                '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '字典用途表';


-- =============================================
-- 2. 字典明细表 (dictionary_item)
-- =============================================
DROP TABLE IF EXISTS `dictionary_item`;

CREATE TABLE `dictionary_item`
(

    `id`                  BIGINT       NOT NULL COMMENT                                                 '主键标识',
    `parent_id`           BIGINT                DEFAULT NULL COMMENT                                    '父节点ID,用于 tree 结构字典',
    `dictionary_usage_id` BIGINT       NOT NULL COMMENT                                                 '字典用途主键标识',
    `code`                VARCHAR(64)  NOT NULL COMMENT                                                 '字典项编码,用于系统标识',
    `name`                VARCHAR(128) NOT NULL COMMENT                                                 '字典名称(默认语言)',
    `description`         VARCHAR(512)          DEFAULT NULL COMMENT                                    '业务描述',
    `sort_index`          INT                   DEFAULT NULL COMMENT                                    '字典排序',
    `create_time`         TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                              '创建时间',
    `update_time`         TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT  '更新时间',
    `version`             INT          NOT NULL DEFAULT 0 COMMENT                                       '记录版本',
    `delete_flag`         TINYINT      NOT NULL DEFAULT 0 COMMENT                                       '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '字典明细表';


-- =============================================
-- 3. 地域-语言设置表 (locale_setting)
-- =============================================
DROP TABLE IF EXISTS `locale_setting`;

CREATE TABLE `locale_setting`
(
    `id`            BIGINT      NOT NULL COMMENT                                                        '主键标识',
    `language_code` VARCHAR(32) NOT NULL COMMENT                                                        '语言编码,如 zh',
    `region_code`   VARCHAR(32) NOT NULL COMMENT                                                        '国家/地区编码,如 CN',
    `code`          VARCHAR(64) NOT NULL COMMENT                                                        '区域标识,如 zh-CN',
    `name`          VARCHAR(64) NOT NULL COMMENT                                                        '区域名称,如 中国-简体中文',
    `description`   VARCHAR(255)         DEFAULT NULL COMMENT                                           '语言描述',
    `create_time`   TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                    '创建时间',
    `update_time`   TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT        '更新时间',
    `version`       INT         NOT NULL DEFAULT 0 COMMENT                                              '记录版本',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '地域-语言设置表';

-- =============================================
-- 4. 国际化信息用途表 (i18n_message_usage)
-- =============================================
DROP TABLE IF EXISTS `i18n_message_usage`;

CREATE TABLE `i18n_message_usage`
(
    `id`          BIGINT       NOT NULL COMMENT                                                         '主键标识',
    `usage_code`  VARCHAR(64)  NOT NULL COMMENT                                                         '用途编码,用于在代码中标识用途:DICTIONARY 字典, ERROR_CODE 错误码为固定用途',
    `name`        VARCHAR(128) NOT NULL COMMENT                                                         '用途名称',
    `remark`      VARCHAR(255)          DEFAULT NULL COMMENT                                            '业务描述',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version`     INT          NOT NULL DEFAULT 0 COMMENT                                               '记录版本',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '国际化信息用途表';

-- =============================================
-- 5. 国际化信息表 (i18n_message)
-- =============================================
DROP TABLE IF EXISTS `i18n_message`;

CREATE TABLE `i18n_message`
(
    `id`               BIGINT       NOT NULL COMMENT                                                    '主键标识',
    `message_usage_id` BIGINT       NOT NULL COMMENT                                                    '用途标识',
    `language_code`    VARCHAR(32)  NOT NULL COMMENT                                                    '语言编码,如 zh',
    `region_code`      VARCHAR(32)           DEFAULT NULL COMMENT                                       '国家/地区编码,如 CN',
    `message_code`     VARCHAR(128) NOT NULL COMMENT                                                    '国际化消息编码(唯一)',
    `message_text`     TEXT         NOT NULL COMMENT                                                    '国际化内容文本',
    `extend_field`     JSON                  DEFAULT NULL COMMENT                                       '扩展字段,存储额外格式化内容',
    `create_time`      TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                 '创建时间',
    `update_time`      TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT     '更新时间',
    `version`          INT          NOT NULL DEFAULT 0 COMMENT                                          '记录版本',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '国际化信息表';

-- =============================================
-- 6. 网关路由表 (route_definition)
-- =============================================
DROP TABLE IF EXISTS `route_definition`;

CREATE TABLE `route_definition`
(
    `id`                BIGINT       NOT NULL COMMENT                                                   '路由标识',
    `name`              VARCHAR(128) NOT NULL COMMENT                                                   '路由名称',
    `endpoint_host`     VARCHAR(256) NOT NULL COMMENT                                                   '终端主机',
    `endpoint_path`     VARCHAR(256)          DEFAULT NULL COMMENT                                      '终端路径',
    `context`           VARCHAR(128) NOT NULL COMMENT                                                   '转发路径',
    `path`              VARCHAR(256) NOT NULL COMMENT                                                   '请求路径',
    `method`            VARCHAR(32)           DEFAULT NULL COMMENT                                      '请求方法',
    `header_parameters` VARCHAR(512)          DEFAULT NULL COMMENT                                      '请求头参',
    `content_type`      VARCHAR(64)           DEFAULT NULL COMMENT                                      '内容类型',
    `description`       VARCHAR(512)          DEFAULT NULL COMMENT                                      '业务说明',
    `create_time`       TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                '创建时间',
    `update_time`       TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT    '更新时间',
    `version`           INT          NOT NULL DEFAULT 0 COMMENT                                         '记录版本',
    `delete_flag`       TINYINT      NOT NULL DEFAULT 0 COMMENT                                         '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4 COMMENT=                                                        '网关路由表';

-- =============================================
-- 7. 全局唯一ID管理表 (g2rain_raindrop)
-- =============================================
DROP TABLE IF EXISTS `g2rain_raindrop`;

CREATE TABLE `g2rain_raindrop`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT                                          '主键标识',
    `biz_tag`     VARCHAR(128) NOT NULL COMMENT                                                         '业务标识,每个业务对应一行',
    `max_id`      BIGINT       NOT NULL DEFAULT 1 COMMENT                                               '当前分配到的最大ID',
    `step`        INT          NOT NULL DEFAULT 0 COMMENT                                               '分配步长,用于批量预分配ID',
    `description` VARCHAR(256)          DEFAULT '' COMMENT                                              '业务描述',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version`     INT          NOT NULL DEFAULT 0 COMMENT                                               '记录版本',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_biz_tag` (`biz_tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '全局唯一ID管理表';

-- 初始化路由表
INSERT INTO `route_definition` (`id`, `name`, `endpoint_host`, `endpoint_path`, `context`, `path`, `method`, `header_parameters`, `content_type`, `description`, `update_time`, `create_time`, `version`, `delete_flag`)
VALUES (1, '基础支撑接口请求', 'lb://g2rain-infra/', NULL, 'infra', '/**', 'GET', NULL, NULL, '基础支撑接口请求',
        '2025-12-24 11:34:33', '2025-12-24 11:34:33', 1, 0),
       (2, '基础支撑接口提交', 'lb://g2rain-infra/', NULL, 'infra', '/**', 'POST', NULL, NULL, '基础支撑接口提交',
        '2025-12-24 11:34:33', '2025-12-24 11:34:33', 1, 0),
       (3, '基础支撑接口修改', 'lb://g2rain-infra/', NULL, 'infra', '/**', 'PUT', NULL, NULL, '基础支撑接口修改',
        '2025-12-24 11:34:33', '2025-12-24 11:34:33', 1, 0),
       (4, '基础支撑接口删除', 'lb://g2rain-infra/', NULL, 'infra', '/**', 'DELETE', NULL, NULL, '基础支撑接口删除',
        '2025-12-24 11:34:33', '2025-12-24 11:34:33', 1, 0),
       (5, '系统支撑接口请求', 'lb://g2rain-basis/', NULL, 'basis', '/**', 'GET', NULL, NULL, '系统支撑接口请求',
        '2025-12-24 11:34:33', '2025-12-24 11:34:33', 1, 0),
       (6, '系统支撑接口提交', 'lb://g2rain-basis/', NULL, 'basis', '/**', 'POST', NULL, NULL, '系统支撑接口提交',
        '2025-12-24 11:34:33', '2025-12-24 11:34:33', 1, 0),
       (7, '系统支撑接口修改', 'lb://g2rain-basis/', NULL, 'basis', '/**', 'PUT', NULL, NULL, '系统支撑接口修改',
        '2025-12-24 11:34:33', '2025-12-24 11:34:33', 1, 0),
       (8, '系统支撑接口删除', 'lb://g2rain-basis/', NULL, 'basis', '/**', 'DELETE', NULL, NULL, '系统支撑接口删除',
        '2025-12-24 11:34:33', '2025-12-24 11:34:33', 1, 0);

insert into g2rain_raindrop (`biz_tag`, `max_id`, `step`, `description`) values ('COMMON', 1, 1000, '全局共用号段');
