/*
[003][M1][Metrics AOP切面]
Library AOP Integration測試類別
測試 Spring AOP 環境中的方法級監控整合
*/
package com.jamestann.test.library.actuator;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.bind.annotation.RestController;
import io.micrometer.core.annotation.Timed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for LibraryMetricsAspect with Spring AOP.
 * <p>
 * Tests the actual AOP functionality in a Spring context with
 * real annotated beans and method interception.
 * 
 * @author James Tann
 * @since 1.0.0
 */
/*
[003][M1][Metrics AOP切面]
LibraryMetricsAspect 與 Spring AOP 的整合測試
測試 Spring 上下文中的實際 AOP 功能和方法攔截
*/
@SpringBootTest(classes = {
    LibraryActuatorAutoConfiguration.class,
    ActuatorTestConfiguration.class,
    LibraryAopIntegrationTest.TestService.class,
    LibraryAopIntegrationTest.TestController.class,
    LibraryAopIntegrationTest.TestRepository.class,
    LibraryAopIntegrationTest.TestRestController.class,
    LibraryAopIntegrationTest.TestTimedService.class
})
@TestPropertySource(properties = {
    "test-library.actuator.enabled=true",
    "test-library.actuator.metrics.enabled=true",
    "test-library.actuator.aop.enabled=true"
})
@EnableAspectJAutoProxy
class LibraryAopIntegrationTest {

    @Autowired
    private MeterRegistry meterRegistry;
    
    @Autowired
    private TestService testService;
    
    @Autowired
    private TestController testController;
    
    @Autowired
    private TestRepository testRepository;
    
    @Autowired
    private TestRestController testRestController;
    
    @Autowired
    private TestTimedService testTimedService;

    /**
     * Tests AOP interception of @Service methods.
     */
    /*
    [003][M1][Metrics AOP切面]
    input: void
    output: void
    測試 @Service 方法的 AOP 攔截
    */
    @Test
    void testServiceMethodInterception() {
        String result = testService.processData("test-data");
        assertThat(result).isEqualTo("Processed: test-data");
        
        // Verify method execution metric was recorded
        assertThat(meterRegistry.find("application.method.duration")
                               .tag("class", "TestService")
                               .tag("method", "processData")
                               .tag("status", "success")
                               .timer())
            .isNotNull();
    }

    /**
     * Tests AOP interception of @Controller methods.
     */
    /*
    [003][M1][Metrics AOP切面]
    input: void
    output: void
    測試 @Controller 方法的 AOP 攔截
    */
    @Test
    void testControllerMethodInterception() {
        String result = testController.handleRequest("test-request");
        assertThat(result).isEqualTo("Handled: test-request");
        
        // Verify method execution metric was recorded
        assertThat(meterRegistry.find("application.method.duration")
                               .tag("class", "TestController")
                               .tag("method", "handleRequest")
                               .timer())
            .isNotNull();
    }

    /**
     * Tests AOP interception of @RestController methods.
     */
    /*
    [003][M1][Metrics AOP切面]
    input: void
    output: void
    測試 @RestController 方法的 AOP 攔截
    */
    @Test
    void testRestControllerMethodInterception() {
        String result = testRestController.getData("123");
        assertThat(result).isEqualTo("Data for ID: 123");
        
        // Verify method execution metric was recorded
        assertThat(meterRegistry.find("application.method.duration")
                               .tag("class", "TestRestController")
                               .tag("method", "getData")
                               .timer())
            .isNotNull();
    }

    /**
     * Tests AOP interception of @Repository methods.
     */
    /*
    [003][M1][Metrics AOP切面]
    input: void
    output: void
    測試 @Repository 方法的 AOP 攔截
    */
    @Test
    void testRepositoryMethodInterception() {
        String result = testRepository.findById("456");
        assertThat(result).isEqualTo("Entity with ID: 456");
        
        // Verify method execution metric was recorded
        assertThat(meterRegistry.find("application.method.duration")
                               .tag("class", "TestRepository")
                               .tag("method", "findById")
                               .timer())
            .isNotNull();
    }

    /**
     * Tests AOP interception of @Timed methods.
     */
    /*
    [003][M1][Metrics AOP切面]
    input: void
    output: void
    測試 @Timed 方法的 AOP 攔截
    */
    @Test
    void testTimedMethodInterception() {
        String result = testTimedService.timedOperation("test");
        assertThat(result).isEqualTo("Timed result: test");
        
        // Verify method execution metric was recorded
        assertThat(meterRegistry.find("application.method.duration")
                               .tag("class", "TestTimedService")
                               .tag("method", "timedOperation")
                               .timer())
            .isNotNull();
    }

    /**
     * Tests error metrics recording for method exceptions.
     */
    /*
    [003][M1][Metrics AOP切面]
    input: void
    output: void
    測試方法異常的錯誤指標記錄
    */
    @Test
    void testErrorMetricsForMethodExceptions() {
        assertThatThrownBy(() -> testService.methodThatThrows("error"))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Service error: error");
        
        // Verify error metric was recorded
        assertThat(meterRegistry.find("application.method.duration")
                               .tag("status", "error")
                               .timer())
            .isNotNull();
            
        assertThat(meterRegistry.find("application.method.errors")
                               .tag("exception", "RuntimeException")
                               .counter())
            .isNotNull();
    }

    /**
     * Tests that method execution times are actually measured.
     */
    /*
    [003][M1][Metrics AOP切面]
    input: void
    output: void
    測試實際測量方法執行時間
    */
    @Test
    void testMethodExecutionTimeMeasurement() {
        testService.slowMethod();
        
        // Find the timer and verify it recorded execution time
        io.micrometer.core.instrument.Timer timer = meterRegistry.find("application.method.duration")
                                                                 .tag("method", "slowMethod")
                                                                 .timer();
        
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(1);
        assertThat(timer.totalTime(java.util.concurrent.TimeUnit.MILLISECONDS)).isGreaterThan(45);
    }

    // Test service classes
    
    @Service
    static class TestService {
        public String processData(String data) {
            return "Processed: " + data;
        }
        
        public String methodThatThrows(String message) {
            throw new RuntimeException("Service error: " + message);
        }
        
        public void slowMethod() {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    @Controller
    static class TestController {
        public String handleRequest(String request) {
            return "Handled: " + request;
        }
    }
    
    @RestController
    static class TestRestController {
        public String getData(String id) {
            return "Data for ID: " + id;
        }
    }
    
    @Repository
    static class TestRepository {
        public String findById(String id) {
            return "Entity with ID: " + id;
        }
    }
    
    @Service
    static class TestTimedService {
        @Timed
        public String timedOperation(String input) {
            return "Timed result: " + input;
        }
    }
}

/**
 * Test for AOP disabled configuration.
 */
/*
[003][M1][Metrics AOP切面]
AOP 禁用配置的測試
*/
@SpringBootTest(classes = {
    LibraryActuatorAutoConfiguration.class,
    ActuatorTestConfiguration.class,
    LibraryAopDisabledIntegrationTest.TestService.class
})
@TestPropertySource(properties = {
    "test-library.actuator.enabled=true",
    "test-library.actuator.metrics.enabled=true",
    "test-library.actuator.aop.enabled=false"
})
@EnableAspectJAutoProxy
class LibraryAopDisabledIntegrationTest {

    @Autowired
    private MeterRegistry meterRegistry;
    
    @Autowired
    private TestService testService;

    /**
     * Tests that method interception doesn't occur when AOP is disabled.
     */
    /*
    [003][M1][Metrics AOP切面]
    input: void
    output: void
    測試 AOP 禁用時不發生方法攔截
    */
    @Test
    void testNoMethodInterceptionWhenAopDisabled() {
        String result = testService.processData("test");
        assertThat(result).isEqualTo("Processed: test");
        
        // Verify no method metrics were recorded
        assertThat(meterRegistry.find("application.method.duration")
                               .timers())
            .isEmpty();
    }

    @Service
    static class TestService {
        public String processData(String data) {
            return "Processed: " + data;
        }
    }
}