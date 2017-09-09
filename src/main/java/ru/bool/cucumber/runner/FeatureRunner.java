package ru.bool.cucumber.runner;

import cucumber.api.DataTable;
import cucumber.api.testng.FeatureResultListener;
import cucumber.runtime.*;
import cucumber.runtime.Runtime;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.model.CucumberFeature;
import cucumber.runtime.model.CucumberScenarioOutline;
import cucumber.runtime.model.CucumberTagStatement;
import gherkin.formatter.model.Examples;
import gherkin.formatter.model.ExamplesTableRow;

import java.util.*;

/**
 * Created by bool on 06.09.17.
 */
public class FeatureRunner {
    private final ClassLoader classLoader;

    private final RuntimeOptions runtimeOptions;
    private final ResourceLoader resourceLoader;
    private final FeatureResultListener resultListener;
    private final Map<String, FeatureWrapper> features = new HashMap<>();
    public final Class testClass;
    private String currentFeature;
    private boolean inUse = false;

    FeatureRunner(Class testClass) {
        this.testClass = testClass;
        classLoader = testClass.getClassLoader();
        resourceLoader = new MultiLoader(classLoader);
        runtimeOptions = new RuntimeOptionsFactory(testClass).create();
        resultListener = new FeatureResultListener(runtimeOptions.reporter(classLoader), runtimeOptions.isStrict());
        runtimeOptions.cucumberFeatures(resourceLoader).forEach(feature ->
                features.put(feature.getGherkinFeature().getName(), new FeatureWrapper(feature))
        );
    }

    /**
     * запустить сценарий из текущей фичи
     * @see #runScenario(String, String)
     */
    public void runScenario(String scenario) {
        runScenario(scenario,currentFeature);
    }

    /**
     * запустить шаблон из текущей фичи
     * @see #runOutline(String, DataTable, String)
     */
    public void runOutline(String outlineName, DataTable params) {
        runOutline(outlineName,params,currentFeature);
    }

    public void setCurrentFeature(CucumberFeature feature) {
        currentFeature = feature.getGherkinFeature().getName();
    }


    /**
     * выполнить сценарий
     * сценарий выполняется без контекста
     * @param scenario название сценария, который нужно выполнить
     * @param feature название фичи, в которой искать указанный сценарий
     */

    public void runScenario(String scenario, String feature) {
        String backup = currentFeature;
        currentFeature = feature;
        runCucumberTagStatement(features.get(feature).getScenario(scenario));
        currentFeature=backup;
    }

    /**
     * выполнить шаблон (Описание сценария)
     * выполняется без контекста (т.е. с первого до последнего шага сценария
     * без дополнительных шагов, например, предварительных)
     *
     * @param outlineName - название шаблона
     * @param params - таблица с данными, которая будет использована в качестве примеров для шаблона
     * @param feature - название фичи
     */
    public void runOutline(String outlineName, DataTable params, String feature) {
        String backup = currentFeature;
        currentFeature = feature;
        CucumberScenarioOutline outline = features.get(feature).getOutline(outlineName);
        outline.getCucumberExamplesList().clear();
        outline.examples(createExamples(params));
        runCucumberTagStatement(outline);
        currentFeature=backup;
    }

    private void runCucumberTagStatement(CucumberTagStatement statement) {
        boolean inUsePrev = inUse;
        inUse = true;
        Runtime runtime = new Runtime(
                resourceLoader,
                classLoader,
                loadBackends(resourceLoader, new ResourceLoaderClassFinder(resourceLoader, classLoader)),
                runtimeOptions,
                new StopWatchImpl(),
                null
        );
        statement.run(
                runtimeOptions.formatter(classLoader),
                resultListener,
                runtime
        );
        inUse = inUsePrev;
    }

    public boolean inUse() {
        return inUse;
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
