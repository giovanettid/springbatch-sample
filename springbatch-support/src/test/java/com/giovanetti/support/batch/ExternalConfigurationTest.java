package com.giovanetti.support.batch;

import com.giovanetti.support.batch.ExternalConfiguration.DataSourcePropertyKeys;
import com.giovanetti.support.batch.ExternalConfiguration.DataSourceType;
import com.giovanetti.support.batch.annotations.FunctionalDataSource;
import com.giovanetti.support.batch.annotations.TechnicalDataSource;
import com.giovanetti.support.batch.extension.BatchProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ExternalConfiguration.class})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ExternalConfigurationTest {

    @RegisterExtension
    public static BatchProperties batchProperties = BatchProperties.getDefault();

    @Inject
    private Environment environment;

    @Inject
    @FunctionalDataSource
    private DataSource functionnalDataSource;

    @Inject
    @TechnicalDataSource
    private DataSource technicalDataSource;

    @Test
    public void functionnalDatasource() {
        assertThat(functionnalDataSource).isInstanceOf(
                DriverManagerDataSource.class);
        DriverManagerDataSource dmds = (DriverManagerDataSource) functionnalDataSource;
        assertThat(dmds.getUrl()).isEqualTo(
                environment.getProperty(DataSourcePropertyKeys.URL.name(DataSourceType.FUNCTIONAL)));
        assertThat(dmds.getUsername()).isEqualTo(
                environment.getProperty(DataSourcePropertyKeys.USERNAME
                        .name(DataSourceType.FUNCTIONAL))
        );
        assertThat(dmds.getPassword()).isEqualTo(
                environment.getProperty(DataSourcePropertyKeys.PASSWORD
                        .name(DataSourceType.FUNCTIONAL))
        );
    }

    @Test
    public void technicalDatasource() {
        assertThat(functionnalDataSource).isInstanceOf(
                DriverManagerDataSource.class);
        DriverManagerDataSource dmds = (DriverManagerDataSource) technicalDataSource;
        assertThat(dmds.getUrl()).isEqualTo(
                environment.getProperty(DataSourcePropertyKeys.URL.name(DataSourceType.TECHNICAL)));
        assertThat(dmds.getUsername()).isEqualTo(
                environment.getProperty(DataSourcePropertyKeys.USERNAME
                        .name(DataSourceType.TECHNICAL))
        );
        assertThat(dmds.getPassword()).isEqualTo(
                environment.getProperty(DataSourcePropertyKeys.PASSWORD
                        .name(DataSourceType.TECHNICAL))
        );
    }

}
