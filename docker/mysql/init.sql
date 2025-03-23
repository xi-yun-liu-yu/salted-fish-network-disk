/*
 Navicat Premium Data Transfer

 Source Server         : a
 Source Server Type    : MySQL
 Source Server Version : 80032
 Source Host           : localhost:3306
 Source Schema         : sfnd

 Target Server Type    : MySQL
 Target Server Version : 80032
 File Encoding         : 65001

 Date: 21/03/2025 15:31:34
*/

-- 保障层1：环境变量创建的数据库可能已存在，但确保字符集正确
CREATE DATABASE IF NOT EXISTS `sfnd`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- 保障层2：明确使用目标数据库
USE `sfnd`;


SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for file
-- ----------------------------
DROP TABLE IF EXISTS `file`;
CREATE TABLE `file`  (
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件名',
  `file_uuid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件UUID',
  `file_format` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件格式',
  `file_size` bigint NOT NULL COMMENT '文件大小（字节）',
  `download_count` int NULL DEFAULT 0 COMMENT '文件下载次数',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件URL',
  `view_permission` enum('PUBLIC','PRIVATE') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PRIVATE' COMMENT '查看权限（PUBLIC：公开，PRIVATE：私有）',
  `creator_id` bigint NOT NULL COMMENT '文件创建者ID',
  `creator_nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件创建者昵称',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '文件创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '文件更新时间',
  PRIMARY KEY (`file_uuid`) USING BTREE,
  UNIQUE INDEX `file_uuid`(`file_uuid` ASC) USING BTREE,
  INDEX `idx_creator_id`(`creator_id` ASC) USING BTREE COMMENT '创建者ID索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of file
-- ----------------------------

-- ----------------------------
-- Table structure for file_shares
-- ----------------------------
DROP TABLE IF EXISTS `file_shares`;
CREATE TABLE `file_shares`  (
  `share_id` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分享唯一标识（UUID格式）',
  `file_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '对应MongoDB文件ObjectId',
  `owner_id` int NOT NULL COMMENT '分享发起用户ID',
  `share_type` enum('public','private') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'private' COMMENT '分享类型',
  `password_hash` char(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '访问密码的哈希值（可选）',
  `expiration` datetime NULL DEFAULT NULL COMMENT '链接过期时间',
  `download_count` int NULL DEFAULT 0 COMMENT '下载次数统计',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`share_id`) USING BTREE,
  INDEX `idx_owner_file`(`owner_id` ASC, `file_id` ASC) USING BTREE,
  CONSTRAINT `file_shares_ibfk_1` FOREIGN KEY (`owner_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文件分享管理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of file_shares
-- ----------------------------

-- ----------------------------
-- Table structure for friendships
-- ----------------------------
DROP TABLE IF EXISTS `friendships`;
CREATE TABLE `friendships`  (
  `user_a` int NOT NULL COMMENT '关系发起方用户ID',
  `user_b` int NOT NULL COMMENT '关系接收方用户ID',
  `status` enum('pending','accepted','blocked') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '关系状态',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关系建立时间',
  PRIMARY KEY (`user_a`, `user_b`) USING BTREE,
  CONSTRAINT `friendships_chk_1` CHECK (`user_a` < `user_b`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户社交关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of friendships
-- ----------------------------

-- ----------------------------
-- Table structure for permissions
-- ----------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions`  (
  `perm_id` int NOT NULL AUTO_INCREMENT COMMENT '权限标识ID',
  `perm_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限名称（如：FILE_UPLOAD）',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '权限详细描述',
  PRIMARY KEY (`perm_id`) USING BTREE,
  UNIQUE INDEX `perm_name`(`perm_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统权限清单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of permissions
-- ----------------------------
INSERT INTO `permissions` VALUES (1, '游客', NULL);
INSERT INTO `permissions` VALUES (2, '普通用户', NULL);
INSERT INTO `permissions` VALUES (3, '会员', NULL);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '系统内唯一用户标识',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录用户名（唯一）',
  `password_hash` char(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'BCrypt加密后的密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '绑定邮箱（用于找回密码）',
  `storage_limit` bigint NULL DEFAULT 1073741824 COMMENT '存储配额（字节，默认1GB）',
  `used_storage` bigint NULL DEFAULT 0 COMMENT '已用存储空间（动态更新）',
  `user_perm_id` int NOT NULL DEFAULT 1 COMMENT '用户的权限ID（默认为0）',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '账户创建时间',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `avatar_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1000006 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户账户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
