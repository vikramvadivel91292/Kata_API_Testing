package com.booking.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.booking.stepdefinitions", "com.booking.pages", "com.booking.hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-report.html",
                "json:target/cucumber-report.json"
        },
        monochrome = true,
        tags = "not @knownIssue"
)
public class TestRunner extends AbstractTestNGCucumberTests {
}
