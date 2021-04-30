package com.kg.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author ShiLin
 * @since 2017-05-27
 */
@Configuration
public class ThreadPoolConfig {

	@Bean(name = "defaultPool")
	public ThreadPoolTaskExecutor defaultPool() {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		pool.setCorePoolSize(20);
		pool.setMaxPoolSize(50);
		return pool;
	}

	@Bean(name = "neo4jThreadPool")
	public ThreadPoolTaskExecutor importOrder_Pool() {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		pool.setCorePoolSize(10);
		pool.setMaxPoolSize(50);
		return pool;
	}


	@Bean(name = "doMasterTask_Step1_Pool")
	public ThreadPoolTaskExecutor doMasterTask_Step1_Pool() {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		pool.setCorePoolSize(10);
		pool.setMaxPoolSize(50);
		return pool;
	}

	@Bean(name = "doMasterTask_Step2_Pool")
	public ThreadPoolTaskExecutor doMasterTask_Step2_Pool() {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		pool.setCorePoolSize(10);
		pool.setMaxPoolSize(50);
		return pool;
	}


}
