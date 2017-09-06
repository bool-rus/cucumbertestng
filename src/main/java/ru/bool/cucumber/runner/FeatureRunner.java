package ru.bool.cucumber.runner;

import cucumber.api.testng.FeatureResultListener;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.RuntimeOptionsFactory;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.model.CucumberFeature;

import java.util.List;

/**
 * Created by bool on 06.09.17.
 */
public class FeatureRunner {
    private final ClassLoader classLoader;

    private RuntimeOptions runtimeOptions;
    private ResourceLoader resourceLoader;
    private FeatureResultListener resultListener;



    public FeatureRunner(Class testClass) {
        classLoader = testClass.getClassLoader();
        resourceLoader = new MultiLoader(classLoader);
        runtimeOptions = new RuntimeOptionsFactory(testClass).create();
        resultListener = new FeatureResultListener(runtimeOptions.reporter(classLoader), runtimeOptions.isStrict());
        List<CucumberFeature> features = runtimeOptions.cucumberFeatures(resourceLoader);

    }
}
