package tests;

import cucumber.api.CucumberOptions;
import org.testng.annotations.Test;
import ru.bool.cucumber.testng.CucumberTestNG;

/**
 * Created by bool on 04.09.17.
 */
@CucumberOptions(
        glue = "steps",
        features = "classpath:features"
)
public class FirstTest extends CucumberTestNG {
}

