/*
[001][專案結構建立]
Library Auto Configuration測試類別
測試Library的自動配置功能
*/
package com.jamestann.test.library.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class TestLibraryAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(TestLibraryAutoConfiguration.class));

    @Test
    void testAutoConfigurationEnabled() {
        contextRunner
            .withPropertyValues("test.library.enabled=true")
            .run(context -> {
                assertThat(context).hasSingleBean(TestLibraryManager.class);
                assertThat(context).hasSingleBean(TestLibraryProperties.class);
            });
    }

    @Test
    void testAutoConfigurationDisabled() {
        contextRunner
            .withPropertyValues("test.library.enabled=false")
            .run(context -> {
                assertThat(context).doesNotHaveBean(TestLibraryManager.class);
            });
    }

    @Test
    void testAutoConfigurationDefaultEnabled() {
        contextRunner
            .run(context -> {
                assertThat(context).hasSingleBean(TestLibraryManager.class);
                TestLibraryManager manager = context.getBean(TestLibraryManager.class);
                assertThat(manager.isEnabled()).isTrue();
            });
    }
}