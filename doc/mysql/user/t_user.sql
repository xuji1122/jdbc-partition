# 买家表  id维度分库分表
DROP TABLE if EXISTS t_user;
CREATE TABLE t_user
(
	id          BIGINT PRIMARY KEY,
	channel     INT         NOT NULL DEFAULT 0,
	app_id      VARCHAR(64) NOT NULL,
	identifier  VARCHAR(128),
	birth_date  DATE,
	status      VARCHAR(16),
	create_dttm DATETIME    NOT NULL DEFAULT now(),
	UNIQUE KEY ux_user(channel, app_id, identifier),
	KEY index_identifier (identifier),
	KEY index_channel_app_id (channel, app_id),
	KEY index_birth_date (birth_date)
)
	COMMENT '用户表'
