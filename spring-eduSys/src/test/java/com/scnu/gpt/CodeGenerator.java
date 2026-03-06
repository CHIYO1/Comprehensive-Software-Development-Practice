package com.scnu.gpt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;
//自动生成实体类、MVC基础架构及注解
public class CodeGenerator {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/gpt?useSSL=false";
        String username = "root";
        String password = "123456";
        String baseDir = System.getProperty("user.dir") + "/src/main/java";

        FastAutoGenerator.create(url, username, password)
                // 全局配置
                .globalConfig(builder -> {
                    builder.author("ldw") // 作者名
                            .outputDir(baseDir) // 输出目录
                            .disableOpenDir() // 生成后不打开目录
                            .commentDate("yyyy-MM-dd"); // 注释日期格式
                })
                // 包配置
                .packageConfig(builder -> {
                    builder.parent("com.scnu.gpt") // 父包名
                            .entity("entity") // 实体类包名
                            .service("service") // Service包名
                            .serviceImpl("service.impl") // Service实现类包名
                            .controller("controller") // Controller包名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, baseDir + "/mapper")); // XML位置
                })
                // 策略配置
                .strategyConfig(builder -> {
//                    builder.addInclude("t_user","t_course","t_section","t_subsection","t_question","t_question_set","t_question_choice",
//                                    "t_video","t_document") // 要生成的表名
                      builder.addInclude("t_record_set","t_record_question") // 要生成的表名
                            .addTablePrefix("t_") // 表前缀过滤

                            // Entity策略
                            .entityBuilder()
                            .enableLombok() // 启用Lombok
                            .naming(NamingStrategy.underline_to_camel) // 下划线转驼峰
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .idType(IdType.AUTO) // 主键策略
                            .formatFileName("%s") // 文件名称格式（如User）
                            .disableSerialVersionUID() // 禁用serialVersionUID

                            // Controller策略
                            .controllerBuilder()
                            .enableRestStyle() // 启用@RestController
                            .formatFileName("%sController")

                            // Service策略
                            .serviceBuilder()
                            .formatServiceFileName("I%sService") // 接口名格式
                            .formatServiceImplFileName("%sServiceImpl"); // 实现类名格式
                })
                // 模板配置（使用默认模板）
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}