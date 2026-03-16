package com.microservice_example.report_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig{

    @Bean(name = "reportExecutor")
    public Executor reportExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4); // 4 threads will handle the tasks immediately (1 task per thread)
        executor.setMaxPoolSize(8); // if queue is full, new thread will be created to handle this task if thread current count < max thread
        executor.setQueueCapacity(50); // if 4 threads are busy (4 threads are working 4 tasks), new task will be in queue
        executor.setThreadNamePrefix("report-executor-");
        executor.initialize();
        return executor;
    }

}
