package com.sitharaj.notes.bdd

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
	features = ["src/test/resources/features"],
	glue = ["com.sitharaj.notes.bdd"],
	plugin = [
		"pretty",
		"json:build/reports/cucumber/cucumber.json",
		// Explicitly write a file with .html extension to avoid creating a raw 'html' file
		"html:build/reports/cucumber/cucumber.html"
	]
)
class RunCucumberTest
