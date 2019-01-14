package com.giovanetti.sample.batch.job.jbehave;


import com.giovanetti.sample.batch.configuration.JobExtractionTestConfiguration;
import com.giovanetti.support.batch.extension.BatchProperties;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.EmbedderControls;
import org.jbehave.core.embedder.executors.SameThreadExecutors;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.CrossReference;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.ParameterConverters;
import org.jbehave.core.steps.spring.SpringApplicationContextFactory;
import org.jbehave.core.steps.spring.SpringStepsFactory;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.context.ApplicationContext;

import java.net.URL;
import java.util.List;

public class ExtractionStoriesTest extends JUnitStories {

    private final CrossReference xref = new CrossReference();

    public ExtractionStoriesTest() {
        BatchProperties.getDefault().create();
        Embedder embedder = configuredEmbedder();
        EmbedderControls controls = embedder.embedderControls();
        embedder.useExecutorService(new SameThreadExecutors().create(controls));
    }

    @Override
    public Configuration configuration() {
        StoryReporterBuilder storyReporter = //
                new StoryReporterBuilder() //
                        .withDefaultFormats() //
                        .withFormats(Format.TXT, Format.CONSOLE, Format.HTML)
                        .withFailureTrace(true) //
                        .withCrossReference(xref);
        return new MostUsefulConfiguration() //
                .useStoryReporterBuilder(storyReporter) //
                .useStepMonitor(xref.getStepMonitor())//
                .useParameterConverters(customConverters());
    }

    private ParameterConverters customConverters() {
        ParameterConverters parameterConverters = new ParameterConverters();
        parameterConverters.addConverters(new ParameterConverters.StringListConverter("\\n"));
        return parameterConverters;
    }

    @Override
    protected List<String> storyPaths() {
        URL searchInURL = CodeLocations.codeLocationFromClass(this.getClass());
        return new StoryFinder().findPaths(searchInURL, "**/jbehave/extraction*.story", "");
    }

    @Override
    public InjectableStepsFactory stepsFactory() {
        return new SpringStepsFactory(configuration(), createContext());
    }

    private ApplicationContext createContext() {
        return new SpringApplicationContextFactory(JobExtractionTestConfiguration.class.getName(),
                JobLauncherTestUtils.class.getName(), ExtractionSteps.class.getName()).createApplicationContext();
    }

}
