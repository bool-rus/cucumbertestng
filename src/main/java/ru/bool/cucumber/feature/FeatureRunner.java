package ru.bool.cucumber.feature;

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
import ru.bool.cucumber.testng.CucumberTestNG;

import java.util.*;

/**
 * Created by bool on 06.09.17.
 */
public class FeatureRunner {
    private static final ThreadLocal<FeatureRunner> INSTANCES = new ThreadLocal<>();
    private class RuntimeObjects {
        private final RuntimeOptions runtimeOptions;
        private final FeatureResultListener resultListener;
        private final gherkin.formatter.Formatter formatter;
        private final Runtime runtime;

        private RuntimeObjects(Class testClass) {
            runtimeOptions = new RuntimeOptionsFactory(testClass).create();
            formatter = runtimeOptions.formatter(classLoader);
            resultListener = new FeatureResultListener(
                    runtimeOptions.reporter(classLoader),
                    runtimeOptions.isStrict()
            );
            runtime = new Runtime(
                    resourceLoader,
                    classLoader,
                    loadBackends(resourceLoader, new ResourceLoaderClassFinder(resourceLoader, classLoader)),
                    runtimeOptions,
                    new StopWatchImpl(),
                    null
            );
        }

        private void runStatement(CucumberTagStatement statement) {
            statement.run(formatter, resultListener, runtime);
            formatter.done();
            formatter.close();
        }

        private void runFeature(CucumberFeature feature) {
            resultListener.startFeature();
            feature.run(formatter, resultListener, runtime);
            runtime.printSummary();
            if (!resultListener.isPassed())
                throw new CucumberException(resultListener.getFirstError());
        }
    }

    private final Map<String, FeatureWrapper> features = new HashMap<>();
    private final Class testClass;
    private final ClassLoader classLoader;
    private final ResourceLoader resourceLoader;
    private final RuntimeObjects rootRuntime;
    private String currentFeature;
    private boolean isRoot = true;

    FeatureRunner(Class testClass) {
        this.testClass = testClass;
        classLoader = testClass.getClassLoader();
        resourceLoader = new MultiLoader(classLoader);
        rootRuntime = new RuntimeObjects(testClass);
        initAllFeatures();
    }

    private void initAllFeatures() {
        List<Object> filters = rootRuntime.runtimeOptions.getFilters();
        List<Object> filtersBackup = new ArrayList<>(filters.size());
        filtersBackup.addAll(rootRuntime.runtimeOptions.getFilters());
        filters.clear();
        rootRuntime.runtimeOptions.cucumberFeatures(resourceLoader).forEach(feature ->
                features.put(feature.getGherkinFeature().getName(), new FeatureWrapper(feature))
        );
        filters.addAll(filtersBackup);
    }

    /**
     * запустить сценарий из текущей фичи
     *
     * @see #runScenario(String, String)
     */
    public void runScenario(String scenario) {
        runScenario(scenario, currentFeature);
    }

    /**
     * запустить шаблон из текущей фичи
     *
     * @see #runOutline(String, DataTable, String)
     */
    public void runOutline(String outlineName, DataTable params) {
        runOutline(outlineName, params, currentFeature);
    }

    private void setCurrentFeature(CucumberFeature feature) {
        currentFeature = feature.getGherkinFeature().getName();
    }

    /**
     * выполнить сценарий
     * сценарий выполняется без контекста
     *
     * @param scenario название сценария, который нужно выполнить
     * @param feature  название фичи, в которой искать указанный сценарий
     */

    public void runScenario(String scenario, String feature) {
        String backup = currentFeature;
        currentFeature = feature;
        runCucumberTagStatement(features.get(feature).getScenario(scenario));
        currentFeature = backup;
    }

    /**
     * выполнить шаблон (Описание сценария)
     * выполняется без контекста (т.е. с первого до последнего шага сценария
     * без дополнительных шагов, например, предварительных)
     *
     * @param outlineName - название шаблона
     * @param params      - таблица с данными, которая будет использована в качестве примеров для шаблона
     * @param feature     - название фичи
     */
    public void runOutline(String outlineName, DataTable params, String feature) {
        String backup = currentFeature;
        currentFeature = feature;
        CucumberScenarioOutline outline = features.get(feature).getOutline(outlineName);
        outline.getCucumberExamplesList().clear();
        outline.examples(createExamples(params));
        runCucumberTagStatement(outline);
        currentFeature = backup;
    }

    private void runCucumberTagStatement(CucumberTagStatement statement) {
        boolean isRootPrev = isRoot;
        isRoot = false;
        new RuntimeObjects(testClass).runStatement(statement);
        isRoot = isRootPrev;
    }

    public boolean isRoot() {
        return isRoot;
    }

    private Examples createExamples(DataTable dataTable) {
        List<ExamplesTableRow> rows = new ArrayList<>(dataTable.raw().size());
        dataTable.getGherkinRows().forEach(tableRow -> rows.add(
                new ExamplesTableRow(Collections.emptyList(), tableRow.getCells(), tableRow.getLine(), "вызов шаблона")
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

    public void runRootFeature(CucumberFeature cucumberFeature) {
        setCurrentFeature(cucumberFeature);
        rootRuntime.runFeature(cucumberFeature);
    }

    public List<CucumberFeature> getFeaturesToRun() {
        return rootRuntime.runtimeOptions.cucumberFeatures(resourceLoader);
    }

    public void finish() {
        rootRuntime.formatter.done();
        rootRuntime.formatter.close();
    }

    /**
     * Usage
     * import static ru.bool.cucumber.instance.FeatureRunner.instance;
     * затем где-нибудь в коде получаем раннер, просто вызывая instance()
     * <p>
     * Работает, только если фича вызывалась из теста, отнаследованного от {@link CucumberTestNG}
     *
     * @return раннер {@link FeatureRunner}, который может запускать сценарии и шаблоны (которые "Описание сценария")
     */
    public static FeatureRunner instance() {
        FeatureRunner result = INSTANCES.get();
        if (result != null)
            return result;
        throw new IllegalStateException("Runner not initialized. You need to initialize them from TestClass by running instance(Class)");
    }

    public static FeatureRunner instance(Class testClass) {
        FeatureRunner result = INSTANCES.get();
        if (result != null && result.getClass() == testClass)
            return result;
        result = new FeatureRunner(testClass);
        INSTANCES.set(result);
        return result;
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
