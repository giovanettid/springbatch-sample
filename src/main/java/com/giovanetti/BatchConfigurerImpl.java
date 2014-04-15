package com.giovanetti;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.giovanetti.annotations.TechnicalDataSource;

@Configuration
//TODO : better name !
public class BatchConfigurerImpl implements BatchConfigurer {

	@Inject
	@TechnicalDataSource
	private DataSource dataSource;
	
	@Override
	public JobRepository getJobRepository() {
		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
		factory.setDataSource(dataSource);
		factory.setTransactionManager(getTransactionManager());
		JobRepository jobRepository = null;
		try {
			factory.afterPropertiesSet();
			jobRepository = (JobRepository) factory.getObject();
		} catch (Exception e) {
			// TODO : pas mieux ?
			throw new IllegalStateException(e);
		}
		return jobRepository;
	}

	@Override
	public PlatformTransactionManager getTransactionManager() {
		return new DataSourceTransactionManager(dataSource);
	}

	@Override
	public JobLauncher getJobLauncher() {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(getJobRepository());
		try {
			jobLauncher.afterPropertiesSet();
		} catch (Exception e) {
			// TODO : pas mieux ?
			throw new IllegalStateException(e);
		}
		return jobLauncher;
	}

}
