package com.rivigo.riconet.core.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Created by ashfakh on 12/02/19.
 */

@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {
    public static final int BATCH_SIZE = 1000;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(BATCH_SIZE);
        executor.setThreadNamePrefix("AyncThread-");
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
//
//    @Bean(name = "asyncTaskExecutor")
//    public Executor threadPoolTaskExecutor() {
//        return new ThreadPoolTaskExecutor();
//    }
}
