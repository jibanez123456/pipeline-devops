 
/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){

	stage('Compile') {
		// 
		env.ETAPA = env.STAGE_NAME
		if (env.PARAM_STAGE.contains('Compile') || env.PARAM_STAGE.isEmpty()) {
			sh 'mvn clean compile -e'		
		}
		else {
			println "no ejecutar stage Compile"
		}
		// sh 'mvn clean compile -e'		
	}

	stage('Test-Code') {
		//
		env.ETAPA = env.STAGE_NAME
		if (env.PARAM_STAGE.contains('Test-Code') || env.PARAM_STAGE.isEmpty()) {
			sh 'mvn clean test -e'
		}
		else {
			println "no ejecutar stage Test"
		}
		// sh 'mvn clean test -e'
	}
  
	stage('Build') {
		// 
		env.ETAPA = env.STAGE_NAME
		if (env.PARAM_STAGE.contains('Build') || env.PARAM_STAGE.isEmpty()) {
			sh 'mvn clean package -e'
		}
		else {
			println "no ejecutar stage Build"
		}
		// sh 'mvn clean package -e'
	}
	stage('Sonar') {
		env.ETAPA = env.STAGE_NAME
		if (env.PARAM_STAGE.contains('Sonar') || env.PARAM_STAGE.isEmpty()) {
		// configurado en sonarcube-configuration
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
		// 
		env.ETAPA = env.STAGE_NAME
		if (env.PARAM_STAGE.contains('Run') || env.PARAM_STAGE.isEmpty()) {
			sh 'mvn spring-boot:run &'
			sleep 20
		}
		else {
			println "no ejecutar stage Run"
		}
		// sh 'mvn spring-boot:run &'
		// sleep 20
	}
	stage('Test-App') {
		//
		env.ETAPA = env.STAGE_NAME
		if (env.PARAM_STAGE.contains('Test-App') || env.PARAM_STAGE.isEmpty()) {
			sh 'curl -X GET http://localhost:8081/rest/mscovid/test?msg=testing'
		}
		else {
			println "no ejecutar stage Test"
		}
		// sh 'curl -X GET http://localhost:8081/rest/mscovid/test?msg=testing'

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
						   filePath: 'C:\\Users\\jibanez\\.jenkins\\workspace\\ejemplo-gradle-library\\build\\DevOpsUsach2020-0.0.1.jar'
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
