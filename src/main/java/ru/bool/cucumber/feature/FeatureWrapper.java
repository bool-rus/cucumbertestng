package ru.bool.cucumber.feature;

import cucumber.runtime.model.*;
import ru.bool.cucumber.reflection.faces.BackgroundContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * Обертка фичи для быстрого поиска сценариев и шаблонов
 * используется в {@link FeatureRunner}
 * Created by bool on 06.09.17.
 */
class FeatureWrapper {
    private final Map<String,CucumberScenario>          sceanrios = new HashMap<>();
    private final Map<String,CucumberScenarioOutline>   outlines = new HashMap<>();

    FeatureWrapper(CucumberFeature feature) {
        feature.getFeatureElements().forEach(this::addElement);
    }

    private void addElement(CucumberTagStatement statement) {
        if(statement instanceof CucumberScenario) {
            CucumberScenario scenario = (CucumberScenario) statement;
            CucumberBackground background = scenario.getCucumberBackground();
            if(background!=null)
                background.getSteps().clear();
            sceanrios.put(scenario.getGherkinModel().getName(),scenario);
        } else if (statement instanceof CucumberScenarioOutline) {
            CucumberScenarioOutline outline = (CucumberScenarioOutline) statement;
            CucumberBackground background = BackgroundContainer.create(outline).getBackground();
            if(background!=null)
                background.getSteps().clear();
            outlines.put(outline.getGherkinModel().getName(),outline);
        }

    }

    CucumberScenario getScenario(String name) {
        return sceanrios.get(name);
    }

    CucumberScenarioOutline getOutline(String name) {
        return outlines.get(name);
    }

}
