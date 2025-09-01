/*
[001][專案結構建立]
Integration Test基礎類別
提供整合測試的基礎設定和共用功能
*/
package com.jamestann.test.library.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration Test基礎類別
 * 所有整合測試應繼承此類別
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    // 共用的整合測試設定和工具方法可在此定義
}