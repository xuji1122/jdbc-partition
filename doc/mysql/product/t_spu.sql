# 商品基本信息 spu维度
DROP TABLE IF EXISTS t_spu;
CREATE TABLE t_spu
(
	id                BIGINT PRIMARY KEY    AUTO_INCREMENT
	COMMENT '数据库自增长列',
	product_unit_cde  CHAR(64)     NOT NULL
	COMMENT '商品编码',
	product_unit_desc VARCHAR(128) NOT NULL DEFAULT ''
	COMMENT '商品编码描述',
	create_dttm       DATETIME     NOT NULL DEFAULT now(),
	UNIQUE KEY ux_product_unit_cde(product_unit_cde)
)
	COMMENT '商品信息表spu维度'
