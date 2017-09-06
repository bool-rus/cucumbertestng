package ru.bool.cucumber.reflection.faces;

import cucumber.runtime.model.CucumberBackground;
import cucumber.runtime.model.CucumberTagStatement;
import ru.bool.cucumber.reflection.FieldInvoker;

import java.lang.reflect.Proxy;

/**
 * Created by bool on 05.09.17.
 */
public interface BackgroundContainer {
    CucumberBackground getBackground();
    static BackgroundContainer create(CucumberTagStatement statement) {
        return (BackgroundContainer) Proxy.newProxyInstance(
                statement.getClass().getClassLoader(),
                new Class[]{BackgroundContainer.class},
                new FieldInvoker(statement)
        );
    }
}
