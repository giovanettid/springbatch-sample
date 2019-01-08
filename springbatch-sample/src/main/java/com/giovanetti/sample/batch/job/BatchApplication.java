package com.giovanetti.sample.batch.job;

import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.JobLauncherCommandLineRunner;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(
        exclude={DataSourceTransactionManagerAutoConfiguration.class, BatchAutoConfiguration.class, DataSourceAutoConfiguration.class})
public class BatchApplication {

    @Bean
    public JobLauncherCommandLineRunner jobLauncherCommandLineRunner(JobLauncher jobLauncher, JobExplorer jobExplorer, JobRepository jobRepository, @Value(
            "${job.name}") String jobName) {
        JobLauncherCommandLineRunner runner = new JobLauncherCommandLineRunner(jobLauncher, jobExplorer, jobRepository);
        runner.setJobNames(jobName);
        return runner;
    }

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(BatchApplication.class, args)));
    }

}
