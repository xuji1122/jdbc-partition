# 卖家表，分库不分表
DROP TABLE if EXISTS t_seller;
CREATE TABLE t_seller
(
	id          BIGINT PRIMARY KEY,
	seller_name VARCHAR(64) NOT NULL,
	status      VARCHAR(16),
	create_dttm DATETIME    NOT NULL DEFAULT now(),
	UNIQUE KEY ux_seller_name(seller_name)
)
	COMMENT '卖家表'
