# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Important Instructions
- 在任何開發之前，先將你設計好的流程告訴我，你的task，你開發的順序，以及你會寫哪些function，用function description描述那個function的功能來自哪個feature(附帶編號)與input,output，並且詢問我是否按照這個todo list開發，如果我說no的話，請依照我的指示做調整，再重複這個步驟
    - 得到同意前，不要做任何執行
    - 得到同意後，將TODO.md寫入`/instructions`底下
        - 如果是全新的class將class description補充於該class comment中,comment格式,ex:
          ```
            /*
              [featureNumber][featureName]
              功能說明
            /*
          ```
        - 如果是全新的function將function description補充於該function comment中,comment格式,ex:
          ```
            /*
              [featureNumber][featureName]
              input
              output
              功能說明
            /*
          ```
        - 如果是要修改的function，在原本function description下方補充這次的update來自哪個`[functionNumber][functionName]-原因`,ex:
          ```
           /*
              [featureNumber][featureName]
              input
              output
              功能說明
           /*
           /*
              Update History:
           /*
          ```
        - 如果任一function中出現了需要列舉TODO的項目，則明確寫出depends on接下來的哪個規劃,ex:
          ```
           /*
              TODO:
              [004][M1][feature4]classA#functionA depends on [004][M3][feature4]
           /*
           /*
              Update History:
           /*
          ```
        - 如果TODO.md寫完被確認同意之後，要將Update code/New code，補寫回這次的[featureNumber][featureName]-SD.md中
        - 如果TODO.md寫完被確認同意之後，Update History補寫回被改動的[featureNumber][featureName]-SD.md中
    - 如果TODO.md內容做完之後，要將該次所有更新Append到`UPDATE-HISTORY.md`底下
    - 如果程式中尚未完成的TODO項目完成了，要記得去TODO-LIST.md中移除項目
- 如果該次開發有做UnitTest，將UnitTest保留在`/test`中

## Project Overview
這個Project的主要目的是在測試如何打造適合spring-boot共用Library。此專案目標是一個高擴展性、高重用性並且具有Matrix Test,Unit Test,Integration Test,Performance Test的Actuator。
這個Project同時也是在測試Library開發的Pilot原型，所以凡是與架構、測試、版本控制等相關的討論議題，都不要綁定這個Library的功能設計，反而應該要更廣泛的思考，避免Pilot只適用於目前的功能。

## Development Commands

### Build and Run

- Build the project: `./mvnw clean compile`
- Run the application: `./mvnw spring-boot:run`
- Package the application: `./mvnw clean package`
- Run tests: `./mvnw test`
- Run a specific test: `./mvnw test -Dtest=ClassName`

### Development Tools
- The application uses Spring Boot DevTools for automatic restart during development

[//]: # (- **IMPORTANT**: Always use Maven wrapper &#40;`./mvnw`&#41; instead of `mvn` as Maven is not installed globally in this environment)

## Architecture and Structure
提供功能的test-library-core(jar)以及驗證功能的test-library-demo(spring-boot專案)

### Core Components

### Library Requirements
Library要具備相容性，所以需要可以測試不同的Java版本(11,17,21)、同時需要測試不同的spring-boot版本(三個主要的LTS)。
故test-library-core的pom.xml需要有可以從使用者注入版本的彈性
故test-library-demo的pom.xml要可以採用不同的java,spring-boot的版本

### Key Technologies
spring-boot Actuator,Lombok,Github


### File Constructure
- `/instruction`:
    - `[編號][功能]-SD.md`、`[編號][功能]-TODO.md`、`[編號][功能][M]-TODO.md`
    - `UPDATE-HISTORY.md`

### featureNumber-featureName-SD.md
- 以中文書寫
- featureName
- feature description
    - Given: The initial state or context of the system.
    - When: The specific action the user performs.
    - Then: The expected, measurable outcome of that action.
- Milestones:當一個任務太大的時候，將他break down成多個Milestones，撰寫TODO時，每個Milestone各自撰寫一個TODO.md，命名範例:`001-M1-feature1-TODO.md`
- Update code/New code History(append):實際上改動的程式，如果是新程式則寫上本身編號與資訊
    - ex:`NEW-[004][抓取html資訊]BrowserSimulationService#getContentWithSelenium`
- Update code/New code History(append):如果是原本就寫好的程式，需要將他在Comment中的資訊補上
    - ex:`UPDATE-[001][抓取html資訊]BrowserSimulationService#copyFromContent`
- Update History(append)
    - 如果因為其他feature而更動，依照日期時間順序寫下被哪個feature更動，調整內容簡述，更動原因，以及時間戳記

### featureNumber-featureName-TODO.mdTODO
- 以中文書寫
- featureName
- 條列實作項目
- 條列受影響的程式名稱
    - ex:`[001][抓取html資訊]BrowserSimulationService#getContentWithSelenium`
- TODO list:順序條列要完成的工作
    - function要有簡單描述input,output,檔案名稱
    - 如果有影響到其他功能，在該步驟明確標示，調整內容簡述，更動原因
        - ex:`[001][抓取html資訊]BrowserSimulationService#getContentWithSelenium`因調整來源所以更換了XXX
### UPDATE-HISTORY.md
- 以中文書寫
- 條列哪個功能調整了哪些程式
- ex:
  ```
  [004][feature4]調整[001][feature1]classA#functionA:因應更動Entity....
  [004][feature4]調整[003][feature3]classB#functionB:因應更動設計模式.....
  ```

### TODO-LIST.md
- 以中文書寫
- 在每次完成的項目中，不管是function、class或者是其他項目中如果出現了TODO，代表這是在後期規劃(Milestones可能分成好多步驟)中才會更動的項目，請詳細將他填寫在TODO-LIST.md中，填寫規範如下
  ```
  [004][M1][feature4]classA#functionA depends on [004][M3][feature4]
  [004][M1][feature4]classB#functionB depends on [004][M5][feature4]
  ```
- 如果程式中尚未完成的TODO項目完成了，要根據該TODO項目中留下的標記，來這邊移除


### Package Structure
- `com.jamestann.test.library`

### Key Business Structure
- 透過Actuator讓DevOps團隊蒐集相關Metrics資訊製作可視化Dashboard
- 透過打造AOP比較未來打造之Library效能議題
  - 根據Library名稱蒐集相關執行時間數據
  - 有Library情況所花時間
  - 無Library情況所花時間
- 透過AOP進行Log標準化之輸出



