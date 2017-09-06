package tests;

import cucumber.api.CucumberOptions;
import cucumber.api.testng.CucumberFeatureWrapper;
import ru.bool.cucumber.testng.TestNGCucumberTestsImpl;

/**
 * Created by bool on 04.09.17.
 */
@CucumberOptions (
        glue = "steps",
        features = {
                "classpath:features/one.feature",
                "classpath:features/two.feature"
        }
)
public class FirstTest extends TestNGCucumberTestsImpl{
}

