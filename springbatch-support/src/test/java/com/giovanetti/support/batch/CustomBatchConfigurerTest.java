package com.giovanetti.support.batch;

import com.giovanetti.support.batch.CustomBatchConfigurer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.support.SimpleJobRepository;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomBatchConfigurerTest {

    private CustomBatchConfigurer customBatchConfigurer;

    @Before
    public void setup() {
        customBatchConfigurer = new CustomBatchConfigurer();
        customBatchConfigurer.dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).build();
    }

    @Test
    public void getTransactionManager() {
        assertThat(customBatchConfigurer.getTransactionManager()).isExactlyInstanceOf(DataSourceTransactionManager.class);
    }

    @Test
    public void getJobRepository() {
        assertThat(customBatchConfigurer.getJobRepository().toString()).contains(SimpleJobRepository.class.getName());
    }

    @Test
    public void getJobLauncher() throws Exception {
        assertThat(customBatchConfigurer.getJobLauncher().toString()).contains(SimpleJobLauncher.class.getName());
    }
}
