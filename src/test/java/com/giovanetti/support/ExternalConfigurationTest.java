package com.giovanetti.support;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.giovanetti.support.ExternalConfiguration.DataSourcePropertyKeys;
import com.giovanetti.support.ExternalConfiguration.DataSourceType;
import com.giovanetti.support.annotations.FunctionalDataSource;
import com.giovanetti.support.annotations.TechnicalDataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ExternalConfiguration.class})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ExternalConfigurationTest {

    @ClassRule
    public final static BatchProperties batchProperties = BatchProperties.getDefault();

    @BeforeClass
    public static void setupClass() {
        batchProperties.flush();
    }

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
