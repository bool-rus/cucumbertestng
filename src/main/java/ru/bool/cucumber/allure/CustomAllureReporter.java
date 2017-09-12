package ru.bool.cucumber.allure;

import gherkin.formatter.model.Scenario;
import ru.yandex.qatools.allure.cucumberjvm.AllureReporter;

import static ru.bool.cucumber.feature.FeatureRunner.instance;

/**
 * Created by bool on 09.09.17.
 */
public class CustomAllureReporter extends AllureReporter {
    @Override
    public void startOfScenarioLifeCycle(Scenario scenario) {
        if(instance().isRoot())
            super.startOfScenarioLifeCycle(scenario);
    }

    @Override
    public void endOfScenarioLifeCycle(Scenario scenario) {
        if(instance().isRoot())
            super.endOfScenarioLifeCycle(scenario);
    }
}
