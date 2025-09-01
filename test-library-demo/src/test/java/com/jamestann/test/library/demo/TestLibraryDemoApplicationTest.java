/*
[001][專案結構建立]
Demo應用程式測試類別
測試Demo應用程式和Library集成功能
*/
package com.jamestann.test.library.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TestLibraryDemoApplicationTest {

    @Test
    void contextLoads() {
        // 測試Spring Boot應用程式上下文是否正確載入
        // 這會驗證Library的Auto Configuration是否正常工作
    }
}