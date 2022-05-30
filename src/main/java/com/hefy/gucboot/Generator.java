package com.hefy.gucboot;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * @Create by hefy
 * @Date 2022/5/17 18:22
 */
public class Generator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://192.168.249.40:3306/dc1", "root", "Aa123456")
                .globalConfig(builder -> {
                    builder.author("baomidou") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("D:\\myworkspace\\plugins\\generator"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.hefy") // 设置父包名
                            .moduleName("gucboot") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.mapper, "D:\\myworkspace\\plugins\\generator")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("stock_log"); // 设置需要生成的表名
                            // 设置过滤表前缀
                }) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
