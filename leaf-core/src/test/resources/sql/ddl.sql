DROP TABLE IF EXISTS `leaf_alloc`;
CREATE TABLE `leaf_alloc`  (
    `biz_tag` varchar(128) NOT NULL DEFAULT '',
    `max_id` bigint(20) NOT NULL DEFAULT 1,
    `step` int(11) NOT NULL,
    `description` varchar(256) NULL DEFAULT NULL,
    `update_time` timestamp(0) NOT NULL DEFAULT current_timestamp(0) ON UPDATE CURRENT_TIMESTAMP(0),
    PRIMARY KEY (`biz_tag`)
) ENGINE = InnoDB ROW_FORMAT = Dynamic;

INSERT INTO `leaf_alloc` VALUES ('leaf-segment-test', 1, 100, 'Test leaf Segment Mode Get Id', '2019-11-12 11:22:39');
