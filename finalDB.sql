/*
  finalDB.sql
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =========================
-- 数据库
-- =========================
DROP DATABASE IF EXISTS `edu_sys_001`;
CREATE DATABASE `edu_sys_001` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `edu_sys_001`;

-- =========================
-- 1. 用户表（注册 / 登录 / 用户信息）
-- 接口：
--   POST /users/register
--   POST /users/login
--   GET  /users/info?user_id=
-- =========================
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `user_id` INT(12) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `account` VARCHAR(64) NOT NULL COMMENT '登录账号',
  `password` VARCHAR(255) NOT NULL COMMENT '密码(建议存加密串)',
  `username` VARCHAR(64) NOT NULL COMMENT '用户名',
  `role` ENUM('Student','Teacher','Admin') NOT NULL COMMENT '用户角色',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像相对路径',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '1正常 0禁用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_user_account` (`account`),
  KEY `idx_user_role_status` (`role`, `status`),
  KEY `idx_user_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='用户表';

-- =========================
-- 2. 课程表（课程列表 / 课程详情）
-- 接口：
--   GET /courses/all
--   GET /courses/detail?course_id=
-- =========================
DROP TABLE IF EXISTS `t_course`;
CREATE TABLE `t_course` (
  `course_id` INT(12) NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `course_name` VARCHAR(128) NOT NULL COMMENT '课程名称',
  `description` TEXT COMMENT '课程描述',
  `keywords_json` JSON DEFAULT NULL COMMENT '关键词JSON数组',
  `teacher_id` INT(12) NOT NULL COMMENT '教师用户ID',
  `student_count` INT(12) NOT NULL DEFAULT 0 COMMENT '选课人数(冗余字段，便于列表显示)',
  `start_date` DATE DEFAULT NULL COMMENT '开课日期',
  `weeks` INT(4) DEFAULT NULL COMMENT '课程时长(周)',
  `course_type` VARCHAR(64) DEFAULT NULL COMMENT '课程类型',
  `difficulty` TINYINT(2) DEFAULT NULL COMMENT '难度1-5',
  `score` DECIMAL(3,1) DEFAULT NULL COMMENT '课程评分',
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面相对路径',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`course_id`),
  KEY `idx_course_teacher` (`teacher_id`),
  KEY `idx_course_name` (`course_name`),
  KEY `idx_course_type_difficulty` (`course_type`, `difficulty`),
  KEY `idx_course_start_date` (`start_date`),
  KEY `idx_course_score` (`score`),
  CONSTRAINT `fk_course_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `t_user`(`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='课程表';

-- =========================
-- 3. 学生选课表（我的课程 / 加入课程 / 退课）
-- 接口：
--   GET    /students/my-courses
--   POST   /students/enroll
--   DELETE /students/drop
-- =========================
DROP TABLE IF EXISTS `t_student_course`;
CREATE TABLE `t_student_course` (
  `id` INT(12) NOT NULL AUTO_INCREMENT,
  `student_id` INT(12) NOT NULL COMMENT '学生ID',
  `course_id` INT(12) NOT NULL COMMENT '课程ID',
  `completed_lessons` INT(12) NOT NULL DEFAULT 0 COMMENT '已完成课时',
  `total_lessons` INT(12) NOT NULL DEFAULT 0 COMMENT '总课时',
  `homework_completed` INT(12) NOT NULL DEFAULT 0 COMMENT '已完成作业',
  `homework_total` INT(12) NOT NULL DEFAULT 0 COMMENT '总作业',
  `score` INT(4) DEFAULT NULL COMMENT '课程成绩',
  `enrolled_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_course` (`student_id`, `course_id`),
  KEY `idx_sc_course` (`course_id`),
  KEY `idx_sc_student_enrolled` (`student_id`, `enrolled_at`),
  CONSTRAINT `fk_sc_student` FOREIGN KEY (`student_id`) REFERENCES `t_user`(`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_sc_course` FOREIGN KEY (`course_id`) REFERENCES `t_course`(`course_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='学生选课表';

-- =========================
-- 4. 章节表（课程详情）
-- 接口字段：chapters[].chapter_id/chapter_name/chapter_description/chapter_order
-- =========================
DROP TABLE IF EXISTS `t_chapter`;
CREATE TABLE `t_chapter` (
  `chapter_id` INT(12) NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `course_id` INT(12) NOT NULL COMMENT '所属课程ID',
  `chapter_name` VARCHAR(128) NOT NULL COMMENT '章节名称',
  `chapter_description` TEXT COMMENT '章节描述',
  `chapter_order` INT(8) NOT NULL DEFAULT 1 COMMENT '章节顺序',
  PRIMARY KEY (`chapter_id`),
  KEY `idx_chapter_course` (`course_id`),
  KEY `idx_chapter_order` (`course_id`, `chapter_order`),
  CONSTRAINT `fk_chapter_course` FOREIGN KEY (`course_id`) REFERENCES `t_course`(`course_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='章节表';

-- =========================
-- 5. 章节内容表（课程详情）
-- 接口字段：contents[].content_id/content_name/content_description/content_type/content_order/video_url/document_url
-- =========================
DROP TABLE IF EXISTS `t_chapter_content`;
CREATE TABLE `t_chapter_content` (
  `content_id` INT(12) NOT NULL AUTO_INCREMENT COMMENT '内容ID',
  `chapter_id` INT(12) NOT NULL COMMENT '所属章节ID',
  `content_name` VARCHAR(128) NOT NULL COMMENT '内容名称',
  `content_description` TEXT COMMENT '内容描述',
  `content_type` ENUM('video','document','exercise','quiz','assignment') NOT NULL COMMENT '内容类型',
  `content_order` INT(8) NOT NULL DEFAULT 1 COMMENT '内容顺序',
  `video_url` VARCHAR(255) DEFAULT NULL COMMENT '视频地址(视频类型时)',
  `document_url` VARCHAR(255) DEFAULT NULL COMMENT '文档地址(文档类型时)',
  PRIMARY KEY (`content_id`),
  KEY `idx_content_chapter` (`chapter_id`),
  KEY `idx_content_order` (`chapter_id`, `content_order`),
  KEY `idx_content_type` (`content_type`),
  CONSTRAINT `fk_content_chapter` FOREIGN KEY (`chapter_id`) REFERENCES `t_chapter`(`chapter_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='章节内容表';

-- =========================
-- 6. AI问答记录表（学习助手）
-- 接口：POST /ai/assistant
-- =========================
DROP TABLE IF EXISTS `t_ai_message`;
CREATE TABLE `t_ai_message` (
  `message_id` INT(12) NOT NULL AUTO_INCREMENT,
  `user_id` INT(12) DEFAULT NULL COMMENT '提问用户ID(可空，支持匿名测试)',
  `question` TEXT NOT NULL COMMENT '用户问题',
  `answer` TEXT COMMENT 'AI回答',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`message_id`),
  KEY `idx_ai_user` (`user_id`),
  KEY `idx_ai_user_created` (`user_id`, `created_at`),
  CONSTRAINT `fk_ai_user` FOREIGN KEY (`user_id`) REFERENCES `t_user`(`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='AI问答记录';

-- =========================
-- 7. 练习题表（获取练习题）
-- 接口：POST /exercises/get
-- =========================
DROP TABLE IF EXISTS `t_exercise_question`;
CREATE TABLE `t_exercise_question` (
  `question_id` INT(12) NOT NULL AUTO_INCREMENT COMMENT '题目ID',
  `course_id` INT(12) NOT NULL COMMENT '课程ID',
  `chapter_id` INT(12) DEFAULT NULL COMMENT '章节ID',
  `question_type` ENUM('single_choice','multiple_choice','true_false','fill_blank','essay') NOT NULL COMMENT '题型',
  `question` TEXT NOT NULL COMMENT '题干',
  `answer` TEXT COMMENT '答案',
  `analysis` TEXT COMMENT '解析',
  `source_type` ENUM('local','ai_generated') NOT NULL DEFAULT 'local' COMMENT '本地题库/AI生成',
  `is_active` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`question_id`),
  KEY `idx_eq_course_chapter_type` (`course_id`, `chapter_id`, `question_type`),
  KEY `idx_eq_active` (`is_active`),
  KEY `idx_eq_fetch` (`course_id`, `chapter_id`, `question_type`, `is_active`, `created_at`),
  CONSTRAINT `fk_eq_course` FOREIGN KEY (`course_id`) REFERENCES `t_course`(`course_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_eq_chapter` FOREIGN KEY (`chapter_id`) REFERENCES `t_chapter`(`chapter_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='练习题表';

-- 选择题选项（单选/多选/判断）
DROP TABLE IF EXISTS `t_exercise_option`;
CREATE TABLE `t_exercise_option` (
  `option_id` INT(12) NOT NULL AUTO_INCREMENT,
  `question_id` INT(12) NOT NULL,
  `option_key` VARCHAR(8) NOT NULL COMMENT '选项标识: A/B/C/D',
  `option_text` VARCHAR(500) NOT NULL COMMENT '选项内容',
  `is_correct` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否正确',
  PRIMARY KEY (`option_id`),
  UNIQUE KEY `uk_q_option_key` (`question_id`, `option_key`),
  KEY `idx_option_question` (`question_id`),
  KEY `idx_option_question_correct` (`question_id`, `is_correct`),
  CONSTRAINT `fk_option_question` FOREIGN KEY (`question_id`) REFERENCES `t_exercise_question`(`question_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='练习题选项表';

-- =========================
-- 8. 笔记表（创建/列表/更新/删除）
-- 接口：
--   POST   /notes/create
--   GET    /notes/list
--   PUT    /notes/update
--   DELETE /notes/delete
-- =========================
DROP TABLE IF EXISTS `t_note`;
CREATE TABLE `t_note` (
  `note_id` INT(12) NOT NULL AUTO_INCREMENT,
  `student_id` INT(12) NOT NULL,
  `course_id` INT(12) NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `content` TEXT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`note_id`),
  KEY `idx_note_student` (`student_id`),
  KEY `idx_note_student_course` (`student_id`, `course_id`),
  KEY `idx_note_student_updated` (`student_id`, `updated_at`),
  CONSTRAINT `fk_note_student` FOREIGN KEY (`student_id`) REFERENCES `t_user`(`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_note_course` FOREIGN KEY (`course_id`) REFERENCES `t_course`(`course_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='学生笔记表';

-- =========================
-- 9. 示例数据（可直接用于联调）
-- 用户：1管理员 + 2教师 + 2学生
-- =========================

-- ---- 用户 (5) ----
INSERT INTO `t_user` (`user_id`,`account`,`password`,`username`,`role`,`avatar`) VALUES
(2024000,'admin001','$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6','管理员','Admin','/uploads/avatars/admin.jpg'),
(2024001,'teacher001','$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6','王老师','Teacher','/uploads/avatars/teacher1.jpg'),
(2024002,'teacher002','$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6','李老师','Teacher','/uploads/avatars/teacher2.jpg'),
(2024003,'student001','$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6','张同学','Student','/uploads/avatars/student1.jpg'),
(2024004,'student002','$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6','赵同学','Student','/uploads/avatars/student2.jpg');

-- ---- 课程 (10)：课程1-5由王老师，课程6-10由李老师 ----
INSERT INTO `t_course` (`course_id`,`course_name`,`description`,`keywords_json`,`teacher_id`,`student_count`,`start_date`,`weeks`,`course_type`,`difficulty`,`score`,`cover_image`) VALUES
(1,'Python编程入门','零基础学习Python编程，从基础语法到项目实战',JSON_ARRAY('Python','编程入门','数据分析'),2024001,0,'2026-03-01',16,'编程开发',2,4.8,'/uploads/covers/course1.jpg'),
(2,'Java程序设计','面向对象基础、集合框架与异常处理',JSON_ARRAY('Java','OOP','集合'),2024001,0,'2026-03-05',16,'编程开发',2,4.7,'/uploads/covers/course2.jpg'),
(3,'数据结构与算法','线性表、树、图与排序查找',JSON_ARRAY('算法','数据结构','复杂度'),2024001,0,'2026-03-06',18,'编程开发',3,4.8,'/uploads/covers/course3.jpg'),
(4,'数据库系统原理','关系模型、SQL、索引、事务',JSON_ARRAY('MySQL','SQL','事务'),2024001,0,'2026-03-08',16,'软件工程',3,4.7,'/uploads/covers/course4.jpg'),
(5,'计算机网络','TCP/IP、HTTP、DNS与网络安全基础',JSON_ARRAY('网络','HTTP','TCP'),2024001,0,'2026-03-09',14,'软件工程',2,4.5,'/uploads/covers/course5.jpg'),
(6,'前端开发基础','HTML、CSS、JavaScript开发实战',JSON_ARRAY('前端','HTML','JavaScript'),2024002,0,'2026-03-10',12,'编程开发',2,4.6,'/uploads/covers/course6.jpg'),
(7,'Vue应用开发','组件化、路由、状态管理与工程化',JSON_ARRAY('Vue','前端工程化','Pinia'),2024002,0,'2026-03-11',12,'编程开发',2,4.7,'/uploads/covers/course7.jpg'),
(8,'SpringBoot实战','REST接口、权限控制、部署运维',JSON_ARRAY('SpringBoot','后端','REST'),2024002,0,'2026-03-12',16,'编程开发',3,4.8,'/uploads/covers/course8.jpg'),
(9,'操作系统基础','进程线程、内存管理与文件系统',JSON_ARRAY('操作系统','并发','内存'),2024002,0,'2026-03-07',14,'软件工程',3,4.6,'/uploads/covers/course9.jpg'),
(10,'软件测试基础','测试设计、单元测试、接口测试',JSON_ARRAY('测试','JUnit','质量保证'),2024002,0,'2026-03-13',10,'软件工程',2,4.6,'/uploads/covers/course10.jpg');

-- ---- 章节 (10)：每门课1个章节 ----
INSERT INTO `t_chapter` (`chapter_id`,`course_id`,`chapter_name`,`chapter_description`,`chapter_order`) VALUES
(1,1,'Python基础语法','学习变量、数据类型和运算符',1),
(2,1,'流程控制','学习条件判断和循环',2),
(3,2,'Java基础','变量、数据类型与方法',1),
(4,3,'算法基础','时间复杂度与排序',1),
(5,4,'SQL基础','查询、分组与连接',1),
(6,5,'网络协议','HTTP与TCP/IP',1),
(7,6,'前端三件套','HTML/CSS/JS基础',1),
(8,7,'Vue组件','组件与生命周期',1),
(9,8,'SpringBoot快速开始','项目搭建与接口',1),
(10,9,'进程线程','并发与调度',1);

-- ---- 章节内容 (10) ----
INSERT INTO `t_chapter_content` (`content_id`,`chapter_id`,`content_name`,`content_description`,`content_type`,`content_order`,`video_url`,`document_url`) VALUES
(1,1,'Python简介与环境搭建','了解Python特点并搭建开发环境','video',1,'/uploads/videos/python_intro.mp4',NULL),
(2,1,'变量与数据类型讲义','变量、数字、字符串、列表等','document',2,NULL,'/uploads/docs/python_basic.pdf'),
(3,2,'流程控制练习','if/for/while 练习题','exercise',1,NULL,NULL),
(4,3,'Java基础视频','讲解Java基础语法','video',1,'/uploads/videos/java_intro.mp4',NULL),
(5,4,'算法基础视频','讲解复杂度与排序','video',1,'/uploads/videos/algo_intro.mp4',NULL),
(6,5,'SQL基础文档','SQL语句示例','document',1,NULL,'/uploads/docs/sql_basic.pdf'),
(7,6,'网络协议文档','TCP/IP与HTTP讲义','document',1,NULL,'/uploads/docs/network.pdf'),
(8,7,'前端基础视频','HTML/CSS/JS快速入门','video',1,'/uploads/videos/frontend.mp4',NULL),
(9,8,'Vue组件文档','组件通信与状态管理','document',1,NULL,'/uploads/docs/vue_comp.pdf'),
(10,9,'SpringBoot接口视频','REST接口开发流程','video',1,'/uploads/videos/springboot.mp4',NULL);

-- ---- 学生选课 (10)：张同学选课1-5，赵同学选课1,6-9 ----
INSERT INTO `t_student_course` (`student_id`,`course_id`,`completed_lessons`,`total_lessons`,`homework_completed`,`homework_total`,`score`) VALUES
(2024003,1,5,20,2,5,88),
(2024003,2,3,16,1,6,75),
(2024003,3,4,18,2,6,82),
(2024003,4,6,16,3,6,90),
(2024003,5,2,14,1,5,70),
(2024004,1,3,20,1,5,78),
(2024004,6,5,12,2,4,85),
(2024004,7,4,12,2,4,80),
(2024004,8,6,16,3,6,92),
(2024004,9,2,14,1,5,73);

-- ---- AI问答记录 (10) ----
INSERT INTO `t_ai_message` (`message_id`,`user_id`,`question`,`answer`,`created_at`) VALUES
(1,2024003,'Python中列表和元组的区别是什么？','列表可变，元组不可变；列表用[]，元组用()。','2026-03-15 09:00:00'),
(2,2024003,'什么是面向对象编程？','面向对象编程以对象为核心，包含封装、继承和多态。','2026-03-15 09:10:00'),
(3,2024003,'冒泡排序的时间复杂度是多少？','冒泡排序平均和最坏时间复杂度都是O(n^2)。','2026-03-15 09:20:00'),
(4,2024003,'SQL中JOIN有哪些类型？','常见有INNER JOIN、LEFT JOIN、RIGHT JOIN和FULL JOIN。','2026-03-15 09:30:00'),
(5,2024003,'HTTP和HTTPS有什么不同？','HTTPS在HTTP基础上增加了TLS加密层，更安全。','2026-03-15 09:40:00'),
(6,2024004,'HTML中块级元素和行内元素的区别？','块级元素独占一行，行内元素不会换行。','2026-03-15 10:00:00'),
(7,2024004,'Vue的双向绑定原理是什么？','Vue通过数据劫持和发布订阅模式实现双向绑定。','2026-03-15 10:10:00'),
(8,2024004,'SpringBoot的核心注解有哪些？','@SpringBootApplication、@RestController、@Autowired等。','2026-03-15 10:20:00'),
(9,2024004,'进程和线程的区别是什么？','进程是资源分配单位，线程是CPU调度单位。','2026-03-15 10:30:00'),
(10,2024004,'什么是索引？为什么能加速查询？','索引通过缩小检索范围来减少全表扫描，提高查询效率。','2026-03-15 10:40:00');

-- ---- 练习题 (10)：课程1有5道，课程2-6各1道 ----
INSERT INTO `t_exercise_question` (`question_id`,`course_id`,`chapter_id`,`question_type`,`question`,`answer`,`analysis`,`source_type`) VALUES
(1,1,1,'single_choice','以下哪个是Python合法变量名？','C','变量名可由字母数字下划线组成，不能数字开头。','local'),
(2,1,1,'multiple_choice','以下哪些是Python中的可变数据类型？','A、C','列表和字典是可变对象；元组和字符串不可变。','local'),
(3,1,1,'true_false','Python中的列表使用方括号[]定义。','正确','这是Python列表的标准定义方式。','local'),
(4,1,1,'fill_blank','在Python中，用于输出内容的函数是______。','print()','print是基础输出函数。','local'),
(5,1,1,'essay','请简述Python中列表和元组的区别。','列表可变，元组不可变；语法分别是[]和()。','开放题，按关键点给分。','local'),
(6,2,3,'single_choice','Java中用于定义类的关键字是？','A','class是Java定义类的关键字。','local'),
(7,3,4,'single_choice','冒泡排序平均时间复杂度是？','B','常见实现平均复杂度为O(n^2)。','local'),
(8,4,5,'single_choice','SQL中去重关键字是？','A','DISTINCT用于去重。','local'),
(9,5,6,'single_choice','HTTP默认端口是？','B','HTTP默认端口是80。','local'),
(10,6,7,'single_choice','网页结构层使用哪种语言？','A','HTML负责结构。','local');

-- ---- 练习题选项 ----
INSERT INTO `t_exercise_option` (`question_id`,`option_key`,`option_text`,`is_correct`) VALUES
(1,'A','2variable',0),(1,'B','my-variable',0),(1,'C','_myvariable',1),(1,'D','my variable',0),
(2,'A','列表(list)',1),(2,'B','元组(tuple)',0),(2,'C','字典(dict)',1),(2,'D','字符串(string)',0),
(3,'A','正确',1),(3,'B','错误',0),
(6,'A','class',1),(6,'B','object',0),(6,'C','new',0),(6,'D','public',0),
(7,'A','O(n)',0),(7,'B','O(n^2)',1),(7,'C','O(logn)',0),(7,'D','O(1)',0),
(8,'A','DISTINCT',1),(8,'B','GROUP',0),(8,'C','ORDER',0),(8,'D','LIMIT',0),
(9,'A','443',0),(9,'B','80',1),(9,'C','3306',0),(9,'D','6379',0),
(10,'A','HTML',1),(10,'B','CSS',0),(10,'C','SQL',0),(10,'D','Docker',0);

-- ---- 笔记 (10) ----
INSERT INTO `t_note` (`note_id`,`student_id`,`course_id`,`title`,`content`) VALUES
(1,2024003,1,'Python变量命名规则总结','变量名只能包含字母、数字和下划线，且不能以数字开头。'),
(2,2024003,1,'Python数据类型笔记','整数int、浮点数float、字符串str、列表list、元组tuple、字典dict。'),
(3,2024003,2,'Java类与对象笔记','类是对象的模板，对象是类的实例。通过new关键字创建对象。'),
(4,2024003,3,'排序算法比较','冒泡O(n^2)、快排O(nlogn)、归并O(nlogn)、插入O(n^2)。'),
(5,2024003,4,'SQL基础笔记','SELECT、WHERE、GROUP BY、HAVING、ORDER BY、LIMIT的使用。'),
(6,2024004,1,'Python学习心得','Python语法简洁，适合初学者入门，动态类型语言。'),
(7,2024004,6,'HTML标签笔记','常用标签：div、span、p、a、img、ul、li、table等。'),
(8,2024004,7,'Vue生命周期笔记','created、mounted、updated、destroyed四个主要阶段。'),
(9,2024004,8,'SpringBoot注解笔记','@Controller处理请求、@Service业务逻辑、@Repository数据访问。'),
(10,2024004,9,'操作系统进程笔记','进程状态：就绪、运行、阻塞。调度算法：FCFS、SJF、RR。');

-- 维护课程选课人数
UPDATE `t_course` c
SET c.`student_count` = (
  SELECT COUNT(1) FROM `t_student_course` sc WHERE sc.`course_id` = c.`course_id`
);

SET FOREIGN_KEY_CHECKS = 1;
