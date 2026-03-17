package com.example.pointsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 포인트 시스템 애플리케이션의 진입점을 제공합니다.
 */
@SpringBootApplication
@EnableCaching
public class PointSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PointSystemApplication.class, args);
    }

}
