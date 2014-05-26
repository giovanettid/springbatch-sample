package com.giovanetti.support.batch.rule;

import com.giovanetti.support.batch.ExternalConfiguration;
import org.junit.contrib.java.lang.system.ProvideSystemProperty;

/**
 * Rule Junit pour fournir la propriété système correspondante
 * à l'emplacement du fichier de propriétés du batch pendant les tests.
 */
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
