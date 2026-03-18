-- 社交模块 SQL 脚本

-- 社交帖子表
DROP TABLE IF EXISTS social_post;
CREATE TABLE social_post (
  post_id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  user_id bigint(20) NOT NULL COMMENT '用户ID',
  content longtext NOT NULL COMMENT '帖子内容（富文本HTML）',
  images varchar(2000) DEFAULT NULL COMMENT '图片地址，多个用逗号分隔',
  tags varchar(500) DEFAULT NULL COMMENT '标签，多个用逗号分隔',
  like_count int(11) DEFAULT 0 COMMENT '点赞数',
  comment_count int(11) DEFAULT 0 COMMENT '评论数',
  status char(1) DEFAULT '0' COMMENT '状态（0正常 1已删除 2已屏蔽）',
  create_by varchar(64) DEFAULT NULL COMMENT '创建者',
  create_time datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by varchar(64) DEFAULT NULL COMMENT '更新者',
  update_time datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  remark varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (post_id),
  KEY idx_user_id (user_id),
  KEY idx_create_time (create_time),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社交帖子表';

-- 社交评论表
DROP TABLE IF EXISTS social_comment;
CREATE TABLE social_comment (
  comment_id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  post_id bigint(20) NOT NULL COMMENT '帖子ID',
  user_id bigint(20) NOT NULL COMMENT '用户ID',
  parent_id bigint(20) DEFAULT NULL COMMENT '父评论ID（回复评论时使用）',
  reply_to_user_id bigint(20) DEFAULT NULL COMMENT '回复的用户ID',
  content longtext NOT NULL COMMENT '评论内容',
  images varchar(1000) DEFAULT NULL COMMENT '评论图片，多个用逗号分隔',
  status char(1) DEFAULT '0' COMMENT '状态（0正常 1已删除）',
  create_by varchar(64) DEFAULT NULL COMMENT '创建者',
  create_time datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by varchar(64) DEFAULT NULL COMMENT '更新者',
  update_time datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (comment_id),
  KEY idx_post_id (post_id),
  KEY idx_user_id (user_id),
  KEY idx_parent_id (parent_id),
  KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社交评论表';

-- 社交点赞表
DROP TABLE IF EXISTS social_like;
CREATE TABLE social_like (
  like_id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  post_id bigint(20) NOT NULL COMMENT '帖子ID',
  user_id bigint(20) NOT NULL COMMENT '用户ID',
  create_time datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (like_id),
  UNIQUE KEY uk_post_user (post_id, user_id),
  KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社交点赞表';

-- 社交举报表
DROP TABLE IF EXISTS social_report;
CREATE TABLE social_report (
  report_id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '举报ID',
  post_id bigint(20) NOT NULL COMMENT '帖子ID',
  user_id bigint(20) NOT NULL COMMENT '举报用户ID',
  reasons varchar(500) NOT NULL COMMENT '举报原因，多个用逗号分隔',
  description varchar(1000) DEFAULT NULL COMMENT '详细描述',
  status char(1) DEFAULT '0' COMMENT '处理状态（0待处理 1已处理 2已驳回）',
  handle_by varchar(64) DEFAULT NULL COMMENT '处理人',
  handle_time datetime DEFAULT NULL COMMENT '处理时间',
  handle_result varchar(500) DEFAULT NULL COMMENT '处理结果',
  create_time datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (report_id),
  KEY idx_post_id (post_id),
  KEY idx_user_id (user_id),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社交举报表';
