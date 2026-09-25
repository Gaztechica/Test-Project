package example.tests.ui;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = "cucumber.glue", value = "example, test.sauceDemoSelenium")
@ConfigurationParameter(key = "cucumber.filter.tags", value = "@ui or @api")
@ConfigurationParameter(key = "cucumber.plugin",
        value = "pretty, io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm")
public class RunCucumberTest {
}


