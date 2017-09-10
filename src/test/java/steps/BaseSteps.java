package steps;

import cucumber.api.DataTable;
import cucumber.api.java.ru.Дано;
import cucumber.api.java.ru.Если;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.То;

import static ru.bool.cucumber.feature.FeatureRunner.instance;

/**
 * Created by bool on 04.09.17.
 */
public class BaseSteps {

    @Дано("нахожусь на странице \"(.*?)\"")
    public void bgg(String value) {
        System.out.println(value);
    }

    @Если("^заполняю поля:$")
    public void заполняюПоля(DataTable table) throws Throwable {
        table.getGherkinRows().forEach(row -> System.out.println(
                "Заполняем поле '" +
                row.getCells().get(0) +
                "' значением '" +
                row.getCells().get(1)
        ));
    }

    @И("^жму кнопку \"([^\"]*)\"$")
    public void жмуКнопку(String btnName) throws Throwable {
        System.out.println("Жмем кнопку " + btnName);
    }

    @То("^попадаю на страницу \"([^\"]*)\"$")
    public void попадаюНаСтраницу(String pageName) throws Throwable {
        System.out.println("открылась страница " + pageName);
    }

    @Дано("^открыт сайт \"([^\"]*)\"$")
    public void открытСайт(String addr) throws Throwable {
        System.out.println("Opening address " + addr);
    }

    @И("^выполняю сценарий \"([^\"]*)\"$")
    public void выполняюСценарий(String scenarioName) throws Throwable {
        instance().runScenario(scenarioName);
    }

    @И("^выполняю шаблон \"([^\"]*)\":$")
    public void выполняюШаблон(String templateName, DataTable dataTable) throws Throwable {
        instance().runOutline(templateName, dataTable);
    }

    @Если("^выполняю шаблон \"([^\"]*)\" используя \"([^\"]*)\":$")
    public void выполняюШаблонИспользуя(String templateName, String featureName, DataTable params) throws Throwable {
        instance().runOutline(templateName,params,featureName);
    }

    @Если("^выполняю сценарий \"([^\"]*)\" используя \"([^\"]*)\"$")
    public void выполняюСценарийИспользуя(String scenarioName, String featureName) throws Throwable {
        instance().runScenario(scenarioName,featureName);
    }
}
