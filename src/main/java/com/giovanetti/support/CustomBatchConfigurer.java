package com.giovanetti.support;

import javax.inject.Inject;
import javax.sql.DataSource;

import com.giovanetti.support.function.Consumer;
import com.giovanetti.support.function.Function;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.giovanetti.support.annotations.TechnicalDataSource;

@Configuration
public class CustomBatchConfigurer implements BatchConfigurer {

    @Inject
    @TechnicalDataSource
    private DataSource dataSource;

    @Override
    public JobRepository getJobRepository() {
        JobRepositoryFactoryBean factoryBean = new JobRepositoryFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setTransactionManager(getTransactionManager());
        Consumer.acceptAndAvoidRawExceptionType(factoryBean, JobRepositoryFactoryBean::afterPropertiesSet);
        return Function.applyAndAvoidRawExceptionType(factoryBean, (JobRepositoryFactoryBean factory) -> {
            return (JobRepository) factoryBean.getObject();
        });
    }

    @Override
    public PlatformTransactionManager getTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    @Override
    public JobLauncher getJobLauncher() {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(getJobRepository());
        Consumer.acceptAndAvoidRawExceptionType(jobLauncher, SimpleJobLauncher::afterPropertiesSet);
        return jobLauncher;
    }

}

