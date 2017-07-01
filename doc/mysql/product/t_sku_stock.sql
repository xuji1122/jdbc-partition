# 商品库存价格表 以seller_spu_id分库分表
DROP TABLE  if EXISTS t_sku_stock;
CREATE TABLE t_sku_stock
(
	id             BIGINT PRIMARY KEY
	COMMENT 'SKU的ID',
	seller_spu_id  BIGINT PRIMARY KEY
	COMMENT '卖家商品ID',
	price          DECIMAL(17, 2) NOT NULL DEFAULT 0
	COMMENT '价格',
	stock_num      BIGINT         NOT NULL DEFAULT 0
	COMMENT '库存数量',
	sku_stock_desc VARCHAR(128)   NOT NULL DEFAULT ''
	COMMENT '商品库存单位描述',
	KEY idx_spu_seller_id(seller_spu_id)
)
	COMMENT '商品库存价格表'
