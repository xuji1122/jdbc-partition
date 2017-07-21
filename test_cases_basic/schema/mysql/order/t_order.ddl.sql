#订单表
DROP TABLE if EXISTS t_order;
CREATE TABLE t_order
(
	id          BIGINT PRIMARY KEY,
	user_id     BIGINT         NOT NULL,
	total_price DECIMAL(17, 2) NOT NULL DEFAULT 0,
	channel     INT            NOT NULL DEFAULT 0,
	status      VARCHAR(16),
	create_dttm DATETIME       NOT NULL DEFAULT now(),
	update_dttm DATETIME       NOT NULL DEFAULT now(),
	KEY idx_user_id(user_id)
)
	COMMENT '订单表';
