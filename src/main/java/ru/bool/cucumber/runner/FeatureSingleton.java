package ru.bool.cucumber.runner;

/**
 * Created by bool on 04.09.17.
 */
public class FeatureSingleton {
    private static final FeatureSingleton INSTANCE = new FeatureSingleton();
    private final ThreadLocal<FeatureRunner> runner = new ThreadLocal<>();
    public static void init(Class testClass) {
        if(runner()==null || !runner().testClass.equals(testClass))
            INSTANCE.runner.set(new FeatureRunner(testClass));
    }
    public static FeatureRunner runner() {
        return INSTANCE.runner.get();
    }
}
