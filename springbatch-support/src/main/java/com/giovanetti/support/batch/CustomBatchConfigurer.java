package com.giovanetti.support.batch;

import com.giovanetti.support.batch.annotations.TechnicalDataSource;
import com.giovanetti.support.batch.function.Consumer;
import com.giovanetti.support.batch.function.Function;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.inject.Inject;
import javax.sql.DataSource;

/**
 * Nécessaire pour gérer une datasource fonctionelle séparée de la datasource technique spring batch.
 * Rempli le contrat de {@link org.springframework.batch.core.configuration.annotation.BatchConfigurer}
 * pour la datasource technique.
 * Utiliser le qualifier {@link com.giovanetti.support.batch.annotations.FunctionalDataSource}
 * pour injecter la datasource fonctionnelle.
 * Utiliser le qualifier {@link com.giovanetti.support.batch.annotations.TechnicalDataSource}
 * pour injecter la datasource technique.
 *
 * @see {@link org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer}
 */
@Configuration
public class CustomBatchConfigurer implements BatchConfigurer {

    @Inject
    @TechnicalDataSource
    DataSource dataSource;

    @Override
    public JobRepository getJobRepository() {
        JobRepositoryFactoryBean factoryBean = new JobRepositoryFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setTransactionManager(getTransactionManager());
        Consumer.acceptWithRawException(factoryBean, JobRepositoryFactoryBean::afterPropertiesSet);
        return Function.applyWithRawException(factoryBean, FactoryBean::getObject);
    }

    @Override
    public PlatformTransactionManager getTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    @Override
    public JobLauncher getJobLauncher() {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(getJobRepository());
        Consumer.acceptWithRawException(jobLauncher, SimpleJobLauncher::afterPropertiesSet);
        return jobLauncher;
    }

    @Override
    public JobExplorer getJobExplorer() {
        return null;
    }


}

