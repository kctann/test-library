/*
[001][專案結構建立]
Demo應用程式主類別
用於測試Test Library功能的Spring Boot Web應用程式
*/
package com.jamestann.test.library.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestLibraryDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestLibraryDemoApplication.class, args);
    }
}