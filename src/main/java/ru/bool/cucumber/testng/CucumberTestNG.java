package ru.bool.cucumber.testng;

import cucumber.api.testng.CucumberFeatureWrapper;
import cucumber.api.testng.CucumberFeatureWrapperImpl;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static ru.bool.cucumber.feature.FeatureRunner.instance;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bool on 06.09.17.
 */
@Test(singleThreaded = true)
public class CucumberTestNG {

    @Test(dataProvider = "getFeatures")
    public void feature(CucumberFeatureWrapper cucumberFeature) throws InterruptedException {
        instance(getClass()).runRootFeature(cucumberFeature.getCucumberFeature());
    }

    @DataProvider(parallel = true)
    public Object[][] getFeatures() {
        List<Object[]> result = new ArrayList<>();
        instance(getClass()).getFeaturesToRun().forEach(
                feature -> result.add(new Object[]{new CucumberFeatureWrapperImpl(feature)})
        );
        return result.toArray(new Object[][]{});
    }

    @AfterMethod
    public void afterTest() {
        instance().finish();
    }
}
