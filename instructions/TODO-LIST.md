# TODO-LIST.md

## 因依賴關係被卡住的 TODO 項目

### 延後至 M3 的項目 ⏸️

- [002][M1][依賴調整]JavaVersionCompatibilityTest更新 **depends on [002][M3][智能CI Workflows設計]**
  - 真正的相容性測試需要CI Matrix Strategy
  - 程式碼層級的版本字串判斷沒有實際意義
  - 應該透過不同環境執行Integration Test觀察通過率

- [002][M1][依賴調整]Maven Compiler Plugin多版本配置 **depends on [002][M3][智能CI Workflows設計]**
  - Maven 不能真正切換 Java 版本編譯
  - `maven.compiler.source/target` 只是設定編譯目標，不是切換JDK
  - 只有CI層級才能做真正的多版本測試

- [002][M1][依賴調整]測試多版本編譯相容性 **depends on [002][M3][智能CI Workflows設計]**
  - `./mvnw compile -Pjava11` 沒有意義，還是用當前JVM
  - 只是改變compiler target，不是真實的相容性測試
  - 需要GitHub Actions Matrix: `java: [11, 17, 21]`

### 延後至 M2 的項目 ⏸️

- [002][M1][依賴調整]本地驗證測試建立 **depends on [002][M2][Demo模組獨立化設計]**
  - Demo模組本身就是"本地驗證專案"
  - Demo的Integration Test就是"模擬用戶使用情境"  
  - 不需要在Core模組重複造輪子
  - 應該整合到M2的LibraryUserScenarioIntegrationTest中

## 依賴關係說明

- **多版本測試相關**: 需要等待M3的GitHub Actions Matrix設計完成後，才能實作有意義的多版本相容性測試
- **本地驗證測試**: 需要等待M2的Demo模組設計完成，因為Demo本身就是最好的驗證環境