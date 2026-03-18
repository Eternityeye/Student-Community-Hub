-- 悬赏任务表
CREATE TABLE IF NOT EXISTS `reward_task` (
  `task_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `user_id` bigint(20) NOT NULL COMMENT '发布者用户ID',
  `title` varchar(255) NOT NULL COMMENT '任务标题',
  `category` char(1) DEFAULT '0' COMMENT '任务分类（0取快递 1占座 2代购 3答疑 4其他）',
  `description` text NOT NULL COMMENT '任务描述',
  `reward` varchar(500) NOT NULL COMMENT '悬赏说明（如：20元、一杯奶茶、以物易物、面议）',
  `location` varchar(255) NOT NULL COMMENT '任务地点',
  `deadline` datetime NOT NULL COMMENT '截止时间',
  `taker_id` bigint(20) DEFAULT NULL COMMENT '接单者用户ID',
  `status` char(1) DEFAULT '0' COMMENT '任务状态（0待接单 1进行中 2已完成待确认 3已关闭）',
  `images` varchar(2000) DEFAULT NULL COMMENT '任务图片，多个用逗号分隔',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`task_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_taker_id` (`taker_id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='悬赏任务表';

-- 任务评价表
CREATE TABLE IF NOT EXISTS `task_review` (
  `review_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `task_id` bigint(20) NOT NULL COMMENT '任务ID',
  `reviewer_id` bigint(20) NOT NULL COMMENT '评价者用户ID',
  `reviewee_id` bigint(20) NOT NULL COMMENT '被评价者用户ID',
  `role_type` char(1) DEFAULT '0' COMMENT '评价类型（0发布者评接单者 1接单者评发布者）',
  `rating` int(11) NOT NULL COMMENT '星级评分（1-5）',
  `content` varchar(1000) DEFAULT NULL COMMENT '评价内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`review_id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_reviewer_id` (`reviewer_id`),
  KEY `idx_reviewee_id` (`reviewee_id`),
  UNIQUE KEY `uk_task_reviewer_type` (`task_id`, `reviewer_id`, `role_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务评价表';
