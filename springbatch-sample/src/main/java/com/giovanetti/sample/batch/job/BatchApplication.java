package com.giovanetti.sample.batch.job;

import com.giovanetti.support.batch.annotations.TechnicalDataSource;
import com.giovanetti.support.batch.function.Consumer;
import com.giovanetti.support.batch.function.Function;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.JobLauncherCommandLineRunner;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableAutoConfiguration(exclude={DataSourceTransactionManagerAutoConfiguration.class, BatchAutoConfiguration.class, DataSourceAutoConfiguration.class})
@SpringBootApplication
public class BatchApplication {

    @Bean
    public JobExplorer jobExplorer(@TechnicalDataSource DataSource dataSource) {
        JobExplorerFactoryBean factory = new JobExplorerFactoryBean();
        factory.setDataSource(dataSource);
        Consumer.acceptWithRawException(factory, JobExplorerFactoryBean::afterPropertiesSet);
        return Function.applyWithRawException(factory, FactoryBean::getObject);
    }

    @Bean
    public JobLauncherCommandLineRunner jobLauncherCommandLineRunner(JobLauncher jobLauncher, JobExplorer jobExplorer, @Value(
            "${job.name}") String jobName) {
        JobLauncherCommandLineRunner runner = new JobLauncherCommandLineRunner(jobLauncher, jobExplorer);
        runner.setJobNames(jobName);
        return runner;
    }

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(BatchApplication.class, args)));
    }

}
