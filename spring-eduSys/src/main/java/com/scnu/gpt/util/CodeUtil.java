package com.scnu.gpt.util;

import com.scnu.gpt.pojo.ApiResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//在线运行代码
@Component
public class CodeUtil {
    //本地执行代码（非常不安全，实际要创建沙箱环境运行代码）
    public ApiResponse<String> runCode(String code) {
        Path tempDirPath = Paths.get("codes");
        Path tempFilePath = null;
        try {
            if (!Files.exists(tempDirPath)) {
                Files.createDirectories(tempDirPath);
            }
            tempFilePath = Files.createTempFile(tempDirPath, "python_", ".py");

            Files.write(tempFilePath, code.getBytes(StandardCharsets.UTF_8));
            // 程序文件执行
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("python", tempFilePath.toAbsolutePath().toString());
            Map<String, String> env = processBuilder.environment();
            env.put("PYTHONIOENCODING", "utf-8");  // 强制Python输出使用UTF-8

            // 启动进程并处理流
            Process process = processBuilder.start();

            // 读取输出流
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).replace("\r\n", "\n");//替换换行符
            String error = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8).replace("\r\n", "\n");
            if (!process.waitFor(3, TimeUnit.SECONDS)) {
                process.destroy();
                return new ApiResponse<>("501","代码执行超时",error);
            } else if (error.isEmpty()) {
                return new ApiResponse<>("200","代码执行成功",output);
            } else {
                return new ApiResponse<>("501","代码执行出错",error);
            }
        } catch (Exception e) {
            return new ApiResponse<>("500","程序出错",e.getMessage());
        } finally {
            // 删除临时文件
            if (tempFilePath != null) {
                try {
                    Files.deleteIfExists(tempFilePath);
                } catch (IOException e) {
                    System.err.println("删除临时文件失败: " + e.getMessage());
                }
            }
        }
    }
}
