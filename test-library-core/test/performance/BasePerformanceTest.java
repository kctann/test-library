/*
[001][專案結構建立]
Performance Test基礎類別
提供效能測試的基礎設定和JMH benchmark功能
*/
package com.jamestann.test.library.performance;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Scope;

import java.util.concurrent.TimeUnit;

/**
 * Performance Test基礎類別
 * 所有效能測試(Benchmark)應繼承此類別
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public abstract class BasePerformanceTest {
    // 共用的效能測試設定和工具方法可在此定義
}