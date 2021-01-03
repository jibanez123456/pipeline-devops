/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){
  
	stage('Build-Test') {
		// sh ".gladlew clean build"
		env.ETAPA = env.STAGE_NAME
		if (env.PARAM_STAGE.contains('Build-Test') || env.PARAM_STAGE.isEmpty()) {
			sh 'gradle clean build'
		}
		else {
			// sh 'gradle clean build'
			println "no ejecutar stage Build-Test"
		}
	}
	stage('Sonar') {
		// configurado en sonarcube-configuration
		env.ETAPA = env.STAGE_NAME
		if (env.PARAM_STAGE.contains('Sonar') || env.PARAM_STAGE.isEmpty()) {
			def scannerHome = tool 'sonar-scanner';
			
			// conf generales
			withSonarQubeEnv('sonar-server') { 
				//sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
				bat "${scannerHome}\\bin\\sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
			}
		}
		else {
			println "no ejecutar stage Sonar"
		}
	}
	stage('Run') {
		// bat "gradle bootRun &"
		env.ETAPA = env.STAGE_NAME
		if (env.PARAM_STAGE.contains('Run') || env.PARAM_STAGE.isEmpty()) {
			sh 'gradle bootRun &'
			sleep 20
		}
		else {
			println "no ejecutar stage Run"
		}
	}
	stage('Test') {
		//
		env.ETAPA = env.STAGE_NAME
		if (env.PARAM_STAGE.contains('Test') || env.PARAM_STAGE.isEmpty()) {
			sh 'curl -X GET http://localhost:8081/rest/mscovid/test?msg=testing'
		}
		else {
			println "no ejecutar stage Test"
		}

	}
	stage('Nexus') {
		//
		env.ETAPA = env.STAGE_NAME
		if (env.PARAM_STAGE.contains('Nexus') || env.PARAM_STAGE.isEmpty()) {
		   nexusPublisher nexusInstanceId: 'nexus', 
		   nexusRepositoryId: 'test-repo',
		   packages: [
			   [$class: 'MavenPackage', mavenAssetList: [
				   [
					   classifier: '', 
					   extension: 'jar', 
					   filePath: 'C:\\Users\\jibanez\\.jenkins\\workspace\\emplo-gradle_feature-dir-inicial\\build\\libs\\DevOpsUsach2020-0.0.1.jar'
					]
				], 
				mavenCoordinate: [
					artifactId: 'DevOpsUsach2020', 
					groupId: 'com.devopsusach2020', 
					packaging: 'jar', 
					version: '1.0.0'
					]
				]
			]
		}
		else {
			println "no ejecutar stage Nexus"
		}	
	}
}

return this; 
