# 商品卖家关联关系表  seller_id维度分库分表
DROP TABLE if EXISTS t_seller_spu;
CREATE TABLE t_seller_spu
(
	id               BIGINT PRIMARY KEY
	COMMENT '卖家商品的ID',
	product_unit_cde VARCHAR(128) NOT NULL DEFAULT ''
	COMMENT '商品编码spu',
	seller_id        BIGINT       NOT NULL DEFAULT 0
	COMMENT '卖家ID',
	create_dttm      DATETIME     NOT NULL DEFAULT now(),
	KEY idx_product_unit_cde(product_unit_cde),
	KEY idx_seller_id(seller_id),
	UNIQUE KEY uk_product_unit_cde_seller_id(product_unit_cde, seller_id)
)
	COMMENT '商品卖家关联关系表'
