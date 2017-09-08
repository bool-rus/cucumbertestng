package ru.bool.cucumber.runner;

import cucumber.runtime.model.*;
import ru.bool.cucumber.reflection.faces.BackgroundContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bool on 06.09.17.
 */
public class FeatureWrapper {
    private final Map<String,CucumberScenario>          sceanrios = new HashMap<>();
    private final Map<String,CucumberScenarioOutline>   outlines = new HashMap<>();

    public FeatureWrapper(CucumberFeature feature) {
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

    public CucumberScenario getScenario(String name) {
        return sceanrios.get(name);
    }

    public CucumberScenarioOutline getOutline(String name) {
        return outlines.get(name);
    }

}
