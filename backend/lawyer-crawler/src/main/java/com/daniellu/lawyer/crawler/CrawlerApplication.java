package com.daniellu.lawyer.crawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.retry.annotation.EnableRetry;

/**
 * 爬虫应用程序主启动类
 *
 * @author Daniel Lu
 * @since 2026-01-11
 */
@SpringBootApplication
@EnableRetry
@ComponentScan(basePackages = {
        "com.daniellu.lawyer.crawler",
        "com.daniellu.lawyer.springcommon"
})
public class CrawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrawlerApplication.class, args);
    }
}
