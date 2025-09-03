/*
[003][M1][Metrics AOP切面]
Library Metrics Aspect測試類別
測試 AOP 切面方法級監控功能（需要 Spring Context）
*/
package com.jamestann.test.library.actuator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import io.micrometer.core.annotation.Timed;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LibraryMetricsAspect.
 * <p>
 * Tests AOP aspect functionality for method-level monitoring
 * using AspectJ proxy factory without full Spring context.
 * 
 * @author James Tann
 * @since 1.0.0
 */
/*
[003][M1][Metrics AOP切面]
LibraryMetricsAspect 的單元測試
使用 AspectJ 代理工廠測試方法級監控的 AOP 切面功能
*/
@ExtendWith(MockitoExtension.class)
class LibraryMetricsAspectTest {

    @Mock
    private LibrarySLICollector sliCollector;
    
    @Mock 
    private MonitoringConfiguration monitoringConfig;
    
    private LibraryMetricsAspect aspect;

    /**
     * Sets up test fixtures before each test.
     */
    /*
    [003][M1][Metrics AOP切面]
    input: void
    output: void
    在每個測試前設置測試裝置
    */
    @BeforeEach
    void setUp() {
        aspect = new LibraryMetricsAspect(sliCollector, monitoringConfig);
    }

    /**
     * Tests monitoring of @RestController methods.
     */
    /*
    [003][M1][Metrics AOP切面]
    input: void
    output: void
    測試 @RestController 方法的監控
    */
    @Test
    void testRestControllerMethodMonitoring() {
        TestRestController target = new TestRestController();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.addAspect(aspect);
        TestRestController proxy = factory.getProxy();
        
        String result = proxy.getUser("123");
        
        assertThat(result).isEqualTo("User 123");
        verify(sliCollector).recordMethodExecution(
            eq("com.jamestann.test.library.actuator.LibraryMetricsAspectTest$TestRestController"),
            eq("getUser"),
            any(Duration.class),
            isNull()
        );
    }

    /**
     * Tests monitoring of @Controller methods.
     */
    /*
    [003][M1][Metrics AOP切面]
    input: void
    output: void
    測試 @Controller 方法的監控
    */
    @Test
    void testControllerMethodMonitoring() {
        TestController target = new TestController();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.addAspect(aspect);
        TestController proxy = factory.getProxy();
        
        String result = proxy.showUser("123");
        
        assertThat(result).isEqualTo("user-view");
        verify(sliCollector).recordMethodExecution(
            eq("com.jamestann.test.library.actuator.LibraryMetricsAspectTest$TestController"),
            eq("showUser"),
            any(Duration.class),
            isNull()
        );
    }

    /**
     * Tests monitoring of @Service methods.
     */
    /*
    [003][M1][Metrics AOP切面]
    input: void
    output: void
    測試 @Service 方法的監控
    */
    @Test
    void testServiceMethodMonitoring() {
        TestService target = new TestService();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.addAspect(aspect);
        TestService proxy = factory.getProxy();
        
        String result = proxy.processUser("123");
        
        assertThat(result).isEqualTo("Processed user 123");
        verify(sliCollector).recordMethodExecution(
            eq("com.jamestann.test.library.actuator.LibraryMetricsAspectTest$TestService"),
            eq("processUser"),
            any(Duration.class),
            isNull()
        );
    }

    /**
     * Tests monitoring of @Repository methods.
     */
    /*
    [003][M1][Metrics AOP切面]
    input: void
    output: void
    測試 @Repository 方法的監控
    */
    @Test
    void testRepositoryMethodMonitoring() {
        TestRepository target = new TestRepository();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.addAspect(aspect);
        TestRepository proxy = factory.getProxy();
        
        String result = proxy.findUser("123");
        
        assertThat(result).isEqualTo("User data for 123");
        verify(sliCollector).recordMethodExecution(
            eq("com.jamestann.test.library.actuator.LibraryMetricsAspectTest$TestRepository"),
            eq("findUser"),
            any(Duration.class),
            isNull()
        );
    }

    /**
     * Tests monitoring of @Timed annotated methods.
     */
    /*
    [003][M1][Metrics AOP切面]
    input: void
    output: void
    測試 @Timed 註解方法的監控
    */
    @Test
    void testTimedMethodMonitoring() {
        TestTimedClass target = new TestTimedClass();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.addAspect(aspect);
        TestTimedClass proxy = factory.getProxy();
        
        String result = proxy.timedMethod("test");
        
        assertThat(result).isEqualTo("Timed result: test");
        verify(sliCollector).recordMethodExecution(
            eq("com.jamestann.test.library.actuator.LibraryMetricsAspectTest$TestTimedClass"),
            eq("timedMethod"),
            any(Duration.class),
            isNull()
        );
    }

    /**
     * Tests exception handling in monitored methods.
     */
    /*
    [003][M1][Metrics AOP切面]
    input: void
    output: void
    測試被監控方法中的異常處理
    */
    @Test
    void testExceptionHandlingInMonitoredMethods() {
        TestService target = new TestService();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.addAspect(aspect);
        TestService proxy = factory.getProxy();
        
        assertThatThrownBy(() -> proxy.throwException("error"))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Test error: error");
        
        verify(sliCollector).recordMethodExecution(
            eq("com.jamestann.test.library.actuator.LibraryMetricsAspectTest$TestService"),
            eq("throwException"),
            any(Duration.class),
            any(RuntimeException.class)
        );
    }

    /**
     * Tests that execution time is measured correctly.
     */
    /*
    [003][M1][Metrics AOP切面]
    input: void
    output: void
    測試執行時間測量正確
    */
    @Test
    void testExecutionTimeMeasurement() {
        TestService target = new TestService();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.addAspect(aspect);
        TestService proxy = factory.getProxy();
        
        proxy.slowMethod();
        
        verify(sliCollector).recordMethodExecution(
            anyString(),
            eq("slowMethod"),
            argThat(duration -> duration.toMillis() >= 50), // Should be at least 50ms
            isNull()
        );
    }

    /**
     * Tests that metrics collection errors don't affect method execution.
     */
    /*
    [003][M1][Metrics AOP切面]
    input: void
    output: void
    測試指標收集錯誤不影響方法執行
    */
    @Test
    void testMetricsCollectionErrorHandling() {
        doThrow(new RuntimeException("Metrics error"))
            .when(sliCollector).recordMethodExecution(anyString(), anyString(), any(Duration.class), any());
        
        TestService target = new TestService();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.addAspect(aspect);
        TestService proxy = factory.getProxy();
        
        // Should not throw exception even if metrics collection fails
        String result = proxy.processUser("123");
        assertThat(result).isEqualTo("Processed user 123");
    }

    /**
     * Tests that non-annotated classes are not monitored.
     */
    /*
    [003][M1][Metrics AOP切面]
    input: void
    output: void
    測試未註解的類別不被監控
    */
    @Test
    void testNonAnnotatedClassesNotMonitored() {
        TestPlainClass target = new TestPlainClass();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.addAspect(aspect);
        TestPlainClass proxy = factory.getProxy();
        
        String result = proxy.plainMethod("test");
        
        assertThat(result).isEqualTo("Plain result: test");
        verify(sliCollector, never()).recordMethodExecution(anyString(), anyString(), any(Duration.class), any());
    }

    // Test classes for AOP testing
    
    @RestController
    static class TestRestController {
        public String getUser(String id) {
            return "User " + id;
        }
    }
    
    @Controller
    static class TestController {
        public String showUser(String id) {
            return "user-view";
        }
    }
    
    @Service
    static class TestService {
        public String processUser(String id) {
            return "Processed user " + id;
        }
        
        public String throwException(String message) {
            throw new RuntimeException("Test error: " + message);
        }
        
        public void slowMethod() {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    @Repository
    static class TestRepository {
        public String findUser(String id) {
            return "User data for " + id;
        }
    }
    
    static class TestTimedClass {
        @Timed
        public String timedMethod(String input) {
            return "Timed result: " + input;
        }
    }
    
    static class TestPlainClass {
        public String plainMethod(String input) {
            return "Plain result: " + input;
        }
    }
}