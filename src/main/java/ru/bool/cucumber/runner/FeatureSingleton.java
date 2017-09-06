package ru.bool.cucumber.runner;

import cucumber.api.DataTable;
import cucumber.api.testng.FeatureResultListener;
import cucumber.api.testng.TestNGCucumberRunner;
import cucumber.runtime.*;
import cucumber.runtime.Runtime;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.model.*;
import gherkin.formatter.model.*;
import ru.bool.cucumber.reflection.faces.BackgroundContainer;
import ru.bool.cucumber.reflection.faces.FeatureParamsContainer;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by bool on 04.09.17.
 */
public class FeatureSingleton {
    public static final FeatureSingleton INSTANCE = new FeatureSingleton();
    private final ThreadLocal<CucumberFeature> currentFeature = new ThreadLocal<>();
    private final ThreadLocal<FeatureParamsContainer> paramsContainer = new ThreadLocal<>();

    private FeatureSingleton() {}

    public void setCurrentFeature(CucumberFeature feature) {
        currentFeature.set(feature);
    }

    public void setCurrentRunner(TestNGCucumberRunner runner) {
        paramsContainer.set(FeatureParamsContainer.create(runner));
    }

    public void runScenario(final String name) {
        runWithoutBackground(
                findScenario(name)
        );
    }

    public void runOutline(String name, DataTable examples) {
        CucumberScenarioOutline outline = (CucumberScenarioOutline) findScenario(name);
        List<CucumberExamples> backup = new ArrayList<>();
        List<CucumberExamples> current = outline.getCucumberExamplesList();
        backup.addAll(current);
        current.clear();
        outline.examples(createExamples(examples));
        runWithoutBackground(outline);
        current.clear();
        current.addAll(backup);
    }

    private void runWithoutBackground(CucumberTagStatement statement) {
        List<Step> backup = new ArrayList<>();
        List<Step> current = getBackground(statement).getSteps();
        backup.addAll(current);
        current.clear();
        runCucumberTagStatement(statement);
        current.addAll(backup);
    }

    private CucumberBackground getBackground(CucumberTagStatement statement) {
        if(statement instanceof  CucumberScenario)
            return ((CucumberScenario) statement).getCucumberBackground();
        if(statement instanceof CucumberScenarioOutline)
            return BackgroundContainer.create(statement).getBackground();
        throw new IllegalArgumentException("Cannot invoke background");
    }


    private Examples createExamples(DataTable dataTable) {
        List<ExamplesTableRow> rows = new ArrayList<>(dataTable.raw().size());
        dataTable.getGherkinRows().forEach(tableRow -> rows.add(
                new ExamplesTableRow(Collections.emptyList(),tableRow.getCells(),tableRow.getLine(),"вызов шаблона")
        ));
        return new Examples(
                        Collections.emptyList(),
                        Collections.emptyList(),
                        "null",
                        "null",
                        "null",
                        rows.get(0).getLine(),
                        "null",
                        rows
                );
    }

    private void runCucumberTagStatement(CucumberTagStatement statement) {
        ClassLoader classLoader = getClass().getClassLoader();
        ResourceLoader resourceLoader = new MultiLoader(classLoader);
        RuntimeOptions options = paramsContainer.get().getRuntimeOptions();
        FeatureResultListener resultListener = new FeatureResultListener(options.reporter(classLoader), options.isStrict());
        Runtime runtime = new Runtime(
                resourceLoader,
                classLoader,
                loadBackends(resourceLoader, new ResourceLoaderClassFinder(resourceLoader, classLoader)),
                options,
                new StopWatchImpl(),
                null
        );
        statement.run(
                options.formatter(classLoader),
                resultListener,
                runtime
        );
    }
    private CucumberTagStatement findScenario(String visualName) {
        return currentFeature.get().getFeatureElements().stream().filter(scenario ->
            scenario.getGherkinModel().getName().equals(visualName)
        ).findFirst().get();
    }

    private Collection<? extends Backend> loadBackends(ResourceLoader resourceLoader, ClassFinder classFinder) {
        Reflections reflections = new Reflections(classFinder);
        return reflections.instantiateSubclasses(Backend.class, "cucumber.runtime", new Class[]{ResourceLoader.class}, new Object[]{resourceLoader});
    }

    private class StopWatchImpl implements StopWatch {
        private long tick = 0;

        @Override
        public void start() {
            tick = System.nanoTime();
        }

        @Override
        public long stop() {
            return System.nanoTime() - tick;
        }
    }
}
