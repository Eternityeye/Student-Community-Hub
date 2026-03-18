-- 纠纷案件表
CREATE TABLE IF NOT EXISTS `dispute_case` (
  `case_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '纠纷案件ID',
  `task_id` bigint(20) NOT NULL COMMENT '关联任务ID',
  `plaintiff_id` bigint(20) NOT NULL COMMENT '申请人(发起方)用户ID',
  `defendant_id` bigint(20) NOT NULL COMMENT '被申请人(被告方)用户ID',
  `reason` varchar(255) NOT NULL COMMENT '纠纷原因',
  `plaintiff_description` text COMMENT '申请人描述',
  `defendant_description` text COMMENT '被申请人描述',
  `plaintiff_evidence_images` varchar(2000) DEFAULT NULL COMMENT '申请人证据图片，多个用逗号分隔',
  `defendant_evidence_images` varchar(2000) DEFAULT NULL COMMENT '被申请人证据图片，多个用逗号分隔',
  `status` char(1) DEFAULT '0' COMMENT '纠纷状态（0待审理 1申请人责任 2被申请人责任 3驳回）',
  `handler_id` bigint(20) DEFAULT NULL COMMENT '处理人(管理员)ID',
  `handle_reason` varchar(500) DEFAULT NULL COMMENT '处理理由',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`case_id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_plaintiff_id` (`plaintiff_id`),
  KEY `idx_defendant_id` (`defendant_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='纠纷案件表';

-- 用户信誉表
CREATE TABLE IF NOT EXISTS `user_credit` (
  `credit_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '信誉记录ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `credit_score` int(11) DEFAULT 100 COMMENT '信誉分（0-100）',
  `dispute_count` int(11) DEFAULT 0 COMMENT '纠纷次数',
  `ban_until` datetime DEFAULT NULL COMMENT '禁止接单至(NULL表示未禁，过期时间则自动解禁)',
  `ban_reason` varchar(255) DEFAULT NULL COMMENT '禁止原因',
  `last_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`credit_id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_ban_until` (`ban_until`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信誉表';

-- 信誉历史记录表
CREATE TABLE IF NOT EXISTS `credit_log` (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `change_type` varchar(50) NOT NULL COMMENT '变动类型（dispute_loss纠纷扣分、appeal_resolve申诉解禁等）',
  `change_score` int(11) NOT NULL COMMENT '变动分数（扣分为负数）',
  `related_id` bigint(20) DEFAULT NULL COMMENT '关联ID(case_id或task_id)',
  `reason` varchar(255) DEFAULT NULL COMMENT '变动原因',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`log_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_change_type` (`change_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='信誉历史记录表';
