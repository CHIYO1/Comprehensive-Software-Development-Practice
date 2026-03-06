/*
 Navicat Premium Data Transfer

 Source Server         : root
 Source Server Type    : MySQL
 Source Server Version : 50738 (5.7.38-log)
 Source Host           : localhost:3306
 Source Schema         : gpt

 Target Server Type    : MySQL
 Target Server Version : 50738 (5.7.38-log)
 File Encoding         : 65001

 Date: 10/07/2025 17:32:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for qhistory_q
-- ----------------------------
DROP TABLE IF EXISTS `qhistory_q`;
CREATE TABLE `qhistory_q`  (
  `history_id` int(12) NOT NULL COMMENT '做题记录id',
  `question_id` int(12) NOT NULL COMMENT '该做题记录下每条题目的id',
  `student_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '学生回答',
  `score` int(4) NULL DEFAULT NULL COMMENT '得分',
  `basis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '评分依据',
  PRIMARY KEY (`history_id`, `question_id`) USING BTREE,
  INDEX `question_id`(`question_id`) USING BTREE,
  CONSTRAINT `qhistory_q_ibfk_1` FOREIGN KEY (`history_id`) REFERENCES `t_question_history` (`history_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `qhistory_q_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `t_question` (`question_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qhistory_q
-- ----------------------------

-- ----------------------------
-- Table structure for question_choice
-- ----------------------------
DROP TABLE IF EXISTS `question_choice`;
CREATE TABLE `question_choice`  (
  `question_id` int(12) NOT NULL,
  `choice` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '选项内容',
  PRIMARY KEY (`question_id`, `choice`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question_choice
-- ----------------------------
INSERT INTO `question_choice` VALUES (31, 'A、张量是矩阵的高维推广，矩阵运算是张量运算的特例');
INSERT INTO `question_choice` VALUES (31, 'B、张量和矩阵是完全不同的概念，没有直接关系');
INSERT INTO `question_choice` VALUES (31, 'C、矩阵是张量的高维推广，张量运算是矩阵运算的特例');
INSERT INTO `question_choice` VALUES (31, 'D、张量和矩阵是同一概念的不同名称');
INSERT INTO `question_choice` VALUES (36, 'A、李白');
INSERT INTO `question_choice` VALUES (36, 'B、王羲之');
INSERT INTO `question_choice` VALUES (36, 'C、杜甫');
INSERT INTO `question_choice` VALUES (36, 'D、苏轼');
INSERT INTO `question_choice` VALUES (39, 'A、矩阵乘法');
INSERT INTO `question_choice` VALUES (39, 'B、张量加法');
INSERT INTO `question_choice` VALUES (39, 'C、张量转置');
INSERT INTO `question_choice` VALUES (39, 'D、张量格式化');
INSERT INTO `question_choice` VALUES (40, 'A. tf.add()');
INSERT INTO `question_choice` VALUES (40, 'B. tf.sub()');
INSERT INTO `question_choice` VALUES (40, 'C. tf.mul()');
INSERT INTO `question_choice` VALUES (40, 'D. tf.square()');
INSERT INTO `question_choice` VALUES (44, '2');
INSERT INTO `question_choice` VALUES (52, '使用a + b');
INSERT INTO `question_choice` VALUES (52, '使用tf.add(a, b)');
INSERT INTO `question_choice` VALUES (52, '使用tf.concat([a, b])');
INSERT INTO `question_choice` VALUES (52, '使用tf.matMul(a, b)');
INSERT INTO `question_choice` VALUES (55, '正确');
INSERT INTO `question_choice` VALUES (55, '错误');
INSERT INTO `question_choice` VALUES (56, '(2 + 2) * 3');
INSERT INTO `question_choice` VALUES (56, '2 ** 3');
INSERT INTO `question_choice` VALUES (56, '2 + 2 * 3');
INSERT INTO `question_choice` VALUES (56, '8 / 1');
INSERT INTO `question_choice` VALUES (58, '仅对图像数据进行reshape处理，不需要归一化');
INSERT INTO `question_choice` VALUES (58, '仅对图像数据进行归一化处理，不需要reshape');
INSERT INTO `question_choice` VALUES (58, '将图像数据reshape为模型需要的形状并进行归一化处理');
INSERT INTO `question_choice` VALUES (58, '直接使用原始图像数据进行模型训练，无需任何预处理');

-- ----------------------------
-- Table structure for question_knowledge
-- ----------------------------
DROP TABLE IF EXISTS `question_knowledge`;
CREATE TABLE `question_knowledge`  (
  `question_id` int(12) NOT NULL COMMENT '题目id',
  `knowledge` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '知识点id',
  PRIMARY KEY (`question_id`, `knowledge`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question_knowledge
-- ----------------------------
INSERT INTO `question_knowledge` VALUES (31, '张量');
INSERT INTO `question_knowledge` VALUES (31, '深度学习');
INSERT INTO `question_knowledge` VALUES (31, '矩阵运算');
INSERT INTO `question_knowledge` VALUES (32, 'CSS');
INSERT INTO `question_knowledge` VALUES (32, 'HTML');
INSERT INTO `question_knowledge` VALUES (32, 'JavaScript');
INSERT INTO `question_knowledge` VALUES (32, 'Node.js');
INSERT INTO `question_knowledge` VALUES (32, 'TypeScript');
INSERT INTO `question_knowledge` VALUES (34, 'TensorFlow');
INSERT INTO `question_knowledge` VALUES (34, '张量');
INSERT INTO `question_knowledge` VALUES (34, '矩阵运算');
INSERT INTO `question_knowledge` VALUES (36, '兰亭集序');
INSERT INTO `question_knowledge` VALUES (37, 'TensorFlow');
INSERT INTO `question_knowledge` VALUES (37, '计算图');
INSERT INTO `question_knowledge` VALUES (39, 'TensorFlow');
INSERT INTO `question_knowledge` VALUES (39, '张量');
INSERT INTO `question_knowledge` VALUES (39, '矩阵运算');
INSERT INTO `question_knowledge` VALUES (40, 'TensorFlow');
INSERT INTO `question_knowledge` VALUES (40, '张量');
INSERT INTO `question_knowledge` VALUES (40, '张量运算');
INSERT INTO `question_knowledge` VALUES (47, 'cc');
INSERT INTO `question_knowledge` VALUES (52, 'TensorFlow.js');
INSERT INTO `question_knowledge` VALUES (52, '张量操作');
INSERT INTO `question_knowledge` VALUES (53, 'TensorFlow.js架构与优势');
INSERT INTO `question_knowledge` VALUES (55, 'python');
INSERT INTO `question_knowledge` VALUES (55, '函数调用');
INSERT INTO `question_knowledge` VALUES (56, 'python');
INSERT INTO `question_knowledge` VALUES (56, '基本运算');
INSERT INTO `question_knowledge` VALUES (57, 'python');
INSERT INTO `question_knowledge` VALUES (57, '函数调用');
INSERT INTO `question_knowledge` VALUES (58, 'Softmax与交叉熵损失');
INSERT INTO `question_knowledge` VALUES (58, '图像数据预处理（reshape、归一化）');
INSERT INTO `question_knowledge` VALUES (59, 'python');
INSERT INTO `question_knowledge` VALUES (59, '基本运算');

-- ----------------------------
-- Table structure for section_knowledge
-- ----------------------------
DROP TABLE IF EXISTS `section_knowledge`;
CREATE TABLE `section_knowledge`  (
  `section_id` int(12) NOT NULL COMMENT '章节id',
  `knowledge` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '知识点',
  PRIMARY KEY (`section_id`, `knowledge`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of section_knowledge
-- ----------------------------
INSERT INTO `section_knowledge` VALUES (1, '知识点1');
INSERT INTO `section_knowledge` VALUES (1, '知识点3');
INSERT INTO `section_knowledge` VALUES (7, 'lllll');
INSERT INTO `section_knowledge` VALUES (8, '？？');
INSERT INTO `section_knowledge` VALUES (9, '？？？？？？');
INSERT INTO `section_knowledge` VALUES (10, '？？？？？');
INSERT INTO `section_knowledge` VALUES (12, '？？？？');
INSERT INTO `section_knowledge` VALUES (14, 'TensorFlow');
INSERT INTO `section_knowledge` VALUES (14, '张量');
INSERT INTO `section_knowledge` VALUES (14, '深度学习');
INSERT INTO `section_knowledge` VALUES (14, '矩阵运算');
INSERT INTO `section_knowledge` VALUES (14, '计算图');
INSERT INTO `section_knowledge` VALUES (40, 'TensorFlow.js架构与优势');
INSERT INTO `section_knowledge` VALUES (40, '内存优化');
INSERT INTO `section_knowledge` VALUES (40, '变量管理');
INSERT INTO `section_knowledge` VALUES (40, '张量操作');
INSERT INTO `section_knowledge` VALUES (40, '模型构建方式');
INSERT INTO `section_knowledge` VALUES (41, '性能评估');
INSERT INTO `section_knowledge` VALUES (41, '数据预处理');
INSERT INTO `section_knowledge` VALUES (41, '线性回归模型原理');
INSERT INTO `section_knowledge` VALUES (41, '训练监控');
INSERT INTO `section_knowledge` VALUES (41, '训练配置');
INSERT INTO `section_knowledge` VALUES (42, 'CNN原理');
INSERT INTO `section_knowledge` VALUES (42, '图像数据预处理');
INSERT INTO `section_knowledge` VALUES (42, '多分类任务');
INSERT INTO `section_knowledge` VALUES (42, '模型评估');
INSERT INTO `section_knowledge` VALUES (42, '混淆矩阵');
INSERT INTO `section_knowledge` VALUES (43, '交互优化');
INSERT INTO `section_knowledge` VALUES (43, '实时数据采集');
INSERT INTO `section_knowledge` VALUES (43, '模型部署');
INSERT INTO `section_knowledge` VALUES (43, '迁移学习');
INSERT INTO `section_knowledge` VALUES (44, 'TensorFlow.js架构与优势');
INSERT INTO `section_knowledge` VALUES (44, '内存管理实战：dispose与tf.tidy对比');
INSERT INTO `section_knowledge` VALUES (44, '张量创建与运算');
INSERT INTO `section_knowledge` VALUES (44, '核心概念：张量操作、变量管理、内存优化');
INSERT INTO `section_knowledge` VALUES (44, '模型构建方式：Sequential Model vs. Functional Model');
INSERT INTO `section_knowledge` VALUES (44, '环境配置：Script标签引入CDN + Parcel工程化构建');
INSERT INTO `section_knowledge` VALUES (45, '数据加载与可视化（tfvis.render.scatterplot）');
INSERT INTO `section_knowledge` VALUES (45, '数据预处理：归一化、打乱、张量转换');
INSERT INTO `section_knowledge` VALUES (45, '构建全连接网络（tf.layers.dense）');
INSERT INTO `section_knowledge` VALUES (45, '线性回归模型原理与结构设计');
INSERT INTO `section_knowledge` VALUES (45, '结果反归一化与预测曲线绘制');
INSERT INTO `section_knowledge` VALUES (45, '训练监控与性能评估（tfvis.show.fitCallbacks）');
INSERT INTO `section_knowledge` VALUES (45, '训练配置：优化器选择（Adam）、损失函数（MSE）');
INSERT INTO `section_knowledge` VALUES (46, 'CNN原理：卷积层、池化层、全连接层作用');
INSERT INTO `section_knowledge` VALUES (46, 'MNIST数据集加载与可视化（tf.browser.toPixels）');
INSERT INTO `section_knowledge` VALUES (46, '图像数据预处理（reshape、归一化）');
INSERT INTO `section_knowledge` VALUES (46, '多分类任务：Softmax与交叉熵损失');
INSERT INTO `section_knowledge` VALUES (46, '构建CNN模型（卷积核、滤波器、步长配置）');
INSERT INTO `section_knowledge` VALUES (46, '模型评估：混淆矩阵与类别准确率（tfvis.metrics.confusionMatrix）');
INSERT INTO `section_knowledge` VALUES (46, '训练过程分析：准确率/损失曲线监控');
INSERT INTO `section_knowledge` VALUES (47, '交互优化：可视化预测结果与置信度');
INSERT INTO `section_knowledge` VALUES (47, '加载Rock-Paper-Scissors数据集');
INSERT INTO `section_knowledge` VALUES (47, '实时数据采集：浏览器摄像头API调用');
INSERT INTO `section_knowledge` VALUES (47, '模型部署：浏览器端实时手势预测');
INSERT INTO `section_knowledge` VALUES (47, '迁移学习：冻结基础层+定制分类层');
INSERT INTO `section_knowledge` VALUES (47, '迁移学习：复用预训练模型（VGG/ResNet）');
INSERT INTO `section_knowledge` VALUES (49, 'Sequential Model vs. Functional Model');
INSERT INTO `section_knowledge` VALUES (49, 'TensorFlow.js架构与优势');
INSERT INTO `section_knowledge` VALUES (49, '内存优化（tf.tidy）');
INSERT INTO `section_knowledge` VALUES (49, '变量管理');
INSERT INTO `section_knowledge` VALUES (49, '张量操作');
INSERT INTO `section_knowledge` VALUES (50, '优化器选择（Adam）');
INSERT INTO `section_knowledge` VALUES (50, '损失函数（MSE）');
INSERT INTO `section_knowledge` VALUES (50, '数据预处理（归一化、打乱、张量转换）');
INSERT INTO `section_knowledge` VALUES (50, '线性回归模型原理');
INSERT INTO `section_knowledge` VALUES (50, '训练监控与性能评估');
INSERT INTO `section_knowledge` VALUES (51, 'CNN原理（卷积层、池化层、全连接层）');
INSERT INTO `section_knowledge` VALUES (51, 'MNIST数据集加载与可视化');
INSERT INTO `section_knowledge` VALUES (51, 'Softmax与交叉熵损失');
INSERT INTO `section_knowledge` VALUES (51, '图像数据预处理（reshape、归一化）');
INSERT INTO `section_knowledge` VALUES (51, '混淆矩阵与类别准确率');
INSERT INTO `section_knowledge` VALUES (52, 'Rock-Paper-Scissors数据集加载');
INSERT INTO `section_knowledge` VALUES (52, '交互优化与可视化');
INSERT INTO `section_knowledge` VALUES (52, '模型部署与实时预测');
INSERT INTO `section_knowledge` VALUES (52, '浏览器摄像头API调用');
INSERT INTO `section_knowledge` VALUES (52, '迁移学习（复用预训练模型）');

-- ----------------------------
-- Table structure for std_crs
-- ----------------------------
DROP TABLE IF EXISTS `std_crs`;
CREATE TABLE `std_crs`  (
  `user_id` int(12) NOT NULL COMMENT '学生id',
  `course_id` int(12) NOT NULL COMMENT '学生学习的课程的id',
  PRIMARY KEY (`user_id`, `course_id`) USING BTREE,
  INDEX `course_id`(`course_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of std_crs
-- ----------------------------
INSERT INTO `std_crs` VALUES (2024001, 12);

-- ----------------------------
-- Table structure for t_conv
-- ----------------------------
DROP TABLE IF EXISTS `t_conv`;
CREATE TABLE `t_conv`  (
  `conv_id` int(12) NOT NULL AUTO_INCREMENT,
  `user_id` int(12) NULL DEFAULT NULL,
  `conv_user` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `conv_gpt` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `activate_state` tinyint(4) NULL DEFAULT NULL,
  PRIMARY KEY (`conv_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_conv
-- ----------------------------
INSERT INTO `t_conv` VALUES (36, 2, '请你写一段简短的java程序\n', '以下是一个简单的 Java \"Hello World\" 程序示例：\n\n```java\npublic class HelloWorld {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, World!\");\n    }\n}\n```\n\n这个程序：\n1. 定义了一个公共类 HelloWorld\n2. 包含主方法 main（程序入口）\n3. 使用 System.out.println 输出字符串到控制台\n4. 遵循标准的 Java 语法结构\n5. 运行时会在控制台显示 \"Hello, World!\"\n\n要运行这个程序：\n1. 保存为 HelloWorld.java\n2. 编译：javac HelloWorld.java\n3. 运行：java HelloWorld', 1);
INSERT INTO `t_conv` VALUES (37, 2, '请你写一段简短的c++程序', '以下是一个简单的 C++ \"Hello World\" 程序示例：\n\n```cpp\n#include <iostream>\n\nint main() {\n    std::cout << \"Hello, World!\" << std::endl;\n    return 0;\n}\n```\n\n### 程序说明：\n1. **`#include <iostream>`**  \n   包含标准输入输出流库，提供 `cout` 和 `endl` 等功能\n\n2. **`int main()`**  \n   程序入口点，每个 C++ 程序必须有且只有一个 `main` 函数\n\n3. **`std::cout << \"Hello, World!\"`**  \n   使用标准输出流将字符串打印到控制台\n\n4. **`<< std::endl`**  \n   输出换行符并刷新输出缓冲区\n\n5. **`return 0;`**  \n   表示程序成功结束（0 表示正常退出）\n\n### 编译运行步骤：\n1. 保存为 `hello.cpp`\n2. 编译：`g++ hello.cpp -o hello`\n3. 运行：\n   - Windows: `hello.exe`\n   - Linux/Mac: `./hello`\n\n### 输出结果：\n```\nHello, World!\n```\n\n> 注意：现代 C++ 中通常会避免 `using namespace std;`，推荐显式使用 `std::` 前缀，防止命名空间污染。', 1);
INSERT INTO `t_conv` VALUES (38, 2024001, '你好', '你好呀！👋 很高兴见到你～我是 DeepSeek 助手，有什么我可以帮你的吗？无论是学习问题、工作文档、生活小技巧，还是只想聊聊天，我都在这里等你哦！😊\n\n想从哪里开始呢？', 0);
INSERT INTO `t_conv` VALUES (39, 2024001, '生成一段简单的java程序\n', '以下是一个简单的 Java 程序示例，功能是计算两个整数的和并输出结果：\n\n```java\npublic class SimpleAddition {\n\n    public static void main(String[] args) {\n        // 定义两个整数\n        int num1 = 15;\n        int num2 = 27;\n        \n        // 计算它们的和\n        int sum = num1 + num2;\n        \n        // 输出结果到控制台\n        System.out.println(\"第一个数字: \" + num1);\n        System.out.println(\"第二个数字: \" + num2);\n        System.out.println(\"两数之和: \" + sum);\n    }\n}\n```\n\n### 程序说明：\n1. **类定义**：创建名为 `SimpleAddition` 的公共类\n2. **主方法**：程序入口点 `main` 方法\n3. **变量定义**：声明两个整数变量并初始化\n4. **加法运算**：计算两数之和\n5. **结果输出**：使用 `System.out.println` 打印计算结果\n\n### 运行效果：\n```\n第一个数字: 15\n第二个数字: 27\n两数之和: 42\n```\n\n### 进阶扩展建议：\n- 添加用户输入功能（使用 `Scanner` 类）\n- 实现减法/乘法运算\n- 增加异常处理（如输入非数字时）\n- 转换为方法调用形式\n\n需要其他类型的示例（如循环、条件判断、面向对象等）可以随时告诉我！', 1);

-- ----------------------------
-- Table structure for t_course
-- ----------------------------
DROP TABLE IF EXISTS `t_course`;
CREATE TABLE `t_course`  (
  `course_id` int(12) NOT NULL AUTO_INCREMENT COMMENT '课程',
  `course_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '课程名',
  `course_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '课程介绍',
  `cover_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '封面相对路径',
  `user_id` int(12) NULL DEFAULT NULL COMMENT '外键-创建课程的教师id',
  PRIMARY KEY (`course_id`) USING BTREE,
  INDEX `course_name`(`course_name`) USING BTREE,
  INDEX `t_course_ibfk_1`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_course
-- ----------------------------
INSERT INTO `t_course` VALUES (1, '测试课程2', '课程描述已修改', '62fb73c6-d6bc-4aa1-8c6e-036e3643f1c0.png', 2024001);
INSERT INTO `t_course` VALUES (2, '测试惆怅长岑长', '无介绍', '1c32ecac-2da7-4f1b-8b6b-b67f8cd92e60.jpg', 2024001);
INSERT INTO `t_course` VALUES (5, '差不多得了', 'null', 'aef7fe42-0a1d-4757-8012-712f327784ec.jpg', 2024001);
INSERT INTO `t_course` VALUES (7, '....', 'null', '31d96732-6e7c-41fe-b7d5-0686723cd798.png', 2024001);
INSERT INTO `t_course` VALUES (8, 'TensorFlow.js应用开发', '123\r\n123\r\n', 'c69e50ff-194e-448d-8e05-b1fef23de6b5.png', 2024001);
INSERT INTO `t_course` VALUES (12, 'TensorFlow.js应用开发', '本课程旨在教授学生如何使用TensorFlow.js进行浏览器端的机器学习应用开发。课程涵盖从基础概念到实战项目的全面内容，包括TensorFlow.js的核心概念、模型构建、数据预处理、训练监控以及模型部署等。通过理论学习和实验操作，学生将掌握在浏览器和Node.js环境中开发交互式AI应用的技能。', '9876bd66-32d2-4d7c-b23d-4c9f0fe360e4.png', 2024001);
INSERT INTO `t_course` VALUES (18, 'TensorFlow.js应用开发', '本课程旨在帮助学习者全面掌握TensorFlow.js的核心概念和应用开发技能。课程内容涵盖TensorFlow.js的基础知识、回归任务实战、图像分类任务实战以及综合项目开发。通过理论学习和实验操作，学习者将能够熟练使用TensorFlow.js构建和部署机器学习模型，开发交互式AI应用。课程特别注重实践，通过多个实战项目，帮助学习者深入理解TensorFlow.js的优势和应用场景。', NULL, 2024001);
INSERT INTO `t_course` VALUES (19, '', '', NULL, 2024001);
INSERT INTO `t_course` VALUES (20, 'TensorFlow.js应用开发', '本课程旨在帮助学员掌握TensorFlow.js的核心概念和应用开发技能。课程内容包括TensorFlow.js的基础知识、回归任务实战、图像分类任务实战以及综合项目开发。通过理论学习和实验操作，学员将能够熟练使用TensorFlow.js构建和部署机器学习模型，开发交互式AI应用。', NULL, 2024001);

-- ----------------------------
-- Table structure for t_document
-- ----------------------------
DROP TABLE IF EXISTS `t_document`;
CREATE TABLE `t_document`  (
  `document_id` int(12) NOT NULL AUTO_INCREMENT COMMENT '文档',
  `document_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件名',
  `document_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文件描述',
  `document_size` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件大小(B)',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '存储相对路径',
  `created_date` date NULL DEFAULT NULL COMMENT '创建日期',
  `user_id` int(12) NULL DEFAULT NULL COMMENT '所属教师id',
  PRIMARY KEY (`document_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_document
-- ----------------------------
INSERT INTO `t_document` VALUES (7, 'dingtalk_downloader.exe', '这是很长的一段描述。这是很长的一段描述。这是很长的一段描述。这是很长的一段描述。这是很长的一段描述。这是很长的一段描述。这是很长的一段描述。这是很长的一段描述。这是很长的一段描述。这是很长的一段描述。这是很长的一段描述。这是很长的一段描述。这是很长的一段描述。这是很长的一段描述。这是很长的一段描述。这是很长的一段描述。这是很长的一段描述。这是很长的一段描述。这是很长的一段描述。这是很长的一段描述。这是很长的一段描述。', '2.69MB', '846972ba-dbec-44f1-9f8a-046246185996.exe', '2025-06-11', 2024001);
INSERT INTO `t_document` VALUES (8, 'RimWorld.zip', '测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你', '2.35MB', '5b1af15e-f808-4ce0-b30a-c451bf05bae3.zip', '2025-06-11', 2024001);
INSERT INTO `t_document` VALUES (9, 'my.docx', '测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你', '9.38KB', '5dc74c55-07a4-4813-9d08-a434baf47dca.docx', '2025-06-11', 2024001);
INSERT INTO `t_document` VALUES (10, 'config.json', '测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你测试室冲冲冲撒擦首付款请你', '502B', '5707408a-724e-4d2f-a7f5-a05446c46e53.json', '2025-06-11', 2024001);
INSERT INTO `t_document` VALUES (11, 'WhoIs.zip', '测试室冲冲冲撒擦首付款请你', '585.14KB', '9ee35a57-546d-4ec9-a9fa-ef4ff7e97bfd.zip', '2025-06-11', 2024001);
INSERT INTO `t_document` VALUES (12, '文献记录模板.png', '挨饿', '12.56KB', '5849f8d8-4289-46dc-b800-8cad45d195e5.png', '2025-06-11', 2024001);
INSERT INTO `t_document` VALUES (13, '游、飞、跑.png', '一坨', '1MB', '4cf9028f-4f40-4690-8ea5-666a1db526d7.png', '2025-06-19', 2024001);
INSERT INTO `t_document` VALUES (14, '文档.docx', '哇哇哇', '10.76KB', '7a369e97-98db-4f6e-8f7f-e643c2736cc2.docx', '2025-06-21', 2024001);
INSERT INTO `t_document` VALUES (15, 'Linux命令.txt', '懂的都懂', '858B', '2c2b2b51-2d54-4d44-8090-9f4aad35ff8d.txt', '2025-06-23', 2024001);
INSERT INTO `t_document` VALUES (16, 'idea.pptx', '测试ppt', '30.78MB', '8e7dac93-0aad-4b25-ac7d-117117af55b0.pptx', '2025-06-25', 2024001);
INSERT INTO `t_document` VALUES (17, '诚信复试.pdf', 'pdf', '61.54KB', 'b3d1e569-e92d-444e-8bb8-31101b1c7433.pdf', '2025-06-25', 2024001);
INSERT INTO `t_document` VALUES (18, 'GPT.txt', 'null', '3.3KB', 'ad561a4d-d816-40bf-990e-f0e957c8e5a7.txt', '2025-06-27', 2024001);
INSERT INTO `t_document` VALUES (19, 'edu-system.pdf', 'null', '250.07KB', '2c9880dd-1d80-4230-83ba-9553f416111e.pdf', '2025-06-27', 2024001);

-- ----------------------------
-- Table structure for t_knowledge
-- ----------------------------
DROP TABLE IF EXISTS `t_knowledge`;
CREATE TABLE `t_knowledge`  (
  `knowledge_id` int(12) NOT NULL AUTO_INCREMENT COMMENT '知识点id',
  `knowledge_summary` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '知识点概要',
  `knowledge_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '知识点完整说明',
  PRIMARY KEY (`knowledge_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_knowledge
-- ----------------------------

-- ----------------------------
-- Table structure for t_question
-- ----------------------------
DROP TABLE IF EXISTS `t_question`;
CREATE TABLE `t_question`  (
  `question_id` int(12) NOT NULL AUTO_INCREMENT COMMENT '题目id',
  `question_stem` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '题干',
  `question_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '正确答案（题目类型为\"选择题\"时，该属性为选项号；题型为“编程题”时，该属性为程序标准输出，可为空）',
  `code_output` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '仅当题目类型为“编程题\"时有效，编程题的期望输出',
  `question_parse` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '解析/评分标准',
  `question_type` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '题目类型（“主观题”、\"选择题\"、\"编程题\")',
  `set_id` int(12) NULL DEFAULT NULL COMMENT '所属试题集',
  PRIMARY KEY (`question_id`, `question_type`) USING BTREE,
  INDEX `set_id`(`set_id`) USING BTREE,
  CONSTRAINT `t_question_ibfk_1` FOREIGN KEY (`set_id`) REFERENCES `t_question_set` (`set_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 60 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_question
-- ----------------------------
INSERT INTO `t_question` VALUES (2, '这是题这是题干这是题干这是题干', 'A', NULL, '解析', '选择题', 3);
INSERT INTO `t_question` VALUES (11, '另外一个题干', 'A', NULL, NULL, '选择题', 3);
INSERT INTO `t_question` VALUES (23, '这是一道主观题题干这是一道主观题题干', NULL, NULL, NULL, '主观题', 3);
INSERT INTO `t_question` VALUES (24, '这是一道编程题题干这是一道编程题题干', '0', NULL, NULL, '编程题', 3);
INSERT INTO `t_question` VALUES (26, '主观题题干', NULL, NULL, NULL, '主观题', 1);
INSERT INTO `t_question` VALUES (27, '选择题题干', 'B', NULL, NULL, '选择题', 1);
INSERT INTO `t_question` VALUES (30, '这是一道主观题', '哇哇哇哇啊啊哇哇哇', NULL, NULL, '主观题', 4);
INSERT INTO `t_question` VALUES (31, '在深度学习中，张量和矩阵运算的关系是什么？', 'A', NULL, '张量是矩阵的高维推广，可以看作是矩阵的扩展。矩阵运算是张量运算在二维情况下的特例。因此，正确答案是A。', '选择题', 6);
INSERT INTO `t_question` VALUES (33, '呵呵呵', 'A', NULL, NULL, '选择题', 3);
INSERT INTO `t_question` VALUES (34, '请解释TensorFlow中的张量（Tensor）是什么，并说明其在矩阵运算中的作用。', 'TensorFlow中的张量（Tensor）是一个可以表示任意维度的数据数组，它是TensorFlow中的基本数据结构。张量在矩阵运算中扮演着核心角色，因为它们可以高效地表示和执行多维数组的数学运算，如矩阵乘法、加法等。', NULL, '评分标准：1. 正确解释张量的定义（30分）；2. 准确描述张量在矩阵运算中的作用（40分）；3. 回答清晰、逻辑性强（30分）。', '主观题', 7);
INSERT INTO `t_question` VALUES (36, '《兰亭集序》的作者是谁？', 'B', NULL, '《兰亭集序》是东晋书法家王羲之的代表作，被誉为\"天下第一行书\"。', '选择题', 6);
INSERT INTO `t_question` VALUES (39, '在TensorFlow中，以下哪种操作不属于基本的张量运算？', 'D', NULL, '矩阵乘法、张量加法和张量转置都是TensorFlow中基本的张量运算，而张量格式化不属于基本的张量运算。', '选择题', 7);
INSERT INTO `t_question` VALUES (40, '在TensorFlow.js中，以下哪种操作可以用于对两个张量进行元素级别的乘法运算？', 'C', NULL, '在TensorFlow.js中，tf.mul()函数用于对两个张量进行元素级别的乘法运算。tf.add()用于加法，tf.sub()用于减法，tf.square()用于对单个张量的元素进行平方运算。', '选择题', 7);
INSERT INTO `t_question` VALUES (47, 'c', 'c', NULL, NULL, '编程题', 6);
INSERT INTO `t_question` VALUES (49, NULL, NULL, NULL, NULL, '选择题', 8);
INSERT INTO `t_question` VALUES (50, NULL, NULL, NULL, NULL, '选择题', 9);
INSERT INTO `t_question` VALUES (51, NULL, NULL, NULL, NULL, '主观题', 9);
INSERT INTO `t_question` VALUES (52, '在TensorFlow.js中，如何正确使用张量操作来实现两个2x2矩阵的加法？', 'A', NULL, '在TensorFlow.js中，tf.add(a, b)是用于实现两个张量相加的正确方法。选项B中的直接相加操作在TensorFlow.js中不被支持，选项C是用于连接张量，选项D是用于矩阵乘法。因此，正确答案是A。', '选择题', 10);
INSERT INTO `t_question` VALUES (53, '请简述TensorFlow.js的主要架构特点及其优势。', 'TensorFlow.js的主要架构特点包括：1. 支持在浏览器和Node.js环境中运行；2. 提供Layers API和Core API两种创建模型的方式；3. 完整模仿Keras的模型定义方式；4. 支持数据预处理和模型可视化。其优势包括：1. 无需安装额外软件，直接在浏览器中运行；2. 可以利用GPU加速计算；3. 支持迁移学习，可以加载预训练模型；4. 便于部署和分享AI应用。', NULL, '评分标准：1. 正确描述TensorFlow.js的架构特点（4分）；2. 正确列举TensorFlow.js的优势（4分）；3. 回答完整、条理清晰（2分）。', '主观题', 10);
INSERT INTO `t_question` VALUES (55, '在Python中，函数调用时参数传递的方式是值传递。', 'B', '', '在Python中，函数调用时参数传递的方式实际上是对象引用传递。对于不可变对象（如整数、字符串、元组等），函数内部对参数的修改不会影响原始对象；对于可变对象（如列表、字典等），函数内部对参数的修改会影响原始对象。因此，题目中的说法是错误的。', '选择题', 10);
INSERT INTO `t_question` VALUES (56, '在Python中，以下哪个表达式的结果是8？', 'C', '', '选项A的结果是8（2 + 6），选项B的结果是12（4 * 3），选项C的结果是8（2的3次方），选项D的结果是8.0（浮点数除法）。虽然选项D的值也是8，但它是浮点数而非整数。因此最符合题目要求的是选项C。', '选择题', 10);
INSERT INTO `t_question` VALUES (57, '请编写一个Python函数，该函数接收两个数字参数，返回这两个数字的和与差的乘积。函数名为`calculate_product`。', 'def calculate_product(a, b):\n    sum_result = a + b\n    difference = a - b\n    return sum_result * difference\n\n# 示例调用\nprint(calculate_product(5, 3))  # 输出: 16', '16\n', '1. 函数定义正确，包含正确的函数名和参数\n2. 正确计算两个数字的和与差的乘积\n3. 返回计算结果', '编程题', 10);
INSERT INTO `t_question` VALUES (58, '在TensorFlow.js中实现手势识别（剪刀、石头、布）时，以下哪个步骤是正确的？', 'B', '', '在TensorFlow.js中实现手势识别时，正确的步骤是将图像数据reshape为模型需要的形状并进行归一化处理。这是因为模型通常需要固定大小的输入，并且归一化可以加速模型的收敛。直接使用原始图像数据或仅进行部分预处理都会影响模型的性能。', '选择题', 10);
INSERT INTO `t_question` VALUES (59, '编写一个Python程序，计算并输出两个数的和、差、积和商。假设这两个数分别为10和5。', 'a = 10\nb = 5\nprint(f\'和: {a + b}\')\nprint(f\'差: {a - b}\')\nprint(f\'积: {a * b}\')\nprint(f\'商: {a / b}\')', '和: 15\n差: 5\n积: 50\n商: 2.0\n', '程序应正确计算两个数的和、差、积和商，并输出结果。评分将基于计算的准确性和输出的格式。', '编程题', 7);

-- ----------------------------
-- Table structure for t_question_choice
-- ----------------------------
DROP TABLE IF EXISTS `t_question_choice`;
CREATE TABLE `t_question_choice`  (
  `choice_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '选择题选项',
  `choice_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '选项内容',
  `question_id` int(12) NULL DEFAULT NULL COMMENT '所属题目',
  PRIMARY KEY (`choice_id`) USING BTREE,
  INDEX `question_id`(`question_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_question_choice
-- ----------------------------
INSERT INTO `t_question_choice` VALUES (1, NULL, 1);
INSERT INTO `t_question_choice` VALUES (8, NULL, 1);
INSERT INTO `t_question_choice` VALUES (9, NULL, 1);
INSERT INTO `t_question_choice` VALUES (11, '正确答案', 2);
INSERT INTO `t_question_choice` VALUES (12, '错误答案', 2);
INSERT INTO `t_question_choice` VALUES (13, NULL, 6);
INSERT INTO `t_question_choice` VALUES (14, '正确答案', 11);
INSERT INTO `t_question_choice` VALUES (15, '错误答案', 10);
INSERT INTO `t_question_choice` VALUES (16, '正确答案', 10);
INSERT INTO `t_question_choice` VALUES (17, '正确答案', 25);
INSERT INTO `t_question_choice` VALUES (18, '错错', 27);
INSERT INTO `t_question_choice` VALUES (19, '错错', 27);
INSERT INTO `t_question_choice` VALUES (20, '错错', 27);
INSERT INTO `t_question_choice` VALUES (21, '错误', 29);
INSERT INTO `t_question_choice` VALUES (22, '正确', 29);
INSERT INTO `t_question_choice` VALUES (23, '错错错', 29);
INSERT INTO `t_question_choice` VALUES (24, 'what?', 31);
INSERT INTO `t_question_choice` VALUES (25, '?????', 31);
INSERT INTO `t_question_choice` VALUES (26, '???????????', 31);
INSERT INTO `t_question_choice` VALUES (27, '正确答案', 33);
INSERT INTO `t_question_choice` VALUES (28, '错误答案', 33);

-- ----------------------------
-- Table structure for t_question_history
-- ----------------------------
DROP TABLE IF EXISTS `t_question_history`;
CREATE TABLE `t_question_history`  (
  `history_id` int(12) NOT NULL AUTO_INCREMENT COMMENT '学生做题记录',
  `history_accuracy` float NULL DEFAULT NULL COMMENT '平均正确率',
  `user_id` int(12) NULL DEFAULT NULL COMMENT '所属学生id',
  PRIMARY KEY (`history_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_question_history
-- ----------------------------

-- ----------------------------
-- Table structure for t_question_set
-- ----------------------------
DROP TABLE IF EXISTS `t_question_set`;
CREATE TABLE `t_question_set`  (
  `set_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '试题集',
  `set_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '试题集名',
  `set_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '试题集描述',
  `user_id` int(12) NULL DEFAULT NULL COMMENT '所属教师id',
  PRIMARY KEY (`set_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_question_set
-- ----------------------------
INSERT INTO `t_question_set` VALUES (1, '测测', NULL, 2024001);
INSERT INTO `t_question_set` VALUES (3, '测试试题集更改111', '这是测试机描述啦啦啦啦啦', 2024001);
INSERT INTO `t_question_set` VALUES (4, '待编辑23333', '无描述', 2024001);
INSERT INTO `t_question_set` VALUES (6, '临时实体2', '？？？？', 2024001);
INSERT INTO `t_question_set` VALUES (7, '测试试题集2', '这是用来测试的', 2024001);
INSERT INTO `t_question_set` VALUES (8, '123', '', 2024001);
INSERT INTO `t_question_set` VALUES (9, '', '', 2024001);
INSERT INTO `t_question_set` VALUES (10, '测试试题集', '这是用来测试的试题集', 2024001);

-- ----------------------------
-- Table structure for t_record_question
-- ----------------------------
DROP TABLE IF EXISTS `t_record_question`;
CREATE TABLE `t_record_question`  (
  `question_record_id` int(12) NOT NULL AUTO_INCREMENT COMMENT '单个题目做题记录id',
  `question_score` int(4) NULL DEFAULT NULL COMMENT '单个题目得分',
  `student_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '学生回答',
  `student_output` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '程序运行结果（仅编程题有效）',
  `score_parse` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '评分依据',
  `error_parse` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '错误分析',
  `question_id` int(12) NULL DEFAULT NULL COMMENT '做的哪道题',
  `set_record_id` int(12) NULL DEFAULT NULL COMMENT '所属记录id',
  PRIMARY KEY (`question_record_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_record_question
-- ----------------------------
INSERT INTO `t_record_question` VALUES (9, 100, 'A', NULL, NULL, '在TensorFlow.js中，tf.add(a, b)是用于实现两个张量相加的正确方法。选项B中的直接相加操作在TensorFlow.js中不被支持，选项C是用于连接张量，选项D是用于矩阵乘法。因此，正确答案是A。', 52, 4);
INSERT INTO `t_record_question` VALUES (10, 60, 'TensorFlow.js的主要架构特点包括：1. 支持在浏览器和Node.js环境中运行；2. 提供Layers API和Core API两种创建模型的方式；', '', '用户正确描述了TensorFlow.js的两个架构特点（支持在浏览器和Node.js环境中运行，提供Layers API和Core API两种创建模型的方式），但未提及完整模仿Keras的模型定义方式和支持数据预处理和模型可视化。用户也未列举TensorFlow.js的优势。', '用户回答中缺少对TensorFlow.js优势的描述，如无需安装额外软件、利用GPU加速计算、支持迁移学习和便于部署等。建议补充完整信息以提高回答质量。', 53, 4);
INSERT INTO `t_record_question` VALUES (11, 0, 'A', NULL, NULL, '在Python中，函数调用时参数传递的方式实际上是对象引用传递。对于不可变对象（如整数、字符串、元组等），函数内部对参数的修改不会影响原始对象；对于可变对象（如列表、字典等），函数内部对参数的修改会影响原始对象。因此，题目中的说法是错误的。', 55, 4);
INSERT INTO `t_record_question` VALUES (12, 100, 'C', NULL, NULL, '选项A的结果是8（2 + 6），选项B的结果是12（4 * 3），选项C的结果是8（2的3次方），选项D的结果是8.0（浮点数除法）。虽然选项D的值也是8，但它是浮点数而非整数。因此最符合题目要求的是选项C。', 56, 4);
INSERT INTO `t_record_question` VALUES (13, 100, 'def calculate_product(a, b):\n    sum = a + b\n    diff = a - b\n    return sum * diff\n\nprint(calculate_product(5, 3))', '16', '1. 函数定义正确，包含正确的函数名和参数。2. 正确计算两个数字的和与差的乘积。3. 返回计算结果。4. 运行结果与期望一致。', '用户代码完全正确，没有错误。代码逻辑清晰，符合题目要求。', 57, 4);
INSERT INTO `t_record_question` VALUES (14, 0, 'A', NULL, NULL, '在TensorFlow.js中实现手势识别时，正确的步骤是将图像数据reshape为模型需要的形状并进行归一化处理。这是因为模型通常需要固定大小的输入，并且归一化可以加速模型的收敛。直接使用原始图像数据或仅进行部分预处理都会影响模型的性能。', 58, 4);

-- ----------------------------
-- Table structure for t_record_set
-- ----------------------------
DROP TABLE IF EXISTS `t_record_set`;
CREATE TABLE `t_record_set`  (
  `set_record_id` int(12) NOT NULL AUTO_INCREMENT COMMENT '试题集做题记录id',
  `set_score` int(4) NULL DEFAULT NULL COMMENT '总得分(四舍五入)',
  `subsection_id` int(12) NULL DEFAULT NULL COMMENT '试题小节id',
  `set_id` int(12) NULL DEFAULT NULL,
  `user_id` int(12) NULL DEFAULT NULL COMMENT '用户id',
  `state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '状态（“未完成”、“评卷中”、“已评分”）',
  PRIMARY KEY (`set_record_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_record_set
-- ----------------------------
INSERT INTO `t_record_set` VALUES (4, 60, 47, 10, 2024001, '已评分');
INSERT INTO `t_record_set` VALUES (5, NULL, 50, 7, 2024001, '未完成');

-- ----------------------------
-- Table structure for t_section
-- ----------------------------
DROP TABLE IF EXISTS `t_section`;
CREATE TABLE `t_section`  (
  `section_id` int(12) NOT NULL AUTO_INCREMENT COMMENT '课程章节',
  `section_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '章节标题',
  `section_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '章节描述',
  `course_id` int(12) NULL DEFAULT NULL COMMENT '外键-该章节所属的课程',
  PRIMARY KEY (`section_id`) USING BTREE,
  INDEX `course_id`(`course_id`) USING BTREE,
  CONSTRAINT `t_section_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `t_course` (`course_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 53 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_section
-- ----------------------------
INSERT INTO `t_section` VALUES (1, '测试章节', '这是章节描述^_^', 1);
INSERT INTO `t_section` VALUES (5, '第三章', NULL, 1);
INSERT INTO `t_section` VALUES (7, '不是哥们', '哇哇哇', 1);
INSERT INTO `t_section` VALUES (8, '？', NULL, 2);
INSERT INTO `t_section` VALUES (9, '章节名', NULL, 2);
INSERT INTO `t_section` VALUES (10, '差不多', NULL, 2);
INSERT INTO `t_section` VALUES (12, '损失', NULL, 2);
INSERT INTO `t_section` VALUES (13, 'shawyi', NULL, 7);
INSERT INTO `t_section` VALUES (14, '第一章 TensorFlow基础', '主要讲解张量、矩阵运算等基础概念', 8);
INSERT INTO `t_section` VALUES (15, '123', '123', 8);
INSERT INTO `t_section` VALUES (16, '', '', NULL);
INSERT INTO `t_section` VALUES (17, '', '', NULL);
INSERT INTO `t_section` VALUES (18, '', '', NULL);
INSERT INTO `t_section` VALUES (19, '', '', NULL);
INSERT INTO `t_section` VALUES (40, '模块1：TensorFlow.js基础', '介绍TensorFlow.js的基本架构和核心概念，包括张量操作、变量管理和内存优化。学生将通过实验学习如何配置开发环境和进行基本的张量运算。', 12);
INSERT INTO `t_section` VALUES (41, '模块2：回归任务实战——汽车油耗预测', '通过线性回归模型的实战项目，学习数据预处理、模型训练和性能评估。学生将掌握如何使用TensorFlow.js进行回归任务的全流程开发。', 12);
INSERT INTO `t_section` VALUES (42, '模块3：图像分类任务实战——手写数字识别', '学习卷积神经网络（CNN）的原理和应用，通过手写数字识别项目掌握图像分类任务的开发流程。', 12);
INSERT INTO `t_section` VALUES (43, '模块4：综合项目——手势识别应用', '通过综合项目学习迁移学习和实时数据采集技术，开发一个浏览器端的手势识别应用。', 12);
INSERT INTO `t_section` VALUES (44, '模块1：TensorFlow.js基础', '本模块介绍TensorFlow.js的基本概念和架构，包括其优势、核心概念（如张量、变量、操作等）以及模型构建方式。通过实验，学习者将掌握环境配置、张量操作和内存管理等基础技能，为后续模块的学习打下坚实基础。', 18);
INSERT INTO `t_section` VALUES (45, '模块2：回归任务实战——汽车油耗预测', '本模块通过汽车油耗预测的回归任务实战，帮助学习者掌握线性回归模型的原理和结构设计，以及数据预处理、训练配置和模型评估的全流程。实验部分将涵盖数据加载与可视化、全连接网络构建、训练监控与性能评估等内容。', 18);
INSERT INTO `t_section` VALUES (46, '模块3：图像分类任务实战——手写数字识别', '本模块通过手写数字识别的图像分类任务实战，介绍CNN的原理和结构设计，包括卷积层、池化层和全连接层的作用。实验部分将涵盖MNIST数据集的加载与可视化、CNN模型构建、训练过程分析和模型评估等内容。', 18);
INSERT INTO `t_section` VALUES (47, '模块4：综合项目——手势识别应用', '本模块通过手势识别综合项目，介绍迁移学习的原理和应用，包括预训练模型的复用和实时数据采集。实验部分将涵盖Rock-Paper-Scissors数据集的加载、迁移学习模型的构建和部署，以及浏览器端实时手势预测的实现。', 18);
INSERT INTO `t_section` VALUES (48, '', '', 12);
INSERT INTO `t_section` VALUES (49, '模块1：TensorFlow.js基础', '介绍TensorFlow.js的架构与优势，核心概念如张量操作、变量管理、内存优化等，以及模型构建方式。', 20);
INSERT INTO `t_section` VALUES (50, '模块2：回归任务实战——汽车油耗预测', '学习线性回归模型原理与结构设计，数据预处理方法，训练配置等，并通过实验掌握回归任务的全流程。', 20);
INSERT INTO `t_section` VALUES (51, '模块3：图像分类任务实战——手写数字识别', '学习CNN原理，图像数据预处理方法，多分类任务处理等，并通过实验掌握图像分类任务的全流程。', 20);
INSERT INTO `t_section` VALUES (52, '模块4：综合项目——手势识别应用', '学习迁移学习原理，实时数据采集方法，并通过实验开发一个手势识别应用。', 20);

-- ----------------------------
-- Table structure for t_subsection
-- ----------------------------
DROP TABLE IF EXISTS `t_subsection`;
CREATE TABLE `t_subsection`  (
  `subsection_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '章节小节',
  `subsection_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '小节标题',
  `subsection_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '小节描述',
  `subsection_type` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '内容类型（question_set、video、document）',
  `resource_id` int(12) NULL DEFAULT NULL COMMENT '该内容对应的id',
  `section_id` int(12) NULL DEFAULT NULL COMMENT '外键-该小节所属章节',
  PRIMARY KEY (`subsection_id`) USING BTREE,
  INDEX `section_id`(`section_id`) USING BTREE,
  CONSTRAINT `t_subsection_ibfk_1` FOREIGN KEY (`section_id`) REFERENCES `t_section` (`section_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 54 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_subsection
-- ----------------------------
INSERT INTO `t_subsection` VALUES (8, '测试小节', '小节描述', 'video', 21, 1);
INSERT INTO `t_subsection` VALUES (12, '测试文档', '测试', 'document', 12, 1);
INSERT INTO `t_subsection` VALUES (13, '测试测试修改', '修改了描述', 'video', 20, 1);
INSERT INTO `t_subsection` VALUES (14, '差不多得了', '', 'questions', 5, NULL);
INSERT INTO `t_subsection` VALUES (15, '^_^', '测试冲冲冲', 'document', 10, 1);
INSERT INTO `t_subsection` VALUES (25, '差不多得了嘿嘿嘿', '', 'document', 13, 5);
INSERT INTO `t_subsection` VALUES (26, '真差不多得了', '^_^', 'video', 22, 5);
INSERT INTO `t_subsection` VALUES (31, 'wc', '', 'video', 20, 9);
INSERT INTO `t_subsection` VALUES (32, 'w?', '', 'video', 20, 10);
INSERT INTO `t_subsection` VALUES (33, '这下应该没问题了吧？', '', 'document', 13, 12);
INSERT INTO `t_subsection` VALUES (34, '这是小节', '2312312312312312', 'document', 9, 7);
INSERT INTO `t_subsection` VALUES (35, '试题集小节', '。。。', 'questions', 6, 7);
INSERT INTO `t_subsection` VALUES (36, '../', '../', 'questions', 7, 13);
INSERT INTO `t_subsection` VALUES (38, 'ccccc', '', 'video', 22, 13);
INSERT INTO `t_subsection` VALUES (39, '汉化', '', 'questions', 7, 1);
INSERT INTO `t_subsection` VALUES (40, '课件', '', 'document', 16, 14);
INSERT INTO `t_subsection` VALUES (41, '文档课件', '', 'document', 14, 14);
INSERT INTO `t_subsection` VALUES (43, '测试视频22', '12345678', 'video', 18, 14);
INSERT INTO `t_subsection` VALUES (44, 'ce', 'ce', 'video', 23, 15);
INSERT INTO `t_subsection` VALUES (45, 'ai生成文档', '无测测测', 'video', 23, 40);
INSERT INTO `t_subsection` VALUES (46, '这是一个文档小节', '是是是', 'document', 17, 40);
INSERT INTO `t_subsection` VALUES (47, '这是一个试题小节', '测测测', 'questions', 10, 40);
INSERT INTO `t_subsection` VALUES (48, '这是一个视频小节', '', 'video', 21, 41);
INSERT INTO `t_subsection` VALUES (49, '这是一个ppt', '', 'document', 16, 42);
INSERT INTO `t_subsection` VALUES (50, '这是一个试题小节', '1', 'questions', 7, 41);
INSERT INTO `t_subsection` VALUES (51, '视频', '', 'video', 23, 44);
INSERT INTO `t_subsection` VALUES (52, '123', '', 'video', 23, 48);
INSERT INTO `t_subsection` VALUES (53, '1231', '', 'questions', 10, 51);

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `user_id` int(12) NOT NULL AUTO_INCREMENT COMMENT '用户id/登录账号（学号、职工号)',
  `user_type` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户类型（student、teacher、admin）',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码（初始为password）',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2024005 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (2024000, 'admin', '管理员', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6');
INSERT INTO `t_user` VALUES (2024001, 'teacher', '教师1号', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6');
INSERT INTO `t_user` VALUES (2024002, 'student', '学生1号', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6');
INSERT INTO `t_user` VALUES (2024003, 'student', '学生2号', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6');
INSERT INTO `t_user` VALUES (2024004, 'student', '学生3号', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6');

-- ----------------------------
-- Table structure for t_video
-- ----------------------------
DROP TABLE IF EXISTS `t_video`;
CREATE TABLE `t_video`  (
  `video_id` int(12) NOT NULL AUTO_INCREMENT COMMENT '视频',
  `video_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件名',
  `video_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '视频相对路径',
  `cover_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '封面图片相对路径',
  `created_date` date NULL DEFAULT NULL COMMENT '创建日期',
  `user_id` int(12) NULL DEFAULT NULL COMMENT '所属教师id',
  PRIMARY KEY (`video_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_video
-- ----------------------------
INSERT INTO `t_video` VALUES (10, '央视公益广告《Family》-音乐视频-搜狐视频.mp4', 'feedc487-6241-403d-a87e-014fa5eadd73.mp4', 'f00180e2-169e-4f6a-a624-dad0d04b2d73.jpg', '2025-06-11', 2024001);
INSERT INTO `t_video` VALUES (11, 'test.mp4', '966f2c95-71ae-486c-91fe-6e99a4ef6be6.mp4', 'bc4d6720-9f30-469f-be5c-62424e446fd0.jpg', '2025-06-11', 2024001);
INSERT INTO `t_video` VALUES (12, 'movie.mp4', 'e4634fd5-b225-4dd0-b9ae-312e83bf8ec2.mp4', '52d9ce53-7276-43d6-b1cc-20bfebd374c7.jpg', '2025-06-11', 2024001);
INSERT INTO `t_video` VALUES (13, 'mov_bbb.mp4', 'c23241ce-e498-4b47-9fa6-0ab8d960cf68.mp4', '821f4931-40c2-479c-80f2-45909caf2174.jpg', '2025-06-11', 2024001);
INSERT INTO `t_video` VALUES (16, 'trailer.mp4', '6c31a6c7-ccd1-4bb7-b2e4-87f661fe01f9.mp4', '8d7ddafa-37c8-4b5c-9438-9474086772bc.jpg', '2025-06-11', 2024001);
INSERT INTO `t_video` VALUES (17, 'movie.mp4', 'efeadb34-ef53-449f-9ad1-5db7d4196920.mp4', 'bafb1fdc-ef73-4cd1-a9ab-1cbed495417e.jpg', '2025-06-11', 2024001);
INSERT INTO `t_video` VALUES (18, 'mov_bbb.mp4', '3fc97a94-917e-402c-b7ad-586f6be42ee6.mp4', 'e403b65b-6482-4e5e-a101-7a83fda6f51c.jpg', '2025-06-11', 2024001);
INSERT INTO `t_video` VALUES (20, 'test.mp4', '3018fc96-bfeb-478a-8042-9dc2706a3d38.mp4', '8afa60a9-cdb9-4b28-bc4e-eb642d67a4af.jpg', '2025-06-18', 2024001);
INSERT INTO `t_video` VALUES (21, 'test.mp4', 'a356902d-94b7-4669-8b40-0d6be73dd3b8.mp4', '1bfc1ec7-9c67-4b4e-be31-28ffa5ce6ec5.png', '2025-06-18', 2024001);
INSERT INTO `t_video` VALUES (22, 'test.mp4', '64631daf-b3f0-462a-9f3b-3fd813c2e5ce.mp4', '9e17a7e6-a54e-4585-a3a3-f2e5f72834db.png', '2025-06-19', 2024001);
INSERT INTO `t_video` VALUES (23, 'test.mp4', 'f4d37261-8592-4ba4-8933-20a8a6fadc0e.mp4', 'fcdb5782-7f95-4cb5-9b5a-fa619c49d20d.jpg', '2025-06-23', 2024001);

-- ----------------------------
-- View structure for v_student
-- ----------------------------
DROP VIEW IF EXISTS `v_student`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_student` AS select `t_user`.`user_id` AS `user_id`,`t_user`.`user_name` AS `user_name`,`t_user`.`password` AS `password` from `t_user` where (`t_user`.`user_type` = 'student');

-- ----------------------------
-- View structure for v_teacher
-- ----------------------------
DROP VIEW IF EXISTS `v_teacher`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_teacher` AS select `t_user`.`user_id` AS `user_id`,`t_user`.`user_name` AS `user_name`,`t_user`.`password` AS `password` from `t_user` where (`t_user`.`user_type` = 'teacher');

SET FOREIGN_KEY_CHECKS = 1;
