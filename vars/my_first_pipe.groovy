#!/usr/bin/env groovy

// pipelline syntax:
// https://www.jenkins.io/doc/book/pipeline/development/
def call() {

	properties([
		parameters([
		// -----------------buildName Selections---------------;
		string(
			name: 'buildName',
			defaultValue: 'BuildName',
			description: 'Enter short build name'
		),
		// ---------------buildDescription Selections----------;
		string(
			name: 'buildDescription',
			defaultValue: 'buildDescription',
			description: 'Enter short build description'
		)
		])
	])
		
	env.EXE_AGENT = "main_agent_label"

	pipeline {
		environment {
			// Custom Build Properties
			// https://www.jenkins.io/doc/pipeline/steps/custom-build-properties/
			env.TASK_NAME = "${params['buildName']}"
		}
		options {
			buildDiscarder(logRotator(numToKeepStr:'10'))
			timestamps()
		}
		agent { label env.EXE_AGENT }
		stages {
			stage('First_Stage') {
			agent { label "${env.EXE_AGENT}"}
			steps {
				script {
				env.BUILD_TRIGGER_BY = currentBuild.getBuildCauses()[0].shortDescription
				
				if (env.BUILD_TRIGGER_BY) {
					println("BUILD_TRIGGER_BY : ${env.BUILD_TRIGGER_BY}")
				} else {
					env.BUILD_TRIGGER_BY = "Started by Cron"
				}
				
				wrap([$class: 'BuildUser']) {
					buildName "#${env.BUILD_NUMBER}: ${env.TASK_NAME}"
					buildDescription "${env.BUILD_TRIGGER_BY} : ${params.buildDescription}"
				}
				
				// base blocks:
				// https://www.jenkins.io/doc/pipeline/steps/workflow-durable-task-step/
				sh(
					script: "echo 'Hello World!'",
					label: "echo_command",
					returnStdout: true //return stdout after command done (usually used to pass result to variable)
				)
			}
		}
	}
}