/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50727
 Source Host           : localhost:3306
 Source Schema         : stock_db

 Target Server Type    : MySQL
 Target Server Version : 50727
 File Encoding         : 65001

 Date: 17/10/2021 23:19:34
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for stock_bi_table
-- ----------------------------
DROP TABLE IF EXISTS `stock_bi_table`;
CREATE TABLE `stock_bi_table`  (
  `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键',
  `symbol` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '股票代码',
  `type` int(11) NOT NULL COMMENT '类型：1：向上笔，-1：向下笔',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `start_price` decimal(7, 2) NULL DEFAULT NULL COMMENT '开始价格',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `end_price` decimal(7, 2) NULL DEFAULT NULL COMMENT '结束价格',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`, `symbol`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for stock_contains_table
-- ----------------------------
DROP TABLE IF EXISTS `stock_contains_table`;
CREATE TABLE `stock_contains_table`  (
  `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键',
  `symbol` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '股票代码',
  `dt` datetime NOT NULL COMMENT '日期',
  `type` int(11) NOT NULL COMMENT '类型：1：未合并，2：合并',
  `num` int(11) NOT NULL DEFAULT 1 COMMENT '合并数量',
  `high` decimal(7, 2) NULL DEFAULT NULL COMMENT '最高价',
  `low` decimal(7, 2) NULL DEFAULT NULL COMMENT '最低价',
  `volume` decimal(20, 0) NULL DEFAULT NULL COMMENT '交易量',
  `fenxing_type` int(11) NULL DEFAULT NULL COMMENT '分型类型：1:顶分型，-1：底分型',
  `fenxing_power` int(11) NULL DEFAULT NULL COMMENT '分型强度：1：最强，2：较强，3：一般，4：较弱',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`, `symbol`, `dt`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for stock_fenxing_table
-- ----------------------------
DROP TABLE IF EXISTS `stock_fenxing_table`;
CREATE TABLE `stock_fenxing_table`  (
  `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键',
  `symbol` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '股票代码',
  `contains_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'stock_contains_table主键',
  `dt` datetime NOT NULL COMMENT '日期',
  `type` int(11) NOT NULL COMMENT '类型：1：顶分型，2：底分型',
  `level` int(11) NOT NULL COMMENT '强度：1：最强，2：较强，3：一般，4：较弱',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`, `symbol`, `dt`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for stock_seg_table
-- ----------------------------
DROP TABLE IF EXISTS `stock_seg_table`;
CREATE TABLE `stock_seg_table`  (
  `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键',
  `symbol` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '股票代码',
  `type` int(11) NOT NULL COMMENT '类型：1：向上段，-1：向下段',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `start_price` decimal(7, 2) NULL DEFAULT NULL COMMENT '开始价格',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `end_price` decimal(7, 2) NULL DEFAULT NULL COMMENT '结束价格',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`, `symbol`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for stock_table
-- ----------------------------
DROP TABLE IF EXISTS `stock_table`;
CREATE TABLE `stock_table`  (
  `symbol` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '股票代码',
  `period` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '周期',
  `dt` datetime NOT NULL COMMENT '日期',
  `open` decimal(7, 2) NULL DEFAULT NULL COMMENT '开盘价',
  `high` decimal(7, 2) NULL DEFAULT NULL COMMENT '最高价',
  `low` decimal(7, 2) NULL DEFAULT NULL COMMENT '最低价',
  `close` decimal(7, 2) NULL DEFAULT NULL COMMENT '收盘价',
  `volume` decimal(20, 0) NULL DEFAULT NULL COMMENT '交易量',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`symbol`, `dt`, `period`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
