/*
[001][專案結構建立]
Matrix Test基礎類別
提供矩陣測試的基礎設定，用於測試多種參數組合
*/
package com.jamestann.test.library.matrix;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * Matrix Test基礎類別
 * 所有矩陣測試應繼承此類別
 * 用於測試不同參數組合下的系統行為
 */
@SpringJUnitConfig
public abstract class BaseMatrixTest {
    // 共用的矩陣測試設定和參數化測試工具方法可在此定義
}