package com.example.pointsystem;

import org.redisson.api.RedissonClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * 애플리케이션 부트스트랩이 정상 작동하는지 확인합니다.
 */
@SpringBootTest
class PointSystemApplicationTests {

    @MockitoBean
    private RedissonClient redissonClient;

    @Test
    // 의도: 기본 스프링 컨텍스트가 정상 부팅되는지 확인한다.
    void contextLoads() {
    }

}
