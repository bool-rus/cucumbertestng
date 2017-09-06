package ru.bool.cucumber.testng;

import cucumber.api.testng.AbstractTestNGCucumberTests;
import cucumber.api.testng.CucumberFeatureWrapper;
import ru.bool.cucumber.runner.FeatureSingleton;

/**
 * Created by bool on 06.09.17.
 */
public class TestNGCucumberTestsImpl extends AbstractTestNGCucumberTests {
    @Override
    public void feature(CucumberFeatureWrapper cucumberFeature) {
        FeatureSingleton.init(this.getClass());
        FeatureSingleton.runner().setCurrentFeature(cucumberFeature.getCucumberFeature());
        super.feature(cucumberFeature);
    }
}
