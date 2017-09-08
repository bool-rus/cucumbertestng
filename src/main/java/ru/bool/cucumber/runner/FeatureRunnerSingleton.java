package ru.bool.cucumber.runner;

/**
 * Created by bool on 04.09.17.
 */
public class FeatureRunnerSingleton {
    private static final FeatureRunnerSingleton INSTANCE = new FeatureRunnerSingleton();
    private final ThreadLocal<FeatureRunner> runner = new ThreadLocal<>();
    public static void init(Class testClass) {
        if(runner()==null || !runner().testClass.equals(testClass))
            INSTANCE.runner.set(new FeatureRunner(testClass));
    }

    /**
     * Usage
     * import static ru.bool.cucumber.runner.FeatureRunnerSingleton.runner;
     * затем где-нибудь в коде получаем раннер, просто вызывая runner()
     *
     * Работает, только если фича вызывалась из теста, отнаследованного от {@link ru.bool.cucumber.testng.TestNGCucumberTestsImpl}
     *
     * @return раннер {@link FeatureRunner}, который может запускать сценарии и шаблоны (которые "Описание сценария")
     */
    public static FeatureRunner runner() {
        return INSTANCE.runner.get();
    }
}
