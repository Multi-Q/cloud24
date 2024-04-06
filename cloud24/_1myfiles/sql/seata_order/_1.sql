create database seata_order;

use  seata_order;

CREATE TABLE IF NOT EXISTS `undo_log`
(
    `branch_id`     BIGINT       NOT NULL COMMENT 'branch transaction id',
    `xid`           VARCHAR(128) NOT NULL COMMENT 'global transaction id',
    `context`       VARCHAR(128) NOT NULL COMMENT 'undo_log context,such as serialization',
    `rollback_info` LONGBLOB     NOT NULL COMMENT 'rollback info',
    `log_status`    INT(11)      NOT NULL COMMENT '0:normal status,1:defense status',
    `log_created`   DATETIME(6)  NOT NULL COMMENT 'create datetime',
    `log_modified`  DATETIME(6)  NOT NULL COMMENT 'modify datetime',
    UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT ='AT transaction mode undo table';
ALTER TABLE `undo_log` ADD INDEX `ix_log_created` (`log_created`);


-- t_order
use seata_order;
CREATE TABLE t_order(

                        `id` BIGINT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,

                        `user_id` BIGINT(11) DEFAULT NULL COMMENT '用户id',

                        `product_id` BIGINT(11)DEFAULT NULL COMMENT '产品id',

                        `count` INT(11) DEFAULT NULL COMMENT '数量',

                        `money` DECIMAL(11,0) DEFAULT NULL COMMENT '金额',

                        `status` INT(1) DEFAULT NULL COMMENT '订单状态: 0:创建中; 1:已完结'

)ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

SELECT * FROM t_order;


