-- MySQL dump 10.13  Distrib 8.0.29, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: katool_security
-- ------------------------------------------------------
-- Server version	8.0.29

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ka_security_auth`
--

DROP TABLE IF EXISTS `ka_security_auth`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ka_security_auth` (
                                    `id` varchar(36) DEFAULT NULL,
                                    `service_name` varchar(50) DEFAULT NULL,
                                    `method` varchar(6) DEFAULT NULL,
                                    `route` varchar(50) DEFAULT NULL,
                                    `oper_user` varchar(36) DEFAULT NULL,
                                    `uri` varchar(128) DEFAULT NULL,
                                    `auth_role` varchar(256) DEFAULT NULL,
                                    `is_delete` tinyint NOT NULL DEFAULT '0',
                                    `is_def` tinyint NOT NULL DEFAULT '0',
                                    `is_open` tinyint NOT NULL DEFAULT '0',
                                    `only_check_login` tinyint DEFAULT NULL,
                                    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ka_security_auth`
--

LOCK TABLES `ka_security_auth` WRITE;
/*!40000 ALTER TABLE `ka_security_auth` DISABLE KEYS */;
INSERT INTO `ka_security_auth` VALUES ('1740031215756980225',NULL,'GET','/api',NULL,'/api/checklogin','[]',0,1,0,1,'2024-01-02 16:47:05','2024-02-05 00:39:54'),('1743621950804713473',NULL,'GET','1123123','123123','123123','[\"123\"]',0,1,0,NULL,'2024-01-06 21:14:02','2024-02-05 00:39:54');
/*!40000 ALTER TABLE `ka_security_auth` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ka_security_login_log`
--

DROP TABLE IF EXISTS `ka_security_login_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ka_security_login_log` (
                                         `id` int NOT NULL AUTO_INCREMENT,
                                         `ip` varchar(50) DEFAULT NULL,
                                         `userName` varchar(50) DEFAULT NULL,
                                         `loginTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                         PRIMARY KEY (`id`),
                                         UNIQUE KEY `id` (`id`),
                                         UNIQUE KEY `id_2` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=151 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ka_security_login_log`
--

LOCK TABLES `ka_security_login_log` WRITE;
/*!40000 ALTER TABLE `ka_security_login_log` DISABLE KEYS */;
INSERT INTO `ka_security_login_log` VALUES (1,'127.0.0.1:admin','admin','2024-01-05 03:48:06'),(2,'127.0.0.1:admin','admin','2024-01-05 03:49:49'),(3,'127.0.0.1:admin','admin','2024-01-05 04:41:38'),(4,'127.0.0.1:admin','admin','2024-01-05 04:45:14'),(5,'127.0.0.1:admin','admin','2024-01-05 04:46:06'),(6,'127.0.0.1','admin','2024-01-05 04:48:01'),(7,'127.0.0.1','admin','2024-01-05 04:48:58'),(8,'127.0.0.1','admin','2024-01-05 04:49:34'),(9,'127.0.0.1','admin','2024-01-05 04:50:17'),(10,'127.0.0.1','admin','2024-01-05 04:51:35'),(11,'127.0.0.1','admin','2024-01-05 04:52:27'),(12,'127.0.0.1','admin','2024-01-05 05:03:52'),(13,'127.0.0.1','admin','2024-01-05 05:06:36'),(14,'127.0.0.1','admin','2024-01-05 05:10:02'),(15,'127.0.0.1','admin','2024-01-05 05:12:46'),(16,'127.0.0.1','admin','2024-01-05 05:18:28'),(17,'127.0.0.1','admin','2024-01-05 05:38:14'),(18,'127.0.0.1','admin','2024-01-05 05:41:33'),(19,'127.0.0.1','admin','2024-01-05 05:46:36'),(20,'127.0.0.1','admin','2024-01-05 08:12:46'),(21,'127.0.0.1','admin','2024-01-05 08:36:44'),(22,'127.0.0.1','admin','2024-01-05 11:37:04'),(23,'127.0.0.1','admin','2024-01-05 11:57:13'),(24,'127.0.0.1','admin','2024-01-05 12:58:22'),(25,'127.0.0.1','admin','2024-01-05 13:08:56'),(26,'127.0.0.1','admin','2024-01-05 13:11:02'),(27,'127.0.0.1','admin','2024-01-05 13:11:18'),(28,'127.0.0.1','admin','2024-01-05 13:12:17'),(29,'127.0.0.1','admin','2024-01-05 13:16:26'),(30,'127.0.0.1','admin','2024-01-05 13:19:23'),(31,'127.0.0.1','admin','2024-01-05 13:19:48'),(32,'127.0.0.1','admin','2024-01-05 13:21:08'),(33,'127.0.0.1','admin','2024-01-05 13:23:08'),(34,'127.0.0.1','admin','2024-01-05 13:23:38'),(35,'127.0.0.1','admin','2024-01-05 13:25:17'),(36,'127.0.0.1','admin','2024-01-05 13:26:20'),(37,'127.0.0.1','admin','2024-01-05 13:26:47'),(38,'127.0.0.1','admin','2024-01-05 13:29:53'),(39,'127.0.0.1','admin','2024-01-05 13:30:28'),(40,'127.0.0.1','admin','2024-01-05 13:33:15'),(41,'127.0.0.1','admin','2024-01-05 13:35:05'),(42,'127.0.0.1','admin','2024-01-05 13:35:33'),(43,'127.0.0.1','admin','2024-01-05 13:39:59'),(44,'127.0.0.1','admin','2024-01-05 13:40:24'),(45,'127.0.0.1','admin','2024-01-05 13:41:31'),(46,'127.0.0.1','admin','2024-01-05 13:43:37'),(47,'127.0.0.1','admin','2024-01-05 13:44:28'),(48,'127.0.0.1','admin','2024-01-05 13:45:59'),(49,'127.0.0.1','admin','2024-01-05 13:46:23'),(50,'127.0.0.1','admin','2024-01-05 13:47:48'),(51,'127.0.0.1','admin','2024-01-05 13:49:16'),(52,'127.0.0.1','admin','2024-01-05 13:51:32'),(53,'127.0.0.1','admin','2024-01-05 13:52:07'),(54,'127.0.0.1','admin','2024-01-05 13:53:21'),(55,'127.0.0.1','admin','2024-01-05 13:55:20'),(56,'127.0.0.1','admin','2024-01-05 13:59:21'),(57,'127.0.0.1','admin','2024-01-05 14:01:12'),(58,'127.0.0.1','admin','2024-01-05 14:01:51'),(59,'127.0.0.1','admin','2024-01-05 14:04:27'),(60,'127.0.0.1','admin','2024-01-05 14:05:11'),(61,'127.0.0.1','admin','2024-01-05 14:06:04'),(62,'127.0.0.1','admin','2024-01-05 14:08:06'),(63,'127.0.0.1','admin','2024-01-05 14:10:04'),(64,'127.0.0.1','admin','2024-01-05 14:10:50'),(65,'127.0.0.1','admin','2024-01-05 14:13:03'),(66,'127.0.0.1','admin','2024-01-05 14:13:53'),(67,'127.0.0.1','admin','2024-01-05 14:21:19'),(68,'127.0.0.1','admin','2024-01-05 15:23:17'),(69,'127.0.0.1','admin','2024-01-05 15:27:35'),(70,'127.0.0.1','admin','2024-01-06 09:03:52'),(71,'127.0.0.1','admin','2024-01-06 09:40:56'),(72,'127.0.0.1','admin','2024-01-06 09:43:16'),(73,'127.0.0.1','admin','2024-01-06 09:48:01'),(74,'127.0.0.1','admin','2024-01-06 10:11:43'),(75,'127.0.0.1','admin','2024-01-06 10:40:35'),(76,'127.0.0.1','admin','2024-01-06 10:41:55'),(77,'127.0.0.1','admin','2024-01-06 10:44:08'),(78,'127.0.0.1','admin','2024-01-06 10:45:14'),(79,'0:0:0:0:0:0:0:1','admin','2024-01-06 11:17:13'),(80,'127.0.0.1','admin','2024-01-06 11:37:43'),(81,'127.0.0.1','admin','2024-01-06 12:35:44'),(82,'127.0.0.1','admin','2024-01-06 12:40:49'),(83,'127.0.0.1','admin','2024-01-06 13:04:22'),(84,'127.0.0.1','admin','2024-01-06 13:13:44'),(85,'127.0.0.1','admin','2024-01-06 13:14:11'),(86,'127.0.0.1','admin','2024-01-06 13:14:32'),(87,'127.0.0.1','admin','2024-01-06 13:19:23'),(88,'127.0.0.1','admin','2024-02-04 08:02:40'),(89,'127.0.0.1','admin','2024-02-04 08:04:05'),(90,'127.0.0.1','admin','2024-02-04 08:27:03'),(91,'127.0.0.1','admin','2024-02-04 08:27:45'),(92,'127.0.0.1','admin','2024-02-04 08:41:35'),(93,'127.0.0.1','admin','2024-02-04 09:01:37'),(94,'127.0.0.1','admin','2024-02-04 09:01:46'),(95,'127.0.0.1','admin','2024-02-04 09:05:24'),(96,'127.0.0.1','admin','2024-02-04 09:11:42'),(97,'127.0.0.1','admin','2024-02-04 09:12:05'),(98,'127.0.0.1','admin','2024-02-04 09:23:48'),(99,'127.0.0.1','admin','2024-02-04 09:29:00'),(100,'127.0.0.1','admin','2024-02-04 09:29:22'),(101,'127.0.0.1','测试','2024-02-04 09:29:35'),(102,'127.0.0.1','测试','2024-02-04 09:29:39'),(103,'127.0.0.1','admin','2024-02-04 09:30:47'),(104,'127.0.0.1','测试','2024-02-04 09:30:58'),(105,'127.0.0.1','admin','2024-02-04 09:31:14'),(106,'127.0.0.1','测试','2024-02-04 09:31:28'),(107,'127.0.0.1','admin','2024-02-04 09:44:36'),(108,'127.0.0.1','admin','2024-02-04 09:46:41'),(109,'127.0.0.1','admin','2024-02-04 09:48:03'),(110,'127.0.0.1','admin','2024-02-04 09:54:09'),(111,'127.0.0.1','admin','2024-02-04 10:09:48'),(112,'127.0.0.1','admin','2024-02-04 10:11:13'),(113,'127.0.0.1','admin','2024-02-04 12:33:37'),(114,'127.0.0.1','admin','2024-02-04 12:42:17'),(115,'127.0.0.1','测试','2024-02-04 12:42:30'),(116,'127.0.0.1','admin','2024-02-04 12:46:34'),(117,'127.0.0.1','admin','2024-02-04 12:49:25'),(118,'127.0.0.1','admin','2024-02-04 12:49:32'),(119,'127.0.0.1','admin','2024-02-04 12:53:09'),(120,'127.0.0.1','测试','2024-02-04 12:53:22'),(121,'127.0.0.1','admin','2024-02-04 12:53:31'),(122,'127.0.0.1','admin','2024-02-04 12:54:38'),(123,'127.0.0.1','admin','2024-02-04 12:55:11'),(124,'127.0.0.1','admin','2024-02-04 12:55:25'),(125,'127.0.0.1','测试','2024-02-04 12:56:21'),(126,'127.0.0.1','admin','2024-02-04 13:03:13'),(127,'127.0.0.1','测试','2024-02-04 13:03:46'),(128,'127.0.0.1','admin','2024-02-04 13:07:33'),(129,'127.0.0.1','admin','2024-02-04 13:07:43'),(130,'127.0.0.1','admin','2024-02-04 13:08:18'),(131,'127.0.0.1','测试','2024-02-04 13:08:31'),(132,'127.0.0.1','admin','2024-02-04 16:02:02'),(133,'127.0.0.1','admin','2024-02-04 16:06:41'),(134,'127.0.0.1','admin','2024-02-04 16:07:09'),(135,'127.0.0.1','admin','2024-02-04 16:07:26'),(136,'127.0.0.1','admin','2024-02-04 16:11:37'),(137,'127.0.0.1','admin','2024-02-04 16:17:34'),(138,'127.0.0.1','admin','2024-02-04 16:29:52'),(139,'127.0.0.1','admin','2024-02-04 16:30:06'),(140,'127.0.0.1','测试','2024-02-04 16:30:24'),(141,'127.0.0.1','admin','2024-02-04 16:30:39'),(142,'127.0.0.1','admin','2024-02-04 16:32:57'),(143,'127.0.0.1','admin','2024-02-04 16:38:02'),(144,'127.0.0.1','admin','2024-02-04 16:42:15'),(145,'127.0.0.1','测试','2024-02-04 16:45:44'),(146,'127.0.0.1','admin','2024-02-04 16:50:25'),(147,'127.0.0.1','admin','2024-02-04 16:50:56'),(148,'127.0.0.1','admin','2024-02-04 17:08:00'),(149,'127.0.0.1','admin','2024-02-04 17:28:48'),(150,'127.0.0.1','admin','2024-02-04 17:30:36');
/*!40000 ALTER TABLE `ka_security_login_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ka_security_role`
--

DROP TABLE IF EXISTS `ka_security_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ka_security_role` (
                                    `id` int NOT NULL AUTO_INCREMENT,
                                    `f_id` int NOT NULL DEFAULT '-1',
                                    `user_role` varchar(256) DEFAULT NULL,
                                    `is_delete` int NOT NULL DEFAULT '0',
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ka_security_role`
--

LOCK TABLES `ka_security_role` WRITE;
/*!40000 ALTER TABLE `ka_security_role` DISABLE KEYS */;
INSERT INTO `ka_security_role` VALUES (0,0,'object',0),(1,0,'root',0),(2,0,'admin',0),(3,0,'test',0),(4,0,'user',0),(5,0,'dev',0);
/*!40000 ALTER TABLE `ka_security_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ka_security_site`
--

DROP TABLE IF EXISTS `ka_security_site`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ka_security_site` (
                                    `attribute` varchar(45) DEFAULT NULL,
                                    `value` varchar(256) DEFAULT NULL,
                                    `comment` varchar(256) DEFAULT NULL,
                                    `type` tinyint DEFAULT NULL,
                                    `createdTime` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ka_security_site`
--

LOCK TABLES `ka_security_site` WRITE;
/*!40000 ALTER TABLE `ka_security_site` DISABLE KEYS */;
INSERT INTO `ka_security_site` VALUES ('icpId','','如果您的站点有ICP备案，请在这里填写您的ICP备案号',0,'2023-01-18 12:45:12'),('navVedio','','这里填写引导页视频地址，您也可以选择在上方直接上传，届时会自动填写地址',1,'2023-01-18 12:45:13'),('siteLogo','https://picdl.sunbangyan.cn/2024/01/06/de3744757984a7ca1dbd7dd1b47a9bde.jpeg\n','站点Logo',1,'2023-01-18 12:45:11'),('siteName','KaSecurity-Auth-Center中台管理','这里填写你站点的名称',0,'2023-01-18 12:45:10');
/*!40000 ALTER TABLE `ka_security_site` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ka_security_user`
--

DROP TABLE IF EXISTS `ka_security_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ka_security_user` (
                                    `id` int NOT NULL,
                                    `user_name` varchar(20) NOT NULL,
                                    `pass_word` varchar(128) NOT NULL,
                                    `user_role` varchar(20) NOT NULL DEFAULT 'admin',
                                    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    `is_delete` int DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ka_security_user`
--

LOCK TABLES `ka_security_user` WRITE;
/*!40000 ALTER TABLE `ka_security_user` DISABLE KEYS */;
INSERT INTO `ka_security_user` VALUES (1,'admin','c0a40b5d61923399b0972096b121c47d','admin','2023-12-29 18:15:38','2023-12-29 18:13:15',0),(2110349313,'123','8fa4d70da01645b169207a3321bbed3e','admin','2024-01-06 10:10:32','2024-01-06 09:59:32',1),(-750141439,'测试','13d73c01294dc35dc26240e7f971b69f','dev','2024-02-04 18:24:44','2024-01-06 10:11:54',0);
/*!40000 ALTER TABLE `ka_security_user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-02-05 11:08:43
