package com.giovanetti.support.batch.rule;

import com.giovanetti.support.batch.ExternalConfiguration;
import org.junit.contrib.java.lang.system.ProvideSystemProperty;

class BatchPropertiesPathSystemProperty extends ProvideSystemProperty {

    BatchPropertiesPathSystemProperty() {
        super(ExternalConfiguration.BATCH_PROPERTIES_PATH, "unknown at this time.");
    }

    @Override
    protected void before() throws Throwable {
        super.before();
    }

    void set(String value) {
        and(ExternalConfiguration.BATCH_PROPERTIES_PATH, "file:" + value);
    }
}
