CREATE TABLE `invoke_item_detail` (
  `id` BIGINT UNSIGNED AUTO_INCREMENT NOT NULL COMMENT '主键',
  `invoke_id` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '调用id',
  `class_name` VARCHAR(256) NOT NULL DEFAULT '' COMMENT '全限定类名',
  `bean_name` VARCHAR(256) NOT NULL DEFAULT '' COMMENT 'bean名',
  `method` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '方法名',
  `args` VARCHAR(65535) NOT NULL DEFAULT '' COMMENT '方法入参',
  `return_value` VARCHAR(65535) NOT NULL DEFAULT '' COMMENT '返回',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态，枚举值：(0,有效),(1,删除)',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_invoke_id` (`invoke_id`)
) ENGINE=InnoDB CHARSET=utf8mb4 COMMENT='调用详情表'

CREATE TABLE `invoke_biz_relation` (
  `id` BIGINT UNSIGNED AUTO_INCREMENT NOT NULL COMMENT '主键',
  `app` VARCHAR(32) NOT NULL DEFAULT '' COMMENT '应用名',
  `unique_id` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '调用id',
  `invoke_id` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '调用方法id',
  `serial_no` TINYINT NOT NULL DEFAULT 0 COMMENT '调用顺序记录 ，从0开始',
  `type` TINYINT NOT NULL DEFAULT 0 COMMENT '状态，枚举值：(0,调用),(1,依赖)',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态，枚举值：(0,有效),(1,删除)',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_unique_id` (`unique_id`)
) ENGINE=InnoDB CHARSET=utf8mb4 COMMENT='调用关系表'