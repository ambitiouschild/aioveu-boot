package com.aioveu.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * @Description: TODO 应用启动类
 * @Param:  null
 * @Return: null
 * @Author:
 * @Email:
 * @Date:  2025-06-11 15:52:28
 * @LastEditors:
 * @LastEditTime: 2025-06-11 15:52:28
*/

@SpringBootApplication
@ConfigurationPropertiesScan // 开启配置属性绑定
public class AioveuBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(AioveuBootApplication.class, args);
    }
}