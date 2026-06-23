-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: edu_sys_001
-- ------------------------------------------------------
-- Server version	8.0.36

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
-- Table structure for table `question_knowledge`
--

DROP TABLE IF EXISTS `question_knowledge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question_knowledge` (
  `question_id` int NOT NULL COMMENT '题目id',
  `knowledge` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '知识点id',
  PRIMARY KEY (`question_id`,`knowledge`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question_knowledge`
--

LOCK TABLES `question_knowledge` WRITE;
/*!40000 ALTER TABLE `question_knowledge` DISABLE KEYS */;
INSERT INTO `question_knowledge` VALUES (31,'张量'),(31,'深度学习'),(31,'矩阵运算'),(32,'CSS'),(32,'HTML'),(32,'JavaScript'),(32,'Node.js'),(32,'TypeScript'),(34,'TensorFlow'),(34,'张量'),(34,'矩阵运算'),(36,'兰亭集序'),(37,'TensorFlow'),(37,'计算图'),(39,'TensorFlow'),(39,'张量'),(39,'矩阵运算'),(40,'TensorFlow'),(40,'张量'),(40,'张量运算'),(47,'cc'),(52,'TensorFlow.js'),(52,'张量操作'),(53,'TensorFlow.js架构与优势'),(55,'python'),(55,'函数调用'),(56,'python'),(56,'基本运算'),(57,'python'),(57,'函数调用'),(58,'Softmax与交叉熵损失'),(58,'图像数据预处理（reshape、归一化）'),(59,'python'),(59,'基本运算');
/*!40000 ALTER TABLE `question_knowledge` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `section_knowledge`
--

DROP TABLE IF EXISTS `section_knowledge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `section_knowledge` (
  `section_id` int NOT NULL COMMENT '章节id',
  `knowledge` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '知识点',
  PRIMARY KEY (`section_id`,`knowledge`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `section_knowledge`
--

LOCK TABLES `section_knowledge` WRITE;
/*!40000 ALTER TABLE `section_knowledge` DISABLE KEYS */;
INSERT INTO `section_knowledge` VALUES (1,'知识点1'),(1,'知识点3'),(7,'lllll'),(8,'？？'),(9,'？？？？？？'),(10,'？？？？？'),(12,'？？？？'),(14,'TensorFlow'),(14,'张量'),(14,'深度学习'),(14,'矩阵运算'),(14,'计算图'),(40,'TensorFlow.js架构与优势'),(40,'内存优化'),(40,'变量管理'),(40,'张量操作'),(40,'模型构建方式'),(41,'性能评估'),(41,'数据预处理'),(41,'线性回归模型原理'),(41,'训练监控'),(41,'训练配置'),(42,'CNN原理'),(42,'图像数据预处理'),(42,'多分类任务'),(42,'模型评估'),(42,'混淆矩阵'),(43,'交互优化'),(43,'实时数据采集'),(43,'模型部署'),(43,'迁移学习'),(44,'TensorFlow.js架构与优势'),(44,'内存管理实战：dispose与tf.tidy对比'),(44,'张量创建与运算'),(44,'核心概念：张量操作、变量管理、内存优化'),(44,'模型构建方式：Sequential Model vs. Functional Model'),(44,'环境配置：Script标签引入CDN + Parcel工程化构建'),(45,'数据加载与可视化（tfvis.render.scatterplot）'),(45,'数据预处理：归一化、打乱、张量转换'),(45,'构建全连接网络（tf.layers.dense）'),(45,'线性回归模型原理与结构设计'),(45,'结果反归一化与预测曲线绘制'),(45,'训练监控与性能评估（tfvis.show.fitCallbacks）'),(45,'训练配置：优化器选择（Adam）、损失函数（MSE）'),(46,'CNN原理：卷积层、池化层、全连接层作用'),(46,'MNIST数据集加载与可视化（tf.browser.toPixels）'),(46,'图像数据预处理（reshape、归一化）'),(46,'多分类任务：Softmax与交叉熵损失'),(46,'构建CNN模型（卷积核、滤波器、步长配置）'),(46,'模型评估：混淆矩阵与类别准确率（tfvis.metrics.confusionMatrix）'),(46,'训练过程分析：准确率/损失曲线监控'),(47,'交互优化：可视化预测结果与置信度'),(47,'加载Rock-Paper-Scissors数据集'),(47,'实时数据采集：浏览器摄像头API调用'),(47,'模型部署：浏览器端实时手势预测'),(47,'迁移学习：冻结基础层+定制分类层'),(47,'迁移学习：复用预训练模型（VGG/ResNet）'),(49,'Sequential Model vs. Functional Model'),(49,'TensorFlow.js架构与优势'),(49,'内存优化（tf.tidy）'),(49,'变量管理'),(49,'张量操作'),(50,'优化器选择（Adam）'),(50,'损失函数（MSE）'),(50,'数据预处理（归一化、打乱、张量转换）'),(50,'线性回归模型原理'),(50,'训练监控与性能评估'),(51,'CNN原理（卷积层、池化层、全连接层）'),(51,'MNIST数据集加载与可视化'),(51,'Softmax与交叉熵损失'),(51,'图像数据预处理（reshape、归一化）'),(51,'混淆矩阵与类别准确率'),(52,'Rock-Paper-Scissors数据集加载'),(52,'交互优化与可视化'),(52,'模型部署与实时预测'),(52,'浏览器摄像头API调用'),(52,'迁移学习（复用预训练模型）');
/*!40000 ALTER TABLE `section_knowledge` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_ai_message`
--

DROP TABLE IF EXISTS `t_ai_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_ai_message` (
  `message_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL COMMENT '提问用户ID(可空，支持匿名测试)',
  `question` text COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户问题',
  `answer` text COLLATE utf8mb4_general_ci COMMENT 'AI回答',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`message_id`),
  KEY `idx_ai_user` (`user_id`),
  KEY `idx_ai_user_created` (`user_id`,`created_at`),
  CONSTRAINT `fk_ai_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='AI问答记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_ai_message`
--

LOCK TABLES `t_ai_message` WRITE;
/*!40000 ALTER TABLE `t_ai_message` DISABLE KEYS */;
INSERT INTO `t_ai_message` VALUES (1,2024003,'Python中列表和元组的区别是什么？','列表可变，元组不可变；列表用[]，元组用()。','2026-03-15 09:00:00'),(2,2024003,'什么是面向对象编程？','面向对象编程以对象为核心，包含封装、继承和多态。','2026-03-15 09:10:00'),(3,2024003,'冒泡排序的时间复杂度是多少？','冒泡排序平均和最坏时间复杂度都是O(n^2)。','2026-03-15 09:20:00'),(4,2024003,'SQL中JOIN有哪些类型？','常见有INNER JOIN、LEFT JOIN、RIGHT JOIN和FULL JOIN。','2026-03-15 09:30:00'),(5,2024003,'HTTP和HTTPS有什么不同？','HTTPS在HTTP基础上增加了TLS加密层，更安全。','2026-03-15 09:40:00'),(6,2024004,'HTML中块级元素和行内元素的区别？','块级元素独占一行，行内元素不会换行。','2026-03-15 10:00:00'),(7,2024004,'Vue的双向绑定原理是什么？','Vue通过数据劫持和发布订阅模式实现双向绑定。','2026-03-15 10:10:00'),(8,2024004,'SpringBoot的核心注解有哪些？','@SpringBootApplication、@RestController、@Autowired等。','2026-03-15 10:20:00'),(9,2024004,'进程和线程的区别是什么？','进程是资源分配单位，线程是CPU调度单位。','2026-03-15 10:30:00'),(10,2024004,'什么是索引？为什么能加速查询？','索引通过缩小检索范围来减少全表扫描，提高查询效率。','2026-03-15 10:40:00');
/*!40000 ALTER TABLE `t_ai_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_chapter`
--

DROP TABLE IF EXISTS `t_chapter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_chapter` (
  `chapter_id` int NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `course_id` int NOT NULL COMMENT '所属课程ID',
  `chapter_name` varchar(128) COLLATE utf8mb4_general_ci NOT NULL COMMENT '章节名称',
  `chapter_description` text COLLATE utf8mb4_general_ci COMMENT '章节描述',
  `chapter_order` int NOT NULL DEFAULT '1' COMMENT '章节顺序',
  PRIMARY KEY (`chapter_id`),
  KEY `idx_chapter_course` (`course_id`),
  KEY `idx_chapter_order` (`course_id`,`chapter_order`),
  CONSTRAINT `fk_chapter_course` FOREIGN KEY (`course_id`) REFERENCES `t_course` (`course_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='章节表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_chapter`
--

LOCK TABLES `t_chapter` WRITE;
/*!40000 ALTER TABLE `t_chapter` DISABLE KEYS */;
INSERT INTO `t_chapter` VALUES (1,1,'Python基础语法','学习变量、数据类型和运算符',1),(2,1,'流程控制','学习条件判断和循环',2),(3,2,'Java基础','变量、数据类型与方法',1),(4,3,'算法基础','时间复杂度与排序',1),(5,4,'SQL基础','查询、分组与连接',1),(6,5,'网络协议','HTTP与TCP/IP',1),(7,6,'前端三件套','HTML/CSS/JS基础',1),(8,7,'Vue组件','组件与生命周期',1),(9,8,'SpringBoot快速开始','项目搭建与接口',1),(10,9,'进程线程','并发与调度',1);
/*!40000 ALTER TABLE `t_chapter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_chapter_content`
--

DROP TABLE IF EXISTS `t_chapter_content`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_chapter_content` (
  `content_id` int NOT NULL AUTO_INCREMENT COMMENT '内容ID',
  `chapter_id` int NOT NULL COMMENT '所属章节ID',
  `content_name` varchar(128) COLLATE utf8mb4_general_ci NOT NULL COMMENT '内容名称',
  `content_description` text COLLATE utf8mb4_general_ci COMMENT '内容描述',
  `content_type` enum('video','document','exercise','quiz','assignment') COLLATE utf8mb4_general_ci NOT NULL COMMENT '内容类型',
  `content_order` int NOT NULL DEFAULT '1' COMMENT '内容顺序',
  `video_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '视频地址(视频类型时)',
  `document_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文档地址(文档类型时)',
  PRIMARY KEY (`content_id`),
  KEY `idx_content_chapter` (`chapter_id`),
  KEY `idx_content_order` (`chapter_id`,`content_order`),
  KEY `idx_content_type` (`content_type`),
  CONSTRAINT `fk_content_chapter` FOREIGN KEY (`chapter_id`) REFERENCES `t_chapter` (`chapter_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='章节内容表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_chapter_content`
--

LOCK TABLES `t_chapter_content` WRITE;
/*!40000 ALTER TABLE `t_chapter_content` DISABLE KEYS */;
INSERT INTO `t_chapter_content` VALUES (1,1,'Python简介与环境搭建','了解Python特点并搭建开发环境','video',1,'/uploads/videos/python_intro.mp4',NULL),(2,1,'变量与数据类型讲义','变量、数字、字符串、列表等','document',2,NULL,'/uploads/docs/python_basic.pdf'),(3,2,'流程控制练习','if/for/while 练习题','exercise',1,NULL,NULL),(4,3,'Java基础视频','讲解Java基础语法','video',1,'/uploads/videos/java_intro.mp4',NULL),(5,4,'算法基础视频','讲解复杂度与排序','video',1,'/uploads/videos/algo_intro.mp4',NULL),(6,5,'SQL基础文档','SQL语句示例','document',1,NULL,'/uploads/docs/sql_basic.pdf'),(7,6,'网络协议文档','TCP/IP与HTTP讲义','document',1,NULL,'/uploads/docs/network.pdf'),(8,7,'前端基础视频','HTML/CSS/JS快速入门','video',1,'/uploads/videos/frontend.mp4',NULL),(9,8,'Vue组件文档','组件通信与状态管理','document',1,NULL,'/uploads/docs/vue_comp.pdf'),(10,9,'SpringBoot接口视频','REST接口开发流程','video',1,'/uploads/videos/springboot.mp4',NULL);
/*!40000 ALTER TABLE `t_chapter_content` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_course`
--

DROP TABLE IF EXISTS `t_course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_course` (
  `course_id` int NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `course_name` varchar(128) COLLATE utf8mb4_general_ci NOT NULL COMMENT '课程名称',
  `description` text COLLATE utf8mb4_general_ci COMMENT '课程描述',
  `keywords_json` json DEFAULT NULL COMMENT '关键词JSON数组',
  `teacher_id` int NOT NULL COMMENT '教师用户ID',
  `student_count` int NOT NULL DEFAULT '0' COMMENT '选课人数(冗余字段，便于列表显示)',
  `start_date` date DEFAULT NULL COMMENT '开课日期',
  `weeks` int DEFAULT NULL COMMENT '课程时长(周)',
  `course_type` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '课程类型',
  `difficulty` tinyint DEFAULT NULL COMMENT '难度1-5',
  `score` decimal(3,1) DEFAULT NULL COMMENT '课程评分',
  `cover_image` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '封面相对路径',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`course_id`),
  KEY `idx_course_teacher` (`teacher_id`),
  KEY `idx_course_name` (`course_name`),
  KEY `idx_course_type_difficulty` (`course_type`,`difficulty`),
  KEY `idx_course_start_date` (`start_date`),
  KEY `idx_course_score` (`score`),
  CONSTRAINT `fk_course_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `t_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='课程表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_course`
--

LOCK TABLES `t_course` WRITE;
/*!40000 ALTER TABLE `t_course` DISABLE KEYS */;
INSERT INTO `t_course` VALUES (1,'Python编程入门','零基础学习Python编程，从基础语法到项目实战','[\"Python\", \"编程入门\", \"数据分析\"]',2024001,2,'2026-03-01',16,'编程开发',2,4.8,'/uploads/covers/course1.jpg','2026-06-02 22:22:58','2026-06-09 13:00:59'),(2,'Java程序设计','面向对象基础、集合框架与异常处理','[\"Java\", \"OOP\", \"集合\"]',2024001,1,'2026-03-05',16,'编程开发',2,4.7,'/uploads/covers/course2.jpg','2026-06-02 22:22:58','2026-06-02 22:22:58'),(3,'数据结构与算法','线性表、树、图与排序查找','[\"算法\", \"数据结构\", \"复杂度\"]',2024001,1,'2026-03-06',18,'编程开发',3,4.8,'/uploads/covers/course3.jpg','2026-06-02 22:22:58','2026-06-02 22:22:58'),(4,'数据库系统原理','关系模型、SQL、索引、事务','[\"MySQL\", \"SQL\", \"事务\"]',2024001,1,'2026-03-08',16,'软件工程',3,4.7,'/uploads/covers/course4.jpg','2026-06-02 22:22:58','2026-06-02 22:22:58'),(5,'计算机网络','TCP/IP、HTTP、DNS与网络安全基础','[\"网络\", \"HTTP\", \"TCP\"]',2024001,1,'2026-03-09',14,'软件工程',2,4.5,'/uploads/covers/course5.jpg','2026-06-02 22:22:58','2026-06-02 22:22:58'),(6,'前端开发基础','HTML、CSS、JavaScript开发实战','[\"前端\", \"HTML\", \"JavaScript\"]',2024002,1,'2026-03-10',12,'编程开发',2,4.6,'/uploads/covers/course6.jpg','2026-06-02 22:22:58','2026-06-02 22:22:58'),(7,'Vue应用开发','组件化、路由、状态管理与工程化','[\"Vue\", \"前端工程化\", \"Pinia\"]',2024002,1,'2026-03-11',12,'编程开发',2,4.7,'/uploads/covers/course7.jpg','2026-06-02 22:22:58','2026-06-02 22:22:58'),(8,'SpringBoot实战','REST接口、权限控制、部署运维','[\"SpringBoot\", \"后端\", \"REST\"]',2024002,1,'2026-03-12',16,'编程开发',3,4.8,'/uploads/covers/course8.jpg','2026-06-02 22:22:58','2026-06-02 22:22:58'),(9,'操作系统基础','进程线程、内存管理与文件系统','[\"操作系统\", \"并发\", \"内存\"]',2024002,1,'2026-03-07',14,'软件工程',3,4.6,'/uploads/covers/course9.jpg','2026-06-02 22:22:58','2026-06-02 22:22:58'),(10,'软件测试基础','测试设计、单元测试、接口测试','[\"测试\", \"JUnit\", \"质量保证\"]',2024002,0,'2026-03-13',10,'软件工程',2,4.6,'/uploads/covers/course10.jpg','2026-06-02 22:22:58','2026-06-02 22:22:58');
/*!40000 ALTER TABLE `t_course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_exercise_option`
--

DROP TABLE IF EXISTS `t_exercise_option`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_exercise_option` (
  `option_id` int NOT NULL AUTO_INCREMENT,
  `question_id` int NOT NULL,
  `option_key` varchar(8) COLLATE utf8mb4_general_ci NOT NULL COMMENT '选项标识: A/B/C/D',
  `option_text` varchar(500) COLLATE utf8mb4_general_ci NOT NULL COMMENT '选项内容',
  `is_correct` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否正确',
  PRIMARY KEY (`option_id`),
  UNIQUE KEY `uk_q_option_key` (`question_id`,`option_key`),
  KEY `idx_option_question` (`question_id`),
  KEY `idx_option_question_correct` (`question_id`,`is_correct`),
  CONSTRAINT `fk_option_question` FOREIGN KEY (`question_id`) REFERENCES `t_exercise_question` (`question_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='练习题选项表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_exercise_option`
--

LOCK TABLES `t_exercise_option` WRITE;
/*!40000 ALTER TABLE `t_exercise_option` DISABLE KEYS */;
INSERT INTO `t_exercise_option` VALUES (1,1,'A','2variable',0),(2,1,'B','my-variable',0),(3,1,'C','_myvariable',1),(4,1,'D','my variable',0),(5,2,'A','列表(list)',1),(6,2,'B','元组(tuple)',0),(7,2,'C','字典(dict)',1),(8,2,'D','字符串(string)',0),(9,3,'A','正确',1),(10,3,'B','错误',0),(11,6,'A','class',1),(12,6,'B','object',0),(13,6,'C','new',0),(14,6,'D','public',0),(15,7,'A','O(n)',0),(16,7,'B','O(n^2)',1),(17,7,'C','O(logn)',0),(18,7,'D','O(1)',0),(19,8,'A','DISTINCT',1),(20,8,'B','GROUP',0),(21,8,'C','ORDER',0),(22,8,'D','LIMIT',0),(23,9,'A','443',0),(24,9,'B','80',1),(25,9,'C','3306',0),(26,9,'D','6379',0),(27,10,'A','HTML',1),(28,10,'B','CSS',0),(29,10,'C','SQL',0),(30,10,'D','Docker',0);
/*!40000 ALTER TABLE `t_exercise_option` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_exercise_question`
--

DROP TABLE IF EXISTS `t_exercise_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_exercise_question` (
  `question_id` int NOT NULL AUTO_INCREMENT COMMENT '题目ID',
  `course_id` int NOT NULL COMMENT '课程ID',
  `chapter_id` int DEFAULT NULL COMMENT '章节ID',
  `question_type` enum('single_choice','multiple_choice','true_false','fill_blank','essay') COLLATE utf8mb4_general_ci NOT NULL COMMENT '题型',
  `question` text COLLATE utf8mb4_general_ci NOT NULL COMMENT '题干',
  `answer` text COLLATE utf8mb4_general_ci COMMENT '答案',
  `analysis` text COLLATE utf8mb4_general_ci COMMENT '解析',
  `source_type` enum('local','ai_generated') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'local' COMMENT '本地题库/AI生成',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`question_id`),
  KEY `idx_eq_course_chapter_type` (`course_id`,`chapter_id`,`question_type`),
  KEY `idx_eq_active` (`is_active`),
  KEY `idx_eq_fetch` (`course_id`,`chapter_id`,`question_type`,`is_active`,`created_at`),
  KEY `fk_eq_chapter` (`chapter_id`),
  CONSTRAINT `fk_eq_chapter` FOREIGN KEY (`chapter_id`) REFERENCES `t_chapter` (`chapter_id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_eq_course` FOREIGN KEY (`course_id`) REFERENCES `t_course` (`course_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='练习题表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_exercise_question`
--

LOCK TABLES `t_exercise_question` WRITE;
/*!40000 ALTER TABLE `t_exercise_question` DISABLE KEYS */;
INSERT INTO `t_exercise_question` VALUES (1,1,1,'single_choice','以下哪个是Python合法变量名？','C','变量名可由字母数字下划线组成，不能数字开头。','local',1,'2026-06-02 22:22:58'),(2,1,1,'multiple_choice','以下哪些是Python中的可变数据类型？','A、C','列表和字典是可变对象；元组和字符串不可变。','local',1,'2026-06-02 22:22:58'),(3,1,1,'true_false','Python中的列表使用方括号[]定义。','正确','这是Python列表的标准定义方式。','local',1,'2026-06-02 22:22:58'),(4,1,1,'fill_blank','在Python中，用于输出内容的函数是______。','print()','print是基础输出函数。','local',1,'2026-06-02 22:22:58'),(5,1,1,'essay','请简述Python中列表和元组的区别。','列表可变，元组不可变；语法分别是[]和()。','开放题，按关键点给分。','local',1,'2026-06-02 22:22:58'),(6,2,3,'single_choice','Java中用于定义类的关键字是？','A','class是Java定义类的关键字。','local',1,'2026-06-02 22:22:58'),(7,3,4,'single_choice','冒泡排序平均时间复杂度是？','B','常见实现平均复杂度为O(n^2)。','local',1,'2026-06-02 22:22:58'),(8,4,5,'single_choice','SQL中去重关键字是？','A','DISTINCT用于去重。','local',1,'2026-06-02 22:22:58'),(9,5,6,'single_choice','HTTP默认端口是？','B','HTTP默认端口是80。','local',1,'2026-06-02 22:22:58'),(10,6,7,'single_choice','网页结构层使用哪种语言？','A','HTML负责结构。','local',1,'2026-06-02 22:22:58');
/*!40000 ALTER TABLE `t_exercise_question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_note`
--

DROP TABLE IF EXISTS `t_note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_note` (
  `note_id` int NOT NULL AUTO_INCREMENT,
  `student_id` int NOT NULL,
  `course_id` int NOT NULL,
  `title` varchar(200) COLLATE utf8mb4_general_ci NOT NULL,
  `content` text COLLATE utf8mb4_general_ci NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`note_id`),
  KEY `idx_note_student` (`student_id`),
  KEY `idx_note_student_course` (`student_id`,`course_id`),
  KEY `idx_note_student_updated` (`student_id`,`updated_at`),
  KEY `fk_note_course` (`course_id`),
  CONSTRAINT `fk_note_course` FOREIGN KEY (`course_id`) REFERENCES `t_course` (`course_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_note_student` FOREIGN KEY (`student_id`) REFERENCES `t_user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='学生笔记表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_note`
--

LOCK TABLES `t_note` WRITE;
/*!40000 ALTER TABLE `t_note` DISABLE KEYS */;
INSERT INTO `t_note` VALUES (1,2024003,1,'Python变量命名规则总结','变量名只能包含字母、数字和下划线，且不能以数字开头。','2026-06-02 22:22:58','2026-06-02 22:22:58'),(2,2024003,1,'Python数据类型笔记','整数int、浮点数float、字符串str、列表list、元组tuple、字典dict。','2026-06-02 22:22:58','2026-06-02 22:22:58'),(3,2024003,2,'Java类与对象笔记','类是对象的模板，对象是类的实例。通过new关键字创建对象。','2026-06-02 22:22:58','2026-06-02 22:22:58'),(4,2024003,3,'排序算法比较','冒泡O(n^2)、快排O(nlogn)、归并O(nlogn)、插入O(n^2)。','2026-06-02 22:22:58','2026-06-02 22:22:58'),(5,2024003,4,'SQL基础笔记','SELECT、WHERE、GROUP BY、HAVING、ORDER BY、LIMIT的使用。','2026-06-02 22:22:58','2026-06-02 22:22:58'),(6,2024004,1,'Python学习心得','Python语法简洁，适合初学者入门，动态类型语言。','2026-06-02 22:22:58','2026-06-02 22:22:58'),(7,2024004,6,'HTML标签笔记','常用标签：div、span、p、a、img、ul、li、table等。','2026-06-02 22:22:58','2026-06-02 22:22:58'),(8,2024004,7,'Vue生命周期笔记','created、mounted、updated、destroyed四个主要阶段。','2026-06-02 22:22:58','2026-06-02 22:22:58'),(9,2024004,8,'SpringBoot注解笔记','@Controller处理请求、@Service业务逻辑、@Repository数据访问。','2026-06-02 22:22:58','2026-06-02 22:22:58'),(10,2024004,9,'操作系统进程笔记','进程状态：就绪、运行、阻塞。调度算法：FCFS、SJF、RR。','2026-06-02 22:22:58','2026-06-02 22:22:58');
/*!40000 ALTER TABLE `t_note` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_record_set`
--

DROP TABLE IF EXISTS `t_record_set`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_record_set` (
  `set_record_id` int NOT NULL AUTO_INCREMENT,
  `set_score` int DEFAULT NULL,
  `set_id` int DEFAULT NULL,
  `subsection_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `state` varchar(20) DEFAULT '未完成',
  PRIMARY KEY (`set_record_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_record_set`
--

LOCK TABLES `t_record_set` WRITE;
/*!40000 ALTER TABLE `t_record_set` DISABLE KEYS */;
INSERT INTO `t_record_set` VALUES (4,60,10,47,2024001,'已评分'),(5,NULL,7,50,2024001,'未完成');
/*!40000 ALTER TABLE `t_record_set` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_student_course`
--

DROP TABLE IF EXISTS `t_student_course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_student_course` (
  `id` int NOT NULL AUTO_INCREMENT,
  `student_id` int NOT NULL COMMENT '学生ID',
  `course_id` int NOT NULL COMMENT '课程ID',
  `completed_lessons` int NOT NULL DEFAULT '0' COMMENT '已完成课时',
  `total_lessons` int NOT NULL DEFAULT '0' COMMENT '总课时',
  `homework_completed` int NOT NULL DEFAULT '0' COMMENT '已完成作业',
  `homework_total` int NOT NULL DEFAULT '0' COMMENT '总作业',
  `score` int DEFAULT NULL COMMENT '课程成绩',
  `enrolled_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_course` (`student_id`,`course_id`),
  KEY `idx_sc_course` (`course_id`),
  KEY `idx_sc_student_enrolled` (`student_id`,`enrolled_at`),
  CONSTRAINT `fk_sc_course` FOREIGN KEY (`course_id`) REFERENCES `t_course` (`course_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_sc_student` FOREIGN KEY (`student_id`) REFERENCES `t_user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='学生选课表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_student_course`
--

LOCK TABLES `t_student_course` WRITE;
/*!40000 ALTER TABLE `t_student_course` DISABLE KEYS */;
INSERT INTO `t_student_course` VALUES (1,2024003,1,5,20,2,5,88,'2026-06-02 22:22:58'),(2,2024003,2,3,16,1,6,75,'2026-06-02 22:22:58'),(3,2024003,3,4,18,2,6,82,'2026-06-02 22:22:58'),(4,2024003,4,6,16,3,6,90,'2026-06-02 22:22:58'),(5,2024003,5,2,14,1,5,70,'2026-06-02 22:22:58'),(6,2024004,1,3,20,1,5,78,'2026-06-02 22:22:58'),(7,2024004,6,5,12,2,4,85,'2026-06-02 22:22:58'),(8,2024004,7,4,12,2,4,80,'2026-06-02 22:22:58'),(9,2024004,8,6,16,3,6,92,'2026-06-02 22:22:58'),(10,2024004,9,2,14,1,5,73,'2026-06-02 22:22:58'),(15,2024006,1,0,0,0,0,NULL,'2026-06-09 14:49:18');
/*!40000 ALTER TABLE `t_student_course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user`
--

DROP TABLE IF EXISTS `t_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user` (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `account` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录账号',
  `password` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码(建议存加密串)',
  `username` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `role` enum('Student','Teacher','Admin') COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户角色',
  `avatar` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '头像相对路径',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1正常 0禁用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_user_account` (`account`),
  KEY `idx_user_role_status` (`role`,`status`),
  KEY `idx_user_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=2024009 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user`
--

LOCK TABLES `t_user` WRITE;
/*!40000 ALTER TABLE `t_user` DISABLE KEYS */;
INSERT INTO `t_user` VALUES (2024000,'admin001','$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6','管理员','Admin','/uploads/avatars/admin.jpg',1,'2026-06-02 22:22:58','2026-06-02 22:22:58'),(2024001,'teacher001','$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6','王老师','Teacher','/uploads/avatars/teacher1.jpg',1,'2026-06-02 22:22:58','2026-06-02 22:22:58'),(2024002,'teacher002','$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6','李老师','Teacher','/uploads/avatars/teacher2.jpg',1,'2026-06-02 22:22:58','2026-06-02 22:22:58'),(2024003,'student001','$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6','张同学','Student','/uploads/avatars/student1.jpg',1,'2026-06-02 22:22:58','2026-06-02 22:22:58'),(2024004,'student002','$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6','赵同学','Student','/uploads/avatars/student2.jpg',1,'2026-06-02 22:22:58','2026-06-02 22:22:58'),(2024005,'testuser01','$2a$10$NP2vBxj1NZZclm2aWHg.6uh5JJ3VmPkRO/myIOZWGUhvGsPY6Hpiy','Test User','Student',NULL,1,'2026-06-02 22:59:36','2026-06-02 22:59:36'),(2024006,'zbotest99','$2a$10$JGhfME9nhOm3OqbpVAOz5eWntzQxJGWnzkBmWAeseKhwWoPC9YwLq','Test','Student',NULL,1,'2026-06-09 14:47:06','2026-06-09 14:47:06'),(2024007,'zzztest888','$2a$10$CYy6V6IuMk4QLarvzXrQ0ugtzi/mBvCINgHbPpHSQ3vgFd9WDvEwy','Test8','Student',NULL,1,'2026-06-09 15:26:28','2026-06-09 15:26:28'),(2024008,'xftest555','$2a$10$qaeXqOrkihmppiMDnAWmmextUE/8Ucur0VtmkVDhG.gmazAa/ixT2','XFT','Student',NULL,1,'2026-06-09 15:26:48','2026-06-09 15:26:48');
/*!40000 ALTER TABLE `t_user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-09 15:49:24
