package ru.bool.cucumber.reflection.faces;

import cucumber.api.testng.FeatureResultListener;
import cucumber.runtime.RuntimeOptions;
import ru.bool.cucumber.reflection.FieldInvoker;

import java.lang.reflect.Proxy;

/**
 * Created by bool on 05.09.17.
 */
public interface FeatureParamsContainer {
    RuntimeOptions getRuntimeOptions();

    FeatureResultListener getResultListener();

    static FeatureParamsContainer create(Object container) {
        return (FeatureParamsContainer) Proxy.newProxyInstance(
                container.getClass().getClassLoader(),
                new Class[]{FeatureParamsContainer.class},
                new FieldInvoker(container)
        );
    }
}
