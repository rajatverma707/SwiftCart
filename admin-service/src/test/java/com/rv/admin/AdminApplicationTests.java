package com.rv.admin;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false",
        "spring.cloud.discovery.enabled=false"
})
class AdminApplicationTests {

    @Test
    void contextLoads() {
    }

}
