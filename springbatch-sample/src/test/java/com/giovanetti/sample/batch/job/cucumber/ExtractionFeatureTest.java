package com.giovanetti.sample.batch.job.cucumber;

import com.giovanetti.support.batch.extension.BatchProperties;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(format = {"pretty", "html:target/cucumber-report"},
        glue = "com.giovanetti.sample.batch.job.cucumber")
public class ExtractionFeatureTest {

    @BeforeClass
    public static void setupClass() {
        BatchProperties.getDefault().create();
    }

}
