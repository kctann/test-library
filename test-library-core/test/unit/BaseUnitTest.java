/*
[001][專案結構建立]
Unit Test基礎類別
提供單元測試的基礎設定和共用功能
*/
package com.jamestann.test.library.unit;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit Test基礎類別
 * 所有單元測試應繼承此類別
 */
@ExtendWith(MockitoExtension.class)
public abstract class BaseUnitTest {
    // 共用的測試設定和工具方法可在此定義
}