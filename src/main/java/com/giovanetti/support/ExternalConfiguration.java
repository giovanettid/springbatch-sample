package com.giovanetti.support;

import javax.inject.Inject;
import javax.sql.DataSource;

import com.giovanetti.support.annotations.CommitInterval;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.giovanetti.support.annotations.FunctionalDataSource;
import com.giovanetti.support.annotations.TechnicalDataSource;

@Configuration
@PropertySource("classpath:batch.properties")
public class ExternalConfiguration {

    @Inject
    private Environment environment;

    public enum DataSourceType {
        FUNCTIONAL, TECHNICAL;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public enum DataSourcePropertyKeys {

        DRIVER_CLASS("driverclassname"), URL("url"), USERNAME("username"), PASSWORD(
                "password");

        private final static String PREFIX = "ds";

        private final String name;

        private DataSourcePropertyKeys(String pName) {
            this.name = pName;
        }

        @Override
        public String toString() {
            return name;
        }

        public String name(DataSourceType dataSourceType) {
            return PREFIX + "." + dataSourceType.toString() + "." + this;
        }

    }

    public enum StepPropertyKeys {

        COMMIT_INTERVAL("commit.interval");

        private final String name;

        // TODO : check uncle bob for naming pArg...
        private StepPropertyKeys(String pName) {
            this.name = pName;
        }

        @Override
        public String toString() {
            return name;
        }

    }

    @Bean
    @CommitInterval
    Integer commitInterval() {
        return Integer.parseInt(environment
                .getProperty(StepPropertyKeys.COMMIT_INTERVAL
                        .toString()));
    }

    @Bean
    @FunctionalDataSource
    DataSource functionnalDataSource() {
        return createDataSource(DataSourceType.FUNCTIONAL);
    }

    @Bean
    @TechnicalDataSource
    DataSource technicalDataSource() {
        return createDataSource(DataSourceType.TECHNICAL);
    }

    private DataSource createDataSource(DataSourceType dataSourceType) {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(environment
                .getProperty(DataSourcePropertyKeys.DRIVER_CLASS
                        .name(dataSourceType)));
        ds.setUrl(environment.getProperty(DataSourcePropertyKeys.URL
                .name(dataSourceType)));
        ds.setUsername(environment.getProperty(DataSourcePropertyKeys.USERNAME
                .name(dataSourceType)));
        ds.setPassword(environment.getProperty(DataSourcePropertyKeys.PASSWORD
                .name(dataSourceType)));
        return ds;
    }

}
