package ru.bool.cucumber.testng;

import cucumber.api.testng.AbstractTestNGCucumberTests;
import cucumber.api.testng.CucumberFeatureWrapper;
import ru.bool.cucumber.runner.FeatureRunnerSingleton;

/**
 * Created by bool on 06.09.17.
 */
public class TestNGCucumberTestsImpl extends AbstractTestNGCucumberTests {
    @Override
    public void feature(CucumberFeatureWrapper cucumberFeature) {
        FeatureRunnerSingleton.init(this.getClass());
        FeatureRunnerSingleton.runner().setCurrentFeature(cucumberFeature.getCucumberFeature());
        super.feature(cucumberFeature);
    }
}
