package com.gendml.kutang.Config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @author Зөндөөa
 * @create 2022-04-17 21:24
 */
@Configuration
@EnableAsync
@ComponentScan("com.gendml.kutang.Config")
public class ThreadConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        //线程池中的线程的名称前缀
        threadPoolTaskExecutor.setThreadNamePrefix("SpringBoot线程池的前缀-");
        //线程池的核心线程数大小
        threadPoolTaskExecutor.setCorePoolSize(4);
        //线程池的最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(8);
        //等待队列的大小
        threadPoolTaskExecutor.setQueueCapacity(25);
        //执行初始化
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }

}
