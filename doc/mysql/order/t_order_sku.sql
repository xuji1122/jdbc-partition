#订单商品表
DROP TABLE if EXISTS t_order_sku;
CREATE TABLE t_order_sku
(
	id           BIGINT PRIMARY KEY,
	order_id     BIGINT         NOT NULL,
	sku_stock_id BIGINT         NOT NULL,
	buy_num      INT            NOT NULL DEFAULT 0,
	buy_price    DECIMAL(17, 2) NOT NULL DEFAULT 0,
	create_dttm  DATETIME       NOT NULL DEFAULT now(),
	update_dttm  DATETIME       NOT NULL DEFAULT now(),
	KEY idx_order_id(order_id),
	KEY idx_sku_stock(sku_stock_id)
)
	COMMENT '订单商品表';
